/*
 *  CatalogUserKey.java
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

import java.io.Serializable;

/**
 * 目录与用户的复合主键
 * @author Winter Lau
 */
public class CatalogUserKey implements Serializable{

	protected int catalog;
	protected int user;
	
	public CatalogUserKey(){}
	
	public CatalogUserKey(int catalog, int user){
		this.catalog = catalog;
		this.user = user;
	}
	
	public int getCatalog() {
		return catalog;
	}
	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}

	public boolean equals(Object arg0) {
		if(arg0 == null)
			return false;
		if(arg0 == this)
			return true;
		if(arg0 instanceof CatalogUserKey){
			CatalogUserKey cuk = (CatalogUserKey)arg0;
			return (cuk.getCatalog()==catalog) && (cuk.getUser()==user);
		}
		return false;
	}

	public int hashCode() {
		return catalog * 100000 + user;
	}
	
}
