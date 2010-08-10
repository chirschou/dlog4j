/*
 *  UserManager.java
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

import javax.servlet.ServletContext;

import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 用户资料管理接口
 * @author liudong
 */
public class DLOGUserManager{
	
	private final static String MANAGER_KEY = "user-ext-manager";
	
	private static UserBridge dlog_bridge;
	private static UserBridge ext_bridge;
	
	/**
	 * 初始化用户资料管理入口,加载扩展用户资料管理接口
	 * @param sContext
	 * @throws Exception
	 */
	public synchronized static void init(ServletContext sContext)
			throws Exception {
		if (dlog_bridge != null)
			return;
		dlog_bridge = new DLOGUserBridge();
		String ext_mgr_cls = sContext.getInitParameter(MANAGER_KEY);
		if (StringUtils.isEmpty(ext_mgr_cls)
				|| StringUtils.equals(dlog_bridge.getClass().getName(),
						ext_mgr_cls))
			return;
		ext_bridge = (UserBridge) Class.forName(ext_mgr_cls).newInstance();
	}
	
	public static void destroy(){
	}
	
	public static void create(UserBean user){
		if(ext_bridge!=null)
			ext_bridge.create(user);
		dlog_bridge.create(user);
	}

	/**
	 * TODO: 考虑同步两边的数据
	 * @param username
	 * @return
	 */
	public static UserBean getUserByName(String username){
		return dlog_bridge.getUserByName(username);
	}

	/**
	 * TODO: 考虑同步两边的数据
	 * @param nickname
	 * @return
	 */
	public static UserBean getUserByNickname(String nickname){
		return dlog_bridge.getUserByNickname(nickname);
	}

	public static void update(UserBean user){
		if(ext_bridge!=null)
			ext_bridge.update(user);
		dlog_bridge.update(user);
	}

	public static void userLogin(UserBean user){
		if(ext_bridge!=null)
			ext_bridge.userLogin(user);
		dlog_bridge.userLogin(user);
	}

	public static void userLogout(SessionUserObject user, boolean manual_logout){
		if(ext_bridge!=null)
			ext_bridge.userLogout(user, manual_logout);
		dlog_bridge.userLogout(user, manual_logout);
	}

}
