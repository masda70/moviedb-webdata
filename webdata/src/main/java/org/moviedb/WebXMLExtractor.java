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
	
	public void removeTag(Node node){
		NodeList list = node.getChildNodes();
		boolean b = false;
		for(int i=0; i< list.getLength();i++){
			Node r = list.item(i);
			if(r.getNodeName()=="script" || r.getNodeName()=="style" || r.getNodeName()=="meta" || r.getNodeName()=="noscript"){
				node.removeChild(r);
				b = true;			
				break;
			}else{
				removeTag(r);
			}
		}
		if(b)removeTag(node);
		
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
	
	public ByteArrayOutputStream getURL(URL url) throws IOException{
    


        try {
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
				
				removeTag(document);

		        ByteArrayOutputStream output = new ByteArrayOutputStream();
		        
		        Result result = new StreamResult(output);

		        // Write the DOM document to the file
		        Transformer xformer = TransformerFactory.newInstance().newTransformer();
		        xformer.transform(new DOMSource(document), result);
		        
				//outputter.output(doc, output);
		        
			/*	Document doc = builder.build(input);
		
				
				
				  ByteArrayOutputStream output = new ByteArrayOutputStream();
				outputter.output(doc, output);
				*/
			  return output;

				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return null;


		

		

		 
		
	}
}
