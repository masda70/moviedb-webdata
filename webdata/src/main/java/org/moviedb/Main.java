package org.moviedb;

import java.io.*;
import java.net.*;
import javax.xml.transform.stream.StreamResult;

public class Main {

	public static void main(String[] args) {
		
		try {
			XSLT xslt = new XSLT();
			
			System.out.println("connexion...");
			URL url = new URL("http://www.imdb.com/title/tt0068646/");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/A.B (KHTML, like Gecko) Chrome/X.Y.Z.W Safari/A.B.");
	
			InputStream html = conn.getInputStream();
			ByteArrayOutputStream cleaned = new ByteArrayOutputStream();
			StreamResult result = new StreamResult("result.xml");
			
			System.out.println("clean...");
			xslt.clean(html, cleaned);
			InputStream buffer = new ByteArrayInputStream(
					cleaned.toByteArray());


			System.out.println("transform...");
			xslt.tr(buffer, result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
