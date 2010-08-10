/*
 *  ReplyDAO.java
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
package com.liusoft.dlog4j.dao;

import java.util.List;

import com.liusoft.dlog4j.base._ReplyBean;

/**
 * 评论数据访问接口
 * @author liudong
 */
public class ReplyDAO extends DAO {

	/**
	 * 根据评论类型以及编号来获取对应的评论信息
	 * @param parent_cls
	 * @param id
	 * @return
	 */
	public static _ReplyBean getReply(Class reply_cls, int id){
		if(id < 0)
			return null;
		return (_ReplyBean)getBean(reply_cls, id);
	}
	
	/**
	 * 修改评论
	 * @param reply
	 */
	public static void updateReply(_ReplyBean reply){
		flush();
	}
	
	/**
	 * 列出某个ID前的N个最新评论
	 * 为了列出整个网站的最新评论只需要将last_reply_id参数值设为Integer.MAX_VALUE
	 * @param reply_cls
	 * @param site_id
	 * @param parent_type
	 * @param last_reply_id
	 * @param count
	 * @return
	 */
	public static List listReplies(Class reply_cls, int site_id, int last_reply_id, int count){		
		StringBuffer hql = new StringBuffer("FROM ");
		hql.append(reply_cls.getName());
		hql.append(" AS r WHERE r.site.id=? AND r.id<? AND r.status=? ORDER BY r.id DESC");
		return executeQuery(hql.toString(), 0, count, site_id, last_reply_id, _ReplyBean.STATUS_NORMAL);
	}
}
