/*
 *  FCK_UploadManager.java
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
package com.liusoft.dlog4j.upload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * 用于协调FCKEditor编辑器的文件上传管理
 * 该类由FCKEditor_UploadServlet进行初始化
 * @author liudong
 */
public class FCK_UploadManager {
	
	private static UploadFileHandler fileHandler;
	
	/**
	 * 初始化
	 * @param sConfig
	 * @param s_file_handler_class
	 * @throws ServletException
	 */
	public static void init(ServletConfig sConfig, String s_file_handler_class)
			throws ServletException
	{
		if (s_file_handler_class == null)
			fileHandler = new DiskUploadFileHandler();
		else {
			try {
				fileHandler = (UploadFileHandler) Class.forName(
						s_file_handler_class).newInstance();
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
		fileHandler.init(sConfig);

	}

	public static void destroy(){
		if (fileHandler != null) {
			fileHandler.destroy();
			fileHandler = null;
		}
	}

	/**
	 * 返回用于处理上传后的类实例
	 * @return
	 */
	public final static UploadFileHandler getUploadHandler(){
		return fileHandler;
	}

}
