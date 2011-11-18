package org.moviedb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XsltCompiler;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.setProperty("http.agent.name", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
			// create an instance of HtmlCleaner
			HtmlCleaner cleaner = new HtmlCleaner();
			 
			// take default cleaner properties
			CleanerProperties props = cleaner.getProperties();
			 
			// customize cleaner's behaviour with property setters
			 
			// Clean HTML taken from simple string, file, URL, input stream, 
			// input source or reader. Result is root node of created 
			// tree-like structure. Single cleaner instance may be safely used
			// multiple times.
			URL url = new URL("http://www.imdb.com/title/tt0068646/");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
	
			TagNode node = cleaner.clean(conn.getInputStream());	


			// serialize a node to a file, output stream, DOM, JDom...
			new PrettyHtmlSerializer(props).writeToFile(node, "C:\\Users\\David Montoya\\Documents\\ProjetWebData\\test2.html");
			
		       TransformerFactory tFactory = TransformerFactory.newInstance();  
		        try {  
		            Transformer transformer =  
		                tFactory.newTransformer(new StreamSource(new File("C:\\Users\\David Montoya\\Documents\\ProjetWebData\\imdb.xslt")));  
		  
		            transformer.transform(new StreamSource(new File("C:\\Users\\David Montoya\\Documents\\ProjetWebData\\test2.html")),  
		                                  new StreamResult(new File("C:\\Users\\David Montoya\\Documents\\ProjetWebData\\test3.html")));  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
