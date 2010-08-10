/*
 *  ODBCDialect.java
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
package com.liusoft.util.db;

import org.hibernate.dialect.Dialect;

/**
 * 用于ODBC数据源的Dialect类
 * <p>
 * 由于Hibernate3的GenericDialect类已经不存在
 * 对于使用Access数据库请使用该Dialect类，否则为了性能考虑请使用数据库相应的Dialect类
 * </p>
 * <p>
 * 详见Hibernate的参考手册，或者是org.hibernate.dialect包
 * </p>
 * @author Winter Lau
 */
public class ODBCDialect extends Dialect {

}
