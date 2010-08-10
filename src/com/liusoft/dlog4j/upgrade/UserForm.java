/*
 *  UserForm.java
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
 * DLOG4J 2.0 的用户
 * @author liudong
 */
public class UserForm {
	// --------------------------------------------------------- Instance Variables

	/** email property */
	private String email;

	/** name property */
	private String loginName;
	
	private String password;
	private String displayName;
	private String homePage;
	private String resume;
	private int loginCount;
	private Date regTime;
	private Date lastTime;		//最后一次登录的时间
	private String lastAddr;	//最后一次登录的IP地址
	private int userRole = 0;
	
	private List logs;
	private int logCount;
	private List replies;
	private int replyCount;
	private int bookMarkCount; //此字段不对应数据库，而是由getLoginUser标签设置该字段的值

	protected int id;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * 如果用户是密友则该属性为拥有特权(查看与添加)的分类
	 * 如果用户是好友则该属性是用户可添加日记的分类
	 * 多个分类使用逗号格开，例如 1,4,5
	 */
	private String cats; 

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public String getHomePage() {
		return homePage;
	}
	public Date getLastTime() {
		return lastTime;
	}
	public int getLoginCount() {
		return loginCount;
	}
	public String getPassword() {
		return password;
	}
	
	public String getCryptPassword() {
	    return StringUtils.encrypt(password);
	}
	
	public void setCryptPassword(String pwd) {
		System.out.println("crypt password="+pwd);
	    password = StringUtils.decrypt(pwd);
		System.out.println("password="+password);
	}
	public Date getRegTime() {
		return regTime;
	}
	public String getResume() {
		return resume;
	}
	public int getUserRole() {
		return userRole;
	}
	public void setDisplayName(String string) {
		displayName = string;
	}
	public void setEmail(String string) {
		email = string;
	}
	public void setHomePage(String string) {
		homePage = string;
	}
	public void setLastTime(Date date) {
		lastTime = date;
	}
	public void setLoginCount(int i) {
		loginCount = i;
	}
	public void setPassword(String string) {
		password = string;
	}
	public void setRegTime(Date date) {
		regTime = date;
	}
	public void setResume(String string) {
		resume = string;
	}
	public void setUserRole(int i) {
		userRole = i;
	}
	public List getLogs() {
		return logs;
	}
	
	public int getLogCount(){
		return logCount;
	}
	public List getReplies() {
		return replies;
	}
	public void setLogs(List list) {
		logs = list;
	}
	public void setReplies(List list) {
		replies = list;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String string) {
		loginName = string;
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setLogCount(int i) {
		logCount = i;
	}

	public String getLastAddr() {
		return lastAddr;
	}
	public void setLastAddr(String lastAddr) {
		this.lastAddr = lastAddr;
	}
	public void setReplyCount(int i) {
		replyCount = i;
	}

	public String getCats() {
		return cats;
	}
	public void setCats(String cats) {
		this.cats = cats;
	}

	public int getBookMarkCount() {
		return bookMarkCount;
	}
	public void setBookMarkCount(int bookMarkCount) {
		this.bookMarkCount = bookMarkCount;
	}
}
