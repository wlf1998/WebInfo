package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Indexer {

    private IndexWriter writer; //д����ʵ��

    //���췽����ʵ����IndexWriter
    public Indexer(String indexDir) throws Exception {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        Analyzer analyzer = new StandardAnalyzer(); //��׼�ִ��������Զ�ȥ���ո񰡣�is a the�ȵ���
        IndexWriterConfig config = new IndexWriterConfig(analyzer); //����׼�ִ����䵽д������������
        writer = new IndexWriter(dir, config); //ʵ����д��������
    }

    //�ر�д����
    public void close() throws Exception {
        writer.close();
    }

    //����ָ��Ŀ¼�µ������ļ�
    public int indexAll(String dataDir) throws Exception {
        File[] files = new File(dataDir).listFiles(); //��ȡ��·���µ������ļ�
        for(File file : files) {
            indexFile(file); //���������indexFile��������ÿ���ļ���������
        }
        return writer.numDocs(); //�����������ļ���
    }

    //����ָ�����ļ�
    private void indexFile(File file) throws Exception {
        System.out.println("�����ļ���·����" + file.getCanonicalPath());
        Document doc = getDocument(file); //��ȡ���ļ���document
        writer.addDocument(doc); //���������getDocument��������doc��ӵ�������
    }

    //��ȡ�ĵ����ĵ���������ÿ���ֶΣ������������ݿ��е�һ�м�¼
    private Document getDocument(File file) throws Exception{
        Document doc = new Document();
        //����ֶ�
        
        String line="";
        String contents="";
        try
        {
                BufferedReader in=new BufferedReader(new FileReader(file));
                line=in.readLine();
                while (line!=null)
                {
                        contents+=line;
                        line=in.readLine();
                }
                in.close();
        } catch (IOException e)
        {
                e.printStackTrace();
        }
        
        doc.add(new TextField("contents", contents,Field.Store.YES)); //�������
        doc.add(new TextField("fileName", file.getName(), Field.Store.YES)); //����ļ�������������ֶδ浽�����ļ���
        doc.add(new TextField("fullPath", file.getCanonicalPath(), Field.Store.YES)); //����ļ�·��
        return doc;
    }

    public static void main(String[] args) {
        String indexDir = "D:\\document\\web\\data\\index\\"; //���������浽��·��
        String dataDir = "D:\\document\\web\\data\\text\\"; //��Ҫ�������ļ����ݴ�ŵ�Ŀ¼
        Indexer indexer = null;
        int indexedNum = 0;
        long startTime = System.currentTimeMillis(); //��¼������ʼʱ��
        try {
            indexer = new Indexer(indexDir);
            indexedNum = indexer.indexAll(dataDir);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                indexer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis(); //��¼��������ʱ��
        System.out.println("������ʱ" + (endTime-startTime) + "����");
        System.out.println("��������" + indexedNum + "���ļ�");
    }
}
