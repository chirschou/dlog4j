/*
 *  SiteDAO.java
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
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.liusoft.dlog4j.base.FunctionStatus;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 个人站点对应的数据库接口
 * SiteBean的url字段必须做唯一索引,url字段不应该由用户进行手工修改
 * @author liudong
 */
public class SiteDAO extends DAO {

	/**
	 * 按照注册用户数排序个人网记
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listSitesOrderByUserCount(int fromIdx , int count){
		return listSitesOrderBy(fromIdx, count, "LIST_SITES_ORDER_BY_USERCOUNT");
	}

	/**
	 * 按照日记数排序个人网记
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listSitesOrderByDiaryCount(int fromIdx , int count){
		return listSitesOrderBy(fromIdx, count, "LIST_SITES_ORDER_BY_DIARYCOUNT");
	}

	/**
	 * 按照照片数排序个人网记
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listSitesOrderByPhotoCount(int fromIdx , int count){
		return listSitesOrderBy(fromIdx, count, "LIST_SITES_ORDER_BY_PHOTOCOUNT");
	}

	/**
	 * 按照歌曲数排序个人网记
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listSitesOrderByMusicCount(int fromIdx , int count){
		return listSitesOrderBy(fromIdx, count, "LIST_SITES_ORDER_BY_MUSICCOUNT");
	}

	/**
	 * 按照讨论话题数排序个人网记
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listSitesOrderByTopicCount(int fromIdx , int count){
		return listSitesOrderBy(fromIdx, count, "LIST_SITES_ORDER_BY_TOPICCOUNT");
	}

	/**
	 * 排序个人网记
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	protected static List listSitesOrderBy(int fromIdx, int count, String hql){
		Session ssn = getSession();
		Query q = ssn.getNamedQuery(hql);
		if(fromIdx > 0)
			q.setFirstResult(fromIdx);
		if(count > 0)
			q.setMaxResults(count);
		List objs = q.list();
		List sites = new ArrayList();
		for(int i=0;i<objs.size();i++){
			Object[] res = (Object[])objs.get(i);
			int siteid = ((Number)res[0]).intValue();
			SiteBean site = new SiteBean(siteid);
			site.setUniqueName((String)res[1]);
			site.setFriendlyName((String)res[2]);
			sites.add(site);
		}
		return sites;
	}
	
	/**
	 * 搜索个人网记
	 * @param key
	 * @return
	 */
	public static List searchSite(String key){
		String pattern = '%' + key + '%';
		return executeNamedQuery("SEARCH_SITE", -1, 20, SiteBean.STATUS_NORMAL, pattern,pattern);
	}

	/**
	 * 列出最新注册的site
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listNewestSites(int fromIdx , int count){
		return executeNamedQuery("LIST_NEW_SITES", fromIdx, count, SiteBean.STATUS_NORMAL);
	}
	
	/**
	 * 列出推荐的网站
	 * 也就是site_level>1的网站
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listRecommendSites(int fromIdx ,int count){
		return executeNamedQuery("LIST_RECOMMEND_SITES", fromIdx, count);
	}
	
	/**
	 * 利用日记的活跃程度来列出热门的site
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listHotSitesViaDiary(int fromIdx, int count){
		return listHotSites("DiaryBean", fromIdx, count);
	}

	/**
	 * 利用照片的活跃程度来列出热门的site
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listHotSitesViaPhoto(int fromIdx, int count){
		return listHotSites("PhotoBean", fromIdx, count);
	}

	/**
	 * 利用讨论的活跃程度来列出热门的site
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listHotSitesViaTopic(int fromIdx, int count){
		return listHotSites("TopicBean", fromIdx, count);
	}

	/**
	 * 列出热门的site
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	protected static List listHotSites(String bean, int fromIdx, int count){
		StringBuffer hql = new StringBuffer("SELECT d.site.id,d.site.uniqueName,d.site.friendlyName,(COUNT(d.id)*100+SUM(d.replyCount)*10+SUM(d.viewCount)) FROM ");
		hql.append(bean);
		hql.append(" d WHERE d.status=? GROUP BY d.site.id,d.site.uniqueName,d.site.friendlyName ORDER BY 4 DESC");
		List objs = executeQuery(hql.toString(), fromIdx, count, SiteBean.STATUS_NORMAL);
		List sites = new ArrayList();
		for(int i=0;i<objs.size();i++){
			int siteid = ((Number)((Object[])objs.get(i))[0]).intValue();
			String name1 = (String)((Object[])objs.get(i))[1];
			String name2 = (String)((Object[])objs.get(i))[2];
			SiteBean site = new SiteBean(siteid);
			site.setUniqueName(name1);
			site.setFriendlyName(name2);
			sites.add(site);
		}
		return sites;
	}
	
	/**
	 * 返回注册的站点总数
	 * @param site
	 * @return
	 */
	public static int getSiteCount(){
		return executeNamedStat("SITE_COUNT", SiteBean.STATUS_NORMAL).intValue();
	}

	/**
	 * 开通个人网记
	 * @param site
	 */
	public static void createSite(SiteBean site){
		site.setFunctionStatus(new FunctionStatus());
		Session ssn = getSession();
		try{
			beginTransaction();
			ssn.save(site);
			site.getOwner().setOwnSiteId(site.getId());
			ssn.update(site.getOwner());
			commit();			
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 更新网站资料
	 * @param site
	 */
	public static void updateSite(SiteBean site){	
		flush();
	}
	
	/**
	 * 根据网站编号获取详细信息
	 * @param site_id
	 * @return
	 * @throws HibernateException
	 */
	public static SiteBean getSiteByID(int site_id){
		if(site_id<1) 
			return null;
		return (SiteBean)getBean(SiteBean.class, site_id);
	}
	
	/**
	 * 根据网站的唯一标识来获取对应网站的详细信息
	 * @param site_name
	 * @return
	 */
	public static SiteBean getSiteByName(String site_name){
		if(StringUtils.isEmpty(site_name))
			return null;
		return (SiteBean)namedUniqueResult("GET_SITE_BY_NAME", site_name);
	}

	/**
	 * 根据网站名来获取对应网站的详细信息
	 * @param site_name
	 * @return
	 */
	public static SiteBean getSiteByFriendlyName(String site_name){
		if(StringUtils.isEmpty(site_name))
			return null;
		return (SiteBean)namedUniqueResult("GET_SITE_BY_FRIENDLYNAME", site_name);
	}
	
	/**
	 * 根据站点自定义的域名进行查询
	 * @param vhost
	 * @return
	 */
	public static SiteBean getSiteByVhost(String vhost){
		return (SiteBean)namedUniqueResult("GET_SITE_BY_VHOST", vhost);
	}
	
}
