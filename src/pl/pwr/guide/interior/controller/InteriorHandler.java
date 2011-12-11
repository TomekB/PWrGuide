package pl.pwr.guide.interior.controller;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.pwr.guide.interior.model.ClickablePoint;
import pl.pwr.guide.interior.model.MultimediaObject;

public class InteriorHandler extends DefaultHandler
{
	String currentValue = null;

	private boolean inBackground = false;
	private String backgroundPath = null;

	private ArrayList<ClickablePoint> clickablePoints = null;
	private ClickablePoint currentPoint = null;
	private boolean inPoints = false;
	private boolean inPoint = false;
	private boolean inPointId = false;
	private boolean inPointName = false;
	private boolean inPointXPos = false;
	private boolean inPointYPos = false;
	private boolean inPointShortDesc = false;
	private boolean inPointLongDesc = false;
	private boolean inPointLink = false;
	private boolean inPointType = false;
	private boolean inPointConnectionReference = false;
	private boolean inMultimediaObjects = false;
	private boolean inMultimediaObject = false;
	private boolean inMultimediaType = false;
	private boolean inMultimediaName = false;
	private boolean inMultimediaDesc = false;
	private boolean inMultimediaPath = false;

	private ArrayList<MultimediaObject> multimediaObjects = null;
	private MultimediaObject currentMultimediaObject = null;

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{

//		Log.d("START", localName);
		currentValue=""; 
		
		if (localName.equals("background"))
		{
			inBackground = true;
		} else if (localName.equals("points"))
		{
			inPoints = true;
			clickablePoints = new ArrayList<ClickablePoint>();
		} else if (localName.equals("point"))
		{
			inPoint = true;
			currentPoint = new ClickablePoint();
		} else if (localName.equals("point_id"))
		{
			inPointId = true;
		} else if (localName.equals("point_name"))
		{
			inPointName = true;
		} else if (localName.equals("x_pos"))
		{
			inPointXPos = true;
		} else if (localName.equals("y_pos"))
		{
			inPointYPos = true;
		} else if (localName.equals("short_desc"))
		{
			inPointShortDesc = true;
		} else if (localName.equals("description"))
		{
			inPointLongDesc = true;
		} else if (localName.equals("link"))
		{
			inPointLink = true;
		} else if (localName.equals("type"))
		{
			inPointType = true;
		} else if (localName.equals("connection_reference"))
		{
			inPointConnectionReference = true;
		} else if (localName.equals("multimedia_objects"))
		{
			inMultimediaObjects = true;
			multimediaObjects = new ArrayList<MultimediaObject>();
		} else if (localName.equals("multimedia_object"))
		{
			inMultimediaObject = true;
			currentMultimediaObject = new MultimediaObject();
		} else if (localName.equals("multimedia_type"))
		{
			inMultimediaType = true;
		} else if (localName.equals("multimedia_name"))
		{
			inMultimediaName = true;
		} else if (localName.equals("multimedia_desc"))
		{
			inMultimediaDesc = true;
		} else if (localName.equals("multimedia_path"))
		{
			inMultimediaPath = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
//		Log.d("END", localName);
		currentValue="";

		if (localName.equals("background"))
		{
			inBackground = false;
		} else if (localName.equals("points"))
		{
			inPoints = false;
		} else if (localName.equals("point"))
		{
			inPoint = false;
			clickablePoints.add(currentPoint);
			currentPoint = null;
		} else if (localName.equals("point_id"))
		{
			inPointId = false;
		} else if (localName.equals("point_name"))
		{
			inPointName = false;
		} else if (localName.equals("x_pos"))
		{
			inPointXPos = false;
		} else if (localName.equals("y_pos"))
		{
			inPointYPos = false;
		} else if (localName.equals("short_desc"))
		{
			inPointShortDesc = false;
		} else if (localName.equals("description"))
		{
			inPointLongDesc = false;
		} else if (localName.equals("link"))
		{
			inPointLink = false;
		} else if (localName.equals("type"))
		{
			inPointType = false;
		} else if (localName.equals("connection_reference"))
		{
			inPointConnectionReference = false;
		} else if (localName.equals("multimedia_objects"))
		{
			inMultimediaObjects = false;
			currentPoint.setMultimedia(multimediaObjects);
			multimediaObjects = null;
		} else if (localName.equals("multimedia_object"))
		{
			inMultimediaObject = false;
			multimediaObjects.add(currentMultimediaObject);
			currentMultimediaObject = null;
		} else if (localName.equals("multimedia_type"))
		{
			inMultimediaType = false;
		} else if (localName.equals("multimedia_name"))
		{
			inMultimediaName = false;
		} else if (localName.equals("multimedia_desc"))
		{
			inMultimediaDesc = false;
		} else if (localName.equals("multimedia_path"))
		{
			inMultimediaPath = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		String tempValue = new String(ch, start, length);
		currentValue +=tempValue;
		
		if (inBackground)
		{
			backgroundPath = currentValue;
		} else if (inPointId)
		{
			currentPoint.setId(Integer.parseInt(currentValue));
		} else if (inPointName)
		{
			currentPoint.setName(currentValue);
		} else if (inPointXPos)
		{
			currentPoint.setPosX(Integer.parseInt(currentValue));
		} else if (inPointYPos)
		{
			currentPoint.setPosY(Integer.parseInt(currentValue));
		} else if (inPointShortDesc)
		{
			currentPoint.setShortDescription(currentValue);
		} else if (inPointLongDesc)
		{
			currentPoint.setFullDescription(currentValue);
		} else if (inPointLink)
		{
			currentPoint.setLink(currentValue);
		} else if (inPointType)
		{
			currentPoint.setPointType(Integer.parseInt(currentValue));
		} else if (inPointConnectionReference)
		{
			currentPoint.setConnectionReference(Integer.parseInt(currentValue));
		} else if (inMultimediaType)
		{
			currentMultimediaObject.setType(Integer.parseInt(currentValue));
		} else if (inMultimediaName)
		{
			currentMultimediaObject.setName(currentValue);
		} else if (inMultimediaDesc)
		{
			currentMultimediaObject.setDescription(currentValue);
		} else if (inMultimediaPath)
		{
			currentMultimediaObject.setPath(currentValue);
		}
	}

	public String getBackgroundPath()
	{
		return backgroundPath;
	}

	public ArrayList<ClickablePoint> getClickablePoints()
	{
		return clickablePoints;
	}
}
