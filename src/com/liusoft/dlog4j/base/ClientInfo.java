/*
 *  ClientInfo.java
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

import javax.servlet.http.HttpServletRequest;

import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 浏览器端信息
 * @author Winter Lau
 */
public class ClientInfo implements Serializable{

	public final static int CLIENT_TYPE_HTML = 0;
	public final static int CLIENT_TYPE_WML  = 1;
	
	protected int type;			//客户端类型，包括有PC或者手机 0:PC,1:MOBILE
	protected String addr;		//客户端IP地址
	protected String userAgent;	//客户端浏览器类型
	
	public ClientInfo(){}
	
	public ClientInfo(HttpServletRequest req, int clientType){
		setAddr(req.getRemoteAddr());
		this.type = clientType;
		String user_agent = RequestUtils.getHeader(req, "user-agent");
		setUserAgent(StringUtils.abbreviate(user_agent, 95));
	}
	
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
}
