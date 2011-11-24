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
	
	public static void IMDBparseMovies(ArrayList<URL> moviesURL) throws IOException, SaxonApiException{
		XSLT xslt_movie = new XSLT("schema/imdb.xslt");
		XSLT xslt_review = new XSLT("schema/imdb_review.xslt");
		WebXMLExtractor web = new WebXMLExtractor();

		for(URL movieURL: moviesURL){
			String title = movieURL.getPath();
			
			title = title.substring(6, title.length()-1);
			OutputStream movie = new BufferedOutputStream(new FileOutputStream(new File("data/imdb/"+title+".xml")));
			xslt_movie.transform(new ByteArrayInputStream(web.getURL(movieURL).toByteArray()), movie);
			URL reviewURL = new URL(movieURL+"reviews");

			OutputStream review = new BufferedOutputStream(new FileOutputStream(new File("data/imdb/"+title+"_review.xml")));
			xslt_review.transform(new ByteArrayInputStream(web.getURL(reviewURL).toByteArray()), review);
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void IMDBparseMoviesFromFile(String fileName){
		try {
			ObjectInputStream ios;
			ios = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(fileName))));
			ArrayList<URL> readObject = (ArrayList<URL>)ios.readObject();
			IMDBparseMovies(readObject);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	
	
	public void extractIMDBMovieList(String year, int pageFrom, int pageTo){
		try {
			IMDBMovieListExtractor movieExtractor= new IMDBMovieListExtractor();

			System.out.println("Extracting IMDB list, year "+ year +" pages from "+pageFrom+" to "+(pageTo-1)+".");
			ArrayList<URL> list = movieExtractor.getList(year, pageFrom, pageTo);
			String outputFile = "data/IMDB_y"+year+"("+pageFrom+","+(pageTo-1)+").object";
			System.out.println("Done, extracted "+list.size()+" movie URLs. Output at "+outputFile+".");
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
	
	public static void main(String[] args) {
		IMDBparseMoviesFromFile("data/IMDB_y1960(0,9).object");
	}

}
