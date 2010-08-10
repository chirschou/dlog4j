/*
 *  SessionUserObject.java
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
package com.liusoft.dlog4j;

import java.sql.Date;
import java.sql.Timestamp;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liusoft.dlog4j.base.ContactInfo;
import com.liusoft.dlog4j.base.CountInfo;
import com.liusoft.dlog4j.base._UserBeanBase;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.FCKUploadFileDAO;
import com.liusoft.dlog4j.db.HibernateUtils;

/**
 * 记录在会话中的用户基本资料
 * @see com.liusoft.dlog4j.beans.UserBean
 * @author Winter Lau
 */
public class SessionUserObject extends _UserBeanBase implements HttpSessionBindingListener{

	private final static Log log = LogFactory.getLog(SessionUserObject.class);
	
	/* session 相关的信息,跟bean无关 */
	private String sessionId;

	/**
	 * 从PO对象中复制一份数据，克隆
	 * @param bean
	 * @return
	 */
	public static SessionUserObject copyFrom(UserBean bean){
		SessionUserObject user = new SessionUserObject();
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
	
	/**
	 * 保存session_id防止某些应用服务器会话实效后无法获取session_id
	 */
	public void valueBound(HttpSessionBindingEvent e) {
		this.sessionId = e.getSession().getId();
	}

	/**
	 * 执行用户注销方法
	 * 由于该方法是由应用服务器调用的，不经过HibernateFilter，因此必须手动关闭Session
	 */
	public void valueUnbound(HttpSessionBindingEvent e) {
		SessionUserObject user = (SessionUserObject)e.getValue();
		if(user != null){
			try{
				UserLoginManager.logoutUser(user);
			}catch(Exception excp){
				log.error("Error when logout user, userid="+user.getId(), excp);
			}
			try{
				//清除dlog_fck_upload_file的相关文件
				FCKUploadFileDAO.cleanupOfSession(user.getSessionId(), user.getId());
			}catch(Exception excp){
				log.error("Error when cleanup upload files, userid="+user.getId(), excp);
			}
			//此处由于不受HibernateFilter控制，因此需要显式的来关闭数据库资源
			HibernateUtils.closeSession();			
		}
	}

	private String getSessionId() {
		return sessionId;
	}

}
