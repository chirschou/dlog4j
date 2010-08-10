/*
 *  DiaryAction.java
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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.liusoft.dlog4j.TextCacheManager;
import com.liusoft.dlog4j.base.ClientInfo;
import com.liusoft.dlog4j.beans.CatalogBean;
import com.liusoft.dlog4j.beans.DiaryBean;
import com.liusoft.dlog4j.beans.DiaryOutlineBean;
import com.liusoft.dlog4j.beans.FckUploadFileBean;
import com.liusoft.dlog4j.beans.MusicBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.CatalogDAO;
import com.liusoft.dlog4j.dao.DiaryDAO;
import com.liusoft.dlog4j.dao.FCKUploadFileDAO;
import com.liusoft.dlog4j.dao.MusicDAO;
import com.liusoft.dlog4j.formbean.DiaryForm;
import com.liusoft.dlog4j.search.SearchProxy;
import com.liusoft.dlog4j.util.DateUtils;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 日记相关操作的Action
 * 
 * @author liudong
 */
public class DiaryAction extends ActionBase {

	public final static String DEFAULT_WEATHER = "sunny";

	/**
	 * 将日记标志为精华
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	protected void doMarkDiaryAsElite(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String s_id) throws Exception {
		int diary_id = Integer.parseInt(s_id);
		String msg = "";
		do{
			UserBean loginUser = super.getLoginUser(request, response);
			if(!SiteBean.isSuperior(loginUser)){
				msg = getMessage(request,null,"error.access_deny");
				break;
			}
			boolean elite = "1".equals(request.getParameter("elite"));
			int er = DiaryDAO.markDiaryAsElite(diary_id, elite);
			msg = getMessage(request,null,(er<1)?"error.diary_not_exists":"operation_done");
			break;
		}while(true);
		
		outputPlainMsg(response, msg);
	}
	
	/**
	 * 自动创建日记分类并开始写日记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param s_log_id
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doAutoCreateCatalogAndWrite(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String s_log_id) throws Exception {
		DiaryForm dform = (DiaryForm) form;
		ActionMessages msgs = super.validateSiteOwner(request, response, dform);
		
		do{
			if (!msgs.isEmpty())
				break;
			String catalog = dform.getCatalog();
			if (StringUtils.isEmpty(catalog)) 
				break;
			SiteBean site = super.getSiteBean(request);			
			if (site.getCatalogs().size() > 0)
				break;
			
			CatalogBean cbean = new CatalogBean();
			cbean.setName(catalog);
			cbean.setCreateTime(new Date());
			cbean.setType(CatalogBean.TYPE_GENERAL);
			cbean.setSite(site);
			CatalogDAO.create(cbean, -1, false);
			break;
		}while(true);

		return makeForward(mapping.findForward("addlog"), dform.getSid());
	}

	/**
	 * 从日记垃圾箱中恢复日记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUndelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			String s_log_id) throws Exception {
		int log_id = Integer.parseInt(s_log_id);
		DiaryForm diaryForm = (DiaryForm) form;
		UserBean loginUser = super.getLoginUser(request, response);
		String msg = null;
		String page = "diary";
		while (true) {
			if (loginUser == null) {
				msg = getMessage(request, null, "error.user_not_login");
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msg = getMessage(request, null, "error.user_disabled");
				break;
			}
			DiaryOutlineBean log = DiaryDAO.getDiaryOutlineByID(log_id);
			if (log == null || log.getStatus() != DiaryBean.STATUS_DELETED) {
				msg = getMessage(request, null, "error.diary_not_exists");
				break;
			}
			// 只有站长可以从垃圾箱中恢复日记
			if (loginUser.getOwnSiteId()!=diaryForm.getSid() && !SiteBean.isSuperior(loginUser) && log.getOwner().getId()!=loginUser.getId()){
				msg = getMessage(request, null, "error.access_deny");
				break;
			}
			try {
				DiaryDAO.unDelete(log);
				page = "trash";
			} catch (Exception e) {
				context().log("undelete diary failed.", e);
				msg = getMessage(request, null, "error.database", e
						.getMessage());
			}
			break;
		}
		String uri = makeForward(mapping.findForward(page), diaryForm.getSid())
				.getPath();
		return msgbox(mapping, form, request, response, msg, uri);
	}

	/**
	 * 修改日记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionMessages msgs = new ActionMessages();
		DiaryForm diaryForm = (DiaryForm) form;
		//super.validateClientId(request, diaryForm);
		UserBean loginUser = super.getLoginUser(request, response);
		if (loginUser == null) {
			msgs.add("editlog", new ActionMessage("error.user_not_login"));
		} else {
			DiaryBean diary = DiaryDAO.getDiaryByID(diaryForm.getId());
			if (diary == null) {
				msgs
						.add("editlog", new ActionMessage(
								"error.diary_not_exists"));
			} else if (!DiaryDAO.canUserEditDiary(loginUser, diary)) {
				msgs.add("editlog", new ActionMessage("error.access_deny"));
			} else {
				boolean catalog_can_access = false;
				if (diary.getCatalog().getId() != diaryForm.getCatalogId()) {
					SiteBean site = diary.getSite();
					CatalogBean catalog = null;
					// 站长可以访问站内的任何分类
					if (site.isOwner(loginUser)) {
						catalog = CatalogDAO.getCatalogByID(diaryForm
								.getCatalogId());
						if (catalog.getSite().getId() == site.getId())
							catalog_can_access = true;
					} else {
						// 列出用户在该站点可访问的日记分类
						List catalogs = CatalogDAO.listCatalogs(site, loginUser, true);
						for (int i = 0; catalogs != null && i < catalogs.size(); i++) {
							CatalogBean t_catalog = (CatalogBean) catalogs
									.get(i);
							if (t_catalog.getId() == diaryForm.getCatalogId()) {
								catalog = t_catalog;
								catalog_can_access = true;
								break;
							}
						}
					}
					// 更新日记所属分类
					if (catalog != null) {
						diary.getCatalog().incArticleCount(-1);
						diary.setCatalog(catalog);
						catalog.incArticleCount(1);
					}
				} else
					catalog_can_access = true;
				if (catalog_can_access) {
					SiteBean site = diary.getSite();
					// 更新日记
					boolean updateTags = false;
					if (!StringUtils.equals(diary.getKeyword(), diaryForm
							.getTags())) {
						updateTags = true;
						if (StringUtils.isNotEmpty(diaryForm.getTags()))
							diary.setKeyword(super.autoFiltrate(site, diaryForm.getTags()));
						else {
							diary.setKeyword(null);
						}
					}
					diary.setTitle(super.autoFiltrate(site,diaryForm.getTitle()));
					if (!StringUtils.equals(diaryForm.getContent(), diary.getContent())) {
						diary.setContent(super.autoFiltrate(site,diaryForm.getContent()));
						diary.setSize(diaryForm.getContent().getBytes().length);
						//更新文本缓存(Winter Lau, 2006-5-12)
						if(diary.getStatus() == DiaryBean.STATUS_NORMAL)
							TextCacheManager.updateTextContent(DiaryBean.TYPE_DIARY, diary.getId(), diary.getContent());
					}
					diary.setAuthor(diaryForm.getAuthor());
					if (StringUtils.isNotEmpty(diaryForm.getAuthorUrl()))
						diary.setAuthorUrl(diaryForm.getAuthorUrl());
					diary.setMoodLevel(diaryForm.getMoodLevel());
					if (StringUtils.isNotEmpty(diaryForm.getRefUrl()))
						diary.setRefUrl(diaryForm.getRefUrl());
					diary.setReplyNotify(diaryForm.getNotify());
					diary.setWeather(diaryForm.getWeather());
					diary.setModifyTime(new Date());
					if (diaryForm.getBgSound() != -1) {
						// 检查背景音乐是否有效
						MusicBean song = MusicDAO.getMusicByID(diaryForm.getBgSound());
						if (song != null
								&& song.getSite().getId() == diary.getSite().getId()) {
							diary.setBgSound(song);
						}
					}
					try {
						// 检索上传的信息
						pickupUploadFileItems(request, response, loginUser
								.getId(), diary.getSite(), diary.getId(), DiaryBean.TYPE_DIARY);
						DiaryDAO.update(diary, updateTags);
					} catch (Exception e) {
						context().log("update diary failed.", e);
						msgs.add("editlog", new ActionMessage("error.database",
								e.getMessage()));
					}
				} else
					msgs.add("log", new ActionMessage("error.catalog_deny",
							new Integer(diaryForm.getCatalogId())));
			}
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("editlog");
		}
		if (diaryForm.getStatus() == DiaryBean.STATUS_DRAFT) {
			return makeForward(mapping.findForward("draft"), diaryForm.getSid());
		}
		return makeForward(mapping.findForward("showlog"), diaryForm.getSid(),
				"log_id", diaryForm.getId());
	}

	/**
	 * 清空垃圾箱
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doCleanupTrash(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DiaryForm diaryForm = (DiaryForm) form;
		UserBean loginUser = super.getLoginUser(request, response);
		String msg = null;
		if (loginUser == null) {
			msg = getMessage(request, null, "error.user_not_login");
		} else {
			if (loginUser.getOwnSiteId()!=diaryForm.getSid()) {
				msg = getMessage(request, null, "error.access_deny");
			} else {
				try {
					DiaryDAO.cleanupTrash(diaryForm.getSid());
					msg = getMessage(request, null, "trash.empty");
				} catch (Exception e) {
					context().log("delete diary failed.", e);
					msg = getMessage(request, null, "error.database", e
							.getMessage());
				}
			}
		}
		String uri = makeForward(mapping.findForward("trash"),
				diaryForm.getSid()).getPath();
		return msgbox(mapping, form, request, response, msg, uri);
	}

	/**
	 * 彻底删除日记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteFromTrash(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DiaryForm diaryForm = (DiaryForm) form;
		UserBean loginUser = super.getLoginUser(request, response);
		String msg = null;
		if (loginUser == null) {
			msg = getMessage(request, null, "error.user_not_login");
		} else {
			DiaryBean diary = DiaryDAO.getDiaryByID(diaryForm.getId());
			if (diary == null) {
				msg = getMessage(request, null, "error.diary_not_exists");
			} else if (!DiaryDAO.canUserEditDiary(loginUser, diary)) {
				msg = getMessage(request, null, "error.access_deny");
			} else {
				try {
					DiaryDAO.forceDelete(diaryForm.getId());
					//msg = getMessage(request, null, "diary.force_deleted");
				} catch (Exception e) {
					context().log("delete diary failed.", e);
					msg = getMessage(request, null, "error.database", e
							.getMessage());
				}
			}
		}
		
		String fromPage = diaryForm.getFromPage();
		
		if (StringUtils.isNotEmpty(fromPage))
			return msgbox(mapping, form, request, response, msg, fromPage);
		
		return makeForward(mapping.findForward("trash"), diaryForm.getSid());
	}

	/**
	 * 锁定日记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doLock(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DiaryForm diaryForm = (DiaryForm) form;
		UserBean loginUser = super.getLoginUser(request, response);
		String msg = null;
		if (loginUser == null) {
			msg = getMessage(request, null, "error.user_not_login");
		} else {
			DiaryBean diary = DiaryDAO.getDiaryByID(diaryForm.getId());
			if (diary == null) {
				msg = getMessage(request, null, "error.diary_not_exists");
			} else if (!DiaryDAO.canUserEditDiary(loginUser, diary)) {
				msg = getMessage(request, null, "error.access_deny");
			} else {
				try {
					DiaryDAO.lock(diaryForm.getId());
					msg = getMessage(request, null, "diary.locked");
				} catch (Exception e) {
					context().log("delete diary failed.", e);
					msg = getMessage(request, null, "error.database", e
							.getMessage());
				}
			}
		}
		
		String fromPage = diaryForm.getFromPage();
		
		if (StringUtils.isNotEmpty(fromPage))
			return msgbox(mapping, form, request, response, msg, fromPage);
		return makeForward(mapping.findForward("diary"), diaryForm.getSid());
	}

	/**
	 * 日记解锁
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUnLock(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DiaryForm diaryForm = (DiaryForm) form;
		UserBean loginUser = super.getLoginUser(request, response);
		String msg = null;
		if (loginUser == null) {
			msg = getMessage(request, null, "error.user_not_login");
		} else {
			DiaryBean diary = DiaryDAO.getDiaryByID(diaryForm.getId());
			if (diary == null) {
				msg = getMessage(request, null, "error.diary_not_exists");
			} else if (!DiaryDAO.canUserEditDiary(loginUser, diary)) {
				msg = getMessage(request, null, "error.access_deny");
			} else {
				try {
					DiaryDAO.unlock(diaryForm.getId());
					msg = getMessage(request, null, "diary.unlocked");
				} catch (Exception e) {
					context().log("delete diary failed.", e);
					msg = getMessage(request, null, "error.database", e
							.getMessage());
				}
			}
		}

		String fromPage = diaryForm.getFromPage();
		
		if (StringUtils.isNotEmpty(fromPage))
			return msgbox(mapping, form, request, response, msg, fromPage);
		return makeForward(mapping.findForward("diary"), diaryForm.getSid());
	}

	/**
	 * 删除日记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DiaryForm diaryForm = (DiaryForm) form;
		UserBean loginUser = super.getLoginUser(request, response);
		String msg = null;
		if (loginUser == null) {
			msg = getMessage(request, null, "error.user_not_login");
		} else {
			DiaryBean diary = DiaryDAO.getDiaryByID(diaryForm.getId());
			if (diary == null) {
				msg = getMessage(request, null, "error.diary_not_exists");
			} else if (!DiaryDAO.canUserEditDiary(loginUser, diary)) {
				msg = getMessage(request, null, "error.access_deny");
			} else {
				try {
					DiaryDAO.delete(diary);
					SearchProxy.remove(diary);
					TextCacheManager.deleteTextContent(DiaryBean.TYPE_DIARY, diary.getId());
					msg = getMessage(request, null, "diary.deleted");
				} catch (Exception e) {
					context().log("delete diary failed.", e);
					msg = getMessage(request, null, "error.database", e
							.getMessage());
				}
			}
		}
		
		String fromPage = diaryForm.getFromPage();
		
		if (StringUtils.isNotEmpty(fromPage))
			return msgbox(mapping, form, request, response, msg, fromPage);
		return makeForward(mapping.findForward("diary"), diaryForm.getSid());
	}

	/**
	 * 删除附件
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteAttachement(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_file_id)
			throws Exception {
		int file_id = Integer.parseInt(s_file_id);
		UserBean loginUser = super.getLoginUser(request, response);
		if (loginUser != null) {
			String ssn_id = RequestUtils.getDlogSessionId(request);
			FCKUploadFileDAO.deleteFileById(loginUser.getId(), ssn_id, file_id);
		}

		DiaryForm diaryForm = (DiaryForm) form;
		return makeForward(mapping.findForward("addlog"), diaryForm.getSid());
	}

	/**
	 * 发表日记
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param identity 如果该值为WML则表示来自WML页面
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doPublishLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String identity)
			throws Exception {
		ActionMessages msgs = new ActionMessages();
		DiaryForm log = (DiaryForm) form;
		//super.validateClientId(request, log);
		while (true) {
			// 检测日记表单域的值
			if (StringUtils.isEmpty(log.getTitle())) {
				msgs.add("title", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			if (StringUtils.isEmpty(log.getContent())) {
				msgs.add("content",
						new ActionMessage("error.empty_not_allowed"));
				break;
			}
			UserBean loginUser = super.getLoginUser(request, response);
			if (loginUser == null) {
				msgs.add("log", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("log", new ActionMessage("error.user_not_available"));
				break;
			}
			SiteBean site = super.getSiteByID(log.getSid());
			if (site == null) {
				msgs.add("log", new ActionMessage("error.site_not_available"));
				break;
			}
			boolean catalog_can_access = false;
			CatalogBean catalog = null;
			// 站长可以访问站内的任何分类
			if (site.isOwner(loginUser)) {
				catalog = CatalogDAO.getCatalogByID(log.getCatalogId());
				if (catalog!=null && catalog.getSite().getId() == site.getId())
					catalog_can_access = true;
			} else {
				// 列出用户在该站点可访问的日记分类
				List catalogs = CatalogDAO.listCatalogs(site, loginUser, true);
				for (int i = 0; catalogs != null && i < catalogs.size(); i++) {
					CatalogBean t_catalog = (CatalogBean) catalogs.get(i);
					if (t_catalog.getId() == log.getCatalogId()) {
						catalog = t_catalog;
						catalog_can_access = true;
						break;
					}
				}
			}
			if (!catalog_can_access) {
				msgs.add("log", new ActionMessage("error.catalog_deny",
						new Integer(log.getCatalogId())));
				break;
			}
			// 用户欲操作的日记分类被允许
			if (StringUtils.isEmpty(log.getWeather()))
				log.setWeather(DEFAULT_WEATHER);
			if (StringUtils.isEmpty(log.getAuthor()))
				log.setAuthor(loginUser.getNickname());
			if (StringUtils.isEmpty(log.getTags()))
				log.setTags(null);
			if (StringUtils.isEmpty(log.getAuthorUrl()))
				log.setAuthorUrl(null);
			if (StringUtils.isEmpty(log.getRefUrl()))
				log.setRefUrl(null);
			// 创建JournalBean
			DiaryBean journal = new DiaryBean();
			journal.setOwner(loginUser);
			journal.setSite(site);
			journal.setAuthor(super.autoFiltrate(site, log.getAuthor()));
			journal.setAuthorUrl(log.getAuthorUrl());
			journal.setCatalog(catalog);
			journal.setClient(new ClientInfo(request, log.getClientType()));
			// 根据网站的安全标志决定是否对内容进行敏感字词过滤

			String ssn_id = RequestUtils.getDlogSessionId(request);
			boolean wml = WML_IDENTITY.equalsIgnoreCase(identity);
			String content = autoCompileContent(request, site, log.getContent(), loginUser.getId(), ssn_id, wml);
			journal.setContent(content);
			journal.setSize(content.getBytes().length);
			journal.setTitle(super.autoFiltrate(site, log.getTitle()));
			journal.setKeyword(super.autoFiltrate(site, log.getTags()));
			
			journal.setMoodLevel(log.getMoodLevel());
			journal.setRefUrl(log.getRefUrl());
			journal.setReplyNotify(log.getNotify());
			journal.setStatus(DiaryBean.STATUS_NORMAL);
			journal.setWeather(log.getWeather());
			journal.setWriteTime(DateUtils.mergeDateTime(log.getWriteDate(), log.getWriteTime()).getTime());
			Date curTime = new Date();
			if(journal.getWriteTime().after(curTime))
				journal.setWriteTime(curTime);
			// 检查背景音乐是否有效
			MusicBean song = MusicDAO.getMusicByID(log.getBgSound());
			if (song != null && song.getSite().getId() == site.getId()) {
				journal.setBgSound(song);
			}
			DiaryDAO.create(journal, log.getBookmark() == 1);
			// 检索上传的信息
			pickupUploadFileItems(request, response, loginUser.getId(), site, journal
					.getId(), DiaryBean.TYPE_DIARY);

			if (log.getRefUrl() != null) {
				trackBack(journal, log.getRefUrl());
			}
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("addlog");
		}
		return makeForward(mapping.findForward("diary"), log.getSid());
	}

	/**
	 * 直接发表草稿
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doPublishDraftDirectly(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionMessages msgs = new ActionMessages();
		DiaryForm log = (DiaryForm) form;

		while (true) {
			UserBean loginUser = super.getLoginUser(request, response);
			if (loginUser == null) {
				msgs.add("log", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("log", new ActionMessage("error.user_not_available"));
				break;
			}
			SiteBean site = super.getSiteByID(log.getSid());
			if (site == null) {
				msgs.add("log", new ActionMessage("error.site_not_available"));
				break;
			}
			// 用户欲操作的日记分类被允许
			DiaryBean journal = DiaryDAO.getDiaryByID(log.getId());
			if (journal == null
					|| journal.getStatus() != DiaryBean.STATUS_DRAFT
					|| journal.getOwner().getId() != loginUser.getId()) {
				msgs.add("draft", new ActionMessage("error.draft_not_exists"));
				break;
			}
			boolean catalog_can_access = false;
			// 站长可以访问站内的任何分类
			if (site.isOwner(loginUser)) {
				catalog_can_access = true;
			} else {
				// 列出用户在该站点可访问的日记分类
				List catalogs = CatalogDAO.listCatalogs(site, loginUser, true);
				for (int i = 0; catalogs != null && i < catalogs.size(); i++) {
					CatalogBean t_catalog = (CatalogBean) catalogs.get(i);
					if (t_catalog.getId() == log.getCatalogId()) {
						catalog_can_access = true;
						break;
					}
				}
			}
			if (!catalog_can_access) {
				msgs.add("log", new ActionMessage("error.catalog_deny",
						new Integer(log.getCatalogId())));
				break;
			}
			// 读取并更新草稿件，然后把状态设为正常
			journal.setClient(new ClientInfo(request, log.getClientType()));
			journal.setViewCount(0);
			journal.setStatus(DiaryBean.STATUS_NORMAL);
			journal.setWriteTime(new Date());
			DiaryDAO.flush();
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("editlog");
		}
		return makeForward(mapping.findForward("diary"), log.getSid());
	}
	/**
	 * 发表草稿
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param identity 如果该值为WML则表示来自WML页面
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doPublishDraft(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String identity)
			throws Exception {
		ActionMessages msgs = new ActionMessages();
		DiaryForm log = (DiaryForm) form;
		//super.validateClientId(request, log);
		while (true) {
			// 检测日记表单域的值
			if (StringUtils.isEmpty(log.getTitle())) {
				msgs.add("title", new ActionMessage("error.empty_not_allowed"));
				break;
			}
			if (StringUtils.isEmpty(log.getContent())) {
				msgs.add("content",
						new ActionMessage("error.empty_not_allowed"));
				break;
			}
			UserBean loginUser = super.getLoginUser(request, response);
			if (loginUser == null) {
				msgs.add("log", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("log", new ActionMessage("error.user_not_available"));
				break;
			}
			SiteBean site = super.getSiteByID(log.getSid());
			if (site == null) {
				msgs.add("log", new ActionMessage("error.site_not_available"));
				break;
			}
			boolean catalog_can_access = false;
			CatalogBean catalog = null;
			// 站长可以访问站内的任何分类
			if (site.isOwner(loginUser)) {
				catalog = CatalogDAO.getCatalogByID(log.getCatalogId());
				if (catalog.getSite().getId() == site.getId())
					catalog_can_access = true;
			} else {
				// 列出用户在该站点可访问的日记分类
				List catalogs = CatalogDAO.listCatalogs(site, loginUser, true);
				for (int i = 0; catalogs != null && i < catalogs.size(); i++) {
					CatalogBean t_catalog = (CatalogBean) catalogs.get(i);
					if (t_catalog.getId() == log.getCatalogId()) {
						catalog = t_catalog;
						catalog_can_access = true;
						break;
					}
				}
			}
			if (!catalog_can_access) {
				msgs.add("log", new ActionMessage("error.catalog_deny",
						new Integer(log.getCatalogId())));
				break;
			}
			// 用户欲操作的日记分类被允许
			DiaryBean journal = DiaryDAO.getDiaryByID(log.getId());
			if (journal == null
					|| journal.getStatus() != DiaryBean.STATUS_DRAFT) {
				msgs.add("draft", new ActionMessage("error.draft_not_exists"));
				break;
			}
			// 检查背景音乐是否有效
			MusicBean song = MusicDAO.getMusicByID(log.getBgSound());
			if (song != null && song.getSite().getId() == site.getId()) {
				journal.setBgSound(song);
			}
			if (StringUtils.isEmpty(log.getWeather()))
				journal.setWeather(DEFAULT_WEATHER);
			else
				journal.setWeather(log.getWeather());
			if (StringUtils.isEmpty(log.getAuthor()))
				journal.setAuthor(loginUser.getNickname());
			else
				journal.setAuthor(log.getAuthor());
			if (StringUtils.isEmpty(log.getTags()))
				journal.setKeyword(null);
			else
				journal.setKeyword(log.getTags());
			if (StringUtils.isEmpty(log.getAuthorUrl()))
				journal.setAuthorUrl(null);
			else
				journal.setAuthorUrl(log.getAuthorUrl());
			if (StringUtils.isEmpty(log.getRefUrl()))
				journal.setRefUrl(null);
			else
				journal.setRefUrl(log.getRefUrl());
			// 读取并更新草稿件，然后把状态设为正常
			journal.setCatalog(catalog);
			journal.setClient(new ClientInfo(request, log.getClientType()));
			String ssn_id = RequestUtils.getDlogSessionId(request);
			boolean wml = WML_IDENTITY.equalsIgnoreCase(identity);
			String content = autoCompileContent(request, site, log.getContent(), loginUser.getId(), ssn_id, wml);
			journal.setContent(content);
			journal.setSize(content.getBytes().length);
			journal.setMoodLevel(log.getMoodLevel());
			journal.setReplyNotify(log.getNotify());
			journal.setViewCount(0);
			journal.setStatus(DiaryBean.STATUS_NORMAL);
			journal.setTitle(log.getTitle());
			journal.setWriteTime(new Date());
			DiaryDAO.create(journal, log.getBookmark() == 1);
			// 检索上传的信息
			pickupUploadFileItems(request, response, loginUser.getId(), site, journal
					.getId(), DiaryBean.TYPE_DIARY);
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("editlog");
		}
		return makeForward(mapping.findForward("diary"), log.getSid());
	}

	/**
	 * TODO:执行Trackback过程
	 * 
	 * @param log
	 * @param ref_url
	 */
	private void trackBack(DiaryBean log, String ref_url) {

	}

