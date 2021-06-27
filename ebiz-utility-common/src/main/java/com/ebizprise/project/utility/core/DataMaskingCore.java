package com.ebizprise.project.utility.core;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 資料遮罩核心 - 輕量化版
 * 
 * 請注意,使用前請搭配使用指定的Excel
 * 
 * 欄位名稱請勿異動,否則會造成檔案找不到的問題
 * 
 * The <code>DataMaskingCore</code>	
 * 
 * @author andrew.lee
 * @version 1.0, Created at 2020年11月6日
 */
public class DataMaskingCore  {
    
    private static final String LANDING_TABLE_LIST_SHEET_NAME = "Landing Table List";
    private static final String MASK_ID_LIST = "Mask ID List";
    
    private Map<String,List<DataMaskingLandingTableVO>> maskingLandingTableMp;
    private Map<String,DataMaskingMaskMappVO> maskingIdMp;
    
    private Map<String,Object> maskingCompleteSQL;
    private Map<String,Object> maskingDataMappingOriginDataSQL;
    
    //資料遮罩Excel,相關欄位名稱
    private static final String MASKING_LANDING_TABLE_NAME = "LANDING_TABLE_NAME";
    private static final String MASKING_COLUMN_NAME = "COLUMN_NAME";
    private static final String MASKING_COLUMN_DESC = "COLUMN_DESC";
    private static final String MASKING_TARGET_COLUMN = "TARGET_COLUMN";
    private static final String MASKING_TYPE = "TYPE";
    private static final String MASKING_LENGTH = "LENGTH";
    private static final String MASKING_MASKING_ID = "MASK_ID";
    private static final String MASKING_SQL_CONDITION = "SQL";
    private static final String MASKING_SQL_COLUMN_NAME = "SQL";
    private static final String MASKING_SQL_FROM_CONDITION = "FROM_CONDITION";
    private static final String MASK_ID = "Mask ID";
    private static final String FROM = "FROM";
    private static final String END = "END";
    private static final String REPLACE_MARK = "REPLACE MARK";
    private static final String MASK_CODE = "JAVA";
    //SQL欄位替換佔位符
    private static final String REPLACE_COLUMN_NAME = "{COLUMN_NAME}";
    private static final String REPLACE_AS_COLUMN_NAME = "{AS_COLUMN_NAME}";
    private static final String REPLACE_FROM = "{FROM}";
    private static final String REPLACE_END = "{END}";
    private static final String REPLACE_REPLACE_MARK = "{REPLACE_MARK}";
    private static final String REPLACE_LENGTH = "{LENGTH}";
    
    private static final int HASH_DATA_MASKING_ID = 1;//需要經過HASH加密的MASKING ID
    
    public DataMaskingCore(String filePath) {
        init(filePath, new ArrayList<Integer>());
    }
    
    public DataMaskingCore(String filePath, List<Integer> sheetIgnore) {
        init(filePath, sheetIgnore);
    }
    
    /**
     * 開始執行資料遮罩
     * 
     */
    public void beginMasking(String fileLandingPath) throws Exception {
        //STEP.1 取得EXCEL資料,過濾出需針對進行主鍵遮罩的資料
        System.out.println("STEP.1 取得Excel資料");
        List<DataMaskingLandingTableVO> maskingMappColumnLs = new ArrayList<>();

        Map<String,List<DataMaskingLandingTableVO>> tableColumnMp = getMaskingLandingTableMp();
        
        for(String key : tableColumnMp.keySet()) {
            List<DataMaskingLandingTableVO> currentTableLs = tableColumnMp.get(key);
            maskingMappColumnLs.addAll(currentTableLs.parallelStream().filter(target -> target.getMaskingId() == HASH_DATA_MASKING_ID).collect(Collectors.toList()));
        }
        
        //STEP.2 組合所有SQL
        maskingCompleteSQL = combineFullSQL();
        System.out.println("STEP.2 開始執行資料新增,本次一共有 " + maskingCompleteSQL.size() + "個資料表");
        if(StringUtils.isNotBlank(fileLandingPath)) {
            createMaskingSQLFile(maskingCompleteSQL, fileLandingPath);
        }
        
        //STEP.3 將STEP.1產生的物件中,取出SQL,並且執行,將資料匯入資料遮罩的DB中
        maskingDataMappingOriginDataSQL = createMaskingMappSQL(maskingMappColumnLs);
        System.out.println("STEP.3 產生資料遮罩對應表內容,本次一共有 " + maskingDataMappingOriginDataSQL.size() + "個資料表欄位");
        if(StringUtils.isNotBlank(fileLandingPath)) {
            createMappingSQLFile(maskingDataMappingOriginDataSQL, fileLandingPath);
        }
    }
    
