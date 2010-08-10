/*
 *  UserLoginManager.java
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
package com.liusoft.dlog4j;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.UserDAO;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 用户登录控制 采用Cookie与Session结合进行用户登录会话的判断 
 * TODO: 阻止一个__ClientId使用多次
 * 
 * <pre>当你不能再拥有时,你唯一能做的,就是让自己不要忘记</pre>
 * 
 * @author Winter Lau
 */
public class UserLoginManager {

	private final static Log log = LogFactory.getLog(UserLoginManager.class);
	
	/**
	 * 表单的提交时间必须是生成表单后的0.5秒钟以上
	 */
	private final static int MIN_MS_BETWEEN_ACTION = 500;

	/**
	 * 表单的有效期是一个小时
	 */
	private final static int MAX_MS_BETWEEN_ACTION = 3600000;
	private final static String SESSION_USER_KEY = "UserBean";
	private final static String COOKIE_LASTLOGIN_KEY = "LL";//last login key
	private final static String COOKIE_UUID_KEY = "uuid";

	/**
	 * 自动登录 用于Velocity页面
	 * 
	 * @param request
	 * @param response
	 * @param uuid
	 * @param verify_host
	 * @return
	 * @see com.liusoft.dlog4j.velocity.DLOG_VelocityTool#verify_login_cookie(String,
	 *      boolean)
	 */
	public static SessionUserObject getLoginUser(HttpServletRequest request,
			HttpServletResponse response, boolean verify_host) {
		// 如果session有记录则直接从session中读取并返回
		Cookie uuidCookie = null;
		HttpSession ssn = request.getSession(false);
		if (ssn != null) {
			SessionUserObject user = (SessionUserObject) ssn
					.getAttribute(SESSION_USER_KEY);
			if (user != null){				
				uuidCookie = getUuidCookie(request);
				//必须是session值存在，且cookie值也存在才有效
				//(主要针对集群环境下，用户在s1注销了，但是链接跳到s2时候还是显示登录状态)
				if(uuidCookie!=null)
					return user;
				ssn.invalidate();
				return null;
			}
		}
		String uuid = null;
		if(uuidCookie == null)
			uuidCookie = getUuidCookie(request);
		if (uuidCookie != null)
			uuid = uuidCookie.getValue();
		if(StringUtils.isEmpty(uuid))
			return null;
		// session不存在用户资料则执行自动登录过程
		try {
			UUID oUUID = new UUID(uuid);
			String new_host = request.getRemoteAddr();
			if (verify_host && !StringUtils.equals(new_host, oUUID.host))
				return null;
			UserBean user = UserDAO.getUserByID(oUUID.uid);
			// 用户不存在
			if (user == null || user.getStatus() != UserBean.STATUS_NORMAL
					|| user.getPassword().hashCode() != oUUID.pwdCode) {
				RequestUtils.setCookie(request, response, COOKIE_UUID_KEY, "", 0);
				RequestUtils.setCookie(request, response, COOKIE_LASTLOGIN_KEY, "", 0);
				return null;
			}
			return loginUser(request, response, user, user.getKeepDays());
		} catch (Exception e) {
			log.error("Exception occur when get current user.", e);
		}

		return null;
	}

	/**
	 * 更新已登录的会话中的用户资料
	 * 
	 * @param req
	 * @param ubean
	 */
	public static void updateLoginUser(HttpServletRequest req, UserBean ubean) {
		HttpSession ssn = req.getSession(true);
		if (ssn != null && ubean != null) {
			ssn.setAttribute(SESSION_USER_KEY, SessionUserObject
					.copyFrom(ubean));
		}
	}

	/**
	 * 返回保存用户登录信息的Cookie
	 * 
	 * @param request
	 * @return
	 */
	protected static Cookie getUuidCookie(HttpServletRequest request) {
		return RequestUtils.getCookie(request, COOKIE_UUID_KEY);
	}

	/**
	 * 执行用户登录过程
	 * 
	 * @param req
	 * @param res
	 * @param ubean
	 * @param keepDays
	 * @see com.liusoft.dlog4j.action.UserAction#doLogin(ActionMapping,
	 *      ActionForm, HttpServletRequest, HttpServletResponse)
	 */
	public static SessionUserObject loginUser(HttpServletRequest req,
			HttpServletResponse res, UserBean ubean, int keepDays) {
		HttpSession ssn = req.getSession(false);
		if (ssn != null) {
			SessionUserObject rub = (SessionUserObject) ssn
					.getAttribute(SESSION_USER_KEY);
			if (rub != null && rub.getId()==ubean.getId()) {
				return rub;
			}
		}
		
		// 更新用户资料中最近一次访问时间以及访问地址,同时设置在线状态
		ubean.setLastAddr(req.getRemoteAddr());
		ubean.setLastTime(new Timestamp(System.currentTimeMillis()));
		ubean.setKeepDays(keepDays);
		ubean.setOnlineStatus(1);
		DLOGUserManager.update(ubean);

		// 写登录信息到cookie,不使用session保存用户资料
		UUID uuid = new UUID();
		uuid.uid = ubean.getId();
		uuid.pwdCode = ubean.getPassword().hashCode();
		uuid.host = req.getRemoteAddr();

		String value = uuid.toString();
		RequestUtils.setCookie(req, res, COOKIE_UUID_KEY, value,
				(keepDays > 0) ? keepDays * 86400 : keepDays);
		RequestUtils.setCookie(req, res, COOKIE_LASTLOGIN_KEY, ubean.getLastTime()
				.toString(), -1);
		
		// 用户资料保存在Session中
		if (ssn == null)
			ssn = req.getSession(true);
		if (ssn != null && ubean != null) {
			ssn.setAttribute(SESSION_USER_KEY, SessionUserObject
					.copyFrom(ubean));
		}
		return ubean;
	}

