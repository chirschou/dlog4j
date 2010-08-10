/*
 *  ForumBean.java
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
package com.liusoft.dlog4j.beans;

import java.util.Date;

import com.liusoft.dlog4j.base.Orderable;
import com.liusoft.dlog4j.base._MultipleSiteEnabledBean;

/**
 * 论坛
 * @author Winter Lau
 */
public class ForumBean extends _MultipleSiteEnabledBean implements Orderable{

	public final static int STATUS_LOCKED = 0x01;	//被锁定的讨论区，可以阅读但不能发帖
	public final static int STATUS_HIDDEN = 0x03;	//被隐藏的讨论区，只有站长可以进入
	
	private String name;
	private String desc;
	private int type;
	private Date createTime;
	private Date lastPostTime;
	private Date modifyTime;
	private UserBean lastUser;
	private String lastUsername;
	private TopicBean lastTopic;
	private int sortOrder;
	private int topicCount;
	private int option;
	private int status;
	
	private TypeBean catalog;
	
	public ForumBean(){}
	
	public ForumBean(int forum_id){
		setId(forum_id);
	}
	
	/**
	 * 判断是否可以发帖或者修改帖子
	 * @param userid
	 * @return
	 */
	public boolean canCreateOrUpdateTopic(UserBean user){
		if(user==null || getStatus() == ForumBean.STATUS_LOCKED)
			return false;
		if(status == STATUS_NORMAL)
			return true;
		return getSite().isOwner(user);
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public int getTopicCount() {
		return topicCount;
	}
	public void setTopicCount(int topicCount) {
		this.topicCount = topicCount;
	}
	public int incTopicCount(int count){
		this.topicCount += count;
		if(this.topicCount<0)
			this.topicCount = 0;
		return this.topicCount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastPostTime() {
		return lastPostTime;
	}
	public void setLastPostTime(Date lastPostTime) {
		this.lastPostTime = lastPostTime;
	}
	public String getLastUsername() {
		return lastUsername;
	}
	public void setLastUsername(String lastUsername) {
		this.lastUsername = lastUsername;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public int getOption() {
		return option;
	}
	public void setOption(int option) {
		this.option = option;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public TopicBean getLastTopic() {
		return lastTopic;
	}

	public void setLastTopic(TopicBean lastTopic) {
		this.lastTopic = lastTopic;
	}

	public UserBean getLastUser() {
		return lastUser;
	}

	public void setLastUser(UserBean lastUser) {
		this.lastUser = lastUser;
	}
	
	public boolean isLocked(){
		return status == STATUS_LOCKED;
	}
	
	public boolean isHidden(){
		return status == STATUS_HIDDEN;		
	}
	
	public boolean isNormal(){
		return status == STATUS_NORMAL;
	}
	
	public void lock(){
		this.status = STATUS_LOCKED;
	}
	
	public void hide(){
		this.status = STATUS_HIDDEN;
	}

	public TypeBean getCatalog() {
		return catalog;
	}

	public void setCatalog(TypeBean catalog) {
		this.catalog = catalog;
	}

}
