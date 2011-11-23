package org.moviedb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltTransformer;


public class XSLT {

	private Processor epicSAXProcessor;

	private XsltTransformer xsltTransformer;
	
	public XSLT (String schemaFile) throws SaxonApiException {
		epicSAXProcessor = new Processor(false); 
		xsltTransformer = epicSAXProcessor.newXsltCompiler().compile(new StreamSource(new File(schemaFile))).load();
		
	}
	
	public void transform (InputStream input, OutputStream output) throws IOException, SaxonApiException {
			XdmNode source;
			source = epicSAXProcessor.newDocumentBuilder().build(new StreamSource(input));
	        Serializer out = epicSAXProcessor.newSerializer(output);
	        
	        out.setOutputProperty(Serializer.Property.METHOD, "html");
	        out.setOutputProperty(Serializer.Property.INDENT, "yes");
	        
	        xsltTransformer.setInitialContextNode(source);
	        xsltTransformer.setDestination(out);
	        xsltTransformer.transform();

	}
}
