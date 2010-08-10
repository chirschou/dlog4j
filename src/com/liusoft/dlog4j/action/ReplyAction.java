/*
 *  ReplyAction.java
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
package com.liusoft.dlog4j.action;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.htmlparser.Node;
import org.htmlparser.Parser;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.HtmlNodeFilters;
import com.liusoft.dlog4j.MailTransportQueue;
import com.liusoft.dlog4j.base.ClientInfo;
import com.liusoft.dlog4j.base._ReplyBean;
import com.liusoft.dlog4j.beans.DiaryOutlineBean;
import com.liusoft.dlog4j.beans.DiaryReplyBean;
import com.liusoft.dlog4j.beans.PhotoOutlineBean;
import com.liusoft.dlog4j.beans.PhotoReplyBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.DiaryDAO;
import com.liusoft.dlog4j.dao.PhotoDAO;
import com.liusoft.dlog4j.dao.ReplyDAO;
import com.liusoft.dlog4j.formbean.ReplyForm;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;

import com.liusoft.dlog4j.util.MailSender;

/**
 * 评论操作相关的Action类
 * 
 * @author liudong
 */
public class ReplyAction extends ActionBase {

	private final static Log log = LogFactory.getLog(ReplyAction.class);
	
	/**
	 * 修改照片评论
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdatePhotoReply(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReplyForm reply = (ReplyForm) form;
		//验证客户端安全识别码
		validateClientId(request, reply);
		ActionMessages msgs = new ActionMessages();
		UserBean loginUser = super.getLoginUser(request, response);
		while (loginUser != null) {
			if (StringUtils.isEmpty(reply.getContent())) {
				msgs.add("reply", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			SiteBean site = super.getSiteByID(reply.getSid());
			if (site == null) {
				msgs.add("reply", new ActionMessage("error.site_not_available"));
				break;
			}
			_ReplyBean rbean = ReplyDAO.getReply(PhotoReplyBean.class, reply
					.getReply_id());
			if (rbean!=null && rbean.getSite().getId() == site.getId()) {
				String content = StringUtils.abbreviate(super.autoFiltrate(
					null, reply.getContent()), MAX_REPLY_LENGTH);
				rbean.setContent(super.filterScriptAndStyle(content));
				rbean.setAuthor(reply.getAuthor());				
				rbean.setOwnerOnly(reply.getOwnerOnly());
				if (StringUtils.isNotEmpty(reply.getAuthorURL()))
					rbean.setAuthorURL(reply.getAuthorURL());
				if (StringUtils.isNotEmpty(reply.getAuthorEmail()))
					rbean.setAuthorEmail(reply.getAuthorEmail());
				ReplyDAO.updateReply(rbean);
			}
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("showphoto");
		}
		StringBuffer ext = new StringBuffer("pid=");
		ext.append(reply.getParentId());
		ext.append('#');
		ext.append(reply.getReply_id());
		return makeForward(mapping.findForward("showphoto"), reply.getSid(),
				ext.toString());
	}

	/**
	 * 修改日记评论
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateDiaryReply(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReplyForm reply = (ReplyForm) form;
		//验证客户端安全识别码
		validateClientId(request, reply);
		ActionMessages msgs = new ActionMessages();
		UserBean loginUser = super.getLoginUser(request, response);
		while (loginUser != null) {
			if (StringUtils.isEmpty(reply.getContent())) {
				msgs.add("reply", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			SiteBean site = super.getSiteByID(reply.getSid());
			if (site == null) {
				msgs.add("reply", new ActionMessage("error.site_not_available"));
				break;
			}
			_ReplyBean rbean = ReplyDAO.getReply(DiaryReplyBean.class, reply.getReply_id());
			if (rbean!=null && rbean.getSite().getId() == site.getId()) {
				String content = StringUtils.abbreviate(super.autoFiltrate(
						null, reply.getContent()), MAX_REPLY_LENGTH);
				rbean.setContent(super.filterScriptAndStyle(content));
				rbean.setAuthor(reply.getAuthor());				
				rbean.setOwnerOnly(reply.getOwnerOnly());
				if (StringUtils.isNotEmpty(reply.getAuthorURL()))
					rbean.setAuthorURL(reply.getAuthorURL());
				if (StringUtils.isNotEmpty(reply.getAuthorEmail()))
					rbean.setAuthorEmail(reply.getAuthorEmail());
				ReplyDAO.updateReply(rbean);
			}
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("showlog");
		}
		return makeForward(mapping.findForward("showlog"), reply.getSid(),
				"log_id", reply.getParentId());
	}

	/**
	 * 删除照片评论
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeletePhotoReply(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String s_reply_id) throws Exception {
		String msg = null;
		ReplyForm reply = (ReplyForm) form;
		int reply_id = Integer.parseInt(s_reply_id);
		UserBean loginUser = super.getLoginUser(request, response);
		while (loginUser != null) {
			SiteBean site = super.getSiteByID(reply.getSid());
			if (site == null) {
				msg = getMessage(request, null, "error.site_not_available");
				break;
			}
			PhotoReplyBean rbean = (PhotoReplyBean) ReplyDAO.getReply(
					PhotoReplyBean.class, reply_id);
			if (rbean == null)
				break;
			if (rbean.getSite().getId() != reply.getSid()) {
				msg = getMessage(request, null, "error.param");
				break;
			}
			if (!site.isOwner(loginUser)
					&& !isReplyBelongToUser(rbean, loginUser.getId())) {
				msg = getMessage(request, null, "error.access_deny");
				break;
			}
			PhotoDAO.deletePhotoReply(rbean);
			break;
		}
		
		String fromPage = reply.getFromPage();
		
		if (StringUtils.isNotEmpty(fromPage))
			return msgbox(mapping, form, request, response, msg, fromPage);
		return makeForward(mapping.findForward("photo"), reply.getSid());
	}

	/**
	 * 删除日记评论
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteDiaryReply(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String s_reply_id) throws Exception {
		String msg = null;
		ReplyForm reply = (ReplyForm) form;
		int reply_id = Integer.parseInt(s_reply_id);
		UserBean loginUser = super.getLoginUser(request, response);
		while (loginUser != null) {
			SiteBean site = super.getSiteByID(reply.getSid());
			if (site == null) {
				msg = getMessage(request, null, "error.site_not_available");
				break;
			}
			DiaryReplyBean rbean = (DiaryReplyBean) ReplyDAO.getReply(
					DiaryReplyBean.class, reply_id);
			if (rbean == null)
				break;
			if (rbean.getSite().getId() != reply.getSid()) {
				msg = getMessage(request, null, "error.param");
				break;
			}
			if (!site.isOwner(loginUser)
					&& !isReplyBelongToUser(rbean, loginUser.getId())) {
				msg = getMessage(request, null, "error.access_deny");
				break;
			}
			DiaryDAO.deleteDiaryReply(rbean);
			break;
		}
		
		String fromPage = reply.getFromPage();
		
		if (StringUtils.isNotEmpty(fromPage))
			return msgbox(mapping, form, request, response, msg, fromPage);
		return makeForward(mapping.findForward("diary"), reply.getSid());
	}

	protected boolean isReplyBelongToUser(_ReplyBean rb, int userid) {
		return (rb.getUser() != null && rb.getUser().getId() == userid);
	}

	/**
	 * 发表日记评论
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doAddDiaryReply(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReplyForm reply = (ReplyForm) form;
		//验证客户端安全识别码
		validateClientId(request, reply);
		ActionMessages msgs = new ActionMessages();
		do{
			if (StringUtils.isEmpty(reply.getContent())){
				msgs.add("reply", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			if(reply.getContent().getBytes().length >= 3000){
				msgs.add("reply", new ActionMessage("error.reply_too_long"));
				break;
			}
			UserBean loginUser = super.getLoginUser(request, response);
			SiteBean site = super.getSiteByID(reply.getSid());
			if (site == null) {
				msgs.add("reply", new ActionMessage("error.site_not_available"));
				break;
			}
			//检查黑名单
			if(loginUser!=null && isUserInBlackList(site, loginUser)){
				msgs.add("topic", new ActionMessage("error.user_in_blacklist"));
				break;
			} 
			DiaryOutlineBean diary = DiaryDAO.getDiaryOutlineByID(reply.getParentId());
			if (diary == null || diary.getSite().getId() != reply.getSid()) {
				msgs.add("reply", new ActionMessage("error.param"));
				break;
			} 			
			if(diary.getLock()==1) {
				msgs.add("reply", new ActionMessage("error.diary.locked"));
				break;
			} 
			// 补齐参数并写入数据
			DiaryReplyBean rbean = new DiaryReplyBean();
			rbean.setUser(loginUser);			
			rbean.setAuthor(super.autoFiltrate(site,reply.getAuthor()));
			if (StringUtils.isNotEmpty(reply.getAuthorURL()))
				rbean.setAuthorURL(reply.getAuthorURL());
			if (StringUtils.isNotEmpty(reply.getAuthorEmail()))
				rbean.setAuthorEmail(reply.getAuthorEmail());
			rbean.setClient(new ClientInfo(request, reply
					.getClientType()));
			String content = StringUtils.abbreviate(super.autoFiltrate(null,
					reply.getContent()), MAX_REPLY_LENGTH);
			rbean.setContent(super.filterScriptAndStyle(content));
			rbean.setDiary(diary);
			rbean.setReplyTime(new Date());
			rbean.setSite(site);
			rbean.setStatus(DiaryReplyBean.STATUS_NORMAL);
			rbean.setOwnerOnly(reply.getOwnerOnly());
			DiaryDAO.createDiaryReply(rbean);
			// 判断是否需要邮件提醒
			if (diary.getReplyNotify() == 1) {
				String email = diary.getOwner().getContactInfo()
						.getEmail();
				if (StringUtils.isEmail(email)) {
					this.sendReplyNotify(request, rbean.getSite().getId(), rbean);
				}
			}
			break;
		}while(true);
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("diary-enter-reply");
		}
		return makeForward(mapping.findForward("showlog"), reply.getSid(),
				"log_id", reply.getParentId());
	}

	/**
	 * 发表照片评论
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doAddPhotoReply(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReplyForm reply = (ReplyForm) form;
		//验证客户端安全识别码
		validateClientId(request, reply);
		ActionMessages msgs = new ActionMessages();
		PhotoReplyBean rbean = new PhotoReplyBean();
		do{
			if (StringUtils.isEmpty(reply.getContent())){
				msgs.add("reply", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			if(reply.getContent().getBytes().length >= 3000){
				msgs.add("reply", new ActionMessage("error.reply_too_long"));
				break;
			}
			SiteBean site = super.getSiteByID(reply.getSid());
			if (site == null) {
				msgs.add("reply", new ActionMessage("error.site_not_available"));
				break;
			}
			UserBean loginUser = super.getLoginUser(request, response);
			//检查黑名单
			if(loginUser!=null && isUserInBlackList(site, loginUser)){
				msgs.add("photo", new ActionMessage("error.user_in_blacklist"));
				break;
			} 
			PhotoOutlineBean photo = PhotoDAO.getPhotoOutlineByID(reply.getParentId());
			if (photo == null || photo.getSite().getId() != reply.getSid()) {
				msgs.add("reply", new ActionMessage("error.param"));
				break;
			}
			if(photo.getLock()==1) {
				msgs.add("reply", new ActionMessage("error.photo.locked"));
				break;
			}
			// 补齐参数并写入数据
			rbean.setUser(loginUser);
			rbean.setAuthor(super.autoFiltrate(site,reply.getAuthor()));
			if (StringUtils.isNotEmpty(reply.getAuthorURL()))
				rbean.setAuthorURL(reply.getAuthorURL());
			if (StringUtils.isNotEmpty(reply.getAuthorEmail()))
				rbean.setAuthorEmail(reply.getAuthorEmail());
			rbean.setClient(new ClientInfo(request, reply
					.getClientType()));
			String content = StringUtils.abbreviate(super.autoFiltrate(null,
					reply.getContent()), MAX_REPLY_LENGTH);
			rbean.setContent(super.filterScriptAndStyle(content));
			rbean.setPhoto(photo);
			rbean.setReplyTime(new Date());
			rbean.setSite(site);
			rbean.setStatus(DiaryReplyBean.STATUS_NORMAL);
			rbean.setOwnerOnly(reply.getOwnerOnly());
			PhotoDAO.createPhotoReply(rbean);
			break;
		}while(true);
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("showphoto");
		}
		StringBuffer ext = new StringBuffer("pid=");
		ext.append(reply.getParentId());
		//ext.append('#');
		//ext.append(rbean.getId());
		return makeForward(mapping.findForward("showphoto"), reply.getSid(),
				ext.toString());
	}

	/**
	 * 发送新评论邮件提醒
	 * 
	 * @param request
	 * @param rbean
	 * @throws Exception
	 */
	protected void sendReplyNotify(HttpServletRequest request, final int site_id,  
			final DiaryReplyBean rbean) throws Exception {
		
		final String contextPath = request.getContextPath();
		final String urlPrefix = RequestUtils.getUrlPrefix(request);
		final String template = super.getReplyNotifyTemplate();
		
		new Thread() {
			public void run() {
				try {
					StringBuffer url = new StringBuffer();
					url.append(urlPrefix);
					url.append(contextPath);
					url.append("/html/diary/showlog.vm?sid=");
					url.append(rbean.getSite().getId());
					url.append("&log_id=");
					url.append(rbean.getDiary().getId());
					url.append("#");
					url.append(rbean.getId());
					String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm")
							.format(new Date());
					// 发送邮件提醒
					String notify_content = MessageFormat.format(template,
							rbean.getDiary().getOwner().getNickname(),
							rbean.getDiary().getTitle(), rbean.getAuthor(),
							url.toString(), curTime, rbean.getContent());
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
					sender.setMailTo(new String[] { rbean.getDiary().getOwner()
							.getContactInfo().getEmail() }, "to");
					MailTransportQueue queue = (MailTransportQueue) getServlet()
							.getServletContext().getAttribute(
									Globals.MAIL_QUEUE);
					// 写入待发送邮件队列
					queue.write(site_id, sender
							.getMimeMessage());
					if(log.isDebugEnabled())
						log.debug("Notification mail was written to the sending queue.");
				} catch (Exception e) {
					log.error("send notification mail failed.", e);
				}
			}
		}.start();
	}
	
}
