package pl.pwr.guide.interior.model;

import java.util.ArrayList;

import pl.pwr.guide.R;
import pl.pwr.guide.interior.InteriorGallery;
import pl.pwr.guide.interior.VideoPlayer;
import pl.pwr.guide.interior.controller.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ClickableImageView extends ImageView
{
	private static final int POINT_RADIUS = 25;

	private int screenWidth;
	private int screenHeight;
	private int bitmapWidth;
	private int bitmapHeight;
	private ClickablePoint lastChoosenPoint;
	private ArrayList<ClickablePoint> clickablePoints;
	private String roomPath;

	boolean tapped;
	private MediaPlayer mediaPlayer;

	// set maximum scroll amount (based on center of image)
	int maxX = (int) ((bitmapWidth / 2) - (screenWidth / 2));
	int maxY = (int) ((bitmapHeight / 2) - (screenHeight / 2));

	// set scroll limits
	int maxLeft = (maxX * -1);
	int maxRight = maxX;
	int maxTop = (maxY * -1);
	int maxBottom = maxY;

	float downX, downY;
	int totalX, totalY;
	int scrollByX, scrollByY;
	private Context context;

	private final Handler handler = new Handler();

	public void setRoomPath(String roomPath)
	{
		this.roomPath = roomPath;
	}

	public ClickableImageView(Context context)
	{
		super(context);
		this.context = context;
		clickablePoints = new ArrayList<ClickablePoint>();
	}

	public ClickableImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		clickablePoints = new ArrayList<ClickablePoint>();
	}

	@Override
	public void setImageBitmap(Bitmap bm)
	{
		super.setImageBitmap(bm);

		bitmapWidth = bm.getWidth();
		bitmapHeight = bm.getHeight();

		// set maximum scroll amount (based on center of image)
		maxX = (int) ((bitmapWidth / 2) - (screenWidth / 2));
		maxY = (int) ((bitmapHeight / 2) - (screenHeight / 2));

		// set scroll limits
		maxLeft = (maxX * -1);
		maxRight = maxX;
		maxTop = (maxY * -1);
		maxBottom = maxY;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// int posX = getScrollX() + bitmapWidth / 2 - screenWidth / 2;
		// int posY = getScrollY() + bitmapHeight / 2 - screenHeight / 2;
		// Log.d("ACTUALC SCROLL: ", posX + " " + posY);

		for (ClickablePoint point : clickablePoints)
		{
			Paint paint = new Paint();
			paint.setARGB(255, 0, 255, 0);
			canvas.drawCircle(point.getPosX() - bitmapWidth / 2 + screenWidth
					/ 2, point.getPosY() - bitmapHeight / 2 + screenHeight / 2,
					15, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float currentX, currentY;
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			tapped = true;
			break;

		case MotionEvent.ACTION_UP:
			Log.d("TAPPED: ", tapped + "");
			if (tapped)
			{
				checkPoints(event.getX(), event.getY());
			}
			break;

		case MotionEvent.ACTION_MOVE:
			currentX = event.getX();
			currentY = event.getY();
			scrollByX = (int) (downX - currentX);
			scrollByY = (int) (downY - currentY);
			tapped = false;

			// scrolling to left side of image (pic moving to the right)
			if (currentX > downX)
			{
				if (totalX == maxLeft)
				{
					scrollByX = 0;
				}
				if (totalX > maxLeft)
				{
					totalX = totalX + scrollByX;
				}
				if (totalX < maxLeft)
				{
					scrollByX = maxLeft - (totalX - scrollByX);
					totalX = maxLeft;
				}
			}

			// scrolling to right side of image (pic moving to the left)
			if (currentX < downX)
			{
				if (totalX == maxRight)
				{
					scrollByX = 0;
				}
				if (totalX < maxRight)
				{
					totalX = totalX + scrollByX;
				}
				if (totalX > maxRight)
				{
					scrollByX = maxRight - (totalX - scrollByX);
					totalX = maxRight;
				}
			}

			// scrolling to top of image (pic moving to the bottom)
			if (currentY > downY)
			{
				if (totalY == maxTop)
				{
					scrollByY = 0;
				}
				if (totalY > maxTop)
				{
					totalY = totalY + scrollByY;
				}
				if (totalY < maxTop)
				{
					scrollByY = maxTop - (totalY - scrollByY);
					totalY = maxTop;
				}
			}

			// scrolling to bottom of image (pic moving to the top)
			if (currentY < downY)
			{
				if (totalY == maxBottom)
				{
					scrollByY = 0;
				}
				if (totalY < maxBottom)
				{
					totalY = totalY + scrollByY;
				}
				if (totalY > maxBottom)
				{
					scrollByY = maxBottom - (totalY - scrollByY);
					totalY = maxBottom;
				}
			}

			scrollBy(scrollByX, scrollByY);
			downX = currentX;
			downY = currentY;
			break;
		}
		return true;
	}

	private void checkPoints(float posX, float posY)
	{
		for (ClickablePoint point : clickablePoints)
		{
			Log.d("CURRENT SCROLL: ", downX + " " + downY + " " + posX + " "
					+ posY + " " + totalX + " " + totalY + " " + scrollByX
					+ " " + scrollByY);
			if (posX > point.getPosX() - POINT_RADIUS
					&& posX < point.getPosX() + POINT_RADIUS
					&& posY > point.getPosY() - POINT_RADIUS
					&& posY < point.getPosY() + POINT_RADIUS)
			{
				if (point.getPointType() == Utils.POINT_TYPE_POI)
				{
					lastChoosenPoint = point;
					final Dialog dialog = new Dialog(context);
					dialog.setContentView(R.layout.short_desc);
					dialog.setTitle(point.getName());
					dialog.setCancelable(true);

					TextView text = (TextView) dialog
							.findViewById(R.id.short_desc_textview);
					text.setText(point.getShortDescription());

					Button button = (Button) dialog
							.findViewById(R.id.short_desc_close_button);
					button.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
						}
					});

					button = (Button) dialog
							.findViewById(R.id.short_desc_expand_button);
					button.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
							showFullDescription(lastChoosenPoint);
						}
					});

					dialog.show();
				} else if (point.getPointType() == Utils.POINT_TYPE_TRANSITION)
				{
					// przejdz do innego pokoju
				} else if (point.getPointType() == Utils.POINT_TYPE_EXIT)
				{
					// zamknij ca¸y modu¸
				}
			}
		}
	}

	private void showFullDescription(ClickablePoint point)
	{
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.full_desc);
		dialog.setTitle(point.getName());
		dialog.setCancelable(true);

		TextView text = (TextView) dialog.findViewById(R.id.full_desc_textview);
		text.setText(point.getFullDescription());

		Button button = (Button) dialog
				.findViewById(R.id.full_desc_close_button);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});

		button = (Button) dialog.findViewById(R.id.full_desc_gallery_button);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ArrayList<String> paths = new ArrayList<String>();
				ArrayList<String> description = new ArrayList<String>();

				Intent i = new Intent(context, InteriorGallery.class);

				for (MultimediaObject object : lastChoosenPoint.getImages())
				{
					paths.add(roomPath + "/" + object.getPath());
					description.add(object.getDescription());
				}

				i.putExtra("paths", paths);
				i.putExtra("descriptions", description);

				context.startActivity(i);
			}
		});
		button.setEnabled(point.getImages().size() > 0);

		button = (Button) dialog.findViewById(R.id.full_desc_videos_button);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(context, VideoPlayer.class);

				i.putExtra("path", roomPath + "/"
						+ lastChoosenPoint.getVideos().get(0).getPath());

				context.startActivity(i);
			}
		});
		button.setEnabled(point.getVideos().size() > 0);

		button = (Button) dialog.findViewById(R.id.full_desc_music_button);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// show alert with list
				try
				{
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setDataSource(roomPath + "/"
							+ lastChoosenPoint.getSongs().get(0).getPath());
					mediaPlayer.prepare();

					SongDialog dialog = new SongDialog(
							context,
							mediaPlayer,
							lastChoosenPoint.getSongs().get(0).getDescription(),
							handler);
					dialog.show();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		button.setEnabled(point.getSongs().size() > 0);

		dialog.show();
	}

	public int getScreenWidth()
	{
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth)
	{
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight()
	{
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight)
	{
		this.screenHeight = screenHeight;
	}

	public int getBitmapWidth()
	{
		return bitmapWidth;
	}

	public void setBitmapWidth(int bitmapWidth)
	{
		this.bitmapWidth = bitmapWidth;
	}

	public int getBitmapHeight()
	{
		return bitmapHeight;
	}

	public void setBitmapHeight(int bitmapHeight)
	{
		this.bitmapHeight = bitmapHeight;
	}

	public ArrayList<ClickablePoint> getClickablePoints()
	{
		return clickablePoints;
	}

	public void setClickablePoints(ArrayList<ClickablePoint> clickablePoints)
	{
		this.clickablePoints = clickablePoints;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}
}
