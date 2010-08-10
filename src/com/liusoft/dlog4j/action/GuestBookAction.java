/*
 *  GuestBookAction.java
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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.HibernateException;

import com.liusoft.dlog4j.base.ClientInfo;
import com.liusoft.dlog4j.beans.GuestBookBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.GuestBookDAO;
import com.liusoft.dlog4j.dao.SiteDAO;
import com.liusoft.dlog4j.formbean.GuestBookForm;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 留言板的Action类
 * @author Winter Lau
 */
public class GuestBookAction extends ActionBase {

	/**
	 * 站长回复留言板之留言
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doReply(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		GuestBookForm msgform = (GuestBookForm)form;
		super.validateClientId(request, msgform);
		if(StringUtils.isNotEmpty(msgform.getReply())){
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser!=null){
				//判断是否为站长
				SiteBean site = SiteDAO.getSiteByID(msgform.getSid());
				if(site!=null && site.isOwner(loginUser)){
					//回复留言
					GuestBookBean gbean = GuestBookDAO.getMsg(msgform.getSid(), msgform.getId());
					if(gbean!=null){
						String reply = super.autoFiltrate(site,msgform.getReply());						
						if(reply.length()>MAX_GB_REPLY_LENGTH)
							reply = reply.substring(0, MAX_GB_REPLY_LENGTH);
						gbean.setReply(super.filterScriptAndStyle(reply));
						gbean.setReplyTime(new Date());
						GuestBookDAO.flush();
					}
				}
			}
		}
		String ext = null;
		if(msgform.getPage()>1){
			ext = "page="+msgform.getPage();
		}
		return makeForward(mapping.findForward("list"), msgform.getSid(), ext);
	}
	/**
	 * 删除留言板之留言
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_msg_id)
			throws Exception 
	{
		int msg_id = Integer.parseInt(s_msg_id); 
		GuestBookForm msgform = (GuestBookForm)form;
		UserBean loginUser = super.getLoginUser(request, response);
		if(loginUser!=null && loginUser.getOwnSiteId()==msgform.getSid()){
			GuestBookDAO.deleteMsg(msgform.getSid(),msg_id);
		}
		String ext = null;
		if(msgform.getPage()>1)
			ext = "page=" + msgform.getPage();
		//System.out.println("ext="+ext+",page="+msgform.getPage());
		return makeForward(mapping.findForward("list"), msgform.getSid(), ext);
	}
	
	/**
	 * 留言板之发表留言
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		GuestBookForm msgform = (GuestBookForm)form;
		super.validateClientId(request, msgform);
		ActionMessages msgs = new ActionMessages();
		while(true){
			if(StringUtils.isEmpty(msgform.getContent())){
				msgs.add("content", new ActionMessage("error.empty_content"));
				break;
			}
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser==null){
				msgs.add("message", new ActionMessage("error.user_not_login"));
				break;
			}
			else if(loginUser.getStatus()!=UserBean.STATUS_NORMAL){
				msgs.add("message", new ActionMessage("error.user_not_available"));
				break;
			}
			SiteBean site = super.getSiteByID(msgform.getSid());
			if(site==null){
				msgs.add("message", new ActionMessage("error.site_not_available"));
				break;
			}
			//检查黑名单
			if(isUserInBlackList(site, loginUser)){
				msgs.add("message", new ActionMessage("error.user_in_blacklist"));
				break;
			} 
			GuestBookBean msgbean = new GuestBookBean();
			String content = super.autoFiltrate(site,msgform.getContent());
			if(content.length()>MAX_GB_COUNT_LENGTH)
				content = content.substring(0, MAX_GB_COUNT_LENGTH);
			msgbean.setContent(super.filterScriptAndStyle(content));
			msgbean.setClient(new ClientInfo(request, 0));
			msgbean.setUser(loginUser);
			msgbean.setSiteId(site.getId());
			try{
				GuestBookDAO.createMsg(msgbean);
			}catch(HibernateException e){
				context().log("undelete diary failed.", e);
				msgs.add("message", new ActionMessage("error.database", e.getMessage()));
			}
			break;
		}
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			return mapping.findForward("pub");
		}
		
		return makeForward(mapping.findForward("list"), msgform.getSid());
	}
}
