package com.chyss.videoplaydemo.views;

import java.util.Timer;
import java.util.TimerTask;

import com.chyss.videoplaydemo.R;
import com.chyss.videoplaydemo.utils.Player;
import com.chyss.videoplaydemo.utils.TimeUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * 视频播放类，视频的播放和控制
 * 
 * @author qinchuo 2016-2-18
 *
 */
public class VideoView extends Activity implements Runnable
{
	public static final String TAG = "VideoView";
	public static final int DismissTime = 10000;
	private Timer timer;
	private SurfaceView surfaceView;
	private ImageView playIcon;
	private SeekBar skbProgress;
	private Player player;
	private TextView playCurplase;
	private boolean isFirst = true;
	private boolean dismiss = true;
	private RelativeLayout videoControl;
	private ProgressBar videoProgressBar;
	private String videoUrl = "http://www.midea.com/video/masvod/public/2015/02/28/20150228_14bcec18032_r1_800k.mp4";
	
	/**
	 * 播放模块入口，需要传人视频URL参数
	 * 
	 * @param context
	 * @param videoUrl
	 */
	public static void playVideo(Context context,String videoUrl)
	{
		Log.d("mediaPlayer", videoUrl);
		
		Intent intent = new Intent(context, VideoView.class);
		intent.putExtra("videourl", videoUrl);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_play_layout);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		initView();
		initData();
		if (getIntent() != null)
		{
			videoUrl = getIntent().getStringExtra("videourl");
		}
	}

	/***
	 * 初始化数据、屏幕关闭、来电状态监听
	 */
	private void initData()
	{
		player = new Player(surfaceView, skbProgress, playIcon,
				videoProgressBar);
		
		//屏幕关闭
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, mFilter);

		//来电状态
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(new MobliePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	private void initView()
	{
		videoProgressBar = (ProgressBar) findViewById(R.id.video_progressBar);
		playIcon = (ImageView) findViewById(R.id.play_icon);
		playCurplase = (TextView) findViewById(R.id.video_curplace);
		surfaceView = (SurfaceView) findViewById(R.id.video_playview);
		videoControl = (RelativeLayout) findViewById(R.id.video_play_control_layout);
		skbProgress = (SeekBar) findViewById(R.id.video_controlprogress);

		playIcon.setOnClickListener(onClickListener);
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		surfaceView.setOnClickListener(onClickListener);
	}

	/**
	 * 电话监听器类 / 当电话来电时，需暂停视频播放
	 * 
	 * @author qinchuo
	 */
	private class MobliePhoneStateListener extends PhoneStateListener
	{
		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			switch (state)
			{
			case TelephonyManager.CALL_STATE_IDLE: // 挂机状态

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 通话状态

				break;
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态，暂停视频播放
				player.isplay = true;
				playIcon.setImageResource(R.drawable.video_play_btn_selector);
				player.pause();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 播放视图和播放按钮的点击事件处理
	 */
	OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.play_icon:
				//播放按钮
				playOrPauseVideo();
				break;
			case R.id.video_playview:
				//当点击播放视图时，隐藏和显示播放控制控件
				showControlView();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 播放进度条事件监听，当滑动进度条时，视频调整相应进度
	 * 
	 * @author qinchuo
	 *
	 */
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener
	{
		int progress;  //播放进度

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser)
		{
			//根据当前进度条位置，计算对应视频播放进度
			this.progress = progress * player.mediaPlayer.getDuration()
					/ seekBar.getMax();
			
			playCurplase.setText(TimeUtils.changeToTimeStr(player.mediaPlayer
					.getCurrentPosition())
					+ "/"
					+ TimeUtils.changeToTimeStr(player.mediaPlayer
							.getDuration()));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			//根据进度条进度，调整视频播放进度
			player.mediaPlayer.seekTo(progress);
		}
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(mReceiver);
		if (player.mTimer != null)
		{
			player.mTimer.cancel();
			player.mTimer = null;
		}
		player.stop();

		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
		super.onDestroy();
	}

	/**
	 * 播放按钮的点击处理
	 */
	protected void playOrPauseVideo()
	{
		if (isFirst)
		{
			isFirst = false;
			player.isplay = false;
			playIcon.setClickable(false);
			videoProgressBar.setVisibility(View.VISIBLE);
			playIcon.setImageResource(R.drawable.video_pause_btn_selector);

			new Thread(VideoView.this).start();
			
			timer = new Timer();
			timer.schedule(new TimerDismiss(), DismissTime);
		}
		else if (player.isplay)
		{
			player.isplay = false;
			playIcon.setImageResource(R.drawable.video_pause_btn_selector);
			player.play();
			// playIcon.setClickable(false);
		}
		else
		{
			player.isplay = true;
			playIcon.setImageResource(R.drawable.video_play_btn_selector);
			player.pause();
		}
	}

	protected void showControlView()
	{
		if (dismiss)
		{
			dismiss = false;
			videoControl.setVisibility(View.GONE);
			if (timer != null)
			{
				timer.cancel();
			}
		}
		else
		{
			dismiss = true;
			videoControl.setVisibility(View.VISIBLE);
			timer = new Timer();
			timer.schedule(new TimerDismiss(), 5000);
		}
	}

	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (dismiss)
			{
				dismiss = false;
				videoControl.setVisibility(View.GONE);
			}
		}
	};

	class TimerDismiss extends TimerTask
	{
		@Override
		public void run()
		{
			handler.sendEmptyMessage(0);
		}
	}

	@Override
	public void run()
	{
		try
		{
			player.playUrl(videoUrl);
		}
		catch (Exception e)
		{
			Thread.currentThread().interrupt();
			Log.e(TAG, "play video error : " + e);
		}

	}

	// 动态广播的注册，监听屏幕的锁屏、电话的来电
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
			{
				player.isplay = true;
				playIcon.setImageResource(R.drawable.video_play_btn_selector);
				player.pause();
			}
		}
	};

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		//退出界面动画
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
}
