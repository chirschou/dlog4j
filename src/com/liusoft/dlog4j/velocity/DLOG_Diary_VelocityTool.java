/*
 *  DLOG_Diary_VelocityTool.java
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
 *  Author: Winter Lau
 *  http://dlog4j.sourceforge.net
 *  
 */
package com.liusoft.dlog4j.velocity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;

import com.liusoft.dlog4j.DLOG_CacheManager;
import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.TextCacheManager;
import com.liusoft.dlog4j.base._DiaryBase;
import com.liusoft.dlog4j.beans.CatalogBean;
import com.liusoft.dlog4j.beans.DiaryBean;
import com.liusoft.dlog4j.beans.DiaryOutlineBean;
import com.liusoft.dlog4j.beans.DiaryReplyBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.dao.CatalogDAO;
import com.liusoft.dlog4j.dao.DiaryDAO;
import com.liusoft.dlog4j.dao.FCKUploadFileDAO;
import com.liusoft.dlog4j.dao.ReplyDAO;

/**
 * 日记相关的Toolbox类
 * @author liudong
 */
public class DLOG_Diary_VelocityTool{
	
	final static Log log = LogFactory.getLog(DLOG_Diary_VelocityTool.class);

	private final static String CACHE_KEY = "dlog_home_info";
	
	/**
	 * 列出最热门的专栏文章
	 * @param days
	 * @param count
	 * @return
	 */
	public List list_hot_articles(SiteBean site, int days, int count){
		if(site==null)
			return null;
		StringBuffer nKey = new StringBuffer("hot_articles_");
		nKey.append(site.getId());
		nKey.append('_');
		nKey.append(days);
		nKey.append('_');
		nKey.append(count);
		List articles = (List)DLOG_CacheManager.getObjectCached(CACHE_KEY, nKey.toString());
		if(articles == null){
			articles = DiaryDAO.listHotArticles(site.getId(), days, count);
			if(articles==null || articles.size()==0){
				articles = DiaryDAO.listHotArticles(site.getId(), days * 100, count);
			}
			else
			if(articles.size() < count){
				List others = DiaryDAO.listHotArticlesBefore(site.getId(),
						days, count - articles.size());
				articles.addAll(others);
			}
			DLOG_CacheManager.putObjectCached(CACHE_KEY, nKey, (Serializable)articles);
		}
		return articles;
	}

	/**
	 * 列出当前已经上传的附件
	 * @param session_id
	 * @return
	 */
	public List attachments(SessionUserObject user, String session_id){
		if(user == null)
			return null;
		return FCKUploadFileDAO.listOrphanFiles(user.getId(), session_id);
	}
	
	/**
	 * 访问某个评论内容
	 * @param site
	 * @param reply_id
	 * @return
	 */
	public DiaryReplyBean reply(int reply_id){
		if(reply_id < 0)
			return null;
		return (DiaryReplyBean)ReplyDAO.getReply(DiaryReplyBean.class, reply_id);
	}
	
	/**
	 * 判断用户可否有访问指定日记的权限并返回日记详细资料(showlog.vm)
	 * 
	 * @param site
	 * @param user
	 * @param log_id
	 * @return
	 * @throws HibernateException
	 */
	public _DiaryBase diary(SiteBean site, SessionUserObject user, int log_id) {
		if (site == null || log_id < 0)
			return null;
		// 如何将日记的内容进行缓存,避免从数据库直接读取大文本
		String text = TextCacheManager.getTextContent(DiaryBean.TYPE_DIARY, log_id);
		_DiaryBase diary = null;
		if(text==null){
			diary = DiaryDAO.getDiaryByID(log_id);
			if(diary!=null && diary.getStatus()==DiaryBean.STATUS_NORMAL){
				TextCacheManager.updateTextContent(DiaryBean.TYPE_DIARY, log_id, diary.getContent());
			}
		}
		else{
			diary = DiaryDAO.getDiaryOutlineByID(log_id);
			if(diary!=null)
				diary.setContent(text);
		}
		if (diary == null || diary.getSite().getId() != site.getId())
			return null;
		if(user!=null && diary.getOwner().getId()==user.getId())
			return diary;
		if (!CatalogDAO.canUserViewThisCatalog(diary.getCatalog(), user))
			return null;
		return diary;
	}

