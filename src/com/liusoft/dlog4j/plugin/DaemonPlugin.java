/*
 *  DaemonPlugin.java
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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

/**
 * 用于间歇性执行某项任务的插件基类
 * @author Winter Lau
 */
public abstract class DaemonPlugin implements PlugIn, Runnable {

	protected final static Log log = LogFactory.getLog(DaemonPlugin.class);
	
	private int activeInterval = 10; // 抓取间隔，单位：分钟

	private int timeSlice = 1; // 插件活动间隔，单位：秒

	private boolean enabled = true; // 是否启用该插件(可在测试环境中关闭该插件)

	private Thread fetcher;

	private boolean stopped = false;

	private ActionServlet servlet;

	private ModuleConfig config;

	/**
	 * 返回插件的名字
	 * 
	 * @return
	 */
	protected abstract String name();

	/**
	 * 插件任务执行过程
	 * 
	 * @throws Exception
	 */
	protected abstract void service() throws Exception;

	protected ServletContext context() {
		return servlet.getServletContext();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.PlugIn#init(org.apache.struts.action.ActionServlet,
	 *      org.apache.struts.config.ModuleConfig)
	 */
	public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
		this.servlet = servlet;
		this.config = config;
		if (enabled) {
			fetcher = new Thread(this);
			fetcher.start();
		}
		log.info(name() + " plugin started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.PlugIn#destroy()
	 */
	public void destroy() {
		stopped = true;
		if (fetcher != null)
			try {
				fetcher.join(10000);
			} catch (InterruptedException e) {
			}
		log.warn(name() + " plugin stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		long interval = activeInterval * 60;// 转化为秒
		long checkCount = interval / timeSlice;
		int sleepTime = timeSlice * 1000;

		while (!stopped) {
			try {
				service();
			} catch (Exception e) {
				log.error(name() + " failed when execution: ", e);
			} finally {
				log.info(name() + " finished, next running time is after " + activeInterval + " min.");
				int i = 0;
				for (; !stopped && i < checkCount; i++) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						break;
					}
				}
				if (i < checkCount)
					break;
			}
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setTimeSlice(int timeSlice) {
		this.timeSlice = timeSlice;
	}

	public ModuleConfig getConfig() {
		return config;
	}

	public ActionServlet getServlet() {
		return servlet;
	}

	public void setActiveInterval(int activeInterval) {
		this.activeInterval = activeInterval;
	}
}
