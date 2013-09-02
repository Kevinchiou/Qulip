import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.netv.vo.CatalogVO;


public class Test {
	
	public static void main(String[] args) {
		Test t = new Test();
		List<CatalogVO> vs = t.getCatalogs();
		for (CatalogVO v : vs) {
			System.out.println(v.getIcon() + "," + v.getTitle() + "," + v.getDesc() + "," + v.getPlaylistId() + "," + v.getSubs().size());
		}
	}

	private List<CatalogVO> getCatalogs() {
    	try {
	    	SAXParserFactory spf = SAXParserFactory.newInstance();
		    SAXParser sp = spf.newSAXParser();
			XMLReader reader = sp.getXMLReader();
			List<CatalogVO> records = new ArrayList<CatalogVO>();
			ContentHandler contentHandler = new RecordContentHandler(records);
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(new FileInputStream("D:\\MyDocuments\\Data\\workspace\\netv\\assets\\catalog.xml")));
			return records;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new ArrayList<CatalogVO>();
    	}
    }
    
    class RecordContentHandler implements ContentHandler {

		private StringBuffer buf = new StringBuffer();
		private String tag = null;
		private List<CatalogVO> catalogs = null;
		private Map<String, String> atts = new HashMap<String, String>();
		private CatalogVO curr = null;
		
		RecordContentHandler(List<CatalogVO> records) {
			this.catalogs = records;
		}
		
		@Override
		public void characters(char[] chars, int start, int length)
				throws SAXException {
			buf.append(chars,start,length);
		}
		
		@Override
		public void startElement(String namespaceURI, String localName, String fullName,
				Attributes attributes) throws SAXException {
			buf.setLength(0);
			tag = localName;
			for (int i=0; i<attributes.getLength(); i++) {
				atts.put(attributes.getLocalName(i), attributes.getValue(i));
			}
			if (tag.equals("item")) {
				curr = new CatalogVO();
				curr.setIcon(atts.get("icon"));	
				catalogs.add(curr);
			}
			if (tag.equals("subitem")) {
				CatalogVO sub = new CatalogVO();
				sub.setIcon(atts.get("icon"));	
				curr.addSub(sub);
				curr = sub;
			}
		}

		@Override
		public void endElement(String namespaceURI, String localName, String fullName)
				throws SAXException {
			String text = buf.toString();
			if (tag.equals("title")) {
				curr.setTitle(text);
			}
			if (tag.equals("desc")) {
				curr.setDesc(text);
			}
			if (tag.equals("playlist")) {
				curr.setPlaylistId(text);
			}
			tag = "";
			atts.clear();
		}

		@Override
		public void endDocument() throws SAXException {
			
		}

		@Override
		public void endPrefixMapping(String arg0) throws SAXException {
			
		}

		@Override
		public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
				throws SAXException {
			
		}

		@Override
		public void processingInstruction(String arg0, String arg1)
				throws SAXException {
			
		}

		@Override
		public void setDocumentLocator(Locator arg0) {
			
		}

		@Override
		public void skippedEntity(String arg0) throws SAXException {
			
		}

		@Override
		public void startDocument() throws SAXException {
			
		}

		@Override
		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			
		}
		
	}
}
