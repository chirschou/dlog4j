/*
 *  TopicBean.java
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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.liusoft.dlog4j.base.ClientInfo;
import com.liusoft.dlog4j.base._TopicBeanBase;
import com.liusoft.dlog4j.search.SearchEnabled;

/**
 * 帖子
 * @author Winter Lau
 */
public class TopicBean extends _TopicBeanBase implements SearchEnabled{
	
	public TopicBean(){}
	
	public TopicBean(HttpServletRequest req, int client_type){
		super.client = new ClientInfo(req, client_type);
		super.setCreateTime(new Date());
	}

	/**** 以下方法属于接口SearchEnabled中 ****/
	public String name() {
		return "bbs_topic";
	}

	public String getKeywordField() {
		return "id";
	}

	public String[] getStoreFields() {
		return new String[] { "site.id", "site.friendlyName",
				"forum.id", "forum.name", "user.id", "user.nickname",
				"title", "createTime" };
	}

	public String[] getIndexFields() {
		return new String[]{"title", "content", "tags"};
	}

}
