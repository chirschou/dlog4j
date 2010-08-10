/*
 *  RequestProxy.java
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
package com.liusoft.dlog4j.servlet;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.liusoft.dlog4j.Globals;

/**
 * 请求对象的封装，用于处理自动编码
 * @author liudong
 */
public class RequestProxy extends HttpServletRequestWrapper{

	protected String encoding;

	public RequestProxy(HttpServletRequest request) throws UnsupportedEncodingException{
		this(request, Globals.ENC_UTF_8);
	}
	
	public RequestProxy(HttpServletRequest request, String encoding) throws UnsupportedEncodingException{
		super(request);
		this.encoding = (encoding==null)?Globals.ENC_UTF_8:encoding;
	}
	
	/**
	 * 重载getParameter
	 */
	public String getParameter(String paramName) {
		String value = super.getParameter(paramName);
		if(value!=null){
			try {
				return new String(value.getBytes(Globals.ENC_8859_1),encoding);
			}catch(UnsupportedEncodingException e) {}
		}
		return value;
	}

	/**
	 * 重载getParameterMap
	 */
	public Map getParameterMap() {
		Map params = super.getParameterMap();
		HashMap new_params = new HashMap();
		Iterator iter = params.keySet().iterator();
		while(iter.hasNext()){
			String key = (String)iter.next();
			Object oValue = params.get(key);
			if(oValue.getClass().isArray()){
				String[] values = (String[])params.get(key);
				String[] new_values = new String[values.length];
				for(int i=0;i<values.length;i++){
					try {
						new_values[i] = new String(values[i].getBytes(Globals.ENC_8859_1),encoding);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						break;
					}
				}
				new_params.put(key, new_values);
			}
			else{
				String value = (String)params.get(key);
				String new_value = null;
				try {
					new_value = (value!=null)?
							new String(value.getBytes(Globals.ENC_8859_1),encoding):null;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					break;
				}
				if(new_value!=null)
					new_params.put(key,new_value);
			}
		}
		return new_params;
	}

	/**
	 * 重载getParameterValues
	 */
	public String[] getParameterValues(String arg0) {
		String[] values = super.getParameterValues(arg0);
		for(int i=0;values!=null&&i<values.length;i++){
			try {
				values[i] = new String(values[i].getBytes(Globals.ENC_8859_1),encoding);
			} catch (UnsupportedEncodingException e) {}
		}
		return values;
	}
	
	public String getEncoding() {
		return encoding;
	}
}
