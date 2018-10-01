package base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    public static ArrayList<Map<String,String>> search(String indexDir, String q) throws Exception {

        Directory dir = FSDirectory.open(Paths.get(indexDir)); //获取要查询的路径，也就是索引所在的位置
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(); //标准分词器，会自动去掉空格啊，is a the等单词
        QueryParser parser = new QueryParser("contents", analyzer); //查询解析器
        Query query = parser.parse(q); //通过解析要查询的String，获取查询对象

        long startTime = System.currentTimeMillis(); //记录索引开始时间
        TopDocs docs = searcher.search(query,100);//开始查询，查询前10条数据，将记录保存在docs中
        long endTime = System.currentTimeMillis(); //记录索引结束时间
        System.out.println("匹配" + q + "共耗时" + (endTime-startTime) + "毫秒");
        System.out.println("查询到" + docs.totalHits + "条记录");

        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color=red>","</font></b>"); //如果不指定参数的话，默认是加粗，即<b><b/>
        QueryScorer scorer = new QueryScorer(query);//计算得分，会初始化一个查询结果最高的得分
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer); //根据这个得分计算出一个片段
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
        highlighter.setTextFragmenter(fragmenter); //设置一下要显示的片段

        ArrayList<Map<String, String>> result = new ArrayList<>();
        
        for(ScoreDoc scoreDoc : docs.scoreDocs) { //取出每条查询结果
            Document doc = searcher.doc(scoreDoc.doc); //scoreDoc.doc相当于docID,根据这个docID来获取文档
            System.out.println(doc.get("fullPath")); //fullPath是刚刚建立索引的时候我们定义的一个字段
            
            
            String contents=doc.get("contents");
            String contentsH="";  
            
          //显示高亮
            if( contents!= null) {
                TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(contents));
                contentsH = highlighter.getBestFragment(tokenStream, contents);
                System.out.println(contentsH);
            }
            //设置返回值
            Map<String,String> map = new HashMap<>();
            map.put("path", doc.get("fullPath"));
            map.put("contents", contentsH);
            result.add(map);
        }
        reader.close();
        return result;
    }

    public static void main(String[] args) {
        String indexDir =  "D:\\document\\web\\data\\index\\";
        String q = "Conceptual Structures"; //查询这个字符串
        try {
            search(indexDir, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