    /**
     * 開始執行資料遮罩,不產生SQL實體檔案
     * 
     */
    public void beginMasking() throws Exception {
        beginMasking("");
    }

    /**
     * 組合完整SQL檔
     * 
     * @return Map
     */
    public Map<String, Object> combineFullSQL() {
        Map<String, Object> fullSQLMp = new HashMap<>();
        Map<String, List<DataMaskingLandingTableVO>> landingTable = getMaskingLandingTableMp();
        Map<String,DataMaskingMaskMappVO> maskingMappingId = getMaskingIdMp();
        
        for(String landingTableName :landingTable.keySet()) {
            List<DataMaskingLandingTableVO> sourceLs = landingTable.get(landingTableName);
            
            String fullSQL = sourceLs.get(0).getFullSQL();
            String whereCondition = sourceLs.get(0).getFromCondition();
            if(StringUtils.isBlank(fullSQL)) {
                continue;
            }
            List<String> columns = new ArrayList<>();
            List<String> maskingColumns = new ArrayList<>();
            for(DataMaskingLandingTableVO source : sourceLs) {
                String columnName = source.getColumnName();
                String targetColumn = source.getTargetColumn();
                
                if(!"SQL".equals(columnName)) {
                    int maskId = source.getMaskingId();
                    columns.add(columnName);
                    if(maskId > 0) {
                        maskingColumns.add(combineSQLMasking(source, maskingMappingId));
                    } else {
                        maskingColumns.add(targetColumn);
                    }
                }
            }
            fullSQL = MessageFormat.format(fullSQL, String.join(", \r\n", columns), String.join(", \r\n", maskingColumns), String.join(", \r\n", whereCondition));
            fullSQLMp.put(landingTableName, fullSQL);
        }
        return fullSQLMp;
    }
    
    /**
     * 將需製作遮罩對照表的欄位,產生出可執行SQL
     * 
     * @param columnMaskingCode
     * @param maskingMappColumnLs
     * @return List
     */
    public Map<String,Object> createMaskingMappSQL(List<DataMaskingLandingTableVO> maskingMappColumnLs) {
        Map<String,Object> rtnMp = new HashMap<>();
        String columnMaskingCode = getMaskingIdMp().get(String.valueOf(HASH_DATA_MASKING_ID)).getMaskCode();
        String insertSQL = DataMaskingSQLEnum.SQL_INSERT_MASKING_MAPP_DATA.getSqlText();
        
        for(DataMaskingLandingTableVO source : maskingMappColumnLs) {
            String originColumn = source.getColumnName();
            String targetColumn = source.getTargetColumn();
            String tableStr = "'" + source.getLandingTable() + "'" ;
            String originColumnStr = "'" + source.getColumnName() + "'" ;
            String asColumnName = targetColumn.contains(".")? targetColumn.split("\\.")[1] : targetColumn;
            String maskingSQL = columnMaskingCode.replace(REPLACE_COLUMN_NAME, targetColumn).replace(REPLACE_AS_COLUMN_NAME, asColumnName);
            //透過MaskingLandingTableMp取得對應的TABLE資訊,然後取第一筆資料,第一筆資料有From後面的SQL組成條件的字串,可用於本次查詢
            String fromCondition = getMaskingLandingTableMp().get(source.getLandingTable()).get(0).getFromCondition();
            String convertSQL = MessageFormat.format(insertSQL,tableStr, originColumnStr, targetColumn, maskingSQL, fromCondition);
            convertSQL = convertSQL.replace(REPLACE_LENGTH, String.valueOf(source.getLength()));

            rtnMp.put(source.getLandingTable() + "." + originColumn, convertSQL);
        }
        
        return rtnMp;
    }

