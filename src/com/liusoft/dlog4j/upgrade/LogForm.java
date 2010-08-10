/*
 *  LogForm.java
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

import java.util.Date;
import java.util.List;

/**
 * DLOG4J 2.0的日记
 * 
 * @author liudong
 */
public class LogForm {

	public final static int STATUS_NORMAL 	= 0x00; //正常日记
	public final static int STATUS_PENDING	= 0x01;	//待审批的日记
	public final static int STATUS_HIDDEN 	= 0x02;	//隐藏的日记
	public final static int STATUS_REJECT	= 0x03; //被驳回的日记
	public final static int STATUS_DELETED	= 0x04;	//已删除的日记
	public final static int STATUS_DRAFT	= 0x05;	//草稿状态的日记
	
	int id;
    String content = null;
    
	private int category;

	private String searchKey;

	private int viewCount;

	private int replyCount;

	private String clientType = "HTML";

	private Date deleteTime;

	private int replyNotify = 0;

	private int status = 0;

	UserForm owner;

	String author;

	String authorUrl;

	String title;

	Date logTime;

	String refUrl;

	String weather;

	int moodLevel = 3;
	
	List replies;

	public String getAuthor() {
		if ("".equals(author))
			return null;
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getOwnerId() {
		return (owner != null) ? owner.getId() : -1;
	}

	public void setOwnerId(int userid) {
		if (owner == null)
			owner = new UserForm();
		owner.setId(userid);
	}

	/**
	 * @return
	 */
	public String getOwnerName() {
		return owner.getDisplayName();
	}

	public String getAuthorUrl() {
		if ("".equals(authorUrl))
			return null;
		return authorUrl;
	}

	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public int getMoodLevel() {
		return moodLevel;
	}

	public void setMoodLevel(int moodLevel) {
		this.moodLevel = moodLevel;
	}

	public UserForm getOwner() {
		return owner;
	}

	public void setOwner(UserForm owner) {
		this.owner = owner;
	}

	public String getRefUrl() {
		if ("".equals(refUrl))
			return null;
		return refUrl;
	}

	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWeather() {
		if ("".equals(weather))
			return null;
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	/**
	 * @return
	 */
	public int getReplyCount() {
		return replyCount;
	}

	/**
	 * @return
	 */
	public int getViewCount() {
		return viewCount;
	}

	/**
	 * @param i
	 */
	public void setReplyCount(int i) {
		replyCount = i;
	}

	/**
	 * @param i
	 */
	public void setViewCount(int i) {
		viewCount = i;
	}

	/**
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param i
	 */
	public void setStatus(int i) {
		status = i;
	}

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public int getReplyNotify() {
		return replyNotify;
	}

	public void setReplyNotify(int replyNotify) {
		this.replyNotify = replyNotify;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List getReplies() {
		return replies;
	}

	public void setReplies(List replies) {
		this.replies = replies;
	}

}
