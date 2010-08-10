/*
 *  BBSReplyDAO.java
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
package com.liusoft.dlog4j.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.liusoft.dlog4j.beans.ForumBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.TypeBean;
import com.liusoft.dlog4j.dao.BBSForumDAO;
import com.liusoft.dlog4j.formbean.BBSForumForm;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * BBS管理的Action
 * @author Winter Lau
 */
public class BBSAdminAction extends AdminActionBase {

	/**
	 * 锁定论坛
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doLockForum(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_forum_id)
			throws Exception {
		BBSForumForm forum = (BBSForumForm)form;
		int forum_id = Integer.parseInt(s_forum_id);
		BBSForumDAO.lockForumByID(forum.getSid(), forum_id);
		return super.makeForward(mapping.findForward("forums"), forum.getSid());
	}
	
	/**
	 * 隐藏论坛
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doHideForum(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_forum_id)
			throws Exception {
		BBSForumForm forum = (BBSForumForm)form;
		int forum_id = Integer.parseInt(s_forum_id);
		BBSForumDAO.hideForumByID(forum.getSid(), forum_id);
		return super.makeForward(mapping.findForward("forums"), forum.getSid());
	}
	
	/**
	 * 删除论坛
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteForum(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_forum_id)
			throws Exception {
		BBSForumForm forum = (BBSForumForm)form;
		int forum_id = Integer.parseInt(s_forum_id);
		BBSForumDAO.deleteForumByID(forum.getSid(), forum_id);
		return super.makeForward(mapping.findForward("forums"), forum.getSid());
	}
	
	/**
	 * 更新论坛
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateForum(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BBSForumForm forum = (BBSForumForm)form;
		super.validateClientId(request, forum);
		ForumBean fbean = BBSForumDAO.getForumByID(forum.getId());
		if(fbean!=null){
			if(StringUtils.isNotEmpty(forum.getName())&&!StringUtils.equals(fbean.getName(),forum.getName())){
				fbean.setName(forum.getName());
			}
			if(StringUtils.isNotEmpty(forum.getDesc())&&!StringUtils.equals(fbean.getDesc(),forum.getDesc())){
				fbean.setDesc(forum.getDesc());
			}
			if(fbean.getStatus()!=forum.getStatus()){
				int s = forum.getStatus();
				if(s==ForumBean.STATUS_NORMAL||s==ForumBean.STATUS_HIDDEN||s==ForumBean.STATUS_LOCKED)
					fbean.setStatus(forum.getStatus());
			}
			//更新内容类别
			if(forum.getCatalog()>0){
				if(fbean.getCatalog()==null)
					fbean.setCatalog(new TypeBean(forum.getCatalog()));
				else if(fbean.getCatalog().getId()!=forum.getCatalog())
					fbean.setCatalog(new TypeBean(forum.getCatalog()));						
			}
			else if(fbean.getCatalog()!=null){
				fbean.setCatalog(null);
			}
			
			BBSForumDAO.flush();
		}
		return super.makeForward(mapping.findForward("forums"), forum.getSid());
	}
	
	/**
	 * 创建论坛
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doCreateForum(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BBSForumForm forum = (BBSForumForm)form;
		ActionMessages msgs = new ActionMessages();
		if(StringUtils.isEmpty(forum.getName())){
			msgs.add("name", new ActionMessage("error.forum_name_empty"));
		}
		else{			
			super.validateClientId(request, forum);
			SiteBean site = super.getSiteBean(request);
			ForumBean fbean = new ForumBean();
			fbean.setName(forum.getName());
			if(!StringUtils.isEmpty(forum.getDesc()))
				fbean.setDesc(forum.getDesc());
			fbean.setCreateTime(new Date());
			fbean.setSite(site);
			int s = forum.getStatus();
			if(s==ForumBean.STATUS_NORMAL||s==ForumBean.STATUS_HIDDEN||s==ForumBean.STATUS_LOCKED)
				fbean.setStatus(forum.getStatus());
			else
				fbean.setStatus(ForumBean.STATUS_NORMAL);
			if(forum.getCatalog()>0){
				fbean.setCatalog(new TypeBean(forum.getCatalog()));
			}
			BBSForumDAO.createForum(fbean, forum.getId(), forum.getDirection()==1);
		}
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			return mapping.findForward("forum_add");
		}
		return super.makeForward(mapping.findForward("forums"), forum.getSid());
	}
	
	/**
	 * 向上移动日记分类
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveUp(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_forum_id)
			throws Exception 
	{
		BBSForumForm lform = (BBSForumForm)form;
		try{
			int forum_id = Integer.parseInt(s_forum_id);
			BBSForumDAO.move(getSiteBean(request), forum_id, true);
		}catch(Exception e){
			context().log("move up forum #"+s_forum_id+" failed.", e);
		}
		return makeForward(mapping.findForward("forums"), lform.getSid());
	}
	
	/**
	 * 向下移动日记分类
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveDown(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_forum_id)
			throws Exception
	{
		BBSForumForm lform = (BBSForumForm)form;
		try{
			int forum_id = Integer.parseInt(s_forum_id);
			BBSForumDAO.move(getSiteBean(request), forum_id, false);
		}catch(Exception e){
			context().log("move up forum #"+s_forum_id+" failed.", e);
		}
		return makeForward(mapping.findForward("forums"), lform.getSid());
	}
	
}