	/**
	 * 注销用户
	 * 
	 * @param req
	 * @param res
	 * @see com.liusoft.dlog4j.action.UserAction#doLogout(ActionMapping,
	 *      ActionForm, HttpServletRequest, HttpServletResponse, String)
	 */
	public static void logoutUser(HttpServletRequest req,
			HttpServletResponse res) {
		// 清除用户表中的keep_days字段的值，用户下次不能再自动登录
		SessionUserObject ubean = getLoginUser(req, res, false);
		if (ubean != null && ubean.getKeepDays() != 0) {
			DLOGUserManager.userLogout(ubean, true);
		}

		// 清除Cookie
		RequestUtils.setCookie(req, res, COOKIE_UUID_KEY, "", 0);
		RequestUtils.setCookie(req, res, COOKIE_LASTLOGIN_KEY, "", 0);

		// 清除session
		HttpSession ssn = req.getSession(false);
		if (ssn != null) {
			ssn.invalidate();
		}
	}

	/**
	 * 注销用户
	 * 
	 * @param userid
	 * @param lastLogin
	 * @see SessionUserObject#valueUnbound(HttpSessionBindingEvent)
	 */
	public static void logoutUser(SessionUserObject user) {
		DLOGUserManager.userLogout(user, false);
	}

	/**
	 * 验证客户端安全识别码
	 * 
	 * @param req
	 * @param clientId
	 * @return
	 */
	public static boolean validateClientId(HttpServletRequest req,
			String clientId) {
		return ClientID.validate(req, clientId);
	}

	/**
	 * 生成客户端安全识别码
	 * 
	 * @param req
	 * @return
	 */
	public static String generateClientId(HttpServletRequest req,
			HttpServletResponse res) {
		return ClientID.generate(req, res);
	}

	/**
	 * 自动登录标识
	 * 
	 * @author liudong
	 */
	private static class UUID {

		/**
		 * 自动登录标识的加密密码 IMPORTANT: 建议修改该值后重新编译系统以保证系统的安全性 该密钥的长度必须是8的整数倍
		 */
		private final static String UUID_ENCRYPT_KEY = "1D2L3O4G546J7V83";

		private final static String PATTERN = "{0}_{1}@{2}";

		private final static MessageFormat parser = new MessageFormat(PATTERN);

		public int uid;

		public String host;

		public int pwdCode;

		public UUID() {
		}

		/**
		 * 序列化到字符串
		 */
		public String toString() {
			String uuid = MessageFormat.format(PATTERN, String.valueOf(uid),
					String.valueOf(pwdCode), host );
			return StringUtils.encrypt(uuid, UUID_ENCRYPT_KEY);
		}

		/**
		 * 还原
		 * 
		 * @param cookie
		 * @return
		 * @throws ParseException
		 */
		public UUID(String cookie) throws ParseException {
			String uuid = StringUtils.decrypt(cookie, UUID_ENCRYPT_KEY);
			Object[] args = parser.parse(uuid);
			uid = Integer.parseInt((String) args[0]);
			pwdCode = Integer.parseInt((String) args[1]);
			host = (String) args[2];
		}

	}

	/**
	 * 客户端验证码
	 * 
	 * @author liudong
	 */
	private static class ClientID {

		private final static String CLIENTID_ENCRYPT_KEY = "DLOG4JV3";

		private final static String PATTERN = "{0}|{1}|{2}";

		private final static MessageFormat parser = new MessageFormat(PATTERN);

		/**
		 * 生成客户端安全识别码
		 * 
		 * @param req
		 * @return
		 */
		public static String generate(HttpServletRequest req,
				HttpServletResponse res) {
			long ct = System.currentTimeMillis();
			String user_agent = RequestUtils.getHeader(req, Globals.USER_AGENT);
			String remote_host = req.getRemoteAddr();
			StringBuffer code = new StringBuffer();
			code.append(ct);
			code.append('|');
			code.append(remote_host);
			code.append('|');
			if (user_agent != null)
				code.append(Math.abs(user_agent.hashCode()));
			else
				code.append(ct);
			return StringUtils.encrypt(code.toString(), CLIENTID_ENCRYPT_KEY);
		}

		/**
		 * 验证客户端安全识别码
		 * 
		 * @param req
		 * @param clientId
		 * @return
		 */
		public static boolean validate(HttpServletRequest req, String clientId) {
			String clientCode = StringUtils.decrypt(clientId,
					CLIENTID_ENCRYPT_KEY);
			try {
				Object[] objs = parser.parse(clientCode);
				String host = req.getRemoteAddr();
				if (host.equals(objs[1])) {
					String user_agent = RequestUtils.getHeader(req,
							Globals.USER_AGENT);
					String ua = (user_agent!=null)?String.valueOf(Math.abs(user_agent.hashCode())):null;
					if (objs[2].equals(ua) || objs[2].equals(objs[0])) {
						long lt = Long.parseLong((String) objs[0]);
						long ct = System.currentTimeMillis();
						// 客户端识别码的有效期是一个钟头
						long it = ct - lt;
						if (MIN_MS_BETWEEN_ACTION < it
								&& it < MAX_MS_BETWEEN_ACTION) {
							return true;
						}
					}
				}
			} catch (ParseException e) {
			}
			return false;
		}

	}
}
