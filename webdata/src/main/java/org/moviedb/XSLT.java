package org.moviedb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltTransformer;


public class XSLT {


	private XsltTransformer xsltTransformer;
	
	public XSLT (String schemaFile) {
		try {
			xsltTransformer = SAXProcessor.getProcessor().newXsltCompiler().compile(new StreamSource(new File(schemaFile))).load();
		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void transform (InputStream input, OutputStream output) throws IOException, SaxonApiException {
			XdmNode source;
			source = SAXProcessor.getProcessor().newDocumentBuilder().build(new StreamSource(input));
	        Serializer out = SAXProcessor.getProcessor().newSerializer(output);
	        
	        out.setOutputProperty(Serializer.Property.METHOD, "html");
	        out.setOutputProperty(Serializer.Property.INDENT, "yes");
	        xsltTransformer.setInitialContextNode(source);
	        xsltTransformer.setDestination(out);
	        xsltTransformer.transform();

	}
	
	public void transform (InputStream input, Destination dest) throws IOException, SaxonApiException {
		XdmNode source;
		source = SAXProcessor.getProcessor().newDocumentBuilder().build(new StreamSource(input));
	

        xsltTransformer.setInitialContextNode(source);
        xsltTransformer.setDestination(dest);
        xsltTransformer.transform();



	}

	public XdmNode transform (InputStream input) throws IOException, SaxonApiException {
		XdmNode source;
		source = SAXProcessor.getProcessor().newDocumentBuilder().build(new StreamSource(input));
	
	    XdmDestination xdmDest = new XdmDestination();
        xsltTransformer.setInitialContextNode(source);
        xsltTransformer.setDestination(xdmDest);
        xsltTransformer.transform();

        return xdmDest.getXdmNode();

	}

	
}

