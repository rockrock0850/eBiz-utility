package com.ebizprise.project.utility.code;

/**
 * @author gary.tsai 2019/6/12
 */
public interface Crypto {

	String getEncrypt(String str);
	
    String getEncrypt(String str, String charCode);

	String getDecode(String str);

    String getDecode(String str, String charCode);
	
}
