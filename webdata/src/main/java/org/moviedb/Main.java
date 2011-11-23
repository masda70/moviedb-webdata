package org.moviedb;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import net.sf.saxon.s9api.SaxonApiException;

public class Main {

	
	
	public static void testRT() throws IOException{
		
		String fileOut = "rottentomatoes.xml";
		try {
			FileOutputStream fos;
			
			fos = new FileOutputStream(fileOut);
			OutputStream out = new BufferedOutputStream(fos);
			RTProcessor rt = new RTProcessor(out);

			try {
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
					name = movie;
					
					rt.processEntry(name);
				
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			throw new IOException("Error: Cannot write to file "+fileOut);
		}
	}
	
	public static void testIMDB() throws IOException, SaxonApiException{
		XSLT xslt = new XSLT("schema/imdb.xslt");
		WebXMLExtractor web = new WebXMLExtractor();

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
		
			OutputStream result = new BufferedOutputStream(new FileOutputStream(new File("xml/"+name+".xml")));

			// get imdb page

			URL url = new URL("http://www.imdb.com/find?q=" + name);
		
			InputStream buffer = new ByteArrayInputStream(web.getURL(url).toByteArray());
			
			// transform
			System.out.println("transform...");
			
			xslt.transform(buffer, result);
		}
	}
	
	public static void main(String[] args) {
		/*
		try {
			testRT();
			testIMDB();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		try {
		
				IMDBMovieListExtractor movieExtractor= new IMDBMovieListExtractor();
			String year = "1960";
			int pageFrom = 0;
			int pageTo = 10;
			System.out.println("Extracting IMDB list, year "+ year +" pages from "+pageFrom+" to "+(pageTo-1)+".");
			ArrayList<URL> list = movieExtractor.getList(year, pageFrom, pageTo);
			String outputFile = "data/IMDB_y"+year+"("+pageFrom+","+(pageTo-1)+").object";
			System.out.println("Done. Output at "+outputFile+".");
			ObjectOutputStream oos;

			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(outputFile))));
		
			oos.writeObject(list);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
