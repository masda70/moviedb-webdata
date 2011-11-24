package org.moviedb;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.w3c.dom.Node;

import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XdmItem;

public class RTProcessor {
	
	private WebXMLExtractor web;
    private XPath xpath;
  
	private XSLT xslt_movie;
	private XSLT xslt_review;
	private  net.sf.saxon.s9api.DocumentBuilder saxDocBuilder;
	public RTProcessor(	net.sf.saxon.s9api.DocumentBuilder _saxDocBuilder){
		saxDocBuilder = _saxDocBuilder;
		web = new WebXMLExtractor();
		xpath = new XPath("//a/@href[starts-with(.,'http://www.rottentomatoes.com/m/')]");
		xslt_movie = new XSLT("schema/rt.xslt");
		xslt_review = new XSLT("schema/rt_review.xslt");
		
	}
	
	
	public boolean processEntry(String fullTitle,Destination main, Destination reviews) throws IOException, SaxonApiException {
		try {
			
			StringBuilder query = new StringBuilder();
			query.append("https://www.google.com/search?as_q=%22");
			try {
				query.append(URLEncoder.encode(fullTitle,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			query.append("%22&as_epq=&as_oq=&as_eq=&as_nlo=&as_nhi=&lr=&cr=&as_qdr=all&as_sitesearch=rottentomatoes.com&as_occt=any&safe=off&tbs=&as_filetype=&as_rights=&btnl=745");
			
			URL url;
	
			url = new URL(query.toString());
			System.out.println("Querying google for this title...");
			Node is = web.getNode(url);
		
	        xpath.select(saxDocBuilder.wrap(is));
	
	        String rt_url =null;
	        for (XdmItem item: xpath.getSelector()) {
	        	rt_url = item.getStringValue();
	        	break;
	        }
	        if(rt_url==null) return false;
	 
	        URL rt_movie = new URL(rt_url);
	        URL rt_review = new URL(rt_url+"/reviews");
	        
	        System.out.print("Found "+fullTitle+" on RT.com, at "+rt_url+", downloading files...");
	        Node ms = web.getNode(rt_movie);
	        Node rs = web.getNode(rt_review);
	        System.out.println("done.");
	        System.out.print("XSLT parsing RT files...");
			xslt_movie.transform(saxDocBuilder.wrap(ms),main);
			xslt_review.transform(saxDocBuilder.wrap(rs),reviews);

	        return true;
		} catch (MalformedURLException e) {
	
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean processEntry(String movieTitle, String movieYear,Destination main, Destination reviews)
			throws IOException, SaxonApiException  {
		return processEntry(movieTitle+" ("+movieYear+")",main,reviews);
	}
	
}
