package pl.pwr.guide.exterior.utils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/***
 * Handles provider xml.
 * 
 * @author Pawel Krawczyk
 * 
 */
public class ProviderXmlHandler extends DefaultHandler
{
	static final String PROVIDER = "provider";
	static final String VERSION = "version";
	static final String URL = "url";

	private StringBuilder builder;

	private List<String> urlList;
	private int version;

	public List<String> getUrlList()
	{
		return urlList;
	}

	public int getVersion()
	{
		return version;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException
	{
		super.endElement(uri, localName, name);

		String currentValue = builder.toString().trim();
		if (localName.equalsIgnoreCase(URL))
		{
			urlList.add(currentValue);
		}
	}

	@Override
	public void startDocument() throws SAXException
	{
		super.startDocument();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);

		if (localName.equalsIgnoreCase(PROVIDER))
		{
			urlList = new ArrayList<String>();
			this.version = Integer.parseInt(attributes.getQName(0));
		}
	}
}
