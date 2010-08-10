/*
 *  BBSTopicDAO.java
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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.liusoft.dlog4j.base._BeanBase;
import com.liusoft.dlog4j.beans.BookmarkBean;
import com.liusoft.dlog4j.beans.DiaryBean;
import com.liusoft.dlog4j.beans.ForumBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.TagBean;
import com.liusoft.dlog4j.beans.TopicBean;
import com.liusoft.dlog4j.beans.TopicOutlineBean;
import com.liusoft.dlog4j.beans.TopicReplyBean;
import com.liusoft.dlog4j.search.SearchDataProvider;

/**
 * 论坛帖子的数据库访问接口
 * 
 * @author Winter Lau
 */
public class BBSTopicDAO extends DAO implements SearchDataProvider {

	/**
	 * 获取网站的精华帖子数
	 * @param site
	 * @param fbean
	 * @return
	 */
	public static int getEliteCount(SiteBean site, ForumBean fbean){
		StringBuffer hql = new StringBuffer("SELECT COUNT(*) FROM TopicBean AS t WHERE t.status=:status");
		if(site!=null)
			hql.append(" AND t.site.id=:site");
		if(fbean!=null)
			hql.append(" AND t.forum.id=:forum");
		hql.append(" AND (t.type=:elite OR t.type=:top_elite)");
		Session ssn = getSession();
		Query q = ssn.createQuery(hql.toString());
		q.setInteger("status", TopicBean.STATUS_NORMAL);
		q.setInteger("elite", TopicBean.INFO_TYPE_ELITE);
		q.setInteger("top_elite", TopicBean.INFO_TYPE_TOP_ELITE);
		if(site!=null)
			q.setInteger("site", site.getId());
		if(fbean!=null)
			q.setInteger("forum", fbean.getId());		
		return ((Number)q.uniqueResult()).intValue();
	}
	
	/**
	 * 列出精华帖
	 * @param site
	 * @param fbean
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listEliteTopics(SiteBean site, ForumBean fbean, int fromIdx, int count){
		StringBuffer hql = new StringBuffer("FROM TopicOutlineBean AS t WHERE t.status=:status");
		if(site!=null)
			hql.append(" AND t.site.id=:site");
		if(fbean!=null)
			hql.append(" AND t.forum.id=:forum");
		hql.append(" AND (t.type=:elite OR t.type=:top_elite) ORDER BY ROUND(t.type / 16, 0) DESC, t.id DESC");
		Session ssn = getSession();
		Query q = ssn.createQuery(hql.toString());
		q.setInteger("status", TopicBean.STATUS_NORMAL);
		q.setInteger("elite", TopicBean.INFO_TYPE_ELITE);
		q.setInteger("top_elite", TopicBean.INFO_TYPE_TOP_ELITE);
		if(site!=null)
			q.setInteger("site", site.getId());
		if(fbean!=null)
			q.setInteger("forum", fbean.getId());
		if(fromIdx > 0)
			q.setFirstResult(fromIdx);
		if(count > 0)
			q.setMaxResults(count);
		return q.list();
	}
	
	/**
	 * 获取下一篇帖子
	 * @param forum_id
	 * @param current_topic_id
	 * @param forward 向前或者向后
	 * @return
	 */
	public static TopicOutlineBean getNextTopic(int forum_id, int current_topic_id, boolean forward){
		return (TopicOutlineBean)namedUniqueResult(forward?"NEXT_TOPIC":"LAST_TOPIC", forum_id, current_topic_id, TopicOutlineBean.STATUS_NORMAL);		
	}

