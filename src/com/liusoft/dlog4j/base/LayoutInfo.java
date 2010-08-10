/*
 *  DLOG_StyleInfo.java
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
package com.liusoft.dlog4j.base;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统存在的一个布局定义
 * @author liudong
 */
public class LayoutInfo implements Serializable, Comparable {

	private String baseStylePath;
	private String name;
	private String previewImg;
	private long createTime;
	private List styles;

	public LayoutInfo(String baseStylePath){
		this.baseStylePath = baseStylePath;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPreviewImg() {
		return previewImg;
	}

	public void setPreviewImg(String previewImg) {
		this.previewImg = previewImg;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int compareTo(Object arg0) {
		LayoutInfo li = (LayoutInfo)arg0;
		return (int)(createTime - li.getCreateTime());
	}

	public synchronized List getStyles(){
		if(styles != null)
			return styles;
		StringBuffer path = new StringBuffer(baseStylePath);
		path.append(name);
		File fPath = new File(path.toString());
		String[] css_files = fPath.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".css") && !name.startsWith("_");
			}
		});
		styles = new ArrayList();
		for(int i=0;css_files!=null&&i<css_files.length;i++){
			if(css_files[i].endsWith("main.css"))
				continue;
			StyleInfo style = new StyleInfo();
			style.setFileName(css_files[i]);
			style.setUri(name + '/' + css_files[i]);
			style.setColor(css_files[i].substring(0, css_files[i].length()-4));
			styles.add(style);
		}
		return styles;
	}
	
	/**
	 * 样式定义
	 * @author liudong
	 */
	public class StyleInfo implements Serializable{
		private String fileName;
		private String color;
		private String uri;
		
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
	}
	
	public static void main(String[] args) {
		LayoutInfo layout = new LayoutInfo("C:\\PROJECTS\\JAVA\\DLOG4J_V3\\webapp\\styles\\");
		layout.setName("1");
		List styles = layout.getStyles();
		for(int i=0;i<styles.size();i++){
			StyleInfo si = (StyleInfo)styles.get(i);
			System.out.println("FILE: "+si.getFileName()+",COLOR: "+si.getColor()+",URI: "+si.getUri());
		}
	}

}
