/*
 *  _ReadingBean.java
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
 */
package com.liusoft.dlog4j.base;

import java.util.Date;
import java.util.List;

import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 阅读对象的基类,例如日记/照片/话题
 * @see com.liusoft.dlog4j.base._DiaryBase
 * @see com.liusoft.dlog4j.base._PhotoBase
 * @see com.liusoft.dlog4j.base._TopicBeanBase
 * @author liudong
 */
public abstract class _ReadingBean extends _MultipleSiteEnabledBean {

	private UserBean owner;	//对象所有者

	private String title;		//对象标题
	private String keyword;		//关键字,Tag,Keyword
	private Date modifyTime;	//最近一次修改时间
	
	private int viewCount;	//阅读次数
	private int replyCount;	//评论数

	private Date createTime;	//创建时间

	private Date lastReplyTime;	//最后一次评论时间
	private Date lastReadTime;	//最后一次阅读时间
	
	private int status;	//对象状态

	private List tags;		//标签
	private List replies;	//评论

	protected ClientInfo client;

	public List getKeywords() {
		return StringUtils.stringToList(keyword);
	}
	
	public int getReplyCount() {
		return replyCount;
	}
	
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	
	public int getViewCount() {
		return viewCount;
	}
	
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	
	public int incReplyCount(int count){
		this.replyCount += count;
		if(this.replyCount<0)
			this.replyCount = 0;
		return this.replyCount;
	}
	
	public int incViewCount(int count){
		this.viewCount += count;
		if(this.viewCount<0)
			this.viewCount = 0;
		return this.viewCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public UserBean getOwner() {
		return owner;
	}

	public void setOwner(UserBean owner) {
		this.owner = owner;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastReadTime() {
		return lastReadTime;
	}

	public void setLastReadTime(Date lastReadTime) {
		this.lastReadTime = lastReadTime;
	}

	public Date getLastReplyTime() {
		return lastReplyTime;
	}

	public void setLastReplyTime(Date lastReplyTime) {
		this.lastReplyTime = lastReplyTime;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public List getReplies() {
		return replies;
	}

	public void setReplies(List replies) {
		this.replies = replies;
	}

	public List getTags() {
		return tags;
	}

	public void setTags(List tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ClientInfo getClient() {
		if(client==null)
			client = new ClientInfo();
		return client;
	}

	public void setClient(ClientInfo client) {
		this.client = client;
	}

	public String getClientAddr() {
		return getClient().getAddr();
	}

	public int getClientType() {
		return getClient().getType();
	}

	public String getUserAgent() {
		return getClient().getUserAgent();
	}

	public void setClientAddr(String addr) {
		getClient().setAddr(addr);
	}

	public void setClientType(int type) {
		getClient().setType(type);
	}

	public void setUserAgent(String userAgent) {
		getClient().setUserAgent(userAgent);
	}
	
	
}
