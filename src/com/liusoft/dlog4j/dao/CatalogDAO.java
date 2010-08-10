/*
 *  CatalogDAO.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.liusoft.dlog4j.CapacityExceedException;
import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.base.Orderable;
import com.liusoft.dlog4j.beans.CatalogBean;
import com.liusoft.dlog4j.beans.CatalogPermBean;
import com.liusoft.dlog4j.beans.CatalogUserKey;
import com.liusoft.dlog4j.beans.DiaryBean;
import com.liusoft.dlog4j.beans.SiteBean;

/**
 * 日记分类数据库访问接口
 * 
 * @author liudong
 */
public class CatalogDAO extends DAO {

	public final static int MAX_CATALOG_COUNT = 10;

	/**
	 * 删除特殊权限
	 * 
	 * @param catalog_id
	 * @param user_id
	 * @return
	 * @throws SQLException
	 */
	public static int deletePermission(int catalog_id, int user_id){
		return commitNamedUpdate("DELETE_CATALOG_PERM", catalog_id, user_id);
	}

	/**
	 * 更新特殊权限
	 * 
	 * @param catalog_id
	 * @param user_id
	 * @param role
	 * @return
	 * @throws SQLException
	 */
	public static int updatePermission(int catalog_id, int user_id, int role){
		return commitNamedUpdate("UPDATE_CATALOG_PERM", role, catalog_id, user_id);
	}

	/**
	 * 创建或者更新权限
	 * 
	 * @param catalog_id
	 * @param user_id
	 * @param role
	 * @throws SQLException
	 */
	public static void createPermission(int catalog_id, int user_id, int role){
		CatalogPermBean cpb = new CatalogPermBean();
		cpb.setRole(role);
		cpb.setKey(new CatalogUserKey(catalog_id, user_id));
		saveOrUpdate(cpb);
	}

	/**
	 * 返回用户在某个分类先的访问权限
	 * 
	 * @param catalog_id
	 * @param user_id
	 * @return 返回-1表示该用户在指定分类下无特殊权限
	 */
	public static int getUserRoleInCatalog(int catalog_id, int user_id) {
		Number res = executeNamedStat("GET_ROLE_IN_CATALOG", catalog_id, user_id);
		return (res != null) ? res.intValue() : -1;
	}

	/**
	 * 列出在某个分类用于特殊操作权限的所有用户 例如可在普通分类中发表日记以及可阅读私有分类中的文章或者可在私有分类中发表日记
	 * 
	 * @param catalog_id
	 * @return
	 */
	public static List listSpecialPopedomUsersByCatalog(int catalog_id) {
		return findNamedAll("LIST_USER_IN_CATALOG",catalog_id);
	}

	/**
	 * 统计分类下指定状态的日记有多少
	 * 
	 * @param catalog_id
	 * @param all
	 * @param status
	 * @return
	 */
	public static int getDiaryCount(int catalog_id, boolean all, int status) {
		String hql = "SELECT COUNT(*) FROM DiaryBean AS d WHERE d.catalog.id=?";
		if (!all){
			hql += " AND d.status=?";
			return executeStatAsInt(hql, catalog_id, status);
		}
		return executeStatAsInt(hql, catalog_id);
	}
	
	/**
	 * 清除某个分类下的所有日记到垃圾箱
	 * 
	 * @param site
	 * @param catalog_id
	 * @return
	 * @throws SQLException
	 */
	public static int removeDiary(SiteBean site, int catalog_id){
		try {
			beginTransaction();
			int er = executeNamedUpdate("UPDATE_DIARY_STATUS_IN_CATALOG", DiaryBean.STATUS_DELETED, catalog_id, site.getId());
			executeNamedUpdate("UPDATE_CATALOG_DIARY_COUNT", 0, catalog_id, site.getId());
			commit();
			return er;
		} catch (HibernateException e) {
			rollback();
			throw e;
		}
	}

	/**
	 * 移动日记
	 * 
	 * @param site
	 * @param fromCatalogId
	 * @param toCatalogId
	 * @return
	 * @throws SQLException
	 */
	public static int moveDiary(SiteBean site, CatalogBean fromCat, CatalogBean toCat){
		try {
			beginTransaction();
			int er = executeNamedUpdate("MOVE_DIARY", toCat.getId(), fromCat.getId(), site.getId());

			executeNamedUpdate("UPDATE_CATALOG_DIARY_COUNT", 0, fromCat.getId(), site.getId());
			executeNamedUpdate("INC_CATALOG_DIARY_COUNT", fromCat.getArticleCount(), toCat.getId(), site.getId());
			
			commit();
			return er;
		} catch (HibernateException e) {
			rollback();
			throw e;
		}
	}

	/**
	 * 判断某个用户是否有访问指定日记分类的权限(浏览)
	 * 
	 * @param catalog
	 * @param user
	 * @return
	 * @throws HibernateException
	 */
	public static boolean canUserViewThisCatalog(CatalogBean catalog,
			SessionUserObject user) throws HibernateException {
		if (catalog.getType() == CatalogBean.TYPE_OWNER) {
			if (user == null)
				return false;
			if (catalog.getSite().isOwner(user))
				return true;
			else {
				return executeNamedStat("CHECK_USER_PERM_IN_CATALOG",catalog.getId(),user.getId()).intValue() > 0 ;
			}
		}
		return true;
	}

