package org.moviedb;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.DOMDestination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

public class Main {

		
	public static void extractMovies(ArrayList<URL> moviesURL, int startAt){
		try{
		XSLT xslt_movie = new XSLT("schema/imdb.xslt");
		XSLT xslt_review = new XSLT("schema/imdb_review.xslt");
		XPath xpath_title = new XPath("movie/original_title/text()");
		XPath xpath_year = new XPath("movie/year/text()");
		WebXMLExtractor web = new WebXMLExtractor();
		RTProcessor rt = new RTProcessor();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setIgnoringElementContentWhitespace(true);
		 DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		net.sf.saxon.s9api.DocumentBuilder saxDocBuilder =  SAXProcessor.getProcessor().newDocumentBuilder();
		 saxDocBuilder.setLineNumbering(true);
		 saxDocBuilder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
		 int count = 0;
		 boolean b = true;
		for(URL movieURL: moviesURL){
			count++;
			if(b && startAt != count) continue; else b = false;
				
			try {
				Document imdb_movie = docBuilder.newDocument();
		        DOMDestination dest = new DOMDestination(imdb_movie);
				String title = movieURL.getPath();
				
				title = title.substring(6, title.length()-1);

				
				System.out.print("#"+count+": Querying "+movieURL+"...");
				ByteArrayOutputStream ms= web.getURL(movieURL);
				System.out.println("done downloading.");
				
				System.out.print("XSLT parsing file...");
				xslt_movie.transform(new ByteArrayInputStream(ms.toByteArray()),dest);
				System.out.println("done.");
				ms.close();

				XdmNode imdb_movieXdm = saxDocBuilder.wrap(imdb_movie);
				xpath_title.getSelector().setContextItem(imdb_movieXdm );
				
				String original_title="";
		        for (XdmItem item: xpath_title.getSelector()) {
		        	original_title = item.getStringValue();
		        }

		        xpath_year.getSelector().setContextItem(imdb_movieXdm);
				
				String year="";
		        for (XdmItem item: xpath_year.getSelector()) {
		        	year = item.getStringValue();
		        }
				
		        System.out.println("Found movie on IMDB: "+original_title+" ("+year+").");
				
				Document imdb_reviews = docBuilder.newDocument();
		        dest = new DOMDestination(imdb_reviews);
		        
		        URL reviewURL = new URL(movieURL+"reviews");
		        
				System.out.print("Downloading IMDB reviews ...");
				ByteArrayOutputStream rs= web.getURL(reviewURL);
				System.out.println("done.");
	
				
				System.out.print("XSLT parsing file...");
				xslt_review.transform(new ByteArrayInputStream(rs.toByteArray()),dest);
				System.out.println("done.");
				
				String outputFile = "data/movies/"+title+".xml";
				OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outputFile)));
				Serializer s = SAXProcessor.getProcessor().newSerializer(out);
				
	            
				Document rt_movie = docBuilder.newDocument();
				Document rt_reviews = docBuilder.newDocument();
		        
				Document doc = docBuilder.newDocument();
				Element root = doc.createElement("movie");
				if(rt.processEntry(original_title, year,new DOMDestination(rt_movie),new DOMDestination(rt_reviews))){
					NodeList nl = imdb_movie.getFirstChild().getChildNodes();
					for(int i=0; i < nl.getLength(); i++)
					{
						root.appendChild(doc.importNode(nl.item(i), true));
					}
					nl = rt_movie.getFirstChild().getChildNodes();
					for(int i=0; i < nl.getLength(); i++)
					{
						root.appendChild(doc.importNode(nl.item(i), true));
					}
					nl = imdb_reviews.getFirstChild().getChildNodes();
					for(int i=0; i < nl.getLength(); i++)
					{
						root.appendChild(doc.importNode(nl.item(i), true));
					}
					nl = rt_reviews.getFirstChild().getChildNodes();
					for(int i=0; i < nl.getLength(); i++)
					{
						root.appendChild(doc.importNode(nl.item(i), true));
					}
				}
				else
				{
					NodeList nl = imdb_movie.getFirstChild().getChildNodes();
					for(int i=0; i < nl.getLength(); i++)
					{
						root.appendChild(doc.importNode(nl.item(i), true));
					}
					nl = imdb_reviews.getFirstChild().getChildNodes();
					for(int i=0; i < nl.getLength(); i++)
					{
						root.appendChild(doc.importNode(nl.item(i), true));
					}
				}
			
				
				s.serializeNode(saxDocBuilder.wrap(root));
				
				System.out.println("Output written on "+outputFile+" for movie "+original_title+"("+year+")");
				System.out.println("-------------");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SaxonApiException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		



		}		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void extractFromFile(String fileName,int startAt){
		try {
			System.out.print("Processing list of movies at "+fileName+" starting from #"+startAt+".");
			ObjectInputStream ios;
			ios = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(fileName))));
			ArrayList<URL> readObject = (ArrayList<URL>)ios.readObject();
			extractMovies(readObject,startAt);
			System.out.print("Done processing list of movies at "+fileName+".");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
		extractFromFile("data/IMDB_y1960(0,9).object",5);
	}

}
