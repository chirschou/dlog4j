/*
 *  DSJUAN_VelocityTool.java
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
 *  2006-8-13
 */
package com.liusoft.dlog4j.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.liusoft.dlog4j.UserLoginManager;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;


/**
 * 长沙新诺斯丢手绢网站集成
 * @author liudong
 */
public class DSJUAN_Proxy extends DAO {

	/**
	 * 返回当前登录用户名
	 * @return
	 */
	public String get_login_username(HttpServletRequest req, HttpServletResponse res){
		Cookie cookie = RequestUtils.getCookie(req, "DSJ_UID");
		if(cookie == null) return null;
		String value = cookie.getValue();
		int idx = value.lastIndexOf('+');
		if(idx < 1) return null;
		String username = value.substring(0, idx);
		String pwdCode = value.substring(idx + 1);
		//根据username查询其密码
		String password = get_password(username);
		if(password==null) return null;
		if(!String.valueOf(password.hashCode()).equals(pwdCode)) return null;
		return username;
	}
	
	/**
	 * 得到交友用户所开通的博客编号
	 * @param username
	 * @return
	 */
	public int get_site_of_username(String username){
		if(StringUtils.isBlank(username)) return -1;
		Session ssn = getSession();
		String sql = "SELECT dlog_site_id FROM dlog_user_site WHERE UserID=?";
		SQLQuery q = ssn.createSQLQuery(sql);
		q.setString(0, username);
		Integer site = (Integer)q.uniqueResult();
		return (site==null)?-1:site.intValue();
	}
	
	/**
	 * 自动创建一个博客网站，并返回该网站的编号 
	 * @param username
	 * @return
	 * @throws Exception 
	 */
	public int auto_create_site(HttpServletRequest req, HttpServletResponse res, String username) throws Exception{
		//创建用户
		UserBean ubean = auto_create_user(username);
		//创建网站
		SiteBean sbean = auto_create_site(ubean);
		//写入绑定关系
		bind_user_site(username, sbean.getId());
		//执行登录过程
		UserLoginManager.loginUser(req, res, ubean, 365);
		return sbean.getId();
	}
	
	/**
	 * 获取用户在交友网上的详细资料
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public HashMap get_user(String username) throws SQLException{
		HashMap map = new HashMap();
		Connection conn = getSession().connection();
		//读取用户基本信息
		String sql1 = "SELECT * FROM userbasicinfo WHERE UserID=?";
		PreparedStatement ps1 = conn.prepareStatement(sql1);
		ps1.setString(1, username);
		if(ps1.execute()){			
			ResultSet rs1 = ps1.getResultSet();
			if(rs1.next()){
				try{
					ResultSetMetaData rsmd = rs1.getMetaData();
					int cc = rsmd.getColumnCount();
					for(int i=1;i<=cc;i++){
						map.put(rsmd.getColumnName(i), rs1.getObject(i));
					}
				}finally{
					rs1.close();
				}
			}
		}
		//读取用户扩展信息
		String sql2 = "SELECT * FROM userdetailinfo WHERE UserID=?";
		PreparedStatement ps2 = conn.prepareStatement(sql2);
		ps2.setString(1, username);
		if(ps2.execute()){			
			ResultSet rs2 = ps2.getResultSet();
			if(rs2.next()){
				try{
					ResultSetMetaData rsmd = rs2.getMetaData();
					int cc = rsmd.getColumnCount();
					for(int i=1;i<=cc;i++){
						map.put(rsmd.getColumnName(i), rs2.getObject(i));
					}
				}finally{
					rs2.close();
				}
			}
		}
		return map;
	}
	
	/**
	 * 绑定用户和个人空间
	 * @param username
	 * @param site_id
	 * @throws Exception
	 */
	private void bind_user_site(String username, int site_id) throws Exception{
		String sql = "INSERT INTO dlog_user_site(UserID,dlog_site_id,create_time) VALUES(?,?,?)";
		Connection conn = getSession().connection();
		try{
			beginTransaction();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setInt(2, site_id);
			ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			ps.executeUpdate();
			commit();			
		}catch(Exception e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 自动开通个人空间
	 * @param ubean
	 * @return
	 */
	private SiteBean auto_create_site(UserBean ubean){
		SiteBean sbean = new SiteBean();
		sbean.setCreateTime(new Date());
		sbean.setUniqueName(ubean.getName());
		sbean.setFriendlyName(ubean.getName()+"的个人空间");
		sbean.setOwner(ubean);
		SiteDAO.createSite(sbean);
		return sbean;
	}
	
	/**
	 * 自动创建dlog用户
	 * @param username
	 * @return
	 */
	private UserBean auto_create_user(String username){
		UserBean ubean = new UserBean();
		ubean.setName(username);
		ubean.setNickname(username);
		ubean.setPassword("dlog_csxns");		
		UserDAO.createUser(ubean);
		return ubean;
	}
	
	/**
	 * 得到某个账户在丢手绢交友网站的密码字符串
	 * @param username
	 * @return
	 */
	private String get_password(String username){
		Session ssn = getSession();
		String sql = "SELECT userPassword FROM userbasicinfo WHERE UserID=?";
		SQLQuery q = ssn.createSQLQuery(sql);
		q.setString(0, username);
		return (String)q.uniqueResult();
	}
	
}
