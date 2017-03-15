package com.csr.csrmeshdemo.newUI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.view.DeviceManger;
import com.csr.csrmeshdemo.view.wheelView.OnWheelChangedListener;
import com.csr.csrmeshdemo.view.wheelView.OnWheelScrollListener;
import com.csr.csrmeshdemo.view.wheelView.WheelView;
import com.csr.csrmeshdemo.view.wheelView.adapter.AbstractWheelTextAdapter;
import com.csr.mesh.DataModelApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yaohj on 2017/2/24.
 */

public class GradientActivity extends AbActivity implements SeekBar.OnSeekBarChangeListener{
    private AbTitleBar mAbTitleBar;
    private WheelView mModeWheelView;
    private TextAdapter shoursAdapter;
    private int maxsize = 20;
    private int minsize = 14;
    List<String> list_str;
    private int speed = 10;
    private int currectMode = 0;
    private SeekBar mSpeedSeekBar;
    private final int Red_Radient = 1;
    private final int Green_Radient = 2;
    private final int Blue_Radient = 3;
    private final int Purple_Radient = 4;
    private final int Yelow_Radient =5;
    private final int Cyan_Radient = 6;
    private final int Colorful_Radient = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.gradient_color_activity_layout);
        initTitleBar();
        initView();
        initrData();

    }

    private void initTitleBar(){
        mAbTitleBar = getTitleBar();
        // mAbTitleBar.getTitleTextButton().setPadding(0, 35, 0, 35);
        mAbTitleBar.setTitleText(R.string.Radient);
        // mAbTitleBar.setTitleText(R.string.title_name);
        mAbTitleBar.setTitleBarBackground(R.color.title_bar_background_color);

        mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void  initView(){
        mModeWheelView = (WheelView) findViewById(R.id.mode_wheelview);
//        initWheelDatePicker(mModeWheelView);
        mSpeedSeekBar = (SeekBar) findViewById(R.id.seekBar_speed);
        mModeWheelView.setCyclic(true);
        mModeWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) shoursAdapter.getItemText(wheel
                        .getCurrentItem());
//                mHour = currentText;
                setTextviewSize(currentText, shoursAdapter);
                Log.e("addChangingListener1", "addChangingListener1");

            }
        });
        mModeWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) shoursAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, shoursAdapter);
//                Log.e("sssss======",wheel.getCurrentItem()+"");
                currectMode = wheel.getCurrentItem();
                gradientMode(currectMode,speed);
            }
        });
    }

    private void initrData(){
        list_str = Arrays.asList(getResources().getStringArray(R.array.GradienrArr));
        shoursAdapter = new TextAdapter(GradientActivity.this, list_str,
                0, maxsize, minsize);
        mModeWheelView.setVisibleItems(5);
        mModeWheelView.setViewAdapter(shoursAdapter);
        mModeWheelView.setCurrentItem(0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        speed = progress;
        gradientMode(currectMode,speed);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class TextAdapter extends AbstractWheelTextAdapter {
        List<String> list;

        protected TextAdapter(Context context, List<String> list,
                              int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem,
                    maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    /**
     * ??????
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, GradientActivity.TextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxsize);
                textvew.setTextColor(getResources().getColor(R.color.black));
            } else {
                textvew.setTextSize(minsize);
                textvew.setTextColor(getResources().getColor(
                        R.color.black_60_translate));
            }
        }
    }


    private void gradientMode(int mode,int speed){
        speed = speed + 1 ;
        switch (mode){
            case Red_Radient:
                DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x35,0x09,0x03,(byte)speed}, false);

                break;
            case Green_Radient:
                DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x35,0x09,0x04,(byte)speed}, false);

                       break;
            case Blue_Radient:
                DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x35,0x09,0x05,(byte)speed}, false);

                break;
            case Purple_Radient:
                DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x35,0x09,0x02,(byte)speed}, false);

                break;
            case Yelow_Radient:
                DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x35,0x09,0x01,(byte)speed}, false);

                break;
            case Cyan_Radient:
                DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x35,0x09,0x00,(byte)speed}, false);
                break;
            case Colorful_Radient:
                DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x38,0x04,0x10,(byte)0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff}, false);
                break;
        }

    }


}
