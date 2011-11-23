package org.moviedb;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.xml.sax.InputSource;

public class RTProcessor {
	
	private OutputStream out;
	WebXMLExtractor web;
    XPathSelector selector;
    Processor epicSAXProcessor;
	public RTProcessor( OutputStream _out){
		try {
			out = _out;
			web = new WebXMLExtractor();

			epicSAXProcessor= new Processor(false);
			XPathCompiler xpath = epicSAXProcessor.newXPathCompiler();

			selector=  xpath.compile("//a/@href[starts-with(.,'http://www.rottentomatoes.com/m/')]").load();
		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void processEntry(String fullTitle) throws Exception{
		StringBuilder query = new StringBuilder();
		query.append("https://www.google.com/search?as_q=%22");
		try {
			query.append(URLEncoder.encode(fullTitle,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		query.append("%22&as_epq=&as_oq=&as_eq=&as_nlo=&as_nhi=&lr=&cr=&as_qdr=all&as_sitesearch=rottentomatoes.com&as_occt=any&safe=off&tbs=&as_filetype=&as_rights=&btnl=745");
		
		URL url = new URL(query.toString());
		
		
		ByteArrayOutputStream is = web.getURL(url);
	

        DocumentBuilder builder = epicSAXProcessor.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode booksDoc = builder.build(new StreamSource(new ByteArrayInputStream(is.toByteArray())));

            selector.setContextItem(booksDoc);

            for (XdmItem item: selector) {
            	System.out.println(item.getStringValue());
            	break;
            }
 

		
/*
		is.close();
		StreamResult result = new StreamResult("result2.xml");
		InputStream buffer = new ByteArrayInputStream(cleaned.toByteArray());	
		xsltProcessor.tr(buffer,result);*/
	}
	public void processEntry(String movieTitle, String movieYear) throws Exception{
		processEntry(movieTitle+" ("+movieYear+")");
	

	}
	
}
