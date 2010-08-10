/*
 *  DLOG_Home_VelocityTool.java
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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.liusoft.dlog4j.DLOG_CacheManager;
import com.liusoft.dlog4j.base._BeanBase;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.dao.DlogDAO;
import com.liusoft.dlog4j.dao.PhotoDAO;
import com.liusoft.dlog4j.dao.SiteDAO;

/**
 * DLOG4J主页用的Toolbox
 * @author Winter Lau
 */
public class DLOG_Home_VelocityTool{

	private final static String CACHE_KEY = "dlog_home_info";
	
	/**
	 * 用于随机排序
	 */
	private final static Comparator random_comparator = new Comparator(){
		Random random_gen = new Random(System.currentTimeMillis());
		public int compare(Object arg0, Object arg1) {
			_BeanBase bean0 = (_BeanBase)arg0;
			_BeanBase bean1 = (_BeanBase)arg1;
			int r1 = random_gen.nextInt(50) + bean0.getId();
			int r2 = random_gen.nextInt(50) + bean1.getId();
			return r1 - r2;
		}		
	};
	
	/**
	 * 列出推荐的站点
	 * @param page
	 * @param count
	 * @return
	 */
	public List list_sites_recommend(int page, int count){
		StringBuffer nKey = new StringBuffer("recommend_sites_");
		nKey.append(page);
		nKey.append('_');
		nKey.append(count);
		List sites = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(sites == null){
			int fromIdx = (page - 1) * count;
			sites = SiteDAO.listRecommendSites(fromIdx, count);
			if(page < 10 && sites!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)sites);
		}
		return sites;
	}

	/**
	 * 列出最新发表的专栏文章
	 * @param days
	 * @param count
	 * @return
	 */
	public List list_new_articles(int page, int count){
		StringBuffer nKey = new StringBuffer("new_articles_");
		nKey.append(page);
		nKey.append('_');
		nKey.append(count);
		List articles = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(articles == null){
			int fromIdx = (page - 1) * count;			
			articles = DlogDAO.listNewArticles(fromIdx, count);
			if(page < 5 && articles!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)articles);
		}
		return articles;
	}

	/**
	 * 列出照片
	 * @param page
	 * @param count
	 * @return
	 */
	public List list_new_photos(int page, int count){
		StringBuffer nKey = new StringBuffer("new_photos_");
		nKey.append(page);
		nKey.append('_');
		nKey.append(count);
		List photos = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(photos==null){
			int fromIdx = (page-1)*count;
			if(fromIdx < 0)
				fromIdx = 0;
			photos = PhotoDAO.listPhotos(-1, -1, -1, fromIdx, count);
			if(page < 10 && photos!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(),(Serializable)photos);			
		}
		return photos;
	}
	
	/**
	 * 列出最热门的专栏文章
	 * @param days
	 * @param count
	 * @return
	 */
	public List list_hot_articles(int days, int count){
		StringBuffer nKey = new StringBuffer("hot_articles_");
		nKey.append(days);
		nKey.append('_');
		nKey.append(count);
		List articles = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(articles == null){			
			articles = DlogDAO.listHotArticles(days, count);
			if(articles==null || articles.size()==0){
				articles = DlogDAO.listHotArticles(days * 100, count);
			}
			else
			if(articles.size() < count){
				articles = DlogDAO.listHotArticles(days * 100, count);
			}
			if(articles!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)articles);
		}
		Collections.sort(articles, random_comparator);
		return articles;
	}

	/**
	 * 列出最热门的专栏文章
	 * @param days
	 * @param count
	 * @return
	 */
	public List list_hot_photos(int days, int count){
		StringBuffer nKey = new StringBuffer("hot_photos_");
		nKey.append(days);
		nKey.append('_');
		nKey.append(count);
		List photos = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(photos == null){
			photos = DlogDAO.listHotPhotos(days, count);
			if(photos==null || photos.size()==0){
				photos = DlogDAO.listHotPhotos(days * 100, count);
			}
			if(photos!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)photos);
		}
		Collections.sort(photos, random_comparator);
		return photos;
	}

	/**
	 * 列出最热门的帖子
	 * @param days
	 * @param count
	 * @return
	 */
	public List list_hot_topics(int days, int count){
		StringBuffer nKey = new StringBuffer("hot_topics_");
		nKey.append(days);
		nKey.append('_');
		nKey.append(count);
		List topics = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(topics == null){
			topics = DlogDAO.listHotTopics(days, count);
			if(topics==null || topics.size()==0){
				topics = DlogDAO.listHotTopics(days * 100, count);
			}
			if(topics!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)topics);
		}
		Collections.sort(topics, random_comparator);
		return topics;
	}
	
	/**
	 * 列出日记最为活跃的网站
	 * @param count
	 * @return
	 */
	public List list_hot_sites_via_diary(int page, int count){
		StringBuffer nKey = new StringBuffer("hot_diary_sites_");
		nKey.append(page);
		nKey.append('_');
		nKey.append(count);
		List sites = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(sites == null){
			int fromIdx = (page - 1) * count;
			sites = SiteDAO.listHotSitesViaDiary(fromIdx, count);
			if(page < 10 && sites!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)sites);
		}
		return sites;
	}

	/**
	 * 列出相册最为活跃的网站
	 * @param count
	 * @return
	 */
	public List list_hot_sites_via_photo(int page, int count){
		StringBuffer nKey = new StringBuffer("hot_photo_sites_");
		nKey.append(page);
		nKey.append('_');
		nKey.append(count);
		List sites = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(sites == null){
			int fromIdx = (page - 1) * count;
			sites = SiteDAO.listHotSitesViaPhoto(fromIdx, count);
			if(page < 10 && sites!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)sites);
		}
		return sites;
	}

	/**
	 * 列出讨论最为活跃的网站
	 * @param count
	 * @return
	 */
	public List list_hot_sites_via_topic(int page, int count){
		StringBuffer nKey = new StringBuffer("hot_topic_sites_");
		nKey.append(page);
		nKey.append('_');
		nKey.append(count);
		List sites = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(sites == null){
			int fromIdx = (page - 1) * count;
			sites = SiteDAO.listHotSitesViaTopic(fromIdx, count);
			if(page < 10 && sites!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)sites);
		}
		return sites;
	}
	
	/**
	 * 列出最新注册的site
	 * @param count
	 * @return
	 */
	public List list_newest_sites(int page, int count){
		StringBuffer nKey = new StringBuffer("new_sites_");
		nKey.append(page);
		nKey.append('_');
		nKey.append(count);
		List sites = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(sites == null){
			int fromIdx = (page - 1) * count;		
			sites = SiteDAO.listNewestSites(fromIdx, count);
			if(page < 10 && sites!=null)
				DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)sites);
		}
		return sites;
	}

	/**
	 * 列出最新注册的site
	 * @param page
	 * @param count
	 * @param ofield
	 * @return
	 */
	public List list_sites_order_by(int page, int count, String ofield){
		int fromIdx = (page - 1) * count;
		if("user".equalsIgnoreCase(ofield))
			return SiteDAO.listSitesOrderByUserCount(fromIdx, count);
		if("diary".equalsIgnoreCase(ofield))
			return SiteDAO.listSitesOrderByDiaryCount(fromIdx, count);
		if("photo".equalsIgnoreCase(ofield))
			return SiteDAO.listSitesOrderByPhotoCount(fromIdx, count);
		if("music".equalsIgnoreCase(ofield))
			return SiteDAO.listSitesOrderByMusicCount(fromIdx, count);
		if("topic".equalsIgnoreCase(ofield))
			return SiteDAO.listSitesOrderByTopicCount(fromIdx, count);
		return list_newest_sites(page, count);
	}
	
	/**
	 * 读取热门的标签
	 * @param site
	 * @param count
	 * @return
	 */
	public List list_hot_tags(int site, int count){
		StringBuffer nKey = new StringBuffer("hot_tags_");
		nKey.append(site);
		nKey.append('_');
		nKey.append(count);
		List tags = (List)DLOG_CacheManager.getObjectCached(HOT_TAGS_KEY, nKey.toString());
		if(tags == null){
			tags = DlogDAO.listHotTags(site, count);
			if(tags!=null)
				DLOG_CacheManager.putObjectCached(HOT_TAGS_KEY, nKey.toString(), (Serializable)tags);
		}
		return tags;
	}
	
	/**
	 * 从缓存中得到某个网站的所有友情链接
	 * @param site_id
	 * @return
	 */
	public List list_links_of_site(int site_id){
		StringBuffer nKey = new StringBuffer("links_");
		nKey.append(site_id);
		List links = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(links == null){
			SiteBean site = SiteDAO.getSiteByID(site_id);
			if(site!=null){
				links = site.getLinks();
				if(links!=null)
					DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey.toString(), (Serializable)links);
			}
		}
		return links;
	}
	
	private final static String HOT_TAGS_KEY = "DLOG4J_hot_tags";
}
