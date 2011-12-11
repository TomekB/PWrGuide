package pl.pwr.guide.interior;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity
{
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		VideoView videoView = new VideoView(getApplicationContext());
		setContentView(videoView);
		videoView.setVideoPath(getIntent().getStringExtra("path"));
		videoView.setMediaController(new MediaController(this));
		videoView.requestFocus();
		videoView.start();
	}
}
