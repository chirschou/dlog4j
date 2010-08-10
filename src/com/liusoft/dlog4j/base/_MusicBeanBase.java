/*
 *  _MusicBeanBase.java
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

import java.io.Serializable;
import java.util.Date;

import com.liusoft.dlog4j.beans.MusicBoxBean;
import com.liusoft.dlog4j.beans.UserBean;

/**
 * 音乐bean的基类
 * @author Winter Lau
 */
public class _MusicBeanBase extends _MultipleSiteEnabledBean implements Serializable {

	public final static int STATUS_RECOMMEND = 0x10;
	
	public final static int MEDIA_TYPE_AUDIO = 0x00;
	public final static int MEDIA_TYPE_VEDIO = 0x01;
	public final static int MEDIA_TYPE_FLASH = 0x02;

	protected MusicBoxBean musicBox;
	protected UserBean introducer;	//推荐者
	
	protected String title;		//歌曲名
	protected String album;		//专辑名
	protected String singer;	//歌手
	protected String url;		//音乐文件链接(用于试听)
	protected Date createTime;
	protected int viewCount;
	protected int type;			//影音类别
	protected int status;

	protected String word;		//歌词

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public MusicBoxBean getMusicBox() {
		return musicBox;
	}
	public void setMusicBox(MusicBoxBean musicBox) {
		this.musicBox = musicBox;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public UserBean getIntroducer() {
		return introducer;
	}
	public void setIntroducer(UserBean introducer) {
		this.introducer = introducer;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
}
