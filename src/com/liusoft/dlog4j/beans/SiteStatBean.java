/*
 *  SiteStatBean.java
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
 *  
 */
package com.liusoft.dlog4j.beans;

import java.util.Date;

import com.liusoft.dlog4j.base._BeanBase;

/**
 * 网站统计信息
 * @author Winter Lau
 * @database_independence 可单独存储,不依赖于其他表
 */
public class SiteStatBean extends _BeanBase {

	public final static int SOURCE_WEB = 0x01;	//来源于WEB
	public final static int SOURCE_WAP = 0x02;	//来源于WAP
	public final static int SOURCE_APP = 0x04;	//来源于应用程序

	protected int siteId;
	protected int statDate;
	protected int source;
	protected int uvCount;
	protected int pvCount;	
	protected Date updateTime;
	
	public int getStatDate() {
		return statDate;
	}
	public void setStatDate(int statDate) {
		this.statDate = statDate;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getPvCount() {
		return pvCount;
	}
	public void setPvCount(int pvCount) {
		this.pvCount = pvCount;
	}
	public void incPvCount(int count){
		this.pvCount += count;
	}
	public int getUvCount() {
		return uvCount;
	}
	public void setUvCount(int uvCount) {
		this.uvCount = uvCount;
	}
	public void incUvCount(int count){
		this.uvCount += count;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	
	public Object clone(){
		SiteStatBean ssb = new SiteStatBean();
		ssb.setId(super.getId());
		ssb.setPvCount(this.pvCount);
		ssb.setUvCount(this.uvCount);
		ssb.setSource(this.source);
		ssb.setStatDate(this.statDate);
		ssb.setUpdateTime(this.updateTime);
		ssb.setSiteId(this.siteId);
		return ssb;
	}
}
