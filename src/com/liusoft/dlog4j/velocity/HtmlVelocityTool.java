/*
 *  HtmlVelocityTool.java
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
 *  2006-4-26
 */
package com.liusoft.dlog4j.velocity;

/**
 * 用于生成HTML标签的Toolbox类
 * @author liudong
 */
public class HtmlVelocityTool extends VelocityTool {
	
	public String image_src(String img_name){
		StringBuffer img = new StringBuffer();
		img.append(super.root());
		img.append("/images/");
		img.append(img_name);
		return img.toString();
	}

	public String img(String img_name){
		StringBuffer prefix = new StringBuffer("<img src=\"");
		prefix.append(super.root());
		prefix.append("/images/");
		prefix.append(img_name);
		prefix.append("\" border=\"0\" alt=\"\"/>");
		return prefix.toString();
	}

	public String img(String img_name, String alt){
		StringBuffer prefix = new StringBuffer("<img src=\"");
		prefix.append(super.root());
		prefix.append("/images/");
		prefix.append(img_name);
		prefix.append("\"");
		
		prefix.append(" alt=\"");
		prefix.append((alt==null)?"":alt);
		prefix.append("\"");

		prefix.append(" border=\"0\"");
		
		prefix.append("/>");
		return prefix.toString();
	}
	
	public String img(String img_name, String title, String alt, String align, int border){
		StringBuffer prefix = new StringBuffer("<img src=\"");
		prefix.append(super.root());
		prefix.append("/images/");
		prefix.append(img_name);
		prefix.append("\"");
		
		if(title!=null){
			prefix.append(" title=\"");
			prefix.append(title);
			prefix.append("\"");
		}

		prefix.append(" alt=\"");
		prefix.append((alt==null)?"":alt);
		prefix.append("\"");

		if(align!=null){
			prefix.append(" align=\"");
			prefix.append(align);
			prefix.append("\"");
		}
		
		prefix.append(" border=\"");
		prefix.append(border);
		prefix.append("\"");
		
		prefix.append("/>");
		return prefix.toString();
	}
	
}