	/**
	 * 直接读取某一篇日记
	 * 
	 * @param log_id
	 * @return
	 * @throws HibernateException
	 */
	public _DiaryBase diary(int log_id) {
		if (log_id < 0)
			return null;
		String text = TextCacheManager.getTextContent(DiaryBean.TYPE_DIARY, log_id);
		_DiaryBase diary = null;
		if(text==null){
			diary = DiaryDAO.getDiaryByID(log_id);
			if(diary!=null && diary.getStatus()==DiaryBean.STATUS_NORMAL){
				TextCacheManager.updateTextContent(DiaryBean.TYPE_DIARY, log_id, diary.getContent());
			}
		}
		else{
			diary = DiaryDAO.getDiaryOutlineByID(log_id);
			if(diary!=null)
				diary.setContent(text);
		}
		return diary;
	}
	
	/**
	 * 填充日记内容
	 * @param diary
	 */
	public void fill_diary_content(_DiaryBase diary){		
		String text = TextCacheManager.getTextContent(DiaryBean.TYPE_DIARY, diary.getId());
		if(text==null){
			DiaryBean db = DiaryDAO.getDiaryByID(diary.getId());
			diary.setContent(db.getContent());
			if(diary!=null && diary.getStatus()==DiaryBean.STATUS_NORMAL){
				TextCacheManager.updateTextContent(DiaryBean.TYPE_DIARY, diary.getId(), diary.getContent());
			}
		}
		else{
			diary.setContent(text);
		}
	}
	
	/**
	 * 获取上一篇日记(showlog.vm)
	 * @param site
	 * @param user
	 * @param catalog_id
	 * @param log_id
	 * @return
	 */
	public DiaryOutlineBean prev_diary(SiteBean site, SessionUserObject user, int catalog_id, int log_id){
		if (site == null || log_id < 0)
			return null;
		try{
			return DiaryDAO.getPrevDiary(site,user,catalog_id,log_id);
		}catch(Exception e){
			log.error("DLOG_VelocityTool.prev_diary execute failed.", e);
		}
		return null;
	}

	/**
	 * 获取下一篇日记(showlog.vm)
	 * @param site
	 * @param user
	 * @param catalog_id
	 * @param log_id
	 * @return
	 */
	public DiaryOutlineBean next_diary(SiteBean site, SessionUserObject user, int catalog_id, int log_id){
		if (site == null || log_id < 0)
			return null;
		try{
			return DiaryDAO.getNextDiary(site, user, catalog_id, log_id);
		}catch(Exception e){
			log.error("DLOG_VelocityTool.next_diary execute failed.", e);
		}
		return null;
	}
	
	/**
	 * 获取某个网站最新的一篇日记
	 * @param site
	 * @param user
	 * @param fromIdx
	 * @param count
	 * @param withContent
	 * @return
	 */
	public List top_diary(SiteBean site, SessionUserObject user, int page, int count, boolean withContent) {
		if (site == null)
			return null;
		return list_diary(site,user,-1,-1,-1,-1,page, count, withContent);
	}

	/**
	 * 列出所有上传的文件（用于管理）
	 * @param page
	 * @param count
	 * @return
	 */
	public List list_files(int page, int count){
		int fromIdx = (page-1)*count;
		return FCKUploadFileDAO.listFiles(fromIdx, count);
	}
	
	public int file_count(){
		return FCKUploadFileDAO.fileCount();
	}

