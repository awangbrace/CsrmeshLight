/******************************************************************************
 Copyright Cambridge Silicon Radio Limited 2014 - 2015.
 ******************************************************************************/

package com.csr.csrmeshdemo.newUI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.ab.adapter.AbFragmentPagerAdapter;
import com.csr.csrmeshdemo.adapter.DeviceAdapter;
import com.csr.csrmeshdemo.util.DeviceController;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.base.BaseFragment;
import com.csr.csrmeshdemo.view.NormalViewPager;

import java.util.ArrayList;

/**
 * Fragment that allows controlling the colour of lights using HSV colour wheel.
 * 
 */
public class ColorModeFragment extends BaseFragment implements ViewPager.OnPageChangeListener{
    public static final String TAG = "LightControlFragment";

    private View mRootView;
    private AbFragmentPagerAdapter mFragmentPagerAdapter = null;
    private NormalViewPager mViewPager;
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();


    private HSVCircle mColorWheel = null;
    private SeekBar mBrightSlider = null;
    private DeviceController mController;
    private DeviceAdapter mDeviceListAdapter;
    private Spinner mDeviceSpinner = null;
    private Switch mPowerSwitch = null;
    private ImageView mCurrentColorView = null;
    
    private int mCurrentColor = Color.rgb(0, 0, 0);

    private boolean mEnableEvents = true;
    private boolean mEnablePowerSwitchEvent = true;
    private boolean mIsChanged = false;
    private int currectIndex = 0;
    private ImageView point1, point2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LightControlFragment","1111111111111111111111111111111");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            // The last two arguments ensure LayoutParams are inflated properly.
            mRootView = inflater.inflate(R.layout.color_mode_fragment, container, false);
            mViewPager = (NormalViewPager) mRootView
                    .findViewById(R.id.viewpage_color_mode);
            point1 = (ImageView) mRootView.findViewById(R.id.point1);
            point2 = (ImageView) mRootView.findViewById(R.id.point2);
        }
        initData();
        mViewPager.setOnPageChangeListener(this);
        return mRootView;
    }

    private void initData() {
        // TODO Auto-generated method stub
        Fragment page1 = new LightControlFragment();
        Fragment page2 = new WarmWhileLightControlFragment();
        // Fragment page3 = new AirDetectionTvocFragment();
        // Fragment page4 = new AirDetectionPM2point5Fragment();
        mFragments = new ArrayList<Fragment>();
        mFragments.add(page1);
        mFragments.add(page2);
        // mFragments.add(page3);
        // mFragments.add(page4);
        FragmentManager mFragmentManager = getChildFragmentManager();
        mFragmentPagerAdapter = new AbFragmentPagerAdapter(mFragmentManager,
                mFragments);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0, false);
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPageScrollStateChanged(int pState) {
        // TODO Auto-generated method stub
        if (ViewPager.SCROLL_STATE_IDLE == pState) {
            if (mIsChanged) {
                mIsChanged = false;
                mViewPager.setCurrentItem(0, false);
            }
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    // private static final int POINT_LENGTH = 2;

    @Override
    public void onPageSelected(int pPosition) {
        currectIndex = pPosition;
//		if (mTclBleConnector.connectionStatus == TclConnector.STATE_CONNECTED) {
        if (pPosition == 0) {
            point1.setImageResource(R.drawable.point);
            point2.setImageResource(R.drawable.point_black1);
        } else if (pPosition == 1) {
            point2.setImageResource(R.drawable.point);
            point1.setImageResource(R.drawable.point_black1);
        }

//		} else if (mTclBleConnector.connectionStatus == TclConnector.STATE_DISCONNECTED) {
//		}

    }






}