    //==================PRIVATE METHOD=============================
    /**
     * 初始化物件
     * 
     * @param filePath
     * @param ignoreSheet 若有Sheet要被忽略的話,則在List中加入Sheet的順序,Sheet的順序從0開始
     * @return boolean
     */
    private boolean init(String filePath, List<Integer> ignoreSheet) {
        try {
            maskingLandingTableMp = new HashMap<>();
            maskingIdMp = new HashMap<>();
            
            File excel = new File(filePath);

            LinkedHashMap<String,List<Map<String, Object>>> excelMp = fromExcel(excel, 0, ignoreSheet);
            //Landing Table對照表
            List<Map<String, Object>> landingTableMappingLs = excelMp.get(LANDING_TABLE_LIST_SHEET_NAME);
            convertLandingTableList(landingTableMappingLs);

            //Masking方法對照表
            List<Map<String, Object>> maskingIdMappingLs = excelMp.get(MASK_ID_LIST);
            convertMaskingIdList(maskingIdMappingLs);
            
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 將Excel讀取到的欄位資料,整理成可用的LandingTableVO
     * 
     * @param landingTableMappingLs
     */
    private void convertLandingTableList(List<Map<String, Object>> landingTableMappingLs) {
        Map<String,List<DataMaskingLandingTableVO>> result = new HashMap<>();

        for(Map<String,Object> source : landingTableMappingLs) {
            DataMaskingLandingTableVO target = new DataMaskingLandingTableVO();
            String landingTableName = MapUtils.getString(source, MASKING_LANDING_TABLE_NAME);
            String columnName = MapUtils.getString(source, MASKING_COLUMN_NAME);
            String columnDesc = MapUtils.getString(source, MASKING_COLUMN_DESC);
            String type = MapUtils.getString(source, MASKING_TYPE);
            String maskingId = MapUtils.getString(source, MASKING_MASKING_ID);
            String sqlCondition = MapUtils.getString(source, MASKING_SQL_CONDITION);
            String targetColumn = MapUtils.getString(source, MASKING_TARGET_COLUMN);
            String fromCondition = MapUtils.getString(source, MASKING_SQL_FROM_CONDITION);
            String length = MapUtils.getString(source, MASKING_LENGTH);
            
            target.setLandingTable(landingTableName);
            target.setColumnName(columnName);
            target.setColumnDesc(columnDesc);
            target.setType(type);
            target.setTargetColumn(targetColumn);
            target.setFromCondition(fromCondition);
            target.setLength(length);
            
            if(StringUtils.isNotBlank(maskingId)) {
                target.setMaskingId(Integer.parseInt(maskingId));
            }

            if(MASKING_SQL_COLUMN_NAME.equals(columnName)) {
                target.setFullSQL(sqlCondition);
            }
            
            if(result.containsKey(landingTableName)) {
                result.get(landingTableName).add(target);
            } else {
                List<DataMaskingLandingTableVO> dataLs = new ArrayList<>();
                dataLs.add(target);
                result.put(landingTableName, dataLs);
            }
        }
        
        maskingLandingTableMp.putAll(result);
    }
    
    /**
     * 將Excel讀取到的欄位資料,整理成可用的
     * 
     * @param maskingIdMappingLs
     */
    private void convertMaskingIdList(List<Map<String, Object>> maskingIdMappingLs) {
        Map<String,DataMaskingMaskMappVO> maskingMp = new HashMap<>();
        for(Map<String, Object> source : maskingIdMappingLs) {
            DataMaskingMaskMappVO target = new DataMaskingMaskMappVO();
            String maskId = MapUtils.getString(source, MASK_ID);
            String from = MapUtils.getString(source, FROM);
            String end = MapUtils.getString(source, END);
            String replaceMark = MapUtils.getString(source, REPLACE_MARK);
            String maskCode = MapUtils.getString(source, MASK_CODE);
            
            if(StringUtils.isNotBlank(maskId)) {
                target.setMaskId(Integer.parseInt(maskId));
                target.setFrom(from);
                target.setEnd(end);
                target.setReplaceMark(replaceMark);
                target.setMaskCode(maskCode);
                
                maskingMp.put(String.valueOf(maskId), target);
            }
        }
        maskingIdMp.putAll(maskingMp);
    }
    
    /**
     * 組合SQL遮罩查詢條件
     * 
     * 
     * @return
     */
    private String combineSQLMasking(DataMaskingLandingTableVO vo, Map<String,DataMaskingMaskMappVO> maskingMappingId) {
        String result = "";
        String columnName = vo.getTargetColumn();
        String length = vo.getLength();
        String maskId = Objects.isNull(vo.getMaskingId())? "" : String.valueOf(vo.getMaskingId());
        String asColumnName = columnName.contains(".")? columnName.split("\\.")[1] : columnName;
        
        DataMaskingMaskMappVO maskingRole = maskingMappingId.get(maskId);
        String maskCodeSQL = maskingRole.getMaskCode();
        String from = maskingRole.getFrom();
        String end = maskingRole.getEnd();
        String replaceMark = maskingRole.getReplaceMark();

        result = maskCodeSQL.replace(REPLACE_COLUMN_NAME, columnName)
                .replace(REPLACE_AS_COLUMN_NAME, asColumnName)
                .replace(REPLACE_FROM, from)
                .replace(REPLACE_END, end)
                .replace(REPLACE_REPLACE_MARK, replaceMark)
                .replace(REPLACE_LENGTH, length);
        
        return result;
    }
    
    
    /**
     * 輸入行號.從該行開始解析Excel.第一個被讀取的所有資料將成為Column Name,來作為Map的Key
     * 
     * @author AndrewLee
     * 
     * @param rowBeginNum 若是Excel的第一行資料.則為0.以此類推
     * @param excel Excel檔案
     * @param ignoreSheet 忽略第幾個Sheet(若沒有則直接傳空List)
     * @return List<Map<String, Object>>
     * @throws IOException 
     */
    @SuppressWarnings("deprecation")
    private LinkedHashMap<String,List<Map<String, Object>>> fromExcel(File excel,int rowBeginNum, List<Integer> ignoreSheet) throws IOException {
        if (excel != null) {
            LinkedHashMap<String,List<Map<String, Object>>> dataMp = new LinkedHashMap<String,List<Map<String, Object>>>();
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(excel);
                
                //從Sheet開始解析
                for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                    if(CollectionUtils.isNotEmpty(ignoreSheet) && ignoreSheet.contains(sheetNum)) {
                        continue;
                    }
                    Sheet sheet = workbook.getSheetAt(sheetNum);
                    List<String> columnList = new ArrayList<String>();
                    List<Map<String, Object>> dataList = new ArrayList<>();
                    
                    // Row Loop 行數Loop
                    for (int rowNum = rowBeginNum; rowNum <= sheet.getLastRowNum(); rowNum++) {
                        Row row = sheet.getRow(rowNum);
                        Map<String, Object> rowMap = new HashMap<String, Object>();
                        
                        if (row != null) {
                            // Cell Loop 格子
                            for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
                                Cell cell = row.getCell(cellNum);
                                
                                if (rowNum == rowBeginNum) {
                                    // 第一列為欄位名稱,以欄位名稱作為key
                                    columnList.add(String.valueOf(cell));
                                } else {
                                    //cell != null的判斷很重要,不然該欄位為空的時候,塞值會導致NullPointerException
                                    if(cell != null) {
                                        cell.setCellType(CellType.STRING);
                                        rowMap.put(columnList.get(cellNum), cell);
                                    }
                                }
                            }
                        }
                        
                        if(rowNum != 0) {
                            dataList.add(rowMap);
                        }
                        
                        dataMp.put(sheet.getSheetName(), dataList);
                    }
                }
                
                return dataMp;
            } catch (IOException e) {
                throw e;
            } finally {
                if(workbook != null) workbook.close();
            }
        }
        return null;
    }
    
    /**
     * 建立資料遮罩前後對應表的Insert語法
     * 
     * @param maskingMappSQLMp
     * @throws IOException
     */
    private static void createMappingSQLFile(Map<String,Object> maskingMappSQLMp, String landingPath) throws IOException {
        for(String targetColumn : maskingMappSQLMp.keySet()) {
            FileUtils.writeStringToFile(new File(landingPath + File.separator + "mapping" + File.separator + targetColumn + ".sql"), MapUtils.getString(maskingMappSQLMp, targetColumn), "UTF-8");
        }
    }
    
    /**
     * 組合資料遮罩完整SQL
     * 
     * @param maskingSqlMp
     * @throws IOException
     */
    private static void createMaskingSQLFile(Map<String,Object> maskingSqlMp, String landingPath) throws IOException {
        for(String landingTableName : maskingSqlMp.keySet()) {
            FileUtils.writeStringToFile(new File(landingPath + File.separator + "masking" + File.separator + landingTableName + ".sql"), MapUtils.getString(maskingSqlMp, landingTableName), "UTF-8");
        }
    }
    
    //==================================================

    public Map<String,DataMaskingMaskMappVO> getMaskingIdMp() {
        return maskingIdMp;
    }
    
    public Map<String,List<DataMaskingLandingTableVO>> getMaskingLandingTableMp() {
        return maskingLandingTableMp;
    }

    public Map<String, Object> getMaskingCompleteSQL() {
        return maskingCompleteSQL;
    }

    public Map<String, Object> getMaskingDataMappingOriginDataSQL() {
        return maskingDataMappingOriginDataSQL;
    }

    //INNER CLASS DataMaskingSQLEnum BEGIN
    /**
     * 
     * The <code>DataMaskingSQLEnum</code>  
     * 
     * @author andrew.lee
     * @version 1.0, Created at 2020年7月31日
     */
    enum DataMaskingSQLEnum {
        
        SQL_INSERT_MASKING_MAPP_DATA(getInsertDataMaskingMappSQL(),"新增遮罩對應表資料");

        public String sqlText;
        public String desc;

        DataMaskingSQLEnum(String sqlText, String desc) {
            this.sqlText = sqlText;
            this.desc = desc;
        }

        public String getSqlText() {
            return sqlText;
        }

        public void setSqlText(String sqlText) {
            this.sqlText = sqlText;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
        
        //==================================SQL Templates===========================
        /**
         * 取得資料遮罩對應表
         * 
         * @return
         */
        private static String getInsertDataMaskingMappSQL() {
            String result = "INSERT INTO MASK_REFERRENCE_LIST ("
                    + "TABLE_NAME,"
                    + "COLUMN_NAME,"
                    + "ORI_DATA,"
                    + "MASK_DATA "
                    + ")"
                    + "SELECT "
                    + "{0}, "  //TABLE名稱
                    + "{1}, "  //欄位名稱
                    + "{2},"  //原始資料欄位
                    + "{3} "  //遮蔽資料SQL
                    + "FROM "
                    + "{4}"; //FROM之後的所有字串,包含Join,Where以及OrderBy
       
            
            return result;
        }
    }
    //INNER CLASS DataMaskingSQLEnum END
    
    //INNER CLASS DataMaskingLandingTableVO BEGIN
    /**
     * 資料遮罩VO
     * 
     * The <code>DataMaskingVO</code>   
     * 
     * @author andrew.lee
     * @version 1.0, Created at 2020年7月22日
     */
    class DataMaskingLandingTableVO {

        private String landingTable;//遮罩資料表名稱
        private String columnName;//欄位名稱
        private String targetColumn;//欄位名稱(有加上Table別名)
        private String length;//欄位長度
        private String columnDesc;//遮罩欄位說明
        private String type;//欄位形態
        private int maskingId;//遮罩方式ID
        private String fullSQL;//完整SQL(只針對columnName為SQL的資料,這邊才會有值)
        private String fromCondition;//SQL From之後的語法,包含Join以,Where以及Order By
        
        public String getLandingTable() {
            return landingTable;
        }
        public void setLandingTable(String landingTable) {
            this.landingTable = landingTable;
        }
        public String getColumnName() {
            return columnName;
        }
        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
        public String getLength() {
            return length;
        }
        public void setLength(String length) {
            this.length = length;
        }
        public String getColumnDesc() {
            return columnDesc;
        }
        public void setColumnDesc(String columnDesc) {
            this.columnDesc = columnDesc;
        }
        public int getMaskingId() {
            return maskingId;
        }
        public void setMaskingId(int maskingId) {
            this.maskingId = maskingId;
        }
        public String getFullSQL() {
            return fullSQL;
        }
        public void setFullSQL(String fullSQL) {
            this.fullSQL = fullSQL;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getTargetColumn() {
            return targetColumn;
        }
        public void setTargetColumn(String targetColumn) {
            this.targetColumn = targetColumn;
        }
        public String getFromCondition() {
            return fromCondition;
        }
        public void setFromCondition(String fromCondition) {
            this.fromCondition = fromCondition;
        }
            
    }
    //INNER CLASS DataMaskingLandingTableVO END
    
    //INNER CLASS DataMaskingMaskMappVO BEGIN
    /**
     * 資料遮罩工具 遮罩方式對應表VO
     * 
     * The <code>DataMaskingMaskMappVO</code>   
     * 
     * @author andrew.lee
     * @version 1.0, Created at 2020年7月22日
     */
    class DataMaskingMaskMappVO {

        private int maskId;//遮罩方式ID
        private String from;//遮罩字串起始位置
        private String end;//遮罩字串結束位置
        private String replaceMark;//替換字元樣式
        private String maskCode;//遮罩方法程式碼
        
        public int getMaskId() {
            return maskId;
        }
        public void setMaskId(int maskId) {
            this.maskId = maskId;
        }
        public String getFrom() {
            return from;
        }
        public void setFrom(String from) {
            this.from = from;
        }
        public String getEnd() {
            return end;
        }
        public void setEnd(String end) {
            this.end = end;
        }
        public String getReplaceMark() {
            return replaceMark;
        }
        public void setReplaceMark(String replaceMark) {
            this.replaceMark = replaceMark;
        }
        public String getMaskCode() {
            return maskCode;
        }
        public void setMaskCode(String maskCode) {
            this.maskCode = maskCode;
        }
        
    }
  //INNER CLASS DataMaskingMaskMappVO END
    
}
