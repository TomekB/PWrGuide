package pl.pwr.guide.exterior.utils;

import java.util.List;
import org.xml.sax.helpers.DefaultHandler;

import pl.pwr.guide.exterior.model.Poi;
import android.util.Xml;

public class SaxFeedParser extends BaseFeedParser
{
	private int version;
	private DefaultHandler handler;
	private List<Poi> poiList;

	public SaxFeedParser(String feedUrl, int version)
	{
		super(feedUrl);
		this.version = version;
	}

	public int parse()
	{
		handler = new PoiXmlHandler();
		try
		{
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, handler);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		int xmlVersion = ((PoiXmlHandler) handler).getVersion();
		if (version == xmlVersion)
		{
			
			this.poiList = ((PoiXmlHandler) handler).getPois();			
		}
		return xmlVersion;
	}

	public List<Poi> getPois()
	{
		return poiList;
	}

}
