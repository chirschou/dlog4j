/*
 *  BBSSearchAction.java
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
package com.liusoft.dlog4j.action;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.TopicBean;
import com.liusoft.dlog4j.beans.TopicReplyBean;
import com.liusoft.dlog4j.formbean.BBSSearchForm;
import com.liusoft.dlog4j.search.SearchParameter;
import com.liusoft.dlog4j.search.SearchProxy;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 论坛搜索的Action
 * @author Winter Lau
 */
public class BBSSearchAction extends ActionBase {

	/**
	 * 论坛搜索
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDefault(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	
		final BBSSearchForm sform = (BBSSearchForm)form;
		if(StringUtils.isEmpty(sform.getKey())){
			return mapping.getInputForward();
		}
		//设置搜索条件
		final SiteBean site = super.getSiteByID(sform.getId());

		SearchParameter param = new SearchParameter() {
			public String getSearchKey() {
				return sform.getKey();
			}
			public HashMap getConditions() {
				HashMap conds = new HashMap();
				if (site != null) {
					conds.put("site.id", new Integer(site.getId()));
				}
				if(sform.getFid()>0){
					conds.put("forum.id", new Integer(sform.getFid()));
				}
				return conds;
			}

			public Class getSearchObject() {
				if(sform.getFid()==-2)
					return TopicReplyBean.class;				
				return TopicBean.class;
			}
		};
		long start = System.currentTimeMillis();
		List results = SearchProxy.search(param);
		//清除不在设定时间条件范围内的记录
		if(results!=null){
			if(sform.getDateRange()>0){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -sform.getDateRange());
				Date t = cal.getTime();
				Iterator objs = results.iterator();
				while(objs.hasNext()){
					Object obj = objs.next();
					if(obj instanceof TopicBean){
						TopicBean tb = (TopicBean)obj;
						if(tb.getCreateTime().before(t))
							objs.remove();
					}
					else if(obj instanceof TopicReplyBean){
						TopicReplyBean trb = (TopicReplyBean)obj;
						if(trb.getReplyTime().before(t))
							objs.remove();
					}
				}
			}
			long lTime = System.currentTimeMillis() - start;
			
			//设置分页
			sform.setPageCount((int)Math.ceil(results.size() / (double)sform.getNumResults()));
			if(sform.getPage()<1)
				sform.setPage(1);
			if(sform.getPage()>sform.getPageCount())
				sform.setPage(sform.getPageCount());
			
			//设置查询结果
			int fromIdx = (sform.getPage()-1) * sform.getNumResults();
			if(fromIdx < 0)
				fromIdx = 0;
			int toIdx = fromIdx + sform.getNumResults();
			if(toIdx > results.size())
				toIdx = results.size();
			
			request.setAttribute("results", results.subList(fromIdx, toIdx));
			request.setAttribute("time", Long.toString(lTime));
		}
		request.setAttribute("param", sform);

		return mapping.getInputForward();
	}
}
