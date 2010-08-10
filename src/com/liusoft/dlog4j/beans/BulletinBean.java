/*
 *  BulletinBean.java
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
 *  2006-6-10
 */
package com.liusoft.dlog4j.beans;

import com.liusoft.dlog4j.base._BulletinBase;
import com.liusoft.dlog4j.search.SearchEnabled;

/**
 * 网站公告
 * @author liudong
 */
public class BulletinBean extends _BulletinBase implements SearchEnabled {

	/***** The methods below is for search proxy *****/
	
	public String name() {
		return "bulletin";
	}

	public String[] getIndexFields() {
		return new String[] { "title", "content" };
	}

	public String getKeywordField() {
		return "id";
	}

	public String[] getStoreFields() {
		return new String[] { "type", "site.id", "site.friendlyName", "title",
				"pubTime" };
	}

}
