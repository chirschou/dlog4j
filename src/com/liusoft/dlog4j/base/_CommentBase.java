/*
 *  _CommentBase.java
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
 *  2006-7-23
 */
package com.liusoft.dlog4j.base;

import java.util.Date;
import java.util.List;

import com.liusoft.dlog4j.beans.CommentBean;

/**
 * 评论的基类
 * @author liudong
 */
public class _CommentBase extends _MultipleSiteEnabledBean {

	private CommentBean parent;
	
	private ClientInfo client;
	private AuthorInfo author;
	
	private int eid;
	private int etype;
	
	private String title;
	private String content;
	private int format;
	
	private Date createTime;
	private int flag;
	private int status;
	
	private List comments;

	public AuthorInfo getAuthor() {
		return author;
	}

	public void setAuthor(AuthorInfo author) {
		this.author = author;
	}

	public ClientInfo getClient() {
		return client;
	}

	public void setClient(ClientInfo client) {
		this.client = client;
	}

	public List getComments() {
		return comments;
	}

	public void setComments(List comments) {
		this.comments = comments;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getEid() {
		return eid;
	}

	public void setEid(int eid) {
		this.eid = eid;
	}

	public int getEtype() {
		return etype;
	}

	public void setEtype(int etype) {
		this.etype = etype;
	}

	public int getFlag() {
		return flag;
	}
	
	public int getOwnerOnly(){
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public CommentBean getParent() {
		return parent;
	}

	public void setParent(CommentBean parent) {
		this.parent = parent;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
