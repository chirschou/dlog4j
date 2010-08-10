/*
 * 版权所有: 摩网信息科技有限公司 2005
 * 项目：DLOG4J_V3
 * 所在包：com.liusoft.dlog4j
 * 文件名：HtmlNodeFilters.java
 * 创建时间：2005-12-21
 * 创建者：Winter Lau
 */
package com.liusoft.dlog4j;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TitleTag;

/**
 * 常用的HTML标签过滤
 * @author Winter Lau
 */
public class HtmlNodeFilters {

	/**
	 * 用于提取页面的图像
	 * @author Winter Lau
	 */
	public final static NodeFilter imageFilter = new NodeFilter() {
		public boolean accept(Node node) {
			return (node instanceof ImageTag);
		}		
	};
	/**
	 * 用于提取页面的表格
	 * @author Winter Lau
	 */
	public final static NodeFilter tableFilter = new NodeFilter() {
		public boolean accept(Node node) {
			return (node instanceof TableTag);
		}		
	};
	/**
	 * 用于提取页面的标题
	 * @author Winter Lau
	 */
	public final static NodeFilter titleFilter = new NodeFilter() {
		public boolean accept(Node node) {
			return (node instanceof TitleTag);
		}		
	};
}
