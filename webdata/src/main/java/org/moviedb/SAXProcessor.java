package org.moviedb;

import net.sf.saxon.s9api.Processor;

public class SAXProcessor {
	private static Processor epicSAXProcessor = new Processor(false);
	
	public static Processor getProcessor(){
		return epicSAXProcessor;
	}
}
