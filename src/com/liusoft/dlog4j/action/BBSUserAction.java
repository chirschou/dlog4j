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

import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.TextCacheManager;
import com.liusoft.dlog4j.base.ClientInfo;
import com.liusoft.dlog4j.beans.ForumBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.TopicBean;
import com.liusoft.dlog4j.beans.TopicOutlineBean;
import com.liusoft.dlog4j.beans.TopicReplyBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.BBSForumDAO;
import com.liusoft.dlog4j.dao.BBSReplyDAO;
import com.liusoft.dlog4j.dao.BBSTopicDAO;
import com.liusoft.dlog4j.formbean.BBSReplyForm;
import com.liusoft.dlog4j.formbean.BBSTopicForm;
import com.liusoft.dlog4j.search.SearchProxy;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * BBS用户操作的Action
 * 
 * @author Winter Lau
 */
public class BBSUserAction extends ActionBase {

	/**
	 * 删除回帖
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteTopic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BBSTopicForm tform = (BBSTopicForm) form;

		while (true) {
			UserBean loginUser = super.getLoginUser(request, response);
			if (loginUser == null)
				break;
			TopicOutlineBean tbean = BBSTopicDAO.getTopicOutlineByID(tform
					.getId());
			if (tbean == null)
				break;
			if (tbean.getUser().getId() != loginUser.getId()
					&& loginUser.getOwnSiteId() != tform.getSid())
				break;
			BBSTopicDAO.delete(tbean);
			TopicBean topic = new TopicBean();
			topic.setId(tbean.getId());
			SearchProxy.remove(topic);
			break;
		}
		StringBuffer ext = new StringBuffer();
		ext.append("fid=");
		ext.append(tform.getForum());
		return makeForward(mapping.findForward("forum"), tform.getSid(), ext
				.toString());
	}

	/**
	 * 回复帖子
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doPublishReply(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BBSReplyForm rform = (BBSReplyForm) form;
		super.validateClientId(request, rform);
		ActionMessages msgs = new ActionMessages();
		while (true) {
			if (StringUtils.isEmpty(rform.getTitle())) {
				msgs.add("title", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			if (StringUtils.isEmpty(rform.getContent())) {
				msgs.add("content", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			UserBean loginUser = super.getLoginUser(request, response);
			if (loginUser == null) {
				msgs.add("reply", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("reply", new ActionMessage("error.user_not_available"));
				break;
			}
			SiteBean site = getSiteByID(rform.getSid());
			if (site == null) {
				msgs.add("reply", new ActionMessage("error.site_not_available"));
				break;
			}
			//检查黑名单
			if(isUserInBlackList(site, loginUser)){
				msgs.add("reply", new ActionMessage("error.user_in_blacklist"));
				break;
			}
			TopicOutlineBean topic = BBSTopicDAO.getTopicOutlineByID(rform.getTid());
			if (topic == null
					|| topic.getStatus() != TopicBean.STATUS_NORMAL
					|| topic.getSite().getId() != site.getId()
					|| !topic.getForum().canCreateOrUpdateTopic(loginUser)) {
				msgs.add("log", new ActionMessage("error.topic_not_available",
						new Integer(rform.getTid())));
				break;
			}
			// 创建TopicBean
			TopicReplyBean reply = new TopicReplyBean();
			reply.setClient(new ClientInfo(request, rform.getClientType()));
			String content = StringUtils.abbreviate(super.autoFiltrate(null,
					rform.getContent()), MAX_REPLY_LENGTH);
			reply.setContent(super.filterScriptAndStyle(content));
			reply.setReplyTime(new Date());
			reply.setSite(site);
			reply.setTitle(super.autoFiltrate(site, rform.getTitle()));
			reply.setTopic(topic);
			reply.setUser(loginUser);
			BBSReplyDAO.create(reply);
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("new_reply");
		}
		StringBuffer ext = new StringBuffer();
		ext.append("fid=");
		ext.append(rform.getFid());
		ext.append("&tid=");
		ext.append(rform.getTid());
		return makeForward(mapping.findForward("topic"), rform.getSid(), ext
				.toString());
	}

	/**
	 * 修改回帖
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doEditReply(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BBSReplyForm rform = (BBSReplyForm) form;
		super.validateClientId(request, rform);
		ActionMessages msgs = new ActionMessages();
		while (true) {
			if (StringUtils.isEmpty(rform.getTitle())) {
				msgs.add("title", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			if (StringUtils.isEmpty(rform.getContent())) {
				msgs.add("content",
						new ActionMessage("error.empty_not_allowed"));
				break;
			}
			UserBean loginUser = super.getLoginUser(request, response);
			if (loginUser == null) {
				msgs.add("reply", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("reply", new ActionMessage("error.user_not_available"));
				break;
			}
			SiteBean site = super.getSiteByID(rform.getSid());
			if (site == null) {
				msgs.add("reply", new ActionMessage("error.site_not_available"));
				break;
			}
			TopicReplyBean rbean = BBSReplyDAO.getTopicReplyByID(rform.getId());
			if (rbean != null
					&& rbean.getStatus() == TopicReplyBean.STATUS_NORMAL) {
				String title = super.autoFiltrate(site, rform.getTitle());
				if (!StringUtils.equals(title, rbean.getTitle()))
					rbean.setTitle(title);
				String content = StringUtils.abbreviate(super.autoFiltrate(
						null, rform.getContent()), MAX_REPLY_LENGTH);
				if (!StringUtils.equals(content, rbean.getContent()))
					rbean.setContent(super.filterScriptAndStyle(content));
				BBSReplyDAO.flush();
			}
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("new_reply");
		}
		StringBuffer ext = new StringBuffer();
		ext.append("fid=");
		ext.append(rform.getFid());
		ext.append("&tid=");
		ext.append(rform.getTid());
		if (rform.getPage() > 1) {
			ext.append("&page=");
			ext.append(rform.getPage());
		}
		return makeForward(mapping.findForward("topic"), rform.getSid(), ext
				.toString());
	}

	/**
	 * 删除回帖
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteReply(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BBSReplyForm rform = (BBSReplyForm) form;

		while (true) {
			UserBean loginUser = super.getLoginUser(request, response);
			if (loginUser == null)
				break;
			TopicReplyBean trb = BBSReplyDAO.getTopicReplyByID(rform.getId());
			if (trb == null)
				break;
			if (trb.getUser().getId() != loginUser.getId()
					&& loginUser.getOwnSiteId() != rform.getSid())
				break;
			BBSReplyDAO.delete(trb);
			break;
		}
		StringBuffer ext = new StringBuffer();
		ext.append("fid=");
		ext.append(rform.getFid());
		ext.append("&tid=");
		ext.append(rform.getTid());
		ext.append("&page=");
		ext.append(rform.getPage());
		return makeForward(mapping.findForward("topic"), rform.getSid(), ext
				.toString());
	}

	/**
	 * 发表帖子
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doPublishTopic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BBSTopicForm log = (BBSTopicForm) form;
		super.validateClientId(request, log);
		ActionMessages msgs = new ActionMessages();
		UserBean loginUser = super.getLoginUser(request, response);
		while (true) {
			if (loginUser == null) {
				msgs.add("topic", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("topic", new ActionMessage("error.user_not_available"));
				break;
			}
			if (StringUtils.isEmpty(log.getTitle())) {
				msgs.add("title", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			if (StringUtils.isEmpty(log.getContent())) {
				msgs.add("content",
						new ActionMessage("error.empty_not_allowed"));
				break;
			}
			SiteBean site = super.getSiteByID(log.getSid());
			if (site == null) {
				msgs.add("topic", new ActionMessage("error.site_not_available"));
				break;
			}
			//检查黑名单
			if(isUserInBlackList(site, loginUser)){
				msgs.add("topic", new ActionMessage("error.user_in_blacklist"));
				break;
			}
			ForumBean forum = BBSForumDAO.getForumByID(log.getForum());
			if (forum == null || forum.getSite().getId() != site.getId()
					|| !forum.canCreateOrUpdateTopic(loginUser)) {
				msgs.add("topic", new ActionMessage("error.forum_not_available",
						new Integer(log.getForum())));
				break;
			}			
			
			// 创建TopicBean
			TopicBean topic = new TopicBean();
			topic.setUser(loginUser);
			topic.setUsername(loginUser.getName());
			topic.setSite(site);
			// 对发贴的标题以及内容自动进行敏感字词过滤
			topic.setTitle(super.autoFiltrate(site, log.getTitle()));
			String content = StringUtils.abbreviate(super.autoFiltrate(null,
					log.getContent()), MAX_TOPIC_LENGTH);
			topic.setContent(super.filterScriptAndStyle(content));
			// FIXME: 处理当关键字太长导致数据库写入失败的问题
			topic.setKeyword(DLOGSecurityManager.IllegalGlossary
					.deleteIllegalWord(log.getSearchKey()));
			topic.setClient(new ClientInfo(request, log.getClientType()));
			topic.setCreateTime(new Date());
			topic.setForum(forum);
			topic.setStatus(TopicBean.STATUS_NORMAL);
			if(site.getOwner().getId()==loginUser.getId()){
				if (log.getTop() == 1)
					topic.setTop(true);
				if (log.getElite() == 1)
					topic.setElite(true);
			}
			BBSTopicDAO.create(topic, (log.getBookmark() == 1));

			// 检索上传的信息
			pickupUploadFileItems(request, response, loginUser.getId(), site, topic
					.getId(), TopicBean.TYPE_BBS);
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("new_topic");
		}
		return makeForward(mapping.findForward("forum"), log.getSid(), "fid",
				log.getForum());
	}

	/**
	 * 修改帖子
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doEditTopic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BBSTopicForm log = (BBSTopicForm) form;
		super.validateClientId(request, log);
		ActionMessages msgs = new ActionMessages();
		UserBean loginUser = super.getLoginUser(request, response);
		while (true) {
			if (loginUser == null) {
				msgs.add("log", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("log", new ActionMessage("error.user_not_available"));
				break;
			}
			if (StringUtils.isEmpty(log.getTitle())) {
				msgs.add("title", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			if (StringUtils.isEmpty(log.getContent())) {
				msgs.add("content",
						new ActionMessage("error.empty_not_allowed"));
				break;
			}
			// 检查用户的权限
			TopicBean topic = BBSTopicDAO.getTopicByID(log.getId());
			if (topic != null) {
				if (topic.getUser().getId() != loginUser.getId()
						&& loginUser.getOwnSiteId() != log.getSid()) {
					msgs.add("bbs", new ActionMessage("error.access_deny"));
					break;
				}
				// 对发贴的标题以及内容自动进行敏感字词过滤
				String title = super.autoFiltrate(topic.getSite(),log.getTitle());
				if (!StringUtils.equals(title, topic.getTitle()))
					topic.setTitle(title);
				String content = StringUtils.abbreviate(super.autoFiltrate(
						null, log.getContent()), MAX_TOPIC_LENGTH);				
				if (!StringUtils.equals(content, topic.getContent())){
					topic.setContent(super.filterScriptAndStyle(content));
					//更新文本缓存(Winter Lau, 2006-5-12)
					TextCacheManager.updateTextContent(
							TopicBean.TYPE_BBS, topic.getId(), topic.getContent());
				}
				String keyword = super.autoFiltrate(topic.getSite(),log.getSearchKey());
				boolean updateTags = false;
				if (!StringUtils.equals(keyword, topic.getKeyword())) {
					topic.setKeyword(keyword);
					updateTags = true;
				}
				topic.setModifyTime(new Date());
				if(topic.getSite().getOwner().getId()==loginUser.getId()){
					topic.setTop(log.getTop() == 1);
					topic.setElite(log.getElite() == 1);
				}
				BBSTopicDAO.update(topic, updateTags);
			}
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edit_topic");
		}

		StringBuffer ext = new StringBuffer("fid=");
		ext.append(log.getForum());
		ext.append("&tid=");
		ext.append(log.getId());
		return makeForward(mapping.findForward("topic"), log.getSid(), ext
				.toString());

	}
}
