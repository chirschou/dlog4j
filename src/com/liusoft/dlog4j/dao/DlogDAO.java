/*
 *  DlogDAO.java
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

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.liusoft.dlog4j.base.DlogStatInfo;
import com.liusoft.dlog4j.db.HibernateUtils;

/**
 * DLOG4J平台独立的数据库访问方法
 * @author Winter Lau
 */
public class DlogDAO extends DAO {

	/**
	 * 列出整个系统最新的日记
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listNewArticles(int fromIdx, int count){
		return DiaryDAO.listNewArticles(fromIdx, count);
	}
	/**
	 * 获取具有专栏标志的Site在days天内的最热门的文章,不包括文章内容
	 * @param days
	 * @param count
	 * @return
	 */
	public static List listHotArticles(int days, int count){
		return DiaryDAO.listHotArticles(days, count);
	}

	/**
	 * 获取具有专栏标志的Site在days天内的最热门的照片
	 * @param days
	 * @param count
	 * @return
	 */
	public static List listHotPhotos(int days, int count){
		return PhotoDAO.listHotPhotos(days, count);
	}
	
	/**
	 * 列出最新热门帖子
	 * @param days
	 * @param count
	 * @return
	 */
	public static List listHotTopics(int days, int count){
		return BBSTopicDAO.listHotTopics(0, count, days);
	}
	
	/**
	 * 获取制定网站最热门的标签,如果网站没指定(site=-1)则查询所有网站
	 * @param site
	 * @param count
	 * @return
	 */
	public static List listHotTags(int site, int count){
		Session ssn = getSession();
		StringBuffer sql = new StringBuffer("SELECT tag_name,COUNT(*) FROM dlog_tag");
		if(site>0)
			sql.append(" WHERE site_id=?");
		sql.append(" GROUP BY tag_name ORDER BY 2 DESC");
		SQLQuery query = ssn.createSQLQuery(sql.toString());
		if(site>0)
			query.setInteger(0, site);
		query.setMaxResults(count);
		List tags = new ArrayList();
		List results = query.list();
		for(int i=0;results!=null && i<results.size();i++){
			tags.add(((Object[])results.get(i))[0]);
		}
		return tags;
	}
	
	/**
	 * 返回指定网站的统计信息,如果site值小于0则取所有网站
	 * @param site
	 * @return
	 */
	public static DlogStatInfo getDlogStatInfo(int site){
		DlogStatInfo count = new DlogStatInfo();
		//============== 日记数
		count.setArticle(DiaryDAO.getDiaryCount(site));
		//============== 日记评论数
		count.setArticleReply(DiaryDAO.getDiaryReplyCount(site));
		//============== 相片数
		count.setPhoto(PhotoDAO.getPhotoCount(site));
		//============== 相片评论数
		count.setPhotoReply(PhotoDAO.getPhotoReplyCount(site));
		//============== 论坛帖子数
		count.setTopic(BBSTopicDAO.getTopicCount(site));
		//============== 论坛评论数
		count.setTopicReply(BBSReplyDAO.getReplyCount(site));
		//============== 注册用户数
		count.setUser(UserDAO.getUserCount(site));
		//============== 注册网站数
		count.setSite(SiteDAO.getSiteCount());
		return count;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HibernateUtils.init();
		try{
			long ct = System.currentTimeMillis();
			listHotArticles(10,10);
			System.out.println("Time used : " + (System.currentTimeMillis() - ct) + " ms.");
		}finally{
			HibernateUtils.destroy();
		}
	}

}
