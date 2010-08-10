/*
 *  BookmarkDAO.java
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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.liusoft.dlog4j.beans.BookmarkBean;

/**
 * 书签相关的数据库操作方法
 * @author Winter Lau
 */
public class BookmarkDAO extends DAO {

	/**
	 * 增加书签
	 * @param bean
	 */
	public static boolean save(BookmarkBean bookmark){
		if(exists(bookmark.getOwner().getId(), bookmark.getParentId(), bookmark.getParentType()))
			return false;
		Session ssn = getSession();
		try{
			beginTransaction();
			if(bookmark.getCreateTime()==null)
				bookmark.setCreateTime(new Date());
			bookmark.getOwner().getCount().incBookmarkCount(1);
			ssn.save(bookmark);
			commit();			
		}catch(HibernateException e){
			rollback();
			throw e;
		}
		return true;
	}
	
	/**
	 * 删除某个书签
	 * @param siteid
	 * @param userid
	 * @param bookmark_id
	 * @return
	 * @throws SQLException
	 */
	public static boolean delete(int userid, int bookmark_id){
		return commitNamedUpdate("DELETE_BOOKMARK", bookmark_id, userid)>0;
	}

	/**
	 * 删除多个书签
	 * @param ownerId
	 * @param bookmarkIds
	 */
	public static int deleteBookmarks(int ownerId, String[] bookmarkIds){
		if(bookmarkIds == null|| bookmarkIds.length == 0)
			return 0;
		StringBuffer hql = new StringBuffer("DELETE FROM BookmarkBean AS f WHERE f.owner=? AND f.id IN (");
		for(int i=0;i<bookmarkIds.length;i++){
			hql.append("?,");
		}
		hql.append("?)");
		Session ssn = getSession();
		try{
			beginTransaction();
			Query q = ssn.createQuery(hql.toString());
			q.setInteger(0, ownerId);
			int i=0;
			for(;i<bookmarkIds.length;i++){
				String s_id = (String)bookmarkIds[i];
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
		}finally{
			hql = null;
		}
	}
	
	/**
	 * 罗列出某个用户的所有书签
	 * @param user_id
	 * @return
	 */
	public static List list(int user_id){
		return findNamedAll("LIST_BOOKMARK",user_id);
	}
	
	/**
	 * 判断某一书签是否存在
	 * @param user_id
	 * @param parent_id
	 * @param parent_type
	 * @return
	 */
	public static boolean exists(int user_id, int parent_id, int parent_type){
		if(user_id < 1 || parent_id < 0) 
			return false;
		return executeNamedStat("CHECK_BOOKMARK", user_id,parent_id,parent_type).intValue()>0;
	}
	
}
