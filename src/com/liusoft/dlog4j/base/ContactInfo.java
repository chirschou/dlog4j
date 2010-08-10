/*
 *  ContactInfo.java
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
package com.liusoft.dlog4j.base;

import java.io.Serializable;

/**
 * 用户的联系信息
 * @author Winter Lau
 */
public class ContactInfo implements Serializable {

	protected String email;		//电子邮件
	protected String homePage;	//个人网站地址
	protected String qq;		//QQ号码
	protected String msn;		//MSN帐号
	protected String mobile;	//手机号码
	
	/** 以下属性暂时没用 **/
	protected String nation;	//国家
	protected String province;	//省份
	protected String city;		//所在城市
	protected String tel;		//电话
	protected String industry;	//行业
	protected String company;	//公司名
	protected String fax;		//传真
	protected String job;		//职位
	protected String address;	//联系地址
	protected String zip;		//邮编
	protected String name;		//联系人姓名
	
	public Object clone(){
		ContactInfo obj = new ContactInfo();
		obj.setEmail(email);
		obj.setHomePage(homePage);
		obj.setQq(qq);
		obj.setMsn(msn);
		obj.setMobile(mobile);
		obj.setNation(nation);
		obj.setProvince(province);
		obj.setCity(city);
		obj.setTel(tel);
		obj.setIndustry(industry);
		obj.setCompany(company);
		obj.setFax(fax);
		obj.setJob(job);
		obj.setAddress(address);
		obj.setZip(zip);
		obj.setName(name);
		return obj;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHomePage() {
		return homePage;
	}
	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMsn() {
		return msn;
	}
	public void setMsn(String msn) {
		this.msn = msn;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}
	
}
