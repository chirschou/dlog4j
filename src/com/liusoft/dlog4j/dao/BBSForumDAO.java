/*
 *  BBSForumDAO.java
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

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.liusoft.dlog4j.CapacityExceedException;
import com.liusoft.dlog4j.base.Orderable;
import com.liusoft.dlog4j.beans.ForumBean;
import com.liusoft.dlog4j.beans.SiteBean;

/**
 * 论坛的数据库访问接口
 * 
 * @author Winter Lau
 */
public class BBSForumDAO extends DAO {

	/**
	 * 读取某个论坛的详细资料
	 * 
	 * @param forum_id
	 * @return
	 */
	public static ForumBean getForumByID(int forum_id) {
		if(forum_id < 0)
			return null;
		return (ForumBean) getBean(ForumBean.class, forum_id);
	}
	
	/**
	 * 锁定论坛
	 * @param forum_id
	 * @return
	 */
	public static int lockForumByID(int site_id, int forum_id){
		return changeForumStatus(site_id, forum_id, ForumBean.STATUS_LOCKED);
	}

	/**
	 * 隐藏论坛
	 * @param forum_id
	 * @return
	 */
	public static int hideForumByID(int site_id, int forum_id){
		return changeForumStatus(site_id, forum_id, ForumBean.STATUS_HIDDEN);
	}
	
	/**
	 * 锁定论坛
	 * @param forum_id
	 * @return
	 */
	protected static int changeForumStatus(int site_id, int forum_id, int status){
		return executeNamedUpdate("CHANGE_FORUM_STATUS", status, forum_id, site_id);
	}

	/**
	 * 创建论坛
	 * 
	 * @param obj
	 * @param pos
	 * @param up
	 * @throws CapacityExceedException
	 */
	public static void createForum(ForumBean obj, int pos, boolean up)
			throws CapacityExceedException {
		Session ssn = getSession();
		int order_value = 1;
		if (pos > 0) {
			ForumBean friend = (ForumBean) ssn.get(ForumBean.class,
					new Integer(pos));
			order_value = friend.getSortOrder();
		}
		obj.setSortOrder(order_value - (up ? 1 : 0));
		try {
			beginTransaction();
			ssn.save(obj);
			// 重新读取链接列表，依照顺序进行整理
			Query q = ssn.getNamedQuery("LIST_FORUMS");
			q.setInteger(0, obj.getSite().getId());
			List links = q.list();
			if (links.size() >= ConfigDAO.getMaxCatalogCount(obj.getSite()
					.getId()))
				throw new CapacityExceedException(links.size());
			if (links.size() > 1) {
				for (int i = 0; i < links.size(); i++) {
					Orderable lb = (Orderable) links.get(i);
					executeNamedUpdate("UPDATE_FORUM_ORDER", i+1, lb.getId());
				}
			}
			commit();
		} catch (HibernateException e) {
			rollback();
			throw e;
		}
	}

	/**
	 * 删除论坛
	 * 
	 * @param site_id
	 * @param forum_id
	 * @return
	 */
	public static int deleteForumByID(int site_id, int forum_id) {
		return executeNamedUpdate("DELETE_FORUM", site_id, forum_id);
	}

	/**
	 * 调整论坛顺序 
	 * 由于在创建论坛的时候已经可以保证所有分类是有序的递增的 因此直接交换两个分类的排序值即可
	 * 
	 * @param site
	 * @param linkid
	 * @param up 向上调整或者向下调整
	 */
	public static void move(SiteBean site, int cat_id, boolean up){
		List objects = site.getForums();
		for (int i = 0; i < objects.size(); i++) {
			Orderable obj = (Orderable) objects.get(i);
			int sort_order = obj.getSortOrder();
			if (obj.getId() == cat_id) {
				if (up) {
					if (i > 0) {
						try {
							Orderable prev = (Orderable) objects.get(i - 1);
							int prev_order = prev.getSortOrder();
							beginTransaction();
							executeNamedUpdate("UPDATE_FORUM_ORDER", sort_order, prev.getId());
							executeNamedUpdate("UPDATE_FORUM_ORDER", prev_order, obj.getId());
							commit();
						} catch (HibernateException e) {
							rollback();
							throw e;
						}
					}
				} else {
					if (i < (objects.size() - 1)) {
						try {
							Orderable next = (Orderable) objects.get(i + 1);
							int next_order = next.getSortOrder();
							beginTransaction();
							executeNamedUpdate("UPDATE_FORUM_ORDER", sort_order, next.getId());
							executeNamedUpdate("UPDATE_FORUM_ORDER", next_order, obj.getId());
							commit();
						} catch (HibernateException e) {
							rollback();
							throw e;
						}
					}
				}
				break;
			}
		}
	}

}
