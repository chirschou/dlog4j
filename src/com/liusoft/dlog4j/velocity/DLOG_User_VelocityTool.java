/*
 *  DLOG_User_VelocityTool.java
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

import java.util.List;

import org.hibernate.HibernateException;

import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.beans.MessageBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.MessageDAO;
import com.liusoft.dlog4j.dao.UserDAO;

/**
 * 跟用户相关的Toolbox
 * @author Winter Lau
 */
public class DLOG_User_VelocityTool {
	
	/**
	 * 根据用户编号获取用户的详细信息(_global.vm)
	 * 
	 * @param user_id
	 * @return
	 * @throws HibernateException
	 */
	public UserBean user(int user_id){
		if (user_id <= 0)
			return null;
		UserBean user = UserDAO.getUserByID(user_id);
		return user;
	}

	/**
	 * 获取注册用户总数(users.vm)
	 * @param site
	 * @return
	 */
	public int user_count(SiteBean site){
		if(site==null)
			return -1;
		return UserDAO.getUserCountFromSite(site.getId());
	}

	/**
	 * 列出从某个网站上注册的用户(_xxx_top_info.vm)
	 * 
	 * @param site
	 * @param page
	 * @param count
	 * @return
	 */
	public List list_users(SiteBean site, int page, int count) {
		if (site == null)
			return null;
		int fromidx = (page - 1) * count;
		return UserDAO.listUsersFromSite(site.getId(), fromidx, count);
	}
	
	/**
	 * 列出某个网站注册的在线用户
	 * @param site
	 * @param page
	 * @param count
	 * @return
	 */
	public List online_users(SiteBean site, int page, int count){
		if (site == null)
			return null;
		if(count<0)
			count = 50;
		int fromidx = (page - 1) * count;
		if(fromidx < 0)
			fromidx = 0;
		return UserDAO.listOnlineUsers(site.getId(), fromidx, count);
	}
	
	/**
	 * 获取在线用户数
	 * @param site
	 * @return
	 */
	public int online_user_count(SiteBean site){
		if (site == null)
			return -1;
		return UserDAO.getOnlineUserCount(site.getId());
	}

	/**
	 * 返回某人的好友数
	 * @param user_id
	 * @return
	 */
	public int friend_count(int user_id){
		if (user_id <= 0)
			return -1;
		return UserDAO.getFriendCount(user_id);
	}
	
	/**
	 * 列出某人的好友
	 * @param user_id
	 * @param page
	 * @param count
	 * @return
	 */
	public List friends(int user_id, int page, int count){
		if (user_id <= 0)
			return null;
		int fromIdx = (page - 1) * count;
		if(fromIdx < 0)
			fromIdx = 0;
		return UserDAO.listFriends(user_id, fromIdx, count);
	}
	
	/**
	 * 列出某人的黑名单中的所有用户
	 * @param user_id
	 * @param page
	 * @param count
	 * @return
	 */
	public List black_users(int user_id, int page, int count){
		if (user_id <= 0)
			return null;
		int fromIdx = (page - 1) * count;
		if(fromIdx < 0)
			fromIdx = 0;
		return UserDAO.listBlackUsers(user_id, fromIdx, count);
	}
	
	/**
	 * 获取某人的黑名单用户数
	 * @param user_id
	 * @return
	 */
	public int black_user_count(int user_id){
		if (user_id <= 0)
			return -1;
		return UserDAO.getBlackUserCount(user_id);
	}
	
	/**
	 * 获取短消息总数
	 * @param user
	 * @return
	 */
	public int msg_count(SessionUserObject user){
		if(user == null)
			return -1;
		return MessageDAO.getMessageCount(user.getId());
	}
	
	/**
	 * 列出某人接收到的短消息
	 * @param user
	 * @param fromId
	 * @param count
	 * @return
	 */
	public List msgs(SessionUserObject user, int page, int count){
		if(user == null)
			return null;
		int fromIdx = (page - 1) * count;		
		return MessageDAO.listMsgs(user.getId(), fromIdx, count);
	}
	
	/**
	 * 阅读单条短消息
	 * @param msg
	 */
	public void read_msg(MessageBean msg){
		if(msg != null)
			MessageDAO.readMsg(msg);
	}

}
