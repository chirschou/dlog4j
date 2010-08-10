/*
 *  ValidateAction.java
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.DLOGUserManager;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.SiteDAO;
import com.liusoft.dlog4j.util.DLOG4JUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 用于检测网站名/用户名/用户昵称等是否有效的Action
 * 该Action提供给WEB下的ajax调用
 * @author liudong
 */
public class ValidateAction extends ActionBase {

	/**
	 * 验证用户是否已登录
	 * 该方法用在提交日记之前，判断网络状况用
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @throws Exception
	 * @ajax_enabled
	 */
	public void doValidateLogin(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception 
	{
		UserBean loginUser = super.getLoginUser(request, response);
		outputPlainMsg(response, String.valueOf((loginUser!=null)?1:0));
	}
	
	/**
	 * 验证用户名是否有效
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	public void doValidateUsername(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String uname) throws Exception 
	{
		if(StringUtils.isBlank(uname)){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String msg = "";
		do{
			if(!StringUtils.isLegalUsername(uname)){
				msg = getMessage(request,null,"error.illegal_username");
				break;
			}
			if(DLOGUserManager.getUserByName(uname) != null){
				msg = getMessage(request,null,"error.username_exists");
				break;
			}
			break;
		}while(true);

		outputPlainMsg(response, msg);
	}

	/**
	 * 验证网站名是否有效
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	public void doValidateSitename(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String sname) throws Exception {
		
		if(StringUtils.isBlank(sname)){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String msg = "";
		do{
			if(!!DLOG4JUtils.isLegalSiteName(sname)){
				msg = getMessage(request,null,"error.site_uniqueName_illegal");
				break;
			}
			if(sname.length() < DlogAction.MIN_SITENAME_LEN){
				msg = getMessage(request, null, "error.site_uniqueName_too_short");
				break;
			}
			if(SiteDAO.getSiteByName(sname) != null){
				msg = getMessage(request,null,"error.sitename_exists");
				break;
			}
			break;
		}while(true);
		
		outputPlainMsg(response, msg);
	}

	/**
	 * 验证网站中文名是否有效
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	public void doValidateSitecname(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String cname) throws Exception {
		
		if(StringUtils.isBlank(cname)){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String msg = "";
		do{
			if (DLOGSecurityManager.IllegalGlossary.existIllegalWord(cname)){
				msg = getMessage(request,null,"error.illegal_glossary");
				break;
			}
			if(SiteDAO.getSiteByFriendlyName(cname) != null){
				msg = getMessage(request,null,"error.site_friendlyName_exists");
				break;
			}
			break;
		}while(true);

		outputPlainMsg(response, msg);
	}
	
	/**
	 * 验证用户昵称是否有效
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @ajax_enabled
	 */
	public void doValidateNickname(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String nname) throws Exception {
		if(StringUtils.isBlank(nname)){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String msg = "";
		do{
			if (DLOGSecurityManager.IllegalGlossary.existIllegalWord(nname)){
				msg = getMessage(request,null,"error.illegal_glossary");
				break;
			}
			if(DLOGUserManager.getUserByNickname(nname) != null){
				msg = getMessage(request,null,"error.nickname_exists");
				break;
			}
			break;
		}while(true);
		
		outputPlainMsg(response, msg);
	}
	
}
