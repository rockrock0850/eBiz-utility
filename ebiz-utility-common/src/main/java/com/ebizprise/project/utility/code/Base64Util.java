package com.ebizprise.project.utility.code;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gary.tsai 2019/6/12
 */
public class Base64Util implements Crypto{
    private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

    // 將原資料字串進行Base64加密
    @Override
    public String getEncrypt(String str) {
        return getEncrypt(str, null);
    }

    // 將已加密字串進行Base64解密
    @Override
    public String getDecode(String str) {
        return getDecode(str, null);
    }

    @Override
    public String getEncrypt (String str, String charCode) {
        logger.info("into getBase64Encrypt orgString:{}", str);
        
        if (StringUtils.isBlank(str)) {
            return "";
        }
        
        byte[] b;
        
        if (StringUtils.isBlank(charCode)) {
            b = str.getBytes();
        } else {
            try {
                b = str.getBytes(charCode);
            } catch (UnsupportedEncodingException e) {
                b = str.getBytes();
                e.printStackTrace();
            }
        }
        
        return new String(Base64.encode(b));
    }

    @Override
    public String getDecode (String str, String charCode) {
        logger.debug("into getBase64Decode encString:{}", str);
        
        if (StringUtils.isBlank(str)) {
            return "";
        }
        
        byte[] b;
        
        if (StringUtils.isBlank(charCode)) {
            b = str.getBytes();
        } else {
            try {
                b = str.getBytes(charCode);
            } catch (UnsupportedEncodingException e) {
                b = str.getBytes();
                e.printStackTrace();
            }
        }
        
        return new String(Base64.decode(b));
    }
}
