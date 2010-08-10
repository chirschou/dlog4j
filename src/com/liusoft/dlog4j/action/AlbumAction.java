/*
 *  AlbumAction.java
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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.liusoft.dlog4j.CapacityExceedException;
import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.beans.AlbumBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.TypeBean;
import com.liusoft.dlog4j.dao.AlbumDAO;
import com.liusoft.dlog4j.formbean.AlbumForm;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 相簿管理的Action类
 * 
 * @author Winter Lau
 */
public class AlbumAction extends AdminActionBase {

	public final static String ALBUMS = "albums";
	public final static String PHOTOS = "photos";
	
	private final static String[] methods = new String[]{"AlbumVerify"};

	protected String[] methodsIgnore() {
		return methods;
	}
	
	/**
	 * 移动相簿中的所有照片到其他相簿
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveToAlbum(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception 
	{
		AlbumForm aform = (AlbumForm) form;
		validateClientId(request, aform);
		if(aform.getFromAlbum()<1||aform.getToAlbum()<1||aform.getFromAlbum()==aform.getToAlbum())
			return makeForward(mapping.findForward("albums"),aform.getSid());
		//检查相簿的有效性
		AlbumBean fromAlbum = AlbumDAO.getAlbumByID(aform.getFromAlbum());
		if(fromAlbum==null || fromAlbum.getSite().getId()!=aform.getSid()){
			return makeForward(mapping.findForward("albums"),aform.getSid());
		}
		AlbumBean toAlbum = AlbumDAO.getAlbumByID(aform.getToAlbum());
		if(toAlbum==null || toAlbum.getSite().getId()!=aform.getSid()){
			return makeForward(mapping.findForward("albums"),aform.getSid());
		}		
		//两个相簿都有效，开始移动相簿中的照片
		AlbumDAO.movePhoto(aform.getSid(), fromAlbum, toAlbum);
		
		return makeForward(mapping.findForward("albums"),aform.getSid(),"aid", toAlbum.getId());
	}

	/**
	 * 相册验证码的判别,用于处理需要口令访问的相簿的口令输入表单的提交动作
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param s_photo_id
	 * @return
	 * @throws Exception
	 * @see /html/photo/_album_verify.vm
	 */
	protected ActionForward doAlbumVerify(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception 			
	{
		AlbumForm aform = (AlbumForm) form;
		validateClientId(request, aform);
		String ext = "aid="+aform.getId();
		AlbumBean album = AlbumDAO.getAlbumByID(aform.getId());
		ActionForward forward = makeForward(mapping.findForward(PHOTOS), aform.getSid(), ext);
		if(album!=null && album.getSite().getId()==aform.getSid()){
			if(StringUtils.equals(album.getVerifyCode(), aform.getVerifyCode())){
				HttpSession ssn = request.getSession(true);
				ssn.setAttribute(Globals.ALBUM_VERIFY_KEY+aform.getId(),album.getVerifyCode());
				return forward;
			}
		}
		ActionMessages msgs = new ActionMessages();
		msgs.add("verify", new ActionMessage("error.illegal_album_verify_code"));
		super.saveMessages(request, msgs);
		forward.setRedirect(false);
		return forward;
	}
	
	/**
	 * 创建相簿
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
		AlbumForm album = (AlbumForm) form;
		ActionMessages msgs = new ActionMessages();

		int new_album_id = -1;
		
		if (StringUtils.isEmpty(album.getName())) {
			msgs.add("name", new ActionMessage("error.album_name_required"));
		} else if (album.getType() == AlbumBean.TYPE_VERIFIED
				&& StringUtils.isEmpty(album.getVerifyCode())) {
			msgs.add("password", new ActionMessage(
					"error.album_verifycode_required"));
		} else if (DLOGSecurityManager.IllegalGlossary.existIllegalWord(album.getName())){
			msgs.add("name", new ActionMessage("error.illegal_glossary"));
		} else if (DLOGSecurityManager.IllegalGlossary.existIllegalWord(album.getDesc())){
			msgs.add("desc", new ActionMessage("error.illegal_glossary"));
		} else {
			SiteBean site = super.getSiteBean(request);
			AlbumBean abean = new AlbumBean();
			abean.setName(super.autoFiltrate(site, album.getName()));
			if (StringUtils.isNotEmpty(album.getDesc())){
				String desc = super.autoFiltrate(site, album.getDesc());
				abean.setDesc(super.filterScriptAndStyle(desc));
			}
			abean.setType(album.getType());
			if (StringUtils.isNotEmpty(album.getVerifyCode()))
				abean.setVerifyCode(album.getVerifyCode());
			abean.setSite(site);
			abean.setCreateTime(new Date());
			try {
				//1: 之前; 2: 之后; 3: 之内
				if(album.getDirection()==3)
					album.setParent(album.getId());
				if(album.getCatalog()>0)
					abean.setCatalog(new TypeBean(album.getCatalog()));
				AlbumDAO.create(album.getParent(), abean, album.getId(), album
						.getDirection());
				new_album_id = abean.getId();
			} catch (CapacityExceedException e) {
				msgs.add("album",
						new ActionMessage("error.album_reach_max_size",
								new Integer(e.getCount())));
			} catch (Exception e) {
				msgs.add("album", new ActionMessage("error.database", e
						.getMessage()));
			}
		}

		if (!msgs.isEmpty()) {
			saveMessages(request, msgs);
			return mapping.findForward("album_add");
		}
		return makeForward(mapping.findForward(ALBUMS), album.getSid(), "aid",
				new_album_id);
	}

	/**
	 * 更新相簿
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
		AlbumForm album = (AlbumForm) form;
		String errMsg = null;
		if (StringUtils.isEmpty(album.getName())) {
			errMsg = getMessage(request, null, "error.album_name_required");
		} else if (album.getType() == AlbumBean.TYPE_VERIFIED
				&& StringUtils.isEmpty(album.getVerifyCode())) {
			errMsg = getMessage(request, null,
					"error.album_verifycode_required");
		} else if (DLOGSecurityManager.IllegalGlossary.existIllegalWord(album.getName())){
			errMsg = getMessage(request, null, "error.illegal_glossary");
		} else if (DLOGSecurityManager.IllegalGlossary.existIllegalWord(album.getDesc())){
			errMsg = getMessage(request, null, "error.illegal_glossary");
		} else {
			try {
				AlbumBean abean = AlbumDAO.getAlbumByID(album.getId());
				abean.setName(super.autoFiltrate(abean.getSite(), album.getName()));
				if (StringUtils.isNotEmpty(album.getDesc())){
					String desc = super.autoFiltrate(abean.getSite(), album.getDesc());
					abean.setDesc(super.filterScriptAndStyle(desc));
				}
				else
					abean.setDesc(null);
				abean.setType(album.getType());
				if (StringUtils.isNotEmpty(album.getVerifyCode())
						&& abean.getType() == AlbumBean.TYPE_VERIFIED) {
					abean.setVerifyCode(album.getVerifyCode());
				} else {
					abean.setVerifyCode(null);
				}
				//更新内容类别
				if(album.getCatalog()>0){
					if(abean.getCatalog()==null)
						abean.setCatalog(new TypeBean(album.getCatalog()));
					else if(abean.getCatalog().getId()!=album.getCatalog())
						abean.setCatalog(new TypeBean(album.getCatalog()));						
				}
				else if(abean.getCatalog()!=null){
					abean.setCatalog(null);
				}
				
				AlbumDAO.flush();
			} catch (Exception e) {
				errMsg = getMessage(request, null, "error.database", e
						.getMessage());
			}
		}
		ActionForward page = null;
		if (errMsg != null)
			page = makeForward(mapping.findForward("album_edit"), album.getSid(),
					"aid", album.getId());
		else
			page = makeForward(mapping.findForward(ALBUMS),album.getSid(),"aid",album.getId());
		return msgbox(mapping, form, request, response, errMsg, page.getPath());
	}

	/**
	 * 删除相簿
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			String s_album_id) throws Exception {
		AlbumForm lform = (AlbumForm) form;
		ActionForward page = makeForward(mapping.findForward(ALBUMS), lform
				.getSid(),"aid="+s_album_id);
		try {
			int album_id = Integer.parseInt(s_album_id);
			// 检查相簿中是否有标志为删除的相片等，如有则提示用户不予删除
			if (!AlbumDAO.isAlbumEmpty(album_id)) {
				String msg = getMessage(request, null, "error.album_not_empty");
				return msgbox(mapping, form, request, response, msg, page
						.getPath());
			}
			AlbumDAO.delete(lform.getSid(), album_id);
		} catch (Exception e) {
			context().log("delete album #" + s_album_id + " failed.", e);
		}
		return page;
	}

	/**
	 * 向上移动相簿
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveUp(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			String s_album_id) throws Exception {
		AlbumForm lform = (AlbumForm) form;
		try {
			int album_id = Integer.parseInt(s_album_id);
			AlbumDAO.move(getSiteBean(request), album_id, true);
		} catch (Exception e) {
			context().log("move up album #" + s_album_id + " failed.", e);
		}
		return makeForward(mapping.findForward(ALBUMS), lform.getSid());
	}

	/**
	 * 向下移动相簿
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveDown(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			String s_album_id) throws Exception {
		AlbumForm lform = (AlbumForm) form;
		try {
			int album_id = Integer.parseInt(s_album_id);
			AlbumDAO.move(getSiteBean(request), album_id, false);
		} catch (Exception e) {
			context().log("move up album #" + s_album_id + " failed.", e);
		}
		return makeForward(mapping.findForward(ALBUMS), lform.getSid());
	}

}
