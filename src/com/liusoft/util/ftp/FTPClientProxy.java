/*
 *  FTPClientProxy.java
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
package com.liusoft.util.ftp;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

/**
 * FTP客户端连接池访问入口
 * @author liudong
 */
public class FTPClientProxy{
	
	/**
	 * 初始化连接池
	 * @param host
	 * @param port
	 * @param user
	 * @param pwd
	 * @param pool_size
	 * @return
	 */
	public final static FTPClientProxy newInstance(String host,int port,String user, String pwd,int pool_size){
		return new FTPClientProxy(host,port,user,pwd,pool_size);
	}
	
	/**
	 * 初始化匿名FTP服务器连接池
	 * @param host
	 * @param port
	 * @param pool_size
	 * @return
	 */
	public final static FTPClientProxy newInstance(String host,int port,int pool_size){
		return new FTPClientProxy(host,port,"anonymous","dlog4j@dlog.cn",pool_size);
	}
	
	private String host;
	private int port = 21;
	private String user;
	private String pwd;
	private int pool_size = 10;
	
	private FTPClientProxy(String host,int port,String user, String pwd,int pool_size){
		this.host = host;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
		this.pool_size = pool_size;
	}
	
	/**
	 * 连接到FTP服务器
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 * @throws IllegalAccessException
	 */
	public FTPClient connect() throws SocketException, IOException, IllegalAccessException{
		FTPClient ftp = new FTPClient();
		ftp.connect(host, port);
		if(ftp.login(user, pwd))
			return ftp;
		throw new IllegalAccessException();
	}
	
	/**
	 * 释放资源
	 */
	public void destroy(){
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}



	public String getHost() {
		return host;
	}

	public int getPool_size() {
		return pool_size;
	}

	public int getPort() {
		return port;
	}

	public String getPwd() {
		return pwd;
	}

	public String getUser() {
		return user;
	}

}
