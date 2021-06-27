package com.ebizprise.project.utility.str;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;

/**
 * 亂碼驗證工具
 * 
 * The <code>MessyCodeVdalite</code>	
 * 
 * @author andrew.lee
 * @version 1.0, Created at 2018年11月23日
 */
public class MessyCodeValidate {
    
    private static List<String> allowLs;

    /**
     * 傳入字串檢查是否符合正則所表達的條件
     * 若為全形字,則先經過轉換,再檢核
     * 
     * @return boolean
     */
    public static boolean isMessyCode(String str) {
        boolean isMessy = false;
        str = str.replace(" ", "");//過濾空格,節省效能以及確保正確性
        String regex = "^[\\u4E00-\\u9FA5_a-zA-Z0-9_]*$";
        Pattern pattern = Pattern.compile(regex); 
        
        for(char c : str.toCharArray()) {
            String targetStr = String.valueOf(c);
            if(!isAllowString(targetStr)) {//檢查是否為過濾列表中允許的符號
                if(!pattern.matcher(targetStr).find()) {//先檢查是否為正常中英文數字
                    if(!pattern.matcher(toHalfWidth(targetStr)).find()) {//在檢查是否為全形英數字或標點符號
                        isMessy = true;
                        break;
                    }
                }
            }
        }
        
        return isMessy;
    }
    
    /**
     * 傳入內碼範圍,檢核文字,確認是否為客戶的自造字
     * 
     * @return boolean
     */
    public static boolean isUserCreateCode(String str, String unicodeBlockBegin, String unicodeBlockEnd) {
        boolean isUserCreateCode = true;
        String regex = "^[\\u{0}-\\u{1}]*$";//自造字內碼區塊,從參數檔取得的區域資訊,來決定客戶的自造字的內碼區塊範圍
        Pattern pattern = Pattern.compile(MessageFormat.format(regex, unicodeBlockBegin, unicodeBlockEnd));
        
        for(char c :str.toCharArray()) {
            String targetStr = String.valueOf(c);
            if(!pattern.matcher(targetStr).find()) {
                isUserCreateCode = false;
                break;
            }
        }
        
        return isUserCreateCode;
    }
    
    /**
     * 中文英數字轉半形
     * 
     * @param numberStr
     * @return String
     */ 
    public static String toHalfWidth(String numberStr) {
        char[] charArray = numberStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == 12288) {
                charArray[i] =' ';
            } else if (charArray[i] >= ' ' && charArray[i] <= 65374) {
                    charArray[i] = (char) (charArray[i] - 65248);
             } 
        }

        return new String(charArray);
    }
    
    /**
     * 檢核是否為標點符號(包含全形)
     * 
     * @param s
     * @return boolean
     */
    public static boolean isPunctuationMarks(String s) {
        //全形中文常用符號
        String regex = "[\\uFF00-\\uFFEF]";
        Pattern pattern = Pattern.compile(regex); 
        //半形符號
        String regex2 = "[\\u0020-\\u002F\\u0030-\\u0039\\u003A-\\u0040\\u0041-\\u005A\\u005B-\\u0060\\u0061-\\u007A\\u007B-\\u007E]";
        Pattern pattern2 = Pattern.compile(regex2); 
        
        return pattern.matcher(s).find() ||  pattern2.matcher(toHalfWidth(s)).find();
    }
    
    /**
     * 驗證字元是否在允許列表內
     * 因部分符號無法透過程式直接找到對應的半形符號,故改用此法作為額外判斷
     * 
     * @return boolean
     */
    public static boolean isAllowString(String targetStr) {
        if(CollectionUtils.isEmpty(allowLs)) {
            allowLs = new ArrayList<>();
            allowLs.add("﹐");
            allowLs.add("˙");
            allowLs.add("·");
            allowLs.add("﹑");
            allowLs.add("@");
            allowLs.add("＠");
        }
        
        if(allowLs.contains(targetStr)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 驗證字元是否在允許列表內
     * 因部分符號無法透過程式直接找到對應的半形符號,故改用此法作為額外判斷
     * @return boolean
     */
    public static boolean isAllowString(String targetStr, List<String> advance) {
        if(CollectionUtils.isEmpty(allowLs)) {
            allowLs = new ArrayList<>();
            allowLs.add("﹐");
            allowLs.add("˙");
            allowLs.add("·");
            allowLs.add("﹑");
            allowLs.add("@");
            allowLs.add("＠");
        }
        
        advance.addAll(allowLs);
        if(advance.contains(targetStr)) {
            return true;
        }
        
        return false;
    }

}
