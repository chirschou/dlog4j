/*
 *  LinkAction.java
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

import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.beans.LinkBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.LinkDAO;
import com.liusoft.dlog4j.formbean.LinkForm;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 友情链接相关的操作
 * @author Winter Lau
 */
public class LinkAction extends AdminActionBase {

	private final static String[] methods = new String[]{"AddSiteToLink"};

	protected String[] methodsIgnore() {
		return methods;
	}
	
	/**
	 * 添加某个网站到自己的友情链接中
	 * http://localhost/html/sitemgr/link.do?eventSubmit_AddSite=1&fromPage=xxx.vm
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	protected ActionForward doAddSiteToLink(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String s_site_id) throws Exception 
	{
		int site_id = Integer.parseInt(s_site_id);
		String msg = null;
		do{
			//判断用户是否登录		
			SessionUserObject loginUser = super.getLoginUser(request, response);
			if(loginUser==null||loginUser.getStatus()!=UserBean.STATUS_NORMAL){
				msg = super.getMessage(request, null, "error.user_not_login");
				break;
			}
			//判断用户是否注册有个人网记
			if(loginUser.getOwnSiteId()<1){
				msg = super.getMessage(request, null, "error.user_not_have_a_site");
				break;
			}
			if(loginUser.getOwnSiteId()==site_id){
				msg = super.getMessage(request, null, "error.cannot_add_myself");
				break;
			}
			//判断用户的个人网记是否有效
			SiteBean toSite = super.getSiteByID(loginUser.getOwnSiteId());
			if(toSite==null){
				msg = super.getMessage(request, null, "error.site_not_available");
				break;
			}
			//检查链接是否已经存在
			if(LinkDAO.isInnerSiteExists(toSite, site_id)){
				msg = super.getMessage(request, null, "error.link_already_exist");
				break;
			}
			//添加链接
			SiteBean friendSite = super.getSiteByID(site_id);
			if(friendSite==null){
				msg = super.getMessage(request, null, "error.site_not_available");
				break;
			}
			LinkBean lbean = new LinkBean();
			lbean.setSiteId(toSite.getId());
			lbean.setCreateTime(new Date());
			lbean.setTitle(friendSite.getFriendlyName());
			lbean.setType(LinkBean.TYPE_INNER);
			lbean.setUrl(s_site_id);
			LinkDAO.create(lbean, 0, false);
			msg = super.getMessage(request, null, "link.added");
			break;
		}while(true);
		
		LinkForm lform = (LinkForm)form;
		String fromPage = lform.getFromPage();
		
		return msgbox(mapping,form,request,response,msg,fromPage);
	}
	
	/**
	 * 删除友情链接
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_lnk_id)
			throws Exception 
	{
		LinkForm lform = (LinkForm)form;
		try{
			int link_id = Integer.parseInt(s_lnk_id);
			LinkDAO.delete(lform.getSid(), link_id);
		}catch(Exception e){
			context().log("delete link #"+s_lnk_id+" failed.", e);
		}
		return makeForward(mapping.findForward("links"), lform.getSid());
	}
	/**
	 * 向上移动友情链接
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveUp(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_lnk_id)
			throws Exception 
	{
		LinkForm lform = (LinkForm)form;
		try{
			int link_id = Integer.parseInt(s_lnk_id);
			LinkDAO.move(getSiteBean(request), link_id, true);
		}catch(Exception e){
			context().log("move up link #"+s_lnk_id+" failed.", e);
		}
		return makeForward(mapping.findForward("links"), lform.getSid());
	}
	/**
	 * 向下移动友情链接
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveDown(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_lnk_id)
			throws Exception 
	{
		LinkForm lform = (LinkForm)form;
		try{
			int link_id = Integer.parseInt(s_lnk_id);
			LinkDAO.move(getSiteBean(request), link_id, false);
		}catch(Exception e){
			context().log("move down link #"+s_lnk_id+" failed.", e);
		}
		return makeForward(mapping.findForward("links"), lform.getSid());
	}
	/**
	 * 添加友情链接
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doCreateLink(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		LinkForm lform = (LinkForm)form;
		super.validateClientId(request, lform);
		ActionMessages msgs = new ActionMessages();
		if(StringUtils.isEmpty(lform.getTitle())){
			msgs.add("title", new ActionMessage("error.link_title_empty"));
		}
		else if(StringUtils.isEmpty(lform.getUrl())){
			msgs.add("url", new ActionMessage("error.link_url_empty"));
		}
		else{
			LinkBean lbean = new LinkBean();
			lbean.setCreateTime(new Date());
			lbean.setSiteId(lform.getSid());
			lbean.setTitle(lform.getTitle());
			lbean.setUrl(lform.getUrl());
			lbean.setType(lform.getType());
			lbean.setStatus(lform.getStatus());
			try{
				LinkDAO.create(lbean, lform.getId(), (lform.getDirection()==1));
			}catch(Exception e){
				msgs.add("link", new ActionMessage("error.database", e.getMessage()));
			}
		}
		
		if(!msgs.isEmpty())
			return mapping.getInputForward();
		
		return makeForward(mapping.findForward("links"), lform.getSid());
	}
	/**
	 * 更新友情链接
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateLink(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		String msg = null;
		LinkForm lform = (LinkForm)form;
		if(StringUtils.isEmpty(lform.getTitle())){
			msg = super.getMessage(request, null, "error.link_title_empty");
		}
		else if(StringUtils.isEmpty(lform.getUrl())){
			msg = super.getMessage(request, null, "error.link_url_empty");
		}
		else{
			SiteBean site = getSiteBean(request);
			LinkBean lbean = LinkDAO.getLinkByID(lform.getId());
			if(lbean!=null && lbean.getSiteId()==site.getId()){
				lbean.setTitle(lform.getTitle());
				lbean.setUrl(lform.getUrl());
				lbean.setType(lform.getType());
				lbean.setStatus(lform.getStatus());
				try{
					LinkDAO.update(lbean);
				}catch(Exception e){
					msg = super.getMessage(request, null, "error.database", e.getMessage());
				}
			}
		}
		return msgbox(mapping,form,request,response,msg,lform.getFromPage());
	}
}
