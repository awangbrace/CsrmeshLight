
/******************************************************************************
 Copyright Cambridge Silicon Radio Limited 2014 - 2015.
 ******************************************************************************/

package com.csr.csrmeshdemo.newUI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ab.view.slidingmenu.SlidingMenu;
import com.ab.view.titlebar.AbTitleBar;
import com.csr.csrmeshdemo.Db.DbManger;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.base.BaseActivity;
import com.csr.csrmeshdemo.base.CsrManger;
import com.csr.csrmeshdemo.entities.AlarmBean;
import com.csr.csrmeshdemo.entities.Device;
import com.csr.csrmeshdemo.ui.NotificationFragment;
import com.csr.csrmeshdemo.view.DeviceManger;
import com.csr.mesh.DataModelApi;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    public SlidingMenu menu;
    private Timer timer = null;
    CheckBox powerButton;
    private CsrManger mCsrManger;
    private Fragment mCurrentFragment;
    private Fragment mPreFragment;
    // Variables used by the notification fragment.
    public NotificationFragment mNotificationFragment;
    private long mExitTime;
    AbTitleBar mAbTitleBar;
    private static Interpolator interp = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setAbContentView(R.layout.main);
        initData();
        mCsrManger = CsrManger.getInstance(this);

        showProgress(getString(R.string.connecting));
        showSlidingMenu();
        initTitleBar();
//        showSlidingMenu();
        // Make a connection to MeshService to enable us to use its services.

    }

    //??????
    public void showSlidingMenu(){
        //SlidingMenu???
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //slidingmenu??????????????????TOUCHMODE_MARGIN
        //?????????
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        //menu???Fragment??
        menu.setMenu(R.layout.sliding_menu_menu);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, new SlidingMenuFragment())
                .commit();
    }

    private void initTitleBar(){
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("Device");
        mAbTitleBar.setLogo(R.drawable.icon_menu);
        mAbTitleBar.setTitleBarBackground(R.color.title_bar_background_color);
        mAbTitleBar.setPadding((int)getResources().getDimension(R.dimen.titlebar_logo_padding),16,0,0);
//        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//        mAbTitleBar.setLogoLine(R.drawable.line);
//        mAbTitleBar.getLogoView().setBackgroundResource(R.drawable.button_selector_menu);
        View rightViewMore = mInflater.inflate(R.layout.menu, null);
        CheckBox powerButton = (CheckBox) rightViewMore.findViewById(R.id.powerBtn);
        mAbTitleBar.clearRightView();
        mAbTitleBar.addRightView(rightViewMore);
        mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
        powerButton.setOnCheckedChangeListener(powerChange);

        mAbTitleBar.getLogoView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.showMenu();
            }
        });

    }


    private void initData(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SlidingTabFragment mSlidingTabFragment = new SlidingTabFragment();
            ft.add(R.id.listcontainer, mSlidingTabFragment, "POSITION_TAB_MAIN");
        ft.show(mSlidingTabFragment);
        ft.commit();
    }

    protected CompoundButton.OnCheckedChangeListener powerChange = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x35,0x09,0x01,0x11}, false);

        }
    };
    @Override
    protected void onPause()
    {
        super.onPause();
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(timer ==null){
            timer = new Timer();
            startSmartAlarm();
        }
        updateCurrectDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, getString(R.string.main_activity_exit),
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mCsrManger.disconnectBridge();
        mCsrManger.remove_Handle();
        mCsrManger.unbindService();
        System.exit(0);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCsrManger.onActivityResult(requestCode, resultCode, data);
    }

    public void updateCurrectDevice(){
        if(DeviceManger.mSendDeviceId!= Device.DEVICE_ID_UNKNOWN)
            mAbTitleBar.setTitleText(mCsrManger.getDevice(DeviceManger.mSendDeviceId).getName());
    }

    public void startSmartAlarm(){

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long time=System.currentTimeMillis();
                final Calendar mCalendar=Calendar.getInstance();
                mCalendar.setTimeInMillis(time);
                int mHour=mCalendar.get(Calendar.HOUR);
                int mMinuts=mCalendar.get(Calendar.MINUTE);
                getMainLooper().prepare();
                List<AlarmBean> alarmBeanList = DbManger.getInstance(MainActivity.this).queryDbDeviceInfoData();
                getMainLooper().loop();
                for (int i = 0;i<alarmBeanList.size();i++){
                    if(mHour == alarmBeanList.get(i).getHour()&&mMinuts == alarmBeanList.get(i).getMinute()){
                        int mCurrentColor = Color.rgb(Color.red( (int) (Math.random() * 255 + 1)), Color.green( (int) (Math.random() * 255 + 1)), (int) (Math.random() * 255 + 1));
                        int mBrightSlider =  (int) (Math.random() * 99 + 1);
                        mCsrManger.setLightColor(mCurrentColor, mBrightSlider);
                    }
                }
            }
        };
        timer.schedule(timerTask, 1000, (int) (Math.random() * 500 + 1));
    }
}
