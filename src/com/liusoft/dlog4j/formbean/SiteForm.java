/*
 *  SiteForm.java
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

import org.apache.struts.upload.FormFile;

/**
 * 网站信息表单
 * @author Winter Lau
 */
public class SiteForm extends FormBean {

	protected String uniqueName;
	protected String url;
	protected String friendlyName;
	protected String title;
	protected String detail;
	protected String icpNumber;		//ICP证编号
	protected FormFile logoFile;		//网站LOGO
	protected String cssFile;		//网站选择的样式单
	protected String layoutFile;	//网站的布局控制文件
	protected String language;		//网站所使用的语言
	protected int type;
	
	//html/sitemgr/funcs.vm
	protected int statusDiary;
	protected int statusPhoto;
	protected int statusMusic;
	protected int statusForum;
	protected int statusGuestbook;
	
	protected String diaryName;
	protected String photoName;
	protected String musicName;
	protected String forumName;
	protected String guestbookName;
	
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCssFile() {
		return cssFile;
	}
	public void setCssFile(String cssFile) {
		this.cssFile = cssFile;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getFriendlyName() {
		return friendlyName;
	}
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	public String getIcpNumber() {
		return icpNumber;
	}
	public void setIcpNumber(String icpNumber) {
		this.icpNumber = icpNumber;
	}
	public String getLayoutFile() {
		return layoutFile;
	}
	public void setLayoutFile(String layoutFile) {
		this.layoutFile = layoutFile;
	}
	public FormFile getLogoFile() {
		return logoFile;
	}
	public void setLogoFile(FormFile logoFile) {
		this.logoFile = logoFile;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUniqueName() {
		return uniqueName;
	}
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDiaryName() {
		return diaryName;
	}
	public void setDiaryName(String diaryName) {
		this.diaryName = diaryName;
	}
	public String getForumName() {
		return forumName;
	}
	public void setForumName(String forumName) {
		this.forumName = forumName;
	}
	public String getGuestbookName() {
		return guestbookName;
	}
	public void setGuestbookName(String guestbookName) {
		this.guestbookName = guestbookName;
	}
	public String getMusicName() {
		return musicName;
	}
	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}
	public String getPhotoName() {
		return photoName;
	}
	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}
	public int getStatusDiary() {
		return statusDiary;
	}
	public void setStatusDiary(int statusDiary) {
		this.statusDiary = statusDiary;
	}
	public int getStatusForum() {
		return statusForum;
	}
	public void setStatusForum(int statusForum) {
		this.statusForum = statusForum;
	}
	public int getStatusGuestbook() {
		return statusGuestbook;
	}
	public void setStatusGuestbook(int statusGuestbook) {
		this.statusGuestbook = statusGuestbook;
	}
	public int getStatusMusic() {
		return statusMusic;
	}
	public void setStatusMusic(int statusMusic) {
		this.statusMusic = statusMusic;
	}
	public int getStatusPhoto() {
		return statusPhoto;
	}
	public void setStatusPhoto(int statusPhoto) {
		this.statusPhoto = statusPhoto;
	}	
	
}
