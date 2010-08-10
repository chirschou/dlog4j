/*
 *  DLOG_Comment_VelocityTool.java
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
 *  2006-7-22
 */
package com.liusoft.dlog4j.velocity;

import java.util.List;

import com.liusoft.dlog4j.dao.CommentDAO;

/**
 * 评论相关的Toolbox类
 * @author liudong
 */
public class DLOG_Comment_VelocityTool {

	/**
	 * 列出某个对象的评论
	 * @param eid
	 * @param etype
	 * @param page
	 * @param pageSize
	 * @param reverse
	 * @param withContent
	 * @return
	 */
	public List comments(int eid, int etype, int page, int pageSize,
			boolean reverse, boolean withContent) {
		if(eid < 0 || etype < 0) return null;
		return CommentDAO.listComments(eid, etype, page, pageSize, reverse,
				withContent);
	}

	/**
	 * 列出某个类别的最新评论
	 * @param siteid
	 * @param etype
	 * @param fromIdx
	 * @param fetchSize
	 * @return
	 */
	public static List comments(int siteid, int etype, int fromIdx, int fetchSize){
		if(siteid <= 0 || etype < 0) return null;
		return CommentDAO.listNewestComments(siteid, etype, fromIdx, fetchSize);
	}
	
	/**
	 * 列出某个站点所有最新的评论
	 * @param siteid
	 * @param fromIdx
	 * @param fetchSize
	 * @return
	 */
	public static List comments(int siteid, int fromIdx, int fetchSize){
		if(siteid <= 0 ) return null;
		return CommentDAO.listNewestComments(siteid, fromIdx, fetchSize);
	}
}
