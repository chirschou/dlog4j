/*
 *  DLOG_ICPRecordServlet.java
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
 *  2006-5-23
 */
package com.liusoft.dlog4j.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 用于处理来自/cert/bazs.cert的请求
 * @author liudong
 */
public class DLOG_ICPRecordServlet extends HttpServlet {

	private String cert_path;
	
	public void init() throws ServletException {
		cert_path = this.getInitParameter("cert-path");
		if(StringUtils.isEmpty(cert_path)){
			cert_path = getServletContext().getRealPath("/cert");
		}
		else{
			if(cert_path.startsWith(Globals.LOCAL_PATH_PREFIX))
				cert_path = cert_path.substring(Globals.LOCAL_PATH_PREFIX.length());		
			else if(cert_path.startsWith("/"))
				cert_path = getServletContext().getRealPath(cert_path);
		}
		if(!cert_path.endsWith(File.separator))
			cert_path += File.separator;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String serverName = req.getServerName();
		String domain = RequestUtils.getDomainOfServerName(serverName);
		res.setContentType("application/octet-stream");
		StringBuffer cert_name = new StringBuffer(cert_path);
		cert_name.append(domain);
		cert_name.append(File.separator);
		cert_name.append("bazs.cert");
		File f = new File(cert_name.toString());
		if(!f.exists() || !f.isFile()){
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		FileInputStream fis = new FileInputStream(f);
		try{
			OutputStream out = res.getOutputStream();
			byte[] buf = new byte[1024];
			do{
				int rc = fis.read(buf);
				if(rc == -1)
					break;
				out.write(buf, 0, rc);
				if(rc < buf.length)
					break;
			}while(true);
			out.close();
		}finally{
			fis.close();
		}
	}

}
