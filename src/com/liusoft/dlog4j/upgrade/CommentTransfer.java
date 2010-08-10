/*
 *  CommentTransfer.java
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
 *  2006-7-22
 */
package com.liusoft.dlog4j.upgrade;

import java.util.List;

import org.hibernate.Session;

import com.liusoft.dlog4j.base.AuthorInfo;
import com.liusoft.dlog4j.beans.CommentBean;
import com.liusoft.dlog4j.beans.DiaryReplyBean;
import com.liusoft.dlog4j.beans.PhotoReplyBean;
import com.liusoft.dlog4j.beans.TopicReplyBean;
import com.liusoft.dlog4j.db.HibernateUtils;

/**
 * 合并评论到新的表中dlog_comments
 * @author liudong
 */
public class CommentTransfer {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		HibernateUtils.init();
		try{
			transfer_diary_replies();
			transfer_photo_replies();
			transfer_bbs_replies();
		}finally{
			HibernateUtils.destroy();
		}
	}
	
	/**
	 * 迁移日记评论
	 * @throws Exception
	 */
	protected static void transfer_diary_replies() throws Exception{
		Session ssn = HibernateUtils.getSession();
		try{
			HibernateUtils.beginTransaction();
			List<DiaryReplyBean> rpls = ssn.createQuery("FROM DiaryReplyBean AS r ORDER BY r.id").list();
			for(DiaryReplyBean rpl : rpls){
				CommentBean cb = new CommentBean();
				cb.setClient(rpl.getClient());
				AuthorInfo author = new AuthorInfo();
				author.setEmail(rpl.getAuthorEmail());
				author.setName(rpl.getAuthor());
				author.setUrl(rpl.getAuthorURL());
				if(rpl.getUser()!=null){
					author.setId(rpl.getUser().getId());
				}
				cb.setAuthor(author);
				cb.setContent(rpl.getContent());
				cb.setCreateTime(rpl.getReplyTime());
				cb.setEid(rpl.getDiary().getId());
				cb.setEtype(DiaryReplyBean.TYPE_DIARY);
				cb.setSite(rpl.getSite());
				cb.setStatus(rpl.getStatus());
				cb.setTitle(StringUtils.abbreviate(rpl.getContent(), 20));
				ssn.save(cb);
				System.out.println("DiaryReplyBean: " + rpl.getId() + " -> " + cb.getId());
			}
			HibernateUtils.commit();
		}catch(Exception e){
			HibernateUtils.rollback();
			throw e;
		}
	}

	/**
	 * 迁移相册评论
	 * @throws Exception
	 */
	protected static void transfer_photo_replies() throws Exception{
		Session ssn = HibernateUtils.getSession();
		try{
			HibernateUtils.beginTransaction();
			List<PhotoReplyBean> rpls = ssn.createQuery("FROM PhotoReplyBean AS r ORDER BY r.id").list();
			for(PhotoReplyBean rpl : rpls){
				CommentBean cb = new CommentBean();
				cb.setClient(rpl.getClient());
				AuthorInfo author = new AuthorInfo();
				author.setEmail(rpl.getAuthorEmail());
				author.setUrl(rpl.getAuthorURL());
				author.setName(rpl.getAuthor());
				if(rpl.getUser()!=null){
					author.setId(rpl.getUser().getId());
				}
				cb.setAuthor(author);
				cb.setContent(rpl.getContent());
				cb.setCreateTime(rpl.getReplyTime());
				cb.setEid(rpl.getPhoto().getId());
				cb.setEtype(DiaryReplyBean.TYPE_PHOTO);
				cb.setSite(rpl.getSite());
				cb.setStatus(rpl.getStatus());
				cb.setTitle(StringUtils.abbreviate(rpl.getContent(), 20));
				ssn.save(cb);
				System.out.println("PhotoReplyBean: " + rpl.getId() + " -> " + cb.getId());
			}
			HibernateUtils.commit();
		}catch(Exception e){
			HibernateUtils.rollback();
			throw e;
		}
	}
	/**
	 * 迁移论坛评论
	 * @throws Exception
	 */
	protected static void transfer_bbs_replies() throws Exception{
		Session ssn = HibernateUtils.getSession();
		try{
			HibernateUtils.beginTransaction();
			List<TopicReplyBean> rpls = ssn.createQuery("FROM TopicReplyBean AS r ORDER BY r.id").list();
			for(TopicReplyBean rpl : rpls){
				CommentBean cb = new CommentBean();
				cb.setClient(rpl.getClient());
				AuthorInfo author = new AuthorInfo();
				author.setEmail(rpl.getAuthorEmail());
				author.setName(rpl.getAuthor());
				author.setUrl(rpl.getAuthorURL());
				if(rpl.getUser()!=null){
					author.setName(rpl.getUser().getNickname());
					author.setId(rpl.getUser().getId());
				}
				else
					author.setName("匿名");
				cb.setAuthor(author);
				cb.setContent(rpl.getContent());
				cb.setCreateTime(rpl.getReplyTime());
				cb.setEid(rpl.getTopic().getId());
				cb.setEtype(DiaryReplyBean.TYPE_BBS);
				cb.setSite(rpl.getSite());
				cb.setStatus(rpl.getStatus());
				cb.setTitle(rpl.getTitle());
				ssn.save(cb);
				System.out.println("TopicReplyBean: " + rpl.getId() + " -> " + cb.getId());
			}
			HibernateUtils.commit();
		}catch(Exception e){
			HibernateUtils.rollback();
			throw e;
		}
	}
}
