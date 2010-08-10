/*
 *  _TopicBeanBase.java
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

import java.util.List;

import com.liusoft.dlog4j.beans.ForumBean;
import com.liusoft.dlog4j.beans.TopicReplyBean;
import com.liusoft.dlog4j.beans.UserBean;

/**
 * 论坛帖子的基类，没有内容以及一些子表的数据
 * @see com.liusoft.dlog4j.beans.TopicBean
 * @see com.liusoft.dlog4j.beans.TopicOutlineBean
 * @author Winter Lau
 */
public class _TopicBeanBase extends _ReadingBean {

	protected ForumBean forum;
	protected String username;
	protected TopicReplyBean lastReply;
	
	protected UserBean lastUser;
	protected String lastUsername;
	protected int lock;
	protected int type;

	protected String content;

	protected List trackbacks;
	
	public boolean isElite(){
		return (type & INFO_TYPE_ELITE) == INFO_TYPE_ELITE;
	}
	
	public boolean isTop(){
		return (type & INFO_TYPE_TOP) == INFO_TYPE_TOP;
	}
	
	public void setTop(boolean top){
		int oldType = type;
		if(top)
			oldType |= INFO_TYPE_TOP;
		else
			oldType &= ~INFO_TYPE_TOP;
		setType(oldType);
	}
	
	public void setElite(boolean elite){
		int oldType = type;
		if(elite)
			oldType |= INFO_TYPE_ELITE;
		else
			oldType &= ~INFO_TYPE_ELITE;
		setType(oldType);
	}
	
	public ForumBean getForum() {
		return forum;
	}
	
	public void setForum(ForumBean forum) {
		this.forum = forum;
	}
	
	public UserBean getUser() {
		return super.getOwner();
	}
	
	public void setUser(UserBean user) {
		super.setOwner(user);
	}
	
	public int getLock() {
		return lock;
	}
	
	public void setLock(int lock) {
		this.lock = lock;
	}
	
	public String getLastUsername() {
		return lastUsername;
	}
	
	public void setLastUsername(String lastUsername) {
		this.lastUsername = lastUsername;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public TopicReplyBean getLastReply() {
		return lastReply;
	}

	public void setLastReply(TopicReplyBean lastReply) {
		this.lastReply = lastReply;
	}

	public UserBean getLastUser() {
		return lastUser;
	}

	public void setLastUser(UserBean lastUser) {
		this.lastUser = lastUser;
	}

	public List getTrackbacks() {
		return trackbacks;
	}

	public void setTrackbacks(List trackbacks) {
		this.trackbacks = trackbacks;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
