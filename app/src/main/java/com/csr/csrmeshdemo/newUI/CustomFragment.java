package com.csr.csrmeshdemo.newUI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.csr.csrmeshdemo.util.DeviceController;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.adapter.Gridadapter;
import com.csr.csrmeshdemo.base.BaseFragment;
import com.csr.csrmeshdemo.base.CsrManger;
import com.csr.csrmeshdemo.view.DeviceManger;
import com.csr.mesh.DataModelApi;


/**
 * Created by Shadow Blade on 2016-12-26.
 */

public class CustomFragment extends BaseFragment {

    private View mRootView;
    private HSVCircle mColorWheel = null;
    private DeviceController mController;
    private GridView mGvCustomMode = null;
    private String[] customerModeArr = new String[]{};
    private  int mCurrentColor = Color.rgb(0, 0, 0);
    private final int mode_Candles = 0;
    private final int mode_Sleep = 1;
    private final int mode_Shakes = 2;
    private final int mode_Yoga= 3;
    private final int mode_Flashlight = 4;
    private final int mode_Night_Light= 5;
//    private final int mode_Sport= 6;
    private final int mode_Romantic= 6;
    private final int mode_Discotheque= 7;
    private final int mode_Read= 9;
    private final int mode_Rock= 8;

    private final int mode_Gradient= 10;
    Gridadapter  adapter;
    Intent intent;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            // The last two arguments ensure LayoutParams are inflated properly.
            mRootView = inflater.inflate(R.layout.custom_fragment, container, false);

        }
        initView(mRootView);
        initrData();
        initListener();
        return mRootView;
    }




    private void initView(View view){
        mGvCustomMode = (GridView) view.findViewById(R.id.gv_custom);
    }

    private void initrData(){
        customerModeArr = getActivity().getResources().getStringArray(R.array.custom_mode);
        adapter=new Gridadapter(getActivity(), customerModeArr, null);

        mGvCustomMode.setAdapter(adapter);
    }

    private void initListener(){
        mGvCustomMode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mGvCustomMode.getAnimation();
                adapter.updateSelect(position);
                switch (position){
                    case mode_Candles:
                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x36}, false);


                        break;
                    case mode_Sleep:
                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x37,0x06,0x00,0x00, (byte) 0xf3, (byte) 0xf9, (byte) 0xff}, false);                        break;
                    case mode_Shakes:
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), ShakeActivity.class);
//                        startActivity(intent);
                        intent = new Intent();
                        intent.setClass(getActivity(), ShakeActivity.class);
                        startActivity(intent);
                        break;
                    case mode_Yoga:

                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x37,0x06,0x00,0x00, (byte) 0xff, (byte) 0xff, (byte) 0x00}, false);
                        break;
                    case mode_Flashlight:
                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x37,0x06,0x00,0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff}, false);
                        break;
                    case mode_Night_Light:
                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x37,0x06,0x00,0x00, (byte) 0xdd, (byte) 0xc9, (byte) 0xc9}, false);

                        break;

                    case mode_Romantic:
                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x37,0x06,0x00,0x00, (byte) 0x00, (byte) 0xff, (byte) 0x00}, false);
                        break;
                    case mode_Discotheque:
                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x37,0x05, (byte) 0x90, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00}, false);

                        break;
                    case mode_Rock:
                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x37,0x06, (byte) 0x90, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00}, false);

                        break;
                    case mode_Read:
                        DataModelApi.sendData(DeviceManger.mSendDeviceId, new byte[]{0x37,0x06,0x00,0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}, false);
                        break;
                    case mode_Gradient:
                        intent = new Intent();
                        intent.setClass(getActivity(),GradientActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mController = (DeviceController) CsrManger.getInstance(getActivity());;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DeviceController callback interface.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
