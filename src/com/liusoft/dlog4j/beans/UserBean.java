/*
 *  UserBean.java
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
package com.liusoft.dlog4j.beans;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.base.ContactInfo;
import com.liusoft.dlog4j.base.CountInfo;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 注册用户对象
 * @author liudong
 */
public class UserBean extends SessionUserObject {	

	protected transient String password;	//登录密码
	protected int onlineStatus;
	
	protected SiteBean site;	//用户从某个网站注册的
	
	protected List bookmarks;	//用户设定的书签
	protected List msgs;	//根据hbm配置的过滤器决定读取新留言或者是已读留言
	protected List friends;	//好友
	
	public UserBean(){}
	
	public UserBean(int userid){
		super.setId(userid);
	}

	/**
	 * 从VO对象中复制一份数据，克隆
	 * @param bean
	 * @return
	 */
	public static UserBean copyFrom(SessionUserObject bean){
		UserBean user = new UserBean();
		user.setId(bean.getId());
		user.setName(bean.getName());
		user.setNickname(bean.getNickname());
		user.setSex(bean.getSex());
		if(bean.getBirth()!=null)
			user.setBirth((Date)bean.getBirth().clone());
		if(bean.getContactInfo()!=null)
			user.setContactInfo((ContactInfo)bean.getContactInfo().clone());
		if(bean.getCount()!=null)
			user.setCount((CountInfo)bean.getCount().clone());
		user.setResume(bean.getResume());
		user.setRegTime(new Timestamp(bean.getRegTime().getTime()));
		if(bean.getLastTime()!=null)
			user.setLastTime(new Timestamp(bean.getLastTime().getTime()));
		user.setLastAddr(bean.getLastAddr());
		user.setStatus(bean.getStatus());
		user.setKeepDays(bean.getKeepDays());
		user.setOwnSiteId(bean.getOwnSiteId());
		user.setPortrait(bean.getPortrait());
		user.setRole(bean.getRole());		
		return user;
	}
	
	public List getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(List bookmarks) {
		this.bookmarks = bookmarks;
	}

	public String getCryptPassword() {
		if(password == null)
			return null;
	    return StringUtils.encryptPassword(password);
	}
	
	public void setCryptPassword(String pwd) {
		if(pwd!=null)
			password = StringUtils.decryptPassword(pwd);
	}
	
	public List getMsgs() {
		return msgs;
	}

	public void setMsgs(List msgs) {
		this.msgs = msgs;
	}

	public SiteBean getSite() {
		return site;
	}

	public void setSite(SiteBean site) {
		this.site = site;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public List getFriends() {
		return friends;
	}

	public void setFriends(List friends) {
		this.friends = friends;
	}

}
