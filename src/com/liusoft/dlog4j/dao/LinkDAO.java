/*
 *  LinkDAO.java
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

import com.liusoft.dlog4j.base.Orderable;
import com.liusoft.dlog4j.beans.LinkBean;
import com.liusoft.dlog4j.beans.SiteBean;

/**
 * 友情链接的数据库访问接口
 * @author liudong
 */
public class LinkDAO extends DAO {
	
	/**
	 * 获取友情链接数
	 * @param site
	 * @return
	 */
	public static int getLinkCount(SiteBean site){
		if(site == null)
			return -1;
		return executeNamedStat("GET_LINK_COUNT", site.getId()).intValue();
	}
	
	/**
	 * 分页列出某个网站的所有友情链接，主要用于WML页面，因为HTML页面无需分页可以直接从site.links获取
	 * @param site
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List getLinksOfSite(SiteBean site, int fromIdx, int count){
		if(site == null)
			return null;
		return executeNamedQuery("LIST_LINKS", fromIdx, count, site.getId());
	}
	
	/**
	 * 判断友情链接中是不是已经包含有某个网站的链接
	 * @param site
	 * @param lnk_site_id
	 * @return
	 */
	public static boolean isInnerSiteExists(SiteBean site, int lnk_site_id){
		return executeNamedStatAsInt("CHECK_LINK_URL", site.getId(),
				LinkBean.TYPE_INNER, String.valueOf(lnk_site_id)) > 0;
	}

	/**
	 * 添加友情链接
	 * @param lnk
	 * @param pos
	 * @param dir
	 * @throws SQLException 
	 */
	public static void create(LinkBean lnk, int pos, boolean up){
		int order_value = 1;
		if(pos > 0){
			LinkBean friend = (LinkBean)getBean(LinkBean.class, pos);
			order_value = friend.getSortOrder();
		}
		lnk.setSortOrder(order_value - (up?1:0));
		try{
			beginTransaction();
			getSession().save(lnk);
			//重新读取链接列表，依照顺序进行整理
			List links = findNamedAll("LIST_LINKS", lnk.getSiteId());
			for(int i=0;i<links.size()&&links.size()>1;i++){
				LinkBean lb = (LinkBean)links.get(i);
				executeNamedUpdate("UPDATE_LINK_ORDER", (i+1), lb.getId());
			}
			commit();
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 根据编号来获取链接信息
	 * @param site
	 * @param lnk_id
	 * @return
	 */
	public static LinkBean getLinkByID(int lnk_id){
		if(lnk_id < 0)
			return null;
		return (LinkBean)getBean(LinkBean.class, lnk_id);
	}

	/**
	 * 删除友情链接
	 * @param siteid
	 * @param linkid
	 * @return
	 * @throws SQLException
	 */
	public static int delete(int siteid, int linkid){
		return commitNamedUpdate("DELETE_LINK", siteid, linkid);
	}
	
	/**
	 * 更新链接
	 * @param lnk
	 */
	public static void update(LinkBean lnk){
		flush();
	}
	
	/**
	 * 调整链接顺序
	 * 由于在创建链接的时候已经可以保证所有链接是有序的递增的
	 * 因此直接交换两个链接的排序值即可
	 * @param site
	 * @param linkid
	 * @param up	向上调整或者向下调整
	 * @throws SQLException 
	 */
	public static void move(SiteBean site, int linkid, boolean up){
		List links = site.getLinks();
		for(int i=0;i<links.size();i++){
			Orderable link = (Orderable)links.get(i);
			int sort_order = link.getSortOrder();
			if(link.getId()==linkid){
				if(up){
					if(i > 0){
						try{
							Orderable prev = (Orderable)links.get(i-1);
							int prev_order = prev.getSortOrder();
							beginTransaction();
							executeNamedUpdate("UPDATE_LINK_ORDER", sort_order, prev.getId());
							executeNamedUpdate("UPDATE_LINK_ORDER", prev_order, link.getId());
							commit();
						}catch(HibernateException e){
							rollback();
							throw e;
						}
					}
				}
				else{
					if(i < (links.size()-1)){
						try{
							Orderable next = (Orderable)links.get(i+1);
							int next_order = next.getSortOrder();
							beginTransaction();
							executeNamedUpdate("UPDATE_LINK_ORDER", sort_order, next.getId());
							executeNamedUpdate("UPDATE_LINK_ORDER", next_order, link.getId());
							commit();
						}catch(HibernateException e){
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
