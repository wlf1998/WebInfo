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

        Directory dir = FSDirectory.open(Paths.get(indexDir)); //��ȡҪ��ѯ��·����Ҳ�����������ڵ�λ��
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(); //��׼�ִ��������Զ�ȥ���ո񰡣�is a the�ȵ���
        QueryParser parser = new QueryParser("contents", analyzer); //��ѯ������
        Query query = parser.parse(q); //ͨ������Ҫ��ѯ��String����ȡ��ѯ����

        long startTime = System.currentTimeMillis(); //��¼������ʼʱ��
        TopDocs docs = searcher.search(query,100);//��ʼ��ѯ����ѯǰ10�����ݣ�����¼������docs��
        long endTime = System.currentTimeMillis(); //��¼��������ʱ��
        System.out.println("ƥ��" + q + "����ʱ" + (endTime-startTime) + "����");
        System.out.println("��ѯ��" + docs.totalHits + "����¼");

        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color=red>","</font></b>"); //�����ָ�������Ļ���Ĭ���ǼӴ֣���<b><b/>
        QueryScorer scorer = new QueryScorer(query);//����÷֣����ʼ��һ����ѯ�����ߵĵ÷�
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer); //��������÷ּ����һ��Ƭ��
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
        highlighter.setTextFragmenter(fragmenter); //����һ��Ҫ��ʾ��Ƭ��

        ArrayList<Map<String, String>> result = new ArrayList<>();
        
        for(ScoreDoc scoreDoc : docs.scoreDocs) { //ȡ��ÿ����ѯ���
            Document doc = searcher.doc(scoreDoc.doc); //scoreDoc.doc�൱��docID,�������docID����ȡ�ĵ�
            System.out.println(doc.get("fullPath")); //fullPath�Ǹոս���������ʱ�����Ƕ����һ���ֶ�
            
            
            String contents=doc.get("contents");
            String contentsH="";  
            
          //��ʾ����
            if( contents!= null) {
                TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(contents));
                contentsH = highlighter.getBestFragment(tokenStream, contents);
                System.out.println(contentsH);
            }
            //���÷���ֵ
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
        String q = "Conceptual Structures"; //��ѯ����ַ���
        try {
            search(indexDir, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

