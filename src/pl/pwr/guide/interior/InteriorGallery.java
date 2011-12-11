package pl.pwr.guide.interior;

import java.util.ArrayList;

import pl.pwr.guide.R;
import pl.pwr.guide.interior.model.InteriorGalleryImageAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class InteriorGallery extends Activity
{
	private ArrayList<String> imagePaths;
	private ArrayList<String> imageDescriptions;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.interior_gallery);

		imagePaths = getIntent().getStringArrayListExtra("paths");
		imageDescriptions = getIntent().getStringArrayListExtra("descriptions");

		Display display = getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();

		Gallery g = (Gallery) findViewById(R.id.interior_gallery);
		g.setAdapter(new InteriorGalleryImageAdapter(this, screenWidth,
				screenHeight, imagePaths));

		g.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id)
			{
				if (imageDescriptions.get(position) != null)
				{
					Toast.makeText(InteriorGallery.this,
							imageDescriptions.get(position), Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}
}