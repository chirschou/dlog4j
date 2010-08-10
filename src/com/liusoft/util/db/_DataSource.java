/*
 *  _DataSource.java
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
import java.sql.Connection;

import javax.sql.DataSource;

/**
 * 数据源对象的接管，由于Struts本身使用的是dbcp连接池
 * 此类就是接管DataSource接口
 * @author Winter Lau
 */
public class _DataSource implements InvocationHandler {

	private DataSource dataSource;
	private boolean encoding;

	public _DataSource(DataSource ds, boolean encoding) {
		dataSource = ds;
		this.encoding = encoding;
	}

	/**
	 * 获取DataSource的代理
	 * @return
	 */
	public DataSource getDataSource() {
		Class[] infs = dataSource.getClass().getInterfaces();
		if(infs==null||infs.length==0)
			infs = IDS;
		return (DataSource) Proxy.newProxyInstance(
			dataSource.getClass().getClassLoader(), infs, this);
	}

	public Object invoke(Object proxy, Method m, Object args[])	throws Throwable 
	{
		if (METHOD_NAME.equals(m.getName())){
			try {
				Connection conn = (Connection) m.invoke(dataSource, args);
				return (conn==null)?null:(new _Connection(conn,encoding)).getConnection();
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		try {
			return m.invoke(dataSource, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private final static String METHOD_NAME = "getConnection";
	
	private final static Class[] IDS = new Class[]{DataSource.class};
	
}
