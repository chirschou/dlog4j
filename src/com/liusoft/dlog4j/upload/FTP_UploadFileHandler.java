/*
 *  FCKEditor_UploadServlet.java
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
package com.liusoft.dlog4j.upload;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import com.liusoft.dlog4j.base.FckUploadFileBeanBase;
import com.liusoft.util.ftp.FTPClientProxy;

/**
 * <p>用于将上传的文件通过FTP传到指定的服务器并返回文件的URL地址</p>
 * <p>配置如下：</p>
 * <p>
 * <code>
 * 		<!-- use disk file upload handler -->
		<init-param>
			<param-name>file_saved_class</param-name>
			<param-value>com.liusoft.dlog4j.upload.FTP_UploadFileHandler</param-value>
		</init-param>
		<init-param>
			<param-name>ftp_host</param-name>
			<param-value>ftp.dlogcn.com</param-value>
		</init-param>
		<init-param>
			<param-name>ftp_port</param-name>
			<param-value>21</param-value>
		</init-param>
		<init-param>
			<param-name>ftp_user</param-name>
			<param-value>user1</param-value>
		</init-param>
		<init-param>
			<param-name>ftp_password</param-name>
			<param-value>password1</param-value>
		</init-param>
		<init-param>
			<param-name>max_client</param-name>
			<param-value>10</param-value>
		</init-param>
		<init-param>
			<param-name>file_base_uri</param-name>
			<param-value>http://img.dlogcn.com</param-value>
		</init-param>
	</code>
    </p>
 * @author Winter Lau
 */
public class FTP_UploadFileHandler implements UploadFileHandler {

	protected final static Log log = LogFactory.getLog(FTP_UploadFileHandler.class);
	
	protected String host;
	protected int port = 21;
	protected String user;
	protected String password;	
	protected String baseURL;
	protected int maxClient = 10;	
	
	private FTPClientProxy ftp_proxy;
	
	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.upload.UploadFileHandler#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) {
		this.host = config.getInitParameter("ftp_host");
		this.user = config.getInitParameter("ftp_user");
		this.password = config.getInitParameter("ftp_password");
		try {
			this.port = Integer.parseInt(config.getInitParameter("ftp_port"));
		} catch (Exception e) {
			log.warn("Get value of ftp port failed.", e);
		}
		try {
			this.maxClient = Integer.parseInt(config
					.getInitParameter("max_client"));
		} catch (Exception e) {
			log.warn("Get value of max client count failed.", e);
		}
		ftp_proxy = FTPClientProxy.newInstance(host, port, user, password,
				maxClient);
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.upload.UploadFileHandler#save(java.io.File)
	 */
	public String save(HttpServletRequest req, HttpServletResponse res,
			File file) throws Exception 
	{
		FTPClient ftp = ftp_proxy.connect();
		try{
			//TODO:建相应目录并上传文件 
		}finally{
			ftp.disconnect();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.upload.UploadFileHandler#remove(FckUploadFileBeanBase)
	 */
	public boolean remove(final FckUploadFileBeanBase fbean) throws Exception {
		FTPClient ftp = ftp_proxy.connect();
		try{
			String path = fbean.getUri();
			return ftp.deleteFile(path);
		}finally{
			ftp.disconnect();
		}
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.upload.UploadFileHandler#destroy()
	 */
	public void destroy() {
		if(ftp_proxy != null)
			ftp_proxy.destroy();
		ftp_proxy = null;
	}

}
