package org.moviedb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.ValidationMode;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltTransformer;



public class XSLT {
	private XsltTransformer xsltTransformer;
	
	public XSLT (String schemaFile) {
		try {
			xsltTransformer = SAXProcessor.getProcessor().newXsltCompiler().compile(new StreamSource(new File(schemaFile))).load();
		} catch (SaxonApiException e) {
			e.printStackTrace();
		}		
	}
	
	
	public void transform (XdmNode source, Destination dest) throws IOException, SaxonApiException {	
	
        xsltTransformer.setInitialContextNode(source);
        xsltTransformer.setDestination(dest);
        xsltTransformer.transform();

	}
	
}

