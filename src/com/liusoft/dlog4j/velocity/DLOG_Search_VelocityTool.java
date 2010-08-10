/*
 *  VelocityTextTool.java
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
package com.liusoft.dlog4j.velocity;

import java.util.HashMap;
import java.util.List;

import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.beans.DiaryBean;
import com.liusoft.dlog4j.beans.DiaryReplyBean;
import com.liusoft.dlog4j.beans.MusicBean;
import com.liusoft.dlog4j.beans.PhotoBean;
import com.liusoft.dlog4j.beans.PhotoReplyBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.TopicBean;
import com.liusoft.dlog4j.beans.TopicReplyBean;
import com.liusoft.dlog4j.dao.MusicDAO;
import com.liusoft.dlog4j.dao.SiteDAO;
import com.liusoft.dlog4j.dao.UserDAO;
import com.liusoft.dlog4j.search.SearchParameter;
import com.liusoft.dlog4j.search.SearchProxy;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 搜索工具类
 * 
 * @author Winter Lau
 */
public class DLOG_Search_VelocityTool{

	private final static int SCOPE_DIARY = 10;
	private final static int SCOPE_DIARY_REPLY = 11;
	
	private final static int SCOPE_PHOTO = 20;
	private final static int SCOPE_PHOTO_REPLY = 21;
	
	private final static int SCOPE_TOPIC = 30;
	private final static int SCOPE_TOPIC_REPLY = 31;
	
	private final static int SCOPE_MUSIC = 40;

	private final static ThreadLocal searchTimes = new ThreadLocal();
	
	/**
	 * 在线程本地存储区中保存搜索所用的时间
	 * @param lastTime
	 */
	protected void saveSearchTime(long lastTime){
		long st = System.currentTimeMillis() - lastTime;
		searchTimes.set(new Long(st));
	}
	
	/**
	 * 搜索用户
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public List search_user(String searchKey) throws Exception{
		if(StringUtils.isEmpty(searchKey))
			return null;
		long ct = System.currentTimeMillis();
		try{
			return UserDAO.searchUser(searchKey);
		}finally{
			saveSearchTime(ct);
		}
	}
	
	/**
	 * 搜索个人网记
	 * @param searchKey
	 * @return
	 */
	public List search_site(String searchKey){
		if(StringUtils.isEmpty(searchKey))
			return null;
		long ct = System.currentTimeMillis();
		try{
			return SiteDAO.searchSite(searchKey);
		}finally{
			saveSearchTime(ct);
		}
	}
	
	/**
	 * 搜索歌曲
	 * @param searchKey
	 * @return
	 */
	public List search_music(String searchKey){
		if(StringUtils.isEmpty(searchKey))
			return null;
		long ct = System.currentTimeMillis();
		try{
			return MusicDAO.search(searchKey);
		}finally{
			saveSearchTime(ct);
		}
	}
	
	/**
	 * 搜索日记
	 * 
	 * @param site
	 * @param user
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public List search_diary(final SiteBean site, final SessionUserObject user,
			final String searchKey) throws Exception {
		return search(site, user, searchKey, SCOPE_DIARY);
	}

	/**
	 * 搜索音乐
	 * 
	 * @param site
	 * @param user
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public List search_music(final SiteBean site, final SessionUserObject user,
			final String searchKey) throws Exception {
		return search(site, user, searchKey, SCOPE_MUSIC);
	}

	/**
	 * 搜索日记评论
	 * 
	 * @param site
	 * @param user
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public List search_diary_reply(final SiteBean site, final SessionUserObject user,
			final String searchKey) throws Exception {
		return search(site, user, searchKey, SCOPE_DIARY_REPLY);
	}

	/**
	 * 搜索相片
	 * 
	 * @param site
	 * @param user
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public List search_photo(final SiteBean site, final SessionUserObject user,
			final String searchKey) throws Exception {
		return search(site, user, searchKey, SCOPE_PHOTO);
	}

	/**
	 * 搜索相片评论
	 * 
	 * @param site
	 * @param user
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public List search_photo_reply(final SiteBean site, final SessionUserObject user,
			final String searchKey) throws Exception {
		return search(site, user, searchKey, SCOPE_PHOTO_REPLY);
	}

	/**
	 * 搜索帖子
	 * 
	 * @param site
	 * @param user
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public List search_topic(final SiteBean site, final SessionUserObject user,
			final String searchKey) throws Exception {
		return search(site, user, searchKey, SCOPE_TOPIC);
	}

	/**
	 * 搜索帖子评论
	 * 
	 * @param site
	 * @param user
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public List search_topic_reply(final SiteBean site, final SessionUserObject user,
			final String searchKey) throws Exception {
		return search(site, user, searchKey, SCOPE_TOPIC_REPLY);
	}

	/**
	 * 搜索
	 * @param site
	 * @param user
	 * @param searchKey
	 * @param scope
	 * @return
	 * @throws Exception 
	 */
	protected List search(final SiteBean site, final SessionUserObject user,
			final String searchKey, final int scope) throws Exception 
	{
		if(StringUtils.isEmpty(searchKey))
			return null;
		
		long ct = System.currentTimeMillis();
		try{
			if(scope == 6)
				return UserDAO.searchUser(searchKey);

			SearchParameter param = new SearchParameter() {
				public String getSearchKey() {
					return searchKey;
				}
				public HashMap getConditions() {
					if (site != null) {
						HashMap conds = new HashMap();
						conds.put("site.id", new Integer(site.getId()));
						return conds;
					}
					return null;
				}
	
				public Class getSearchObject() {
					switch (scope) {
					case SCOPE_DIARY:
						return DiaryBean.class;
					case SCOPE_DIARY_REPLY:
						return DiaryReplyBean.class;
					case SCOPE_PHOTO:
						return PhotoBean.class;
					case SCOPE_PHOTO_REPLY:
						return PhotoReplyBean.class;
					case SCOPE_TOPIC:
						return TopicBean.class;
					case SCOPE_TOPIC_REPLY:
						return TopicReplyBean.class;
					case SCOPE_MUSIC:
						return MusicBean.class;
					}
					return null;
				}
	
			};
			return SearchProxy.search(param);
		}finally{
			saveSearchTime(ct);
		}
	}

	/**
	 * 返回上次搜索所花的时间
	 * 
	 * @return
	 */
	public long get_search_time() {
		Long sTime = (Long)searchTimes.get();
		return (sTime!=null)?sTime.longValue():-1;
	}

}
