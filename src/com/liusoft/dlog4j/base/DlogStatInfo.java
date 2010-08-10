/*
 *  DlogStatInfo.java
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
 * 整个系统的统计信息
 * @author Winter Lau
 */
public class DlogStatInfo implements Serializable {

	protected int article;
	protected int articleReply;
	protected int topic;
	protected int topicReply;
	protected int photo;
	protected int photoReply;
	
	protected int user;
	protected int site;
	
	public int getReplyCount(){
		return articleReply + photoReply + topicReply;
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
	public int getSite() {
		return site;
	}
	public void setSite(int site) {
		this.site = site;
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
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
}
