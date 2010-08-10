/*
 *  MessageBean.java
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
package com.liusoft.dlog4j.beans;

import java.util.Date;

import com.liusoft.dlog4j.base._BeanBase;

/**
 * 站内留言对象
 * @author liudong
 */
public class MessageBean extends _BeanBase {

	public final static int STATUS_NEW = 0x00;  //新信息
    public final static int STATUS_READ = 0x01;	//已读
    
    protected UserBean fromUser;	//发送者
    protected UserBean toUser;		//接收者
    protected String content;		//留言内容
    protected Date sendTime;		//留言发送时间
    protected Date expiredTime;		//留言的失效时间(NEW:3.0版本新增)
    protected Date readTime;		//留言阅读时间
    protected int status;			//留言的当前状态
    
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}
	public UserBean getFromUser() {
		return fromUser;
	}
	public void setFromUser(UserBean fromUser) {
		this.fromUser = fromUser;
	}
	public Date getReadTime() {
		return readTime;
	}
	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public UserBean getToUser() {
		return toUser;
	}
	public void setToUser(UserBean toUser) {
		this.toUser = toUser;
	}

}
