/*
 *  ExternalReferBean.java
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
 */
package com.liusoft.dlog4j.dao;

import java.util.List;

/**
 * 用于记录外部网站引用
 * 通过该类查询访问用户的来源
 * @author liudong
 */
public class ExternalReferDAO extends DAO {

	/**
	 * 列出外部网站对某个信息的引用资料
	 * @param siteid
	 * @param ref_id
	 * @param ref_type
	 * @param count
	 * @return
	 */
	public static List listRefers(int siteid, int ref_id, int ref_type, int count){
		return executeNamedQuery("LIST_EXTERNAL_REFER",-1,count,siteid,ref_id,ref_type);
	}
	
}
