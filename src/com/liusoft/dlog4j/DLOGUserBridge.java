/*
 *  DLOGUserBridge.java
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
package com.liusoft.dlog4j;

import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.UserDAO;

/**
 * DLOG对用户资料管理接口的实现
 * @author liudong
 */
public class DLOGUserBridge implements UserBridge {

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.UserBridge#create(com.liusoft.dlog4j.beans.UserBean)
	 */
	public void create(UserBean user) {
		UserDAO.createUser(user);		
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.UserBridge#getUserByName(java.lang.String)
	 */
	public UserBean getUserByName(String username){
		return UserDAO.getUserByName(username);
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.UserBridge#getUserByNickname(java.lang.String)
	 */
	public UserBean getUserByNickname(String nickname){
		return UserDAO.getUserByNickname(nickname);
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.UserBridge#update(com.liusoft.dlog4j.beans.UserBean)
	 */
	public void update(UserBean user) {
		UserDAO.updateUser(user);
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.UserBridge#userLogin(com.liusoft.dlog4j.beans.UserBean)
	 */
	public void userLogin(UserBean user) {
		UserDAO.flush();
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.UserBridge#userLogout(com.liusoft.dlog4j.beans.UserBean)
	 */
	public void userLogout(SessionUserObject user, boolean manual_logout) {
		UserDAO.userLogout(user.getId(), user.getLastTime(), manual_logout);
	}

}