	/**
	 * 根据要求的条件读取日记(包括日记内容)
	 * 
	 * @param site
	 * @param user
	 * @param catalog_id
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List list_diary(SiteBean site, SessionUserObject user, int catalog_id, int year,int month,int date,
			int page, int pageSize) {
		return list_diary(site, user, catalog_id, year,month,date, page, pageSize, true);
	}

	/**
	 * 根据要求的条件读取日记
	 * 
	 * @param site
	 * @param user
	 * @param catalog_id
	 * @param page
	 * @param pageSize
	 * @param withContent 是否需要包含日记详细内容
	 * @return
	 */
	public List list_diary(SiteBean site, SessionUserObject user, int catalog_id, int year,int month,int date,
			int page, int pageSize, boolean withContent) {
		int fromIdx = (page - 1) * pageSize;
		if (fromIdx < 0)
			fromIdx = 0;
		if (pageSize < 1)
			pageSize = 5;
		if (site == null)
			return DiaryDAO.listDiary(year, month, date, fromIdx, pageSize, withContent);
		else
			return DiaryDAO.listDiary(site, user, catalog_id, year,month,date, fromIdx, pageSize, withContent);
	}

	/**
	 * 计算日记数
	 * 
	 * @param site
	 * @param user
	 * @param catalog_id
	 * @return
	 */
	public int diary_count(SiteBean site, SessionUserObject user, int catalog_id, int year, int month, int date) {
		if (site == null)
			return -1;
		return DiaryDAO.getDiaryCount(site, user, catalog_id, year,month,date);
	}
	
	/**
	 * 返回总的日记数，不包括隐藏日记
	 * @return
	 */
	public int public_diary_count(){
		return DiaryDAO.getPublicDiaryCount();
	}
	
	/**
	 * 分页列出某篇日记的评论
	 * @param site
	 * @param user
	 * @param diary
	 * @param page
	 * @param pageSize
	 * @param reverse 是否倒序,如果该值为true则最新评论排在最前面
	 * @return
	 */
	public List replies(SiteBean site, SessionUserObject user, _DiaryBase diary, int page, int pageSize, boolean reverse){
		if (site == null)
			return null;
		if (diary == null || diary.getSite().getId() != site.getId())
			return null;
		if (!CatalogDAO.canUserViewThisCatalog(diary.getCatalog(), user))
			return null;
		int fromIdx = (page - 1) * pageSize;
		if(fromIdx < 0)
			fromIdx = 0;
		return DiaryDAO.listDiaryReplies(diary.getId(), fromIdx, pageSize, reverse);
	}

	/**
	 * 判断用户是否有编辑某篇日记的权限
	 * @param user
	 * @param diary
	 * @return
	 */
	public boolean can_user_edit_diary(SessionUserObject user, _DiaryBase diary){
		if(user==null || diary==null)
			return false;
		if(diary.getOwner().getId()==user.getId())
			return true;
		if(diary.getSite().isOwner(user))
			return true;
		return false;
	}
	
	/**
	 * 列出某个网站的最新日记评论(_diary_top_info.vm, j_replies.vm)
	 * 
	 * @param site
	 * @param user
	 * @param last_reply_id
	 * @param count
	 * @return
	 */
	public List list_diary_replies(SiteBean site, int page, int count, SessionUserObject user) {
		if (site == null)
			return null;
		int fromidx = (page - 1) * count;
		return DiaryDAO.listDiaryReplies(site, fromidx, count, user);
	}

	/**
	 * 获取日记评论总数(j_replies.vm)
	 * @param site
	 * @param user
	 * @return
	 */
	public int diary_reply_count(SiteBean site, SessionUserObject user){
		if(site==null) 
			return -1;
		return DiaryDAO.getDiaryReplyCount(site, user);
	}

	/**
	 * 统计某个月历每天的日记数(_diary_calendar.vm)
	 * @param year
	 * @param month
	 * @return 结果集合中的第一个元素是该月的日记总数外,其他是对应每天的日记数
	 */
	public List diary_counts_by_month(SiteBean site, SessionUserObject user, int year, int month){
		if(site==null) 
			return null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		int[] logc = DiaryDAO.statCalendarLogs(site, user, cal);
		List logcs = new ArrayList();
		for(int i=0;i<logc.length;i++){
			logcs.add(new Integer(logc[i]));
		}
		return logcs;
	}
	
	/**
	 * 得到垃圾箱中日记数(_catalog_and_calendar.vm, trash.vm)
	 * @param site
	 * @param user
	 * @return
	 */
	public int trash_count(SiteBean site, SessionUserObject user){
		if(site==null || user==null)
			return -1;
		if(!site.isOwner(user))
			return -1;
		return DiaryDAO.getTrashCount(site.getId());
	}
	
