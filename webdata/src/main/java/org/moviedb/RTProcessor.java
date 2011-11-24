package org.moviedb;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;

public class RTProcessor {
	
	private WebXMLExtractor web;
    private XPath xpath;
  
	private XSLT xslt_movie;
	private XSLT xslt_review;
    
	public RTProcessor(){
		web = new WebXMLExtractor();
		xpath = new XPath("//a/@href[starts-with(.,'http://www.rottentomatoes.com/m/')]");
		xslt_movie = new XSLT("schema/rt.xslt");
		xslt_review = new XSLT("schema/rt_review.xslt");
		
	}
	
	
	public boolean processEntry(String fullTitle,Destination main, Destination reviews) throws IOException, SaxonApiException {
		try {
			
			StringBuilder query = new StringBuilder();
			query.append("https://www.google.com/search?as_q=%22");
			query.append(fullTitle);
			query.append("%22&as_epq=&as_oq=&as_eq=&as_nlo=&as_nhi=&lr=&cr=&as_qdr=all&as_sitesearch=rottentomatoes.com&as_occt=any&safe=off&tbs=&as_filetype=&as_rights=&btnl=745");
			
			URL url;
	
			url = new URL(query.toString());
			System.out.println("Querying google for this title...");
			ByteArrayOutputStream is = web.getURL(url);
		
	        xpath.select(new ByteArrayInputStream(is.toByteArray()));
	
	        String rt_url =null;
	        for (XdmItem item: xpath.getSelector()) {
	        	rt_url = item.getStringValue();
	        	break;
	        }
	        if(rt_url==null) return false;
	 
	        URL rt_movie = new URL(rt_url);
	        URL rt_review = new URL(rt_url+"/reviews");
	        
	        System.out.print("Found "+fullTitle+" on RT.com, at "+rt_url+", downloading files...");
	        ByteArrayOutputStream ms = web.getURL(rt_movie);
	        ByteArrayOutputStream rs = web.getURL(rt_review);
	        System.out.println("done.");
	        System.out.print("XSLT parsing RT files...");
			xslt_movie.transform(new ByteArrayInputStream(ms.toByteArray()),main);
			xslt_review.transform(new ByteArrayInputStream(rs.toByteArray()),reviews);

			ms.close();
			rs.close();
	        return true;
		} catch (MalformedURLException e) {
	
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean processEntry(String movieTitle, String movieYear,Destination main, Destination reviews) throws IOException, SaxonApiException  {
		return processEntry(movieTitle+" ("+movieYear+")",main,reviews);
	}
	
}
