package com.chyss.videoplaydemo;

import com.chyss.videoplaydemo.utils.TimeUtils;
import com.chyss.videoplaydemo.views.VideoView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{
	public static String TAG = "MainActivity";
	
	Button netVideoBtn, localVideoBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
	}

	private void initView()
	{
		netVideoBtn = (Button) findViewById(R.id.button1);
		localVideoBtn = (Button) findViewById(R.id.button2);
		
		netVideoBtn.setOnClickListener(onClickListener);
		localVideoBtn.setOnClickListener(onClickListener);
	}

	/**
	 * 控件点击事件监听处理
	 */
	OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.button1:
				netPlay();
				break;
			case R.id.button2:
				localPlay();
				break;
			}
		}
	};

	/**
	 * 播放网络视频
	 */
	protected void netPlay()
	{
		VideoView.playVideo(MainActivity.this, "http://www.midea.com/video/masvod/public/2015/02/28/20150228_14bcec18032_r1_800k.mp4");
	}

	/**
	 * 播放本地视频
	 */
	protected void localPlay()
	{
		VideoView.playVideo(MainActivity.this,"file://"+Environment.getExternalStorageDirectory()
				.getPath() + "/hello.mp4" );
	}
}
