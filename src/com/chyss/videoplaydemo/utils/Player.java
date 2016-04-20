package com.chyss.videoplaydemo.utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import com.chyss.videoplaydemo.R;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;

/**
 *  视频播放的控制工具类，提供播放，暂停等功能
 *  
 *  @author qinchuo 2016-2-16
 */
public class Player implements OnBufferingUpdateListener, OnCompletionListener,
		MediaPlayer.OnPreparedListener, SurfaceHolder.Callback,OnVideoSizeChangedListener
{
	private int videoWidth;
	private int videoHeight;
	public MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private SeekBar skbProgress;
	private ImageView playbtn;
	private ProgressBar videoProgressBar;
	public boolean isplay = false;
	public Timer mTimer;
	private SurfaceView surfaceView;

	public Player(SurfaceView surfaceView, SeekBar skbProgress,ImageView playbtn,ProgressBar videoProgressBar)
	{
		this.surfaceView = surfaceView;
		this.skbProgress = skbProgress;
		this.playbtn = playbtn;
		this.videoProgressBar = videoProgressBar;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	class SkbTimerTask extends TimerTask
	{

		@Override
		public void run()
		{
			Log.d("mediaPlayer", "run()");
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying()&& skbProgress != null && skbProgress.isPressed() == false)
			{
				handleProgress.sendEmptyMessage(0);
			}
			if (!mediaPlayer.isPlaying())
			{
				handleProgress.sendEmptyMessage(1);
			}
		}
	}

	Handler handleProgress = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				int position = mediaPlayer.getCurrentPosition();
				int duration = mediaPlayer.getDuration();

				if (duration > 0)
				{
					long pos = skbProgress.getMax() * position / duration;
					skbProgress.setProgress((int) pos);
				}
				isplay = false;
				playbtn.setImageResource(R.drawable.video_pause_btn_selector);
				break;
			case 1:
				isplay = true;
				playbtn.setImageResource(R.drawable.video_play_btn_selector);
				break;
			default:
				break;
			}
		};
	};

	public void play()
	{
		mediaPlayer.start();
	}

	public void playUrl(String videoUrl)
	{
		try
		{
			mediaPlayer.reset();
			
			mediaPlayer.setOnErrorListener(new OnErrorListener()
			{
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra)
				{
					Log.d("mediaPlayer","what = "+what+",extra = "+extra);
					return false;
				}
			});
			
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.setDisplay(surfaceView.getHolder());
			mediaPlayer.prepare();
			//mediaPlayer.start();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void pause()
	{
		mediaPlayer.pause();
	}

	public void stop()
	{
		if (mediaPlayer != null)
		{
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		Log.d("mediaPlayer", "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		try
		{
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnVideoSizeChangedListener(this);
		}
		catch (Exception e)
		{
			Log.e("mediaPlayer", "error", e);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		Log.d("mediaPlayer", "surface destroyed");
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0)
		{
			mp.start();
		}
		mTimer = new Timer();
		mTimer.schedule(new SkbTimerTask(), 0, 1000);
		playbtn.setClickable(true);
		videoProgressBar.setVisibility(View.GONE);
		Log.d("mediaPlayer", "onPrepared : "+mp.getDuration());
	}

	@Override
	public void onCompletion(MediaPlayer player)
	{
		
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress)
	{
		skbProgress.setSecondaryProgress(bufferingProgress);
		int currentProgress = skbProgress.getMax()
				* mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
		
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
	{
		Log.d("mediaPlayer", "mp.getDuration() = "+mp.getDuration()+",w = "+surfaceView.getWidth()+",width = "+width+",height = "+height);
		if (width == 0 || height == 0)
		{
			return;
		}
		
		int w = surfaceView.getWidth();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, w * height / width);
		surfaceView.setLayoutParams(layoutParams);
	}
}