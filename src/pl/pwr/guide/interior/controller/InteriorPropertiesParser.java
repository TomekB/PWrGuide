package pl.pwr.guide.interior.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import pl.pwr.guide.interior.model.ClickableImageView;
import pl.pwr.guide.interior.model.ClickablePoint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class InteriorPropertiesParser
{
	public void parse(String path, Context context, ClickableImageView imageView)
	{
		File dir = Environment.getExternalStorageDirectory();
		File myFile = new File(dir, path + "/config.xml");
		Log.d("PATH2", myFile.getAbsolutePath());
		try
		{
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			InteriorHandler myXMLHandler = new InteriorHandler();
			xr.setContentHandler(myXMLHandler);
			xr.parse(new InputSource(new FileInputStream(myFile)));

			Bitmap bitmap = BitmapFactory.decodeFile(dir + path + "/"
					+ myXMLHandler.getBackgroundPath());
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			imageView.setBitmapWidth(bitmapWidth);
			imageView.setBitmapHeight(bitmapHeight);

			ArrayList<ClickablePoint> clickablePoints = myXMLHandler
					.getClickablePoints();
			imageView.setClickablePoints(clickablePoints);
			imageView.setImageBitmap(bitmap);
			
			imageView.setRoomPath(dir + path);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
