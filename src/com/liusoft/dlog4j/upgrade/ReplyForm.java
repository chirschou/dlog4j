/*
 *  ReplyForm.java
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

/**
 * DLOG4J 2.0的评论
 * 
 * @author liudong
 */
public class ReplyForm {

	private Date writeTime;
	private LogForm log;
	private UserForm author;
	private String clientType = "HTML";
    String content = null;

	protected int id;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	// --------------------------------------------------------- Methods

	public Date getWriteTime() {
		return writeTime;
	}
	public void setWriteTime(Date writeTime) {
		this.writeTime = writeTime;
	}
	public int getAuthorId() {
		return author.getId();
	}
	public void setAuthorId(int aid) {
		if (author == null)
			author = new UserForm();
		author.setId(aid);
	}
	public String getAuthorName() {
		return author.getDisplayName();
	}
	public UserForm getAuthor() {
		return author;
	}
	public void setAuthor(UserForm form) {
		author = form;
	}
	public LogForm getLog() {
		return log;
	}
	public void setLog(LogForm form) {
		log = form;
	}
	public int getLogId() {
		return log.getId();
	}
	public void setLogId(int logid) {
		if (log == null)
			log = new LogForm();
		log.setId(logid);
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
