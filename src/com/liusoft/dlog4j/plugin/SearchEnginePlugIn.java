/*
 *  SearchEnginePlugIn.java
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
package com.liusoft.dlog4j.plugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.db.HibernateUtils;
import com.liusoft.dlog4j.search.SearchDataProvider;
import com.liusoft.dlog4j.search.SearchEnabled;
import com.liusoft.dlog4j.search.SearchProxy;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * <p>A search engine daemon using lucene implements struts's plugin</p>
 * <p>This plugin require a property named "basePath"
 * to indicate where the lucene files stored.</p>
 * <p>
 * In UNIX or Linux OS, you must use "file://" to a no-webapp-context path<br/>
 * Example: "file:///data/lucene" refer to /data/lucene but {webapp}/data/lucene
 * </p>
 * @author Winter Lau
 */
public class SearchEnginePlugIn extends DaemonPlugin {

	private String basePath;
	private String dataProvider0;
	private String dataProvider1;
	private String dataProvider2;
	private String dataProvider3;
	private String dataProvider4;
	private String dataProvider5;
	private String dataProvider6;
	private String dataProvider7;
	private String dataProvider8;
	private String dataProvider9;
	
	private ThreadGroup tGroup = new ThreadGroup("build_idx_thread");
	
	private boolean stop = false;
	
	/**
	 * 初始化搜索引擎
	 */
	public void init(ActionServlet servlet, ModuleConfig config) 
		throws ServletException 
	{		
		if(basePath.startsWith(Globals.LOCAL_PATH_PREFIX)){
			basePath = basePath.substring(Globals.LOCAL_PATH_PREFIX.length());
		}
		else if(basePath.startsWith("/")){
			basePath = servlet.getServletContext().getRealPath(basePath);
		}
		//初始化搜索代理
		SearchProxy.init(basePath);

		super.init(servlet, config);
		
	}

	public void destroy() {
		stop = true;
		int tCount = tGroup.activeCount();
		if(tCount > 0){
			Thread[] threads = new Thread[tCount];
			int tc = tGroup.enumerate(threads);
			for(int i=0;i<tc;i++){
				if(threads[i] instanceof BuildIndexThread)					
				try{
					threads[i].join(10000, 200);
				}catch(InterruptedException e){
					log.error("Exception occurred when waiting for thread " + threads[i].getClass().getName(), e);
				}
			}
		}
		super.destroy();
	}

	/**
	 * 自动build文档的索引
	 */
	protected void service() throws Exception {
		if(StringUtils.isNotEmpty(dataProvider0))
			buildIndexes(dataProvider0);
		if(StringUtils.isNotEmpty(dataProvider1))
			buildIndexes(dataProvider1);
		if(StringUtils.isNotEmpty(dataProvider2))
			buildIndexes(dataProvider2);
		if(StringUtils.isNotEmpty(dataProvider3))
			buildIndexes(dataProvider3);
		if(StringUtils.isNotEmpty(dataProvider4))
			buildIndexes(dataProvider4);
		if(StringUtils.isNotEmpty(dataProvider5))
			buildIndexes(dataProvider5);
		if(StringUtils.isNotEmpty(dataProvider6))
			buildIndexes(dataProvider6);
		if(StringUtils.isNotEmpty(dataProvider7))
			buildIndexes(dataProvider7);
		if(StringUtils.isNotEmpty(dataProvider8))
			buildIndexes(dataProvider8);
		if(StringUtils.isNotEmpty(dataProvider9))
			buildIndexes(dataProvider9);		
	}
	
	/**
	 * 构建索引
	 * TODO: 如何防止上一个线程还没结束又启动了一个新的线程
	 * @param providerClass
	 * @param lastTime
	 */
	private int buildIndexes(final String providerClass){
		if(stop)
			return -1;
		
		new BuildIndexThread(context(), tGroup, providerClass).start();
		
		return 0;
	}
	
