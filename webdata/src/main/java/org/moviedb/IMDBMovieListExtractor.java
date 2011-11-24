package org.moviedb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

public class IMDBMovieListExtractor {


	WebXMLExtractor web;
    XPathSelector selector;
    Processor epicSAXProcessor;
    
    private static final int IMDB_MOVIESPERPAGE = 50;
    
    private static final int MAXNUMBEROFGETURLATTEMPTS= 5;
    private static final String imdbHOME = "http://www.imdb.com";
	public IMDBMovieListExtractor(){
		try {
			web = new WebXMLExtractor();

			epicSAXProcessor= new Processor(false);
			XPathCompiler xpath = epicSAXProcessor.newXPathCompiler();

			selector=  xpath.compile("//td[@class='title']/a/@href").load();
		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ArrayList<URL> getList(String year, int pageFrom, int pageTo)
	{
		
		ArrayList<URL> urlList = new ArrayList<URL>((pageTo-pageFrom+1)*IMDB_MOVIESPERPAGE);
		for(int i=pageFrom; i<pageTo; i++ ){
			int index = 1+i*IMDB_MOVIESPERPAGE;
			try {
				URL url;

				url = new URL(imdbHOME+"/search/title?sort=moviemeter,asc&start="+index+"&title_type=feature&year="+URLEncoder.encode(year,"UTF-8")+","+URLEncoder.encode(year,"UTF-8"));
				ByteArrayOutputStream is;
					is = null;
					int attempt = 0;
					while(true){
						IOException err = null;
						try {
							is = web.getURL(url);
						} catch (IOException e) {
							err = e;
						}
						if(is!=null) break;
						attempt++;
						if(attempt>=MAXNUMBEROFGETURLATTEMPTS){
							err.printStackTrace();
							System.err.println("Warning: could not retrieve page at "+url);
						}
					}

				   DocumentBuilder builder = epicSAXProcessor.newDocumentBuilder();
				    builder.setLineNumbering(true);
				    builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
				    XdmNode booksDoc = builder.build(new StreamSource(new ByteArrayInputStream(is.toByteArray())));
				
			        selector.setContextItem(booksDoc);
				
			        for (XdmItem item: selector) {
			        	urlList.add(new URL(imdbHOME+item.getStringValue()));
			        }
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SaxonApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return urlList;

	 
	}

}