/*
 *  SecureDataSourceConnectionProvider.java
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
 *  2006-4-24
 */
package com.liusoft.dlog4j.db;

import java.util.Properties;

import org.hibernate.HibernateException;

import com.liusoft.dlog4j.util.StringUtils;

/**
 * 支持数据库配置中使用加密的口令
 * @author liudong
 */
public class SecureDataSourceConnectionProvider extends
		DataSourceConnectionProvider {

	private final static String PASSWORD_KEY = "dscp.password";
	
	/**
	 * 对加密密码进行解码
	 */
	public void configure(Properties props) throws HibernateException {
		String sec_pwd = props.getProperty(PASSWORD_KEY);
		String password = StringUtils.decryptPassword(sec_pwd);
		props.setProperty(PASSWORD_KEY, password);
		super.configure(props);
	}
	
	/**
	 * @see encrypt_pwd.bat
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length == 0){
			System.out.println("Usage: java com.liusoft.dlog4j.db.SecureDataSourceConnectionProvider <password>");
			return;
		}
		System.out.println("The encrypt password is : " + StringUtils.encryptPassword(args[0]));
	}

}
