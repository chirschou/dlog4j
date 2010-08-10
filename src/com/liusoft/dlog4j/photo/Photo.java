/*
 *  Photo.java
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

import java.io.File;

import com.liusoft.dlog4j.base.PhotoInfo;

/**
 * 图片经过PhotoSaver接口保存后返回的基本信息
 * @author Winter Lau
 */
public class Photo extends PhotoInfo{

	protected String fileName;
	protected String imageURL;
	protected String previewURL;
	
	private int orientation;

	public String getName(){
		int idx = fileName.lastIndexOf('.');
		int idx2 = fileName.lastIndexOf(File.separator);
		if(idx2 == -1)
			idx2 = fileName.lastIndexOf('/');
		try{
			return fileName.substring(idx2+1, idx);
		}catch(Exception e){}
		return fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getPreviewURL() {
		return previewURL;
	}

	public void setPreviewURL(String previewURL) {
		this.previewURL = previewURL;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	
}
