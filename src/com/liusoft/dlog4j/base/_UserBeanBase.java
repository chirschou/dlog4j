/*
 *  _UserBeanBase.java
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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 用户资料类的基类
 * @see com.liusoft.dlog4j.SessionUserObject
 * @author liudong
 */
public abstract class _UserBeanBase extends _BeanBase {

	public final static int ROLE_COMMON = 0x00;	  //普通用户
	public final static int ROLE_INSPECTOR = 0x01;//网站内容巡视者
	public final static int ROLE_ADMINISTRATOR = 0x02;//系统管理员

	public final static int SEX_UNKNOWN = 0x00;
	public final static int SEX_MALE = 0x01;
	public final static int SEX_FEMALE = 0x02;
	
	private int age = -1;
	
	private String name;		//用户登录名
	private String nickname;	//用户昵称
	private int sex;
	private Date birth;
	
	private ContactInfo contactInfo;
	private CountInfo count;	
	
	private String portrait;	//头像
	private int role = ROLE_COMMON;			//角色
	
	private String resume;	//个人宣言
	
	private Timestamp regTime;	//帐号注册时间
	private Timestamp lastTime;	//最后一次登录的时间
	private String lastAddr;	//第一次注册的IP地址或者是最后一次登录的IP地址
	
	private int status;		//帐号状态
	private int level = 1;
	private int keepDays;		//登录资料的有效时间,单位:天
	
	private int ownSiteId;	//该用户所拥有的个人网记网编号
	
	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
		if(birth!=null){
			Calendar cal = Calendar.getInstance();
			int cur_year = cal.get(Calendar.YEAR);
			cal.setTime(birth);
			int the_year = cal.get(Calendar.YEAR);
			age = cur_year - the_year;
		}
		else
			age = -1;
	}

	public ContactInfo getContactInfo() {
		if(contactInfo==null)
			contactInfo = new ContactInfo();
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	public CountInfo getCount() {
		if(count==null)
			count = new CountInfo();
		return count;
	}

	public void setCount(CountInfo count) {
		this.count = count;
	}

	public int getKeepDays() {
		return keepDays;
	}

	public void setKeepDays(int keepDays) {
		this.keepDays = keepDays;
	}

	public String getLastAddr() {
		return lastAddr;
	}

	public void setLastAddr(String lastAddr) {
		this.lastAddr = lastAddr;
	}

	public Timestamp getLastTime() {
		return lastTime;
	}

	public void setLastTime(Timestamp lastTime) {
		this.lastTime = lastTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Timestamp getRegTime() {
		return regTime;
	}

	public void setRegTime(Timestamp regTime) {
		this.regTime = regTime;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOwnSiteId() {
		return ownSiteId;
	}

	public void setOwnSiteId(int ownSiteId) {
		this.ownSiteId = ownSiteId;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
	
	public int getAge(){
		return age;		
	}

	public String getAddress() {
		return getContactInfo().getAddress();
	}

	public String getCity() {
		return getContactInfo().getCity();
	}

	public String getCompany() {
		return getContactInfo().getCompany();
	}

	public String getEmail() {
		return getContactInfo().getEmail();
	}

	public String getFax() {
		return getContactInfo().getFax();
	}

	public String getHomePage() {
		return getContactInfo().getHomePage();
	}

	public String getIndustry() {
		return getContactInfo().getIndustry();
	}

	public String getJob() {
		return getContactInfo().getJob();
	}

	public String getMobile() {
		return getContactInfo().getMobile();
	}

	public String getMsn() {
		return getContactInfo().getMsn();
	}

	public String getNation() {
		return getContactInfo().getNation();
	}

	public String getProvince() {
		return getContactInfo().getProvince();
	}

	public String getQq() {
		return getContactInfo().getQq();
	}

	public String getTel() {
		return getContactInfo().getTel();
	}

	public String getZip() {
		return getContactInfo().getZip();
	}

	public void setAddress(String address) {
		getContactInfo().setAddress(address);
	}

	public void setCity(String city) {
		getContactInfo().setCity(city);
	}

	public void setCompany(String company) {
		getContactInfo().setCompany(company);
	}

	public void setEmail(String email) {
		getContactInfo().setEmail(email);
	}

	public void setFax(String fax) {
		getContactInfo().setFax(fax);
	}

	public void setHomePage(String homePage) {
		getContactInfo().setHomePage(homePage);
	}

	public void setIndustry(String industry) {
		getContactInfo().setIndustry(industry);
	}

	public void setJob(String job) {
		getContactInfo().setJob(job);
	}

	public void setMobile(String mobile) {
		getContactInfo().setMobile(mobile);
	}

	public void setMsn(String msn) {
		getContactInfo().setMsn(msn);
	}

	public void setNation(String nation) {
		getContactInfo().setNation(nation);
	}

	public void setProvince(String province) {
		getContactInfo().setProvince(province);
	}

	public void setQq(String qq) {
		getContactInfo().setQq(qq);
	}

	public void setTel(String tel) {
		getContactInfo().setTel(tel);
	}

	public void setZip(String zip) {
		getContactInfo().setZip(zip);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
