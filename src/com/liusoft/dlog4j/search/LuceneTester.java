/*
 * 版权所有: 摩网信息科技有限公司 2005
 * 项目：DLOG4J_V3
 * 所在包：com.liusoft.dlog4j.lucene
 * 文件名：LuceneTester.java
 * 创建时间：2005-10-24
 * 创建者：Winter Lau
 */
package com.liusoft.dlog4j.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;

/**
 * useless, just for test
 * @author Winter Lau
 */
public class LuceneTester {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		addIndex();
		IndexReader reader = IndexReader.open(lucenePath);
		System.out.println("文档数:"+reader.numDocs());
		TermEnum tes = reader.terms();
		while(tes.next()){
			Term t = tes.term();
			System.out.println(t.toString());
		}
		//IndexSearcher searcher = new IndexSearcher(lucenePath);
	}

    /**
     * 增加索引
     */
    protected static int addIndex() throws IOException {
		Document doc = new Document();
		doc.add(new Field("id", "1", Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field("author", "刘冬IBM OSI一一二二", Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("time", Long.toString(System.currentTimeMillis()), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field("content", "不知道从什么时候开始，在什么东西上面都有个日期，秋刀鱼会过期，肉罐头会过期，连保鲜纸都会过期，我开始怀疑，在这个世界上，还有什么东西是不会过期的？", Field.Store.NO, Field.Index.TOKENIZED));
		IndexWriter writer = getWriter();
		try {
		    writer.addDocument(doc);
		    writer.optimize();
		}finally {
		    writer.close();
		}
		System.out.println("doc count = " + writer.docCount());
        return 1;
    }
    /* (non-Javadoc)
     * @see jdlog.search.SearchProxy#getWriter()
     */
    protected static IndexWriter getWriter() throws IOException{
        File rp = new File(lucenePath);
        if(!rp.exists())
            rp.mkdirs();
        int wc = 0;
        while(wc<10 && IndexReader.isLocked(lucenePath)){
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return null;
			}
			wc++;
        }
        File segments = new File(lucenePath + File.separator + SEGMENTS);
        boolean bCreate = !segments.exists();
        return new IndexWriter(lucenePath,new StandardAnalyzer(),bCreate);
    }
    
    public final static String SEGMENTS = "segments";
    protected static String lucenePath = "D:\\lucene";
}
