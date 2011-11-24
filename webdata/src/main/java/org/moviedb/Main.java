package org.moviedb;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import net.sf.saxon.s9api.*;

public class Main {
	private XSLT _xslt_movie;
	private XSLT _xslt_review;
	private XPath _xpath_title;
	private XPath _xpath_year;
	private WebXMLExtractor _web;
	private RTProcessor _rt;
	private DocumentBuilder _docBuilder;
	private net.sf.saxon.s9api.DocumentBuilder _saxDocBuilder;
	private Document _fileListDoc;
	private Element _fileListRoot;

	public Main () throws ParserConfigurationException {

		_xslt_movie = new XSLT("schema/imdb.xslt");
		_xslt_review = new XSLT("schema/imdb_review.xslt");

		_xpath_title = new XPath("movie/original_title/text()");
		_xpath_year = new XPath("movie/year/text()");
		
		_web = new WebXMLExtractor();
		
		_rt = new RTProcessor();

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setIgnoringElementContentWhitespace(true);
		_docBuilder = docFactory.newDocumentBuilder();

		_saxDocBuilder =  SAXProcessor.getProcessor().newDocumentBuilder();
		_saxDocBuilder.setLineNumbering(true);
		_saxDocBuilder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
		

		_fileListDoc = _docBuilder.newDocument();
		_fileListRoot = _fileListDoc.createElement("movies");

	}
	
	public void extractMovies(ArrayList<URL> moviesURL, int startAt, int stopAt){
		int count = 0;
		boolean isInit = true;
		
		for(URL movieURL: moviesURL){
			count++;
			if(isInit && startAt != count) continue;
			else isInit = false;
			if(stopAt != 0 && count > stopAt) return;
		
			try {
				processUrl(movieURL, count);
			} catch (IOException ex) {
				System.out.println("ERROR "+ex.getMessage());
			} catch (SaxonApiException ex) {
				System.out.println("ERROR "+ex.getMessage());
			}
		}
	}

	private void processUrl ( URL movieURL, int count )
			throws IOException, SaxonApiException {
		Document imdb_movie = _docBuilder.newDocument();
		DOMDestination dest = new DOMDestination(imdb_movie);
		String title = movieURL.getPath();
		title = title.substring(7, title.length()-1);
	
		System.out.print("#"+count+": Querying "+movieURL+"...");
		ByteArrayOutputStream ms= _web.getURL(movieURL);
		System.out.println("done downloading.");
	
		System.out.print("XSLT parsing file...");
		_xslt_movie.transform(new ByteArrayInputStream(ms.toByteArray()),dest);
		System.out.println("done.");
		ms.close();

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
		ByteArrayOutputStream rs= _web.getURL(reviewURL);
		System.out.println("done.");

	
		System.out.print("XSLT parsing file...");
		_xslt_review.transform(new ByteArrayInputStream(rs.toByteArray()),dest);
		System.out.println("done.");
	
		String outputFile = "data/movies/"+title+".xml";
		OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outputFile)));
		Serializer s = SAXProcessor.getProcessor().newSerializer(out);
	
		Element root = mergeImdbRtResults(imdb_movie, imdb_reviews, original_title, year);

		s.serializeNode(_saxDocBuilder.wrap(root));
	
		System.out.println("Output written on "+outputFile+" for movie "+original_title+"("+year+")");
		System.out.println("-------------");
	
		addToFileList(title);
	}

	private Element mergeImdbRtResults ( Document imdb_movie, Document imdb_reviews, String original_title, String year )
			throws IOException, SaxonApiException {
		Document rt_movie = _docBuilder.newDocument();
		Document rt_reviews = _docBuilder.newDocument();
		Document doc = _docBuilder.newDocument();
		
		Element root = doc.createElement("movie");
		
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
			NodeList nl = imdb_movie.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				root.appendChild(doc.importNode(nl.item(i), true));
			}
			nl = rt_movie.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				root.appendChild(doc.importNode(nl.item(i), true));
			}
			nl = imdb_reviews.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				root.appendChild(doc.importNode(nl.item(i), true));
			}
			nl = rt_reviews.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				root.appendChild(doc.importNode(nl.item(i), true));
			}
		} else {
			NodeList nl = imdb_movie.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				root.appendChild(doc.importNode(nl.item(i), true));
			}
			nl = imdb_reviews.getFirstChild().getChildNodes();
			for(int i=0; i < nl.getLength(); i++) {
				root.appendChild(doc.importNode(nl.item(i), true));
			}
		}
		
		return root;
	}

	private void addToFileList(String outputFile) {
		Element item = _fileListDoc.createElement("item");
		item.appendChild(_fileListDoc.createTextNode(outputFile));
		_fileListRoot.appendChild(item);		
	}
	
	@SuppressWarnings("unchecked")
	public void extractFromFile(String fileName, int startAt, int stopAt)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		System.out.print("Processing list of movies at "+fileName+" starting from #"+startAt+".");
		ObjectInputStream ios;
		ios = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(fileName))));
		
		ArrayList<URL> readObject = (ArrayList<URL>)ios.readObject();
		
		extractMovies(readObject,startAt, stopAt);
		
		System.out.print("Done processing list of movies at "+fileName+".");
	}
	
	public void extractFromFile(String fileName)
			throws FileNotFoundException, IOException, ClassNotFoundException{
		extractFromFile(fileName, 0, 0);
	}
	
	public void extractIMDBMovieList(String year, int pageFrom, int pageTo)
			throws FileNotFoundException, IOException {
		IMDBMovieListExtractor movieExtractor= new IMDBMovieListExtractor();

		System.out.println("Extracting IMDB list, year "+ year +" pages from "+pageFrom+" to "+(pageTo-1)+".");
		ArrayList<URL> list = movieExtractor.getList(year, pageFrom, pageTo);
		String outputFile = "data/IMDB_y"+year+"("+pageFrom+","+(pageTo-1)+").object";
		System.out.println("Done, extracted "+list.size()+" movie URLs. Output at "+outputFile+".");
		ObjectOutputStream oos;

		oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(outputFile))));
	
		oos.writeObject(list);
		oos.close();
	}

	public void createFileList ( String output )
			throws FileNotFoundException, IllegalArgumentException, SaxonApiException {
		String outputFile = "data/"+output+".xml";
		OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outputFile)));
		Serializer s = SAXProcessor.getProcessor().newSerializer(out);
	
		s.serializeNode(_saxDocBuilder.wrap(_fileListRoot));
	}
	
	public static void main(String[] args) {
		try {
			Main main = new Main();
			main.extractFromFile("data/IMDB_y1960(0,9).object",1,5);
			main.createFileList("file_list");
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		} catch ( ClassNotFoundException e ) {
			e.printStackTrace();
		} catch ( ParserConfigurationException e ) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SaxonApiException e) {
			e.printStackTrace();
		}
	}
}
