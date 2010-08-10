/*
 *  _PhotoBase.java
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
package com.liusoft.dlog4j.base;

import java.util.List;

import com.liusoft.dlog4j.beans.AlbumBean;
import com.liusoft.dlog4j.beans.UserBean;

/**
 * base of PhotoBean
 * @author Winter Lau
 */
public class _PhotoBase extends _ReadingBean {

	/**
	 * 超过十人投诉的照片将被置为敏感信息
	 */
	public final static int STATUS_SENSITIVITY = 0xFF; // 敏感信息，例如黄色图片等
	public final static int STATUS_PRIVATE = 0x02;

	protected AlbumBean album;
	protected String fileName;
	protected String imageURL;
	protected String previewURL;
	protected PhotoInfo photoInfo;
	protected int year;
	protected int month;
	protected int date;
	protected List trackbacks;
	
	protected int lock;
	protected int type;

	protected String desc;
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List getTrackbacks() {
		return trackbacks;
	}

	public void setTrackbacks(List trackbacks) {
		this.trackbacks = trackbacks;
	}

	public AlbumBean getAlbum() {
		return album;
	}

	public void setAlbum(AlbumBean album) {
		this.album = album;
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

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public PhotoInfo getPhotoInfo() {
		if(photoInfo==null)
			photoInfo = new PhotoInfo();
		return photoInfo;
	}

	public void setPhotoInfo(PhotoInfo photoInfo) {
		this.photoInfo = photoInfo;
	}

	public UserBean getUser() {
		return super.getOwner();
	}

	public void setUser(UserBean user) {
		super.setOwner(user);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return super.getTitle();
	}

	public void setName(String name) {
		super.setTitle(name);
	}

}
