/*
 *  DiaryBase.java
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
 */
package com.liusoft.dlog4j.base;

import java.util.Date;
import java.util.List;

import com.liusoft.dlog4j.beans.CatalogBean;
import com.liusoft.dlog4j.beans.MusicBean;

/**
 * 日记对象的基类
 * @author Winter Lau
 */
public abstract class _DiaryBase extends _ReadingBean {

	public final static int STATUS_PENDING	= 0x01;	//待审批的日记
	public final static int STATUS_REJECT	= 0x03; //被驳回的日记
	public final static int STATUS_HIDDEN 	= 0x04;	//隐藏的日记
	public final static int STATUS_DRAFT	= 0x05;	//草稿状态的日记

	public final static int MOOD_LEVEL_1 = 1;
	public final static int MOOD_LEVEL_2 = 2;
	public final static int MOOD_LEVEL_3 = 3;
	public final static int MOOD_LEVEL_4 = 4;
	public final static int MOOD_LEVEL_5 = 5;
	
	public final static int TYPE_ELITE = 0x01;	//精华日记(由编辑进行遴选)
	
	protected CatalogBean catalog;	//日记所属的分类

	protected String content;	//日记内容
	
	protected String author;	//作者名称
	protected String authorUrl;	//作者的主页
	protected int size;
	protected String refUrl;	//日记引用的地址
	protected String weather;	//当时的天气
	
	protected int moodLevel = MOOD_LEVEL_3;	//心情指数

	protected int trackBackCount;//被引用的次数
	protected int replyNotify = 0;	//当有新评论时候是否提醒

	protected int lock;
	protected int type;
	
	protected List trackbacks;

	protected MusicBean bgSound;//背景音乐

	public int getSize(){
		return size;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthorUrl() {
		return authorUrl;
	}
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	public CatalogBean getCatalog() {
		return catalog;
	}
	public void setCatalog(CatalogBean catalog) {
		this.catalog = catalog;
	}
	public int getMoodLevel() {
		return moodLevel;
	}
	public void setMoodLevel(int moodLevel) {
		this.moodLevel = moodLevel;
	}
	public String getRefUrl() {
		return refUrl;
	}
	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}
	public int getReplyNotify() {
		return replyNotify;
	}
	public void setReplyNotify(int replyNotify) {
		this.replyNotify = replyNotify;
	}
	public int getTrackBackCount() {
		return trackBackCount;
	}

	public void setTrackBackCount(int trackBackCount) {
		this.trackBackCount = trackBackCount;
	}
	
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public Date getWriteTime() {
		return super.getCreateTime();
	}
	public void setWriteTime(Date writeTime) {
		super.setCreateTime(writeTime);
	}
	
	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public MusicBean getBgSound() {
		return bgSound;
	}
	public void setBgSound(MusicBean bgSound) {
		this.bgSound = bgSound;
	}
	
	public List getTrackbacks() {
		return trackbacks;
	}
	
	public void setTrackbacks(List trackbacks) {
		this.trackbacks = trackbacks;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
