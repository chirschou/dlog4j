/*
 *  DLOG_Type_VelocityTool.java
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
package com.liusoft.dlog4j.velocity;

import java.util.List;

import com.liusoft.dlog4j.dao.DlogTypeDAO;

/**
 * 内容类别的Toolbox类
 * TODO: Cache the resultset
 * @author liudong
 */
public class DLOG_Type_VelocityTool extends VelocityTool {

	/**
	 * 获取顶层的所有分类
	 * @return
	 */
	public List root_types(){
		return DlogTypeDAO.listRootTypes();
	}
	
}
