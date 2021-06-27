package com.ebizprise.project.utility.code;

/**
 * @author gary.tsai 2019/6/12
 */
public class CryptoUtil {

	private Crypto crypto;

	public CryptoUtil(Crypto crypto) {
		this.crypto = crypto;
	}

	public String encrypt(String str) {
		return encrypt(str, null);
	}

    public String encrypt(String str, String charCode) {
        return crypto.getEncrypt(str, charCode);
    }

	public String decode(String str) {
		return decode(str, null);
	}

    public String decode(String str, String charCode) {
        return crypto.getDecode(str, charCode);
    }
    
}
