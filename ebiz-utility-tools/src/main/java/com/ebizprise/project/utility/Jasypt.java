package com.ebizprise.project.utility;

import org.jasypt.util.text.BasicTextEncryptor;

/** * 
 * 改properties檔桌面應用程式
 * @author jacky.fu
 * @version 1.0, Created at 2020年4月10日
 */
public class Jasypt {

    private static final String KEY_STR = "Copyright(C)eBizprise";

    /**
     * 用KEY_STR進行輸入字串的加密
     * 
     * @param text
     * @return
     */
    public static String enctyptStr(String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(KEY_STR.toCharArray());
        String encryptedText = textEncryptor.encrypt(text);

        return encryptedText;
    }

    /**
     * 用KEY_STR進行輸入字串的解密
     * 
     * @param text
     * @return
     */
    public static String decryptStr(String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(KEY_STR.toCharArray());
        String decryptedText = textEncryptor.decrypt(text);

        return decryptedText;
    }

}
