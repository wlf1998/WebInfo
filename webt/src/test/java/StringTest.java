public class StringTest{
	public static void main(String[]args)
	{
		String a="https://research.cs.wisc.edu/dbworld/messages/2018-10/1538439952.html";
		String []b=a.split("/|\\.");
		for(String s:b)
		{
			System.out.println(s);
		}
		System.out.println(b[8]+"-"+b[9]);
		System.out.println("https://research.cs.wisc.edu/dbworld/messages/"+b[8]+"/"+b[9]+".html");
				
	}
}