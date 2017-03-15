package com.csr.csrmeshdemo.newUI;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.csr.csrmeshdemo.R;


public class LauncherActivity extends Activity {

	private RelativeLayout mLaunchLayout;
	private Animation mFadeIn;
	private Animation mFadeInScale;
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.launcher);
		mLaunchLayout = (RelativeLayout) findViewById(R.id.launch);
		init();
		initBle();
		setListener();

	}

	private void initBle() {
		mHandler = new Handler();
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			finish();
		}
		// TODO Auto-generated method stub
	}

	private void setListener() {

		mFadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				mLaunchLayout.startAnimation(mFadeInScale);
			}
		});

		mFadeInScale.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {

				goActivity();
				// Intent intent = new Intent();
				// if (mTclBleConnector.connectionStatus){
				// intent.setClass(LauncherActivity.this,
				// TabBottomActivity.class);
				// }else{
				// intent.setClass(LauncherActivity.this,SettingDeviceManger.class);
				// }
				// intent.putExtra("AUTO_CONNECT",true);
				// startActivity(intent);
				// finish();
			}
		});

	}

	private void goActivity() {
		SharedPreferences activityPrefs = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);

		boolean lastIdUsed = activityPrefs.getBoolean("FAST_LAUNCH", true);
		Intent intent = new Intent();
		if (lastIdUsed) {
			intent.setClass(LauncherActivity.this, MainActivity.class);
//			XMLPreferences.getInstance(this).setIsFirstStartApplication(false);
		} else {


			intent.setClass(LauncherActivity.this, MainActivity.class);

		}
		startActivity(intent);
		this.finish();

	}

	private void init() {
		initAnim();
				new Thread((new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mLaunchLayout.startAnimation(mFadeIn);

			}
		})).start();
	}

	private void initAnim() {
		mFadeIn = AnimationUtils.loadAnimation(LauncherActivity.this,
				R.anim.welcome_fade_in);
		mFadeIn.setDuration(500);
		mFadeIn.setFillAfter(true);
		mFadeInScale = AnimationUtils.loadAnimation(LauncherActivity.this,
				R.anim.welcome_fade_in_scale);
		mFadeInScale.setDuration(2000);
		mFadeInScale.setFillAfter(true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

}
