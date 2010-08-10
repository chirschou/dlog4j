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
 *  Author: Winter Lau (javayou@gmail.com)
 *  http://dlog4j.sourceforge.net
 */
package com.liusoft.dlog4j.formbean;

/**
 * 日记分类表单
 * @author liudong
 */
public class CatalogForm extends FormBean {

	private String name;		//分类名
	private String detail;	//分类详细描述
	private int direction;	//插入的方向
	private int type;			//分类类型
	
	private int catalog;		//内容类别
	
	/**** property in move_logs.vm ****/
	private int fromCatalog;
	private int toCatalog;
	
	/**** property in catalog_user_add.vm ****/
	private int userid;
	private int role;
	
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getFromCatalog() {
		return fromCatalog;
	}
	public void setFromCatalog(int fromCatalog) {
		this.fromCatalog = fromCatalog;
	}
	public int getToCatalog() {
		return toCatalog;
	}
	public void setToCatalog(int toCatalog) {
		this.toCatalog = toCatalog;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public final int getCatalog() {
		return catalog;
	}
	public final void setCatalog(int catalog) {
		this.catalog = catalog;
	}
}
