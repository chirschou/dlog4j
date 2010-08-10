/*
 *  DLOGTemplateManager.java
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
 *  2006-5-29
 */
package com.liusoft.dlog4j;

import com.liusoft.dlog4j.beans.SiteBean;

/**
 * 模板管理
 * @author liudong
 */
public class DLOGTemplateManager {

	private final static ThreadLocal<SiteBean> request_sites = new ThreadLocal<SiteBean>();
	
	/**
	 * 保存当前的网站编号
	 * @param site_id
	 */
	public static void saveSite(SiteBean site){
		if(site!=null)
			request_sites.set(site);
	}
	
	public static SiteBean getSite(){
		return request_sites.get();
	}
	
	public static void clearSite(){
		request_sites.set(null);
	}
	
}
