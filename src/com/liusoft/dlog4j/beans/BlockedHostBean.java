/*
 *  BlockedHostBean.java
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
package com.liusoft.dlog4j.beans;

import java.util.Date;

import com.liusoft.dlog4j.base._BeanBase;
import com.liusoft.dlog4j.util.DLOG4JUtils;

/**
 * 黑主机名单
 * @author liudong
 * @database_independence 可单独存储,不依赖于其他表
 */
public class BlockedHostBean extends _BeanBase {

	public static int TYPE_BLOCKED_NOTHING = 0x00;
	public static int TYPE_BLOCKED_VIEW = 0x01;
	public static int TYPE_BLOCKED_POST = 0x02;
	public static int TYPE_BLOCKED_ALL  = 0x03;
	
	public static String DEFAULT_MASK = "255.255.255.255";
	
	protected int siteId;
	protected int ip;
	protected String sip;
	
	protected int mask;
	protected String smask;
	
	protected int type;
	
	protected Date blockedTime;
	
	protected int status;

	public BlockedHostBean(){}
	
	public BlockedHostBean(String sip, String smask){
		if(!DLOG4JUtils.isAddrAvailable(sip))
			throw new IllegalArgumentException(sip);
		setSip(sip);
		setIp(DLOG4JUtils.getIPValue(sip));
		if(smask!=null){
			if(!DLOG4JUtils.isAddrAvailable(smask))
				throw new IllegalArgumentException(smask);
			setSmask(smask);
		}
		else
			setSmask(DEFAULT_MASK);
		setMask(DLOG4JUtils.getIPValue(getSmask()));
		setBlockedTime(new Date());
		type = TYPE_BLOCKED_POST;
		status = STATUS_NORMAL;
	}
	
	public Date getBlockedTime() {
		return blockedTime;
	}

	public void setBlockedTime(Date blockedTime) {
		this.blockedTime = blockedTime;
	}

	public int getIp() {
		return ip;
	}

	public void setIp(int ip) {
		this.ip = ip;
	}

	public int getMask() {
		return mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	public String getSip() {
		return sip;
	}

	public void setSip(String sip) {
		this.sip = sip;
	}

	public String getSmask() {
		return smask;
	}

	public void setSmask(String smask) {
		this.smask = smask;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	
}
