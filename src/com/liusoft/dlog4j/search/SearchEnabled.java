/*
 *  SearchEnabled.java
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
package com.liusoft.dlog4j.search;

/**
 * 支持搜索功能的接口
 * @author Winter Lau
 */
public interface SearchEnabled {

	/**
	 * 对象名称，例如：forum_topic，该名称用来对应唯一的索引目录名
	 * @return
	 */
	public String name();
	
	/**
	 * 获取搜索对象的关键字字段名，例如id
	 * @return
	 */
	public String getKeywordField();
	
	/**
	 * 返回搜索对象需要存储的字段名，例如createTime, author等
	 * @return
	 */
	public String[] getStoreFields();
	
	/**
	 * 返回搜索对象的索引字段，例如title,content
	 * @return
	 */
	public String[] getIndexFields();

}
