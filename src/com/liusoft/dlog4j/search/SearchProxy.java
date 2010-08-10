/*
 *  SearchProxy.java
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
 *  
 */
package com.liusoft.dlog4j.search;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import com.liusoft.dlog4j.beans.DiaryBean;

/**
 * 搜索代理
 * 
 * <pre>
 * 碧水长天兮，昭昭日月不同弦
 * 知向谁边兮，点点渔火不同眠
 * 青山如黛兮，悠悠吴钩共秦剑
 * 孤舟一叶兮，化做了淡梦寒烟
 * </pre>
 * 
 * @author Winter Lau
 */
public class SearchProxy {
	
	private final static Log log = LogFactory.getLog(SearchProxy.class);
	
	public final static int MAX_RESULT_COUNT = 200;
	
	//用于存储对象的类名
	private final static String CLASSNAME_FIELD = "___className";
	
	private static String _baseIndexPath;
	
	public static void init(String _base_index_path){
		_baseIndexPath = _base_index_path;
		if(!_baseIndexPath.endsWith(File.separator))
			_baseIndexPath += File.separator;
	}
	
	/**
	 * 添加文档
	 * @param doc
	 * @throws Exception 
	 */
	public static synchronized void add(SearchEnabled doc) throws Exception {
		if(doc == null)
			return ;

		Document lucene_doc = new Document();
		
		//Set keyword field
		String key = getField(doc, doc.getKeywordField());
		lucene_doc.add(Keyword(doc.getKeywordField(), key));
		
		//Set identity(classname) of object
		lucene_doc.add(Keyword(CLASSNAME_FIELD, doc.getClass().getName()));
		
		//Set storage field
		String[] storeFields = doc.getStoreFields();
		for(int i=0;storeFields!=null && i<storeFields.length;i++){
			String propertyValue = getField(doc, storeFields[i]);
			if(propertyValue!=null)
				lucene_doc.add(Keyword(storeFields[i], propertyValue));
		}
		//Set indexed field
		String[] indexFields = doc.getIndexFields();
		for(int i=0;indexFields!=null && i<indexFields.length;i++){
			String propertyValue = getField(doc, indexFields[i]);
			lucene_doc.add(UnStored(indexFields[i], propertyValue));
		}
		//Write document
		IndexWriter writer = getWriter(doc.name());
		try {
		    writer.addDocument(lucene_doc);
		    writer.optimize();
		}finally {
			try{
				writer.close();
			}catch(Exception e){
				log.error("Error occur when closing IndexWriter", e);
			}finally{
				writer = null;
			}
			lucene_doc = null;
		}
		
	}
	
	/**
	 * 从索引中删除文档
	 * @param doc
	 * @return
	 * @throws Exception 
	 */
	public static synchronized int remove(SearchEnabled doc){
		if(doc == null)
			return -1;
		
		IndexReader reader = null;
		try{
			reader = getReader(doc.name());	
			String pvalue = getField(doc, doc.getKeywordField());
			Term keyTerm = new Term(doc.getKeywordField(), pvalue);
			return reader.deleteDocuments(keyTerm);
		}catch(Exception e){
			log.error("Error where delete index of "+ doc, e);
		}finally{
			if(reader!=null)
			try{
				reader.close();
			}catch(Exception e){
				log.error("Error occur when closing IndexReader", e);
			}finally{
				reader = null;
			}
		}
		return -1;
	}
	
	/**
	 * 更新文档的索引
	 * @param doc
	 * @return
	 * @throws Exception 
	 */
	public static void update(SearchEnabled doc) throws Exception{
		if(doc == null)
			return;
		remove(doc);
		add(doc);
	}
	
