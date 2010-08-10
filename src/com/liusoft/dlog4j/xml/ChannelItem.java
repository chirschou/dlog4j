/*
 *  ChannelItem.java
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
package com.liusoft.dlog4j.xml;

import org.apache.commons.digester.rss.Item;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.ImageTag;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.HtmlNodeFilters;

/**
 * 扩展频道中的文章，用于处理文章内容中可能出现的相对路径的处理
 * @author Winter Lau
 */
public class ChannelItem extends Item {
	
	private boolean need_link = false;
	private String temp_desc = null;
	
	/**
	 * 自动处理图片的相对路径
	 */
	public void setDescription(String desc) {
		if(desc==null)
			return;
		if(link!=null)
			super.setDescription(autoFormatImage(desc));
		else{
			need_link = true;
			temp_desc = desc;
		}
	}
	
	/**
	 * description的自动格式化依赖于link属性
	 */
	public void setLink(String link) {
		super.setLink(link);
		if(need_link && link!=null)
			super.setDescription(autoFormatImage(temp_desc));
	}

	public String getDescription() {
		return (need_link)?temp_desc:description;
	}

	/**
	 * 自动处理相对路径的图片地址
	 * @param desc
	 * @return
	 */
	protected String autoFormatImage(String desc){
		StringBuffer content = new StringBuffer();
		int last_pos = 0;
		try{
			Parser parser = new Parser();
			parser.setInputHTML(desc);
			parser.setEncoding(Globals.ENC_8859_1);
			Node[] images = parser.extractAllNodesThatMatch(HtmlNodeFilters.imageFilter).toNodeArray();
			for(int i=0;images!=null&&i<images.length;i++){
				int start_pos = images[i].getStartPosition();
				content.append(desc.substring(last_pos, start_pos));
				last_pos = images[i].getEndPosition();
				ImageTag img = (ImageTag)images[i];
				String img_url = img.getImageURL();
				if(!img_url.startsWith("http://")&&!img_url.startsWith("https://")){				
					if(img_url.startsWith("/")){
						int idx = link.indexOf("/", 7);
						img.setImageURL(link.substring(0, idx) + img_url);
					}
					else{
						int idx = link.lastIndexOf('/');
						img.setImageURL(link.substring(0, idx) + '/' + img_url);
					}
				}
				content.append(img.toHtml());
				//System.out.println(img.getImageURL());
			}
		}catch(Exception e){}
		content.append(desc.substring(last_pos));
		return content.toString();
	}

}
