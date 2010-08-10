/*
 *  MusicDAO.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.liusoft.dlog4j.CapacityExceedException;
import com.liusoft.dlog4j.base.Orderable;
import com.liusoft.dlog4j.beans.MusicBean;
import com.liusoft.dlog4j.beans.MusicBoxBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.search.SearchDataProvider;

/**
 * 音乐数据访问接口
 * @author Winter Lau
 */
public class MusicDAO extends DAO implements SearchDataProvider{

	public final static int MAX_MUSICBOX_COUNT = 20;

	/**
	 * 增加音乐的欣赏次数
	 * @param music_id
	 * @param incCount
	 * @return
	 */
	public static int incViewCount(int incCount, int[] music_ids){
		StringBuffer hql = new StringBuffer("UPDATE MusicBean d SET d.viewCount=d.viewCount+? WHERE d.id IN (");
		for(int i=0;i<music_ids.length;i++){
			if(i > 0) hql.append(',');
			hql.append('?');
		}
		hql.append(')');
		try{
			Session ssn = getSession();
			beginTransaction();
			Query q = ssn.createQuery(hql.toString());
			q.setInteger(0, incCount);
			for(int i=1;i<=music_ids.length;i++){
				q.setParameter(i, music_ids[i-1]);
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
	 * 根据编号获取歌曲信息
	 * @param ids
	 * @return
	 */
	public static List listSongs(List ids){
		Session ssn = getSession();
		StringBuffer hql = new StringBuffer("FROM MusicBean m WHERE m.id IN (");
		int i=0;
		for(;i<ids.size();i++){
			hql.append("?,");
		}
		hql.append("?) ORDER BY m.id DESC");			
		Query q = ssn.createQuery(hql.toString());
		for(i=0;i<ids.size();i++){
			int id = ((Number)ids.get(i)).intValue();
			q.setInteger(i, id);
		}
		q.setInteger(i, -1);
		return q.list();
	}
	
	/**
	 * 搜索歌曲
	 * @param site
	 * @param key
	 * @return
	 */
	public static List search(String key){
		Session ssn = getSession();
		Query q = ssn.getNamedQuery("SEARCH_MUSIC");
		String pattern = '%' + key + '%';
		q.setString("key", pattern);
		q.setMaxResults(20);
		List res = q.list();
		List songs = new ArrayList();
		for(int i=0;res!=null&&i<res.size();i++){
			Object[] objs = (Object[])res.get(i);
			MusicBean mbean = new MusicBean();
			mbean.setTitle((String)objs[0]);
			mbean.setUrl((String)objs[1]);
			songs.add(mbean);
		}
		return songs;
	}
	
	/**
	 * 列出某个站点最新的N首歌曲
	 * @param siteid
	 * @param maxCount
	 * @return
	 */
	public static List listNewSongs(int siteid, int maxCount){
		return executeNamedQuery("LIST_NEW_MUSIC",-1,maxCount,siteid);
	}

	/**
	 * 列出某个站点最新的N首歌曲
	 * @param siteid
	 * @param maxCount
	 * @return
	 */
	public static List listSongsWithoutBox(int siteid){
		return findNamedAll("LIST_MUSIC_WITHOUT_BOX",siteid);
	}
	
	/**
	 * 添加歌曲
	 * @param mbean
	 */
	public static void addMusic(MusicBean mbean){
		Session ssn = getSession();
		try{
			beginTransaction();
			if(mbean.getStatus()==MusicBean.STATUS_NORMAL){
				if(mbean.getMusicBox()!=null)
					mbean.getMusicBox().incMusicCount(1);
			}
			ssn.save(mbean);
			commit();
		}catch(HibernateException e){
			e.printStackTrace();
			rollback();
			throw e;
		}
	}

	/**
	 * 删除歌曲
	 * @param mbean
	 */
	public static void deleteMusic(MusicBean mbean){
		Session ssn = getSession();
		try{
			beginTransaction();
			if(mbean.getStatus()==MusicBean.STATUS_NORMAL){
				if(mbean.getMusicBox()!=null)
					mbean.getMusicBox().incMusicCount(-1);
			}
			//删除引用该歌曲的日记背景音乐
			String hql = "UPDATE DiaryBean d SET d.bgSound = ? WHERE d.bgSound.id=?";
			executeUpdate(hql, null, mbean.getId());
			//删除歌曲
			ssn.delete(mbean);
			commit();
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}

	/**
	 * 删除音乐盒
	 * @param mbean
	 */
	public static void deleteMusicBox(MusicBoxBean mbean){
		delete(mbean);
	}

	/**
	 * 批量删除歌曲
	 * @param mbean
	 */
	public static void deleteMusics(int siteid, int[] ids){
		Session ssn = getSession();
		try{
			beginTransaction();
			StringBuffer hql = new StringBuffer("FROM MusicBean m WHERE m.site.id=? AND m.id IN (");
			StringBuffer hql2 = new StringBuffer("UPDATE DiaryBean d SET d.bgSound=? WHERE d.bgSound.id IN (");
			int i=0;
			for(;i<ids.length;i++){
				hql.append("?,");
				hql2.append("?,");
			}
			hql.append("?)");	
			hql2.append("?)");			
			Query q = ssn.createQuery(hql.toString());
			q.setInteger(0, siteid);
			i=0;
			for(;i<ids.length;i++){
				q.setInteger(i+1, ids[i]);
			}
			q.setInteger(i+1, ids[0]);
			List musics = q.list();
			if(musics.size()>0){
				beginTransaction();
				//清除日记中对该音乐的引用
				Query q2 = ssn.createQuery(hql2.toString());
				i=0;
				q2.setParameter(0, null);
				for(;i<ids.length;i++){
					q2.setInteger(i+1, ids[i]);
				}
				q2.setInteger(i+1, ids[0]);
				q2.executeUpdate();
				//删除音乐
				for(i=0;i<musics.size();i++){
					MusicBean mbean = (MusicBean)musics.get(i);
					if(mbean.getMusicBox()!=null)
						mbean.getMusicBox().incMusicCount(-1);
					ssn.delete(mbean);
				}
				commit();
			}
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 创建音乐盒
	 * @param mbox
	 * @param pos
	 * @param up
	 * @throws CapacityExceedException 
	 * @throws SQLException  
	 */
	public static void createBox(MusicBoxBean mbox, int pos, boolean up)
			 throws CapacityExceedException {
		Session ssn = getSession();
		int order_value = 1;
		if (pos > 0) {
			MusicBoxBean friend = (MusicBoxBean) ssn.get(MusicBoxBean.class,
					new Integer(pos));
			order_value = friend.getSortOrder();
		}
		mbox.setSortOrder(order_value - (up ? 1 : 0));
		try {
			beginTransaction();
			ssn.save(mbox);
			// 重新读取链接列表，依照顺序进行整理
			List links = findNamedAll("LIST_MUSICBOXES", mbox.getSite().getId());
			if (links.size() >= ConfigDAO.intValue(mbox.getSite().getId(),
					"MAX_MUSICBOX_COUNT", MAX_MUSICBOX_COUNT))
				throw new CapacityExceedException(links.size());
			if (links.size() > 1) {
				for (int i = 0; i < links.size(); i++) {					
					Orderable lb = (Orderable) links.get(i);
					executeNamedUpdate("UPDATE_MUSICBOX_ORDER", (i+1), lb.getId());
				}
			}
			commit();
		} catch (HibernateException e) {
			rollback();
			throw e;
		}
	}
	
	/**
	 * 返回某个站点的歌曲数
	 * @param site
	 * @return
	 */
	public static int getMusicCount(SiteBean site){
		if(site==null) return -1;
		return executeNamedStat("MUSIC_COUNT", site.getId()).intValue();
	}
	
	/**
	 * 根据音乐编号获取详细资料
	 * @param mid
	 * @return
	 */
	public static MusicBean getMusicByID(int mid){
		if(mid <=0)
			return null;
		return (MusicBean)getBean(MusicBean.class, mid);
	}
	
	/**
	 * 根据音乐盒的编号获取详细信息
	 * @param mboxid
	 * @return
	 */
	public static MusicBoxBean getMusicBoxByID(int mboxid){
		if(mboxid < 1) 
			return null;
		return (MusicBoxBean)getBean(MusicBoxBean.class, mboxid);
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.search.SearchDataProvider#fetchAfter(java.util.Date)
	 */
	public List fetchAfter(Date beginTime) throws Exception {
		return findNamedAll("LIST_MUSIC_AFTER", beginTime, MusicBean.STATUS_NORMAL);
	}
	
}