	/**
	 * 根据日记分类的编号获取日记分类详细信息
	 * 
	 * @param catalog_id
	 * @return
	 */
	public static CatalogBean getCatalogByID(int catalog_id) {
		if(catalog_id < 0)
			return null;
		return (CatalogBean)getBean(CatalogBean.class, catalog_id);
	}

	/**
	 * 判断用户在某个分类下的权限
	 * 
	 * @param perms
	 * @param catalog_id
	 * @param user_id
	 * @return
	 */
	private static int getUserRoleInCatalog(List perms, CatalogBean catalog,
			SessionUserObject user) {
		if(catalog==null || user==null || perms==null)
			return -1;
		for (int i = 0; i < perms.size(); i++) {
			CatalogPermBean perm = (CatalogPermBean) perms.get(i);
			if (perm.getKey().getCatalog() == catalog.getId()
					&& perm.getKey().getUser() == user.getId())
				return perm.getRole();
		}
		return -1;
	}
	
	/**
	 * 查询用户在某个站点上可写日记的分类数
	 * @param site
	 * @param user
	 * @return
	 */
	public static boolean userCanBlog(SiteBean site, SessionUserObject user){
		if(site == null || user==null)
			return false;
		if(site.isOwner(user))
			return true;
		return executeNamedStat("CHECK_USER_CAN_DIARY", user.getId(), CatalogPermBean.ROLE_BLOG, site.getId()).intValue()>0;
	}

	/**
	 * 列出在某个站点某个用户可见的所有分类 (此方法应该在session一级做缓存)
	 * 
	 * @param site
	 * @param user_id
	 * @param maintain
	 *            表明是写日记或者是浏览
	 * @return
	 * @throws HibernateException
	 */
	public static List listCatalogs(SiteBean site, SessionUserObject user, boolean maintain) {
		List catalogs = new ArrayList();
		catalogs.addAll(site.getCatalogs());
		List perms = null;
		if(user != null){
			Query q = getSession().getNamedQuery("USER_PERMS");
			q.setInteger(0, user.getId());
			perms = q.list();
		}
		Iterator iter = catalogs.iterator();
		while (iter.hasNext()) {
			CatalogBean catalog = (CatalogBean) iter.next();
			// 自由分类任何人都可以访问
			if (catalog.getType() == CatalogBean.TYPE_FREE)
				continue;
			int role = getUserRoleInCatalog(perms, catalog, user);
			if (!maintain) {// 浏览用
				if (catalog.getType() != CatalogBean.TYPE_GENERAL) {
					if (role < 0)
						iter.remove();
				}
			} else {// 写日记用
				if (catalog.getType() == CatalogBean.TYPE_GENERAL)
					iter.remove();
				else {
					if (role != CatalogPermBean.ROLE_BLOG)
						iter.remove();
				}
			}
		}
		return catalogs;
	}

	/**
	 * 调整分类顺序 由于在创建分类的时候已经可以保证所有分类是有序的递增的 因此直接交换两个分类的排序值即可
	 * 
	 * @param site
	 * @param linkid
	 * @param up
	 *            向上调整或者向下调整
	 */
	public static void move(SiteBean site, int cat_id, boolean up){
		List objects = site.getCatalogs();
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
							executeNamedUpdate("UPDATE_CATALOG_ORDER",sort_order, prev.getId());
							executeNamedUpdate("UPDATE_CATALOG_ORDER",prev_order, obj.getId());
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
							executeNamedUpdate("UPDATE_CATALOG_ORDER",sort_order, next.getId());
							executeNamedUpdate("UPDATE_CATALOG_ORDER",next_order, obj.getId());
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

	/**
	 * 添加日记分类
	 * 
	 * @param lnk
	 * @param pos
	 * @param dir
	 * @throws SQLException
	 * @throws CapacityExceedException
	 */
	public static void create(CatalogBean obj, int pos, boolean up)
			throws CapacityExceedException {
		Session ssn = getSession();
		int order_value = 1;
		if (pos > 0) {
			CatalogBean friend = (CatalogBean) ssn.get(CatalogBean.class,
					new Integer(pos));
			order_value = friend.getSortOrder();
		}
		obj.setSortOrder(order_value - (up ? 1 : 0));
		try {
			beginTransaction();
			ssn.save(obj);
			List catalogs = findNamedAll("LIST_CATALOGS",obj.getSite().getId());
			if (catalogs.size() >= ConfigDAO.getMaxCatalogCount(obj.getSite().getId()))
				throw new CapacityExceedException(catalogs.size());
			if (catalogs.size() > 1) {
				for (int i = 0; i < catalogs.size(); i++) {					
					Orderable lb = (Orderable) catalogs.get(i);
					executeNamedUpdate("UPDATE_CATALOG_ORDER",(i+1),lb.getId());
				}
			}
			commit();
		} catch (HibernateException e) {
			rollback();
			throw e;
		}
	}

	/**
	 * 删除日记分类
	 * 
	 * @param siteid
	 * @param linkid
	 * @return
	 * @throws SQLException
	 */
	public static void delete(int siteid, int catalog_id){
		commitNamedUpdate("DELETE_CATALOG", siteid, catalog_id);
	}

}