	/**
	 * 文档搜索
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public static List search(SearchParameter params) throws Exception{
		if(params == null)
			return null;
		
        SearchEnabled searching = (SearchEnabled)params.getSearchObject().newInstance();
        
    	StringBuffer path = new StringBuffer(_baseIndexPath);
    	path.append(searching.name());
    	File f = new File(path.toString());
    	if(!f.exists())
    		return null;
    	
    	IndexSearcher searcher = new IndexSearcher(path.toString());
    	
        //设置搜索关键字
        BooleanQuery comboQuery = new BooleanQuery();
        int _query_count = 0;
        StringTokenizer st = new StringTokenizer(params.getSearchKey());
        while(st.hasMoreElements()){
        	String q = st.nextToken();
			String[] indexFields = searching.getIndexFields();
			for(int i=0;i<indexFields.length;i++){
				QueryParser qp = new QueryParser(indexFields[i], analyzer);
				try{
					Query subjectQuery = qp.parse(q);
					comboQuery.add(subjectQuery, BooleanClause.Occur.SHOULD);
					_query_count ++;
				}catch(Exception e){
					log.error("Add query parameter failed. key="+q, e);
				}
			}
        }
        
        if(_query_count==0)//没有任何关键字条件
        	return null;
        
        //搜索的附加条件
        MultiFilter multiFilter = null;
        HashMap conds = params.getConditions();
		if(conds!=null){
			Iterator keys = conds.keySet().iterator();
			while(keys.hasNext()){
				if(multiFilter == null)
		        	multiFilter = new MultiFilter(0);
				String key = (String)keys.next();
				multiFilter.add(new FieldFilter(key,conds.get(key).toString()));
			}
		}
		
		/*
		 * Creates a sort, possibly in reverse,
		 * by terms in the given field with the type of term values explicitly given.
		 */
		SortField[] s_fields = new SortField[2];
		s_fields[0] = SortField.FIELD_SCORE;
		s_fields[1] = new SortField(searching.getKeywordField(), SortField.INT, true);
		Sort sort = new Sort(s_fields);
        
        Hits hits = searcher.search(comboQuery, multiFilter, sort);
		int numResults = hits.length();
		//System.out.println(numResults + " found............................");
		int result_count = Math.min(numResults, MAX_RESULT_COUNT);
		List results = new ArrayList(result_count);
		for(int i=0;i<result_count;i++){
			Document doc = (Document)hits.doc(i);
			//映射文档属性到Java对象中
			Object result = params.getSearchObject().newInstance();
			Enumeration fields = doc.fields();
			while(fields.hasMoreElements()){
				Field field = (Field)fields.nextElement();
				//System.out.println(field.name()+" -- "+field.stringValue());
				if(CLASSNAME_FIELD.equals(field.name()))
					continue;
				//索引字段不进行映射
				if(!field.isStored())
					continue;
				//System.out.println("=========== begin to mapping ============");
				//String --> anything
		    	Class fieldType = getNestedPropertyType(result, field.name());
		    	//System.out.println(field.name()+", class = " + fieldType.getName());
		    	Object fieldValue = null;
		    	if(fieldType.equals(Date.class))
		    		fieldValue = new Date(Long.parseLong(field.stringValue()));
		    	else
		    		fieldValue = ConvertUtils.convert(field.stringValue(), fieldType);
		    	//System.out.println(fieldValue+", class = " + fieldValue.getClass().getName());
		    	setNestedProperty(result, field.name(), fieldValue);
			}
			results.add(result);
		}
		
