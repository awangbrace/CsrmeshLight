/******************************************************************************
 Copyright Cambridge Silicon Radio Limited 2014 - 2015.
 ******************************************************************************/

package com.csr.csrmeshdemo.newUI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import com.csr.csrmeshdemo.adapter.DeviceAdapter;
import com.csr.csrmeshdemo.util.DeviceController;
import com.csr.csrmeshdemo.util.DevicesComparator;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.base.BaseFragment;
import com.csr.csrmeshdemo.base.CsrManger;
import com.csr.csrmeshdemo.entities.Device;
import com.csr.csrmeshdemo.entities.SingleDevice;
import com.csr.mesh.LightModelApi;
import com.csr.mesh.PowerModelApi.PowerState;

import java.util.Collections;
import java.util.List;

/**
 * Fragment that allows controlling the colour of lights using HSV colour wheel.
 * 
 */
public class WarmWhileLightControlFragment extends BaseFragment implements View.OnClickListener{
    public static final String TAG = "LightControlFragment";

    private View mRootView;
    private SeekBar mBrightSlider = null;
    private DeviceController mController;
    private DeviceAdapter mDeviceListAdapter;
    private Spinner mDeviceSpinner = null;
    private ImageView mCurrentColorView = null;

    private ImageView warmWhileColor1,warmWhileColor2,warmWhileColor3,warmWhileColor4;
    
    private int mCurrentColor ;

