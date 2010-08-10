/*
 *  UploadFileHandler.java
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

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liusoft.dlog4j.base.FckUploadFileBeanBase;

/**
 * 上传后的文件处理接口
 * @author Winter Lau
 */
public interface UploadFileHandler {
	
	/**
	 * 初始化文件处理接口
	 * @param config
	 */
	public void init(ServletConfig config);
	
	/**
	 * 保存上传后的文件并返回该文件对应的URL地址
	 * 子类必须设置图片的存储路径,例如
	 * req.setAttribute("file.path", newFile);
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public String save(HttpServletRequest req, HttpServletResponse res, File file) throws Exception;
	
	/**
	 * 删除一个上传文件的信息
	 * @param fbean
	 * @return
	 * @throws Exception
	 */
	public boolean remove(FckUploadFileBeanBase fbean) throws Exception;
	
	/**
	 * 退出
	 */
	public void destroy();

}
