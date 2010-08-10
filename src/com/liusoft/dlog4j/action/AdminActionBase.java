/*
 *  AdminActionBase.java
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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.liusoft.dlog4j.formbean.FormBean;

/**
 * 必须是站长才能执行的方法 该类实现了统一判断是否具有站长的权限
 * 
 * @author Winter Lau
 */
public abstract class AdminActionBase extends ActionBase {

	/**
	 * 返回不需要经过管理员权限验证的方法名
	 * @return
	 */
	protected String[] methodsIgnore(){ 
		return null; 
	}
	
	/*
	 * 判断当前操作用户是否拥有站长的权限
	 * 
	 * @see com.liusoft.dlog4j.action.ActionBase#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward beforeExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception 
	{		
		if(needVerify(req)){
			FormBean fbean = (FormBean)form;
			ActionMessages msgs = validateSiteOwner(req, res, fbean);
			if(!msgs.isEmpty()){
				Iterator iter = msgs.get();
				String msg = null;
				if(iter.hasNext()){
					ActionMessage am = (ActionMessage)iter.next();
					msg = super.getMessage(req, null, am.getKey());
				}
				String page = (fbean != null) ? fbean.getFromPage() : (req
						.getContextPath() + "/index.vm");
				return msgbox(mapping,form,req,res,msg,page);
			}
		}
		//否则继续父类的处理方法
		return null;
	}
	
	/**
	 * 判断当前请求是否需要进行管理员的权限判断
	 * @param req
	 * @return
	 */
	private final boolean needVerify(HttpServletRequest req){
		String[] methods = methodsIgnore();
		for(int i=0;methods!=null&&i<methods.length;i++){
			String m_name = SUBMIT_BUTTON_PREFIX + methods[i];
			if(req.getParameter(m_name)!=null)
				return false;
		}
		return true;
	}
	
}
