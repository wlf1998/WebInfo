package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONObject;

@SuppressWarnings("unchecked")
public class Indexer {

    private IndexWriter writer; //д����ʵ��
    private int FileNum=0;

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

    //��������mainPage3.dataָ��������
    public int indexAll() throws Exception {
        HashSet<String>set3;
        try {
			FileInputStream fis3 = new FileInputStream("D:\\document\\web\\data\\record\\mainPage3.data");
			ObjectInputStream ois3 = new ObjectInputStream(fis3);
			set3=(HashSet<String>)ois3.readObject();
			ois3.close();
        }catch (Exception e){
			set3=new HashSet<String>();
        }
        HashSet<String>setTmp=new HashSet<>();
        
        
        int stop=0;
        System.out.println(set3.size());
        for(String name : set3) {
            indexFile(name); //���������indexFile��������ÿ���ļ���������
            setTmp.add(name);
            stop++;
            //if(stop>5) {
            //	
            //	break;
            //}
        }
        FileNum=stop;
        /*
        set3.removeAll(setTmp);
        try {
			FileOutputStream fos3 = new FileOutputStream("D:\\document\\web\\data\\record\\mainPage3.data");
			ObjectOutputStream oos3 = new ObjectOutputStream(fos3);
			
			oos3.writeObject(set3);
			oos3.flush();
			oos3.close();
			
		}catch (IOException e)
		{
			e.printStackTrace();
		}
        
        */
        
        return writer.numDocs(); //�����������ļ���
        
    }

    //����ָ�����ļ�
    private void indexFile(String name) throws Exception {
        System.out.println("�����ļ���·����" + name);
        Document doc = getDocument(name); //��ȡ���ļ���document
        writer.addDocument(doc); //���������getDocument��������doc��ӵ�������
    }

