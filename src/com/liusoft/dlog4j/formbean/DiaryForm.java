/*
 *  DiaryForm.java
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
package com.liusoft.dlog4j.formbean;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import com.liusoft.dlog4j.base._BeanBase;
import com.liusoft.dlog4j.beans.DiaryBean;

/**
 * 日记表单
 * @author liudong
 */
public class DiaryForm extends FormBean {

	protected int catalogId;	//所在分类
	protected String catalog;
	protected int bgSound;		//背景音乐

	protected String author;	//作者名称
	protected String authorUrl;	//作者的主页
	protected String title;		//日记标题
	protected String content;	//日记内容
	protected String refUrl;	//日记引用的地址
	protected String weather;	//当时的天气
	
	protected int moodLevel = DiaryBean.MOOD_LEVEL_3;	//心情指数

	protected String tags;	//关键字,Tag,Keyword
	protected int clientType = 0;	//写此日记的终端类别
	protected int notify = 0;	//当有新评论时候是否提醒
	protected int bookmark = 0;	//是否标志为书签
	
	protected Date writeDate;	//自定义写日记的日期
	protected int writeHour;	//自定义写日记的小时
	protected int writeMinute;	//自定义写日记的分钟
	
	protected int status = _BeanBase.STATUS_NORMAL;

	public int getBookmark() {
		return bookmark;
	}

	public void setBookmark(int bookmark) {
		this.bookmark = bookmark;
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

	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getMoodLevel() {
		return moodLevel;
	}

	public void setMoodLevel(int moodLevel) {
		this.moodLevel = moodLevel;
	}

	public int getNotify() {
		return notify;
	}

	public void setNotify(int notify) {
		this.notify = notify;
	}

	public String getRefUrl() {
		return refUrl;
	}

	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public int getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(int catalogId) {
		this.catalogId = catalogId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getBgSound() {
		return bgSound;
	}

	public void setBgSound(int bgSound) {
		this.bgSound = bgSound;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public Date getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
	}

	public int getWriteHour() {
		return writeHour;
	}

	public void setWriteHour(int writeHour) {
		this.writeHour = writeHour;
	}

	public int getWriteMinute() {
		return writeMinute;
	}

	public void setWriteMinute(int writeMinute) {
		this.writeMinute = writeMinute;
	}
	
	public Time getWriteTime(){
		if(writeHour<0 || writeHour > 23)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, writeHour);
		if(writeMinute >=0 && writeMinute <60)
			cal.set(Calendar.MINUTE, writeMinute);
		return new Time(cal.getTime().getTime());
	}

}
