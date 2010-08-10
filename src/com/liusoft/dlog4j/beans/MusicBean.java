/*
 *  MusicBean.java
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

import com.liusoft.dlog4j.base._MusicBeanBase;
import com.liusoft.dlog4j.search.SearchEnabled;

/**
 * 音乐信息
 * @author Winter Lau
 */
public class MusicBean extends _MusicBeanBase implements SearchEnabled{
	
	/*************************************************************/
	public String[] getIndexFields() {
		return new String[] { "album", "title", "word", "singer" };
	}

	public String getKeywordField() {
		return "id";
	}

	public String[] getStoreFields() {
		return new String[] { "album", "title", "singer", "url", "createTime",
				"type", "site.id", "site.friendlyName" };
	}

	public String name() {
		return "music";
	}
}
