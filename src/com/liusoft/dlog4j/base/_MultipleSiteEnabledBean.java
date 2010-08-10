/*
 *  _MultiSiteEnabledBean.java
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

import com.liusoft.dlog4j.beans.SiteBean;

/**
 * 支持多用户的对象基类
 * @author liudong
 */
public abstract class _MultipleSiteEnabledBean extends _BeanBase {
	
	/**
	 * 对象所属的网站
	 */
	private SiteBean site;

	public SiteBean getSite() {
		return site;
	}

	public void setSite(SiteBean site) {
		this.site = site;
	}

}
