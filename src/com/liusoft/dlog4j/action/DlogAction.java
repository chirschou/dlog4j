/*
 *  SiteAction.java
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
package com.liusoft.dlog4j.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.htmlparser.Node;
import org.htmlparser.Parser;

import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.DLOGUserManager;
import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.HtmlNodeFilters;
import com.liusoft.dlog4j.MailTransportQueue;
import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.UserLoginManager;
import com.liusoft.dlog4j.beans.FriendBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.ConfigDAO;
import com.liusoft.dlog4j.dao.SiteDAO;
import com.liusoft.dlog4j.dao.UserDAO;
import com.liusoft.dlog4j.formbean.FormBean;
import com.liusoft.dlog4j.formbean.SiteForm;
import com.liusoft.dlog4j.formbean.UserForm;
import com.liusoft.dlog4j.util.DLOG4JUtils;
import com.liusoft.dlog4j.util.DateUtils;
import com.liusoft.dlog4j.util.ImageUtils;
import com.liusoft.dlog4j.util.MailSender;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 个人网记相关的Action类
 * @author Winter Lau
 */
public class DlogAction extends ActionBase {

	private final static Log log = LogFactory.getLog(DlogAction.class);
	
	/**
	 * 存放用户头像文件的路径
	 */
	public final static String PORTRAIT_PATH = "/uploads/portrait/";

	/**
	 * 用户头像的最大宽度
	 */
	public final static int PORTRAIT_WIDTH = 155;
	
	/**
	 * 用户头像的最大高度
	 */
	public final static int PORTRAIT_HEIGHT = 200;
	
	public final static int MIN_SITENAME_LEN = 3;

	/**
	 * 获取用户密码
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doFetchPwd(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm uform = (UserForm) form;
		validateClientId(request, uform);
		ActionMessages msgs = new ActionMessages();
		do{
			if(StringUtils.isEmpty(uform.getName())){
				msgs.add("name", new ActionMessage("error.username_empty"));
				break;
			}
			UserBean ubean = UserDAO.getUserByName(uform.getName());
			if(ubean==null){
				msgs.add("name", new ActionMessage("error.user_not_found"));
				break;
			}
			if (ubean.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("name", new ActionMessage("error.user_disabled"));
				break;
			}
			if(!StringUtils.isEmail(ubean.getEmail())){
				msgs.add("name", new ActionMessage("error.email_format"));
				break;
			}
			//发送邮件
			int siteid = (ubean.getSite()!=null)?ubean.getSite().getId():-1;
			sendPasswordNotify(request, siteid, ubean);
			msgs.add("name", new ActionMessage("mail.sent"));
			break;
		}while(true);
			
		if(!msgs.isEmpty())
			saveMessages(request, msgs);
		
		return mapping.findForward("fetchpwd");
	}

	/**
	 * 发送忘记密码邮件提醒
	 * 
	 * @param request
	 * @param rbean
	 * @throws Exception
	 */
	protected void sendPasswordNotify(HttpServletRequest request, final int site_id, 
			final UserBean ubean) throws Exception {
		
		//final String contextPath = request.getContextPath();
		final String urlPrefix = RequestUtils.getUrlPrefix(request);
		final String template = super.getPasswordTipTemplate();
		
		new Thread() {
			public void run() {
				try {
					// 发送邮件提醒
					String notify_content = MessageFormat.format(template,
							ubean.getName(),ubean.getPassword(),urlPrefix);
					Parser html = new Parser();
					html.setEncoding(Globals.ENC_8859_1);
					html.setInputHTML(notify_content);
					Node[] nodes = html.extractAllNodesThatMatch(
							HtmlNodeFilters.titleFilter).toNodeArray();
					String title = nodes[0].toPlainTextString();
					MailSender sender = MailSender.getHtmlMailSender(null, 25,
							null, null);
					sender.setSubject(title);
					sender.setSendDate(new Date());
					sender.setMailContent(notify_content);
					sender.setMailTo(new String[] { ubean
							.getContactInfo().getEmail() }, "to");
					MailTransportQueue queue = (MailTransportQueue) getServlet()
							.getServletContext().getAttribute(
									Globals.MAIL_QUEUE);
					// 写入待发送邮件队列
					queue.write(site_id, sender.getMimeMessage());
					if(log.isDebugEnabled())
						log.debug("Notification mail was written to the sending queue.");
				} catch (Exception e) {
					log.error("send notification mail failed.", e);
				}
			}
		}.start();
	}
	
