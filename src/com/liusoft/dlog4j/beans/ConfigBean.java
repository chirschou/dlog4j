/*
 *  ConfigBean.java
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
package com.liusoft.dlog4j.beans;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.liusoft.dlog4j.base._BeanBase;

/**
 * 系统配置信息
 * @author Winter Lau
 * @database_independence 可单独存储,不依赖于其他表
 */
public class ConfigBean extends _BeanBase implements Serializable {
	
	protected int siteId;
	protected String name;
	protected int intValue;
	protected String stringValue;
	protected Date dateValue;
	protected Time timeValue;
	protected Timestamp timestampValue;
	
	protected Timestamp lastUpdate;
	
	public int intValue(){
		return intValue;
	}	
	public String stringValue(){
		return stringValue;
	}	
	public Date dateValue(){
		return dateValue;
	}	
	public Time timeValue(){
		return timeValue;
	}	
	public Timestamp timestampValue(){
		return timestampValue;
	}	
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public Timestamp getTimestampValue() {
		return timestampValue;
	}
	public void setTimestampValue(Timestamp timestampValue) {
		this.timestampValue = timestampValue;
	}
	public Time getTimeValue() {
		return timeValue;
	}
	public void setTimeValue(Time timeValue) {
		this.timeValue = timeValue;
	}
	public Timestamp getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	
}
