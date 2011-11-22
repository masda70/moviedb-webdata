package org.moviedb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.htmlcleaner.*;

public class XSLT {
	private HtmlCleaner _cleaner;
	private PrettyXmlSerializer _serializer;
	private Transformer _transformer;
	
	public XSLT (String schemaFile) {
		 _cleaner = new HtmlCleaner();
		CleanerProperties props = _cleaner.getProperties();
		props.setPruneTags("script,style");
		props.setRecognizeUnicodeChars(false);
		props.setAdvancedXmlEscape(false);

		_serializer= new PrettyXmlSerializer(props);
		TransformerFactory tFactory = TransformerFactory.newInstance();

   		try {
			_transformer = tFactory.newTransformer(
					new StreamSource(new File(schemaFile)));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}  

	}
	
	public void clean ( InputStream input, OutputStream output ) throws IOException {
		TagNode cleaned = _cleaner.clean(input);
		_serializer.writeToStream(cleaned, output);
	}
	
	public void tr (InputStream input, StreamResult output) throws IOException {

		Source inStream = new StreamSource(input);

	   	try {
			_transformer.transform(inStream, output);  
	   	} catch (Exception e) {  
	   		e.printStackTrace();  
	   	}  
	}
}
