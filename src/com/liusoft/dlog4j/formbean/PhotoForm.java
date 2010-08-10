/*
 *  PhotoForm.java
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
package com.liusoft.dlog4j.formbean;

import org.apache.struts.upload.FormFile;

/**
 * 照片表单
 * @author Winter Lau
 */
public class PhotoForm extends FormBean {

	protected int album;	//所在相簿
	
	protected String name;	//名称
	protected String desc;	//描述
	protected String keyword;	//标签
	
	protected FormFile image;//照片文件1
	protected FormFile image2;//照片文件2
	protected FormFile image3;//照片文件3
	protected FormFile image4;//照片文件4
	protected FormFile image5;//照片文件5
	
	protected int status;		//公开或者隐藏
	protected int cover;	//设置为封面
	protected int autoRotate = 1;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public FormFile getImage() {
		return image;
	}
	public void setImage(FormFile image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAlbum() {
		return album;
	}
	public void setAlbum(int album) {
		this.album = album;
	}
	public int getCover() {
		return cover;
	}
	public void setCover(int cover) {
		this.cover = cover;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getAutoRotate() {
		return autoRotate;
	}
	public void setAutoRotate(int autoRotate) {
		this.autoRotate = autoRotate;
	}
	public FormFile getImage2() {
		return image2;
	}
	public void setImage2(FormFile image2) {
		this.image2 = image2;
	}
	public FormFile getImage3() {
		return image3;
	}
	public void setImage3(FormFile image3) {
		this.image3 = image3;
	}
	public FormFile getImage4() {
		return image4;
	}
	public void setImage4(FormFile image4) {
		this.image4 = image4;
	}
	public FormFile getImage5() {
		return image5;
	}
	public void setImage5(FormFile image5) {
		this.image5 = image5;
	}
	
}
