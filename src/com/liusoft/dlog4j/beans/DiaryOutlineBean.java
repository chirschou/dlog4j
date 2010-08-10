/*
 *  DiaryOutlineBean.java
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
package com.liusoft.dlog4j.beans;

import com.liusoft.dlog4j.base._DiaryBase;

/**
 * 日记的预览，不包括详细内容，用于与评论相关联
 * @author Winter Lau
 */
public class DiaryOutlineBean extends _DiaryBase{
	
	public DiaryOutlineBean(){}
	
	public DiaryOutlineBean(int id){
		super.setId(id);
	}

}
