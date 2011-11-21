package org.moviedb;

import java.io.*;
import java.net.*;
import javax.xml.transform.stream.StreamResult;

public class Main {

	static String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/A.B (KHTML, like Gecko) Chrome/X.Y.Z.W Safari/A.B.";
	
	public static void testRT() throws IOException{
		
		String fileOut = "rottentomatoes.xml";
		try {
			FileOutputStream fos;
			
			fos = new FileOutputStream(fileOut);
			OutputStream out = new BufferedOutputStream(fos);
			RTProcessor rt = new RTProcessor(userAgent,out);
			
			try {
				rt.processEntry("Titanic", "1997");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			throw new IOException("Error: Cannot write to file "+fileOut);
		}

	}
	
	public static void testXSLT() throws IOException{
			XSLT xslt = new XSLT();
			
			System.out.println("connexion...");
			URL url = new URL("http://www.imdb.com/title/tt0068646/");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", userAgent);
	
			InputStream html = conn.getInputStream();
			ByteArrayOutputStream cleaned = new ByteArrayOutputStream();
			StreamResult result = new StreamResult("result.xml");
			
			System.out.println("clean...");
			xslt.clean(html, cleaned);
			InputStream buffer = new ByteArrayInputStream(
					cleaned.toByteArray());


			System.out.println("transform...");
			xslt.tr(buffer, result);
			

	}
	
	public static void main(String[] args) {

		try {
			testRT();
			testXSLT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
