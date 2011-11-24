package org.moviedb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

public class XPath {
	private XPathCompiler xpath;
    private XPathSelector selector;
	public XPath(String value){
		try{
			xpath = SAXProcessor.getProcessor().newXPathCompiler();
			selector=  xpath.compile(value).load();

		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public XPathSelector getSelector(){
		return selector;
	}
	
	public void select(InputStream is){		
		try {
			DocumentBuilder builder = SAXProcessor.getProcessor().newDocumentBuilder();
	        builder.setLineNumbering(true);
	        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
			XdmNode doc = builder.build(new StreamSource(is));
			
			selector.setContextItem(doc);
		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