		return results;
	}
	
	/**
	 * 获取嵌套属性的类型
	 * @param obj
	 * @param field
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IntrospectionException 
	 */
	private static Class getNestedPropertyType(Object obj, String field)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NoSuchFieldException, IntrospectionException {
		StringTokenizer st = new StringTokenizer(field, ".");
		Class nodeClass = obj.getClass();
		while (st.hasMoreElements()) {
			String f = st.nextToken();
			PropertyDescriptor[] props = Introspector.getBeanInfo(nodeClass).getPropertyDescriptors();
			for(int i=0;i<props.length;i++){
				if(props[i].getName().equals(f)){
					nodeClass = props[i].getPropertyType();
					continue;
				}
			}
		}
		return nodeClass;
	}
	
	/**
	 * 设置字段值
	 * @param obj
	 * @param field
	 * @param value
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IntrospectionException 
	 * @throws InstantiationException 
	 */
	private static void setNestedProperty(Object obj, String field, Object value)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IntrospectionException,
			InstantiationException {
		StringTokenizer st = new StringTokenizer(field, ".");
		Class nodeClass = obj.getClass();
		StringBuffer tmp_prop = new StringBuffer();
		while (st.hasMoreElements()) {
			String f = st.nextToken();
			if(tmp_prop.length()>0)
				tmp_prop.append('.');
			tmp_prop.append(f);
			PropertyDescriptor[] props = Introspector.getBeanInfo(nodeClass)
					.getPropertyDescriptors();
			for (int i = 0; i < props.length; i++) {
				if (props[i].getName().equals(f)) {
					if(PropertyUtils.getNestedProperty(obj, tmp_prop.toString())==null){
						nodeClass = props[i].getPropertyType();
						PropertyUtils.setNestedProperty(obj, f, nodeClass
							.newInstance());
					}
					continue;
				}
			}
		}
		PropertyUtils.setNestedProperty(obj, field, value);
	}
	
	/**
	 * 获取索引读
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private static IndexReader getReader(String name) throws IOException{
    	StringBuffer path = new StringBuffer(_baseIndexPath);
    	path.append(name);
    	try{
			return IndexReader.open(path.toString());
    	}finally{
    		path = null;
    	}
	}
	
	/**
	 * 获取索引写
	 * @param name
	 * @return
	 * @throws IOException
	 */
    private static IndexWriter getWriter(String name) throws IOException{
    	StringBuffer path = new StringBuffer(_baseIndexPath);
    	path.append(name);
    	String index_path = path.toString();
        File rp = new File(index_path);
        if(!rp.exists())
            rp.mkdirs();
        int wc = 0;
        //waiting for the lock of indexes
        while(wc<10 && IndexReader.isLocked(index_path)){
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return null;
			}
			wc++;
        }
        path.append(File.separator);
        path.append(SEGMENTS);
        File segments = new File(path.toString());
        try{
	        boolean bCreate = !segments.exists();
	        return new IndexWriter(index_path,new StandardAnalyzer(),bCreate);
        }finally{
        	path = null;
        	segments = null;
        	rp = null;
        }
    }
    
    private final static String SEGMENTS = "segments";
    
    /**
     * 访问对象某个属性的值
     * @param obj
     * @param field
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private static String getField(Object obj, String field) 
    	throws IllegalAccessException, 
    		   InvocationTargetException, 
    		   NoSuchMethodException 
    {
    	try{
	    	Object fieldValue = PropertyUtils.getNestedProperty(obj, field);
			if(fieldValue instanceof String)
				return (String)fieldValue;
			if(fieldValue instanceof Date)
				return Long.toString(((Date)fieldValue).getTime());
			return String.valueOf(fieldValue);
    	}catch(NestedNullException e){}
		return null;
    }

    private static StandardAnalyzer analyzer = new StandardAnalyzer();
    
    protected static final Field Keyword(String name, String value) {
        return new Field(name, value, Field.Store.YES, Field.Index.UN_TOKENIZED);
	}
    
    protected static final Field Text(String name, String value){
    	return new Field(name, value, Field.Store.YES, Field.Index.TOKENIZED);
    }
    
    protected static final Field UnStored(String name, String value){
    	return new Field(name, value, Field.Store.NO, Field.Index.TOKENIZED);
    }
    /**
     * 类私有方法的测试
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
    	//嵌套属性读取    	
    	DiaryBean log = new DiaryBean();
    	/*
    	log.setAuthor("Winter Lau");
    	System.out.println(getField(log, "author"));
    	log.setOwner(new UserBean(123));
    	log.getOwner().setNickname("红薯");
    	System.out.println(getField(log, "owner.nickname"));
    	
    	Class ft = PropertyUtils.getPropertyType(log, "owner.id");
    	Object fv = ConvertUtils.convert("119", ft);
    	PropertyUtils.setNestedProperty(log, "owner.id", fv);
    	System.out.println(getField(log, "owner.id"));
    	*/
    	setNestedProperty(log,"site.title", "JavaYou");
    	setNestedProperty(log,"site.friendlyName", "Java自由人");
    	System.out.println(getNestedPropertyType(log,"site.id").getName());
    	System.out.println(log.getSite().getTitle());
    	System.out.println(log.getSite().getFriendlyName());
    }
}
