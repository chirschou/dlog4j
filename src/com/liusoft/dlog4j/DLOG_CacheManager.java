/*
 *  DLOG_CacheManager.java
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
package com.liusoft.dlog4j;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * DLOG4J的缓存管理器
 * @author Winter Lau
 */
public class DLOG_CacheManager {
	
	final static Log log = LogFactory.getLog(DLOG_CacheManager.class);
	
	public static CacheManager manager;
	
	static{
		try {
			manager = CacheManager.getInstance();
			if(manager==null)
				manager = CacheManager.create();
		} catch (CacheException e) {
			log.fatal("Initialize cache manager failed.", e);
		}
	}

	/**
	 * 从缓存中获取对象
	 * @param cache_name
	 * @param key
	 * @return
	 */
	public static Serializable getObjectCached(String cache_name, Serializable key){
		Cache cache = getCache(cache_name);
		if(cache!=null){
			try {
				Element elem = cache.get(key);
				if(elem!=null && !cache.isExpired(elem))
					return elem.getValue();
			} catch (Exception e) {
				log.error("Get cache("+cache_name+") of "+key+" failed.", e);
			}
		}
		return null;
	}
	
	/**
	 * 把对象放入缓存中
	 * @param cache_name
	 * @param key
	 * @param value
	 */
	public synchronized static void putObjectCached(String cache_name, Serializable key, Serializable value){
		Cache cache = getCache(cache_name);
		if(cache!=null){
			try {
				cache.remove(key);
				Element elem = new Element(key, value);
				cache.put(elem);
			} catch (Exception e) {
				log.error("put cache("+cache_name+") of "+key+" failed.", e);
			}
		}
	}
	
	/**
	 * 获取指定名称的缓存
	 * @param arg0
	 * @return
	 * @throws IllegalStateException
	 */
	public static Cache getCache(String arg0) throws IllegalStateException {
		return manager.getCache(arg0);
	}

	/**
	 * 获取缓冲中的信息
	 * @param cache
	 * @param key
	 * @return
	 * @throws IllegalStateException
	 * @throws CacheException
	 */
	public static Element getElement(String cache, Serializable key) throws IllegalStateException, CacheException{
		Cache cCache = getCache(cache);
		return cCache.get(key);
	}
	
	/**
	 * 获取存储RSS信息的缓存
	 * @return
	 */
	public static Cache getRssCache(){
		if(manager!=null)
			return manager.getCache("DLOG4J_channels");
		return null;
	}
	
	/**
	 * 获取RSS缓存中的某个信息
	 * @param key
	 * @return
	 * @throws IllegalStateException
	 * @throws CacheException
	 */
	public static Element getRssElement(Serializable key) throws IllegalStateException, CacheException{
		Cache cache = getRssCache();		
		return (cache!=null)?cache.get(key):null;
	}

	/**
	 * 停止缓存管理器
	 */
	public static void shutdown(){
		if(manager!=null)
			manager.shutdown();
	}

}
