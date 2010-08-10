/*
 *  FormBean.java
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
 *  Author: Winter Lau (javayou@gmail.com)
 *  http://dlog4j.sourceforge.net
 */
package com.liusoft.dlog4j.formbean;

import org.apache.struts.action.ActionForm;

import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * FormBean的基类
 * @author Winter Lau
 */
public abstract class FormBean extends ActionForm {

	private int id;		//对象编号
	private int sid;	//网站编号
	private String fromPage;
	private String __ClientId;	//客户端安全识别码

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public final int getId() {
		return id;
	}

	public final void setId(int id) {
		this.id = id;
	}

	public final String getFromPage() {
		return fromPage;
	}

	public final void setFromPage(String fromPage) {
		this.fromPage = fromPage;		
	}

	public String get__ClientId() {
		return __ClientId;
	}

	public void set__ClientId(String clientId) {
		__ClientId = clientId;
	}

	/**
	 * 自动判断是否对内容进行敏感字过滤
	 * @param site
	 * @param content
	 * @return
	 */
	protected String autoFiltrate(String content) {
		if(StringUtils.isEmpty(content))
			return null;
		return DLOGSecurityManager.IllegalGlossary
				.autoGlossaryFiltrate(content);
	}
	
}
