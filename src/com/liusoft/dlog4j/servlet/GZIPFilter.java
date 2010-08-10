/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
package com.liusoft.dlog4j.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liusoft.dlog4j.util.RequestUtils;

/**
 * 数据压缩过滤器
 * @author Winter Lau
 */
public class GZIPFilter implements Filter {

	private List ignore_pages = new ArrayList();
	
	public void init(FilterConfig filterConfig) {
		Enumeration pnames = filterConfig.getInitParameterNames();
		while(pnames.hasMoreElements()){
			String param = (String)pnames.nextElement();
			if(param.startsWith("ignore")){
				ignore_pages.add(filterConfig.getInitParameter(param));
			}
		}
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {			
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			try{
				String uri = request.getRequestURI().substring(request.getContextPath().length());
				boolean ignore = ignore_pages.contains(uri);
				/*
				if(ignore){
					System.out.println("Ignore " + uri);
				}*/
				if (!ignore && isGZIPSupported(request)) {
					GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(
							response);
					chain.doFilter(req, wrappedResponse);
					wrappedResponse.finishResponse();
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		chain.doFilter(req, res);
	}
	
	/**
	 * 判断浏览器是否支持GZIP压缩
	 * @param req
	 * @return
	 */
	private boolean isGZIPSupported(HttpServletRequest req) {
        String browserEncodings = RequestUtils.getHeader(req, "accept-encoding");
        return
            ((browserEncodings != null) &&
            (browserEncodings.indexOf("gzip") != -1));
    } 
	
	public void destroy() {
		if(ignore_pages!=null){
			ignore_pages.clear();
			ignore_pages = null;
		}
	}
}
