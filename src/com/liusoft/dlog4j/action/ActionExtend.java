/*
 *  ActionExtend.java
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.velocity.tools.struts.StrutsUtils;

import com.liusoft.dlog4j.util.StringUtils;

/**
 * 实现Struts的功能扩展
 * @author liudong
 */
abstract class ActionExtend extends Action {

	public final static String METHOD_IDENT_PARAM = "__method";
	private final static Log log = LogFactory.getLog(ActionExtend.class);
	
	/**
	 * 执行前准备
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected ActionForward beforeExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		return null;
	}

	/**
	 * 执行后
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected void afterExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
	}

	/**
	 * 返回是否将表单的域映射到Action类的属性上<br/> 子类可以覆盖该方法已启用自动映射功能 <br/>
	 * 建议还是使用Struts的Formbean更符合设计模式<br/>
	 * 
	 * @param method
	 * @return
	 */
	protected boolean paramMapping(String method) {
		return false;
	}

	/**
	 * Action类的入口，用于根据不同的提交按钮名称执行相对应的方法 
	 * 按钮的名称是eventSubmit_Xxxx，对应执行的方法是doXxxx
	 */
	public final ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception 
	{
		ActionForward af = beforeExecute(mapping, form, req, res);
		if(af != null)
			return af;
		
		String param = null;
		String value = null;
		
		String __method = req.getParameter(METHOD_IDENT_PARAM);
		if(StringUtils.isNotBlank(__method)){
			param = METHOD_PREFIX + __method;
		}
		else{
			for (Enumeration params = req.getParameterNames(); params
					.hasMoreElements();) {
				String t_param = (String) params.nextElement();
				if (t_param.startsWith(SUBMIT_BUTTON_PREFIX)) {
					value = req.getParameter(t_param);
					param = METHOD_PREFIX
							+ t_param.substring(SUBMIT_BUTTON_PREFIX.length());
					break;
				}
			}
		}

		if (param == null)
			param = "doDefault";

		try {
			return callActionMethod(mapping, form, req, res, param, value);
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if(t instanceof IllegalAccessException){
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
			log.error("Exception occur when calling "+param+" in action:" + getClass().getName(), t);
			if (t instanceof Exception)
				throw (Exception) t;
			else
				throw new Exception(t);
		} catch (NoSuchMethodException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
			return null;
		} finally{
			afterExecute(mapping,form,req,res);
		}
	}
	
	/**
	 * 默认的执行方法
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDefault(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception{
		res.sendError(HttpServletResponse.SC_NOT_FOUND, "METHOD NOT FOUND.");
		return null;
	}

	/**
	 * 调用事件处理方法
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param methodName
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private ActionForward callActionMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest req, HttpServletResponse res,
			String methodName, String value) throws Exception {
		Method doMethod = null;
		Object params[] = (Object[]) null;

		for (int i = 0; i < methodParams.length; i++) {
			try {
				doMethod = getClass().getDeclaredMethod(methodName,
						methodParams[i]);
				if (doMethod == null)
					continue;
				Class[] param_classes = doMethod.getParameterTypes();
				if (param_classes.length == 4)
					params = new Object[] { mapping, form, req, res };
				else
					params = new Object[] { mapping, form, req, res, value };
				break;
			} catch (NoSuchMethodException excp) {
			}
		}

		if (doMethod != null) {
			if (paramMapping(doMethod.getName()))
				BeanUtils.populate(this, req.getParameterMap());

			Object ret = doMethod.invoke(this, params);
			
			Class returnType = doMethod.getReturnType();

			if (returnType.equals(ActionForward.class))
				return (ActionForward) ret;

			if (returnType.equals(String.class))
				return new ActionForward((String) ret, true);
			
			if (returnType.equals(void.class)||returnType.equals(Void.class))
				return null;
			
			throw new UnsupportedReturnTypeException(ret.getClass().getName());
		}

		throw new NoSuchMethodException(getClass().getName()+":"+methodName);
	}

	protected final static String SUBMIT_BUTTON_PREFIX = "eventSubmit_";

	protected final static String METHOD_PREFIX = "do";

	private final static Class method1Params[];
	private final static Class method2Params[];
	private final static Class methodParams[][];

	static {
		method1Params = (new Class[] {
				org.apache.struts.action.ActionMapping.class,
				org.apache.struts.action.ActionForm.class,
				javax.servlet.http.HttpServletRequest.class,
				javax.servlet.http.HttpServletResponse.class });
		method2Params = (new Class[] {
				org.apache.struts.action.ActionMapping.class,
				org.apache.struts.action.ActionForm.class,
				javax.servlet.http.HttpServletRequest.class,
				javax.servlet.http.HttpServletResponse.class,
				java.lang.String.class });
		methodParams = (new Class[][] { method1Params, method2Params});
	}
	
	/**
	 * 读取资源中定义的信息
	 * @param req
	 * @param bundle
	 * @param key
	 * @return
	 */
    protected String getMessage(HttpServletRequest req, String bundle, String key) {
    	if(key==null)
    		return null;
    	MessageResources res = StrutsUtils.getMessageResources(req, context(), bundle);
    	if(res==null)
    		return null;
    	return res.getMessage(key);
    }

	/**
	 * 读取资源中定义的信息
	 * @param req
	 * @param bundle
	 * @param key
	 * @param param
	 * @return
	 */
    protected String getMessage(HttpServletRequest req, String bundle, String key, Object param) {
    	if(key==null)
    		return null;
    	MessageResources res = StrutsUtils.getMessageResources(req, context(), bundle);
    	if(res==null)
    		return null;
    	return res.getMessage(key, param);
    }

	/**
	 * 读取资源中定义的信息
	 * @param req
	 * @param bundle
	 * @param key
	 * @param param1
	 * @param param2
	 * @return
	 */
    protected String getMessage(HttpServletRequest req, String bundle, String key, Object param1, Object param2) {
    	if(key==null)
    		return null;
    	MessageResources res = StrutsUtils.getMessageResources(req, context(), bundle);
    	if(res==null)
    		return null;
    	return res.getMessage(key, param1, param2);
    }

	/**
	 * 读取资源中定义的信息
	 * @param req
	 * @param bundle
	 * @param key
	 * @param params
	 * @return
	 */
    protected String getMessage(HttpServletRequest req, String bundle, String key, Object[] params) {
    	if(key==null)
    		return null;
    	MessageResources res = StrutsUtils.getMessageResources(req, context(), bundle);
    	if(res==null)
    		return null;
    	return res.getMessage(key, params);
    }
    
	protected ServletContext context() {
		return servlet.getServletContext();
	}

	protected ServletConfig config() {
		return servlet.getServletConfig();
	}

}

/**
 * 返回类型不被支持
 * @author liudong
 */
class UnsupportedReturnTypeException extends Exception {

	public UnsupportedReturnTypeException(String message) {
		super(message);
	}

}
