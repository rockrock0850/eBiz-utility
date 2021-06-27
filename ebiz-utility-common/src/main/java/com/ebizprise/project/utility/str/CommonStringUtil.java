package com.ebizprise.project.utility.str;

import java.lang.Character.UnicodeBlock;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.RegExUtils;

public class CommonStringUtil {

    public static final String BLANK = "";
    public static final long MEGABYTE = 1024L * 1024L;
    public static final byte BYTE_BLANK = " ".getBytes()[0];
    public static final String NUMBER_FORMAT0 = "#";
    public static final String NUMBER_FORMAT1 = "###,###";
    public static final String NUMBER_FORMAT2 = "###,###.00";
    public static final String NUMBER_FORMAT3 = "###,##0.00";
    public static final String NUMBER_FORMAT4 = "###,##0.000";
    
    /**
     * UTF-8轉成UNICODE
     * @param str
     * @return
     */
    public static String utf8ToUnicode (String str) {
        int j;
        short s;
        UnicodeBlock unicodeBlock;
        String hexStr, unicode;
        char[] myBuffer = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < str.length(); i++) {
            unicodeBlock = UnicodeBlock.of(myBuffer[i]);
            
            if (unicodeBlock == UnicodeBlock.BASIC_LATIN) {// 英文及數字等
                sb.append(myBuffer[i]);
            } else if (unicodeBlock == 
                    UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {// 全角半角字符
                j = myBuffer[i] - 65248;
                sb.append((char) j);
            } else {// 漢字
                s = (short) myBuffer[i];
                hexStr = Integer.toHexString(s);
                
                // 如果s是負數會導致hexStr變成八碼(前四碼為f), 導致unicode轉回中文失敗。
                // 因此從後面往前截四碼確保hexStr能正確轉成四碼的unicode。
                hexStr = hexStr.substring(hexStr.length() - 4);
                
                unicode = "\\u" + hexStr;
                sb.append(unicode.toLowerCase());
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 把字串內含有UNICODE的字轉成UTF-8中文
     * @param unicodeStr
     * @return
     */
    public static String unicodeToUtf8 (String unicodeStr) {
        char c;
        int value = 0;
        int len = unicodeStr.length();
        StringBuffer outBuffer = new StringBuffer(len);
        
        for (int x = 0; x < len;) {
            c = unicodeStr.charAt(x++);
            if (c == '\\') {
                c = unicodeStr.charAt(x++);
                if (c == 'u') {
                    value = 0;
                    for (int i = 0; i < 4; i++) {
                        c = unicodeStr.charAt(x++);
                        switch (c) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + c - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + c - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + c - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (c == 't')
                        c = '\t';
                    else if (c == 'r')
                        c = '\r';
                    else if (c == 'n')
                        c = '\n';
                    else if (c == 'f')
                        c = '\f';
                    outBuffer.append(c);
                }
            } else {
                outBuffer.append(c);
            }
        }
        
        return outBuffer.toString();
    }
	
	/**
	 * 去除數字以外的文字
	 * 
	 * @param str 含有數字以外的文字
	 * @return
	 * @author adam.yeh
	 */
	public static String exceptString (String str) {
		return RegExUtils.removeAll(str, "([a-zA-Z-/*-+!@#$%^&*_=?])");
	}

    /**
     * 數字轉指定格式
     * 
     * @param number
     * @param format
     * @return
     * @author adam.yeh
     */
    public static String numberFormat (double number, String format){
        return numberFormat(String.valueOf(number), format);
    }

    /**
     * 數字字串轉指定格式
     * 
     * @param format
     * @return
     * @author adam.yeh
     */
    public static String numberFormat (String str, String format){
        DecimalFormat decFormat = new DecimalFormat(format);
        
        if( StringUtils.isBlank(str)) {
            str = "0";
        }
        
        return decFormat.format(new BigDecimal(str));
    }
	
	public static boolean isNullOrWhiteSpace(String str){
		if( null == str){
			return true;
		}
		if( str.trim().length()<= 0 ){
			return true;
		}
		return false;
	}

	/**
	 * num = 傳入的值，len = 總長度
	 * @param num
	 * @param len
	 * @return
	 */
	public static String addZero(Integer num, int len) {
		String newStr = "";
		String str = String.valueOf(num.intValue());
		int strLength = str.length();
		int addZeroLen = len - strLength;
		StringBuffer addZeroStr=new StringBuffer("");
		if( addZeroLen > 0 ){
			for (int i=0;i<addZeroLen;i++) {
				addZeroStr.append("0");
			}
			if (addZeroStr.equals("")) {
				newStr = str;
			} else {
				newStr = addZeroStr.toString()+str;
			}
		} else {
			newStr =str;
		}
		return newStr;
	}

	/**
	 * num = 傳入的值，len = 總長度
	 * @param num
	 * @param len
	 * @return
	 */
	public static String addZero(int num, int len) {
		String newStr = "";
		String str = String.valueOf(num);
		int strLength = str.length();
		int addZeroLen = len - strLength;
		StringBuffer addZeroStr=new StringBuffer("");
		if( addZeroLen > 0 ){
			for (int i=0;i<addZeroLen;i++) {
				addZeroStr.append("0");
			}
			if (addZeroStr.equals("")) {
				newStr = str;
			} else {
				newStr = addZeroStr.toString()+str;
			}
		} else {
			newStr =str;
		}
		return newStr;
	}


	/**
	 * 取得字串長度,中文兩碼,英文一碼
	 * @param str
	 * @return
	 */
	public static int getStringLength(String str) {
		return str.getBytes().length;
	}


	/**
	 * 檢查是否為數字
	 * @param sText
	 * @return 是數字：true，不是數字：false
	 */
	public static boolean isNumber(String sText) {
		   String ValidChars = "0123456789.";
		   boolean IsNumber=true;
		   char sChar;

		   for (int i = 0; i < sText.length() && IsNumber == true; i++) {
			   sChar = sText.charAt(i);
		      if (ValidChars.indexOf(sChar) == -1) {
		         IsNumber = false;
		      }
		   }
		   return IsNumber;
	}

	/**
	 * 檢查是否為整數
	 * @param sText
	 * @return 是整數：true，不是整數：false
	 */
	public static boolean isInteger(String sText) {
		   String ValidChars = "0123456789";
		   boolean IsInteger=true;
		   char sChar;

		   for (int i = 0; i < sText.length() && IsInteger == true; i++) {
			   sChar = sText.charAt(i);
		      if (ValidChars.indexOf(sChar) == -1) {
		    	  IsInteger = false;
		      }
		   }
		   return IsInteger;
	}

	/**
	 * 字串右補空白
	 * @param body
	 * @param length
	 * @return
	 */
	public static String stringRightPad(String body, int length) {
		if(StringUtils.isNotBlank(body)) {
			byte[] blankByte =((length - body.getBytes().length)>0 ? new byte[(length-body.getBytes().length)]:new byte[0]);
			Arrays.fill(blankByte, BYTE_BLANK);
			return new String(ArrayUtils.subarray(body.getBytes(), 0, length)) + new String(blankByte);
		}
		return StringUtils.rightPad(BLANK, length, BLANK);
	}

	/**
	 * 字串左補空白
	 * @param body
	 * @param length
	 * @return
	 */
	public static String stringLeftPad(String body, int length) {
		if(StringUtils.isNotBlank(body)) {
			String bodyString = String.valueOf(body);
			return StringUtils.leftPad(bodyString.substring(0,
					(bodyString.length()<=length? bodyString.length():length)), length, BLANK);
		}
		return StringUtils.leftPad(BLANK, length, BLANK);
	}

	/**
	 * 字串左補0
	 * @param body
	 * @param length
	 * @return
	 */
	public static String stringLeftZero(String body, int length) {
		if(StringUtils.isNotBlank(body)) {
			String bodyString = String.valueOf(body);
			return StringUtils.leftPad(bodyString.substring(0,
					(bodyString.length()<=length? bodyString.length():length)), length, "0");
		}
		return StringUtils.leftPad("0", length, "0");
	}

	/**
	 * 從字串中取出部分字串,會考慮到中文字的處理
	 * @param source  原始字串
	 * @param start   開始位置(從0起算)
	 * @param end     結束位置
	 * @return 部分字串
	 */
	public static String getSubstring(String source , int start , int end ){
		String resultStr ="" ;
		if(source==null || start<0 || start>=end || end<0){
			return resultStr ;
		}
		try{
			byte[]  byteArray = source.getBytes() ;
			if(end>byteArray.length) return resultStr ;

			if(byteArray.length== source.length()){  //不含中文字
				resultStr =  StringUtils.substring(source, start , end)  ;
			}else{    //含有中文字
				resultStr = new String( ArrayUtils.subarray(byteArray, start, end) );
			}
		}catch(Exception e){
			//System.out.println("getSubstring() Exception=\r\n" + e.toString());
		}
		return resultStr ;
	}

	/**
	 * 從字串中取出部分字串(從開始到最後),會考慮到中文字的處理
	 * @param source  原始字串
	 * @param start   開始位置(從0起算)
	 * @return 部分字串
	 */
	public static String getSubstring(String source , int start ){
		String resultStr ="" ;
		if(source==null || start<0 ) return resultStr ;
		try{
			byte[]  byteArray = source.getBytes() ;
			if(byteArray.length== source.length()){  //不含中文字
				resultStr =  StringUtils.substring(source, start )  ;
			}else{    //含有中文字
				resultStr = new String( ArrayUtils.subarray(byteArray, start, byteArray.length-1) );
			}
		}catch(Exception e){
		}
		return resultStr ;
	}

	/**將字串長度不足的部分補空白
	 * @param sourceArgu          原始字串
	 * @param requestLengthArgu   要求長度
	 * @return  補足長度的字串
	 */
	public static  String fillWithSpace(String sourceArgu , int requestLengthArgu ){
		String source = sourceArgu;
		int requestLength = requestLengthArgu;
		if(source==null) source="" ;
		if(requestLength<0)     requestLength=0 ;
		int sourceLength = source.getBytes().length; //原始字串長度(用byte方式計算)
		if(sourceLength == requestLength){           //長度相同
			return source ;
		}

		String result = source ;
		String space  = "                                        " ; //40個空白
		if(sourceLength < requestLength){           //長度太短
			int moreLength= requestLength - sourceLength ;  //不足的長度
			while(moreLength>space.length()){
				space += space ;
			}
			result = source  + space.substring(0, moreLength) ;
		}
		if(sourceLength > requestLength){          //長度太長
			byte[]  byteArray = source.getBytes() ;
			result = new String( ArrayUtils.subarray(byteArray, 0, requestLength ) );
		}

		return result ;
	}

	public static String fillWithSpaceByNumber(BigDecimal b, int requestLength) {
		String source = "" ;
		if(b != null) {
			source = b.toString() ;
			if(!NumberUtils.isDigits(b.abs().toString())) {
				StringBuffer reverse = new StringBuffer(source).reverse() ;
				while(reverse.toString().startsWith("0")) {
					reverse = new StringBuffer(StringUtils.chomp(reverse.reverse().toString(), "0")).reverse() ;
				}
				if(reverse.toString().startsWith(".")) {
					reverse.deleteCharAt(0) ;
				}
				source = reverse.reverse().toString() ;
			}
		}
		return StringUtils.leftPad(source, requestLength) ;
	}
	
	
	public static Object getNotNullObject(Object obj){
		if(obj==null){
			return "";
		}else{
			return obj;
		}
	}
	
	/**
	 * 檢核是否為Email格式
	 * 
	 * @param email
	 * @return String
	 */
	public static boolean isEmailFormat(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	    return Pattern.compile(regex).matcher(email).matches();
	}

}
