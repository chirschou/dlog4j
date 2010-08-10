/*
 *  AlbumForm.java
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
package com.liusoft.dlog4j.formbean;

/**
 * 相簿表单
 * @author Winter Lau
 */
public class AlbumForm extends FormBean {
	
	protected int parent;
	
	protected String name;
	protected String desc;
	protected int type;
	protected int direction;	//插入的方向
	protected String verifyCode;//验证码
	
	//album_move.vm
	protected int fromAlbum;
	protected int toAlbum;

	private int catalog;		//内容类别
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public int getFromAlbum() {
		return fromAlbum;
	}
	public void setFromAlbum(int fromAlbum) {
		this.fromAlbum = fromAlbum;
	}
	public int getToAlbum() {
		return toAlbum;
	}
	public void setToAlbum(int toAlbum) {
		this.toAlbum = toAlbum;
	}
	public final int getCatalog() {
		return catalog;
	}
	public final void setCatalog(int catalog) {
		this.catalog = catalog;
	}
}