    private boolean mEnableEvents = true;
    private boolean mEnablePowerSwitchEvent = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LightControlFragment","1111111111111111111111111111111");
        mCurrentColor = getResources().getColor(R.color.warm_while_color_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            // The last two arguments ensure LayoutParams are inflated properly.
            mRootView = inflater.inflate(R.layout.warm_while_light_control_tab, container, false);

            warmWhileColor1 = (ImageView) mRootView.findViewById(R.id.img_warm_while_1);
            warmWhileColor2 = (ImageView) mRootView.findViewById(R.id.img_warm_while_2);
            warmWhileColor3= (ImageView) mRootView.findViewById(R.id.img_warm_while_3);
            warmWhileColor4= (ImageView) mRootView.findViewById(R.id.img_warm_while_4);
            mBrightSlider = (SeekBar) mRootView.findViewById(R.id.seekBrightness);
            mBrightSlider.setOnSeekBarChangeListener(brightChange);

            mDeviceSpinner = (Spinner) mRootView.findViewById(R.id.spinnerLight);
            
//            mPowerSwitch = (Switch) mRootView.findViewById(R.id.powerSwitch);
//            mPowerSwitch.setOnCheckedChangeListener(powerChange);

            mCurrentColorView = (ImageView) mRootView.findViewById(R.id.currentColor);
        }
        initLinstener();
        return mRootView;
    }

    private void initLinstener(){
        warmWhileColor1.setOnClickListener(this);
        warmWhileColor2.setOnClickListener(this);
        warmWhileColor3.setOnClickListener(this);
        warmWhileColor4.setOnClickListener(this);
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
    public void onStart() {
        super.onStart();
        if (mDeviceListAdapter == null) {
            mDeviceListAdapter = new DeviceAdapter(getActivity());
            mDeviceSpinner.setAdapter(mDeviceListAdapter);
            mDeviceSpinner.setOnItemSelectedListener(deviceSelect);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStop() {
        super.onStop();
        mDeviceListAdapter.clear();        
    }

    @Override
    public void onResume() {
        super.onResume();

        loadDevices();
    }

    private void loadDevices() {
    	List<Device> groups = mController.getGroups();

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
     * Called when the brightness slider changes position.
     */
    protected OnSeekBarChangeListener brightChange = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // No behaviour.
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // No behaviour.
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!mEnableEvents)
                return;
            
            // Force power button on.
            mEnablePowerSwitchEvent = false;
//            mPowerSwitch.setChecked(true);
            // Save the new power state but don't send a message to the device.
            mController.setLocalLightPower(PowerState.ON);
            mEnablePowerSwitchEvent = true;
            
            // Set a new colour to send.
            mController.setLightColor(mCurrentColor, progress);
        }
    };

    /**
     * Called when power button is pressed.
     */
    protected OnCheckedChangeListener powerChange = new OnCheckedChangeListener() {
        
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mEnablePowerSwitchEvent) {
                mController.setLightPower(isChecked ? PowerState.ON : PowerState.OFF);
            }
        }
    };
    
    /**
     * Event handler for when a new device is selected from the Spinner.
     * 
     * @param position
     *            Position within Spinner of selected device.
     */
    protected void deviceSelected(int position) {
        if (position == 0) {
            mController.setSelectedDeviceId(0);
        }
        else {
            int deviceId = mDeviceListAdapter.getItemDeviceId(position);
            mController.setSelectedDeviceId(deviceId);
        }
    }


    protected void enableEvents(boolean enabled) {
        mEnableEvents = enabled;
    }

    /**
     * Called when a new device is selected from the spinner.
     */
    private OnItemSelectedListener deviceSelect = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            deviceSelected(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * Get the selected device id and set the spinner to it.
     */
    protected void selectSpinnerDevice() {
        int selectedDeviceId = mController.getSelectedDeviceId();        
        if (selectedDeviceId != Device.DEVICE_ID_UNKNOWN) {
    		Device dev = mController.getDevice(selectedDeviceId);
    		if (dev instanceof SingleDevice && 
    			((SingleDevice)dev).isModelSupported(LightModelApi.MODEL_NUMBER)) {
    			mDeviceSpinner.setSelection(mDeviceListAdapter.getDevicePosition(selectedDeviceId), true);
    		}
        }
        else {
            // No active device, so select the first device in the spinner if there is one.
            if (mDeviceListAdapter.getCount() > 0) {
                if (mDeviceSpinner.getSelectedItemPosition() == 0) {
                    // Make sure the event handler is called even if index zero was already selected.
                }
                else {
                    mDeviceSpinner.setSelection(0);
                }
            }
        }
    }
    
    /**
	 * Reload the displayed devices and groups.
	 */
	public void refreshUI() {
		mDeviceListAdapter.clear();
		loadDevices();
	}

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_warm_while_1:
                warmWhileColor1.setBackgroundResource(R.drawable.design_warm_while_select_1);
                warmWhileColor2.setBackgroundResource(R.drawable.design_warm_while_2);
                warmWhileColor3.setBackgroundResource(R.drawable.design_warm_while_3);
                warmWhileColor4.setBackgroundResource(R.drawable.design_warm_while_4);
                mCurrentColor = getResources().getColor(R.color.warm_while_color_1);

                break;
            case R.id.img_warm_while_2:
                warmWhileColor2.setBackgroundResource(R.drawable.design_warm_while_select_2);
                warmWhileColor1.setBackgroundResource(R.drawable.design_warm_while_1);
                warmWhileColor3.setBackgroundResource(R.drawable.design_warm_while_3);
                warmWhileColor4.setBackgroundResource(R.drawable.design_warm_while_4);
                mCurrentColor = getResources().getColor(R.color.warm_while_color_2);
                break;
            case R.id.img_warm_while_3:
                warmWhileColor3.setBackgroundResource(R.drawable.design_warm_while_select_3);
                warmWhileColor1.setBackgroundResource(R.drawable.design_warm_while_1);
                warmWhileColor2.setBackgroundResource(R.drawable.design_warm_while_2);
                warmWhileColor4.setBackgroundResource(R.drawable.design_warm_while_4);
                mCurrentColor = getResources().getColor(R.color.warm_while_color_3);
                break;
            case R.id.img_warm_while_4:
                warmWhileColor4.setBackgroundResource(R.drawable.design_warm_while_select_4);
                warmWhileColor1.setBackgroundResource(R.drawable.design_warm_while_1);
                warmWhileColor2.setBackgroundResource(R.drawable.design_warm_while_2);
                warmWhileColor3.setBackgroundResource(R.drawable.design_warm_while_3);
                mCurrentColor = getResources().getColor(R.color.warm_while_color_4);
                break;


        }
        mController.setLightColor(mCurrentColor, mBrightSlider.getProgress());
    }
}
