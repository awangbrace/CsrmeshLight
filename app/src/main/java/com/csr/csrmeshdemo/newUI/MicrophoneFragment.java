package com.csr.csrmeshdemo.newUI;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.csr.csrmeshdemo.util.DeviceController;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.util.RecordManager;
import com.csr.csrmeshdemo.base.BaseFragment;
import com.csr.csrmeshdemo.base.CsrManger;

import java.io.File;
import java.util.Timer;

/**
 * Created by Shadow Blade on 2016-12-21.
 */

public class MicrophoneFragment extends BaseFragment  {
    private Timer timer = null;
    private DeviceController mController;
    private String output_Path= Environment.getExternalStorageDirectory().getAbsolutePath()
            +File.separator+"luyin.3gp";
    //????
    private File soundFile;
    private ImageView mimageView = null;
    RecordManager mRecordManager;
    private ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.microphone_fragment, container, false);
        mimageView = (ImageView) rootView.findViewById(R.id.imageView);
        Log.e("ssssssss","11111111111111111111111111");
        initData();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("ssssssss","222222222222222222222222");
        try {
             mController = (DeviceController) CsrManger.getInstance(getActivity());

        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DeviceController callback interface.");
        }
    }

    private void initData(){
        soundFile=new File(output_Path);
        mRecordManager =  new RecordManager(mController,soundFile,mimageView);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecordManager.stopRecord();
    }

    @Override
    public void fragment_select(boolean select) {
        super.fragment_select(select);
        if(select){
//            if(timer ==null1){
            if(mRecordManager!=null)
                mRecordManager.startRecord();
//            }

        }else{
//            if(timer!=null) {
//                timer.cancel();
//                timer = null;
            if(mRecordManager!=null)
                mRecordManager.stopRecord();
//            }

        }
    }


}
