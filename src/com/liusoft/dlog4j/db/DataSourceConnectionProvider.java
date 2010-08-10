/*
 *  DataSourceConnectionProvider.java
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
 */
package com.liusoft.dlog4j.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.util.StringUtils;
import com.liusoft.util.db._Connection;

/**
 * 让Hibernate支持各种实现了DataSource接口的数据源
 * @author Winter Lau
 */
public class DataSourceConnectionProvider implements ConnectionProvider {
	
	private final static String BASE_KEY = "dscp.";
	private final static String ENCODING_KEY = "dscp.encoding";
	private final static String DATASOURCE_KEY = "dscp.datasource";
	
	protected DataSource dataSource;
	protected boolean encoding = false;
	protected boolean useProxy = false;
	
	/* (non-Javadoc)
	 * @see org.hibernate.connection.ConnectionProvider#configure(java.util.Properties)
	 */
	public void configure(Properties props) throws HibernateException {
		initDataSource(props);
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.connection.ConnectionProvider#getConnection()
	 */
	public Connection getConnection() throws SQLException {		
		final Connection conn = dataSource.getConnection();
		if(useProxy && conn!=null){
			return (new _Connection(conn,encoding)).getConnection();
		}
		return conn;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.connection.ConnectionProvider#closeConnection(java.sql.Connection)
	 */
	public void closeConnection(Connection conn) throws SQLException {
		if(conn!=null && !conn.isClosed())
			conn.close();
	}

	/* (non-Javadoc)
	 * @see org.hibernate.connection.ConnectionProvider#close()
	 */
	public void close() throws HibernateException {
		if(dataSource != null)
		try {
			Method mClose = dataSource.getClass().getMethod("close");
			mClose.invoke(dataSource);
		} catch (Exception e) {
			throw new HibernateException(e);
		}
		dataSource = null;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.connection.ConnectionProvider#supportsAggressiveRelease()
	 */
	public boolean supportsAggressiveRelease() {
		return false;
	}

	/**
	 * Initialize the datasource
	 * @param props
	 * @throws HibernateException
	 */
	protected synchronized void initDataSource(Properties props) throws HibernateException {
		String dataSourceClass = null;
		Properties new_props = new Properties();
		Iterator keys = props.keySet().iterator();
		while(keys.hasNext()){
			String key = (String)keys.next();
			//System.out.println(key+"="+props.getProperty(key));
			if(ENCODING_KEY.equalsIgnoreCase(key)){
				encoding = "true".equalsIgnoreCase(props.getProperty(key));
				useProxy = true;
			}
			else if(DATASOURCE_KEY.equalsIgnoreCase(key)){
				dataSourceClass = props.getProperty(key);
			}
			else if(key.startsWith(BASE_KEY)){
				String value = props.getProperty(key);
				value = StringUtils.replace(value, "{DLOG4J}", Globals.WEBAPP_PATH);
				new_props.setProperty(key.substring(BASE_KEY.length()), value);
			}
		}
		if(dataSourceClass == null)
			throw new HibernateException("Property 'dscp.datasource' no defined.");
		try {
			dataSource = (DataSource)Class.forName(dataSourceClass).newInstance();
			BeanUtils.populate(dataSource, new_props);
		} catch (Exception e) {
			throw new HibernateException(e);
		}
	}

}
