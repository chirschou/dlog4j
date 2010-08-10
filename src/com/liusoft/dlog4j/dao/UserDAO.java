/*
 *  UserDAO.java
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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.liusoft.dlog4j.beans.FriendBean;
import com.liusoft.dlog4j.beans.MyBlackListBean;
import com.liusoft.dlog4j.beans.UserBean;

/**
 * 用户相关的数据库访问接口
 * @author liudong
 */
public class UserDAO extends DAO {
	
	/**
	 * 返回某个站点的注册用户数,如果没有指定站点则返回注册用户总数
	 * @param site
	 * @return
	 */
	public static int getUserCount(int site){
		String hql = "SELECT COUNT(*) FROM UserBean d WHERE d.status=?";
		if(site>0){
			hql += " AND d.site.id=?";
			return executeStatAsInt(hql, UserBean.STATUS_NORMAL, site);
		}
		return executeStatAsInt(hql, UserBean.STATUS_NORMAL);
	}

	/**
	 * 添加好友
	 * @param friend
	 */
	public static boolean addFriend(FriendBean friend){
		if(namedUniqueResult("FRIEND", friend.getOwner(), friend.getFriend().getId())==null){
			save(friend);
			return true;
		}
		return false;
	}

	/**
	 * 添加黑名单 
	 * @param myId
	 * @param otherId
	 * @param type
	 * @return
	 */
	public static boolean addBlackList(int myId, int otherId, int type){
		if(namedUniqueResult("BLACKLIST", myId, otherId)==null){
			MyBlackListBean fbean = new MyBlackListBean();
			fbean.setAddTime(new Date());
			fbean.setMyId(myId);
			fbean.setOther(new UserBean(otherId));
			fbean.setType(type);
			save(fbean);
			return true;
		}
		return false;
	}
	
	/**
	 * 从黑名单中删除某个用户
	 * @param myId
	 * @param otherId
	 * @return
	 */
	public static boolean delBlackList(int myId, int otherId){
		return commitNamedUpdate("DELETE_BLACKLIST", myId, otherId)>0;
	}
	
	/**
	 * 判断某用户是否在你的黑名单中
	 * @param myId
	 * @param otherId
	 * @return
	 */
	public static boolean isUserInBlackList(int myId, int otherId){
		return namedUniqueResult("BLACKLIST", myId, otherId)!=null;
	}
	
	/**
	 * 查询在线用户
	 * @param site_id
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listOnlineUsers(int site_id, int fromIdx, int count){
		return executeNamedQuery("ONLINE_USERS", fromIdx, count, site_id, UserBean.STATUS_ONLINE);
	}
	
	/**
	 * 查询在线用户数
	 * @param site_id
	 * @return
	 */
	public static int getOnlineUserCount(int site_id){
		return executeNamedStat("ONLINE_USER_COUNT", site_id, UserBean.STATUS_ONLINE).intValue();
	}
	
	/**
	 * 判断两人是否好友
	 * @param ownerId
	 * @param friendId
	 * @return
	 */
	public static FriendBean getFriend(int ownerId, int friendId){
		return (FriendBean)namedUniqueResult("FRIEND", ownerId, friendId);
	}
	
	/**
	 * 返回某人的好友数
	 * @param ownerId
	 * @return
	 */
	public static int getFriendCount(int ownerId){
		return executeNamedStat("FRIEND_COUNT", ownerId).intValue();
	}
	
	/**
	 * 列出某人的好友
	 * @param ownerId
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listFriends(int ownerId, int fromIdx, int count){
		return executeNamedQuery("LIST_FRIEND", fromIdx, count, ownerId);
	}

	/**
	 * 返回某人的黑名单用户数
	 * @param ownerId
	 * @return
	 */
	public static int getBlackUserCount(int ownerId){
		return executeNamedStat("BLACKLIST_COUNT", ownerId).intValue();
	}
	
