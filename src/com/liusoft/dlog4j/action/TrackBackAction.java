/*
 *  TrackBackAction.java
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
package com.liusoft.dlog4j.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liusoft.dlog4j.beans.TrackBackBean;
import com.liusoft.dlog4j.dao.TrackBackDAO;
import com.liusoft.dlog4j.formbean.TrackBackForm;

/**
 * 用于处理Trackback请求
 * @author Winter Lau
 */
public class TrackBackAction extends Action {

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception 
	{
		TrackBackForm tbf = (TrackBackForm)form;
		String msg = validate(tbf);
		if(msg==null){
			try{
				TrackBackBean tbb = new TrackBackBean();
				tbb.setBlogName(tbf.getBlog_name());
				tbb.setExcerpt(tbf.getExcerpt());
				tbb.setParentId(tbf.getId());
				tbb.setParentType(tbf.getType());
				tbb.setRemoteAddr(req.getRemoteAddr());
				tbb.setTitle(tbf.getTitle());
				tbb.setTrackTime(new Date());
				tbb.setUrl(tbf.getUrl());
				TrackBackDAO.create(tbb);
			}catch(Exception e){
				getServlet().log("TrackBackAction.execute failed.", e);
				msg = e.getMessage();
			}
		}
		String xml = getResponse(msg!=null, msg);
		res.getWriter().print(xml);
		return null;
	}
	

    /**
     * 验证输入的有效性
     */
    protected String validate(TrackBackForm form) {
        if(StringUtils.isEmpty(form.getUrl())) 
            return "url is empty";
        else
        if(form.getId()<0)
            return "Illegal value of object id";
        else
        if(StringUtils.isEmpty(form.getBlog_name()))
            return "Blog_name is empty";
        else
        if(StringUtils.isEmpty(form.getTitle()))
            return "Title is empty";
        return null;
    }
    
	
	/**
	 * 得到TrackBack的反馈信息
	 * @param error
	 * @param msg
	 * @return
	 */
	protected String getResponse(boolean error, String msg){
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<response>");
		if(error){
			xml.append("<error>1</error>");
			xml.append("<message>");
			xml.append(msg);
			xml.append("</message>");
		}
		else
			xml.append("<error>0</error>");
		xml.append("</response>");
		return xml.toString();
	}
}
