package com.ebizprise.project.utility.str;

/**
 * @author gary.tsai 2019/6/20
 */
public class StringConstant {
	public static final String SYSTEM_ADMIN = "Admin";// 系統管理者
	public static final String SYSTEM = "System";// 系統
	public static final String BATCH = "Batch";// 排程

	public static final String ENCODE_MS950 = "MS950";
	public static final String ENCODE_BIG5 = "Big5";
	public static final String ENCODE_UTF8 = "UTF-8";

	public static final String SQLLOADER_ENCODE_UTF8 = "AL32UTF8";

	public static int PAGE_INDEX = 0;// 分頁起始位置
	public static int MAX_PAGE_COUNT = 100;// 一次分頁筆數
	public static int PAGE_ROW_COUNT = 10;// 每頁筆數
	public static int PAGING = 10;// 資料分頁數

	public static final String BATCH_STATUS_DESC_START = "Start"; // Start: Batch開始
	public static final String BATCH_STATUS_DESC_SUCCESS_END = "SuccessEnd"; // SuccessEnd: 本 Batch 正常結束
	public static final String BATCH_STATUS_DESC_FAIL_END = "FailEnd"; // FailEnd: 本 Batch 異常結束

	public static final String CHANGE_FORMAT_STATUS_FAILED = "3";
	public static final String CHANGE_FORMAT_STATUS_NOT_DONE = "0";

	public static final String COMMA = ","; // 逗號
	public static final String COLON = ":"; //冒號
	public static final String PERCENT = "%"; //百分比
	public static final String DOT = "."; // 點號
	public static final String SEMICOLON = ";"; // 分號
	public static final String DASH = "-"; // 破折號
	public static final String UNDERLINE = "_";
	public static final String AT = "@";
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String SHORT_YES = "Y";
	public static final String SHORT_NO = "N";
	public static final String YES = "YES";
	public static final String NO = "NO";
	public static final String SHORT_TRUE = "T";
	public static final String SHORT_FALSE = "F";
	public static final String TRUE = "TRUE";
	public static final String FALSE = "FALSE";
	public static final String EQUALS = "=";
	public static final int DEFAULT_THREAD_SIZE = 5;

	public static final String LINE_SEPERATOR2 = "\r\n";
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");
	public static final String LINE_SEPERATOR3 = "<br/>";

	public static final String SLASH = "/";
	public static final String TILDE = "~";
	public static final String EXCEL_SEXTENSION = "xlsx";//excel 副檔名

	public static final String SET_CONTENT_TYPE = "APPLICATION/OCTET-STREAM";
	public static final String SET_HEADER_COOKIE = "Set-Cookie";
	public static final String SET_HEADER_FILEDOWNLOAD_PATH = "fileDownload=true; path=/";
	public static final String SET_HEADER_CONTENT_DISPOSITION = "Content-disposition";
	public static final String SET_HEADER_ATTACHMENT_FILENAME = "attachment; filename=";

	public static final String PARAM_TITLE = "_param=title_";    //excel style 參數
	public static final String PARAM_TITLEM = "_param=titlem_";    //excel style 參數
	public static final String PARAM_CONTENT = "_param=content_";//excel style 參數
	public static final String PARAM_CONTENTM = "_param=contentm_";//excel style 參數
	public static final String PARAM_HEADER = "_param=header_";  //excel style 參數
	public static final String PARAM_HEADERM = "_param=headerm_";  //excel style 參數
	public static final String PARAM_BODY = "_param=body_";      //excel style 參數
	public static final String PARAM_BODYM = "_param=bodym_";      //excel style 參數
	public static final String PARAM_FOOTER = "_param=footer_";  //excel style 參數
	public static final String PARAM_FOOTERM = "_param=footerm_";  //excel style 參數
	public static final String PARAM_TOTAL = "_param=total_";    //excel style 參數
	public static final String PARAM_TOTALM = "_param=totalm_";  //excel style 參數
	public static final String PARAM_BLANK = "_param=blank_";    //excel style 參數
	public static final String PARAM_SKIP_A_LINE = "_param=skipALine_";
	public static final String PARAM_MERGE_COLUMNS="_param=mergeColumns_";
	public static final String PAPAM_1 ="=1"; //合併欄位數1
	public static final String PAPAM_2 ="=2"; //合併欄位數2
	public static final String PAPAM_3 ="=3"; //合併欄位數3
	public static final String PAPAM_4 ="=4"; //合併欄位數4
	public static final String PAPAM_5 ="=5"; //合併欄位數5
	public static final String PAPAM_6 ="=6"; //合併欄位數6
	public static final String PAPAM_7 ="=7"; //合併欄位數7
	public static final String PAPAM_8 ="=8"; //合併欄位數8
	public static final String PAPAM_9 ="=9"; //合併欄位數9
	public static final String PAPAM_10 ="=10"; //合併欄位數10
	public static final String PAPAM_11 ="=11"; //合併欄位數10
	public static final String PAPAM_12 ="=12"; //合併欄位數12
	public static final String PAPAM_13 ="=13"; //合併欄位數13
	public static final String PAPAM_14 ="=14"; //合併欄位數14
	public static final String PAPAM_15 ="=15"; //合併欄位數15
	public static final String PAPAM_16 ="=16"; //合併欄位數16
	public static final String PAPAM_17 ="=17"; //合併欄位數17
	public static final String PAPAM_18 ="=18"; //合併欄位數18
	public static final String PAPAM_19 ="=19"; //合併欄位數18
	public static final String PAPAM_20 ="=20"; //合併欄位數20
	public static final String UNDERLINE_PARAM = "_param";
}
