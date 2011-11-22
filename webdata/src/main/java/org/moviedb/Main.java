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
	
	public static void testIMDB() throws IOException{
		XSLT xslt = new XSLT("schema/imdb.xslt");
		

		BufferedReader movies = new BufferedReader(
				new InputStreamReader(
						new DataInputStream(
								new FileInputStream("movies.txt")
								)
						)
				);
		
		String movie, name;
		  
		while ((movie = movies.readLine()) != null)   {
			System.out.println(movie);
			name = URLEncoder.encode(movie, "UTF-8");
			
			ByteArrayOutputStream cleaned = new ByteArrayOutputStream();
			StreamResult result = new StreamResult("xml/"+name+".xml");

			// connect to imdb
			URL url = new URL("http://www.imdb.com/find?q=" + name);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", userAgent);
			InputStream html = conn.getInputStream();
			
			// clean
			xslt.clean(html, cleaned);
			InputStream buffer = new ByteArrayInputStream(
					cleaned.toByteArray());

			// transform
			System.out.println("transform...");
			xslt.tr(buffer, result);
		}
	}
	
	public static void main(String[] args) {

		try {
			//testRT();
			testIMDB();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
