/*
 *  DLOG_HomeFilter.java
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
 *  2006-5-10
 */
package com.liusoft.dlog4j.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.digester.Digester;

import com.liusoft.dlog4j.DLOG_CacheManager;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.dao.SiteDAO;
import com.liusoft.dlog4j.util.DLOG4JUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 用于处理使用网站名直接访问的请求，例如
 * http://www.dlog.cn/javayou
 * @author liudong
 */
public class DLOG_HomeFilter implements Filter {

	protected String msg_page = "/html/_sub/_err_msg.vm";
	protected String content_type = "text/html;charset=utf-8";
	protected String html_home_page_pattern = "/?sid={SID}";
	protected String wml_home_page_pattern = "/wml/?sid={SID}";

	private ServletContext context;
	private String filterName;
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig cfg) throws ServletException {
		//this.config = cfg;
		context = cfg.getServletContext();
		filterName = cfg.getFilterName();
		initServlet();
		
		if(mappingFormat == null)
			throw new ServletException("DLOG_HomeFilter's filter-mapping undefined.");
		
		/* reading the init params */
		String page = cfg.getInitParameter("msg_page");
		if(StringUtils.isNotEmpty(page))
			this.msg_page = page;
		String ct = cfg.getInitParameter("content_type");
		if(StringUtils.isNotEmpty(ct))
			this.content_type = ct;
		ct = cfg.getInitParameter("home_page_pattern");
		if(StringUtils.isNotEmpty(ct))
			this.html_home_page_pattern = ct;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		String siteName = req.getParameter("sitename");
		if(StringUtils.isEmpty(siteName)){
			try{
				String servletPath = request.getRequestURI();
				//System.out.println("servletPath1="+servletPath);
				servletPath = servletPath.substring(request.getContextPath().length());
				//System.out.println("servletPath2="+servletPath);
				if(StringUtils.isEmpty(servletPath)){
					chain.doFilter(req, res);
					return;
				}
				Object[] ps = mappingFormat.parse(servletPath);
				siteName = (String)ps[0];
				//System.out.println("siteName="+siteName);
				if(!DLOG4JUtils.isLegalSiteName(siteName)){
					chain.doFilter(req, res);
					return;
				}
			}catch(ParseException e){
				context.log("Parse request url failed.", e);
				chain.doFilter(req, res);
				return;
			}
		}
		Cache cache = DLOG_CacheManager.getCache("DLOG4J_sites");
		if(cache != null){
			try {
				Element elem = cache.get(siteName);
				if(elem!=null){
					Integer site_id = (Integer)elem.getValue();
					String home_uri = StringUtils.replace(html_home_page_pattern, "{SID}", String.valueOf(site_id));
					//response.sendRedirect(request.getContextPath()+home_uri);
					context.getRequestDispatcher(home_uri).forward(req, res);
					return;
				}
			} catch (Exception e) {
				context.log("Read cache named DLOG4J_sites failed", e);
			}
		}
		else{
			context.log("WARN: ****** cache named DLOG4J_sites unavailable. ******");
		}
		SiteBean site = SiteDAO.getSiteByName(siteName);
		if(site==null){
			req.setAttribute("msg", "site named "+siteName+" not found.");
		}
		else if(site.getStatus()!=SiteBean.STATUS_NORMAL){
			req.setAttribute("msg", "site named "+siteName+" unavailable.");
		}
		else{
			cache.put(new Element(siteName, new Integer(site.getId())));
			String pattern;
			String host = req.getServerName().toLowerCase();
			if(host.startsWith("wap."))
				pattern = wml_home_page_pattern;
			else
				pattern = html_home_page_pattern;
			String home_uri = StringUtils.replace(pattern, "{SID}", String.valueOf(site.getId()));
			//response.sendRedirect(request.getContextPath()+home_uri);
			context.getRequestDispatcher(home_uri).forward(req, res);
			return;
		}
		res.setContentType("text/html;charset=utf-8");
		RequestDispatcher rd = context.getRequestDispatcher(msg_page);
		rd.include(req, res);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {

	}

	protected MessageFormat mappingFormat;
    /**
     * <p>Initialize the servlet mapping under which our controller servlet
     * is being accessed.</p>
     *
     * @throws ServletException if error happens while scanning web.xml
     */
    private void initServlet() throws ServletException {
        // Prepare a Digester to scan the web application deployment descriptor
        Digester digester = new Digester();
        digester.push(this);
        digester.setNamespaceAware(true);
        digester.setValidating(false);

        // Register our local copy of the DTDs that we can find
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(registrations[i+1]);
            if (url != null)
                digester.register(registrations[i], url.toString());            
        }

        // Configure the processing rules that we need
        digester.addCallMethod("web-app/filter-mapping", "addFilterMapping", 2);
        digester.addCallParam("web-app/filter-mapping/filter-name", 0);
        digester.addCallParam("web-app/filter-mapping/url-pattern", 1);

        InputStream input = context.getResourceAsStream("/WEB-INF/web.xml");

        if(input==null)
        	throw new ServletException("Cannot read web.xml");

        try{
            digester.parse(input);
        }catch(Exception e){
            throw new ServletException(e);
        }finally{
            try{
                input.close();
            }catch(Exception e){}
        }
        
        if(this.mappingFormat==null)
        	mappingFormat = new MessageFormat("{1}/sites/{0}");
    }
    /**
     * <p>Remember a servlet mapping from our web application deployment
     * descriptor, if it is for this servlet.</p>
     *
     * @param servletName The name of the servlet being mapped
     * @param urlPattern The URL pattern to which this servlet is mapped
     */
    public void addFilterMapping(String servletName, String urlPattern) {
        if (servletName == null)
            return;
        if (servletName.equals(filterName)) {
            String servletMapping = StringUtils.replace(urlPattern, "*", "{0}");
            servletMapping = "{1}"+servletMapping;
            mappingFormat = new MessageFormat(servletMapping);
        }
    }

    /**
     * <p>The set of public identifiers, and corresponding resource names, for
     * the versions of the configuration file DTDs that we know about.  There
     * <strong>MUST</strong> be an even number of Strings in this list!</p>
     */
    private final static String registrations[] = {
        "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN",
        "/web-app_2_2.dtd",
        "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN",
        "/web-app_2_3.dtd"
    };
}
