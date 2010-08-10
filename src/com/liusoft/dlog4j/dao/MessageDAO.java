/*
 *  MessageDAO.java
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

import com.liusoft.dlog4j.beans.MessageBean;

/**
 * 站内短消息之数据库访问接口
 * @author Winter Lau
 */
public class MessageDAO extends DAO {
	
	/**
	 * 回复并删除所回复的短消息
	 * @param old_msg_id
	 * @param msg
	 */
	public static void replyAndDeleteMessage(int old_msg_id, MessageBean msg){
		try{
			Session ssn = getSession();
			beginTransaction();
			//回复短消息
			ssn.save(msg);
			//删除所回复的短消息
			if(old_msg_id > 0)
				executeNamedUpdate("DELETE_MESSAGE", old_msg_id, msg.getFromUser().getId());
			commit();
		}catch(HibernateException e){
			rollback();
		}
	}
	
	/**
	 * 阅读单独消息
	 * @param msg
	 */
	public static void readMsg(MessageBean msg){
		if(msg != null){
			msg.setStatus(MessageBean.STATUS_READ);
			msg.setReadTime(new Date());		
			flush();
		}
	}
	
	/**
	 * 设置新短消息状态为已读
	 * @param userid
	 * @return
	 */
	public static int readNewMsgs(int userid){
		return commitNamedUpdate("READ_MESSAGE", MessageBean.STATUS_READ,
				new Timestamp(System.currentTimeMillis()), userid,
				MessageBean.STATUS_NEW);
	}
	
	/**
	 * 读取短消息
	 * @param msg_id
	 * @return
	 */
	public static MessageBean getMsg(int msg_id) {
		if(msg_id < 0)
			return null;
		return (MessageBean)getBean(MessageBean.class, msg_id);
	}
	
	/**
	 * 判断是否有新短消息
	 * @param userid
	 * @return
	 */
	public static boolean hasNewMessage(int userid){
		return getNewMessageCount(userid)>0;
	}
	
	/**
	 * 返回某个用户的新短消息数(未读短消息)
	 * @param userid
	 * @return
	 */
	public static int getNewMessageCount(int userid){
		return executeNamedStat("NEW_MESSAGE_COUNT_OF_STATUS", userid,
				MessageBean.STATUS_NEW, new Date()).intValue();
	}

	/**
	 * 返回某个用户的短消息数
	 * @param userid
	 * @return
	 */
	public static int getMessageCount(int userid){
		return executeNamedStat("MESSAGE_COUNT", userid, MessageBean.STATUS_DELETED).intValue();
	}
	
	/**
	 * 列出某个用户接收到的短消息
	 * @param userid
	 * @param fromId
	 * @param count
	 * @return
	 */
	public static List listMsgs(int userid, int fromIdx, int count){
		return executeNamedQuery("LIST_MESSAGE", fromIdx, count, userid);
	}
	
	/**
	 * 删除某类短消息
	 * @param userid
	 * @param status
	 * @param commit
	 * @return
	 * @throws SQLException 
	 */
	public static int deleteMsgs(int userid, int status){
		return commitNamedUpdate("DELETE_MESSAGE_BY_STATUS", userid, status);
	}
	
	/**
	 * 删除某条短消息
	 * @param userid
	 * @param msgid
	 * @param commit
	 * @return
	 */
	public static int deleteMsg(int userid, int msgid){
		return commitNamedUpdate("DELETE_MESSAGE", msgid, userid);
	}

	/**
	 * 删除某些短消息
	 * @param ownerId
	 * @param friendId
	 */
	public static int deleteMsgs(int ownerId, String[] msgIds){
		if(msgIds == null|| msgIds.length == 0)
			return 0;
		//一次最多删除五十条
		int max_msg_count = Math.min(msgIds.length, 50);
		StringBuffer hql = new StringBuffer("DELETE FROM MessageBean AS f WHERE f.toUser.id=? AND f.id IN (");
		for(int i=0;i<max_msg_count;i++){
			hql.append("?,");
		}
		hql.append("?)");
		Session ssn = getSession();
		try{
			beginTransaction();
			Query q = ssn.createQuery(hql.toString());
			q.setInteger(0, ownerId);
			int i=0;
			for(;i<max_msg_count;i++){
				String s_id = (String)msgIds[i];
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
	
}
