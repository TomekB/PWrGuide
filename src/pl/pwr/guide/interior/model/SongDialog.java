package pl.pwr.guide.interior.model;

import pl.pwr.guide.R;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;

public class SongDialog extends Dialog implements
		android.view.View.OnClickListener
{
	Button buttonPlayStop, buttonClose;
	SeekBar seekBar;
	MediaPlayer mediaPlayer;
	Handler handler;

	public SongDialog(Context context, final MediaPlayer mediaPlayer,
			String title, Handler handler)
	{
		super(context);

		setContentView(R.layout.song_player);
		setTitle(title);
		setCancelable(true);

		this.mediaPlayer = mediaPlayer;
		this.handler = handler;

		buttonPlayStop = (Button) findViewById(R.id.song_player_play_button);
		buttonPlayStop.setOnClickListener(this);
		buttonClose = (Button) findViewById(R.id.song_player_close_button);
		buttonClose.setOnClickListener(this);

		seekBar = (SeekBar) findViewById(R.id.song_player_seekbar);
		seekBar.setMax(mediaPlayer.getDuration());
		seekBar.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (mediaPlayer.isPlaying())
				{
					SeekBar sb = (SeekBar) v;
					mediaPlayer.seekTo(sb.getProgress());
				}
				return false;
			}
		});
	}

	public void startPlayProgressUpdater()
	{
		seekBar.setProgress(mediaPlayer.getCurrentPosition());

		if (mediaPlayer.isPlaying())
		{
			Runnable notification = new Runnable()
			{
				public void run()
				{
					startPlayProgressUpdater();
				}
			};
			handler.postDelayed(notification, 1000);
		} else
		{
			mediaPlayer.pause();
			buttonPlayStop.setText(R.string.play);
			seekBar.setProgress(0);
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v == buttonPlayStop)
		{
			if (!mediaPlayer.isPlaying())
			{
				buttonPlayStop.setText(R.string.pause);
				try
				{
					mediaPlayer.start();
					startPlayProgressUpdater();
				} catch (IllegalStateException e)
				{
					e.printStackTrace();
					mediaPlayer.pause();
				}
			} else
			{
				buttonPlayStop.setText(R.string.play);
				mediaPlayer.pause();
			}
		} else if (v == buttonClose)
		{
			if (mediaPlayer.isPlaying())
			{
				mediaPlayer.stop();
			}
			dismiss();
		}
	}
}
