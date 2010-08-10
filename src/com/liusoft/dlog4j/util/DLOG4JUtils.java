/*
 *  DLOG4JUtils.java
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
package com.liusoft.dlog4j.util;

import java.text.MessageFormat;

/**
 * 杂项工具类
 * @author liudong
 */
public class DLOG4JUtils {

	public final static MessageFormat IP_PATTERN = new MessageFormat("{0}.{1}.{2}.{3}");

	/**
	 * 判断是否是一个合法的站点名
	 * @param siteName
	 * @return
	 */
	public static boolean isLegalSiteName(String siteName) {
		if (StringUtils.isEmpty(siteName)
				|| !StringUtils.isAsciiOrDigit(siteName))
			return false;
		if ("wml".equalsIgnoreCase(siteName)
				|| "html".equalsIgnoreCase(siteName)
				|| "dlog".equalsIgnoreCase(siteName))
			return false;
		return true;
	}

	/**
	 * 返回以K字节为单位的文件大小
	 * @param size
	 * @return
	 */
	public static int sizeInKbytes(long size){
		int iSize = (int)Math.round(size/1024.0);
		return Math.max(1, iSize);
	}
	
	/**
	 * 判断某个IP地址是否在一个指定的子网中
	 * @param ip
	 * @param net_ip
	 * @param net_mask
	 * @return
	 */
	public static boolean isIpInNet(int ip, int net_ip, int net_mask){
		return (ip & net_mask) == (net_ip & net_mask);
	}
	
	/**
	 * 判断某个IP地址是否在一个指定的子网中
	 * @param ip
	 * @param net_ip
	 * @param net_mask
	 * @return
	 */
	public static boolean isIpInNet(String s_ip, String s_net_ip, String s_net_mask){
		int ip = getIPValue(s_ip);
		if(ip==-1)
			throw new IllegalArgumentException(s_ip);
		int net_ip = getIPValue(s_net_ip);
		if(net_ip==-1)
			throw new IllegalArgumentException(s_net_ip);
		int net_mask = getIPValue(s_net_mask);
		if(net_mask==-1)
			throw new IllegalArgumentException(s_net_mask);
		return isIpInNet(ip,net_ip,net_mask);
	}
	
	/**
	 * 计算IP的整数值
	 * @param sip
	 * @return
	 */
	public static int getIPValue(String sip){
		try {
			Object[] ips = IP_PATTERN.parse(sip);
			int ipValue = 0;
			for(int i=0;i<4;i++){
				int nodeValue = Integer.parseInt((String)ips[i]);
				ipValue += nodeValue << ((3-i) * 8 );
			}
			return ipValue;
		} catch (Exception e) {	
			e.printStackTrace();		
		}
		return -1;
	}
	
	/**
	 * 验证IP地址格式是否正确
	 * @param ip
	 * @return
	 */
	public static boolean isAddrAvailable(String sip){
		if(StringUtils.isEmpty(sip))
			return false;
		try {
			Object[] ips = IP_PATTERN.parse(sip);
			if(ips.length==4){
				int ip = Integer.parseInt((String)ips[0]);
				if(ip<0 || ip>255)
					return false;
				ip = Integer.parseInt((String)ips[1]);
				if(ip<0 || ip>255)
					return false;
				ip = Integer.parseInt((String)ips[2]);
				if(ip<0 || ip>255)
					return false;
				ip = Integer.parseInt((String)ips[3]);
				if(ip<0 || ip>255)
					return false;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args){
		String ip = "192.168.0.119";
		System.out.println(isAddrAvailable(ip));
		System.out.println(getIPValue(ip));
		System.out.println(isIpInNet(ip, "192.168.0.0", "255.255.255.0"));
	}
	
}
