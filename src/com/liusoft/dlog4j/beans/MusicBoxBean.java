/*
 *  MusicBoxBean.java
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
package com.liusoft.dlog4j.beans;

import java.util.Date;
import java.util.List;

import com.liusoft.dlog4j.base.Orderable;
import com.liusoft.dlog4j.base._MultipleSiteEnabledBean;

/**
 * 音乐辑
 * @author Winter Lau
 */
public class MusicBoxBean extends _MultipleSiteEnabledBean implements Orderable{

	protected String name;
	protected String desc;
	protected Date createTime;
	protected int sortOrder;
	protected int musicCount;
	
	protected List songs;
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public List getSongs() {
		return songs;
	}
	public void setSongs(List songs) {
		this.songs = songs;
	}
	public int getMusicCount() {
		return musicCount;
	}
	public void setMusicCount(int musicCount) {
		this.musicCount = musicCount;
	}
	public int incMusicCount(int count){
		this.musicCount += count;
		if(this.musicCount < 0)
			this.musicCount = 0;
		return this.musicCount;
	}
	
}
