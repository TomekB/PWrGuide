package pl.pwr.guide.interior.model;

import java.util.ArrayList;

import pl.pwr.guide.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class InteriorGalleryImageAdapter extends BaseAdapter
{
	int mGalleryItemBackground;
	private Context mContext;
	int screenWidth, screenHeight;
	private ArrayList<String> imagesArray;

	public InteriorGalleryImageAdapter(Context c, int width, int height,
			ArrayList<String> images)
	{
		imagesArray = images;
		screenWidth = width;
		screenHeight = height;
		mContext = c;
		TypedArray a = c.obtainStyledAttributes(R.styleable.Gallery1);
		mGalleryItemBackground = a.getResourceId(
				R.styleable.Gallery1_android_galleryItemBackground, 0);
		a.recycle();
	}

	public int getCount()
	{
		return imagesArray.size();
	}

	public Object getItem(int position)
	{
		return position;
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ImageView i = new ImageView(mContext);

		i.setImageBitmap(BitmapFactory.decodeFile(imagesArray.get(position)));
		i.setLayoutParams(new Gallery.LayoutParams(screenWidth, screenHeight));
		i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		i.setBackgroundResource(mGalleryItemBackground);

		return i;
	}
}