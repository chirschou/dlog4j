/*
 *  PhotoSaver.java
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
package com.liusoft.dlog4j.photo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.struts.upload.FormFile;

import com.liusoft.dlog4j.HttpContext;

/**
 * 用于保存上传后的图片的接口
 * @author Winter Lau
 */
public interface PhotoSaver {

	public final static int PREVIEW_WIDTH = 75;
	public final static int PREVIEW_HEIGHT = 75;
	
	public final static int MAX_WIDTH = 1024;
	public final static int MAX_HEIGHT = 768;
	
	public final static String KEY_PHOTO_SAVE_PATH = "photo_base_path";
	public final static String KEY_PHOTO_SAVE_URI = "photo_base_uri";
	
	/**
	 * 保存照片
	 * @param context
	 * @param imgForm
	 * @param autoRotate
	 * @return
	 * @throws IOException
	 */
	public Photo save(HttpContext context, FormFile imgForm, boolean autoRotate) throws IOException;
	
	/**
	 * 删除照片
	 * @param context
	 * @param imgURL
	 * @return
	 * @throws IOException
	 */
	public boolean delete(HttpContext context, String imgURL) throws IOException;
	
	/**
	 * 读取照片文件流
	 * @param imgURL
	 * @return
	 * @throws IOException
	 */
	public InputStream read(HttpContext ctx, String imgURL) throws IOException;
	
	/**
	 * 准备写入照片文件
	 * @param imgURL
	 * @return
	 * @throws IOException
	 */
	public OutputStream write(HttpContext ctx, String imgURL) throws IOException;

}
