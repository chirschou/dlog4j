/*
 *  DLOG_BBS_VelocityTool.java
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
 */
package com.liusoft.dlog4j.velocity;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.TextCacheManager;
import com.liusoft.dlog4j.base._TopicBeanBase;
import com.liusoft.dlog4j.beans.ForumBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.TopicBean;
import com.liusoft.dlog4j.beans.TopicOutlineBean;
import com.liusoft.dlog4j.beans.TopicReplyBean;
import com.liusoft.dlog4j.dao.BBSForumDAO;
import com.liusoft.dlog4j.dao.BBSReplyDAO;
import com.liusoft.dlog4j.dao.BBSTopicDAO;

/**
 * 用在DLOG中的BBS的VelocityTool类
 * 
 * @author Winter Lau
 */
public class DLOG_BBS_VelocityTool{

	private static Log log = LogFactory.getLog(DLOG_BBS_VelocityTool.class);
	
	private static final String FORUMS = "forums";
	
	/**
	 * 列出某个网站对指定用户有效的所有讨论区
	 * @param site
	 * @param user
	 * @return
	 */
	public List forums(HttpServletRequest req, SiteBean site, SessionUserObject user){
		List forums = (List)req.getAttribute(FORUMS);
		if(forums == null && site!=null){
			forums = site.getForums();
			Iterator iter = forums.iterator();
			while(iter.hasNext()){
				ForumBean fbean = (ForumBean)iter.next();
				if (fbean.getStatus() == ForumBean.STATUS_HIDDEN
						&& (user == null || !site.isOwner(user)))
					iter.remove();
			}
			req.setAttribute(FORUMS, forums);
		}
		return forums;
	}
	
	/**
	 * 获取网站的精华帖子数
	 * @param site
	 * @param forum
	 * @return
	 */
	public int elite_count(SiteBean site, ForumBean forum){
		return BBSTopicDAO.getEliteCount(site, forum);
	}
	
	/**
	 * 查询所有的精华帖
	 * @param site
	 * @param forum
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List elite_topics(SiteBean site, ForumBean forum, int page, int pageSize){
		if(pageSize<1 || pageSize>200)
			pageSize = 50;
		int fromIdx = (page-1)*pageSize;
		if(fromIdx < 0)
			fromIdx = 0;
		return BBSTopicDAO.listEliteTopics(site, forum, fromIdx, pageSize);
	}
	
	/**
	 * 获取论坛中所有的帖子数
	 * @param site
	 * @return
	 */
	public int topic_count(SiteBean site){
		if(site==null)
			return -1;
		return BBSTopicDAO.getTopicCount(site.getId());
	}
	
	/**
	 * 获取下一篇帖子
	 * @param forum
	 * @param topic
	 * @return
	 */
	public TopicOutlineBean next_topic(_TopicBeanBase topic){
		if(topic==null)
			return null;
		return BBSTopicDAO.getNextTopic(topic.getForum().getId(), topic.getId(), true);
	}
	
	/**
	 * 获取上一篇帖子
	 * @param forum
	 * @param topic
	 * @return
	 */
	public TopicOutlineBean prev_topic(_TopicBeanBase topic){
		if(topic==null)
			return null;
		return BBSTopicDAO.getNextTopic(topic.getForum().getId(), topic.getId(), false);
	}
	
	/**
	 * 读取某个论坛的详细资料
	 * 
	 * @param site
	 * @param forum_id
	 * @return
	 */
	public ForumBean forum(SiteBean site, int forum_id) {
		if (site == null || forum_id < 1)
			return null;
		ForumBean forum = BBSForumDAO.getForumByID(forum_id);
		if (forum != null && forum.getSite().getId() == site.getId())
			return forum;
		return null;
	}

