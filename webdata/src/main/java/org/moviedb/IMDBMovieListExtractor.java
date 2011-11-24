package org.moviedb;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import net.sf.saxon.s9api.*;

public class IMDBMovieListExtractor {
	WebXMLExtractor web;
    XPath xpath;
    
    private static final int IMDB_MOVIESPERPAGE = 50;
    
    private static final int MAXNUMBEROFGETURLATTEMPTS= 5;
    private static final String imdbHOME = "http://www.imdb.com";
    
	public IMDBMovieListExtractor(){
		web = new WebXMLExtractor();
		xpath = new XPath("//td[@class='title']/a/@href");
	}
	
	public ArrayList<URL> getList(String year, int pageFrom, int pageTo) {
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

					xpath.select(new ByteArrayInputStream(is.toByteArray()));
			
			        for (XdmItem item: xpath.getSelector()) {
			        	urlList.add(new URL(imdbHOME+item.getStringValue()));
			        }
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return urlList;
	}
}
