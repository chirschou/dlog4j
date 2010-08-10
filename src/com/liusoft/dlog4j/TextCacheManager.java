/*
 *  TextCacheManager.java
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
 *  2006-5-12
 */
package com.liusoft.dlog4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liusoft.dlog4j.util.StringUtils;

/**
 * 大文本缓存处理
 * {base}/{type}/{id/10000}/id.htm
 * @author liudong
 * @call_by 
 */
public class TextCacheManager {
	
	private final static Log log = LogFactory.getLog(TextCacheManager.class);
	
	private static String basePath;

	/**
	 * 初始化存放目录
	 * @param arg0
	 */
	public synchronized static void init(String arg0){
		if(basePath == null){
			if(!StringUtils.equals("/", File.separator))
				basePath = StringUtils.replace(arg0, "/", File.separator);
			else
				basePath = arg0;
			if(!basePath.endsWith(File.separator))
				basePath += File.separator;
			File f = new File(basePath);
			if(!f.exists())
				f.mkdirs();
		}
	}
	
	/**
	 * 根据类型和编号来获取文本内容
	 * @param type
	 * @param id
	 * @return
	 */
	public static String getTextContent(int type, long id){
		String path = getFilePath(type, id);
		BufferedReader br = null;
		StringBuffer text = new StringBuffer();
		try{
			String line_sep = System.getProperty("line.separator");
			if(line_sep==null)
				line_sep = "\r\n";
			br = new BufferedReader(new FileReader(path));
			do{
				String line = br.readLine();
				if(line == null)
					break;
				text.append(line);
				text.append(line_sep);
			}while(true);
			return text.toString();
		}catch(FileNotFoundException e){
		}catch(IOException e){
			log.error("Exception occur when reading " + path, e);
		}finally{
			if(br!=null){
				try{
					br.close();
				}catch(IOException e){}
			}
			text = null;
		}
		return null;
	}
	
	/**
	 * 更新缓存文本的内容
	 * @param type
	 * @param id
	 * @param text
	 */
	public static void updateTextContent(int type, long id, String text){
		if(text==null)
			return;
		String path = getFilePath(type, id);
		FileOutputStream fos = null;
		try{
			File f = new File(path);
			if(!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			fos = new FileOutputStream(f);
			fos.write(text.getBytes());
		}catch(IOException e){
			log.error("Exception occur when writing to " + path, e);
		}finally{
			if(fos!=null){
				try{
					fos.close();
				}catch(Exception e){}
			}
		}
	}
	
	/**
	 * 删除一个缓存文本
	 * @param type
	 * @param id
	 * @return
	 */
	public static boolean deleteTextContent(int type, long id){
		String path = getFilePath(type, id);
		return new File(path).delete();
	}
	
	/**
	 * 返回编号对应的文件路径
	 * @param type
	 * @param id
	 * @return
	 */
	protected static String getFilePath(int type, long id){
		StringBuffer sb = new StringBuffer(basePath);
		sb.append(type);
		sb.append(File.separator);
		sb.append(id/10000);
		sb.append(File.separator);
		sb.append(id);
		sb.append(".htm");
		return sb.toString();
	}
	
}
