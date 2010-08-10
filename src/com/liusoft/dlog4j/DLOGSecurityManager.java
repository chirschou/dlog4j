/*
 *  SiteAction.java
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
package com.liusoft.dlog4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

/**
 * DLOG在安全方面的一些处理方法 
 * 敏感词汇表：/WEB-INF/conf/illegal_glossary.dat
 * 
 * @author Winter Lau
 */
public class DLOGSecurityManager {

	/**
	 * 初始化
	 * @param sc
	 * @throws IOException
	 * 
	 * @see com.liusoft.dlog4j.servlet.DLOG_ActionServlet#init()
	 */
	public static void init(ServletContext sc) throws IOException {
		IllegalGlossary.init(sc);
	}
	
	public static void destroy(){
		IllegalGlossary.destroy();
	}
	
	/**
	 * 敏感字汇
	 * @author Winter Lau
	 */
	public static class IllegalGlossary {

		private final static String file_glossary = "/WEB-INF/conf/illegal_glossary.dat";
		
		private static List<String> glossary = null;
		
		public static void init(ServletContext sc) throws IOException {
			glossary = new ArrayList<String>(1000);
			if(sc!=null)
				loadIllegalGlossary(sc);
		}

		public static void destroy(){
			if(glossary!=null)
				glossary.clear();
		}
		
		/**
		 * 加载敏感词汇表
		 * @param sc
		 * @throws IOException 
		 */
		private synchronized static void loadIllegalGlossary(ServletContext sc) throws IOException {
			InputStream in = sc.getResourceAsStream(file_glossary);
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new InputStreamReader(in));
				do{
					String line = reader.readLine();
					if(line==null)
						break;
					glossary.add(line.trim());
				}while(true);
			}finally{
				in.close();
			}
		}

		/**
		 * 自动将敏感词汇用XXX替换
		 * 
		 * @param content
		 * @return
		 */
		public static String autoGlossaryFiltrate(String content) {
			if(StringUtils.isEmpty(content))
				return content;
			for (int i = 0; i < glossary.size(); i++) {
				String word = glossary.get(i);
				content = StringUtils.replace(content, word, StringUtils
						.repeat("X", word.length()));
			}
			return content;
		}
		
		/**
		 * 判断是否存在非法内容
		 * @param content
		 * @return
		 */
		public static boolean existIllegalWord(String content){
			if(StringUtils.isEmpty(content))
				return false;
			for (int i = 0; i < glossary.size(); i++) {
				String word = (String) glossary.get(i);
				if(content.indexOf(word)>=0)
					return true;
			}
			return false;
		}
		
		/**
		 * 删除内容中存在的关键字
		 * @param content
		 * @return
		 */
		public static String deleteIllegalWord(String content){
			if(StringUtils.isEmpty(content))
				return content;
			for (int i = 0; i < glossary.size(); i++) {
				String word = (String) glossary.get(i);
				content = StringUtils.remove(content, word);
			}
			return content;
		}
		
	}

	public static void main(String[] args) throws IOException{
		init(null);
		String text = "中华人民共和国国家主席毛泽东，我们叫他毛主席";
		System.out.println(IllegalGlossary.autoGlossaryFiltrate(text));
	}
}
