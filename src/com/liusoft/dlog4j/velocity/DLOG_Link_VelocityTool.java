/*
 *  DLOG_Link_VelocityTool.java
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
package com.liusoft.dlog4j.velocity;

import java.util.List;

import org.apache.velocity.tools.struts.StrutsLinkTool;

import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.dao.LinkDAO;

/**
 * 用于简化页面链接的Toolbox类
 * 用法: $link.encodeURL(...)
 * @author liudong
 */
public class DLOG_Link_VelocityTool extends StrutsLinkTool {

	/**
	 * 为了解决$link总是出现空指针异常导致页面出错的问题
	 */
	public String encodeURL(String arg0) {
		if(arg0==null)
			return arg0;
		return super.encodeURL(arg0);
	}
	
	/**
	 * 获取友情链接的总数
	 * @param site
	 * @return
	 * @see /wml/links.vm
	 */
	public int link_count(SiteBean site){
		if(site == null)
			return -1;
		return LinkDAO.getLinkCount(site);
	}
	
	/**
	 * 分页显示友情链接
	 * @param site
	 * @param page
	 * @param count
	 * @return
	 * @see /wml/links.vm
	 */
	public List links(SiteBean site, int page, int count){
		if(site == null)
			return null;
		int fromIdx = (page - 1) * count;
		return LinkDAO.getLinksOfSite(site, fromIdx, count);
	}

}
