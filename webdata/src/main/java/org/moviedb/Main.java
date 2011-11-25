package org.moviedb;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.w3c.dom.*;

import net.sf.saxon.s9api.*;

public class Main{
	private XSLT _xslt_movie;
	private XSLT _xslt_review;
	private XPath _xpath_title;
	private XPath _xpath_year;
	private WebXMLExtractor _web;
	private RTProcessor _rt;
	private DocumentBuilder _docBuilder;
	private net.sf.saxon.s9api.DocumentBuilder _saxDocBuilder;
	private ArrayList<URL> movieURLList;

	private String mainOutputFile;
	private String movieXMLOutputFolder;
	
	private String outputURLListFolder;
	private boolean outputURLList = false;
	
	private int maxProcessed = -1;

	private Document xmlFileListDoc;
	private Element xmlFileListElement;
	
	public Main (String _mainOutputFile)  throws ParserConfigurationException {

		_xslt_movie = new XSLT("schema/imdb.xslt");
		_xslt_review = new XSLT("schema/imdb_review.xslt");

		_xpath_title = new XPath("movie/original_title/text()");
		_xpath_year = new XPath("movie/year/text()");
		
		_web = new WebXMLExtractor();
		_saxDocBuilder =  SAXProcessor.getProcessor().newDocumentBuilder();
		_saxDocBuilder.setLineNumbering(true);
		_saxDocBuilder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
		_rt = new RTProcessor(_saxDocBuilder);

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setIgnoringElementContentWhitespace(true);
		_docBuilder = docFactory.newDocumentBuilder();

		mainOutputFile = _mainOutputFile;
		xmlFileListDoc = _docBuilder.newDocument();
		xmlFileListElement = xmlFileListDoc.createElement("list");

	
		
	}
	
	
	public void setOutputURLList(String path){
		 outputURLList = true;
		 outputURLListFolder = path;
	}
	
	public void setMaxProcessed(int n){
		maxProcessed = n;
	}

	


	private void addXMLFile(String path){
		Element item = xmlFileListDoc.createElement("entry");
		item.appendChild(xmlFileListDoc.createTextNode(path));
		xmlFileListElement.appendChild(item);
	}
	

    
    public void extractMovies(String _movieXMLOutputFolder){
    	extractMovies(_movieXMLOutputFolder,movieURLList,0,-1);
    }
    
	public void extractMovies(String _movieXMLOutputFolder, ArrayList<URL> moviesURL, int startAt, int stopAt){
		movieXMLOutputFolder = _movieXMLOutputFolder;
		if(stopAt == -1) stopAt = moviesURL.size();
		if(maxProcessed <0) maxProcessed = Integer.MAX_VALUE;
		for(int count = startAt; count < stopAt; count++){
			if(maxProcessed==0) return;
			maxProcessed--;
			URL movieURL = moviesURL.get(count);
			try {
				System.out.print("-- #"+(count+1)+": Extracting "+movieURL+"...");
				processUrl(movieURL);
				System.out.println("-------------");
			} catch (IOException ex) {
				System.out.println("ERROR "+ex.getMessage());
			} catch (SaxonApiException ex) {
				System.out.println("ERROR "+ex.getMessage());
			}
			
		}
	}

