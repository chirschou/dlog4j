/*
 *  _BeanBase.java
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
package com.liusoft.dlog4j.base;

import java.io.Serializable;

/**
 * 所有BEAN的基类
 * @author liudong
 */
public abstract class _BeanBase implements Serializable {

	public final static int STATUS_NORMAL  = 0;
	public final static int STATUS_DELETED = 2;
	
	public final static int STATUS_ONLINE  = 1;
	public final static int STATUS_OFFLINE = 0;
	
	public final static int TYPE_DIARY 	 = 0x01;
	public final static int TYPE_PHOTO   = 0x02;
	public final static int TYPE_MMEDIA	 = 0x03;	//多媒体(音频/视频/动画)
	public final static int TYPE_BBS	 = 0x04;

	public final static int INFO_TYPE_GENERAL 	= 0x00;	//普通信息
	public final static int INFO_TYPE_ELITE 	= 0x01;	//精华信息
	public final static int INFO_TYPE_TOP 		= 0x10;	//置顶信息
	public final static int INFO_TYPE_TOP_ELITE = 0x11;	//置顶且是精华信息

	public final static int CLIENT_HTML = 0;
	public final static int CLIENT_WML = 1;
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		//不同的子类尽管ID是相同也是不相等的
		if(!getClass().equals(obj.getClass()))
			return false;
		_BeanBase wb = (_BeanBase) obj;
		return getId() == wb.getId();
	}

	public int hashCode() {
		return _BeanBase.class.hashCode() + getId();
	}

}
