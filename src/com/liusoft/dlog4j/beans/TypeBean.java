/*
 *  SiteTypeBean.java
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
package com.liusoft.dlog4j.beans;

import java.util.List;

import com.liusoft.dlog4j.base._BeanBase;

/**
 * 站点类型
 * @author Winter Lau
 */
public class TypeBean extends _BeanBase {

	private TypeBean parent;	
	private String name;	
	private int sortOrder;
	
	private List subTypes;	
	
	public TypeBean(){}
	
	public TypeBean(int type_id){
		super.setId(type_id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeBean getParent() {
		return parent;
	}

	public void setParent(TypeBean parent) {
		this.parent = parent;
	}

	public List getSubTypes() {
		return subTypes;
	}

	public void setSubTypes(List subTypes) {
		this.subTypes = subTypes;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}
