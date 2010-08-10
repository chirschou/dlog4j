/*
 *  DLOG_Stat_VelocityTool.java
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

import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liusoft.dlog4j.DLOG_CacheManager;
import com.liusoft.dlog4j.base.DlogStatInfo;
import com.liusoft.dlog4j.base.SiteStatInfo;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.dao.DlogDAO;
import com.liusoft.dlog4j.dao.SiteDAO;
import com.liusoft.dlog4j.dao.VisitStatDAO;
import com.liusoft.dlog4j.util.DateUtils;
import com.liusoft.dlog4j.util.RequestUtils;

/**
 * 用于网站访问统计的Toolbox
 * @author Winter Lau
 */
public class DLOG_Stat_VelocityTool{

	private final static Log log = LogFactory.getLog(DLOG_Stat_VelocityTool.class);
	private final static String CACHE_KEY = "vstat";

	/**
	 * 注册网站总数
	 * @return
	 */
	public int site_count(){
		return SiteDAO.getSiteCount();
	}
	
	/**
	 * 获取系统的统计信息
	 * @param site
	 * @return
	 */
	public DlogStatInfo get_dlog_stat_info(SiteBean site){
		if(site==null)
			return null;
		String nSite = "stat_info_"+site.getId();
		DlogStatInfo dsi = (DlogStatInfo)DLOG_CacheManager.getObjectCached(CACHE_KEY, nSite);
		if(dsi==null){
			dsi = DlogDAO.getDlogStatInfo(site.getId());
			DLOG_CacheManager.putObjectCached(CACHE_KEY, nSite, dsi);
		}
		return dsi;
	}
	
	/**
	 * 获取某个网站的访问统计信息
	 * @param site
	 * @return
	 */
	public SiteStatInfo get_site_stat_info(SiteBean site) {
		if (site == null)
			return null;

		SiteStatInfo ssi = (SiteStatInfo) DLOG_CacheManager.getObjectCached(
				CACHE_KEY, new Integer(site.getId()));

		if (ssi == null) {
			ssi = new SiteStatInfo();

			ssi.setSite(site.getId());
			ssi.setUvThisMonth(VisitStatDAO.getUVThisMonth(site));
			ssi.setUvThisWeek(VisitStatDAO.getUVThisWeek(site));
			ssi.setUvThisYear(VisitStatDAO.getUVThisYear(site));
			ssi.setUvToday(VisitStatDAO.getUVToday(site));
			ssi.setUvTotal(VisitStatDAO.getUVTotal(site));

			DLOG_CacheManager.putObjectCached(CACHE_KEY, new Integer(site
					.getId()), ssi);
		}
		return ssi;
	}
	
	/**
	 * 今日访问人次
	 * @param site
	 * @return
	 */
	public long uv_today(SiteBean site){
		if(site==null)
			return -1;
		return VisitStatDAO.getUVToday(site);
	}

	/**
	 * 本周访问人次
	 * @param site
	 * @return
	 */
	public long uv_this_week(SiteBean site){
		if(site==null)
			return -1;
		return VisitStatDAO.getUVThisWeek(site);
	}

	/**
	 * 本月访问人次
	 * @param site
	 * @return
	 */
	public long uv_this_month(SiteBean site){
		if(site==null)
			return -1;
		return VisitStatDAO.getUVThisMonth(site);
	}

	/**
	 * 今年访问人次
	 * @param site
	 * @return
	 */
	public long uv_this_year(SiteBean site){
		if(site==null)
			return -1;
		return VisitStatDAO.getUVThisYear(site);
	}

	/**
	 * 素有访问人次
	 * @param site
	 * @return
	 */
	public long uv_total(SiteBean site){
		if(site==null)
			return -1;
		return VisitStatDAO.getUVTotal(site);
	}
	
	/**
	 * 记录页面访问统计
	 * @param site
	 */
	public boolean execute(HttpServletRequest request,
			HttpServletResponse response, SiteBean site, int source) {
		boolean write_db = false;
		Cookie u_cookie = RequestUtils.getCookie(request, KEY_COOKIE);
		String ident = getSiteIdAsString(site);
		if(u_cookie == null){
			//设置Cookie有效期到今天结束
			Calendar t = Calendar.getInstance();
			t.add(Calendar.DATE, 1);
			DateUtils.resetTime(t);
			int maxAge = (int)(t.getTime().getTime()-System.currentTimeMillis()) / 1000;
			RequestUtils.setCookie(request, response, KEY_COOKIE, ident, maxAge);
			write_db = true;
		}
		else{
			String cookie_value = u_cookie.getValue();
			if(cookie_value.indexOf(ident)<0){
				//设置Cookie有效期到今天结束
				Calendar t = Calendar.getInstance();
				t.add(Calendar.DATE, 1);
				DateUtils.resetTime(t);
				int maxAge = (int)(t.getTime().getTime()-System.currentTimeMillis()) / 1000;
				//设置Cookie
				RequestUtils.setCookie(request, response, KEY_COOKIE, cookie_value+ident, maxAge);
				write_db = true;
			}
		}
		if(write_db){
			//写访问人次
			try {
				VisitStatDAO.writeStatData((site!=null)?site.getId():-1, 1, source);
				return true;
			} catch (Exception e) {
				log.error("write visit stat failed.", e);
			}
		}
		return false;
	}

	private String getSiteIdAsString(SiteBean site){
		StringBuffer ident = new StringBuffer();
		ident.append('#');
		ident.append((site!=null)?site.getId():-1);
		ident.append(':');
		return ident.toString();
	}
	
	private final static String KEY_COOKIE = "vt";
	
}
