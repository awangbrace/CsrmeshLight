package com.csr.csrmeshdemo.newUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ab.fragment.AbFragment;
import com.csr.csrmeshdemo.adapter.DeviceAdapter;
import com.csr.csrmeshdemo.util.DeviceController;
import com.csr.csrmeshdemo.util.DevicesComparator;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.base.CsrManger;
import com.csr.csrmeshdemo.entities.Device;
import com.csr.csrmeshdemo.entities.SingleDevice;
import com.csr.csrmeshdemo.view.DeviceManger;
import com.csr.mesh.LightModelApi;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shadow Blade on 2016-12-23.
 */

public class SlidingMenuFragment extends AbFragment implements View.OnClickListener{
    private DeviceController mController;
    private ListView mLvDeviceList = null;
    private DeviceAdapter mDeviceListAdapter;
    private LinearLayout mBtnDeviceManger;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sliding_menu_fragment, container, false);
        initView(rootView);
        initData();
        initLitener();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mController = (DeviceController) CsrManger.getInstance(getActivity());

        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DeviceController callback interface.");
        }
    }

    private void initView(View view){
        mLvDeviceList = (ListView) view.findViewById(R.id.lvDeviceList);
        mBtnDeviceManger = (LinearLayout) view.findViewById(R.id.btn_device_manger);

    }


    private void initLitener(){
        mBtnDeviceManger.setOnClickListener(this);
    }


    /**
     * Called when a new device is selected from the spinner.
     */
    private AdapterView.OnItemClickListener deviceSelect = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            deviceSelected(position);

            ((MainActivity)getActivity()).menu.showContent();
        }
    };

    protected void deviceSelected(int position) {
        if (position == 0) {
            mController.setSelectedDeviceId(0);
            DeviceManger.mSendDeviceId = 0;
//            updateControls(0);
        }
        else {
            int deviceId = mDeviceListAdapter.getItemDeviceId(position);
            mController.setSelectedDeviceId(deviceId);
            DeviceManger.mSendDeviceId = deviceId;
//            updateControls(deviceId);
        }

        ((MainActivity)getActivity()).updateCurrectDevice();

    }


    private void initData() {
        if (mDeviceListAdapter == null) {
            mDeviceListAdapter = new DeviceAdapter(getActivity());
            mLvDeviceList.setAdapter(mDeviceListAdapter);
            mLvDeviceList.setOnItemClickListener(deviceSelect);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        loadDevices();
        Log.e("onResume","1111");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadDevices() {
        List<Device> groups = mController.getGroups();
        mDeviceListAdapter.clear();
        for (Device dev : groups) {
            if (dev.getDeviceId() == 0) {
                dev.setName(getString(R.string.all_lights));
            }

            mDeviceListAdapter.addDevice(dev);
        }

        // Add individual lights already associated.
        List<Device> lights = mController.getDevices(LightModelApi.MODEL_NUMBER);
        // sort devices list.
        Collections.sort(lights, new DevicesComparator());
        // add devices to adapter.
        for (Device dev : lights) {
            mDeviceListAdapter.addDevice(dev);
        }

        selectSpinnerDevice();

    }


    /**
     * Reload the displayed devices and groups.
     */
    public void refreshUI() {
        mDeviceListAdapter.clear();
        loadDevices();
    }

    protected void selectSpinnerDevice() {
        int selectedDeviceId = mController.getSelectedDeviceId();
        if (selectedDeviceId != Device.DEVICE_ID_UNKNOWN) {
            Device dev = mController.getDevice(selectedDeviceId);
            if (dev instanceof SingleDevice &&
                    ((SingleDevice)dev).isModelSupported(LightModelApi.MODEL_NUMBER)) {
                mLvDeviceList.setSelection(mDeviceListAdapter.getDevicePosition(selectedDeviceId));
                DeviceManger.mSendDeviceId = selectedDeviceId;
                ((MainActivity)getActivity()).updateCurrectDevice();
            }
        }
        else {
            // No active device, so select the first device in the spinner if there is one.
            if (mDeviceListAdapter.getCount() > 0) {
                if (mLvDeviceList.getSelectedItemPosition() == 0) {
                    // Make sure the event handler is called even if index zero was already selected.
//                    updateControls(mDeviceListAdapter.getItemDeviceId(0));
                    DeviceManger.mSendDeviceId = mDeviceListAdapter.getItemDeviceId(0);
                    ((MainActivity)getActivity()).updateCurrectDevice();
                }
                else {
                    mLvDeviceList.setSelection(0);
                    DeviceManger.mSendDeviceId = mDeviceListAdapter.getItemDeviceId(0);
                    ((MainActivity)getActivity()).updateCurrectDevice();
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_device_manger:
                Intent intent = new Intent();
                intent.setClass(getActivity(), GroupAssignActivity.class);
                startActivity(intent);
                ((MainActivity)getActivity()).menu.showContent();
                break;
        }
    }
}
