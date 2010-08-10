/*
 *  TrackBackDAO.java
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

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.liusoft.dlog4j.base._BeanBase;
import com.liusoft.dlog4j.beans.TrackBackBean;

/**
 * 用于处理TrackBack的数据库访问接口
 * @author Winter Lau
 */
public class TrackBackDAO extends DAO {
	
	/**
	 * 增加一条引用信息
	 * @param bean
	 */
	public static void create(TrackBackBean bean){
		if(bean.getTrackTime()==null)
			bean.setTrackTime(new Date());
		Session ssn = getSession();
		try{
			beginTransaction();
			ssn.save(bean);
			if(bean.getParentType()==_BeanBase.TYPE_DIARY){
				DiaryDAO.incTrackBackCount(ssn, bean.getParentId(), 1);
			}
			commit();
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}

}