    //��ȡ�ĵ����ĵ���������ÿ���ֶΣ������������ݿ��е�һ�м�¼
    private Document getDocument(String name) throws Exception{
        Document doc = new Document();
        //����ֶ�
        
        //String line="";
        String textPath="D:\\document\\web\\data\\text\\"+name+".json";
        String entityPath="D:\\document\\web\\data\\entityMention\\"+name+".json";
        
        JSONObject textoj=JsonFile.readFile(textPath);
        JSONObject entityoj=JsonFile.readFile(entityPath);
               
        
        String sent,type,from,subject,url,deadline,webPage,contents;
        if(textoj.has("sent"))
        	sent=textoj.getString("sent");
        else
        	sent=" ";
        
        if(textoj.has("type"))
        	type=textoj.getString("type");
        else
        	type=" ";
        
        if(textoj.has("from"))
        	from=textoj.getString("from");
        else
        	from=" ";
        
        if(textoj.has("subject"))
        	subject=textoj.getString("subject");
        else
        	subject=" ";
        
        if(textoj.has("url"))
        	url=textoj.getString("url");
        else
        	url=" ";
        
        if(textoj.has("deadline"))
        	deadline=textoj.getString("deadline");
        else
        	deadline=" ";
        
        if(textoj.has("webPage"))
        	webPage=textoj.getString("webPage");
        else
        	webPage=" ";
        
        if(textoj.has("contents"))
        	contents=textoj.getString("contents");
        else
        	contents=" ";
        
        //String PERSON,ORGANIZATION, MISC,CITY,COUNTRY;
        String MISC=" ",CITY=" ",COUNTRY=" ";
        if(entityoj.has("CITY")) {
        	JSONObject jo=(JSONObject)entityoj.get("CITY");
        	for(String s :jo.keySet()) {
        		CITY=CITY+" "+s;
        	}
        }
        
        if(entityoj.has("MISC")) {
        	JSONObject jo=(JSONObject)entityoj.get("MISC");
        	for(String s :jo.keySet()) {
        		MISC=MISC+" "+s;
        	}
        }
        
        if(entityoj.has("COUNTRY")) {
        	JSONObject jo=(JSONObject)entityoj.get("COUNTRY");
        	for(String s :jo.keySet()) {
        		COUNTRY=COUNTRY+" "+s;
        	}
        }
        
        
        
/*
named (PERSON, LOCATION, ORGANIZATION, MISC), 
numerical (MONEY, NUMBER, ORDINAL, PERCENT),
temporal (DATE, TIME, DURATION, SET) 
entities (12 classes). 
*********
additional entity classes 
EMAIL, URL, CITY, STATE_OR_PROVINCE, COUNTRY, NATIONALITY, RELIGION, (job) TITLE, IDEOLOGY, CRIMINAL_CHARGE, CAUSE_OF_DEATH 
(11 classes) for a total of 23 classes. 
*/
      //day,month,year
        int[]sentD=toDate(sent);
        int[]deadlineD=toDate(deadline);
        
        //sent,type,from,subject,url,deadline,webPage,contents;
        //MISC=" ",CITY=" ",COUNTRY=" ";
        doc.add(new TextField("MISC", MISC,Field.Store.YES));
        doc.add(new TextField("CITY", CITY,Field.Store.YES));
        doc.add(new TextField("COUNTRY", COUNTRY,Field.Store.YES));
        
        doc.add(new TextField("sent",sent ,Field.Store.YES));
        doc.add(new IntPoint("sentD", sentD));
        //doc.add(new IntField("sentM", sentD[1],Field.Store.YES));
        //doc.add(new IntField("sentY", sentD[2],Field.Store.YES));
        
        
        
        doc.add(new TextField("type", type,Field.Store.YES));
        doc.add(new TextField("from",from ,Field.Store.YES));
        doc.add(new TextField("subject",subject ,Field.Store.YES));
        doc.add(new TextField("url", url,Field.Store.YES));
        
        doc.add(new TextField("deadline", deadline,Field.Store.YES));
        doc.add(new IntPoint("deadlineD", deadlineD));
        
        doc.add(new TextField("webPage",webPage ,Field.Store.YES));
        doc.add(new TextField("contents", contents,Field.Store.YES)); //�������
        doc.add(new TextField("fileName", name, Field.Store.YES)); //����ļ�������������ֶδ浽�����ļ���
        doc.add(new TextField("fullPath", textPath, Field.Store.YES)); //����ļ�·��
        
        
        System.out.println("sent: "+sentD[0]+" "+sentD[1]+" "+sentD[2]+" "+sent
        		+"\ntype: "+type
        		+"\nfrom: "+from+"\nsubject: "+subject
        		+"\nurl: "+url+"\ndeadline: "
        		+deadlineD[0]+" "+deadlineD[1]+" "+deadlineD[2]+" "+deadline
        		+"\nwebPage: "+webPage+"\ncontents: "+contents
        		+"\nMISC: "+MISC+"\nCITY: "+CITY+"\nCOUNTRY: "+COUNTRY+"\n\n\n");
        
        return doc;
        
    }
    
    public int[]toDate(String s){
    	if(s.length()<11) {
    		int[]t= {0,0,0};
    		return t;
    		
    	}
    		
    	int[]date=new int[3];//day,month,year
    	String[]b=s.split("-");

    	int month;
		switch(b[1].trim()) {
		case "Jan":month=1;break;
		case "Feb":month=2;break;
		case "Mar":month=3;break;
		case "Apr":month=4;break;
		case "May":month=5;break;
		case "Jun":month=6;break;
		case "Jul":month=7;break;
		case "Aug":month=8;break;
		case "Sep":month=9;break;
		case "Oct":month=10;break;
		case "Nov":month=11;break;
		case "Dec":month=12;break;
		default:month=0;
		}
		
		date[0]=Integer.parseInt(b[0].trim());
		date[1]=month;
		date[2]=Integer.parseInt(b[2].trim());
		return date;
    }

    public static void start() {
        String indexDir = "D:\\document\\web\\data\\index\\"; //���������浽��·��
        //String dataDir = "D:\\document\\web\\data\\text\\"; //��Ҫ�������ļ����ݴ�ŵ�Ŀ¼
        for(File f:(new File(indexDir)).listFiles()) {
        	f.delete();
        }
        
        
        
        Indexer indexer = null;
        int indexedNum = 0;
        long startTime = System.currentTimeMillis(); //��¼������ʼʱ��
        try {
            indexer = new Indexer(indexDir);
            indexer.indexAll();
            indexedNum = indexer.FileNum;
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
    
    public static void main(String[] args) {
    	start();
    }
}
