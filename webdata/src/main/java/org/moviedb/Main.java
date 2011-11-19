package org.moviedb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XsltCompiler;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			// create an instance of HtmlCleaner
			HtmlCleaner cleaner = new HtmlCleaner();
			 // take default cleaner properties
			CleanerProperties props = cleaner.getProperties();

			props.setPruneTags("script,style");
			props.setRecognizeUnicodeChars(false);

			props.setAdvancedXmlEscape(false);

			URL url = new URL("http://www.imdb.com/title/tt0068646/");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/A.B (KHTML, like Gecko) Chrome/X.Y.Z.W Safari/A.B.");
	
			System.out.println(Charset.defaultCharset());
			TagNode node = cleaner.clean(conn.getInputStream());	
			
			// serialize a node to a file, output stream, DOM, JDom...
			PrettyXmlSerializer serializer= new PrettyXmlSerializer(props);
			serializer.writeToFile(node, "C:\\Users\\David Montoya\\Documents\\ProjetWebData\\test2.html");
			
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