	/**
	 * 列出某人黑名单中的用户
	 * @param ownerId
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listBlackUsers(int ownerId, int fromIdx, int count){
		return executeNamedQuery("LIST_BLACKLIST", fromIdx, count, ownerId);
	}
	
	/**
	 * 删除两个人的好友关系
	 * @param ownerId
	 * @param friendId
	 */
	public static int deleteFriend(int ownerId, String[] friendIds){
		if(friendIds == null|| friendIds.length == 0)
			return 0;
		StringBuffer hql = new StringBuffer("DELETE FROM FriendBean f WHERE f.owner=? AND f.friend.id IN (");
		for(int i=0;i<friendIds.length;i++){
			hql.append("?,");
		}
		hql.append("?)");
		Session ssn = getSession();
		try{
			beginTransaction();
			Query q = ssn.createQuery(hql.toString());
			q.setInteger(0, ownerId);
			int i=0;
			for(;i<friendIds.length;i++){
				String s_id = (String)friendIds[i];
				int id = -1;
				try{
					id = Integer.parseInt(s_id);
				}catch(Exception e){}
				q.setInteger(i+1, id);
			}
			q.setInteger(i+1, -1);
			int er = q.executeUpdate();
			commit();
			return er;
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}

	/**
	 * 删除黑名单
	 * @param ownerId
	 * @param friendId
	 */
	public static int deleteBlacklist(int ownerId, String[] otherIds){
		if(otherIds == null|| otherIds.length == 0)
			return 0;
		StringBuffer hql = new StringBuffer("DELETE FROM MyBlackListBean f WHERE f.myId=? AND f.other.id IN (");
		for(int i=0;i<otherIds.length;i++){
			hql.append("?,");
		}
		hql.append("?)");
		Session ssn = getSession();
		try{
			beginTransaction();
			Query q = ssn.createQuery(hql.toString());
			q.setInteger(0, ownerId);
			int i=0;
			for(;i<otherIds.length;i++){
				String s_id = (String)otherIds[i];
				int id = -1;
				try{
					id = Integer.parseInt(s_id);
				}catch(Exception e){}
				q.setInteger(i+1, id);
			}
			q.setInteger(i+1, -1);
			int er = q.executeUpdate();
			commit();
			return er;
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 搜索某个站的注册用户
	 * @param site
	 * @param searchKey
	 * @return
	 */
	public static List searchUser(String searchKey){
		String key = searchKey + '%';
		return executeNamedQuery("SEARCH_USER", -1, 20, key, key);
	}
	
	/**
	 * 创建新用户
	 * @param user
	 * @param commit
	 * @return
	 */
	public static boolean createUser(UserBean user){
		if(user == null) 
			return false;
		Timestamp ct = new Timestamp(System.currentTimeMillis());
		user.setRegTime(ct);
		user.setLastTime(ct);
		user.setStatus(UserBean.STATUS_NORMAL);
		save(user);
		return true;
	}
	
	/**
	 * 列出从某个网站上注册的用户(_xxx_top_info.vm, users.vm)
	 * @param site_id
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listUsersFromSite(int site_id, int fromIdx, int count){
		return executeNamedQuery("LIST_REGUSERS_OF_SITE", fromIdx, count, site_id);
	}

	/**
	 * 列出从某个网站上注册的用户数(users.vm)
	 * @param site_id
	 * @return
	 */
	public static int getUserCountFromSite(int site_id){
		return executeNamedStat("REGUSER_COUNT_OF_SITE", site_id).intValue();
	}
	
	/**
	 * 更新用户资料
	 * @param user
	 * @param commit
	 * @throws HibernateException
	 */
	public static void updateUser(UserBean user){
		flush();
	}

	/**
	 * 清除keep_days字段值,用于注销时候调用
	 * @param userid
	 * @param lastLogin 最近一次登录的时间
	 * @param manual_logout 是否手工注销
	 * @return
	 */
	public static int userLogout(int userid, Timestamp lastLogin, boolean manual_logout){
		Session ssn = getSession();
		if(ssn == null)
			return -1;
		try{
			beginTransaction();
			Query q = ssn.getNamedQuery(manual_logout?"USER_LOGOUT_1":"USER_LOGOUT_2");
			q.setInteger("online_status", UserBean.STATUS_OFFLINE);
			if(manual_logout)
				q.setInteger("keep_day", 0);
			q.setInteger("user_id", userid);
			q.setTimestamp("last_time", lastLogin);
			int er = q.executeUpdate();
			commit();
			return er;
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 根据用户编号获取用户详细信息
	 * @param user_id
	 * @return
	 * @throws HibernateException
	 */
	public static UserBean getUserByID(int user_id){
		if(user_id < 0)
			return null;
		return (UserBean)getBean(UserBean.class, user_id);
	}
	
	/**
	 * 根据用户名加载用户资料,用于用户的登录
	 * @param username
	 * @return
	 * @throws HibernateException
	 */
	public static UserBean getUserByName(String username){
		return (UserBean)namedUniqueResult("GET_USER_BY_NAME", username);
	}
	
	/**
	 * 根据用户昵称加载用户资料,用于注册时不允许同名存在
	 * @param nickname
	 * @return
	 * @throws HibernateException
	 */
	public static UserBean getUserByNickname(String nickname){
		return (UserBean)namedUniqueResult("GET_USER_BY_NICKNAME", nickname);
	}
	
}
