package org.moviedb;

import net.sf.saxon.s9api.*;

public class XPath {
	private XPathCompiler xpath;
    private XPathSelector selector;
	public XPath(String value){
		try{
			xpath = SAXProcessor.getProcessor().newXPathCompiler();
			selector = xpath.compile(value).load();

		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public XPathSelector getSelector(){
		return selector;
	}
	public void select(XdmNode doc){		
		try {
			selector.setContextItem(doc);
		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