	/**
	 * 将日记存为草稿
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doSaveAsDraft(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionMessages msgs = new ActionMessages();
		DiaryForm log = (DiaryForm) form;
		//super.validateClientId(request, log);
		UserBean loginUser = super.getLoginUser(request, response);
		while (true) {
			if (loginUser == null) {
				msgs.add("log", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("log", new ActionMessage("error.user_not_available"));
				break;
			}
			
			SiteBean site = super.getSiteByID(log.getSid());
			if (site == null) {
				msgs.add("log", new ActionMessage("error.site_not_available"));
				break;
			}
			boolean catalog_can_access = false;
			CatalogBean catalog = null;
			// 站长可以访问站内的任何分类
			if (site.isOwner(loginUser)) {
				catalog = CatalogDAO.getCatalogByID(log.getCatalogId());
				if (catalog.getSite().getId() == site.getId())
					catalog_can_access = true;
			} else {
				// 列出用户在该站点可访问的日记分类
				List catalogs = CatalogDAO.listCatalogs(site, loginUser, true);
				for (int i = 0; catalogs != null && i < catalogs.size(); i++) {
					CatalogBean t_catalog = (CatalogBean) catalogs.get(i);
					if (t_catalog.getId() == log.getCatalogId()
							&& t_catalog.getSite().getId() == site.getId()) {
						catalog = t_catalog;
						catalog_can_access = true;
						break;
					}
				}
			}
			//用户欲操作的日记分类不被允许
			if(!catalog_can_access){
				msgs.add("log", new ActionMessage("error.catalog_deny",
						new Integer(log.getCatalogId())));
				break;
			}
			// 检测日记表单域的值
			if (StringUtils.isEmpty(log.getTitle())){
				msgs.add("title", new ActionMessage(
						"error.empty_not_allowed"));
				break;
			}
			if (StringUtils.isEmpty(log.getContent())){
				msgs.add("content", new ActionMessage(
						"error.empty_not_allowed"));
				break;
			}
			if (StringUtils.isEmpty(log.getWeather()))
				log.setWeather(DEFAULT_WEATHER);
			if (StringUtils.isEmpty(log.getAuthor()))
				log.setAuthor(loginUser.getNickname());
			if (StringUtils.isEmpty(log.getTags()))
				log.setTags(null);
			if (StringUtils.isEmpty(log.getAuthorUrl()))
				log.setAuthorUrl(null);
			if (StringUtils.isEmpty(log.getRefUrl()))
				log.setRefUrl(null);
			// 创建JournalBean
			DiaryBean journal = new DiaryBean();
			journal.setOwner(loginUser);
			journal.setSite(site);
			journal.setAuthor(super.autoFiltrate(site,log.getAuthor()));
			journal.setAuthorUrl(log.getAuthorUrl());
			journal.setCatalog(catalog);
			journal.setClient(new ClientInfo(request, log
					.getClientType()));
			journal.setContent(super.autoFiltrate(site,log.getContent()));
			journal.setMoodLevel(log.getMoodLevel());
			journal.setRefUrl(log.getRefUrl());
			journal.setReplyNotify(log.getNotify());
			journal.setStatus(DiaryBean.STATUS_DRAFT);
			journal.setKeyword(super.autoFiltrate(site,log.getTags()));
			journal.setTitle(super.autoFiltrate(site,log.getTitle()));
			journal.setWeather(log.getWeather());
			journal.setWriteTime(DateUtils.mergeDateTime(log.getWriteDate(), log.getWriteTime()).getTime());
			Date curTime = new Date();
			if(journal.getWriteTime()==null || journal.getWriteTime().after(curTime))
				journal.setWriteTime(curTime);
			DiaryDAO.create(journal, false);
			//检索上传的信息
			super.pickupUploadFileItems(request, response, loginUser
					.getId(), site, journal.getId(), DiaryBean.TYPE_DIARY);
		
			break;
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("addlog");
		}
		return makeForward(mapping.findForward("draft"), log.getSid());
	}
	
	/**
	 * 自动汇编内容
	 * @param site
	 * @param content
	 * @param uid
	 * @param ssn_id
	 * @param wml
	 * @return
	 */
	private String autoCompileContent(HttpServletRequest req, SiteBean site,
			String content, int uid, String ssn_id, boolean wml) {
		StringBuffer text = new StringBuffer();
		// 根据网站的安全标志决定是否对内容进行敏感字词过滤
		text.append(super.autoFiltrate(site, content));
		if (wml) {
			text.append("<p>");
			// 加载附件
			String context_path = req.getContextPath();
			List files = FCKUploadFileDAO.listOrphanFiles(uid, ssn_id);
			for (int i = 0; files != null && i < files.size(); i++) {
				FckUploadFileBean file = (FckUploadFileBean) files.get(i);
				String uri = context_path + file.getUri();
				text.append(MessageFormat.format(img_pattern, uri));
			}
			text.append("</p>");
		}
		return text.toString();
	}
	
	private final static String img_pattern = "<img src=\"{0}\" alt=\"\"/>";
	
}