	/**
	 * 列出垃圾箱中的所有日记(trash.vm)
	 * @param site
	 * @param user
	 * @return
	 */
	public List list_trash(SiteBean site, SessionUserObject user){
		return list_trash(site, user, 1, 0);
	}

	/**
	 * 列出垃圾箱中的所有日记(trash.vm)
	 * @param site
	 * @param user
	 * @return
	 */
	public List list_trash(SiteBean site, SessionUserObject user, int page, int pageSize){
		if(site==null || user==null)
			return null;
		if(!site.isOwner(user))
			return null;
		int fromIdx = (page - 1) * pageSize;
		return DiaryDAO.listTrash(site.getId(), fromIdx, pageSize);
	}
	
	/**
	 * 得到草稿数(_catalog_and_calendar.vm, drafts.vm) 
	 * @param site
	 * @param user
	 * @return
	 */
	public int draft_count(SiteBean site, SessionUserObject user){
		if(site==null || user==null) return -1;
		return DiaryDAO.getDraftCount(site, user.getId());
	}
	
	/**
	 * 得到所有的草稿(drafts.vm)
	 * @param site
	 * @param user
	 * @return
	 */
	public List list_drafts(SiteBean site, SessionUserObject user){
		return list_drafts(site, user, 1, 0);
	}

	public List list_drafts(SiteBean site, SessionUserObject user, int page, int pageSize){
		if(site==null || user==null) return null;
		int fromIdx = (page - 1) * pageSize;		
		return DiaryDAO.listDrafts(site, user.getId(), fromIdx, pageSize);
	}
	
	/**
	 * 访问日记，增加日记的阅读数
	 * @param site
	 * @param user
	 * @param log
	 */
	public void visit_diary(SiteBean site, SessionUserObject user, _DiaryBase diary){
		if(diary!=null && diary.getSite().getId()==site.getId()){
			try{
				DiaryDAO.incViewCount(diary.getId(), 1);
			}catch(Exception e){
				log.error("DLOG_VelocityTool.visit_diary failed.", e);
			}
		}
	}

	/**
	 * 列出指定网站的所有日记分类
	 * 
	 * @param site_id
	 * @param user
	 * @param maintain
	 * @return
	 * @throws HibernateException
	 */
	public List catalogs(SiteBean site, SessionUserObject user, boolean maintain) {
		if (site == null)
			return null;
		if (site.isOwner(user))
			return site.getCatalogs();
		else {
			return CatalogDAO.listCatalogs(site, user, maintain);
		}
	}

	/**
	 * 查询用户在某个站点上可写日记的分类数(_catalog_and_calendar.vm)
	 * @param site
	 * @param user
	 * @return
	 */
	public boolean user_can_blog(SiteBean site, SessionUserObject user){
		if(site == null || user == null)
			return false;
		return CatalogDAO.userCanBlog(site, user);
	}

	/**
	 * 获取某分类的详细资料
	 * @param site
	 * @param user
	 * @param cat_id
	 * @return
	 */
	public CatalogBean catalog(SiteBean site, SessionUserObject user, int cat_id){
		if(site==null || cat_id < 1)
			return null;
		CatalogBean catalog = CatalogDAO.getCatalogByID(cat_id);
		if (catalog != null && catalog.getSite().getId() == site.getId()
				&& CatalogDAO.canUserViewThisCatalog(catalog, user))
			return catalog;
		return null;
	}

	/**
	 * 列出在某个日记分类拥有特殊权限的所有用户
	 * @param site
	 * @param user
	 * @param catalog
	 * @return
	 */
	public List list_special_popedom_users_by_catalog(SiteBean site, SessionUserObject user, CatalogBean catalog){
		if(site==null||user==null||catalog==null)
			return null;
		if(site.isOwner(user) && site.getId()==catalog.getSite().getId())
			return CatalogDAO.listSpecialPopedomUsersByCatalog(catalog.getId());
		return null;
	}
	
}
