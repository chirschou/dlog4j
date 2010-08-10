/*
 *  _Statement.java
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
package com.liusoft.util.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库语句对象的代理
 * @author Winter Lau
 */
public class _Statement implements InvocationHandler {

	private Statement statement;
	private boolean decode;

	private Class[] infs;
	private Class[] infs2;

	public _Statement(Statement stmt, boolean decode) {
		statement = stmt;
		this.decode = decode;
	}

	/**
	 * 获取Statement实例的代理
	 * @return
	 */
	public Statement getStatement() {
		if(statement instanceof PreparedStatement){
			infs2 = statement.getClass().getInterfaces();
			if(infs2==null || infs2.length==0)
				infs2 = IPS;
			return (Statement)Proxy.newProxyInstance(
				statement.getClass().getClassLoader(),infs2, this);
		}
		else{
			infs = statement.getClass().getInterfaces();
			if(infs==null || infs.length==0)
				infs = IS;
			return (Statement)Proxy.newProxyInstance(
				statement.getClass().getClassLoader(),infs, this);
		}
	}

	public Object invoke(Object proxy, Method m, Object args[])	throws Throwable {
		String method = m.getName();

		if (decode && SETSTRING.equals(method)){
			try {
				String param = (String) args[1];
				if (param != null)
					param = new String(param.getBytes(), "8859_1");
				return m.invoke(statement, new Object[] { args[0], param });
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		if (decode && (EXECUTEQUERY.equals(method) || GETRESULTSET.equals(method))){
			try {
				ResultSet rs = (ResultSet) m.invoke(statement, args);
				return (new _ResultSet(rs, decode)).getResultSet();
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		try {
			return m.invoke(statement, args);
		} catch (InvocationTargetException e) {
			if (GETQUERYTIMEOUT.equals(method))
				return new Integer(gqt_return);
			else
				throw e.getTargetException();
		}
	}
	
	private final static Class[] IPS = new Class[]{PreparedStatement.class};
	private final static Class[] IS = new Class[]{Statement.class};

	private static final String GETQUERYTIMEOUT = "getQueryTimeout";
	private static final int gqt_return = 0;
	private static final String SETSTRING = "setString";
	private static final String EXECUTEQUERY = "executeQuery";
	private static final String GETRESULTSET = "getResultSet";
}