	private void processUrl ( URL movieURL)
			throws IOException, SaxonApiException {
		Document imdb_movie = _docBuilder.newDocument();
		DOMDestination dest = new DOMDestination(imdb_movie);
		String title = movieURL.getPath();
		title = title.substring(7, title.length()-1);
	

		Node ms= _web.getNode(movieURL);
		System.out.println("done downloading.");
	
		
		System.out.print("XSLT parsing file...");
		_xslt_movie.transform(_saxDocBuilder.wrap(ms),dest);
		System.out.println("done.");


		XdmNode imdb_movieXdm = _saxDocBuilder.wrap(imdb_movie);
		_xpath_title.getSelector().setContextItem(imdb_movieXdm );
	
		String original_title="";
		for (XdmItem item: _xpath_title.getSelector()) {
			original_title = item.getStringValue();
		}

		_xpath_year.getSelector().setContextItem(imdb_movieXdm);
	
		String year="";
		for (XdmItem item: _xpath_year.getSelector()) {
			year = item.getStringValue();
		}
	
		System.out.println("Found movie on IMDB: "+original_title+" ("+year+").");
	
		Document imdb_reviews = _docBuilder.newDocument();
		dest = new DOMDestination(imdb_reviews);
    
		URL reviewURL = new URL(movieURL+"reviews");
    
		System.out.print("Downloading IMDB reviews ...");
		Node rs= _web.getNode(reviewURL);
		System.out.println("done.");

	
		System.out.print("XSLT parsing file...");
		_xslt_review.transform(_saxDocBuilder.wrap(rs),dest);
		System.out.println("done.");
	
		String outputFile = movieXMLOutputFolder+"/"+title+".xml";
		
		File folder = new File(movieXMLOutputFolder);
		
		if(folder.exists()){
			if(!folder.isDirectory()){
				throw new IOException("Error: supplied output XML path is not a directory.");
			}
		}else{
			if(!folder.mkdirs()){
				throw new IOException("Error: could not create XML output directory.");
			}
		}
		
		OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outputFile)));
		Serializer s = SAXProcessor.getProcessor().newSerializer(out);
	
		Element root = buildMovieRtImdb(imdb_movie, imdb_reviews, original_title, year);

		s.serializeNode(_saxDocBuilder.wrap(root));
	
		System.out.println("Output written on "+outputFile+" for movie "+original_title+"("+year+")");
		addXMLFile(outputFile);
	

	}

	private Element buildMovieRtImdb ( Document imdb_movie, Document imdb_reviews, String original_title, String year )
			throws IOException, SaxonApiException {
		
		Document rt_movie = _docBuilder.newDocument();
		Document rt_reviews = _docBuilder.newDocument();
		Document doc =  _docBuilder.newDocument();
		Element root = doc.createElement("movies");
		Element subroot = doc.createElement("movie");
		
		root.appendChild(subroot);
		
		
		boolean ok;
		try {
			ok = _rt.processEntry(original_title, year,
					new DOMDestination(rt_movie), new DOMDestination(rt_reviews));
		} catch ( IOException e ) {
			ok = false;
			System.out.println("ERROR "+e.getMessage());
		} catch ( SaxonApiException e ) {
			ok = false;
			System.out.println("ERROR "+e.getMessage());
		}
		
		if( ok ) {
			// Found title on RT
			NodeList nl = imdb_movie.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				subroot.appendChild(doc.importNode(nl.item(i), true));
			}
			nl = rt_movie.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				subroot.appendChild(doc.importNode(nl.item(i), true));
			}
			nl = imdb_reviews.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				subroot.appendChild(doc.importNode(nl.item(i), true));
			}
			nl = rt_reviews.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				subroot.appendChild(doc.importNode(nl.item(i), true));
			}
		} else {
			// Could not find title on RT
			NodeList nl = imdb_movie.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				subroot.appendChild(doc.importNode(nl.item(i), true));
			}
			nl = imdb_reviews.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				subroot.appendChild(doc.importNode(nl.item(i), true));
			}
		}
		
		return root;
	}
	
	public void extractIMDBMovieList(String year, int pageFrom, int pageTo)
			throws FileNotFoundException, IOException {
		
		IMDBMovieListExtractor movieExtractor= new IMDBMovieListExtractor(_saxDocBuilder);

		System.out.println("Extracting IMDB list, year "+ year +" pages from "+pageFrom+" to "+(pageTo)+".");
		movieURLList = movieExtractor.getList(year, pageFrom, pageTo,maxProcessed);

		System.out.println("Done, extracted "+ movieURLList.size()+" movie URLs.");
		
		if (outputURLList){
			String outputFile = outputURLListFolder+"/IMDB_y"+year+"("+pageFrom+","+(pageTo)+").object";
			System.out.println("Saved output at "+outputFile+".");
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(outputFile))));
			oos.writeObject( movieURLList);
			oos.close();
		}
		
		
	}
	
	public void inputMoviesFromDirectory(String path) throws IllegalArgumentException, IOException, SaxonApiException, TransformerFactoryConfigurationError, TransformerException{
		File dir = new File(path);
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		};
		String[] children = dir.list(filter);
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		    	addXMLFile(path+"/"+children[i]);
		  }
		}		

	}
	
	public void output()
			throws IllegalArgumentException, IOException, SaxonApiException, TransformerFactoryConfigurationError, TransformerException {

		

		XSLT xslt_merge = new XSLT("schema/merge.xslt");
	
		
        File file = new File(mainOutputFile);
		Destination dest = new Serializer(file);

		xslt_merge.transform(_saxDocBuilder.wrap(xmlFileListElement), dest);
        
        System.out.println("Done processing movies, output XML at: "+mainOutputFile);
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		try {
			Options options = new Options();
			Option maxProcessed = OptionBuilder.withArgName("number")
				.hasArgs(1)
				.withDescription("max number of processed movies")
				.withLongOpt("countmax")
				.create("c");
			Option help = OptionBuilder
				.hasArgs(0)
				.withDescription("print help")
				.withLongOpt("help")
				.create("h");
			Option outputFile = OptionBuilder.withArgName( "outputfile")
				.hasArgs(1)
				.withDescription("output .xml file")
				.withLongOpt("output")
				.create("o");
			Option moviesXMLInput = OptionBuilder.withArgName( "path")
        		.hasArgs(1)
	        	.withLongOpt("merge")
	        	.withDescription( "merge XML database movies")
	        	.create( "m" );
			Option buildList = OptionBuilder.withArgName( "path year pageFrom pageTo")
	        	.hasArgs(4)
	        	.withLongOpt("build")
	        	.withDescription( "extract movies from a given year, save an xml for each movie" )
	        	.create( "b" );
			
			
			// add t option
			options.addOption(moviesXMLInput);
			options.addOption(maxProcessed);
			options.addOption(buildList);
			options.addOption(outputFile);
			options.addOption(help);
			
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
	
			if(cmd.hasOption(help.getOpt())){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "webdata", options );
				return;
			}
			
			
			String mainOutputFile;
			if(cmd.hasOption(outputFile.getOpt())){
				mainOutputFile = cmd.getOptionValues(outputFile.getOpt())[0];
			}else{
				mainOutputFile = "out.xml";
				System.out.println("[OPT] No output file supplied, defaulting to "+ mainOutputFile);
			}
							
			
			Main main = new Main(mainOutputFile);

			if(cmd.hasOption(maxProcessed.getOpt())){
				main.setMaxProcessed(Integer.parseInt(cmd.getOptionValues(maxProcessed.getOpt())[0]));		
			}
			
			if(cmd.hasOption(moviesXMLInput.getOpt())){
				System.out.println("[OPT] Merge input XML files.");
				main.inputMoviesFromDirectory(cmd.getOptionValues(moviesXMLInput.getOpt())[0]);
			}
			
			if(cmd.hasOption(buildList.getOpt())){
				String values[] = cmd.getOptionValues(buildList.getOpt());
				System.out.println("[OPT] Build IMDB Titles from Web.");
				main.extractIMDBMovieList(values[1],Integer.parseInt(values[2]),Integer.parseInt(values[3]));
				main.extractMovies(values[0]);
			}
			

			main.output();
			


		} catch ( Exception e ) {
			e.printStackTrace();

		}
	}
}
