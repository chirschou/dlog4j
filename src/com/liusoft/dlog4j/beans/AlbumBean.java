/*
 *  AlbumBean.java
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
package com.liusoft.dlog4j.beans;

import java.util.Date;
import java.util.List;

import com.liusoft.dlog4j.base.Orderable;
import com.liusoft.dlog4j.base._MultipleSiteEnabledBean;

/**
 * 相册
 * @author Winter Lau
 */
public class AlbumBean extends _MultipleSiteEnabledBean implements Orderable{

	public final static int TYPE_PUBLIC  = 0x00;	//完全公开
	public final static int TYPE_VERIFIED = 0x01;	//需要密码验证才能访问
	public final static int TYPE_PRIVATE = 0x02;	//完全私有
	
	protected AlbumBean parent;
	protected TypeBean catalog;	//所属分类
	
	protected PhotoBean cover;
	
	protected String name;
	protected String desc;
	protected int photoCount;
	protected int type;
	protected String verifyCode;
	protected int sortOrder;
	protected Date createTime;
	
	protected List<AlbumBean> childs;
	protected List<PhotoOutlineBean> photos;
	
	public AlbumBean(){}
	
	public AlbumBean(int id){
		super.setId(id);
	}
	
	public AlbumBean album(int aid){
		if(getId() == aid)
			return this;
		for(int i=0;childs!=null&&i<childs.size();i++){
			AlbumBean ab = ((AlbumBean)childs.get(i)).album(aid);
			if(ab!=null)
				return ab;
		}
		return null;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPhotoCount() {
		return photoCount;
	}

	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}
	
	public int incPhotoCount(int count){
		this.photoCount += count;
		if(this.photoCount<0)
			this.photoCount = 0;
		return this.photoCount;
	}

	public List<AlbumBean> getChilds() {
		return childs;
	}

	public void setChilds(List<AlbumBean> childs) {
		this.childs = childs;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AlbumBean getParent() {
		return parent;
	}

	public void setParent(AlbumBean parent) {
		this.parent = parent;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public List<PhotoOutlineBean> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PhotoOutlineBean> photos) {
		this.photos = photos;
	}

	public TypeBean getCatalog() {
		return catalog;
	}

	public void setCatalog(TypeBean catalog) {
		this.catalog = catalog;
	}

	public PhotoBean getCover() {
		return cover;
	}

	public void setCover(PhotoBean cover) {
		this.cover = cover;
	}
}