	/**
	 * 添加好友
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @usage http://localhost/dlog/user.do?sid=1&uid=223&eventSubmit_AddFriend&fromPage=xxxx
	 * @ajax_enabled
	 */
	protected ActionForward doAddFriend(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FormBean fbean = (FormBean) form;
		validateClientId(request, fbean);
		String msg = null;
		//判断用户是否登录
		SessionUserObject loginUser = super.getLoginUser(request, response, false);
		if(loginUser != null){
			int friendId = RequestUtils.getParam(request, "uid", -1);
			UserBean friend = UserDAO.getUserByID(friendId);
			if(friend==null)
				msg = getMessage(request,null,"error.user_not_found", new Integer(friendId));
			else if(friendId == loginUser.getId())
				msg = getMessage(request,null,"error.cannot_add_myself");
			else{
				FriendBean fb = new FriendBean();
				fb.setAddTime(new Date());
				fb.setFriend(friend);
				fb.setOwner(loginUser.getId());
				fb.setType(FriendBean.TYPE_GENERAL);
				fb.setRole(FriendBean.ROLE_GENERAL);
				UserDAO.addFriend(fb);
				msg = getMessage(request,null,"error.friend_added", new Integer(friendId));
			}
		}
		else
			msg = getMessage(request,null,"error.user_not_login");
		return msgbox(mapping,form,request,response,msg,fbean.getFromPage());
	}
	
