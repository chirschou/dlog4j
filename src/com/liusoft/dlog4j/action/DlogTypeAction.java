/*
 *  DlogTypeAction.java
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
 */
package com.liusoft.dlog4j.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liusoft.dlog4j.beans.TypeBean;
import com.liusoft.dlog4j.dao.DlogTypeDAO;

/**
 * 用于类别维护的Action类
 * @author liudong
 */
public class DlogTypeAction extends ActionBase {

	/**
	 * 返回指定类别下的所有子类别
	 * 生成的ＸＭＬ格式如下
	 * <pre>
	 * <?xml version=\"1.0\" encoding=\"UTF-8\"?>
	 * <type id="1" name="生活">
	 * 	<subTypes>
	 * 		<subType id="123" name="生活记录"/>
	 * 		<subType id="124" name="成长故事"/>
	 * 	</subTypes>
	 * </type>
	 * </pre>
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	protected void doListSubTypes(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, String s_type_id)
			throws Exception {
		int type_id = Integer.parseInt(s_type_id);
		TypeBean tbean = DlogTypeDAO.getTypeByID(type_id);
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		if(tbean==null)
			xml.append("<type-not-found/>");		
		else{
			List types = tbean.getSubTypes();
			xml.append("<type id=\"");
			xml.append(tbean.getId());
			xml.append("\" name=\"");
			xml.append(tbean.getName());
			xml.append("\">\r\n");
			xml.append("\t<subTypes>\r\n");
			for(int i=0;types!=null&&i<types.size();i++){
				TypeBean subType = (TypeBean)types.get(i);
				xml.append("\t\t<subType id=\"");
				xml.append(subType.getId());
				xml.append("\" name=\">");
				xml.append(subType.getName());
				xml.append("\"/>\r\n");
			}
			xml.append("\t</subTypes>\r\n");
			xml.append("</type>");
		}
		res.setContentType("text/xml;charset=UTF-8");
		res.getWriter().print(xml.toString());
	}

}
