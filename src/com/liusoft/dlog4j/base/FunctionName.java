/*
 * 版权所有: 摩网信息科技有限公司 2005
 * 项目：DLOG4J_V3
 * 所在包：com.liusoft.dlog4j.beans
 * 文件名：FunctionName.java
 * 创建时间：2005-12-9
 * 创建者：Winter Lau
 */
package com.liusoft.dlog4j.base;

import java.io.Serializable;

/**
 * 网站自定义功能名
 * @author Winter Lau
 */
public class FunctionName implements Serializable {
	
	protected String diary;
	protected String photo;
	protected String music;
	protected String forum;
	protected String guestbook;
	
	public String getDiary() {
		return diary;
	}
	public void setDiary(String diary) {
		this.diary = diary;
	}
	public String getGuestbook() {
		return guestbook;
	}
	public void setGuestbook(String guestbook) {
		this.guestbook = guestbook;
	}
	public String getMusic() {
		return music;
	}
	public void setMusic(String music) {
		this.music = music;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getForum() {
		return forum;
	}
	public void setForum(String forum) {
		this.forum = forum;
	}

}
