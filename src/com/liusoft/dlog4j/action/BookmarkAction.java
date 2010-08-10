/*
 *  BookmarkAction.java
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.beans.BookmarkBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.BookmarkDAO;
import com.liusoft.dlog4j.formbean.BookmarkForm;
import com.liusoft.dlog4j.formbean.FormBean;

/**
 * 书签相关的Action类
 * @author Winter Lau
 */
public class BookmarkAction extends ActionBase {

	/**
	 * 删除书签
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDelete(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String s_bm_id) throws Exception 			
	{
		int bm_id = Integer.parseInt(s_bm_id);
		BookmarkForm bookmark = (BookmarkForm)form;
		UserBean loginUser = super.getLoginUser(request, response);
		if(loginUser!=null){
			try{
				BookmarkDAO.delete(loginUser.getId(), bm_id);
			}catch(Exception e){
				context().log("delete bookmark #"+s_bm_id+" failed.", e);
				throw e;
			}
		}
		return makeForward(mapping.findForward("bookmark"), bookmark.getSid());
	}

	/**
	 * 删除选中书签
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteBookmarks(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception 			
	{
		FormBean fbean = (FormBean) form;
		validateClientId(request, fbean);
		//判断用户是否登录
		SessionUserObject loginUser = super.getLoginUser(request, response, false);
		if(loginUser != null){
			String[] bids = request.getParameterValues("bid");			
			BookmarkDAO.deleteBookmarks(loginUser.getId(), bids);
		}
		return makeForward(mapping.findForward("bookmark"), fbean.getSid());
	}
	
	/**
	 * 存为书签
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	protected ActionForward doAdd(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception 			
	{
		BookmarkForm bookmark = (BookmarkForm)form;
		UserBean loginUser = super.getLoginUser(request, response);
		String msg = null;
		if(loginUser!=null){
			BookmarkBean bean = new BookmarkBean();
			bean.setParentId(bookmark.getParentId());
			bean.setParentType(bookmark.getParentType());
			bean.setOwner(loginUser);
			bean.setSite(new SiteBean(bookmark.getSid()));
			bean.setTitle(bookmark.getTitle());
			try{
				if(BookmarkDAO.save(bean))
					msg = getMessage(request,null,"bookmark.created");
				else
					msg = getMessage(request,null,"error.bookmark.exists");
			}catch(Exception e){
				context().log("add bookmark failed.", e);
				msg = getMessage(request,null,"error.database", e.getMessage());
			}
		}
		else{
			msg = getMessage(request,null,"error.user_not_login");
		}
		return msgbox(mapping, form, request, response, msg, bookmark
				.getFromPage());
	}
	
	protected ActionForward doDefault(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		return doAdd(mapping, form, request, response);
	}
}
