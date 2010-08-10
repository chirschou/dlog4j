/*
 *  UserBridge.java
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

/**
 * 用户资料管理接口
 * 用户注册/登录/资料修改等都是通过该接口进行
 * 该接口用户扩展跟其他系统的用户数据同步功能
 * @author liudong
 */
public interface UserBridge {
	
	/**
	 * 添加一个用户
	 * @param user
	 * @throws Exception
	 */
	public void create(final UserBean user);
	
	/**
	 * 更新用户资料
	 * @param user
	 * @throws Exception
	 */
	public void update(final UserBean user);
	
	/**
	 * 通过用户名来获取用户资料
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public UserBean getUserByName(final String username);
	
	/**
	 * 通过用户昵称来获取用户资料
	 * @param nickname
	 * @return
	 * @throws Exception
	 */
	public UserBean getUserByNickname(final String nickname);
	
	/**
	 * 用户登录后触发该方法
	 * @param user
	 * @throws Exception
	 */
	public void userLogin(final UserBean user);
	
	/**
	 * 用户退出登录后触发该方法
	 * @param user
	 * @param manual_logout 是手工注销还是会话实效时由系统调用
	 * @throws Exception
	 */
	public void userLogout(final SessionUserObject user, boolean manual_logout);

}
