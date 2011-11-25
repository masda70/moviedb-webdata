package org.moviedb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.xsltc.trax.SAX2DOM;
import org.ccil.cowan.tagsoup.Parser;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class WebXMLExtractor {
	static String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/A.B (KHTML, like Gecko) Chrome/X.Y.Z.W Safari/A.B.";

	
	public WebXMLExtractor(){
	}
	
	public Node getNode(URL url) throws IOException{
	    


        URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", userAgent);
		InputStream input = conn.getInputStream();

		final Parser parser = new Parser();
		SAX2DOM sax2dom = null;
		try {
			sax2dom = new SAX2DOM();

			parser.setContentHandler(sax2dom);
			parser.setFeature(Parser.namespacesFeature, false);
			parser.parse(new InputSource(input));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Node document = sax2dom.getDOM();
		
		return document;
	


		

		

		 
		
	}

}
