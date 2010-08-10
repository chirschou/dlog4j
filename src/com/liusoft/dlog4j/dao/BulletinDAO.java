/*
 *  BulletinDAO.java
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
package com.liusoft.dlog4j.dao;

import java.util.List;

import com.liusoft.dlog4j.beans.BulletinBean;

/**
 * 公告信息数据库接口
 * @author liudong
 */
public class BulletinDAO extends DAO {

	/**
	 * 获取公告详细信息
	 * @param id
	 * @return
	 */
	public static BulletinBean getBulletinById(int id){
		if(id <= 0)
			return null;
		return (BulletinBean)getSession().get(BulletinBean.class, new Integer(id));
	}
	
	/**
	 * 查询某个网站的公告信息
	 * @param site_id
	 * @param fromIdx
	 * @param fetchSize
	 * @return
	 */
	public static List listBulletins(int site_id, int fromIdx, int fetchSize, boolean withContent){
		StringBuffer hql = new StringBuffer("FROM ");
		hql.append(withContent?"BulletinBean":"BulletinOutlineBean");
		hql.append(" AS b WHERE b.site.id=? AND b.status=? ORDER BY b.id DESC");
		return executeQuery(hql.toString(), fromIdx, fetchSize, site_id, BulletinBean.STATUS_NORMAL);
	}
	
	/**
	 * 获取公告信息条数
	 * @param site_id
	 * @return
	 */
	public static int getBulletinCount(int site_id){
		final String hql = "SELECT COUNT(*) FROM BulletinBean b WHERE b.site=? AND b.status=?";
		return executeStatAsInt(hql, site_id, BulletinBean.STATUS_NORMAL);
	}
	
	/**
	 * 删除公告信息
	 * @param site_id
	 * @param b_id
	 * @return
	 */
	public static int deleteBulletinByID(int site_id, int b_id){
		final String hql = "DELETE FROM BulletinBean b WHERE b.site=? AND b.id=?";
		return commitUpdate(hql, site_id, b_id);
	}
	
}