	/**
	 * 分页浏览整个系统的热门帖子
	 * 帖子按照回帖数
	 * @param forum_id
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listHotTopics(int fromIdx, int count, int days){
		Calendar cur_time = Calendar.getInstance();
		cur_time.add(Calendar.DATE, -days);
		return executeNamedQuery("HOT_TOPICS", fromIdx,count, TopicOutlineBean.STATUS_NORMAL, cur_time.getTime());		
	}

	/**
	 * 分页浏览某个论坛的热门帖子
	 * 帖子按照回帖数
	 * @param forum_id
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listHotTopics(SiteBean site, ForumBean forum, int fromIdx, int count, int days){
		StringBuffer hql = new StringBuffer("FROM TopicOutlineBean AS t WHERE t.site.id=? AND t.status=? AND t.createTime >= ? AND t.replyCount > 0");
		if(forum != null)
			hql.append(" AND t.forum.id=?");
		hql.append(" ORDER BY ROUND(t.type / 16, 0) DESC, t.replyCount DESC, t.id DESC");
		Session ssn = getSession();
		try{
			Query q = ssn.createQuery(hql.toString());
			q.setInteger(0, site.getId());
			q.setInteger(1, TopicOutlineBean.STATUS_NORMAL);
			Calendar cur_time = Calendar.getInstance();
			cur_time.add(Calendar.DATE, -days);
			q.setTimestamp(2, new Timestamp(cur_time.getTime().getTime()));
			if(forum != null)
				q.setInteger(3, forum.getId());
			if(fromIdx > 0)
				q.setFirstResult(fromIdx);
			q.setMaxResults(count);
			return q.list();
		}finally{
			hql = null;
		}
	}

	/**
	 * 返回某个站点论坛中的帖子数
	 * @param site
	 * @return
	 */
	public static int getTopicCount(int site){
		return executeNamedStatAsInt("TOPIC_COUNT_OF_SITE", TopicBean.STATUS_NORMAL, site);
	}
	
	/**
	 * 分页浏览某个论坛的帖子
	 * 帖子按照置顶状态/最近评论时间/帖子创建时间进行排序
	 * @param forum_id
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listTopics(int forum_id, int fromIdx, int count){
		return executeNamedQuery("LIST_TOPICS", fromIdx, count, forum_id, TopicOutlineBean.STATUS_NORMAL);
	}
	
	/**
	 * 列出整个论坛的所有帖子
	 * @param site_id
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listAllTopics(int site_id, int fromIdx, int count){
		return executeNamedQuery("LIST_ALL_TOPICS", fromIdx, count, site_id, TopicOutlineBean.STATUS_NORMAL);
	}

	/**
	 * 列出所有论坛的所有帖子
	 * @param site_id
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listAllTopics(int fromIdx, int count){
		return executeNamedQuery("LIST_ALL_TOPICS2", fromIdx, count, TopicOutlineBean.STATUS_NORMAL);
	}

	/**
	 * 根据帖子的编号获取帖子详细信息
	 * 
	 * @param topic_id
	 * @return
	 */
	public static TopicBean getTopicByID(int topic_id) {
		if(topic_id <= 0)
			return null;
		return (TopicBean)getBean(TopicBean.class, topic_id);
	}

	/**
	 * 根据帖子的编号获取帖子概要信息
	 * 
	 * @param topic_id
	 * @return
	 */
	public static TopicOutlineBean getTopicOutlineByID(int topic_id) {
		if(topic_id <= 0)
			return null;
		return (TopicOutlineBean)getBean(TopicOutlineBean.class, topic_id);
	}

