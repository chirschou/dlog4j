/*
 *  FriendBean.java
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
package com.liusoft.dlog4j.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友信息，对应dlog_friend表中的记录
 * @author Winter Lau
 */
public class FriendBean implements Serializable {
	
	public final static int TYPE_GENERAL = 0x00;
	
	public final static int ROLE_GENERAL = 0x00;
	
	private int owner;		//所有者编号
	private UserBean friend;	//好友资料
	private int type;
	private int role;
	private Date addTime;
	
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public UserBean getFriend() {
		return friend;
	}
	public void setFriend(UserBean friend) {
		this.friend = friend;
	}
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}

	public boolean equals(Object arg0) {
		if(arg0 == null)
			return false;
		if(arg0 == this)
			return true;
		if(arg0 instanceof FriendBean){
			FriendBean fb = (FriendBean)arg0;
			return (fb.getOwner()==owner) && (fb.getFriend().getId()==friend.getId());
		}
		return false;
	}

	public int hashCode() {
		return (String.valueOf(owner) + "_" + friend.getId()).hashCode();
	}
	
}
