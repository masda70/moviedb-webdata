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

import javax.xml.transform.stream.StreamResult;

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
		xsltProcessor = new XSLT("schema/googlefirstresult.xslt");
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
		
		InputStream is = conn.getInputStream();
		
		ByteArrayOutputStream cleaned = new ByteArrayOutputStream();
		
		Processor processor = new Processor();

		Build the source document by calling newDocumentBuilder() to create a document builder, setting appropriate options, and then calling the build() method. This returns an XdmNode which can be supplied as the context item to the XPath expression.

		Call newXPathCompiler() to create an XPath Compiler, and set any options that are local to a specific compilation (notably declaring namespace prefixes that are used in the XPath expression).

		Call the compile() method to compile an expression. The result is an XPathExecutable, which can be used as often as you like in the same thread or in different threads.

		To evaluate the expression, call the load() method on the XPathExecutable. This creates an XPathSelector. The XPathSelector can be serially reused, but it must not be shared across multiple threads. Set any options required for the specific XPath execution (for example, the initial context node, the values of any variables referenced in the expression), and then call one of the methods iterator()evaluate(), or evaluateSingle() to execute the XPath expression.

		Because the XPathSelector is an Iterable, it is possible to iterate over the results directly using the Java 5 "for-each" construct.

		The result of an XPath expression is in general an XdmValue, representing a value as defined in the XDM data model (that is, a sequence of nodes and/or atomic values). Subclasses of XdmValue include XdmItem, XdmNode, and XdmAtomicValue, and these relate directly to the corresponding concepts in XDM. Various methods are available to translate between this model and native Java data types.

		Examples of the use of s9api to evaluate XPath expressions are included in the Saxon resources file, see module S9APIExamples.java.


		
		xsltProcessor.clean(is, cleaned);
		is.close();
		StreamResult result = new StreamResult("result2.xml");
		InputStream buffer = new ByteArrayInputStream(cleaned.toByteArray());	
		xsltProcessor.tr(buffer,result);
	}
	
}