	/**
	 * 创建新帖子
	 * 
	 * @param topic
	 * @param add_bookmark
	 */
	public static void create(TopicBean topic, boolean add_bookmark) {
		try {
			if (topic.getCreateTime() == null)
				topic.setCreateTime(new Date());
			Session ssn = getSession();
			beginTransaction();
			topic.getUser().getCount().incTopicCount(1);
			topic.getForum().incTopicCount(1);
			topic.getForum().setLastPostTime(new Date());
			topic.getForum().setLastUser(topic.getUser());
			topic.getForum().setLastUsername(topic.getUsername());
			topic.getForum().setLastTopic(topic);

			List tags = topic.getKeywords();
			if(tags!=null && tags.size()>0){
				int tag_count = 0;
				for(int i=0;i<tags.size();i++){
					if(tag_count>=MAX_TAG_COUNT)
						break;
					String tag_name = (String)tags.get(i);
					if(tag_name.getBytes().length > MAX_TAG_LENGTH)
						continue;
					TagBean tag = new TagBean();
					tag.setSite(topic.getSite());
					tag.setRefId(topic.getId());
					tag.setRefType(DiaryBean.TYPE_BBS);
					tag.setName(tag_name);
					ssn.save(tag);
					tag_count ++;
				}
			}			
			
			ssn.save(topic);
			if (add_bookmark) {
				BookmarkBean bmb = new BookmarkBean();
				bmb.setOwner(topic.getUser());
				bmb.setSite(topic.getSite());
				bmb.setCreateTime(new Date());
				bmb.setParentId(topic.getId());
				bmb.setParentType(_BeanBase.TYPE_BBS);
				bmb.setTitle(topic.getTitle());
				ssn.save(bmb);
			}
			commit();
		} catch (HibernateException e) {
			rollback();
			throw e;
		}
	}

	/**
	 * 修改帖子
	 * @param topic
	 */
	public static void update(TopicBean topic, boolean updateTags){
		try{
			beginTransaction();			
			if(updateTags){				
				TagDAO.deleteTagByRefId(topic.getId(), DiaryBean.TYPE_BBS);				
				List tags = topic.getKeywords();
				if(tags!=null && tags.size()>0){
					int tag_count = 0;
					for(int i=0;i<tags.size();i++){
						if(tag_count>=MAX_TAG_COUNT)
							break;
						String tag_name = (String)tags.get(i);
						if(tag_name.getBytes().length > MAX_TAG_LENGTH)
							continue;
						TagBean tag = new TagBean();
						tag.setSite(topic.getSite());
						tag.setRefId(topic.getId());
						tag.setRefType(DiaryBean.TYPE_BBS);
						tag.setName((String)tags.get(i));
						topic.getTags().add(tag);
						tag_count ++;
					}
				}
			}
			commit();
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 删除帖子
	 * 
	 * @param topic
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static void delete(TopicOutlineBean topic) throws Exception {
		if(topic==null)
			return ;
		Session ssn = getSession();
		try {
			beginTransaction();
			// 论坛的帖子数减一
			topic.getForum().incTopicCount(-1);
			//论坛的最后回帖
			if (topic.getForum().getLastTopic() != null
					&& topic.getForum().getLastTopic().getId() == topic.getId()) {
				topic.getForum().setLastTopic(null);
				topic.getForum().setLastPostTime(null);
				topic.getForum().setLastUsername(null);
				topic.getForum().setLastUser(null);
			}
			topic.getUser().getCount().incTopicCount(-1);
			
			List rpls = topic.getReplies();
			for(int i=rpls.size()-1;i>=0;i--){
				TopicReplyBean rbean = (TopicReplyBean)rpls.get(i);
				if(rbean.getUser()!=null)
					rbean.getUser().getCount().incTopicReplyCount(-1);
			}
			
			ssn.delete(topic);

			//删除标签
			TagDAO.deleteTagByRefId(topic.getId(), TagBean.TYPE_BBS);

			//删除附件
			FCKUploadFileDAO.deleteFilesByRef(ssn, topic.getSite().getId(),
					topic.getId(), DiaryBean.TYPE_BBS);
			
			commit();
		} catch (HibernateException e) {
			rollback();
			throw e;
		}
	}

	/**
	 * 增加日记的阅读数
	 * @param log_id
	 * @param incCount
	 * @return
	 */
	public static void incViewCount(int topic_id, int incCount){
		executeNamedUpdate("INC_TOPIC_VIEW_COUNT", incCount, topic_id);
	}
	
	/**
	 * @see com.liusoft.dlog4j.search.SearchDataProvider#fetchAfter(Date)
	 */
	public List fetchAfter(Date date) throws Exception {
		return findNamedAll("LIST_TOPICS_AFTER_SQL", date, TopicBean.STATUS_NORMAL);
	}

}
