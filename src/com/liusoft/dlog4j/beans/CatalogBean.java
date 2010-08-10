/*
 *  CatalogBean.java
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
 * 文章分类对象
 * @author liudong
 */
public class CatalogBean extends _MultipleSiteEnabledBean implements Orderable{

	public final static int TYPE_OWNER 		= 0x00;		//只有日记所有者才可以看
	public final static int TYPE_GENERAL 	= 0x01;		//一般的日记分类
	public final static int TYPE_COMMON 	= 0x02;		//只要是角色为ROLE_FRIEND的都可以发表日记
	public final static int TYPE_FREE		= 0x04;		//自由分类,类似论坛,注册的用户都可以发表日记
	
	protected TypeBean catalog;	//所属的分组，例如 生活、技术、摄影等等
	
	protected String name;		//分类名
	protected String detail;	//分类详细描述
	protected Date createTime;	//
	protected int sortOrder;	//分类的排序
	protected int type;			//分类类型，具体取值有前面的常量锁定
	protected String verifyCode;
	protected int articleCount;	//文章数
	
	protected List diaries;
	
	public CatalogBean(){}
	
	public CatalogBean(int id){
		super.setId(id);
	}
	
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public int getArticleCount() {
		return articleCount;
	}
	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}
	public int incArticleCount(int count){
		this.articleCount += count;
		if(this.articleCount<0)
			this.articleCount = 0;
		return this.articleCount;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
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
	
	public String getTypeDescKey(){
		return "catalog.type." + type;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public TypeBean getCatalog() {
		return catalog;
	}
	public void setCatalog(TypeBean catalog) {
		this.catalog = catalog;
	}
	public List getDiaries() {
		return diaries;
	}
	public void setDiaries(List diaries) {
		this.diaries = diaries;
	}
	
}
