/*
 *  StringUtils.java
 *  
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *  
 *  Author: Winter Lau (javayou@gmail.com)
 *  http://dlog4j.sourceforge.net
 */
package com.liusoft.dlog4j.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

/**
 * 字符串工具集合
 * @author Winter Lau
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

	/**
	 * 如果系统中存在旧版本的数据，则此值不能修改，否则在进行密码解析的时候出错
	 */
    private static final String PASSWORD_CRYPT_KEY = "__jDlog_";
	private final static String DES = "DES";
    private final static String ISO8859_1 = "8859_1";
	
	private final static NodeFilter nodeFilter = new NodeFilter() {
		public boolean accept(Node node) {
			return true;
		}
	};

	/**
	 * retrive the extend name of the given filename
	 * @param fn
	 * @return
	 */
	public static String getFileExtend(String fn){
		if(isEmpty(fn))
			return null;
		int idx = fn.lastIndexOf('.')+1;
		if(idx==0 || idx >= fn.length())
			return null;
		return fn.substring(idx);
	}
	
	/**
	 * 将字符串用ch分割并放入队列
	 * @param tags
	 * @param ch
	 * @return
	 */
	public static List stringToList(String tags, String ch){
		if(tags==null)
			return null;
		ArrayList tagList = new ArrayList();
		StringTokenizer st = new StringTokenizer(tags, ch);
		while(st.hasMoreElements()){
			tagList.add(st.nextToken());
		}
		return tagList;
	}

	/**
	 * 将字符串用空格分割并放入队列
	 * @param tags
	 * @return
	 */
	public static List stringToList(String tags){
		if(tags==null)
			return null;
		ArrayList tagList = new ArrayList();
		StringTokenizer st = new StringTokenizer(tags);
		while(st.hasMoreElements()){
			tagList.add(st.nextToken());
		}
		return tagList;
	}

	/**
	 * BASE64编码
	 * @param s
	 * @return String
	 */
	public static byte[] enBASE64(byte[] bytes){
		return Base64Code.encode(bytes);
	}
	/**
	 * BASE64反编码
	 * @param bytes
	 * @return byte[]
	 */
	public static byte[] deBASE64(byte[] bytes){
		return Base64Code.decode(bytes);
	}
	/**
	 * BASE64编码
	 * @param s
	 * @return String
	 */
	public static String enBASE64(String s) {
		if (s != null) {
			byte abyte0[] = s.getBytes();
			abyte0 = Base64Code.encode(abyte0);
			s = new String(abyte0);
			return s;
		} 
		return null;
	}
	/**
	 * BASE64反编码
	 * @param s
	 * @return String
	 */
	public static String deBASE64(String s) {
		if (s != null) {
			byte abyte0[] = s.getBytes();
			abyte0 = Base64Code.decode(abyte0);
			s = new String(abyte0);
			abyte0 = null;
			return s;
		} 
		return null;
	}
	/**
	 * HTML输出内容格式转换
	 * @param content
	 * @return
	 */
	public static String formatContent(String content) {
        if(content==null)
            return "";        
		String randomStr = String.valueOf(System.currentTimeMillis());
		String html = StringUtils.replace(content,"&nbsp;",randomStr);		
		html = StringUtils.replace(html, "&", "&amp;");
		html = StringUtils.replace(html, "'", "&apos;");
		html = StringUtils.replace(html, "\"", "&quot;");
		html = StringUtils.replace(html, "\t", "&nbsp;&nbsp;");// 替换跳格
		html = StringUtils.replace(html, " ", "&nbsp;");// 替换空格
		html = StringUtils.replace(html, "<", "&lt;");
		html = StringUtils.replace(html, ">", "&gt;");
		return StringUtils.replace(html,randomStr,"&nbsp;").trim();
	}

    /**
	  * 抽取纯文本信息
	  * @param inputHtml
	  * @return
	  */
	public static String extractText(String inputHtml) throws Exception {
		StringBuffer text = new StringBuffer();
		Parser parser = new Parser();
		parser.setInputHTML(new String(inputHtml.getBytes(),ISO8859_1));
		//Parser parser = Parser.createParser(new String(inputHtml.getBytes(),ISO8859_1));
		//遍历所有的节点
		NodeList nodes = parser.extractAllNodesThatMatch(nodeFilter);
		for(int i=0;i<nodes.size();i++){
			Node node = nodes.elementAt(i);
			text.append(new String(node.toPlainTextString().getBytes(ISO8859_1)));
		}
		return text.toString();
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		if(email==null)
			return false;
		email = email.trim();
		if(email.indexOf(' ')!=-1)
			return false;
		
		int idx = email.indexOf('@');
		if(idx==-1 || idx==0 || (idx+1)==email.length())
			return false;
		if(email.indexOf('@', idx+1)!=-1)
			return false;
        if(email.indexOf('.')==-1)
            return false;
		return true;
		/*
		Pattern emailer;
		if(emailer==null){
			String check = "^([a-z0-9A-Z]+[-|\\._]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			emailer = Pattern.compile(check);
		}
	    Matcher matcher = emailer.matcher(email);
	    return matcher.matches();
		*/
	}
	
	/**
	 * 判断字符串是否是一个IP地址
	 * @param addr
	 * @return
	 */
	public static boolean isIPAddr(String addr){
		if(isEmpty(addr))
			return false;
		String[] ips = split(addr, '.');
		if(ips.length != 4)
			return false;
		try{
			int ipa = Integer.parseInt(ips[0]);
			int ipb = Integer.parseInt(ips[1]);
			int ipc = Integer.parseInt(ips[2]);
			int ipd = Integer.parseInt(ips[3]);
			return ipa >= 0 && ipa <= 255 && ipb >= 0 && ipb <=255 && ipc >= 0
					&& ipc <= 255 && ipd >= 0 && ipd <=255;
		}catch(Exception e){}
		return false;
	}
	/**
	 * 加密
	 * @param src 数据源
	 * @param key 密钥，长度必须是8的倍数
	 * @return	  返回加密后的数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] src, byte[] key)
		throws Exception {
		//		DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(src);
	}
	
	/**
	 * 解密
	 * @param src	数据源
	 * @param key	密钥，长度必须是8的倍数
	 * @return		返回解密后的原始数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] key)
		throws Exception {
		//		DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		return cipher.doFinal(src);
	}
    /**
     * 数据解密
     * @param data
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public final static String decrypt(String data, String key){
    	if(data!=null)
        try {
            return new String(decrypt(hex2byte(data.getBytes()),key.getBytes()));
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    /**
     * 数据加密
     * @param data
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public final static String encrypt(String data, String key){
    	if(data!=null)
        try {
            return byte2hex(encrypt(data.getBytes(),key.getBytes()));
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    /**
     * 密码解密
     * @param data
     * @return
     * @throws Exception
     */
    public final static String decryptPassword(String data){
    	if(data!=null)
        try {
            return new String(decrypt(hex2byte(data.getBytes()),PASSWORD_CRYPT_KEY.getBytes()));
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    /**
     * 密码加密
     * @param password
     * @return
     * @throws Exception
     */
    public final static String encryptPassword(String password){
    	if(password!=null)
        try {
            return byte2hex(encrypt(password.getBytes(),PASSWORD_CRYPT_KEY.getBytes()));
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
	/**
	 * 二行制转字符串
	 * @param b
	 * @return
	 */
    public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; b!=null && n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
    
    public static byte[] hex2byte(byte[] b) {
        if((b.length%2)!=0)
            throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length/2];
		for (int n = 0; n < b.length; n+=2) {
		    String item = new String(b,n,2);
		    b2[n/2] = (byte)Integer.parseInt(item,16);
		}
        return b2;
    }
    
    /**
     * 大小写无关的字符串替换策略
     * @param str
     * @param src
     * @param obj
     * @return
     */
    public static String replaceIgnoreCase(String str, String src, String obj){
    	String l_str = str.toLowerCase();
    	String l_src = src.toLowerCase();
    	int fromIdx = 0;
    	StringBuffer result = new StringBuffer();
    	do{
    		int idx = l_str.indexOf(l_src, fromIdx);
    		if(idx==-1)
    			break;
    		result.append(str.substring(fromIdx, idx));
    		result.append(obj);
    		fromIdx = idx + src.length();
    	}while(true);
    	result.append(str.substring(fromIdx));
    	return result.toString();
    }

	/**
	 * 根据汉字字符获得笔画数,拼音和非法字符默认为0
	 * @param charcator
	 * @return int
	 */
	public static int getStrokeCount(char charcator) {
		byte[] bytes = (String.valueOf(charcator)).getBytes();
		if (bytes == null || bytes.length > 2 || bytes.length <= 0) {
			// 错误引用,非合法字符
			return 0;
		}
		if (bytes.length == 1) {
			// 英文字符
			return 0;
		}
		if (bytes.length == 2) {
			// 中文字符
			int highByte = 256 + bytes[0];
			int lowByte = 256 + bytes[1];
			return getStrokeCount(highByte, lowByte);
		}

		// 未知错误
		return 0;
	}

	 /** 
	  * @param highByte 高位字节
	  * @param lowByte 低位字节
	  * @return int
	  */
	private static int getStrokeCount(int highByte, int lowByte) {
		if (highByte < 0xB0 || highByte > 0xF7 || lowByte < 0xA1
				|| lowByte > 0xFE) {
			// 非GB2312合法字符
			return -1;
		}
		int offset = (highByte - 0xB0) * (0xFE - 0xA0) + (lowByte - 0xA1);
		return Constants.gb2312StrokeCount[offset];
	}

	/**
	 * 该方法返回一个字符串的拼音，对于要做敏感字
	 * 检查时应该一个字一个字来获取其拼音以免无法
	 * 得知每个字对应的拼音。
	 * @param word
	 * @return String
	 */
	public static String getPinyin(String word) {
		String pinyin = "";
		for (int i = 0; i < word.length(); i++)
			pinyin += getPinyin2(getCode(word.charAt(i)));
		return pinyin;
	}
	/**
	 * 该方法返回一个字符的DBCS编码值
	 * @param cc
	 * @return int
	 */
	protected static int getCode(char cc) {
		byte[] bs = String.valueOf(cc).getBytes();
		int code = (bs[0] << 8) | (bs[1] & 0x00FF);
		if(bs.length < 2)
			code = (int)cc;
		bs = null;
		return code;
	}
	/**
	 * 该方法通过DBCS的编码值到哈希表中查询得到对应的拼音串
	 * @param hz
	 * @return String
	 */
	protected static String getPinyin2(int hz) {
		String py = "";
		if (hz > 0 && hz < 160)
			py += hz;
		//else if (hz < -20319 || hz > -10247);
		else if (hz <= -10247 && hz >= -20319){
			PinyinCode pc = null;
			int i = Constants.pinyin.size() - 1;
			for (; i >= 0; i--) {
				pc = (PinyinCode) Constants.pinyin.get(i);
				if (pc.code <= hz)
					break;
			}
			if (i >= 0)
				py = pc.pinyin;
		}
		return py;
	}
	
    /**
     * 用户名必须是数字或者字母的结合
     * @param username
     * @return
     */
    public static boolean isLegalUsername(String username) {
        for(int i=0;i<username.length();i++){
            char ch = username.charAt(i);
            if(!isAscii(ch)&& 
                ch != '.' &&
                ch != '_' &&
                ch != '-' &&
                ch != '+' &&
                ch != '(' &&
                ch != ')' &&
                ch != '*' &&
                ch != '^' &&
                ch != '@' &&
                ch != '%' &&
                ch != '$' &&
                ch != '#' &&
                ch != '~' &&
                ch != '-')
                return false;
        }
        return true;
    }
    
    /**
     * 判断是否是字母和数字的结合
     * @param name
     * @return
     */
    public static boolean isAsciiOrDigit(String name){
        for(int i=0;i<name.length();i++){
            char ch = name.charAt(i);
            if(!isAscii(ch))
            	return false;
        }
        return true;
    }
    
    public static boolean isAscii(char ch){
    	return (ch >='a' && ch <='z') || (ch >='A' && ch <='Z') || (ch >='0' && ch <='9');
    }
    
    /**
     * 返回姓名的拼音首字母
     * @param username
     * @return
     */
    public static String getTxlUserPinyin(String username) {
        if(username.getBytes().length == (2 * username.length())){
            //纯中文
            StringBuffer pinyin = new StringBuffer();
            for(int i=0;i<username.length();i++){
                String py = StringUtils.getPinyin(String.valueOf(username.charAt(i)));
                if(py!=null &&  py.length()>0)
                    pinyin.append(py.charAt(0));
                else
                    pinyin.append('V');
            }
            return pinyin.toString().toUpperCase();
        }
        else
        if(username.getBytes().length == username.length()){
            int len = (username.length()>3)?3:username.length();
            return username.substring(0,len).toUpperCase();
        }
        else{
            StringBuffer pinyin = new StringBuffer();
            for(int i=0;i<username.length();i++){
                char ch = username.charAt(i);
                try{
                    String py = StringUtils.getPinyin(String.valueOf(ch));
                    if(py!=null &&  py.length()>0)
                        pinyin.append(py.charAt(0));
                    else
                        pinyin.append(ch);
                }catch(ArrayIndexOutOfBoundsException e){}
                if(pinyin.length()>=3)
                    break;
            }
            return pinyin.toString().toUpperCase();
        }
        
    }

	 
}

/**
 * BASE64编码解码实现类
 * @author liusoft
 * created on 2002-12-19
 */
class Base64Code {


	protected static byte[] _encode_map = 
		{
		(byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
		(byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
		(byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U',
		(byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',

		(byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
		(byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
		(byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u',
		(byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z',

		(byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', 
		(byte)'5', (byte)'6', (byte)'7', (byte)'8', (byte)'9',

		(byte)'+', (byte)'/' };
	
	protected static byte _decode_map[] = new byte[128];
	static {
		/*
		 * Fill in the decode map
		 */
		for (int i = 0; i < _encode_map.length; i++) {
			_decode_map[_encode_map[i]] = (byte)i;
		}
	}


	/**
	 * This class isn't meant to be instantiated.
	 */
	private Base64Code () 
	{

	}



	/**
	 * This method encodes the given byte[] using the Base64 encoding
	 * 
	 *
	 * @param  data the data to encode.
	 * @return the Base64 encoded <var>data</var>
	 */
	public final static byte[] encode (byte[] data) {

		if (data == null) {
			return (null);
		}

		/*
		 * Craete a buffer to hold the results
		 */
		byte dest[] = new byte[((data.length + 2) / 3) * 4];


		/*
		 * 3-byte to 4-byte conversion and 
		 * 0-63 to ascii printable conversion
		 */
		int i, j;
		int data_len = data.length - 2;
		for (i = 0, j = 0; i < data_len; i += 3) {

			dest[j++] = _encode_map[(data[i] >>> 2) & 077];
	    		dest[j++] = _encode_map[(data[i + 1] >>> 4) & 017 |
				(data[i] << 4) & 077];
	    		dest[j++] = _encode_map[(data[i + 2] >>> 6) & 003 |
				(data[i + 1] << 2) & 077];
	    		dest[j++] = _encode_map[data[i + 2] & 077];
		}
	
		if (i < data.length) {
			dest[j++] = _encode_map[(data[i] >>> 2) & 077];

			if (i < data.length-1) {
			    dest[j++] = _encode_map[(data[i + 1] >>> 4) & 017 |
				    (data[i] << 4) & 077];
			    dest[j++] = _encode_map[(data[i + 1] << 2) & 077];
	    		} else {
			    dest[j++] = _encode_map[(data[i] << 4) & 077];
			}
		}


		/*
		 * Pad with "=" characters
		 */
		for ( ; j < dest.length; j++) {
			dest[j] = (byte)'=';
		}

		return (dest);
	}


	/**
	 * This method decodes the given byte[] using the Base64 encoding
	 *
	 *
	 * @param  data the Base64 encoded data to decode.
	 * @return the decoded <var>data</var>.
	 */
	public final static byte[] decode (byte[] data) {

		if (data == null)
			return (null);

		/*
		 * Remove the padding on the end
		 */
		int ending = data.length;
		if (ending < 1) {
			return (null);
		}
		while (data[ending - 1] == '=')
			ending--;

		/*
		 * Create a buffer to hold the results
		 */
		byte dest[] = new byte[ending - data.length / 4];


		/*
		 * ASCII printable to 0-63 conversion
		 */
		for (int i = 0; i < data.length; i++) {
			data[i] = _decode_map[data[i]];
		}

	
		/*
		 * 4-byte to 3-byte conversion
		 */
		int i, j;
		int dest_len = dest.length - 2;
		for (i = 0, j = 0; j < dest_len; i += 4, j += 3) {
			dest[j] = (byte) (((data[i] << 2) & 255) |
			 	((data[i + 1] >>> 4) & 003));
			dest[j + 1] = (byte) (((data[i + 1] << 4) & 255) |
				((data[i + 2] >>> 2) & 017));
	    		dest[j + 2] = (byte) (((data[i + 2] << 6) & 255) |
			    (data[i + 3] & 077));
		}

		if (j < dest.length) {
			dest[j] = (byte) (((data[i] << 2) & 255) |
				((data[i + 1] >>> 4) & 003));
		}

		j++;
		if (j < dest.length) {
			dest[j] = (byte) (((data[i + 1] << 4) & 255) |
				((data[i + 2] >>> 2) & 017));
		}

		return (dest);
	}
    
}