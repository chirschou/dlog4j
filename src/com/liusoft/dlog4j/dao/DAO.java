/*
 *  DAO.java
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
package com.liusoft.dlog4j.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.liusoft.dlog4j.db.HibernateUtils;

/**
 * 所有数据库访问接口的基类
 * 
 * 人之患，在于好为人师
 * 
 * @author liudong
 */
public abstract class DAO extends _DAOBase{

	public final static int MAX_TAG_COUNT = 5;//限制每篇文章的标签最多五个
	public final static int MAX_TAG_LENGTH = 20;//标签最大长度,字节

	/**
	 * 添加对象
	 * @param cbean
	 */
	public static void save(Object cbean){
		try{
			Session ssn = getSession();
			beginTransaction();
			ssn.save(cbean);
			commit();
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}

	/**
	 * 添加对象
	 * @param cbean
	 */
	public static void saveOrUpdate(Object cbean){
		try{
			Session ssn = getSession();
			beginTransaction();
			ssn.saveOrUpdate(cbean);
			commit();
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}

	/**
	 * 删除对象
	 * @param cbean
	 */
	public static void delete(Object cbean){
		try{
			Session ssn = getSession();
			beginTransaction();
			ssn.delete(cbean);
			commit();
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 根据主键删除某个对象
	 * @param objClass
	 * @param key
	 * @return
	 */
	protected static int delete(Class objClass, Serializable key){
		StringBuffer hql = new StringBuffer("DELETE FROM ");
		hql.append(objClass.getName());
		hql.append(" AS t WHERE t.id=?");
		return commitUpdate(hql.toString(), key);
	}

	/**
	 * 写脏数据到数据库
	 */
	public static void flush(){
		try{
			Session ssn = getSession();
			if(ssn.isDirty()){
				beginTransaction();
				ssn.flush();
				commit();
			}
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 根据主键加载对象
	 * @param beanClass
	 * @param ident
	 * @return
	 */
	protected static Object getBean(Class beanClass, int id){
		return getSession().get(beanClass, id);
	}

	/**
	 * 执行统计查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static Number executeStat(String hql, Object...args){
		return (Number)uniqueResult(hql, args);
	}

	/**
	 * 执行统计查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static int executeStatAsInt(String hql, Object...args){
		return (executeStat(hql, args)).intValue();
	}

	/**
	 * 执行统计查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static long executeStatAsLong(String hql, Object...args){
		return (executeStat(hql, args)).longValue();
	}

	/**
	 * 执行普通查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static List findAll(String hql, Object...args){
		return executeQuery(hql, 1, -1, args);
	}
	
	/**
	 * 执行普通查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static List executeQuery(String hql, int fromIdx, int fetchCount, Object...args){
		Session ssn = getSession();
		Query q = ssn.createQuery(hql);
		for(int i=0;i<args.length;i++){
			q.setParameter(i, args[i]);
		}
		if(fromIdx > 0)
			q.setFirstResult(fromIdx);
		if(fetchCount > 0)
			q.setMaxResults(fetchCount);
		return q.list();
	}
	
	/**
	 * 执行更新语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static int executeUpdate(String hql, Object...args){
		Session ssn = getSession();
		Query q = ssn.createQuery(hql);
		for(int i=0;i<args.length;i++){
			q.setParameter(i, args[i]);
		}
		return q.executeUpdate();
	}

	/**
	 * 执行更新语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static int commitUpdate(String hql, Object...args){
		try{
			Session ssn = getSession();
			beginTransaction();
			Query q = ssn.createQuery(hql);
			for(int i=0;i<args.length;i++){
				q.setParameter(i, args[i]);
			}
			int er = q.executeUpdate();
			commit();
			return er;
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 执行返回单一结果的查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static Object uniqueResult(String hql, Object...args){
		Session ssn = getSession();
		Query q = ssn.createQuery(hql);
		for(int i=0;i<args.length;i++){
			q.setParameter(i, args[i]);
		}
		q.setMaxResults(1);
		return q.uniqueResult();
	}

	/**
	 * 执行统计查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static Number executeNamedStat(String hql, Object...args){
		return (Number)namedUniqueResult(hql, args);
	}

	/**
	 * 执行统计查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static int executeNamedStatAsInt(String hql, Object...args){
		return (executeNamedStat(hql, args)).intValue();
	}

	/**
	 * 执行统计查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static long executeNamedStatAsLong(String hql, Object...args){
		return (executeNamedStat(hql, args)).longValue();
	}

	/**
	 * 执行普通查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static List executeNamedQuery(String hql, int fromIdx, int fetchCount, Object...args){
		Session ssn = getSession();
		Query q = ssn.getNamedQuery(hql);
		for(int i=0;i<args.length;i++){
			q.setParameter(i, args[i]);
		}
		if(fromIdx > 0)
			q.setFirstResult(fromIdx);
		if(fetchCount > 0)
			q.setMaxResults(fetchCount);
		return q.list();
	}

	/**
	 * 执行普通查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static List findNamedAll(String hql, Object...args){
		return executeNamedQuery(hql, -1, -1, args);
	}
	
	/**
	 * 执行返回单一结果的查询语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static Object namedUniqueResult(String hql, Object...args){
		Session ssn = getSession();
		Query q = ssn.getNamedQuery(hql);
		for(int i=0;i<args.length;i++){
			q.setParameter(i, args[i]);
		}
		q.setMaxResults(1);
		return q.uniqueResult();
	}

	/**
	 * 执行更新语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static int executeNamedUpdate(String hql, Object...args){
		Session ssn = getSession();
		Query q = ssn.getNamedQuery(hql);
		for(int i=0;i<args.length;i++){
			q.setParameter(i, args[i]);
		}
		return q.executeUpdate();
	}

	/**
	 * 执行更新语句
	 * @param hql
	 * @param args
	 * @return
	 */
	protected static int commitNamedUpdate(String hql, Object...args){
		try{
			Session ssn = getSession();
			beginTransaction();
			Query q = ssn.getNamedQuery(hql);
			for(int i=0;i<args.length;i++){
				q.setParameter(i, args[i]);
			}
			int er = q.executeUpdate();
			commit();
			return er;
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
}

/**
 * 用于操作Hibernate的一些方法
 * @author Winter Lau
 */
abstract class _DAOBase {

	/**
	 * Get a instance of hibernate's session
	 * @return
	 * @throws HibernateException
	 */
	protected static Session getSession(){
		return HibernateUtils.getSession();
	}

	/**
	 * Start a new database transaction.
	 */
	protected static void beginTransaction(){
		HibernateUtils.beginTransaction();
	}

	/**
	 * Commit the database transaction.
	 */
	protected static void commit(){
		HibernateUtils.commit();
	}

	/**
	 * Rollback the database transaction.
	 */
	protected static void rollback(){
		HibernateUtils.rollback();
	}
	
}