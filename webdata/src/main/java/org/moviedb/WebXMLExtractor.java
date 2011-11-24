package org.moviedb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

public class WebXMLExtractor {
	static String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/A.B (KHTML, like Gecko) Chrome/X.Y.Z.W Safari/A.B.";
	private HtmlCleaner _cleaner;
	private PrettyXmlSerializer _serializer;
	
	public WebXMLExtractor(){
		 _cleaner = new HtmlCleaner();
		CleanerProperties props = _cleaner.getProperties();
		props.setPruneTags("script,style");
		props.setRecognizeUnicodeChars(false);
		props.setAdvancedXmlEscape(false);
		_serializer= new PrettyXmlSerializer(props);
	}
	
	public ByteArrayOutputStream getURL(URL url) throws IOException{

		URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", userAgent);
		InputStream input = conn.getInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TagNode cleaned = _cleaner.clean(input);
		_serializer.writeToStream(cleaned, output);
		return output;
	}
}
