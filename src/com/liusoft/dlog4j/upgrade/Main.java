/*
 *  Main.java
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
package com.liusoft.dlog4j.upgrade;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.liusoft.dlog4j.beans.BookmarkBean;
import com.liusoft.dlog4j.beans.CatalogBean;
import com.liusoft.dlog4j.beans.DiaryBean;
import com.liusoft.dlog4j.beans.DiaryOutlineBean;
import com.liusoft.dlog4j.beans.DiaryReplyBean;
import com.liusoft.dlog4j.beans.LinkBean;
import com.liusoft.dlog4j.beans.MessageBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.util.db.Hibernate;

/**
 * 迁移程序入口
 * @author liudong
 */
public class Main{

	static SiteBean site ;
	
	static HashMap userids = new HashMap();
	static HashMap catalogids = new HashMap();
	static HashMap logids = new HashMap();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int site_id = 1;
		try{
			site_id = Integer.parseInt(args[0]);
		}catch(Exception e){}
		System.out.println("============== Press any key to begin upgrade to site #"+site_id+" =================");
		System.in.read();
		site = new SiteBean(site_id);

		URL xml = Main.class.getResource("old_hibernate.cfg.xml");
		old_hb = Hibernate.init(xml.getPath());
		xml = Main.class.getResource("new_hibernate.cfg.xml");
		new_hb = Hibernate.init(xml.getPath());

		Session old_ssn = old_hb.getSession();
		Session new_ssn = new_hb.getSession();
		Transaction tx = new_ssn.beginTransaction();
		
