/*
 *  SiteStatInfo.java
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
package com.liusoft.dlog4j.base;

import java.io.Serializable;

/**
 * 网站的访问统计信息
 * @author Winter Lau
 */
public class SiteStatInfo implements Serializable {

	private int site;
	private long uvToday;
	private long uvThisWeek;
	private long uvThisMonth;
	private long uvThisYear;
	private long uvTotal;
	
	public int getSite() {
		return site;
	}
	public void setSite(int site) {
		this.site = site;
	}
	public long getUvThisMonth() {
		return uvThisMonth;
	}
	public void setUvThisMonth(long uvThisMonth) {
		this.uvThisMonth = uvThisMonth;
	}
	public long getUvThisWeek() {
		return uvThisWeek;
	}
	public void setUvThisWeek(long uvThisWeek) {
		this.uvThisWeek = uvThisWeek;
	}
	public long getUvThisYear() {
		return uvThisYear;
	}
	public void setUvThisYear(long uvThisYear) {
		this.uvThisYear = uvThisYear;
	}
	public long getUvToday() {
		return uvToday;
	}
	public void setUvToday(long uvToday) {
		this.uvToday = uvToday;
	}
	public long getUvTotal() {
		return uvTotal;
	}
	public void setUvTotal(long uvTotal) {
		this.uvTotal = uvTotal;
	}
	
}
