package pl.pwr.guide.exterior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/***
 * Handles city xml.
 * 
 * @author Pawel Krawczyk
 * 
 */
public class PoiXmlHandler extends DefaultHandler {

	// name of parent node
	private String parentNode;
	static final String POI = "poi";
	static final String INTERIOR = "interior";
	static final String TRIP = "trip";

	// names of the XML tags - POI
	static final String ID = "id";
	static final String NAME = "p_name";
	static final String LATITUDE = "latitude";
	static final String LONGITUDE = "longitude";
	static final String SHORT_DESC = "short_desc";
	static final String DESCRIPTION = "description";
	static final String LINK = "link";
	static final String CATEGORY = "category";
	static final String IMAGES = "images";
	static final String IMAGE = "image";
	static final String I_NAME = "i_name";
	static final String I_DESC = "i_desc";
	static final String I_PATH = "i_path";

	private List<Poi> pois;
	private List<Image> images;
	private List<Interior> interiors;
	private List<Trip> trips;
	private Poi poi;
	private Image image;
	private Interior interior;
	private Trip trip;
	private StringBuilder builder;

	public List<Poi> getPois() {
		return pois;
	}

	public List<Interior> getInteriors() {
		return interiors;
	}

	public List<Trip> getTrips() {
		return trips;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);
		if (this.poi != null && parentNode.equals(POI)) {
			String currentValue = builder.toString().trim();
			if (localName.equalsIgnoreCase(NAME)) {
				poi.setName(currentValue);
			} else if (localName.equalsIgnoreCase(LATITUDE)) {
				poi.setLatitude(Double.parseDouble(currentValue));
			} else if (localName.equalsIgnoreCase(LONGITUDE)) {
				poi.setLongitude(Double.parseDouble(currentValue));
			} else if (localName.equalsIgnoreCase(SHORT_DESC)) {
				poi.setShortDescription(currentValue);
			} else if (localName.equalsIgnoreCase(DESCRIPTION)) {
				poi.setDescription(currentValue);
			} else if (localName.equalsIgnoreCase(LINK)) {
				poi.setLink(currentValue);
			} else if (localName.equalsIgnoreCase(CATEGORY)) {
				// poi.setCategory(currentValue); //TODO handle categories ENUM
			} else if (localName.equalsIgnoreCase(I_NAME)) {
				image.setName(currentValue);
			} else if (localName.equalsIgnoreCase(I_DESC)) {
				image.setDescription(currentValue);
			} else if (localName.equalsIgnoreCase(I_PATH)) {
				
				byte[] data = null;

				DefaultHttpClient mHttpClient = new DefaultHttpClient();
				HttpGet mHttpGet = new HttpGet(currentValue);
				HttpResponse mHttpResponse = null;
				
				try {
					mHttpResponse = mHttpClient.execute(mHttpGet);
					if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = mHttpResponse.getEntity();
						if (entity != null) {
							data = EntityUtils.toByteArray(entity);
						}
					}
					image.setData(data);
				} catch (IOException e) { 
					e.printStackTrace();
				}
			} else if (localName.equalsIgnoreCase(IMAGE)) {
				images.add(image);
			} else if (localName.equalsIgnoreCase(IMAGES)) {
				poi.setImages(images);
			}

			builder.setLength(0);
		} else if (this.interior != null && parentNode.equals(INTERIOR)) {
			// TODO handle interiors nodes
		} else if (this.trip != null && parentNode.equals(TRIP)) {
			// TODO TOMAS handle trip nodes
		}
		if (localName.equalsIgnoreCase(POI)) {
			pois.add(poi);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		pois = new ArrayList<Poi>();
		interiors = new ArrayList<Interior>();
		trips = new ArrayList<Trip>();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equalsIgnoreCase(POI)) {

			poi = new Poi();
			parentNode = POI;
			poi.setId(Long.parseLong(attributes.getValue(0)));
		} else if (localName.equalsIgnoreCase(IMAGES)) {

			images = new ArrayList<Image>();
			parentNode = IMAGES;
		} else if (localName.equalsIgnoreCase(IMAGE)) {

			image = new Image();
		} else if (localName.equalsIgnoreCase(INTERIOR)) {

			interior = new Interior();
			parentNode = INTERIOR;
		} else if (localName.equalsIgnoreCase(TRIP)) {

			trip = new Trip();
			parentNode = TRIP;
		}
	}
}
