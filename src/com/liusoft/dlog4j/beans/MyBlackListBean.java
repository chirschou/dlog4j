/*
 *  MyBlackListBean.java
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

import java.io.Serializable;
import java.util.Date;

/**
 * 黑名单对象
 * @author liudong
 */
public class MyBlackListBean implements Serializable {
	
	private int myId;
	private UserBean other;
	private int type;
	private Date addTime;
	
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public int getMyId() {
		return myId;
	}
	public void setMyId(int myId) {
		this.myId = myId;
	}
	public UserBean getOther() {
		return other;
	}
	public void setOther(UserBean other) {
		this.other = other;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public boolean equals(Object arg0) {
		if(arg0 == null)
			return false;
		if(arg0 == this)
			return true;
		if(arg0 instanceof MyBlackListBean){
			MyBlackListBean fb = (MyBlackListBean)arg0;
			return (fb.getMyId()==myId) && (fb.getOther().getId()==other.getId());
		}
		return false;
	}
	
	public int hashCode() {
		return (String.valueOf(myId) + "_" + other.getId()).hashCode();
	}

}
