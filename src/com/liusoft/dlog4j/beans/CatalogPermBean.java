/*
 *  CatalogPermBean.java
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
package com.liusoft.dlog4j.beans;

/**
 * 分类访问控制
 * @author Winter Lau
 */
public class CatalogPermBean {

	public final static int ROLE_VIEW	= 1;	//用户可见
	public final static int ROLE_AUDIT	= 2;	//用户可写，但是必须由站长审批后方可发布
	public final static int ROLE_BLOG 	= 4;	//用户可写

	protected CatalogUserKey key;
	protected int role;
	
	public CatalogUserKey getKey() {
		return key;
	}
	public void setKey(CatalogUserKey key) {
		this.key = key;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	
}
