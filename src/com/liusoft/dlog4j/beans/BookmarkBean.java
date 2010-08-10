/*
 *  BookmarkBean.java
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

import javax.servlet.http.HttpServletRequest;

import com.liusoft.dlog4j.base._MultipleSiteEnabledBean;

/**
 * 书签对象
 * @author liudong
 */
public class BookmarkBean extends _MultipleSiteEnabledBean {

	public final static int TYPE_SITE	 = 0x05;
	
    protected UserBean owner;		//加此书签的用户
    
    protected String title;	//书签标题
	protected int parentId;	//评论对应的文章编号
	protected int parentType;
	protected String url;

    protected Date createTime;		//加此书签的时间

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public UserBean getOwner() {
		return owner;
	}

	public void setOwner(UserBean owner) {
		this.owner = owner;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getParentType() {
		return parentType;
	}

	public void setParentType(int parentType) {
		this.parentType = parentType;
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
        
	/**
	 * 返回用于显示该书签详细信息的URL地址
	 * FIXME: 链接写在这里实在不好，以后再想办法解决
	 * @param req
	 * @return
	 */
	public String url(HttpServletRequest req, String subDir){
		StringBuffer v_url = new StringBuffer(req.getContextPath());
		v_url.append('/');
		v_url.append(subDir);
		v_url.append('/');
		switch(this.parentType){
		case TYPE_DIARY:
			v_url.append("diary/showlog.vm?log_id=");
			v_url.append(parentId);
			break;
		case TYPE_PHOTO:
			v_url.append("photo/show.vm?pid=");
			v_url.append(parentId);
			break;
		case TYPE_MMEDIA:
			v_url.append("music/show.vm?mid=");
			v_url.append(parentId);
			break;
		case TYPE_BBS:
			v_url.append("bbs/topic.vm?tid=");
			v_url.append(parentId);
			break;
		case TYPE_SITE:
			StringBuffer s_url = new StringBuffer(req.getContextPath());
			s_url.append('/');
			s_url.append("?sid=");
			s_url.append(parentId);
			return s_url.toString();
		default:
			return this.url;
		}
		v_url.append("&amp;");
		v_url.append("sid=");
		v_url.append(this.getSite().getId());
		return v_url.toString();
	}

}
