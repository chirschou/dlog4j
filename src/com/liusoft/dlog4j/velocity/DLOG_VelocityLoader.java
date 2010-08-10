/*
 *  DLOG_VelocityLoader.java
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

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.tools.view.servlet.WebappLoader;

import com.liusoft.dlog4j.DLOGTemplateManager;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 自定义Velocity模板加载器
 * @author liudong
 * @email javayou@gmail.com
 */
public class DLOG_VelocityLoader extends WebappLoader {

	public final static String CUSTOM_TEMPLATES_PATH = "/WEB-INF/templates/";
	private final static HashMap<Integer, _VelocityTemplate> cache = new HashMap<Integer, _VelocityTemplate>();
	public final static int CHECK_INTERVAL = 1800000; //半个小时重新检查一下文件
	public final static String _clean_cache_vm = "/_clean_vm_cache.vm";
	
	/**
	 * 实现自定义页面
	 */
    public InputStream getResourceStream(String arg0) throws ResourceNotFoundException {
    	SiteBean site = DLOGTemplateManager.getSite();    	
    	if(site !=null && !arg0.startsWith("/WEB-INF/")){
    		String templateName = getTemplateName(arg0);
    		if(!templateName.startsWith("_")){
    			String layout = getLayoutNameOfSite(site);
	    		StringBuffer custom_page = new StringBuffer(CUSTOM_TEMPLATES_PATH);
	        	custom_page.append(layout);
	        	if(!arg0.startsWith("/"))
	        		custom_page.append('/');
	        	custom_page.append(arg0);
	        	String vm_page = custom_page.toString();
	        	do{
		        	_VelocityTemplate tmp = cache.get(vm_page.hashCode());
		        	if(tmp!=null && tmp.isExist())
		        		return super.getResourceStream(vm_page);
	
		        	if(tmp!=null && !tmp.isExist()){
		        		long timeout = System.currentTimeMillis() - tmp.getLastCheckTime();
		        		if(timeout < CHECK_INTERVAL)
		        			break;
		        	}
		        	//System.out.println("checking if the template is exists, vm="+vm_page+",size="+cache.size());
		        	String real_path = servletContext.getRealPath(vm_page);
		        	File vm_file = new File(real_path);
		        	try{
		        		boolean isTemplate = vm_file.exists() && vm_file.isFile();
		        		if(tmp == null)
		        			tmp = new _VelocityTemplate();
		        		tmp.setExist(isTemplate);
		        		tmp.setLastCheckTime(System.currentTimeMillis());
		        		//tmp.setPath(vm_page);
		        		cache.put(vm_page.hashCode(), tmp);
			        	if(isTemplate)
			        		return super.getResourceStream(vm_page);
		        	}finally{
		        		vm_file = null;
		        		custom_page = null;
		        	}
		        	break;
	        	}while(true);
    		}
    	}
		return super.getResourceStream(arg0);
	}
    
    /**
     * 获取网站所使用的布局
     * @param site
     * @return
     */
    private String getLayoutNameOfSite(SiteBean site){
    	if(site == null)
    		return null;
    	if(StringUtils.isEmpty(site.getStyle().getLayout()))
    		return "1";
    	return site.getStyle().getLayout();
    }
    
    private String getTemplateName(String uri){
    	int idx = uri.lastIndexOf('/');
    	if(idx < 0)
    		return uri;
    	return uri.substring(idx + 1);
    }

	/**
     * Defaults to return false.
     */
    public boolean isSourceModified(Resource resource)
    {
        return false;
    }

    /**
     * Defaults to return 0
     */
    public long getLastModified(Resource resource)
    {
    	/*
    	if(servletContext!=null){
	    	String vmPath = servletContext.getRealPath(resource.getName());
	    	return new File(vmPath).lastModified();
    	}*/
    	return 0;
    }
    
    /**
     * 记录上次模板的加载信息
     * @author liudong
     */
    private class _VelocityTemplate implements Serializable{
    	//private String path;
    	private long lastCheckTime;
    	private boolean exist;
    	
		public boolean isExist() {
			return exist;
		}
		public void setExist(boolean exist) {
			this.exist = exist;
		}
		public long getLastCheckTime() {
			return lastCheckTime;
		}
		public void setLastCheckTime(long lastCheckTime) {
			this.lastCheckTime = lastCheckTime;
		}
		/*
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}*/
    }
}
