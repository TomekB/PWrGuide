package pl.pwr.guide.interior;

import pl.pwr.guide.R;
import pl.pwr.guide.interior.controller.InteriorPropertiesParser;
import pl.pwr.guide.interior.model.ClickableImageView;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class InteriorMainActivity extends Activity
{
	boolean tapped = false;
	ClickableImageView switcherView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		switcherView = (ClickableImageView) this
				.findViewById(R.id.main_image_view);
		switcherView.setContext(InteriorMainActivity.this);

		Display display = getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		switcherView.setScreenWidth(screenWidth);
		switcherView.setScreenHeight(screenHeight);

		prepareBackground(1, 1);
	}

	private void prepareBackground(int interiorId, int roomId)
	{
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
		{
			String roomPath = "/PWrGuide/INTERIOR_" + interiorId + "/Interior" + roomId;
			
			Log.d("PATH", roomPath);
			InteriorPropertiesParser parser = new InteriorPropertiesParser();
			parser.parse(roomPath, InteriorMainActivity.this, switcherView);
		}
	}
}