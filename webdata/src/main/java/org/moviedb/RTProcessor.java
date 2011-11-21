package org.moviedb;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

public class RTProcessor {
	
	private String userAgent;
	private OutputStream out;
	private XSLT xsltProcessor; 
	public RTProcessor(String _userAgent, OutputStream _out){
		userAgent = _userAgent;
		out = _out;
		xsltProcessor = new XSLT();
	}
	public void processEntry(String movieTitle, String movieYear) throws Exception{

	
		StringBuilder query = new StringBuilder();
		query.append("https://www.google.com/search?as_q=%22");
		try {
			query.append(URLEncoder.encode(movieTitle+" ("+movieYear+")","UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		query.append("%22&as_epq=&as_oq=&as_eq=&as_nlo=&as_nhi=&lr=&cr=&as_qdr=all&as_sitesearch=rottentomatoes.com&as_occt=any&safe=off&tbs=&as_filetype=&as_rights=&btnl=745");
		
		URL url = new URL(query.toString());
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent",userAgent);

		xsltProcessor.clean(conn.getInputStream(), out);	
		
	}
	
}
