/*
 *  CountInfo.java
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
package com.liusoft.dlog4j.base;

import java.io.Serializable;

/**
 * 参与数字信息
 * @author Winter Lau
 * @see com.liusoft.dlog4j.beans.UserBean
 */
public class CountInfo implements Serializable{

	protected int article;
	protected int articleReply;
	protected int topic;
	protected int topicReply;
	protected int photo;
	protected int photoReply;
	protected int guestbook;
	protected int bookmark;	
	
	public Object clone(){
		CountInfo ci = new CountInfo();
		ci.setArticle(article);
		ci.setArticleReply(articleReply);
		ci.setTopic(topic);
		ci.setTopicReply(topicReply);
		ci.setPhoto(photo);
		ci.setPhotoReply(photoReply);
		ci.setGuestbook(guestbook);
		ci.setBookmark(bookmark);
		return ci;
	}

	public int incArticleCount(int count){
		this.article += count;
		if(this.article<0)
			this.article = 0;
		//setArticle(article);
		return this.article;
	}
	
	public int incArticleReply(int count){
		this.articleReply += count;
		if(this.articleReply<0)
			this.articleReply = 0;
		//setArticleReply(articleReply);
		return this.articleReply;
	}
	
	public int incTopicCount(int count){
		this.topic += count;
		if(this.topic<0)
			this.topic = 0;
		return this.topic;
	}
	
	public int incTopicReplyCount(int count){
		this.topicReply += count;
		if(this.topicReply<0)
			this.topicReply = 0;
		return this.topicReply;
	}
	
	public int incPhotoCount(int count){
		this.photo += count;
		if(this.photo<0)
			this.photo = 0;
		return this.photo;
	}
	
	public int incPhotoReplyCount(int count){
		this.photoReply += count;
		if(this.photoReply<0)
			this.photoReply = 0;
		return this.photoReply;
	}
	
	public int incBookmarkCount(int count){
		this.bookmark += count;
		if(this.bookmark<0)
			this.bookmark = 0;
		return this.bookmark;
	}
	
	public int incGuestbookCount(int count){
		this.guestbook += count;
		if(this.guestbook<0)
			this.guestbook = 0;
		return this.guestbook;
	}
	
	public int getArticle() {
		return article;
	}
	public void setArticle(int article) {
		this.article = article;
	}
	public int getArticleReply() {
		return articleReply;
	}
	public void setArticleReply(int articleReply) {
		this.articleReply = articleReply;
	}
	public int getGuestbook() {
		return guestbook;
	}
	public void setGuestbook(int guestbook) {
		this.guestbook = guestbook;
	}
	public int getPhoto() {
		return photo;
	}
	public void setPhoto(int photo) {
		this.photo = photo;
	}
	public int getPhotoReply() {
		return photoReply;
	}
	public void setPhotoReply(int photoReply) {
		this.photoReply = photoReply;
	}
	public int getTopic() {
		return topic;
	}
	public void setTopic(int topic) {
		this.topic = topic;
	}
	public int getTopicReply() {
		return topicReply;
	}
	public void setTopicReply(int topicReply) {
		this.topicReply = topicReply;
	}
	public int getBookmark() {
		return bookmark;
	}
	public void setBookmark(int bookmark) {
		this.bookmark = bookmark;
	}
	
}
