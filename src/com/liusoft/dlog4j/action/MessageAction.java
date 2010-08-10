/*
 *  MessageAction.java
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

import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.beans.MessageBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.MessageDAO;
import com.liusoft.dlog4j.dao.UserDAO;
import com.liusoft.dlog4j.formbean.MessageForm;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 留言相关的Action类
 * @author Winter Lau
 */
public class MessageAction extends ActionBase {

	/**
	 * 删除所有已读留言
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteAll(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String s_status) throws Exception 			
	{
		MessageForm msg = (MessageForm)form;
		UserBean loginUser = super.getLoginUser(request, response);
		if(loginUser!=null){
			try{
				int status = Integer.parseInt(s_status);
				MessageDAO.deleteMsgs(loginUser.getId(),status);
			}catch(Exception e){
				context().log("delete message where status is "+s_status+" failed.", e);
			}
		}
		return makeForward(mapping.findForward("msgs"), msg.getSid());
	}
	
	/**
	 * 删除留言
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDelete(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String s_msg_id) throws Exception 			
	{
		MessageForm msg = (MessageForm)form;
		UserBean loginUser = super.getLoginUser(request, response);
		if(loginUser!=null){
			try{
				int msg_id = Integer.parseInt(s_msg_id);
				MessageDAO.deleteMsg(loginUser.getId(), msg_id);
			}catch(Exception e){
				context().log("delete message #"+s_msg_id+" failed.", e);
			}
		}
		String ext = null;
		int page = RequestUtils.getParam(request, "p", -1);
		if(page > 1){
			ext = "p="+page;
		}
		return makeForward(mapping.findForward("msgs"), msg.getSid(), ext);
	}

	/**
	 * 删除选中的留言
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteMessages(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception 			
	{
		MessageForm msg = (MessageForm)form;
		UserBean loginUser = super.getLoginUser(request, response);
		if(loginUser!=null){
			String[] mids = request.getParameterValues("mid");
			MessageDAO.deleteMsgs(loginUser.getId(), mids);
		}
		return makeForward(mapping.findForward("msgs"), msg.getSid());
	}

	/**
	 * 发送留言
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doSendMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		MessageForm msgform = (MessageForm)form;
		super.validateClientId(request, msgform);
		ActionMessages msgs = new ActionMessages();
		while(true){
			if(msgform.getExpiredTime()!=null && msgform.getExpiredTime().before(new Date())){
				msgs.add("message", new ActionMessage("error.expired_time_not_available"));
				break;
			}
			if(StringUtils.isEmpty(msgform.getContent())){
				msgs.add("content", new ActionMessage("error.empty_content"));
				break;
			}
			if(msgform.getReceiverId()==0 || msgform.getSid()==0){
				msgs.add("message", new ActionMessage("error.param"));
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
			else if(DLOGSecurityManager.IllegalGlossary.existIllegalWord(msgform.getContent())){
				msgs.add("message", new ActionMessage("error.illegal_glossary"));
				break;
			}
			UserBean receiver = UserDAO.getUserByID(msgform.getReceiverId());
			if(receiver==null || receiver.getStatus()!=UserBean.STATUS_NORMAL){
				msgs.add("message", new ActionMessage("error.user_not_available"));
				break;
			}
			//判断接受者是否已经将发送者加为黑名单
			if(UserDAO.isUserInBlackList(receiver.getId(), loginUser.getId())){
				msgs.add("message", new ActionMessage("message.sent"));
				break;
			}
			MessageBean msgbean = new MessageBean();
			String content = StringUtils.abbreviate(super.autoFiltrate(null,
					msgform.getContent()), MAX_MESSAGE_LENGTH);
			msgbean.setContent(super.filterScriptAndStyle(content));
			msgbean.setExpiredTime(msgform.getExpiredTime());
			msgbean.setFromUser(loginUser);
			msgbean.setToUser(receiver);
			msgbean.setStatus(MessageBean.STATUS_NEW);
			msgbean.setSendTime(new Date());
			try{
				MessageDAO.save(msgbean);
				msgs.add("message", new ActionMessage("message.sent"));
			}catch(HibernateException e){
				context().log("undelete diary failed.", e);
				msgs.add("message", new ActionMessage("error.database", e.getMessage()));
			}
			break;
		}
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
		}
		
		return mapping.findForward("send");
	}
	
	/**
	 * 回复留言并删除所回复的信息
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doReplyMsgAndDeleteOld(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		MessageForm msgform = (MessageForm)form;
		super.validateClientId(request, msgform);
		ActionMessages msgs = new ActionMessages();
		while(true){
			if(msgform.getExpiredTime()!=null && msgform.getExpiredTime().before(new Date())){
				msgs.add("message", new ActionMessage("error.expired_time_not_available"));
				break;
			}
			if(StringUtils.isEmpty(msgform.getContent())){
				msgs.add("content", new ActionMessage("error.empty_content"));
				break;
			}
			if(msgform.getReceiverId()==0 || msgform.getSid()==0){
				msgs.add("message", new ActionMessage("error.param"));
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
			else if(DLOGSecurityManager.IllegalGlossary.existIllegalWord(msgform.getContent())){
				msgs.add("message", new ActionMessage("error.illegal_glossary"));
				break;
			}
			UserBean receiver = UserDAO.getUserByID(msgform.getReceiverId());
			if(receiver==null || receiver.getStatus()!=UserBean.STATUS_NORMAL){
				msgs.add("message", new ActionMessage("error.user_not_available"));
				break;
			}
			//判断接受者是否已经将发送者加为黑名单
			if(UserDAO.isUserInBlackList(receiver.getId(), loginUser.getId())){
				msgs.add("message", new ActionMessage("message.sent"));
				break;
			}
			MessageBean msgbean = new MessageBean();
			String content = StringUtils.abbreviate(super.autoFiltrate(null,
					msgform.getContent()), MAX_MESSAGE_LENGTH);
			msgbean.setContent(super.filterScriptAndStyle(content));
			msgbean.setExpiredTime(msgform.getExpiredTime());
			msgbean.setFromUser(loginUser);
			msgbean.setToUser(receiver);
			msgbean.setStatus(MessageBean.STATUS_NEW);
			msgbean.setSendTime(new Date());
			try{
				MessageDAO.replyAndDeleteMessage(msgform.getMsgID(), msgbean);
				msgs.add("message", new ActionMessage("message.sent"));
			}catch(HibernateException e){
				context().log("undelete diary failed.", e);
				msgs.add("message", new ActionMessage("error.database", e.getMessage()));
			}
			break;
		}
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
		}
		
		return mapping.findForward("send");
	}
}