		try{
			upgradeUsers(old_ssn, new_ssn);
			System.out.println("============== Users upgraded.=================");
			
			upgradeLinks(old_ssn, new_ssn);
			System.out.println("============== Links upgraded.=================");
			
			upgradeCatalogs(old_ssn, new_ssn);
			System.out.println("============== Catalogs upgraded.=================");
			
			upgradeLogs(old_ssn, new_ssn);
			System.out.println("============== Articles upgraded.=================");
			
			upgradeReplies(old_ssn, new_ssn);
			System.out.println("============== Replies upgraded.=================");

			upgradeBookmarks(old_ssn, new_ssn);
			System.out.println("============== Bookmarks upgraded.=================");
			
			upgradeMessages(old_ssn, new_ssn);
			System.out.println("============== Messages upgraded.=================");
			
			tx.commit();
			System.out.println("============== DLOG4J upgraded.=================");
		}catch(Exception e){
			e.printStackTrace();
			tx.rollback();
		}finally{
			old_hb.destroy();
			new_hb.destroy();
		}
				
	}
	
	/**
	 * 迁移日记分类
	 */
	protected static void upgradeCatalogs(Session old_ssn, Session new_ssn){
		Query q = old_ssn.createQuery("FROM CatalogForm c ORDER BY c.id ASC");
		List catalogs = q.list();
		
		for(int i=0;i<catalogs.size();i++){
			CatalogForm cform = (CatalogForm)catalogs.get(i);
			CatalogBean cbean = new CatalogBean();
			cbean.setSite(site);
			cbean.setCreateTime(new Date());
			cbean.setDetail(cform.getDetail());
			cbean.setName(cform.getName());
			cbean.setArticleCount(0);
			cbean.setSortOrder(cform.getOrder());
			if(cform.getType()==CatalogForm.TYPE_OWNER)
				cbean.setType(CatalogBean.TYPE_OWNER);
			else
				cbean.setType(CatalogBean.TYPE_GENERAL);
			new_ssn.save(cbean);
			catalogids.put(new Integer(cform.getId()), cbean);	
			System.out.println("Catalog: " + cform.getId() + " -> " + cbean.getId());
		}
	}
	
	/**
	 * 迁移日记
	 */
	protected static void upgradeLogs(Session old_ssn, Session new_ssn){
		Query q = old_ssn.createQuery("FROM LogForm c ORDER BY c.id ASC");
		List logs = q.list();
		
		for(int i=0;i<logs.size();i++){
			LogForm log = (LogForm)logs.get(i);
			DiaryBean dbean = new DiaryBean();
			dbean.setCatalog((CatalogBean)catalogids.get(new Integer(log.getCategory())));
			//owner
			dbean.setOwner((UserBean)userids.get(new Integer(log.getOwnerId())));
			if(StringUtils.isNotEmpty(log.getAuthor()))
				dbean.setAuthor(log.getAuthor());
			else
				dbean.setAuthor(log.getOwner().getDisplayName());
			dbean.setAuthorUrl(StringUtils.abbreviate(log.getAuthorUrl(),100));
			dbean.setClientType("HTML".equalsIgnoreCase(log.getClientType())?0:1);
			if(log.getContent().length()>20000)
				dbean.setContent(log.getContent().substring(0, 20000));
			else
				dbean.setContent(log.getContent());
			dbean.setCreateTime(log.getLogTime());
			dbean.setStatus(log.getStatus());
			if("ptcl".equalsIgnoreCase(log.getWeather()))
				dbean.setWeather("ptcloudy");
			else
				dbean.setWeather(log.getWeather());
			
			dbean.setViewCount(log.getViewCount());
			dbean.setReplyCount(log.getReplies().size());
			dbean.setReplyNotify(log.getReplyNotify());
			dbean.setTitle(log.getTitle());
			dbean.setSite(site);
			dbean.setRefUrl(log.getRefUrl());
			dbean.setMoodLevel(log.getMoodLevel());
			dbean.setKeyword(log.getSearchKey());
			dbean.setClientAddr("127.0.0.1");
			if(log.getStatus()==LogForm.STATUS_DELETED)
				dbean.setStatus(DiaryBean.STATUS_DELETED);
			else if(log.getStatus()==LogForm.STATUS_DRAFT)
				dbean.setStatus(DiaryBean.STATUS_DRAFT);
			else
				dbean.setStatus(DiaryBean.STATUS_NORMAL);
			
			if(dbean.getStatus()==DiaryBean.STATUS_NORMAL){
				dbean.getCatalog().incArticleCount(1);
				dbean.getOwner().getCount().incArticleCount(1);
			}
			
			new_ssn.save(dbean);
			logids.put(new Integer(log.getId()), new Integer(dbean.getId()));
			System.out.println("Diary: " + log.getId() + " -> " + dbean.getId());		
		}
	}
	
	/**
	 * 迁移评论
	 */
	protected static void upgradeReplies(Session old_ssn, Session new_ssn){
		Query q = old_ssn.createQuery("FROM ReplyForm c ORDER BY c.id ASC");
		List replies = q.list();
		
		for(int i=0;i<replies.size();i++){
			ReplyForm rform = (ReplyForm)replies.get(i);
			DiaryReplyBean rbean = new DiaryReplyBean();
			rbean.getClient().setAddr("127.0.0.1");
			
			rbean.setDiary(new DiaryOutlineBean(((Integer)logids.get(new Integer(rform.getLogId()))).intValue()));
			rbean.setUser((UserBean)userids.get(new Integer(rform.getAuthor().getId())));
			
			rbean.getUser().getCount().incArticleReply(1);
			
			rbean.setAuthor(rform.getAuthor().getDisplayName());
			rbean.setAuthorEmail(rform.getAuthor().getEmail());
			rbean.setAuthorURL(StringUtils.abbreviate(rform.getAuthor().getHomePage(),100));
			if(rform.getContent().length()>10000)
				rbean.setContent(rform.getContent().substring(0, 10000));
			else
				rbean.setContent(rform.getContent());
			rbean.setReplyTime(rform.getWriteTime());
			rbean.setSite(site);
			new_ssn.save(rbean);
			System.out.println("Reply: " + rform.getId() + " -> " + rbean.getId());		
		}			
	}
	
	/**
	 * 用户迁移
	 */
	protected static void upgradeUsers(Session old_ssn, Session new_ssn){
		Query q = old_ssn.createQuery("FROM UserForm u ORDER BY u.id ASC");
		List users = q.list();
		
		for(int i=0;i<users.size();i++){
			UserForm user = (UserForm)users.get(i);	
			UserBean ubean = new UserBean();
			//导入到指定的site
			ubean.setSite(site);
			ubean.setName(user.getLoginName());
			ubean.setNickname(user.getDisplayName());
			ubean.setPassword(user.getPassword());
			ubean.setRegTime(new Timestamp(user.getRegTime().getTime()));
			ubean.setResume(user.getResume());
			ubean.setEmail(user.getEmail());
			ubean.setHomePage(StringUtils.abbreviate(user.getHomePage(),50));
			ubean.setLastAddr(user.getLastAddr());
			if(user.getLastTime()!=null)
				ubean.setLastTime(new Timestamp(user.getLastTime().getTime()));
			new_ssn.save(ubean);
			//保存新旧用户的编号对应关系
			userids.put(new Integer(user.getId()), ubean);
			System.out.println("User: " + user.getId() + " -> " + ubean.getId());
		}
	}

	/**
	 * 友情链接迁移
	 */
	protected static void upgradeLinks(Session old_ssn, Session new_ssn){
		Query q = old_ssn.createQuery("FROM LinkForm l ORDER BY l.id ASC");
		List links = q.list();		
		for(int i=0;i<links.size();i++){
			LinkForm link = (LinkForm)links.get(i);	
			LinkBean lbean = new LinkBean();
			lbean.setCreateTime(link.getCreateTime());
			lbean.setSiteId(site.getId());
			lbean.setSortOrder(link.getOrder());
			lbean.setTitle(link.getTitle());
			lbean.setUrl(link.getUrl());
			if("HTML".equalsIgnoreCase(link.getMode()))
				lbean.setType(LinkBean.TYPE_HTML);
			else
				lbean.setType(LinkBean.TYPE_XML);
			new_ssn.save(lbean);
			System.out.println("LINK: " + link.getId() + " -> " + lbean.getId());
		}
	}
	
	/**
	 * 留言迁移
	 * @param old_ssn
	 * @param new_ssn
	 */
	protected static void upgradeMessages(Session old_ssn, Session new_ssn){
		Query q = old_ssn.createQuery("FROM MessageForm l ORDER BY l.id ASC");
		List msgs = q.list();		
		for(int i=0;i<msgs.size();i++){
			MessageForm msg = (MessageForm)msgs.get(i);	
			MessageBean mbean = new MessageBean();
			mbean.setContent(msg.getContent());
			mbean.setReadTime(msg.getReadTime());
			mbean.setSendTime(msg.getSendTime());
			mbean.setStatus(msg.getStatus());
			mbean.setFromUser((UserBean)userids.get(new Integer(msg.getFromUserId())));
			mbean.setToUser((UserBean)userids.get(new Integer(msg.getToUserId())));
			new_ssn.save(mbean);
			System.out.println("MESSAGE: " + msg.getId() + " -> " + mbean.getId());
		}
	}

	/**
	 * 书签迁移
	 * @param old_ssn
	 * @param new_ssn
	 */
	protected static void upgradeBookmarks(Session old_ssn, Session new_ssn){
		Query q = old_ssn.createQuery("FROM BookMarkBean l ORDER BY l.id ASC");
		List bmbs = q.list();		
		for(int i=0;i<bmbs.size();i++){
			BookMarkBean bm = (BookMarkBean)bmbs.get(i);	
			BookmarkBean mbean = new BookmarkBean();
			mbean.setCreateTime(bm.getCreateTime());
			mbean.setOwner((UserBean)userids.get(new Integer(bm.getUserId())));
			mbean.setSite(site);
			mbean.setTitle(bm.getLog().getTitle());
			mbean.setParentType(BookmarkBean.TYPE_DIARY);
			mbean.setParentId(((Integer)logids.get(new Integer(bm.getLog().getId()))).intValue());			
			new_ssn.save(mbean);
			System.out.println("BOOKMARK: " + bm.getId() + " -> " + mbean.getId());
		}
	}
	
	private static Hibernate old_hb;
	private static Hibernate new_hb;
	
}
