/*
 *  EditSiteAction.java
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

import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.base.FunctionStatus;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.dao.SiteDAO;
import com.liusoft.dlog4j.formbean.SiteForm;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 站长修改网站信息的操作
 * @author Winter Lau
 */
public class EditSiteAction extends AdminActionBase {

	/**
	 * 修改网站的中文名
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only mgr/modify_name.vm
	 */
	protected ActionForward doUpdateFriendlyName(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SiteForm sform = (SiteForm) form;
		super.validateClientId(request, sform);
		ActionMessages msgs = new ActionMessages();
		
		while (StringUtils.isNotEmpty(sform.getFriendlyName())) {
			SiteBean site = super.getSiteBean(request);
			String fn = super.autoFiltrate(site, sform.getFriendlyName());
			if(!StringUtils.equals(site.getFriendlyName(), fn)){
				//检查网站名是否已经存在 
				if(SiteDAO.getSiteByFriendlyName(fn) != null){
					msgs.add("friendlyName", new ActionMessage("error.site_friendlyName_exists"));
					break;
				}
				try {
					site.setFriendlyName(fn);
					SiteDAO.flush();
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e.getMessage()));
				}
			}
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("modify_name");
		}

		return makeForward(mapping.findForward("editsite"), sform.getSid());
	}

	/**
	 * 修改网站的宣言
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only mgr/modify_detail.vm
	 */
	protected ActionForward doUpdateDetail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SiteForm sform = (SiteForm) form;
		super.validateClientId(request, sform);
		ActionMessages msgs = new ActionMessages();
		
		do{
			SiteBean site = super.getSiteBean(request);
			String detail = super.autoFiltrate(site, StringUtils.extractText(sform.getDetail()));
			if(!StringUtils.equals(site.getDetail(), detail)){
				try {
					site.setDetail(detail);
					SiteDAO.flush();
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e.getMessage()));
				}
			}
			break;
		}while(true);

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("modify_detail");
		}

		return makeForward(mapping.findForward("editsite"), sform.getSid());
	}

	/**
	 * 修改网站的标题
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only mgr/modify_title.vm
	 */
	protected ActionForward doUpdateTitle(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SiteForm sform = (SiteForm) form;
		super.validateClientId(request, sform);
		ActionMessages msgs = new ActionMessages();
		
		do{
			SiteBean site = super.getSiteBean(request);
			String title = super.autoFiltrate(site, sform.getTitle());
			if(!StringUtils.equals(site.getTitle(), title)){
				try {
					site.setTitle(title);
					SiteDAO.flush();
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e.getMessage()));
				}
			}
			break;
		}while(true);

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("modify_title");
		}

		return makeForward(mapping.findForward("editsite"), sform.getSid());
	}

	/**
	 * 更新网站基本资料
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateSite(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		SiteForm f_site = (SiteForm)form;
		super.validateClientId(request, f_site);
		ActionMessages msgs = new ActionMessages();
		//验证输入表单
		String friendlyName = super.autoFiltrate(null, f_site.getFriendlyName());
		do{
			if(StringUtils.isEmpty(friendlyName)){
				msgs.add("friendlyName", new ActionMessage("error.site_friendlyName_empty"));
				break;
			}
			//判断网站名是否存在
			SiteBean site = super.getSiteBean(request);
			if (!StringUtils.equals(site.getFriendlyName(), friendlyName)
					&& SiteDAO.getSiteByFriendlyName(friendlyName) != null) {				
				msgs.add("friendlyName", new ActionMessage("error.site_friendlyName_exists"));
				break;
			}
			if(site!=null){
				if(StringUtils.isNotEmpty(f_site.getDetail())){
					String dt = super.autoFiltrate(site, StringUtils.extractText(f_site.getDetail()));
					site.setDetail(StringUtils.abbreviate(dt,250));
				}
				else
					site.setDetail(null);
				
				if(!StringUtils.equals(site.getFriendlyName(), friendlyName))
					site.setFriendlyName(friendlyName);
				
				if(StringUtils.isNotEmpty(f_site.getIcpNumber())){
					site.setIcpNumber(DLOGSecurityManager.IllegalGlossary
							.deleteIllegalWord(f_site.getIcpNumber()));
				}
				else
					site.setIcpNumber(null);
				site.setLastTime(new Date());
				if(StringUtils.isNotEmpty(f_site.getTitle()))
					site.setTitle(super.autoFiltrate(site, f_site.getTitle()));
				else
					site.setTitle(site.getFriendlyName());
				
				if(StringUtils.isNotEmpty(f_site.getUrl()))
					if(f_site.getUrl().toLowerCase().startsWith("http://"))
						site.setUrl(f_site.getUrl().substring(7));
					else
						site.setUrl(f_site.getUrl());
				else
					site.setUrl(null);
				
				try{
					SiteDAO.updateSite(site);
					msgs.add("site", new ActionMessage("site.updated"));
				}catch(Exception e){
					context().log("Update site failed.", e);
					msgs.add("site", new ActionMessage("error.database", e.getMessage()));
				}
			}
			break;
		}while(true);
		
		if(!msgs.isEmpty())
			saveMessages(request, msgs);
		
		return mapping.getInputForward();
	}

	/**
	 * 更新网站排版样式以及LOGO
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateStyle(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		SiteForm f_site = (SiteForm)form;
		super.validateClientId(request, f_site);
		ActionMessages msgs = new ActionMessages();
		
		SiteBean site = super.getSiteBean(request);
		if(site!=null){
			if(StringUtils.isNotEmpty(f_site.getLayoutFile()) && 
					!StringUtils.equals(f_site.getLayoutFile(), site.getStyle().getLayout()))
				site.getStyle().setLayout(f_site.getLayoutFile());
			else
				site.getStyle().setLayout("1");
			
			if(StringUtils.isNotEmpty(f_site.getCssFile()) && 
					!StringUtils.equals(f_site.getCssFile(), site.getStyle().getCss()))
				site.getStyle().setCss(f_site.getCssFile());
			else
				site.getStyle().setCss("main.css");
			
			//TODO: 处理LOGO
			
			site.setLastTime(new Date());
			try{
				SiteDAO.updateSite(site);
			}catch(Exception e){
				context().log("Update site's style failed.", e);
				msgs.add("site", new ActionMessage("error.database", e.getMessage()));
			}
		}
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			return mapping.findForward("style-setting");
		}
		
		return makeForward(mapping.findForward("settings"), f_site.getSid());
	}
	

	/**
	 * 更新网站的功能
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateFuncs(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		SiteForm f_site = (SiteForm)form;
		super.validateClientId(request, f_site);
		ActionMessages msgs = new ActionMessages();
		
		SiteBean site = super.getSiteBean(request);
		if(site!=null){
			//TODO: 控制长度
			// Diary
			if (StringUtils.isEmpty(f_site.getDiaryName()))
				site.setDiary(null);
			else
				site.setDiary(super.autoFiltrate(site, f_site.getDiaryName()));
			site
					.getFunctionStatus()
					.setDiary(
							(f_site.getStatusDiary() == 1) ? FunctionStatus.STATUS_NORMAL
									: FunctionStatus.STATUS_CLOSED);
			// Photo
			if (StringUtils.isEmpty(f_site.getPhotoName()))
				site.setPhoto(null);
			else
				site.setPhoto(super.autoFiltrate(site, f_site.getPhotoName()));
			site
					.getFunctionStatus()
					.setPhoto(
							(f_site.getStatusPhoto() == 1) ? FunctionStatus.STATUS_NORMAL
									: FunctionStatus.STATUS_CLOSED);
			// Music
			if (StringUtils.isEmpty(f_site.getMusicName()))
				site.setMusic(null);
			else
				site.setMusic(super.autoFiltrate(site, f_site.getMusicName()));
			site
					.getFunctionStatus()
					.setMusic(
							(f_site.getStatusMusic() == 1) ? FunctionStatus.STATUS_NORMAL
									: FunctionStatus.STATUS_CLOSED);
			// BBS
			if (StringUtils.isEmpty(f_site.getForumName()))
				site.setForum(null);
			else
				site.setForum(super.autoFiltrate(site, f_site.getForumName()));
			site
					.getFunctionStatus()
					.setForum(
							(f_site.getStatusForum() == 1) ? FunctionStatus.STATUS_NORMAL
									: FunctionStatus.STATUS_CLOSED);
			// Guestbook
			if (StringUtils.isEmpty(f_site.getGuestbookName()))
				site.setGuestbook(null);
			else
				site.setGuestbook(super.autoFiltrate(site, f_site
						.getGuestbookName()));
			site
					.getFunctionStatus()
					.setGuestbook(
							(f_site.getStatusGuestbook() == 1) ? FunctionStatus.STATUS_NORMAL
									: FunctionStatus.STATUS_CLOSED);
			
			site.setLastTime(new Date());
			try{
				SiteDAO.updateSite(site);
			}catch(Exception e){
				context().log("Update site's functions failed.", e);
				msgs.add("site", new ActionMessage("error.database", e.getMessage()));
			}
		}
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			return mapping.findForward("funcs-setting");
		}
		
		return makeForward(mapping.findForward("settings"), f_site.getSid());
	}
}
