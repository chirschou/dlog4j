/*
 *  CatalogAction.java
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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.liusoft.dlog4j.CapacityExceedException;
import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.beans.CatalogBean;
import com.liusoft.dlog4j.beans.DiaryBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.TypeBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.CatalogDAO;
import com.liusoft.dlog4j.dao.UserDAO;
import com.liusoft.dlog4j.formbean.CatalogForm;

/**
 * 日记分类相关操作的Action类
 * @author liudong
 */
public class CatalogAction extends AdminActionBase {

	final static String CATALOGS = "catalogs";

	/**
	 * 删除访问日记分类的特殊权限
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDeleteCatalogUser(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception 			
	{
		CatalogForm catalog = (CatalogForm) form;
		SiteBean site = getSiteBean(request);
		CatalogBean bean = CatalogDAO.getCatalogByID(catalog.getId());
		//检查sid和catalog_id是否对应
		if(bean!=null && bean.getSite().getId()==site.getId()){
			CatalogDAO.deletePermission(catalog.getId(), catalog.getUserid());
		}
		return makeForward(mapping.findForward("catalog-users"), catalog
				.getSid(), "cat_id", catalog.getId());
	}
	/**
	 * 添加访问日记分类的特殊权限
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doCreateCatalogUser(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception 			
	{
		CatalogForm catalog = (CatalogForm) form;
		super.validateClientId(request, catalog);
		ActionMessages msgs = new ActionMessages();
		UserBean loginUser = super.getLoginUserAfterValidateSiteOwner(request);
		//检查userid是否存在
		if (catalog.getUserid() < 1
				|| UserDAO.getUserByID(catalog.getUserid()) == null) {
			msgs.add("userid", new ActionMessage("error.user_not_found",
					new Integer(catalog.getUserid())));
		}
		else if(catalog.getUserid()== loginUser.getId()){
			//不能添加自己
		}
		else{
			SiteBean site = getSiteBean(request);
			//检查catalog_id是否有效
			CatalogBean bean = CatalogDAO.getCatalogByID(catalog.getId());
			if(bean==null){
				msgs.add("userid", new ActionMessage("error.catalog_not_found",
						new Integer(catalog.getId())));
			}
			//检查sid和catalog_id是否对应
			else if(bean.getSite().getId()!=site.getId()){
				msgs.add("userid", new ActionMessage("error.catalog_deny",
						new Integer(catalog.getId())));
			}
			else{
				//判断是否已经存在权限信息
				int role = CatalogDAO.getUserRoleInCatalog(catalog.getId(), catalog.getUserid());
				if(role < 0){
					//创建新权限
					CatalogDAO.createPermission(catalog.getId(), catalog
							.getUserid(), catalog.getRole());
				}
				else if (role < catalog.getRole()){
					CatalogDAO.updatePermission(catalog.getId(), catalog
							.getUserid(), catalog.getRole());
				}
			}
		}
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			return mapping.findForward("adduser");
		}
		return makeForward(mapping.findForward("catalog-users"), catalog
				.getSid(), "cat_id", catalog.getId());
	}
	/**
	 * 创建日记分类
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doCreateCatalog(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception 			
	{
		CatalogForm catalog = (CatalogForm)form;
		ActionMessages msgs = new ActionMessages();
		
		if(StringUtils.isEmpty(catalog.getName()))
			msgs.add("name", new ActionMessage("error.empty_not_allowed"));
		else if(DLOGSecurityManager.IllegalGlossary.existIllegalWord(catalog.getName()))
			msgs.add("name", new ActionMessage("error.illegal_glossary"));
		else if(DLOGSecurityManager.IllegalGlossary.existIllegalWord(catalog.getDetail()))
			msgs.add("detail", new ActionMessage("error.illegal_glossary"));
		else{
			SiteBean site = super.getSiteBean(request);
			CatalogBean cbean = new CatalogBean();
			cbean.setName(super.autoFiltrate(site, catalog.getName()));
			if(StringUtils.isNotEmpty(catalog.getDetail())){
				String detail = super.autoFiltrate(site, catalog.getDetail());
				cbean.setDetail(super.filterScriptAndStyle(detail));
			}
			cbean.setType(catalog.getType());
			cbean.setSite(site);
			cbean.setCreateTime(new Date());
			if(catalog.getCatalog()>0){
				cbean.setCatalog(new TypeBean(catalog.getCatalog()));
			}
			try{
				CatalogDAO.create(cbean, catalog.getId(), catalog.getDirection()==1);
			}catch(CapacityExceedException e){
				msgs.add("catalog", new ActionMessage("error.catalog_reach_max_size", new Integer(e.getCount())));
			}catch(Exception e){
				msgs.add("catalog", new ActionMessage("error.database", e.getMessage()));
			}			
		}
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			return mapping.getInputForward();
		}
		return makeForward(mapping.findForward(CATALOGS), catalog.getSid());
	}

	/**
	 * 更新日记分类
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdateCatalog(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception 			
	{
		CatalogForm catalog = (CatalogForm) form;
		String errMsg = null;

		if (StringUtils.isEmpty(catalog.getName()))
			errMsg = getMessage(request, null, "error.empty_not_allowed");
		else if(DLOGSecurityManager.IllegalGlossary.existIllegalWord(catalog.getName()))
			errMsg = getMessage(request, null, "error.illegal_glossary");
		else if(DLOGSecurityManager.IllegalGlossary.existIllegalWord(catalog.getDetail()))
			errMsg = getMessage(request, null, "error.illegal_glossary");
		else {
			try {
				CatalogBean cbean = CatalogDAO.getCatalogByID(catalog.getId());
				cbean.setName(super.autoFiltrate(cbean.getSite(), catalog.getName()));
				if(StringUtils.isNotEmpty(catalog.getDetail())){
					String detail = super.autoFiltrate(cbean.getSite(), catalog.getDetail());
					cbean.setDetail(super.filterScriptAndStyle(detail));
				}
				cbean.setType(catalog.getType());
				int logCount = CatalogDAO.getDiaryCount(catalog.getId(), false,
						DiaryBean.STATUS_NORMAL);
				cbean.setArticleCount(logCount);
				
				//更新内容类别
				if(catalog.getCatalog()>0){
					if(cbean.getCatalog()==null)
						cbean.setCatalog(new TypeBean(catalog.getCatalog()));
					else if(cbean.getCatalog().getId()!=catalog.getCatalog())
						cbean.setCatalog(new TypeBean(catalog.getCatalog()));						
				}
				else if(cbean.getCatalog()!=null){
					cbean.setCatalog(null);
				}
				
				CatalogDAO.flush();
			} catch (Exception e) {
				errMsg = getMessage(request, null, "error.database", e
						.getMessage());
			}
		}

		ActionForward page = null;
		if(errMsg!=null)
			page = makeForward(mapping.findForward(CATALOGS), catalog
				.getSid(), "cat_id=" + catalog.getId() + "#edit");
		else
			page = makeForward(mapping.findForward(CATALOGS), catalog.getSid());
		return msgbox(mapping, form, request, response, errMsg, page.getPath());
	}

	/**
	 * 删除日记分类
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_cat_id)
			throws Exception 
	{
		CatalogForm lform = (CatalogForm)form;
		ActionForward page = makeForward(mapping.findForward(CATALOGS), lform.getSid());
		try{
			int cat_id = Integer.parseInt(s_cat_id);
			//检查分类中是否有标志为删除的日记或者是草稿等，如有则提示用户不予删除
			if(CatalogDAO.getDiaryCount(cat_id,true,0)>0){
				String msg = getMessage(request,null,"error.catalog_not_empty");
				return msgbox(mapping,form,request,response,msg,page.getPath());
			}
			CatalogDAO.delete(lform.getSid(), cat_id);
		}catch(Exception e){
			context().log("delete catalog #"+s_cat_id+" failed.", e);
		}
		return page;
	}
	/**
	 * 向上移动日记分类
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveUp(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_cat_id)
			throws Exception 
	{
		CatalogForm lform = (CatalogForm)form;
		try{
			int cat_id = Integer.parseInt(s_cat_id);
			CatalogDAO.move(getSiteBean(request), cat_id, true);
		}catch(Exception e){
			context().log("move up catalog #"+s_cat_id+" failed.", e);
		}
		return makeForward(mapping.findForward(CATALOGS), lform.getSid());
	}
	/**
	 * 向下移动日记分类
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveDown(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String s_cat_id)
			throws Exception
	{
		CatalogForm lform = (CatalogForm)form;
		try{
			int cat_id = Integer.parseInt(s_cat_id);
			CatalogDAO.move(getSiteBean(request), cat_id, false);
		}catch(Exception e){
			context().log("move up catalog #"+s_cat_id+" failed.", e);
		}
		return makeForward(mapping.findForward(CATALOGS), lform.getSid());
	}

	/**
	 * 移动日记到另外的分类或者是垃圾箱
	 * 当目标分类编号为-1的时候移动到垃圾箱
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doMoveToCatalog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		CatalogForm lform = (CatalogForm)form;
		SiteBean site = getSiteBean(request);
		if(lform.getToCatalog()==-1){
			//move to trash
			CatalogDAO.removeDiary(site, lform.getFromCatalog());
		}
		else if(lform.getToCatalog()>0){
			CatalogBean toCat = CatalogDAO.getCatalogByID(lform.getToCatalog());
			if(toCat!=null && toCat.getSite().getId()==site.getId()){
				CatalogBean fromCat = CatalogDAO.getCatalogByID(lform.getFromCatalog());
				if(fromCat!=null && fromCat.getSite().getId()==site.getId())
					CatalogDAO.moveDiary(site,fromCat,toCat);
			}
		}
		return makeForward(mapping.findForward(CATALOGS), site.getId());
	}
}
