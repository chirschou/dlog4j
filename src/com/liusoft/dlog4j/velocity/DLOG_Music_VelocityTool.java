/*
 *  DLOG_Music_VelocityTool.java
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
package com.liusoft.dlog4j.velocity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liusoft.dlog4j.base._BeanBase;
import com.liusoft.dlog4j.beans.MusicBean;
import com.liusoft.dlog4j.beans.MusicBoxBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.dao.MusicDAO;

/**
 * 音乐频道的Velocity toolbox类
 * @author Winter Lau
 */
public class DLOG_Music_VelocityTool{

	private final static Log log = LogFactory.getLog(DLOG_Music_VelocityTool.class);
	
	/**
	 * 记录音乐欣赏次数
	 * @param songs
	 */
	public void visit_music(List songs){
		if(songs!=null && songs.size()>0){
			int[] ids = new int[songs.size()];
			for(int i=0;i<songs.size();i++){
				_BeanBase bean = (_BeanBase)songs.get(i);
				ids[i] = bean.getId();
			}
			try{
				MusicDAO.incViewCount(1, ids);
			}catch(Exception e){
				log.error("increment music's listen count failed.", e);
			}
		}
	}
	
	/**
	 * 获取音乐盒信息
	 * @param mboxid
	 * @return
	 */
	public MusicBoxBean box(int mboxid){
		if(mboxid < 1)
			return null;
		return MusicDAO.getMusicBoxByID(mboxid);
	}
	
	/**
	 * 查询出所有不在某个音乐盒的歌曲
	 * @param site_id
	 * @return
	 */
	public List songs_without_box(SiteBean site){
		if(site == null)
			return null;
		return MusicDAO.listSongsWithoutBox(site.getId());
	}
	
	/**
	 * 获取音乐详细信息
	 * @param music_id
	 * @return
	 */
	public MusicBean music(int music_id){
		if(music_id < 1)
			return null;
		return MusicDAO.getMusicByID(music_id);		
	}

	/**
	 * 获取歌曲数
	 * @param site
	 * @return
	 */
	public int music_count(SiteBean site){
		if(site == null)
			return -1;
		return MusicDAO.getMusicCount(site);
	}
	
	/**
	 * 列出选中的歌曲信息
	 * @param mids
	 * @return
	 */
	public List list_songs(String mids){
		if(mids == null)
			return null;
		StringTokenizer st = new StringTokenizer(mids,",");
		List ids = new ArrayList();
		while(st.hasMoreElements()){
			String sid = st.nextToken();
			try{
				ids.add(new Integer(sid));
			}catch(Exception e){}
		}
		return MusicDAO.listSongs(ids);
	}
	
	/**
	 * 列出某个站点最新的歌曲
	 * @param site
	 * @return
	 */
	public List list_new_songs(SiteBean site, int max_count){
		if(site==null)
			return null;
		return MusicDAO.listNewSongs(site.getId(), max_count);
	}
	
}
