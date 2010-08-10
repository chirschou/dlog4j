/*
 *  DLOG_StyleManager.java
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
 *  2006-5-20
 */
package com.liusoft.dlog4j;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServlet;

import com.liusoft.dlog4j.base.LayoutInfo;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 用于处理网站个性化风格
 * @author liudong
 */
public class DLOG_LayoutManager {

	private static String styles_path;
	
	public static void init(HttpServlet servlet){
		styles_path = servlet.getInitParameter("styles_base_path");
		if(StringUtils.isEmpty(styles_path))
			styles_path = servlet.getServletContext().getRealPath("/styles");
		else{
			if(styles_path.startsWith(Globals.LOCAL_PATH_PREFIX))
				styles_path = styles_path.substring(Globals.LOCAL_PATH_PREFIX.length());		
			else if(styles_path.startsWith("/"))
				styles_path = servlet.getServletContext().getRealPath(styles_path);
		}
		if(!styles_path.endsWith(File.separator))
			styles_path += File.separator;
	}
	
	/**
	 * 列表所有的布局
	 * @return
	 */
	public static List<LayoutInfo> layouts(){
		File f = new File(styles_path);
		File[] layouts = f.listFiles(new FileFilter(){
			public boolean accept(File f) {
				return !f.getName().startsWith("_") && f.isDirectory();
			}});
		List<LayoutInfo> dlog_layouts = new ArrayList<LayoutInfo>();
		for(int i=0;layouts!=null&&i<layouts.length;i++){
			LayoutInfo layout = new LayoutInfo(styles_path);
			layout.setName(layouts[i].getName());
			layout.setPreviewImg(layout.getName()+'/'+"main.jpg");
			layout.setCreateTime(layouts[i].lastModified());
			dlog_layouts.add(layout);
		}
		Collections.sort(dlog_layouts);
		return dlog_layouts;
	}
	
	/**
	 * 获取某个布局
	 * @param name
	 * @return
	 */
	public static LayoutInfo getLayout(String name){
		if(!isLayoutExist(name))
			return null;
		LayoutInfo layout = new LayoutInfo(styles_path);
		layout.setName(name);
		layout.setPreviewImg(layout.getName()+'/'+"main.jpg");
		return layout;
	}
	
	/**
	 * 判断网站预定义布局是否存在
	 * @param name
	 * @return
	 */
	public static boolean isLayoutExist(String name){
		return isStyleExist(name, "main.css");
	}
	
	/**
	 * 判断某一个样式控制文件是否存在
	 * @param layout
	 * @param css
	 * @return
	 */
	public static boolean isStyleExist(String layout, String css){
		StringBuffer main_css = new StringBuffer(styles_path);
		main_css.append(layout);
		main_css.append(File.separator);
		main_css.append(css);
		File f = new File(main_css.toString());
		return f.exists() && f.isFile();
	}
	
}
