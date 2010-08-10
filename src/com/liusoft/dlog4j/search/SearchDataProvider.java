/*
 *  SearchDataProvider.java
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
package com.liusoft.dlog4j.search;

import java.util.Date;
import java.util.List;

/**
 * 用于提供读取要写入搜索的数据查询接口
 * <p>
 * <b>花开之际逢君，花落之际思君</b>
 * </p>
 * @see com.liusoft.dlog4j.dao.BBSReplyDAO
 * @see com.liusoft.dlog4j.dao.BBSTopicDAO
 * @see com.liusoft.dlog4j.dao.DiaryDAO
 * @see com.liusoft.dlog4j.search.DiaryReplyProvider
 * @see com.liusoft.dlog4j.search.PhotoReplyProvider
 * @see com.liusoft.dlog4j.dao.MusicDAO
 * @see com.liusoft.dlog4j.dao.PhotoDAO
 * @author Winter Lau
 */
public interface SearchDataProvider {
	
	/**
	 * 读取在指定时间点后的数据
	 * @param beginTime
	 * @return 返回实现了SearchEnabled接口的对象列表
	 * @throws Exception
	 */
	public List fetchAfter(Date beginTime) throws Exception;

}
