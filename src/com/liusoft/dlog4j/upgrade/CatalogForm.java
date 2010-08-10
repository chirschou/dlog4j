/*
 *  CatalogForm.java
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
package com.liusoft.dlog4j.upgrade;

import java.util.List;

/**
 * DLOG4J 2.0 的日记分类
 * @author liudong
 */
public class CatalogForm {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3618136758013671737L;
	public final static int TYPE_OWNER 		= 0x00;		//只有日记所有者才可以看
	public final static int TYPE_GENERAL 	= 0x01;		//一般的日记分类
	public final static int TYPE_COMMON 	= 0x02;		//只要是角色为ROLE_FRIEND的都可以发表日记
	public final static int TYPE_FREE		= 0x04;		//自由分类,类似论坛,注册的用户都可以发表日记
	// --------------------------------------------------------- Instance Variables

	private int id;
	private String name;
	private String detail;
	private int type;
	private int order = -1;//该初始值请不要修改,用于创建分类时order字段的自动处理
	private String iconUrl;
	private int showInHome = 0;
	
	
	private List logs;

	// --------------------------------------------------------- Methods
	public boolean isCommon(){
		return type == TYPE_COMMON;
	}
	public boolean isOwnerOnly(){
		return type == TYPE_OWNER;
	}
	public boolean isFree(){
		return type == TYPE_FREE;
	}
	/** 
	 * Returns the iconUrl.
	 * @return String
	 */
	public String getIconUrl() {
		return iconUrl;
	}

	/** 
	 * Set the iconUrl.
	 * @param iconUrl The iconUrl to set
	 */
	public void setIconUrl(String iconUrl) {
	    if(!"".equals(iconUrl))
	        this.iconUrl = iconUrl;
	}

	/** 
	 * Returns the type.
	 * @return int
	 */
	public int getType() {
		return type;
	}
	
	/** 
	 * Set the type.
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/** 
	 * Returns the order.
	 * @return int
	 */
	public int getOrder() {
		return order;
	}

	/** 
	 * Set the order.
	 * @param order The order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/** 
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/** 
	 * Set the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public List getLogs() {
		return logs;
	}

	/**
	 * @param list
	 */
	public void setLogs(List list) {
		logs = list;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}

	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public int getShowInHome() {
		return showInHome;
	}
	public void setShowInHome(int showInHome) {
		this.showInHome = showInHome;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
