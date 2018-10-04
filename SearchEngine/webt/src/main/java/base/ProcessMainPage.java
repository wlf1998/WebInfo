package base;
//import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import java.io.*;

import us.codecraft.webmagic.Page;
//import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
//import us.codecraft.webmagic.Task;
//import us.codecraft.webmagic.pipeline.Pipeline;
//import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;



//��ȡ���б�
//��Ϣ����json
//ֻδ��ȡ��
//mainPage.data ��ʾ��ǰ����ȡ��url/filename
//mainPage2.data ��Ҫʹ��entityMention�����file
//mainPage3.data �Ѿ���entityMention�����굫��û�н���������file
@SuppressWarnings("unchecked")
public class ProcessMainPage implements PageProcessor {

    private Site site = Site.me().setRetryTimes(10).setSleepTime(1000).setCharset("utf-8");
    private Set<String> set;
    private Set<String> set2;//for entityMention
    private boolean MainPageFlag=false;
    
    int count=1;

    @Override
    public void process(Page page) {  
    	
    	Html html=page.getHtml();
    	String article=html.xpath("/html/body/pre/text()").toString();   
    	
    	
    	if(article!=null)
    	{
    		
    		String url=page.getUrl().toString();
    		String []splitUrl=url.split("/|\\.");
			String name=splitUrl[8]+"-"+splitUrl[9];
			

			JSONObject jo=JsonFile.readFile("D:\\document\\web\\data\\text\\"+name+".json");
			jo.put("contents", article);
			JsonFile.writeFile("D:\\document\\web\\data\\text\\"+name+".json", jo);
			    		
    	}
    	else if(MainPageFlag==false)
    	{
    		int nullUrl=0;
    		for(int i=1;;i++)
    		{
    			String url=html.xpath("/html/body/table/tbody["+i+"]/tr/td[4]/a/@href").toString();
    			if(url==null)
    			{	
    				nullUrl++;
    				continue;
    			}
    			
    			String []splitUrl=url.split("/|\\.");
    			String name=splitUrl[8]+"-"+splitUrl[9];
    			
    			
    			if(set.contains(name)||nullUrl>5)
    				break;
    			  			
    			System.out.println(name);
    			set.add(name);
    			set2.add(name);
    			
    			page.addTargetRequest(url);
    			
    			String sent=html.xpath("/html/body/table/tbody["+i+"]/tr/td[1]/text()").toString();
    			String type=html.xpath("/html/body/table/tbody["+i+"]/tr/td[2]/text()").toString();
    			String from=html.xpath("/html/body/table/tbody["+i+"]/tr/td[3]/text()").toString();
    			String subject=html.xpath("/html/body/table/tbody["+i+"]/tr/td[4]/a/text()").toString();
    			String deadline=html.xpath("/html/body/table/tbody["+i+"]/tr/td[5]/text()").toString();
    			String webPage=html.xpath("/html/body/table/tbody["+i+"]/tr/td[6]/a/@href").toString();
    			System.out.println(subject);
    			
    			JSONObject jo=new JSONObject();
    			jo.put("name",name);
    			jo.put("sent",sent);
    			jo.put("type",type);
    			jo.put("from",from);
    			jo.put("subject",subject);
    			jo.put("url",url);
    			jo.put("deadline",deadline);
    			jo.put("webPage",webPage);
    			jo.put("contents","");
    			
    			JsonFile.writeFile("D:\\document\\web\\data\\text\\"+name+".json", jo);
    			
    		}
    		MainPageFlag=true;
    		
    	}
    	
    }

    @Override
    public Site getSite() {
        return site;
    }
    
    public void over()
    {
    	try {
			FileOutputStream fos = new FileOutputStream("D:\\document\\web\\data\\record\\mainPage.data");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(set);
			oos.flush();
			oos.close();
			
			//for entityMention
			FileOutputStream fos2 = new FileOutputStream("D:\\document\\web\\data\\record\\mainPage2.data");
			ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
			
			oos2.writeObject(set2);
			oos2.flush();
			oos2.close();
			
		}catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
    public static void start() {
    	ProcessMainPage pmp=new ProcessMainPage();
    	//pmp.set=new HashSet<String>();
    	try {
			FileInputStream fis = new FileInputStream("D:\\document\\web\\data\\record\\mainPage.data");
			ObjectInputStream ois = new ObjectInputStream(fis);
			pmp.set=(HashSet<String>)ois.readObject();
			ois.close();
			
			
		}catch (Exception e){
			pmp.set=new HashSet<String>();
		}
    	
    	try {
    		//for entityMention
			FileInputStream fis2 = new FileInputStream("D:\\document\\web\\data\\record\\mainPage2.data");
			ObjectInputStream ois2 = new ObjectInputStream(fis2);
			pmp.set2=(HashSet<String>)ois2.readObject();
			ois2.close();
    	}catch (Exception e){
			pmp.set2=new HashSet<String>();
		}
    	
    	Spider.create(pmp)
        .addUrl("https://research.cs.wisc.edu/dbworld/browse.html")
        //.addPipeline(new JsonFilePipeline("D:\\document\\web\\file"))
        .thread(5)
        .run();  
    	
    	pmp.over();
    }
    
    
    
    public static void main(String[] args) {
          	start();
    }
}

