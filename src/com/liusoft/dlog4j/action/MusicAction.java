/*
 * 版权所有: 摩网信息科技有限公司 2005
 * 项目：DLOG4J_V3
 * 所在包：com.liusoft.dlog4j.action
 * 文件名：MusicAction.java
 * 创建时间：2005-12-8
 * 创建者：Winter Lau
 */
package com.liusoft.dlog4j.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.beans.MusicBean;
import com.liusoft.dlog4j.beans.MusicBoxBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.MusicDAO;
import com.liusoft.dlog4j.formbean.MusicBoxForm;
import com.liusoft.dlog4j.formbean.MusicForm;
import com.liusoft.dlog4j.search.SearchProxy;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 音乐频道的Action类
 * 
 * @author Winter Lau
 */
public class MusicAction extends ActionBase {

	/**
	 * 用于在其他网站浏览音乐时候可以将自己喜欢的音乐加到自己的音乐盒中
	 * http://localhost/html/music.do?sid=12&eventSubmit_doCollect=232&fromPage=xxxx
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	protected ActionForward doCollect(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_music_id)
			throws Exception {
		MusicForm mform = (MusicForm) form;
		int mid = Integer.parseInt(s_music_id);
		String msg = null;
		do{
			//判断用户是否登录，是否有自己的网站
			SessionUserObject loginUser = super.getLoginUser(request, response);
			if(loginUser==null){
				msg = getMessage(request, null, "error.user_not_login");
				break;
			}
			if(loginUser.getStatus()!=UserBean.STATUS_NORMAL){
				msg = getMessage(request, null, "error.user_disabled");
				break;
			}
			if(loginUser.getOwnSiteId()<=0){
				msg = getMessage(request, null, "error.user_not_have_a_site");
				break;
			}
			//判断用户的个人网记是否有效
			SiteBean toSite = super.getSiteByID(loginUser.getOwnSiteId());
			if(toSite==null){
				msg = super.getMessage(request, null, "error.site_not_available");
				break;
			}
			MusicBean mbean = MusicDAO.getMusicByID(mid);
			if(mbean!=null && mbean.getSite().getId()==mform.getSid())
			{
				MusicBean music = new MusicBean();
				music.setCreateTime(new Date());
				music.setMusicBox(null);
				music.setAlbum(mbean.getAlbum());
				music.setSinger(mbean.getSinger());
				music.setSite(new SiteBean(loginUser.getOwnSiteId()));
				music.setTitle(mbean.getTitle());
				music.setUrl(mbean.getUrl());
				music.setWord(mbean.getWord());
				MusicDAO.addMusic(music);
				msg = getMessage(request, null, "music.collected", music.getTitle());
			}
			break;
		}while(true);
		return msgbox(mapping, form, request, response, msg, mform.getFromPage());
	}
	
	/**
	 * 创建音乐盒
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MusicBoxForm mbox = (MusicBoxForm) form;
		super.validateClientId(request, mbox);
		ActionMessages msgs = validateSiteOwner(request, response, mbox);
		int mboxid = -1;
		if (msgs.isEmpty() && StringUtils.isEmpty(mbox.getName()))
			msgs.add("name", new ActionMessage("error.mbox.name_empty"));
		else if (msgs.isEmpty()) {
			SiteBean site = super.getSiteBean(request);
			MusicBoxBean mbean = new MusicBoxBean();
			mbean.setName(super.autoFiltrate(site, mbox.getName()));
			if (StringUtils.isNotEmpty(mbox.getDesc())){
				String desc = super.autoFiltrate(site, mbox.getDesc());
				mbean.setDesc(super.filterScriptAndStyle(desc));
			}
			mbean.setCreateTime(new Date());
			mbean.setSite(site);
			MusicDAO.createBox(mbean, mbox.getId(), mbox.getDirection() == 1);
			mboxid = mbean.getId();
		}
		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("mbox_add");
		}

		return makeForward(mapping.findForward("music"), mbox.getSid(), "box",
				mboxid);
	}

	/**
	 * 推荐歌曲
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doRecommend(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MusicForm mform = (MusicForm) form;
		super.validateClientId(request, mform);
		ActionMessages msgs = new ActionMessages();
		do{
			if (StringUtils.isEmpty(mform.getTitle())) {
				msgs.add("name", new ActionMessage("error.music.title_empty"));
				break;
			}
			if (StringUtils.isEmpty(mform.getUrl())) {
				msgs.add("url", new ActionMessage("error.music.url_empty"));
				break;
			}
			// 验证是否登录用户
			UserBean loginUser = super.getLoginUser(request, response);
			if (loginUser == null) {
				msgs.add("url", new ActionMessage("error.user_not_login"));
				break;
			}
			if (loginUser.getStatus() != UserBean.STATUS_NORMAL) {
				msgs.add("url", new ActionMessage("error.user_not_available"));
				break;
			}
			// 验证音乐盒的有效性
			MusicBoxBean mbox = MusicDAO.getMusicBoxByID(mform.getBox());
			if (mbox == null || mbox.getSite().getId() != mform.getSid()) {
				msgs.add("name", new ActionMessage("error.mbox_not_available",
						new Integer(mform.getBox())));
				break;
			}
			SiteBean site = super.getSiteByID(mform.getSid());
			if(site==null){
				msgs.add("site", new ActionMessage("error.site_not_available"));
				break;
			}
			//检查黑名单
			if(isUserInBlackList(site, loginUser)){
				msgs.add("music", new ActionMessage("error.user_in_blacklist"));
				break;
			} 
			MusicBean mbean = new MusicBean();
			mbean.setSite(site);
			mbean.setIntroducer(loginUser);
			mbean.setMusicBox(mbox);
			mbean.setTitle(super.autoFiltrate(site, mform.getTitle()));
			if (StringUtils.isNotEmpty(mform.getAlbum()))
				mbean.setAlbum(mform.getAlbum());
			if (StringUtils.isNotEmpty(mform.getSinger()))
				mbean.setSinger(mform.getSinger());
			if (StringUtils.isNotEmpty(mform.getUrl()))
				mbean.setUrl(mform.getUrl());
			if (StringUtils.isNotEmpty(mform.getWord())){
				String word = super.autoFiltrate(site, mform.getWord());
				mbean.setWord(super.filterScriptAndStyle(word));
			}
			else
				mbean.setWord("&nbsp;");
			mbean.setCreateTime(new Date());
			mbean.setStatus(MusicBean.STATUS_RECOMMEND);
			MusicDAO.addMusic(mbean);
			break;
		}while(true);

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("music_intro");
		}
		String msg = getMessage(request, null, "music.recommend.submitted");
		return msgbox(mapping, form, request, response, msg, mform
				.getFromPage());
	}

	/**
	 * 添加歌曲
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doAdd(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MusicForm mform = (MusicForm) form;
		super.validateClientId(request, mform);
		ActionMessages msgs = validateSiteOwner(request, response, mform);
		while (msgs.isEmpty()) {
			if (StringUtils.isEmpty(mform.getTitle())) {
				msgs.add("name", new ActionMessage("error.music.title_empty"));
				break;
			}
			// 验证音乐盒的有效性
			MusicBoxBean mbox = MusicDAO.getMusicBoxByID(mform.getBox());
			if (mbox != null && mbox.getSite().getId() != mform.getSid()) {
				msgs.add("name", new ActionMessage("error.mbox_not_available",
						new Integer(mform.getBox())));
				break;
			}
			SiteBean site = super.getSiteBean(request);
			MusicBean mbean = new MusicBean();
			mbean.setSite(site);
			mbean.setMusicBox(mbox);
			mbean.setTitle(super.autoFiltrate(site, mform.getTitle()));
			if (StringUtils.isNotEmpty(mform.getAlbum()))
				mbean.setAlbum(mform.getAlbum());
			if (StringUtils.isNotEmpty(mform.getSinger()))
				mbean.setSinger(mform.getSinger());
			if (StringUtils.isNotEmpty(mform.getUrl()))
				mbean.setUrl(mform.getUrl());
			if (StringUtils.isNotEmpty(mform.getWord())){
				String word = StringUtils.abbreviate(super.autoFiltrate(site,
						mform.getWord()), MAX_MUSIC_LENGTH);
				mbean.setWord(super.filterScriptAndStyle(word));
			}
			else
				mbean.setWord("  ");
			mbean.setCreateTime(new Date());
			MusicDAO.addMusic(mbean);
			break;
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("music_add");
		}

		return makeForward(mapping.findForward("music"), mform.getSid(), "box",
				mform.getBox());
	}

	/**
	 * 删除歌曲
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteMusic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String mid)
			throws Exception {
		MusicForm mform = (MusicForm) form;
		ActionMessages msgs = validateSiteOwner(request, response, mform);
		if (msgs.isEmpty()) {
			int music_id = Integer.parseInt(mid);
			MusicBean mbean = MusicDAO.getMusicByID(music_id);
			SiteBean site = super.getSiteBean(request);
			if (mbean != null && mbean.getSite().getId() == site.getId()) {
				MusicDAO.deleteMusic(mbean);
				SearchProxy.remove(mbean);
			}
		}
		return makeForward(mapping.findForward("music"), mform.getSid(), "box",
				mform.getBox());
	}

	/**
	 * 更新歌曲
	 * 这段代码很糟糕，shit!
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateMusic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MusicForm mform = (MusicForm) form;
		super.validateClientId(request, mform);
		do{
			if (StringUtils.isEmpty(mform.getTitle())){
				break;			
			}
			ActionMessages msgs = super.validateSiteOwner(request, response, mform);
			if(!msgs.isEmpty()){
				saveMessages(request, msgs);
				break;
			}
			SiteBean site = super.getSiteBean(request);
			MusicBean mbean = MusicDAO.getMusicByID(mform.getId());
			//判断是否为本站音乐
			if(mbean==null ||mbean.getSite().getId()!=site.getId()){
				break;
			}
			if (mbean.getStatus() == MusicBean.STATUS_NORMAL) {
				// 验证新的音乐盒的有效性(移动到其他音乐盒)
				if ((mbean.getMusicBox() == null && mform.getBox() > 0)
						|| (mbean.getMusicBox() != null && mform.getBox() != mbean
								.getMusicBox().getId())) {
					MusicBoxBean mbox = MusicDAO
							.getMusicBoxByID(mform.getBox());
					//判断是否为本站的音乐盒
					if(mbox!=null && mbox.getSite().getId()!=site.getId()){
						break;
					}
					//变换音乐盒
					if (mbox != null
							&& (mbean.getMusicBox() == null || mbean.getMusicBox()
									.getId() != mbox.getId())) {
						//两个音乐盒之间移动或者从无到有
						if (mbean.getMusicBox() != null)
							mbean.getMusicBox().incMusicCount(-1);
						mbean.setMusicBox(mbox);
						mbox.incMusicCount(1);
					} else if (mbox == null && mbean.getMusicBox() != null) {
						//从有到无
						mbean.getMusicBox().incMusicCount(-1);
						mbean.setMusicBox(null);
					}
				}
			}

			//赋新值
			if(!StringUtils.equals(mbean.getTitle(), mform.getTitle()))
				mbean.setTitle(super.autoFiltrate(site,mform.getTitle()));
			if (StringUtils.isNotEmpty(mform.getAlbum()))
				mbean.setAlbum(super.autoFiltrate(site,mform.getAlbum()));
			if (StringUtils.isNotEmpty(mform.getSinger()))
				mbean.setSinger(super.autoFiltrate(site,mform.getSinger()));
			if (StringUtils.isNotEmpty(mform.getUrl()))
				mbean.setUrl(mform.getUrl());
			if (StringUtils.isNotEmpty(mform.getWord())){
				String word = StringUtils.abbreviate(super.autoFiltrate(site,
					mform.getWord()), MAX_MUSIC_LENGTH);
				mbean.setWord(super.filterScriptAndStyle(word));
			}
			else
				mbean.setWord("  ");
			if (mbean.getStatus() != MusicBean.STATUS_NORMAL) {
				// 网友推荐的情况处理
				mbean.getMusicBox().incMusicCount(1);
				mbean.setStatus(MusicBean.STATUS_NORMAL);
			}
			MusicDAO.flush();
			break;
		}while(true);
		
		return makeForward(mapping.findForward("music"), mform.getSid(), "box",
				mform.getBox());
	}

	/**
	 * 删除选中的歌曲
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteSelected(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		MusicForm mform = (MusicForm) form;
		ActionMessages msgs = validateSiteOwner(request, response, mform);
		if (msgs.isEmpty() && mform.getMid() != null
				&& mform.getMid().length > 0) {
			SiteBean site = super.getSiteBean(request);
			MusicDAO.deleteMusics(site.getId(), mform.getMid());
		}
		return makeForward(mapping.findForward("music"), mform.getSid(), "box",
				mform.getBox());
	}

	/**
	 * 删除音乐盒
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteBox(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			String mboxid) throws Exception {
		MusicBoxForm mform = (MusicBoxForm) form;
		ActionMessages msgs = validateSiteOwner(request, response, mform);
		if (msgs.isEmpty()) {
			SiteBean site = super.getSiteBean(request);
			MusicBoxBean mbox = MusicDAO.getMusicBoxByID(Integer
					.parseInt(mboxid));
			if (mbox.getSite().getId() == site.getId())
				MusicDAO.deleteMusicBox(mbox);
		}
		return makeForward(mapping.findForward("music"), mform.getSid());
	}

	/**
	 * 修改音乐盒
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateBox(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MusicBoxForm mform = (MusicBoxForm) form;
		super.validateClientId(request, mform);
		if (StringUtils.isNotEmpty(mform.getName())) {
			ActionMessages msgs = validateSiteOwner(request, response, mform);
			if (msgs.isEmpty()) {
				SiteBean site = super.getSiteBean(request);
				MusicBoxBean mbox = MusicDAO.getMusicBoxByID(mform.getId());
				if (mbox.getSite().getId() == site.getId()) {
					mbox.setName(super.autoFiltrate(site,mform.getName()));
					if (StringUtils.isNotEmpty(mform.getDesc())){
						String desc = super.autoFiltrate(site,mform.getDesc()); 
						mbox.setDesc(desc);
					}
					else
						mbox.setDesc(null);
					MusicDAO.flush();
				}
			}
		}
		return makeForward(mapping.findForward("music"), mform.getSid(), "box",
				mform.getId());
	}
	
}
