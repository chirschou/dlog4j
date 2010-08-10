/*
 *  DataExportServlet.java
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
package com.liusoft.dlog4j.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.UserLoginManager;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.dao.SiteDAO;
import com.liusoft.dlog4j.util.DateUtils;
import com.liusoft.dlog4j.util.RequestUtils;

/**
 * 用于将网站数据导出成纯HTML文本打包的压缩文件
 * 便于用户备份数据
 * 使用示例： http://localhost/servlet/export?sid=123
 * 必须得是站长才能导出数据，且需要限制导出的次数
 * @author Winter Lau
 */
public class DLOG_DataExportServlet extends HttpServlet {

	/**
	 * TODO: 导出数据
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		//判断用户是否已登录
		SessionUserObject loginUser = UserLoginManager.getLoginUser(req, res, true);
		if(loginUser==null){
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		int site_id = RequestUtils.getParam(req, "sid", -1);
		SiteBean site = SiteDAO.getSiteByID(site_id);
		if(site==null||site.getStatus()!=SiteBean.STATUS_NORMAL){
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "site #"+site_id+" not found.");
			return;
		}
		//判断上次备份时间是否已经超过一周
		if(site.getLastExportTime()!=null && DateUtils.diff_in_date(new Date(), site.getLastExportTime())<7){
			alert(req, res, "Data export must be after 1 week since last export.");
			return;
		}
		//生成数据压缩包到web的临时目录，并将请求重定向到这个文件
		//该文件在一段时间内自动删除
		
	}
	
	/**
	 * 在浏览器上提示信息
	 * 
	 * @param req
	 * @param res
	 * @param msg
	 * @throws IOException
	 */
	protected void alert(HttpServletRequest req, HttpServletResponse res,
			String msg) throws IOException {
		String html = MessageFormat.format(msg_tmp, msg);
		res.getWriter().write(html);
	}

	private final static String msg_tmp = "<script language='javascript'>alert('{0}');</script>";
}
