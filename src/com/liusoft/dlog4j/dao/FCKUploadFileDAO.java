/*
 *  FCKUploadFileDAO.java
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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.liusoft.dlog4j.CapacityExceedException;
import com.liusoft.dlog4j.beans.FckUploadFileBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.upload.FCK_UploadManager;
import com.liusoft.dlog4j.util.DLOG4JUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 用于管理数据库中FCK编辑器上传的文件信息
 * @author liudong
 */
public class FCKUploadFileDAO extends DAO {
	
	/**
	 * 写入一个上传文件的信息
	 * @see com.liusoft.dlog4j.upload.SecurityFCKUploadServlet#doPost(HttpServletRequest, HttpServletResponse)
	 * @param fbean
	 */
	public static void createUploadFileItem(FckUploadFileBean fbean){
		if(fbean.getUploadTime()==null)
			fbean.setUploadTime(new Date());	
		save(fbean);
	}

	/**
	 * 查询某个HTTP会话上传的且尚未被领取的所有文件信息
	 * @param userid
	 * @param sessionId
	 * @return
	 */
	public static List listOrphanFiles(int userid, String sessionId){
		if(StringUtils.isEmpty(sessionId))
			return null;
		return findNamedAll("LIST_ORPHAN_FILES_IN_SESSION", userid, sessionId);
	}
	
	/**
	 * 列出所有上传的图片(用于管理)
	 * @param fromIdx
	 * @param count
	 * @return
	 */
	public static List listFiles(int fromIdx, int count){
		String hql = "FROM FckUploadFileBean AS f ORDER BY f.id DESC";
		return executeQuery(hql, fromIdx, count);
	}	

	/**
	 * 获取文件总数（用于管理）
	 * @return
	 */
	public static int fileCount(){
		String hql = "SELECT COUNT(*) FROM FckUploadFileBean AS f";
		return executeStatAsInt(hql);
	}
	

	/**
	 * 查询某个HTTP会话上传的且尚未被领取的所有文件信息
	 * 要把文件大小计算进site中
	 * @param userid
	 * @param sessionId
	 * @return
	 * @throws CapacityExceedException  
	 */
	public static int pickupOrphanFiles(int userid, String sessionId,
			 SiteBean site, int refId, int refType)
			 throws CapacityExceedException 
	{
		int er = 0;
		try{
			//要把文件大小计算进site中
			Number totalSize = executeNamedStat("SUM_UPLOAD_FILE_SIZE", userid, sessionId);
			if(totalSize!=null){
				beginTransaction();
				int iTotalSize = totalSize.intValue();			
				int file_size = DLOG4JUtils.sizeInKbytes(iTotalSize);
				if (site.getCapacity().getDiaryTotal() > 0
						&& site.getCapacity().getDiaryUsed() + file_size > site
								.getCapacity().getDiaryTotal()) {
					// 已然超过可用的空间
					throw new CapacityExceedException(site.getCapacity()
							.getDiaryTotal());
				}
				executeNamedUpdate("INC_SITE_UPLOAD_SPACE", Math.max(1, file_size), site.getId());
				//更新尚未领取文件的归属信息
				executeNamedUpdate("PICKUP_UPLOAD_FILES", site.getId(), refId, refType, userid, sessionId);
				commit();
			}
			return er;
		}catch(HibernateException e){
			rollback();
			throw e;
		}
	}
	
	/**
	 * 删除某条信息
	 * @param siteid
	 * @param obj_id
	 * @return
	 * @throws IOException 
	 */
	public static boolean deleteFileById(int siteid, int obj_id) throws Exception{
		FckUploadFileBean fufb = (FckUploadFileBean)namedUniqueResult("GET_UPLOAD_FILE", obj_id);
		if(fufb!=null && fufb.getSite().getId()==siteid){
			delete(fufb);
			FCK_UploadManager.getUploadHandler().remove(fufb);
			return true;
		}
		return false;
	}

	/**
	 * 删除某条信息
	 * @param siteid
	 * @param obj_id
	 * @return
	 * @throws IOException 
	 */
	public static boolean deleteFileById(int userid, String ssn_id, int obj_id) throws Exception{
		FckUploadFileBean fufb = (FckUploadFileBean)namedUniqueResult("GET_UPLOAD_FILE", obj_id);
		if(fufb!=null && fufb.getUser().getId()==userid && StringUtils.equals(fufb.getSessionId(), ssn_id)){				
			delete(fufb);
			FCK_UploadManager.getUploadHandler().remove(fufb);
			return true;
		}
		return false;
	}
	
	/**
	 * 删除被某篇文章引用的所有上传的文件
	 * @param site_id
	 * @param ref_id
	 * @param ref_type
	 * @return
	 * @throws IOException 
	 */
	public static int deleteFilesByRef(Session ssn, int site_id, int ref_id, int ref_type) throws Exception{		
		List files = findNamedAll("GET_UPLOAD_FILE_BY_REF", site_id,ref_id, ref_type);
		for(int i=0;i<files.size();i++){
			FckUploadFileBean fufb = (FckUploadFileBean)files.get(i);
			if(StringUtils.isNotEmpty(fufb.getSavePath())){
				FCK_UploadManager.getUploadHandler().remove(fufb);
			}

			int photo_site = DLOG4JUtils.sizeInKbytes(fufb.getFileSize());
			fufb.getSite().getCapacity().incDiaryUsed(-photo_site);
			
			ssn.delete(fufb);
		}
		return files.size();
	}
	
	/**
	 * 清除无用的上传文件
	 * @param session_id
	 * @param userid
	 * @return
	 * @throws IOException 
	 */
	public static int cleanupOfSession(String session_id, int userid) throws Exception{
		Session ssn = getSession();
		if(ssn == null)
			return -1;
		int er = 0;
		try{
			beginTransaction();
			List beans = findNamedAll("LIST_ORPHAN_FILES_IN_SESSION",userid, session_id);
			for(int i=0;i<beans.size();i++){
				FckUploadFileBean fufb = (FckUploadFileBean)beans.get(i);
				FCK_UploadManager.getUploadHandler().remove(fufb);
				ssn.delete(fufb);
				er ++;
			}
			commit();			
		}catch(HibernateException e){
			rollback();
		}catch(IOException e){
			commit();
			throw e;
		}
		return er;
	}
	
}
