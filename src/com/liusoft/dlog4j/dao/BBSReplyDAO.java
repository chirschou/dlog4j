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
package com.liusoft.dlog4j.dao;

import java.util.Date;
import java.util.List;

import com.liusoft.dlog4j.beans.TopicReplyBean;
import com.liusoft.dlog4j.search.SearchDataProvider;

/**
 * 论坛评论的数据库访问接口
 * 
 * @author Winter Lau
 */
public class BBSReplyDAO extends DAO implements SearchDataProvider {

	/**
	 * 返回指定站点的论坛评论总数
	 * @param site
	 * @return
	 */
	public static int getReplyCount(int site){
		String hql = "SELECT COUNT(*) FROM TopicReplyBean AS d WHERE d.status=?";
		if(site>0){
			hql += " AND d.site.id=?";
			return executeStatAsInt(hql, TopicReplyBean.STATUS_NORMAL, site);
		}
		return executeStat(hql, TopicReplyBean.STATUS_NORMAL).intValue();
	}
	/**
	 * 分页列出某个帖子的所有回帖
	 * @param topic_id
	 * @param fromIdx
	 * @param pageSize
	 * @return
	 */
	public static List listReplies(int topic_id, int fromIdx, int pageSize){
		return executeNamedQuery("LIST_TOPIC_REPLIES",fromIdx,pageSize,topic_id,TopicReplyBean.STATUS_NORMAL);
	}
	
	/**
	 * 根据评论的编号获取评论详细信息
	 * 
	 * @param reply_id
	 * @return
	 */
	public static TopicReplyBean getTopicReplyByID(int reply_id) {
		if(reply_id <= 0)
			return null;
		return (TopicReplyBean)getBean(TopicReplyBean.class, reply_id);
	}

	/**
	 * 创建新评论
	 * 
	 * @param t_reply
	 */
	public static void create(TopicReplyBean t_reply) {
		if (t_reply.getReplyTime() == null)
			t_reply.setReplyTime(new Date());
		t_reply.getTopic().incReplyCount(1);
		t_reply.getTopic().setLastReply(t_reply);
		t_reply.getTopic().setLastReplyTime(new Date());
		t_reply.getTopic().setLastUser(t_reply.getUser());
		t_reply.getTopic().setLastUsername(t_reply.getUser().getName());
		
		t_reply.getUser().getCount().incTopicReplyCount(1);
		save(t_reply);
	}

	/**
	 * 删除评论
	 * 
	 * @param reply_id
	 */
	public static void delete(TopicReplyBean rpl) {
		if(rpl == null)
			return;
		rpl.getTopic().incReplyCount(-1);
		rpl.getUser().getCount().incTopicReplyCount(-1);
		delete(rpl);
	}

	/**
	 * @see com.liusoft.dlog4j.search.SearchDataProvider#fetchAfter(Date)
	 */
	public List fetchAfter(Date beginTime) throws Exception {
		return findNamedAll("LIST_TOPIC_REPLIES_AFTER", beginTime, TopicReplyBean.STATUS_NORMAL);
	}

}
