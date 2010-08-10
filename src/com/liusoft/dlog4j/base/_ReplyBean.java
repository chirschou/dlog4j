/*
 *  ReplyBean.java
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
 *  
 */
package com.liusoft.dlog4j.base;

import java.util.Date;

import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.search.SearchEnabled;

/**
 * 评论对象的基类
 * 
 * @author Winter Lau
 */
public abstract class _ReplyBean extends _MultipleSiteEnabledBean implements
		SearchEnabled {

	protected String author; // 评论者名称
	protected String authorURL; // 评论者网址
	protected String authorEmail; // 评论者邮箱
	protected String content; // 评论内容
	protected Date replyTime; // 评论时间
	protected ClientInfo client; // 客户端信息
	protected int status; // 该条评论信息的状态，例如已删除之类的
	protected int ownerOnly;    //是否只允许发文人和评论者查阅
	protected UserBean user;

	/**
	 * 返回评论的对象编号
	 * 
	 * @return
	 */
	public abstract int getParentId();

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(Date replyTime) {
		this.replyTime = replyTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAuthorURL() {
		return authorURL;
	}

	public void setAuthorURL(String authorURL) {
		this.authorURL = authorURL;
	}

	public ClientInfo getClient() {
		if(client==null)
			client = new ClientInfo();
		return client;
	}

	public void setClient(ClientInfo client) {
		this.client = client;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public int getOwnerOnly() {
		return ownerOnly;
	}

	public void setOwnerOnly(int ownerOnly) {
		this.ownerOnly = ownerOnly;
	}

	/** *** The methods below is for search proxy **** */

	public String getKeywordField() {
		return "id";
	}

	public String[] getStoreFields() {
		return new String[] { "site.id", "site.friendlyName", "author",
				"replyTime", "user.id", "user.nickname" };
	}

	public String[] getIndexFields() {
		return new String[] { "content" };
	}

}
