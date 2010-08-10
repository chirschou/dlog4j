/*
 *  Globals.java
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
 *  Author: Winter Lau
 *  http://dlog4j.sourceforge.net
 *  
 */
package com.liusoft.dlog4j;

/**
 * DLOG4J的全局常量定义
 * @author Winter Lau
 */
public class Globals {

	public final static String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";
	
	public final static String LOCAL_PATH_PREFIX = "file://";
	
	public final static String ENC_UTF_8 = "UTF-8";
	public final static String ENC_8859_1 = "8859_1";

	public final static String RANDOM_LOGIN_KEY = "RANDOM_LOGIN_KEY";
	
	public final static String PARAM_SID = "sid";
	
	public final static String MAIL_QUEUE = "MAIL_QUEUE";

	public final static String USER_AGENT = "user-agent";
	
	public final static String SESSION_ID_KEY_IN_COOKIE = "DLOG_SESSION_ID";
	
	public final static String ALBUM_VERIFY_KEY = "ALBUM_";
	
	/**
	 * 运行过程中webapp所在的路径
	 * 由DLOG_ActionServlet进行赋值
	 * @see com.liusoft.dlog4j.servlet.DLOG_ActionServlet#init()
	 */
	public static String WEBAPP_PATH ;

}