	/**
	 * 阻止用户(加入黑名单)
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param s_other_id
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	protected ActionForward doBlockUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm fbean = (UserForm)form;
		super.validateClientId(request, fbean);
		String msg = null;
		//判断用户是否登录
		SessionUserObject loginUser = super.getLoginUser(request, response, false);
		if(loginUser!=null){
			String s_id = String.valueOf(fbean.getId());
			if(UserDAO.isUserInBlackList(loginUser.getId(), fbean.getId())){
				msg = getMessage(request,null,"error.user_already_in_block", s_id);
			}
			else{
				UserDAO.addBlackList(loginUser.getId(), fbean.getId(), 0);
				msg = getMessage(request,null,"error.user_added_to_block", s_id);
			}
		}
		else
			msg = getMessage(request,null,"error.user_not_login");
		request.setAttribute("msg", msg);
		return makeForward(mapping.findForward("blacklist"),fbean.getSid());
	}

	/**
	 * 取消阻止(从黑名单中删除)
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * http://localhost/dlog/user.do?sid=1&uid=223&eventSubmit_DelFriend
	 */
	protected ActionForward doUnblockUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FormBean fbean = (FormBean) form;
		validateClientId(request, fbean);
		String msg = null;
		//判断用户是否登录
		SessionUserObject loginUser = super.getLoginUser(request, response, false);
		if(loginUser != null){
			String[] uids = request.getParameterValues("uid");
			UserDAO.deleteBlacklist(loginUser.getId(), uids);
			msg = getMessage(request,null,"error.user_delete_from_block", "");
		}
		else
			msg = getMessage(request,null,"error.user_not_login");
		return msgbox(mapping,form,request,response,msg,fbean.getFromPage());
	}
	
	/**
	 * 删除好友
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * http://localhost/dlog/user.do?sid=1&uid=223&eventSubmit_DelFriend
	 */
	protected ActionForward doDelFriend(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FormBean fbean = (FormBean) form;
		validateClientId(request, fbean);
		String msg = null;
		//判断用户是否登录
		SessionUserObject loginUser = super.getLoginUser(request, response, false);
		if(loginUser != null){
			String[] uids = request.getParameterValues("uid");
			int er = UserDAO.deleteFriend(loginUser.getId(), uids);
			if(er>0)
				msg = getMessage(request,null,"error.friend_deleted");
		}
		else
			msg = getMessage(request,null,"error.user_not_login");
		return msgbox(mapping,form,request,response,msg,fbean.getFromPage());
	}
	
	/**
	 * 用户登录并注册个人网记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @see com.liusoft.dlog4j.action.UserAction#doLogin(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
	 */
	protected ActionForward doLogin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int own_site_id = -1;
		UserForm user = (UserForm) form;
		validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		if (StringUtils.isEmpty(user.getName()))
			msgs.add("username", new ActionMessage("error.username_empty"));
		else if (StringUtils.isEmpty(user.getPassword()))
			msgs.add("password", new ActionMessage("error.password_empty"));
		else {
			UserBean ubean = DLOGUserManager.getUserByName(user.getName());
			if (ubean == null
					|| !StringUtils.equals(ubean.getPassword(), user
							.getPassword()))
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
			else if (ubean.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("password", new ActionMessage("error.user_disabled"));
			} else {
				// 执行登录过程
				UserLoginManager.loginUser(request, response, ubean, user.getKeepDays());
				own_site_id = ubean.getOwnSiteId();
			}
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("login");
		}
		
		String fromPage = user.getFromPage();
		
		if (StringUtils.isNotEmpty(fromPage))
			return new ActionForward(fromPage, true);
		
		if(user.getSid()>0)
			return makeForward(mapping.findForward("main"), user.getSid());
		
		if(own_site_id < 1)
			return mapping.findForward("home");

		return makeForward(mapping.findForward("home"), own_site_id);
		
	}

	/**
	 * 用户注销，清除COOKIE
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doLogout(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			String fromPage) throws Exception {
		
		//执行注销过程
		UserLoginManager.logoutUser(request, response);

		if (StringUtils.isNotEmpty(fromPage))
			return new ActionForward(fromPage, true);
		return mapping.findForward("home");
	}
	
	private static String g_portrait_path;
	private static String g_portrait_uri;
	
	/**
	 * 获取头像的存放路径
	 */
	private synchronized void initPortraitPath(){
		if(g_portrait_uri!=null)
			return;
		g_portrait_uri = getServlet().getInitParameter("portrait_base_uri");
		String portrait_path = this.getServlet().getInitParameter("portrait_base_path");

		if(portrait_path.startsWith(Globals.LOCAL_PATH_PREFIX))
			g_portrait_path = portrait_path.substring(Globals.LOCAL_PATH_PREFIX.length());		
		else if(portrait_path.startsWith("/"))
			g_portrait_path = getServlet().getServletContext().getRealPath(portrait_path);
		else
			g_portrait_path = portrait_path;
		if(!g_portrait_path.endsWith(File.separator))
			g_portrait_path += File.separator;
	}

	/**
	 * 修改用户的生日(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_birth.vm
	 */
	protected ActionForward doUpdateBirth(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			boolean update = false;
			if(StringUtils.isNotEmpty(user.getSbirth())){
				try{
					int birth = Integer.parseInt(user.getSbirth());
					int year = birth / 10000;
					int month = (birth % 10000) / 100;
					int date = birth % 100;
					long lbirth = DateUtils.getDateBegin(year,month,date).getTime().getTime();
					ubean.setBirth(new java.sql.Date(lbirth));
					System.out.println("=================  new birth is " + ubean.getBirth());
					update = true;
				}catch(Exception e){}
			}
			if(update){
				try {
					DLOGUserManager.update(ubean);
					//更新session中的用户资料
					UserLoginManager.updateLoginUser(request, ubean);		
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e
							.getMessage()));
				}
			}			
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}
	
	/**
	 * 修改用户的电子邮件地址(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_email.vm
	 */
	protected ActionForward doUpdateEmail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			ubean.setEmail(user.getEmail());
			try {
				DLOGUserManager.update(ubean);
				//更新session中的用户资料
				UserLoginManager.updateLoginUser(request, ubean);		
			} catch (Exception e) {
				msgs.add("result", new ActionMessage("error.database", e
						.getMessage()));
			}
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}

	/**
	 * 修改用户的性别(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_sex.vm
	 */
	protected ActionForward doUpdateSex(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			if (ubean.getSex() != user.getSex()) {
				if (user.getSex() == UserBean.SEX_FEMALE
						|| user.getSex() == UserBean.SEX_MALE
						|| user.getSex() == UserBean.SEX_UNKNOWN) {
					ubean.setSex(user.getSex());
					try {
						DLOGUserManager.update(ubean);
						// 更新session中的用户资料
						UserLoginManager.updateLoginUser(request, ubean);
					} catch (Exception e) {
						msgs.add("result", new ActionMessage("error.database",
								e.getMessage()));
					}
				}
			}
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}

	/**
	 * 修改用户的登录密码(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_pwd.vm
	 */
	protected ActionForward doUpdatePwd(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			if(!StringUtils.equals(user.getPassword2(), ubean.getPassword())){
				ubean.setPassword(user.getPassword2());
				try {
					DLOGUserManager.update(ubean);
					//更新session中的用户资料
					UserLoginManager.updateLoginUser(request, ubean);		
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e
							.getMessage()));
				}
			}
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}

	/**
	 * 修改用户的宣言(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_resume.vm
	 */
	protected ActionForward doUpdateResume(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			if(!StringUtils.equals(user.getResume(), ubean.getResume())){
				String resume = super.autoFiltrate(null, StringUtils.extractText(user.getResume()));
				ubean.setResume(resume);
				try {
					DLOGUserManager.update(ubean);
					//更新session中的用户资料
					UserLoginManager.updateLoginUser(request, ubean);		
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e
							.getMessage()));
				}
			}
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}

	/**
	 * 修改用户的MSN(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_msn.vm
	 */
	protected ActionForward doUpdateMSN(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			if(!StringUtils.equals(user.getMsn(), ubean.getMsn())){
				ubean.setMsn(user.getMsn());
				try {
					DLOGUserManager.update(ubean);
					//更新session中的用户资料
					UserLoginManager.updateLoginUser(request, ubean);		
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e
							.getMessage()));
				}
			}
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}

	/**
	 * 修改用户的个人网址(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_homepage.vm
	 */
	protected ActionForward doUpdateHomePage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			if(!StringUtils.equals(user.getHomePage(), ubean.getHomePage())){
				ubean.setHomePage(user.getHomePage());
				try {
					DLOGUserManager.update(ubean);
					//更新session中的用户资料
					UserLoginManager.updateLoginUser(request, ubean);		
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e
							.getMessage()));
				}
			}
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}

	/**
	 * 修改用户的QQ(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_qq.vm
	 */
	protected ActionForward doUpdateQQ(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			if(!StringUtils.equals(user.getQq(), ubean.getQq()) && StringUtils.isNumeric(user.getQq())){
				ubean.setQq(user.getQq());
				try {
					DLOGUserManager.update(ubean);
					//更新session中的用户资料
					UserLoginManager.updateLoginUser(request, ubean);		
				} catch (Exception e) {
					msgs.add("result", new ActionMessage("error.database", e
							.getMessage()));
				}
			}
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}
	
	/**
	 * 用户资料更新
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		// 验证用户资料表单
		user.validateUserForm(request, msgs, false);
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			ubean.setNickname(super.autoFiltrate(null, user.getNickname()));
			if(user.getBirth()!=null && user.getBirth().before(new Date()))
				ubean.setBirth(user.getBirth());
			else if(ubean.getBirth()!=null)
				ubean.setBirth(null);
			ubean.setSex(user.getSex());
			if (StringUtils.isNotEmpty(user.getResume()))
				ubean.setResume(super.autoFiltrate(null, StringUtils.extractText(user.getResume())));
			else
				ubean.setResume(null);
			if (!StringUtils.equals(ubean.getPassword(), user.getPassword2())
					&& StringUtils.isNotEmpty(user.getPassword2()))
				ubean.setPassword(user.getPassword2());
			if (StringUtils.isNotEmpty(user.getEmail()))
				ubean.setEmail(user.getEmail());
			else
				ubean.setEmail(null);
			if (StringUtils.isNotEmpty(user.getHomePage()))
				ubean.setHomePage(user.getHomePage());
			else
				ubean.setHomePage(null);
			if (StringUtils.isNotEmpty(user.getMobile()))
				ubean.setMobile(user.getMobile());
			else
				ubean.setMobile(null);
			if (StringUtils.isNotEmpty(user.getMsn()))
				ubean.setMsn(user.getMsn());
			else
				ubean.setMsn(null);
			if (StringUtils.isNotEmpty(user.getQq()))
				ubean.setQq(user.getQq());
			else
				ubean.setQq(null);
			if(!StringUtils.equals(ubean.getProvince(), user.getProvince()))
				ubean.setProvince(user.getProvince());
			
			if(!StringUtils.equals(ubean.getCity(), user.getCity()))
				ubean.setCity(user.getCity());
			
			if(user.getRemovePortrait()==1)
				ubean.setPortrait(null);
			else{
				String portrait_uri = handleUserPortrait(ubean.getId(), user.getPortrait());
				if(StringUtils.isNotEmpty(portrait_uri))
					ubean.setPortrait(portrait_uri);
			}
			try {
				DLOGUserManager.update(ubean);
				//更新session中的用户资料
				UserLoginManager.updateLoginUser(request, ubean);		
			} catch (Exception e) {
				msgs.add("result", new ActionMessage("error.database", e
						.getMessage()));
			}
			
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}
	
	private String getPortraitPath(String uri){
		initPortraitPath();
		StringBuffer path = new StringBuffer(g_portrait_path);
		path.append(StringUtils.replace(uri, "/", File.separator));
		return path.toString();
	}
	
	private final static Object sync_portrait_upload = new Object();
	/**
	 * 处理用户上传的头像
	 * @param pFile
	 * @return
	 * @throws IOException 
	 */
	private String handleUserPortrait(int userid, FormFile pFile) throws IOException{
		if(pFile==null)
			return null;
		//获取图像的扩展名
		String extendName = StringUtils.getFileExtend(pFile.getFileName());
		if(StringUtils.isEmpty(extendName))
			return null;
		//判断是否为图像文件
		if(!ImageUtils.isImage(extendName))
			return null;
		extendName = extendName.toLowerCase();
		//计算图像存放的路径
		StringBuffer fileName = new StringBuffer();
		fileName.append(userid / 10000);
		fileName.append('/');
		fileName.append(userid);
		fileName.append('.');
		fileName.append(extendName);
		String img_path = getPortraitPath(fileName.toString());
		File img = new File(img_path);
		File img_dir = img.getParentFile();
		if(!img_dir.exists()){
			synchronized(sync_portrait_upload){
				if(!img_dir.mkdirs())
					throw new IOException("Cannot make directory: " + img_dir.getParent());
			}
		}
		//判断图像的大小并根据需要进行调整
		BufferedImage orig_portrait = (BufferedImage)ImageIO.read(pFile.getInputStream());
		int preview_width = Math.min(PORTRAIT_WIDTH, orig_portrait.getWidth());
		int preview_height= Math.min(PORTRAIT_HEIGHT,orig_portrait.getHeight());
		if(preview_width != orig_portrait.getWidth() || preview_height!=orig_portrait.getHeight()){
			ImageUtils.createPreviewImage(pFile.getInputStream(), img_path,
					preview_width, preview_height);
		}
		else{
			ImageUtils.writeToFile(pFile, img_path);
		}
		return fileName.toString();
	}
	
	/**
	 * 用户注册
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @see com.liusoft.dlog4j.action.UserAction#doCreate(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
	 */
	protected ActionForward doCreateUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		// 验证用户注册表单
		user.validateUserForm(request, msgs, true);
		if (msgs.isEmpty()) {
			UserBean ubean = user.formToBean();
			try {
				ubean.setLastAddr(request.getRemoteAddr());
				ubean.setMobile(RequestUtils.getRequestMobile(request));
				ubean.setResume(super.filterScriptAndStyle(ubean.getResume()));
				DLOGUserManager.create(ubean);
				// 执行登录过程
				UserLoginManager.loginUser(request, response, ubean, user.getKeepDays());
			} catch (Exception e) {
				msgs.add("result", new ActionMessage("error.database", e.getMessage()));
			}
		} else {
			saveMessages(request, msgs);
			return mapping.findForward("reg");
		}

		String fromPage = user.getFromPage();
		
		if (StringUtils.isNotEmpty(fromPage))
			return new ActionForward(fromPage, true);
		
		if(user.getSid()>0)
			return makeForward(mapping.findForward("main"), user.getSid());
		
		return mapping.findForward("home");
	}

	/**
	 * 创建个人网记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doCreateSite(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int sid = -1;
		SiteForm sform = (SiteForm) form;
		validateClientId(request, sform);
		ActionMessages msgs = new ActionMessages();
		
		do{
			//验证表单
			if(StringUtils.isEmpty(sform.getUniqueName())){
				msgs.add("uniqueName", new ActionMessage("error.site_uniqueName_empty"));
				break;
			}
			if(sform.getUniqueName().length() < MIN_SITENAME_LEN){
				msgs.add("uniqueName", new ActionMessage("error.site_uniqueName_too_short"));
				break;
			}
			if (!DLOG4JUtils.isLegalSiteName(sform.getUniqueName())) {
				msgs.add("uniqueName", new ActionMessage("error.site_uniqueName_illegal"));
				break;
			}
			if(StringUtils.isEmpty(sform.getFriendlyName())){
				sform.setFriendlyName(sform.getUniqueName());
			}
			if(StringUtils.isEmpty(sform.getTitle())){
				sform.setTitle(sform.getFriendlyName());
			}
			//检查用户是否已登录
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null){
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getStatus()!=UserBean.STATUS_NORMAL){
				msgs.add("user", new ActionMessage("error.user_not_available"));
				break;
			}
			if(loginUser.getOwnSiteId()>0){
				msgs.add("user", new ActionMessage("error.one_site_per_user"));
				break;
			}
			//网站名不允许出现敏感字词
			if(DLOGSecurityManager.IllegalGlossary.existIllegalWord(sform.getFriendlyName())){
				msgs.add("friendlyName", new ActionMessage("error.illegal_glossary"));
				break;
			}
			String uniqueName = sform.getUniqueName().trim();
			String friendlyName = sform.getFriendlyName().trim();
			//检查输入参数的有效性，例如是否有同名存在
			SiteBean sbean = SiteDAO.getSiteByName(uniqueName);
			if(sbean != null){
				msgs.add("uniqueName", new ActionMessage("error.sitename_exists"));
				break;
			}
			sbean = SiteDAO.getSiteByFriendlyName(friendlyName);
			if(sbean != null){
				msgs.add("friendlyName", new ActionMessage("error.site_friendlyName_exists"));
				break;
			}
			//检查无误允许注册
			sbean = new SiteBean();
			sbean.setCreateTime(new Date());
			if(StringUtils.isNotEmpty(sform.getDetail())){
				String detail = super.autoFiltrate(null, sform.getDetail());
				sbean.setDetail(StringUtils.extractText(detail));
			}
			sbean.setFriendlyName(super.autoFiltrate(null, friendlyName));
			if(StringUtils.isNotEmpty(sform.getIcpNumber()))
				sbean.setIcpNumber(sform.getIcpNumber());
			sbean.setStatus(SiteBean.STATUS_NORMAL);
			if(StringUtils.isNotEmpty(sform.getUrl()))
				sbean.setUrl(sform.getUrl());
			sbean.setUniqueName(uniqueName);
			if(StringUtils.isNotEmpty(sform.getTitle()))
				sbean.setTitle(super.autoFiltrate(null, sform.getTitle()));
			else
				sbean.setTitle(sbean.getFriendlyName());
			sbean.setOwner(loginUser);
			//设置初始的相册容量
			int max_photo_size = ConfigDAO.getMaxPhotoSize(-1);
			sbean.getCapacity().setPhotoTotal(max_photo_size);
			
			if(sform.getType()>=SiteBean.SITE_TYPE_INDIVIDUAL && sform.getType()<=SiteBean.SITE_TYPE_PRODUCTION)
				sbean.setType(sform.getType());
			
			SiteDAO.createSite(sbean);
			//修改用户的own_site_id字段值
			sid = sbean.getId();
			loginUser.setOwnSiteId(sid);
			UserLoginManager.updateLoginUser(request, loginUser);
			break;
		}while(true) ;
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			return mapping.getInputForward();
		}
		
		return makeForward(mapping.findForward("main"), sid);
	}

	/**
	 * 修改用户的头像(WML)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @wml_only modify_portrait.vm
	 */
	protected ActionForward doUpdatePortrait(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm user = (UserForm) form;
		super.validateClientId(request, user);
		ActionMessages msgs = new ActionMessages();
		
		while (msgs.isEmpty()) {
			UserBean loginUser = super.getLoginUser(request, response);
			if(loginUser == null) {
				msgs.add("user", new ActionMessage("error.user_not_login"));
				break;
			}
			if(loginUser.getId() != user.getId()){
				msgs.add("user", new ActionMessage("error.access_deny"));
				break;
			}
			UserBean ubean = UserDAO.getUserByID(user.getId());
			if (ubean == null || ubean.getStatus()!=UserBean.STATUS_NORMAL) {
				msgs.add("id", new ActionMessage("error.user_not_available",
						new Integer(user.getId())));
				break;
			}
			if (!StringUtils.equals(ubean.getPassword(), user
					.getPassword())){
				msgs.add("password",
						new ActionMessage("error.user_auth_failed"));
				break;
			}
			if(user.getRemovePortrait()==1)
				ubean.setPortrait(null);
			else{
				String portrait_uri = handleUserPortrait(ubean.getId(), user.getPortrait());
				if(StringUtils.isNotEmpty(portrait_uri))
					ubean.setPortrait(portrait_uri);
			}
			try {
				DLOGUserManager.update(ubean);
				//更新session中的用户资料
				UserLoginManager.updateLoginUser(request, ubean);		
			} catch (Exception e) {
				msgs.add("result", new ActionMessage("error.database", e.getMessage()));
			}
			
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("edituser");
		}

		StringBuffer ext = new StringBuffer();
		ext.append("uid=");
		ext.append(user.getId());
		return makeForward(mapping.findForward("viewuser"), user.getSid(), ext.toString());
	}

}
