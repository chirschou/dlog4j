/*
 *  DiaryBean.java
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

import java.util.List;

import com.liusoft.dlog4j.base.TrackbackEnabled;
import com.liusoft.dlog4j.base._DiaryBase;
import com.liusoft.dlog4j.search.SearchEnabled;

import com.liusoft.dlog4j.util.HTML_Utils;

/**
 * 日记对象
 * @see SearchEnabled
 * @author liudong
 */
public class DiaryBean extends _DiaryBase implements TrackbackEnabled, SearchEnabled{

	public DiaryBean(){}
	
	public DiaryBean(int journal_id){
		super.setId(journal_id);
	}
	
	public List getTrackbacks() {
		return super.getTrackbacks();
	}

	public void setTrackbacks(List tbs) {
		super.setTrackbacks(tbs);
	}

	/**
	 * 获取HTML的预览信息
	 * @return
	 */
	public String getPreviewContent(){
		return HTML_Utils.preview(content, 400);
	}
	
	/***** The methods below is for search proxy *****/
	public String name() {
		return "diary";
	}

	public String getKeywordField() {
		return "id";
	}

	/**
	 * 存储的内容包括作者、所有者、网站、分类、标题时间等信息
	 */
	public String[] getStoreFields() {
		return new String[] { "author", "site.id", "site.friendlyName",
				"catalog.id", "catalog.name", "owner.id", "owner.nickname",
				"title", "writeTime" };
	}

	public String[] getIndexFields() {
		return new String[] { "keyword", "title", "content" };
	}

}
