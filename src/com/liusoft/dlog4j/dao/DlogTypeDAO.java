/*
 *  DlogTypeDAO.java
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

import com.liusoft.dlog4j.beans.TypeBean;

/**
 * 分类(dlog_type)数据接口
 * @author liudong
 */
public class DlogTypeDAO extends DAO {
	
	/**
	 * 根据编号加载类别对象
	 * @param type_id
	 * @return
	 */
	public static TypeBean getTypeByID(int type_id){
		if(type_id<=0)
			return null;
		return (TypeBean)DAO.getBean(TypeBean.class, type_id);
	}
	/**
	 * 列出所有的第一级的分类
	 * @return
	 */
	public static List listRootTypes(){
		return findNamedAll("LIST_ROOT_TYPES");
	}
	
	/**
	 * 列出子类型
	 * @param type_id
	 * @return
	 */
	public static List listSubTypes(int type_id){
		return findNamedAll("LIST_SUB_TYPES",type_id);
	}

}
