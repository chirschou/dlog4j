/*
 *  ConfigDAO.java
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
package com.liusoft.dlog4j.dao;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.liusoft.dlog4j.beans.ConfigBean;

/**
 * 系统配置数据数据库访问接口
 * @author Winter Lau
 */
public class ConfigDAO extends DAO {
	
	private final static int MAX_CATALOG_COUNT = 20;
	private final static int MAX_ALBUM_COUNT = 20;
	private final static int MAX_PHOTO_SIZE = 100000;	//默认相册的最大容量是100兆 
	private final static int MAX_REPLY_COUNT = 1000;	//文章＆相片的最大评论数，负数为无限制
	
	public static int getMaxCatalogCount(int siteid){
		return intValue(siteid, "MAX_CATALOG_COUNT", MAX_CATALOG_COUNT);
	}
	
	public static int getMaxAlbumCount(int siteid){
		return intValue(siteid, "MAX_ALBUM_COUNT", MAX_ALBUM_COUNT);
	}
	
	public static int getMaxPhotoSize(int siteid){
		return intValue(siteid, "MAX_PHOTO_SIZE", MAX_PHOTO_SIZE);
	}
	
	public static int getMaxReplyCount(int siteid){
		return intValue(siteid, "MAX_REPLY_COUNT", MAX_REPLY_COUNT);
	}
	
	/**
	 * 获取某个配置信息
	 * @param site_id
	 * @param key
	 * @return
	 */
	public static ConfigBean getConfig(int site_id, String key){
		if(site_id < 1 || key==null)
			return null;
		return (ConfigBean)namedUniqueResult("GET_CONFIG", key, site_id);
	}

	public static Date dateValue(int site_id, String key) {
		ConfigBean cb = getConfig(site_id, key);		
		return (cb==null)?null:cb.dateValue();
	}

	protected static int intValue(int site_id, String key) {
		return intValue(site_id, key, -1);
	}

	public static int intValue(int site_id, String key, int defaultValue) {
		ConfigBean cb = getConfig(site_id, key);		
		return (cb==null)?defaultValue:cb.intValue();
	}

	public static String stringValue(int site_id, String key) {
		ConfigBean cb = getConfig(site_id, key);		
		return (cb==null)?null:cb.stringValue();
	}

	public static Timestamp timestampValue(int site_id, String key) {
		ConfigBean cb = getConfig(site_id, key);		
		return (cb==null)?null:cb.timestampValue();
	}

	public static Time timeValue(int site_id, String key) {
		ConfigBean cb = getConfig(site_id, key);		
		return (cb==null)?null:cb.timeValue();
	}

}
