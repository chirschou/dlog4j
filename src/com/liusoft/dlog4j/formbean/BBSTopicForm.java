/*
 *  BBSTopicForm.java
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
package com.liusoft.dlog4j.formbean;

import java.util.StringTokenizer;

/**
 * 论坛帖子表单
 * @author Winter Lau
 */
public class BBSTopicForm extends FormBean {

	protected String title;
	protected String content;
	protected int forum;		//所在论坛的编号
	protected int clientType;
	protected String searchKey;
	protected int bookmark;
	protected int notify;
	protected int top;
	protected int elite;
	
	/**
	 * 由于WML页面无法使用checkbox,因此使用select来替代
	 */
	protected String options;	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getForum() {
		return forum;
	}
	public void setForum(int forum) {
		this.forum = forum;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getBookmark() {
		return bookmark;
	}
	public void setBookmark(int bookmark) {
		this.bookmark = bookmark;
	}
	public int getClientType() {
		return clientType;
	}
	public void setClientType(int clientType) {
		this.clientType = clientType;
	}
	public int getNotify() {
		return notify;
	}
	public void setNotify(int notify) {
		this.notify = notify;
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	public int getElite() {
		return elite;
	}
	public void setElite(int elite) {
		this.elite = elite;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public final String getOptions() {
		return options;
	}
	public final void setOptions(String options) {
		this.options = options;
		StringTokenizer st = new StringTokenizer(options, ";");
		while(st.hasMoreElements()){
			String tk = st.nextToken();
			if("bookmark".equalsIgnoreCase(tk))
				this.setBookmark(1);
			else if("elite".equalsIgnoreCase(tk))
				this.setElite(1);
			else if("top".equalsIgnoreCase(tk))
				this.setTop(1);
		}
	}
	
}