	protected String name() {
		return "search_engine";
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setDataProvider0(String dataProvider0) {
		this.dataProvider0 = dataProvider0;
	}

	public void setDataProvider1(String dataProvider1) {
		this.dataProvider1 = dataProvider1;
	}

	public void setDataProvider2(String dataProvider2) {
		this.dataProvider2 = dataProvider2;
	}

	public void setDataProvider3(String dataProvider3) {
		this.dataProvider3 = dataProvider3;
	}

	public void setDataProvider4(String dataProvider4) {
		this.dataProvider4 = dataProvider4;
	}

	public void setDataProvider5(String dataProvider5) {
		this.dataProvider5 = dataProvider5;
	}

	public void setDataProvider6(String dataProvider6) {
		this.dataProvider6 = dataProvider6;
	}

	public void setDataProvider7(String dataProvider7) {
		this.dataProvider7 = dataProvider7;
	}

	public void setDataProvider8(String dataProvider8) {
		this.dataProvider8 = dataProvider8;
	}

	public void setDataProvider9(String dataProvider9) {
		this.dataProvider9 = dataProvider9;
	}

}

/**
 * 构建索引的线程
 * @author liudong
 */
class BuildIndexThread extends Thread {

	protected final static Log log = LogFactory.getLog(BuildIndexThread.class);

	private ServletContext context;
	private String providerClass;

	public BuildIndexThread(ServletContext context, ThreadGroup group,
			String providerClass) {
		super(group, providerClass);
		this.context = context;
		this.providerClass = providerClass;
	}
	
	public void run() {
		List objs = null;
		try {
			Date lastTime = getLastActiveTime(providerClass);
			SearchDataProvider sdp = (SearchDataProvider) Class.forName(
					providerClass).newInstance();
			if (lastTime == null)
				lastTime = new Date(0);
			objs = sdp.fetchAfter(lastTime);
			if (objs != null) {
				for (int i = 0; i < objs.size(); i++) {
					SearchEnabled obj = (SearchEnabled) objs.get(i);
					SearchProxy.add(obj);
					if (i > 0 && (i + 1) % 10 == 0)
						log.info((i + 1) + " document's indexes added.");
				}
				saveLastActiveTime(providerClass, new Date());
				if (objs.size() > 0) {
					log.info(objs.size() + " documents writed to disk of "
							+ providerClass);
				}
			} else
				log.warn("fetch data of " + providerClass + " return null");
		} catch (Exception e) {
			log.error("Exception occur when buildIndexes using "
					+ providerClass, e);
		} finally {
			HibernateUtils.closeSession();
			objs = null;
		}
	}

	/**
	 * 获取某个数据接口上次的活动时间
	 * 
	 * @param pvdClass
	 * @return
	 * @throws IOException
	 */
	private Date getLastActiveTime(String pvdClass) throws IOException {
		// Date lastTime = null;
		StringBuffer status_file_uri = new StringBuffer(STATUS_FILE_PATH);
		status_file_uri.append(pvdClass);
		status_file_uri.append(".his");
		String realPath = context.getRealPath(status_file_uri.toString());
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(realPath);
			props.load(fis);
			String s_last_time = props.getProperty(TIME_KEY);
			return new Date(Long.parseLong(s_last_time));
		} catch (FileNotFoundException e) {
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		} finally {
			props = null;
			if (fis != null)
				fis.close();
		}
		return null;
	}

	/**
	 * 保存最近一次活动时间
	 * 
	 * @param pvdClass
	 * @param time
	 * @throws IOException
	 */
	private void saveLastActiveTime(String pvdClass, Date time)
			throws IOException {
		StringBuffer status_file_uri = new StringBuffer(STATUS_FILE_PATH);
		status_file_uri.append(pvdClass);
		status_file_uri.append(".his");
		String realPath = context.getRealPath(status_file_uri.toString());
		Properties props = new Properties();
		FileOutputStream fos = null;
		try {
			props.setProperty(TIME_KEY, String.valueOf(time.getTime()));
			props.setProperty("LAST_TIME", time.toString());
			fos = new FileOutputStream(realPath);
			props.store(fos, null);
		} finally {
			props = null;
			if (fos != null){
				fos.close();
				fos = null;
			}
		}
	}

	private final static String STATUS_FILE_PATH = "/WEB-INF/tmp/";

	private final static String TIME_KEY = "LAST_ACTIVITY_TIME";

}