	/**
	 * 读取某个帖子的详细信息
	 * 
	 * @param site
	 * @param topic_id
	 * @return
	 */
	public _TopicBeanBase topic(SiteBean site, SessionUserObject user, int topic_id) {
		if (site == null || topic_id < 1)
			return null;
		_TopicBeanBase topic;
		String text = TextCacheManager.getTextContent(TopicBean.TYPE_BBS, topic_id);
		if(text==null){
			topic = BBSTopicDAO.getTopicByID(topic_id);
			if(topic!=null && topic.getStatus()==TopicBean.STATUS_NORMAL){
				TextCacheManager.updateTextContent(TopicBean.TYPE_BBS, topic_id, topic.getContent());
			}
		}
		else{
			topic = BBSTopicDAO.getTopicOutlineByID(topic_id);
			if(topic!=null)
				topic.setContent(text);
		}
		if(user!=null && site.isOwner(user))
			return topic;
		if (topic != null && topic.getSite().getId() == site.getId()
				&& topic.getStatus() == TopicBean.STATUS_NORMAL && topic.getForum().getStatus()==0)
			return topic;
		return null;
	}

	/**
	 * 读取某个回帖的详细信息
	 * 
	 * @param site
	 * @param reply_id
	 * @return
	 */
	public TopicReplyBean reply(SiteBean site, int reply_id) {
		if (site == null || reply_id < 1)
			return null;
		TopicReplyBean reply = BBSReplyDAO.getTopicReplyByID(reply_id);
		if (reply != null && reply.getSite().getId() == site.getId()
				&& reply.getStatus() == TopicBean.STATUS_NORMAL)
			return reply;
		return null;
	}
	/**
	 * 列出某个论坛的帖子
	 * 
	 * @param forum
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List topics(SiteBean site, ForumBean forum, int page, int pageSize) {
		if (site==null || forum == null || forum.getSite().getId()!=site.getId())
			return null;
		if (pageSize < 1)
			pageSize = 20;
		int fromIdx = (page - 1) * pageSize;
		if (fromIdx < 0)
			fromIdx = 0;
		return BBSTopicDAO.listTopics(forum.getId(), fromIdx, pageSize);
	}
	
	/**
	 * 列出整个论坛的所有帖子
	 * @param site
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List all_topics(SiteBean site, int page, int pageSize){
		if (pageSize < 1)
			pageSize = 20;
		int fromIdx = (page - 1) * pageSize;
		if (fromIdx < 0)
			fromIdx = 0;
		if (site == null)
			return BBSTopicDAO.listAllTopics(fromIdx, pageSize);
		else
			return BBSTopicDAO.listAllTopics(site.getId(), fromIdx, pageSize);
	}
	
	/**
	 * 分页列出某个帖子的回帖
	 * @param topic
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List replies(_TopicBeanBase topic, int page, int pageSize){
		if(topic == null)
			return null;
		if (pageSize < 1)
			pageSize = 20;
		int fromIdx = (page - 1) * pageSize;
		if (fromIdx < 0)
			fromIdx = 0;
		return BBSReplyDAO.listReplies(topic.getId(), fromIdx, pageSize);
	}

	/**
	 * 列出热门帖子
	 * 
	 * @param forum
	 * @param page
	 * @param pageSize
	 * @param days
	 *            看前几天的热门贴(热门贴有失效性)
	 * @return
	 */
	public List hot_topics(SiteBean site, ForumBean forum, int page, int pageSize, int days) {
		if(site==null)
			return null;
		if (pageSize < 1)
			pageSize = 20;
		int fromIdx = (page - 1) * pageSize;
		if (fromIdx < 0)
			fromIdx = 0;
		return BBSTopicDAO.listHotTopics(site, forum, fromIdx, pageSize, days);
	}
	
	/**
	 * 列出最新的讨论话题
	 * @param page
	 * @param count
	 * @return
	 */
	public List list_new_topics(int page, int count){
		int fromIdx = (page - 1) * count;
		return BBSTopicDAO.listAllTopics(fromIdx, count);
	}

	/**
	 * 访问日记，增加日记的阅读数
	 * @param site
	 * @param user
	 * @param log
	 */
	public void visit_topic(SiteBean site, _TopicBeanBase topic){
		if(topic!=null && topic.getSite().getId()==site.getId()){
			try{
				BBSTopicDAO.incViewCount(topic.getId(), 1);
			}catch(Exception e){
				log.error("visit_topic failed.", e);
			}
		}
	}
}
