package org.moviedb;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.w3c.dom.Node;

import net.sf.saxon.s9api.*;

public class IMDBMovieListExtractor {
	WebXMLExtractor web;
    XPath xpath;
    
    private static final int IMDB_MOVIESPERPAGE = 50;
    
    private static final int MAXNUMBEROFGETURLATTEMPTS= 5;
    private static final String imdbHOME = "http://www.imdb.com";
	private  net.sf.saxon.s9api.DocumentBuilder saxDocBuilder;
	
	public IMDBMovieListExtractor(	net.sf.saxon.s9api.DocumentBuilder _saxDocBuilder){
	 	saxDocBuilder =  _saxDocBuilder;
		web = new WebXMLExtractor();
		xpath = new XPath("//td[@class='title']/a/@href");
	}
	
	public ArrayList<URL> getList(String year, int pageFrom, int pageTo, int max) {
		ArrayList<URL> urlList;
		if(pageTo == -1){
			urlList = new ArrayList<URL>();
			pageTo = Integer.MAX_VALUE;
		}else{
			urlList = new ArrayList<URL>((pageTo-pageFrom+1)*IMDB_MOVIESPERPAGE);
		}
		if(max < 0) max= Integer.MAX_VALUE;
		for(int i=pageFrom; i<=pageTo; i++ ){
			int index = 1+i*IMDB_MOVIESPERPAGE;
			try {
				URL url;

				url = new URL(imdbHOME+"/search/title?sort=moviemeter,asc&start="+index+"&title_type=feature&year="+URLEncoder.encode(year,"UTF-8")+","+URLEncoder.encode(year,"UTF-8"));
				Node is;
					is = null;
					int attempt = 0;
					while(true){
						IOException err = null;
						try {
							is = web.getNode(url);
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

					xpath.select(saxDocBuilder.wrap(is));
					if(!(xpath.getSelector().iterator().hasNext())) break;
					
			        for (XdmItem item: xpath.getSelector()) {
			        	urlList.add(new URL(imdbHOME+item.getStringValue()));
			        	max--;
			        }
			        if(max == 0) break;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return urlList;
	}
}
