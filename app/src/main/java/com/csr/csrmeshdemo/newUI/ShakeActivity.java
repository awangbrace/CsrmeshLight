package com.csr.csrmeshdemo.newUI;


import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.csr.csrmeshdemo.util.DeviceController;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.base.CsrManger;
import com.csr.csrmeshdemo.listeners.ShakeListener;


public class ShakeActivity extends AbActivity {

	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	private DeviceController mController;
	private View mRootView;
	private ImageView image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.shake_activity);
		mController = CsrManger.getInstance(this);
		initView();
		initTitleBar();

	}



	private void initView(){
		AnimationDrawable anim = new AnimationDrawable();
		for (int i = 1; i <= 4; i++) {
			int id = getResources().getIdentifier("shake0" + i, "drawable",getPackageName());
			Drawable drawable = getResources().getDrawable(id);
			anim.addFrame(drawable, 300);
		}
		anim.setOneShot(false);
		image = (ImageView) findViewById(R.id.shake_img);

		image.setBackgroundDrawable(anim);
		//drawerSet ();//设置  drawer监听    切换 按钮的方向

		mVibrator = (Vibrator)getApplication().getSystemService(VIBRATOR_SERVICE);

		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
			public void onShake() {
				startAnim();  //开始 摇一摇手掌动画
				mShakeListener.stop();

				startVibrato(); //开始 震动
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						int mCurrentColor = Color.rgb(Color.red( (int) (Math.random() * 255 + 1)), Color.green( (int) (Math.random() * 255 + 1)), (int) (Math.random() * 255 + 1));
						int mBrightSlider =  (int) (Math.random() * 99 + 1);
						mController.setLightColor(mCurrentColor, mBrightSlider);
						mVibrator.cancel();
						mShakeListener.start();
						stopAnim();
					}
				}, 1000);
			}
		});
	}

	private void initTitleBar(){
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(getString(R.string.shake));
		mAbTitleBar.setLogo(R.drawable.icon_back);
		mAbTitleBar.setTitleBarBackground(R.color.title_bar_background_color);
		mAbTitleBar.setPadding((int)getResources().getDimension(R.dimen.titlebar_logo_padding),16,0,0);
//        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//        mAbTitleBar.setLogoLine(R.drawable.line);
//        mAbTitleBar.getLogoView().setBackgroundResource(R.drawable.button_selector_menu);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	public void startAnim () {   //定义摇一摇动画动画
		AnimationDrawable anim = new AnimationDrawable();
		for (int i = 1; i <= 4; i++) {
			int id = getResources().getIdentifier("shake0" + i, "drawable",getPackageName());
			Drawable drawable = getResources().getDrawable(id);
			anim.addFrame(drawable, 300);
		}
		anim.setOneShot(false);
		image.setBackgroundDrawable(anim);
		anim.start();
	}

	public void stopAnim() {
		AnimationDrawable anim = (AnimationDrawable) image.getBackground();
		if (anim.isRunning()) { //如果正在运行,就停止
			anim.stop();
		}
	}

	public void startVibrato(){
//		MediaPlayer player;
//		player = MediaPlayer.create(mactivity, R.raw.awe);
//		player.setLooping(false);
//		player.start();
//		player.stop();



		//定义震动
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); //第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("ssssssssssssssssssss","zzzzzzzzzzzzzzzzzzzzzzzzzz");
		stopAnim();
//		player.
		mShakeListener.stop();
	}
}