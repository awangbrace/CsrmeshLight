package com.csr.csrmeshdemo.base;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.csr.csrmeshdemo.util.DeviceController;
import com.csr.csrmeshdemo.util.DeviceInfoProtocol;
import com.csr.csrmeshdemo.state.DeviceState;
import com.csr.csrmeshdemo.util.DeviceStore;
import com.csr.csrmeshdemo.Interface.CsrStateChangeInterface;
import com.csr.csrmeshdemo.util.L;
import com.csr.csrmeshdemo.state.LightState;
import com.csr.csrmeshdemo.newUI.MainActivity;
import com.csr.csrmeshdemo.state.PowState;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.state.TemperatureStatus;
import com.csr.csrmeshdemo.util.Utils;
import com.csr.csrmeshdemo.entities.Device;
import com.csr.csrmeshdemo.entities.GroupDevice;
import com.csr.csrmeshdemo.entities.Setting;
import com.csr.csrmeshdemo.entities.SingleDevice;
import com.csr.csrmeshdemo.listeners.AssociationListener;
import com.csr.csrmeshdemo.listeners.AssociationStartedListener;
import com.csr.csrmeshdemo.listeners.DataListener;
import com.csr.csrmeshdemo.listeners.GroupListener;
import com.csr.csrmeshdemo.listeners.InfoListener;
import com.csr.csrmeshdemo.listeners.RemovedListener;
import com.csr.csrmeshdemo.listeners.TemperatureListener;
import com.csr.csrmeshdemo.newUI.AssociationActivity;
import com.csr.csrmeshdemo.newUI.GroupAssignActivity;
import com.csr.csrmeshdemo.view.DeviceManger;
import com.csr.mesh.ActuatorModelApi;
import com.csr.mesh.AttentionModelApi;
import com.csr.mesh.BatteryModelApi;
import com.csr.mesh.ConfigModelApi;
import com.csr.mesh.DataModelApi;
import com.csr.mesh.FirmwareModelApi;
import com.csr.mesh.GroupModelApi;
import com.csr.mesh.LightModelApi;
import com.csr.mesh.MeshService;
import com.csr.mesh.PowerModelApi;
import com.csr.mesh.SensorModelApi;
import com.csr.mesh.SwitchModelApi;
import com.csr.mesh.sensor.DesiredAirTemperature;
import com.csr.mesh.sensor.InternalAirTemperature;
import com.csr.mesh.sensor.SensorValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//import com.ab.activity.AbActivity;

//import com.ab.activity.AbActivity;

/**
 * Created by Shadow Blade on 2016-12-19.
 */

public class CsrManger implements DeviceController{

    private String TAG="CsrManger";

    private static CsrManger mCsrManger;
    private DeviceStore mDeviceStore;
    private MeshService mService = null;
    private AssociationListener mAssListener;
    private AssociationStartedListener mAssStartedListener;
    private Context mcontext;
    private int mAssociationTransactionId = -1;
    // Result code after to get the callback from the filepicker.
    private static final int SCANCODE_RESULT_CODE = 0;
    private static final int PICKFILE_RESULT_CODE = 1;
    private static final int SHARING_RESULT_CODE = 2;
    private static final int REQUEST_BT_RESULT_CODE = 3;

    /*package*/ static final int DEVICE_LOCAL_ADDRESS =  0x8000;
    /*package*/ static final int ATTENTION_DURATION_MS = 20000;
    /*package*/ static final int ATTRACTION_DURATION_MS = 5000;


    /*package*/ static final int MAX_TTL_MASP = 0xFF;



    // How often to send a colour - i.e. how often the periodic timer fires.
    private static final int TRANSMIT_COLOR_PERIOD_MS = 240;

    // How often to send a temperature value - i.e. how often the periodic timer fires.
    private static final int TRANSMIT_TEMPERATURE_PERIOD_MS = 500;

    // Time to wait for device UUID after removing a device.
    private static final int REMOVE_ACK_WAIT_TIME_MS = (10 * 1000);

    // Time to wait showing the progress dialog.
    private static final int PROGRESS_DIALOG_TIME_MS = (10 * 1000);

    private boolean mConnected = false;
    private HashSet<String> mConnectedDevices = new HashSet<String>();


    private int testMeshId = 0;

    // The address to send packets to.
//    private int DeviceManger.mSendDeviceId = Device.DEVICE_ID_UNKNOWN;

    // The colour sent every time the periodic timer fires (if mNewColor is true).
    // This will be updated by calls to setLightColor.
    private int mColorToSend = Color.rgb(0, 0, 0);

    // A new colour is only sent every TRANSMIT_PERIOD_MS if this is true. Set to true by setLightColour.
    private boolean mNewColor = false;

    private SensorValue mTemperatureToSend = null;

    private int mGroupAcksWaiting = 0;
    private boolean mGroupSuccess = true;

    private ArrayList<Integer> mNewGroups = new ArrayList<Integer>();
    private List <Integer> mGroupsToSend;
    private int mLastActuatorMeshId = 0;
    private boolean mPendingDesiredTemperatureRequest = false;

    // A list of model ids that are waiting on a query being sent to find out how many groups are supported.
    private Queue<Integer> mModelsToQueryForGroups = new LinkedList<Integer>();

    private SparseIntArray mDeviceIdtoUuidHash = new SparseIntArray();
    private SparseArray<String> mUuidHashToAppearance = new SparseArray<String>();



    private int mRemovedUuidHash;
    private int mRemovedDeviceId;



    // Keys used to save settings
    private static final String SETTING_LAST_ID = "lastID";

    // Listeners
    private GroupListener mGroupAckListener;
    private InfoListener mInfoListener;

    private RemovedListener mRemovedListener;
    private DataListener mDataListener;
    private TemperatureListener mTemperatureListener;

    // ConfigModelApi.DeviceInfo.VID_PID_VERSION
    byte[] vid;
    byte[] pid;
    byte[] version;

    // Temporal file that will be created for sharing purposes. It will be deleted after the sharing process will be completed.
    File tmpSharingFile = null;


    // HashMap with all the temperature status linked to a deviceID.
    private HashMap<Integer, TemperatureStatus> mTemperatureStatus = new HashMap<>();

    private boolean mRemoveNotificationAfterClick = true;


    private byte [] mData = new byte[DATA_BUFFER_SIZE];

    private static final int DATA_BUFFER_SIZE = 200;
    private CsrStateChangeInterface.ConnectionStateChange mConnectionStateChange;
    private CsrStateChangeInterface.SecurityCallback mSecurityCallback;

    private static Interpolator interp = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };

    public CsrManger(Context context) {
        mcontext = context;
        mDeviceStore = new DeviceStore(context);
        Intent bindIntent = new Intent(context, MeshService.class);
        context.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public static CsrManger getInstance(Context context) {
    if(mCsrManger==null){
        mCsrManger = new CsrManger(context);
    }
       return mCsrManger;
    }

    @Override
    public Device getDevice(int deviceId) {
        return mDeviceStore.getDevice(deviceId);
    }

    @Override
    public void setSelectedDeviceId(int deviceId) {
        Log.d(TAG, String.format("Device id is now 0x%x", deviceId));
        DeviceManger.mSendDeviceId = deviceId;
    }

    @Override
    public void requestCurrentTemperature() {
        SensorModelApi.getValue(DeviceManger.mSendDeviceId, SensorValue.SensorType.INTERNAL_AIR_TEMPERATURE,SensorValue.SensorType.DESIRED_AIR_TEMPERATURE);
    }

    @Override
    public void onMicrophoneValueChane(int level) {
        microhoneLightControl();
    }

    @Override
    public int getSelectedDeviceId() {
        return DeviceManger.mSendDeviceId;
    }

    @Override
    public void setLightColor(int color, int brightness) {
        if (brightness < 0 || brightness > 99) {
            throw new NumberFormatException("Brightness value should be between 0 and 99");
        }

        // Convert currentColor to HSV space and make the brightness (value) calculation. Then convert back to RGB to
        // make the colour to send.
        // Don't modify currentColor with the brightness or else it will deviate from the HS colour selected on the
        // wheel due to accumulated errors in the calculation after several brightness changes.
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = ((float) brightness + 1) / 100.0f;
        mColorToSend = Color.HSVToColor(hsv);

        // Indicate that there is a new colour for next time the timer fires.
        mNewColor = true;
    }

    @Override
    public void setLightPower(PowerModelApi.PowerState state) {
        PowerModelApi.setState(DeviceManger.mSendDeviceId, state, false);
        setLocalLightPower(state);
    }


    @Override
    public void setCustomerData(byte[] data) {
        DataModelApi.sendData(DeviceManger.mSendDeviceId,data,false);
    }



    @Override
    public void setLocalLightPower(PowerModelApi.PowerState state) {
        Device dev = mDeviceStore.getDevice(DeviceManger.mSendDeviceId);
        if (dev != null) {
            PowState powState = (PowState)dev.getState(DeviceState.StateType.POWER);
            powState.setPowerState(state);
            mDeviceStore.addDevice(dev);
        }
    }

    @Override
    public void removeDevice(RemovedListener listener) {
        if (DeviceManger.mSendDeviceId < Device.DEVICE_ADDR_BASE && DeviceManger.mSendDeviceId >= Device.GROUP_ADDR_BASE) {
            mDeviceStore.removeDevice(DeviceManger.mSendDeviceId);
            listener.onDeviceRemoved(DeviceManger.mSendDeviceId, true);
            DeviceManger.mSendDeviceId = Device.GROUP_ADDR_BASE;
        }
        else {
            mRemovedUuidHash = mDeviceStore.getSingleDevice(DeviceManger.mSendDeviceId).getUuidHash();
            mRemovedDeviceId = DeviceManger.mSendDeviceId;
            mRemovedListener = listener;
            // Enable discovery so that the device uuid message is received when the device is unassociated.
            mService.setDeviceDiscoveryFilterEnabled(true);
            // Send CONFIG_RESET
            ConfigModelApi.resetDevice(DeviceManger.mSendDeviceId);
            DeviceManger.mSendDeviceId = Device.GROUP_ADDR_BASE;
            // Start a timer so that we don't wait for the ack forever.
            mMeshHandler.postDelayed(removeDeviceTimeout, REMOVE_ACK_WAIT_TIME_MS);
        }
    }

    @Override
    public void getFwVersion(InfoListener listener) {
        mInfoListener = listener;
        FirmwareModelApi.getVersionInfo(DeviceManger.mSendDeviceId);
    }

    @Override
    public void getVID_PID_VERSION(InfoListener listener) {
        mInfoListener = listener;
        // reset values
        vid = null;
        pid = null;
        version = null;

        // ask for new values
        ConfigModelApi.getInfo(DeviceManger.mSendDeviceId, ConfigModelApi.DeviceInfo.VID_PID_VERSION);
    }

    @Override
    public void requestModelsSupported(InfoListener listener) {
        mInfoListener = listener;
        ConfigModelApi.getInfo(DeviceManger.mSendDeviceId, ConfigModelApi.DeviceInfo.MODEL_LOW);
    }

    @Override
    public void setDeviceGroups(List<Integer> groups, GroupListener listener) {
        if (DeviceManger.mSendDeviceId == Device.DEVICE_ID_UNKNOWN)
            return;
        mNewGroups.clear();
        mGroupAckListener = listener;
        boolean inProgress = false;
        for (int group : groups) {
            mNewGroups.add(group);
        }
        SingleDevice selectedDev = mDeviceStore.getSingleDevice(DeviceManger.mSendDeviceId);

        //

        // Send message to find out how many group ids the device supports for each model type.
        // Once a response is received to this command sendGroupAssign will be called to assign the groups.
        if (selectedDev.isModelSupported(LightModelApi.MODEL_NUMBER) && !selectedDev.isNumSupportedGroupsKnown(LightModelApi.MODEL_NUMBER)) {
            // Only query light model and assume power model supports the same number.
            mModelsToQueryForGroups.add(LightModelApi.MODEL_NUMBER);
            inProgress = true;
        }
        if (selectedDev.isModelSupported(SwitchModelApi.MODEL_NUMBER) && !selectedDev.isNumSupportedGroupsKnown(SwitchModelApi.MODEL_NUMBER)) {
            mModelsToQueryForGroups.add(SwitchModelApi.MODEL_NUMBER);
            inProgress = true;
        }
        if (selectedDev.isModelSupported(SensorModelApi.MODEL_NUMBER) && !selectedDev.isNumSupportedGroupsKnown(SensorModelApi.MODEL_NUMBER)) {
            mModelsToQueryForGroups.add(SensorModelApi.MODEL_NUMBER);
            inProgress = true;
        }
        if (selectedDev.isModelSupported(ActuatorModelApi.MODEL_NUMBER) && !selectedDev.isNumSupportedGroupsKnown(ActuatorModelApi.MODEL_NUMBER)) {
            mModelsToQueryForGroups.add(ActuatorModelApi.MODEL_NUMBER);
            inProgress = true;
        }
        if (selectedDev.isModelSupported(DataModelApi.MODEL_NUMBER) && !selectedDev.isNumSupportedGroupsKnown(DataModelApi.MODEL_NUMBER)) {
            mModelsToQueryForGroups.add(DataModelApi.MODEL_NUMBER);
            inProgress = true;
        }
        if (inProgress) {
            GroupModelApi.getNumModelGroupIds(DeviceManger.mSendDeviceId,mModelsToQueryForGroups.peek());
        }
        else {
            // We already know the number of supported groups from a previous query, so go straight to assigning.
            assignGroups(selectedDev.getMinimumSupportedGroups());
            inProgress = true;
        }


        // There isn't any operation to do, so the dialog should be dismissed.
        if (!inProgress) {
            mGroupAckListener.groupsUpdated(DeviceManger.mSendDeviceId, false, mcontext.getString(R.string.group_query_fail));
        }

    }

    @Override
    public void setDeviceName(int deviceId, String name) {
        mDeviceStore.updateDeviceName(deviceId, name);
    }

    @Override
    public void setSecurity(String networkKeyPhrase, boolean authRequired) {
        Log.e("zzzzzzzzzzzzzzzzzzzz","zzzzzzzzzzzzzzzzzzzzzzzzzz");
        Setting setting = mDeviceStore.getSetting();
        if (setting != null) {
            // Set the new setting values
            setting.setNetworkKey(networkKeyPhrase);
            setting.setAuthRequired(authRequired);
        }
        else {
            // if we don't have settings yet we need to create one and set the new setting values.
            setting = new Setting();
            setting.setNetworkKey(networkKeyPhrase);
            setting.setAuthRequired(authRequired);
        }
        // store the setting in the database.
        mDeviceStore.setSetting(setting, true);

        // set the new NetworkPassPhrase to the MeshService
        mService.setNetworkPassPhrase(mDeviceStore.getSetting().getNetworkKey());

        // change to the association fragment.

        // If there are devices already associated we lead the user to go to association page otherwise we lead the user to go to light control.
        if (mDeviceStore.getAllSingleDevices().size() > 0) {
//            getActionBar().setSelectedNavigationItem(SimpleNavigationListener.POSITION_LIGHT_CONTROL);
            mSecurityCallback.Security(true);
        }
        else {
//            getActionBar().setSelectedNavigationItem(POSITION_ASSOCIATION);
            mSecurityCallback.Security(false);
        }


    }

    @Override
    public boolean isAuthRequired() {
        if (mDeviceStore.getSetting() != null) {
            return mDeviceStore.getSetting().isAuthRequired();
        }
        else {
            return false;
        }
    }

    @Override
    public String getNetworkKeyPhrase() {
        if (mDeviceStore.getSetting() != null) {
            return mDeviceStore.getSetting().getNetworkKey();
        }
        else {
            return null;
        }
    }

    @Override
    public void associateWithQrCode(AssociationStartedListener listener) {
        mAssStartedListener = listener;
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            ((AssociationActivity)mcontext).startActivityForResult(intent, SCANCODE_RESULT_CODE);
        }
        catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            mcontext.startActivity(marketIntent);
        }
    }

    @Override
    public Device addLightGroup(String groupName) {
        GroupDevice result = new GroupDevice(mDeviceStore.getSetting().getNextGroupIndexAndIncrement(), groupName);
        mDeviceStore.addGroupDevice(result, true);
        return result;
    }

    @Override
    public void setAttentionEnabled(boolean enabled) {
        AttentionModelApi.setState(DeviceManger.mSendDeviceId, enabled, ATTENTION_DURATION_MS);
    }

    @Override
    public void removeDeviceLocally(RemovedListener removedListener) {

        mDeviceStore.removeDevice(DeviceManger.mSendDeviceId);
        removedListener.onDeviceRemoved(DeviceManger.mSendDeviceId, true);
        DeviceManger.mSendDeviceId = Device.GROUP_ADDR_BASE;
        removedListener = null;
    }

    @Override
    public String getBridgeAddress() {
        if (mConnected) {
            return mConnectedDevices.toString();
        }
        else {
            return null;
        }
    }

    @Override
    public void setDesiredTemperature(float celsius) {
        double kelvin = Utils.convertCelsiusToKelvin(celsius);
        mTemperatureToSend = new DesiredAirTemperature((float)kelvin);

        if (DeviceManger.mSendDeviceId != Device.DEVICE_ID_UNKNOWN && mTemperatureToSend != null) {
            mPendingDesiredTemperatureRequest = true;
        }
        mMeshHandler.removeCallbacks(transmitTempCallback);
        mMeshHandler.postDelayed(transmitTempCallback, TRANSMIT_TEMPERATURE_PERIOD_MS);
    }


    @Override
    public boolean associateDevice(int uuidHash, String shortCode) {
        try {
            if (shortCode == null) {
                mAssociationTransactionId = mService.associateDevice(uuidHash, 0, false);
                notifyAssociationFragment(0);
                return true;
            } else {
                int decodedHash = MeshService.getDeviceHashFromShortcode(shortCode);

                if (decodedHash == uuidHash) {
                    mAssociationTransactionId = mService.associateDevice(uuidHash, MeshService.getAuthorizationCode(shortCode), true);
                    notifyAssociationFragment(0);
                    return true;
                }
                return false;
            }
        }
        catch (Exception e) {
            Toast.makeText(mcontext, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    @Override
    public void activateAttentionMode(int uuidHash, boolean enabled) {

        // enable the new uuidHash.
        mService.setAttentionPreAssociation(uuidHash, enabled, ATTRACTION_DURATION_MS);

        // notify the user.
        if (enabled) {
            Toast.makeText(mcontext, mcontext.getString(R.string.attraction_enabled), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void discoverDevices(boolean enabled, AssociationListener listener) {
        if (enabled) {
            mAssListener = listener;
        }
        else {
            mAssListener = null;
        }

        //avoiding crashes
        if (mService != null) {
            mService.setDeviceDiscoveryFilterEnabled(enabled);
        }
    }

    @Override
    public List<Device> getDevices(int ... modelNumber) {
        ArrayList<Device> result = new ArrayList<Device>();
        for (Device dev : mDeviceStore.getAllSingleDevices()) {
            if (((SingleDevice)dev).isAnyModelSupported(modelNumber)) {
                result.add(dev);
            }
        }
        return result;
    }

    @Override
    public ArrayList<String> getModelsLabelSupported(int deviceId) {

        Device device =mDeviceStore.getDevice(deviceId);
        if (device instanceof SingleDevice) {
            return ((SingleDevice)device).getModelsLabelSupported();
        }
        return null;
    }


    @Override
    public List<Device> getGroups() {
        return mDeviceStore.getAllGroups();
    }

    @Override
    public void getDeviceData(DataListener listener) {
        this.mDataListener = listener;
        mService.setContinuousLeScanEnabled(true);
        DeviceInfoProtocol.requestDeviceInfo(DeviceManger.mSendDeviceId);
    }

    public Handler getMeshHandler(){
        return mMeshHandler;
    }

    @Override
    public void startUITimeOut() {
        mMeshHandler.postDelayed(progressTimeOut, PROGRESS_DIALOG_TIME_MS);

    }

    @Override
    public void stopUITimeOut() {
        mMeshHandler.removeCallbacks(progressTimeOut);
    }

    @Override
    public void setContinuousScanning(boolean enabled) {
        mService.setContinuousLeScanEnabled(enabled);
    }

    @Override
    public void setTemperatureListener(TemperatureListener listener) {
        this.mTemperatureListener = listener;
    }

    @Override
    public void postRunnable(Runnable checkScanInfoRunnable) {
        getMeshHandler().post(checkScanInfoRunnable);
    }

    @Override
    public void removeRunnable(Runnable checkScanInfoRunnable) {
        getMeshHandler().removeCallbacks(checkScanInfoRunnable);
    }

    @Override
    public int getMaxTTLForMASP() {
        return MAX_TTL_MASP;
    }

    @Override
    public int getTTLForMCP() {
        return mService.getTTL();
    }

    @Override
    public void setTTLForMCP(int ttl) {
        // set ttl to the library
        mService.setTTL(ttl);
        Setting settings =mDeviceStore.getSetting();
        settings.setTTL(ttl);

        // save new settings with the new TTL value
        mDeviceStore.setSetting(settings, true);
    }

    /**
     * Callbacks for changes to the state of the connection.
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((MeshService.LocalBinder) rawBinder).getService();
            if (mService != null) {
                // Try to get the last setting ID used.
//                SharedPreferences activityPrefs = ((AbActivity)mcontext).getPreferences(Activity.MODE_PRIVATE);
                SharedPreferences activityPrefs = mcontext.getSharedPreferences(mcontext.getPackageName(),Context.MODE_PRIVATE);

                int lastIdUsed = activityPrefs.getInt(SETTING_LAST_ID, Setting.UKNOWN_ID);
                restoreSettings(lastIdUsed);

                connect();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };


    private void microhoneLightControl(){

//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
        int mCurrentColor = Color.rgb(Color.red( (int) (Math.random() * 255 + 1)), Color.green( (int) (Math.random() * 255 + 1)), (int) (Math.random() * 255 + 1));
        int mBrightSlider =  (int) (Math.random() * 99 + 1);
        mCsrManger.setLightColor(mCurrentColor, mBrightSlider);
//            }
//        };
//        timer.schedule(timerTask, 1000, (int) (Math.random() * 500 + 1));
    }

    // End of notification fragment methods ////
    private BluetoothAdapter.LeScanCallback mScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(mService.processMeshAdvert(device, scanRecord, rssi)){
                Log.e("onLeScan",device.getName()+"-----"+device.getAddress());
            };
        }
    };

    private void connect() {
        mService.setHandler(mMeshHandler);
        mService.setLeScanCallback(mScanCallBack);
        mService.setMeshListeningMode(true, true);
        mService.autoConnect(1, 15000, 0, 2);
    }

    /**
     * Executed when LE link to bridge is connected.
     */
    private void onConnected() {
        ((MainActivity)mcontext).hideProgress();

        mConnected = true;
        // Set active fragment based on settings.
        if (mDeviceStore.getSetting() == null || mDeviceStore.getSetting().getNetworkKey() == null) {
//            getActionBar().setSelectedNavigationItem(POSITION_NETWORK_SETTINGS);
            mConnectionStateChange.OnConnected(false);
        }else{
            ((MainActivity)mcontext).showSlidingMenu();
            mConnectionStateChange.OnConnected(true);
        }
        startPeriodicTransmit();

        // check if there is any required action from the user.
        checkIfDeviceRequiredAction();
    }


    /**
     * Handle messages from mesh service.
     */
    private final Handler mMeshHandler = new MeshHandler(mcontext);

    private  class MeshHandler extends Handler {
//        private final WeakReference<MainActivity> mActivity;

        public MeshHandler(Context context) {
//            mActivity = new WeakReference<MainActivity>(activity);
        }

        public void handleMessage(Message msg) {
//            MainActivity parentActivity = mActivity.get();
            switch (msg.what) {
                case MeshService.MESSAGE_LE_CONNECTED: {
                    mConnectedDevices.add(msg.getData().getString(MeshService.EXTRA_DEVICE_ADDRESS));
                    if (!mConnected) {
                        onConnected();
                    }
                    break;
                }
                case MeshService.MESSAGE_LE_DISCONNECTED: {
                    int numConnections = msg.getData().getInt(MeshService.EXTRA_NUM_CONNECTIONS);
                    String address = msg.getData().getString(MeshService.EXTRA_DEVICE_ADDRESS);
                    if (address != null) {
                        String toRemove = null;
                        for (String s : mConnectedDevices) {
                            if (s.compareTo(address) == 0) {
                                toRemove = s;
                                break;
                            }
                        }
                        if (toRemove != null) {
                            mConnectedDevices.remove(toRemove);
                        }
                    }
                    if (numConnections == 0) {
                        mConnected = false;
                        ((MainActivity)mcontext).showProgress(mcontext.getString(R.string.connecting));
                    }
                    break;
                }
                case MeshService.MESSAGE_LE_DISCONNECT_COMPLETE:
                    ((MainActivity)mcontext).finish();
                    break;
                case MeshService.MESSAGE_REQUEST_BT:

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    ((AbActivity)mcontext).startActivityForResult(enableBtIntent, REQUEST_BT_RESULT_CODE);
                    break;
                case MeshService.MESSAGE_TIMEOUT:{
                    int expectedMsg = msg.getData().getInt(MeshService.EXTRA_EXPECTED_MESSAGE);
                    int id;
                    int meshRequestId;
                    if (msg.getData().containsKey(MeshService.EXTRA_UUIDHASH_31)) {
                        id = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
                    }
                    else {
                        id = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                    }
                    meshRequestId = msg.getData().getInt(MeshService.EXTRA_MESH_REQUEST_ID);
                    onMessageTimeout(expectedMsg, id, meshRequestId);
                    break;
                }
                case MeshService.MESSAGE_DEVICE_DISCOVERED: {
                    ParcelUuid uuid = msg.getData().getParcelable(MeshService.EXTRA_UUID);
                    int uuidHash = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
                    int rssi = msg.getData().getInt(MeshService.EXTRA_RSSI);
                    int ttl = msg.getData().getInt(MeshService.EXTRA_TTL);
                    if (mRemovedListener != null && mRemovedUuidHash == uuidHash) {
                        // This was received after a device was removed, so let the removed listener know.

                        mDeviceStore.removeDevice(mRemovedDeviceId);
                        mRemovedListener.onDeviceRemoved(mRemovedDeviceId, true);
                        mRemovedListener = null;
                        mRemovedUuidHash = 0;
                        mRemovedDeviceId = 0;
                        mService.setDeviceDiscoveryFilterEnabled(false);
                        removeCallbacks(removeDeviceTimeout);
                    } else if (mAssListener != null) {
                        // This was received after discover was enabled so let the UUID listener know.
                        mAssListener.newUuid(uuid.getUuid(), uuidHash, rssi, ttl);
                        L.e("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+uuid.getUuid());
                    }
                    break;
                }
                case MeshService.MESSAGE_DEVICE_APPEARANCE: {
                    // This is the appearance received when a device is in association mode.
                    // If appearance has been explicitly requested via CONFIG_DEVICE_INFO, then the appearance
                    // will be received in a MESSAGE_CONFIG_DEVICE_INFO.
                    byte[] appearance = msg.getData().getByteArray(MeshService.EXTRA_APPEARANCE);
                    String shortName = msg.getData().getString(MeshService.EXTRA_SHORTNAME);
                    int uuidHash = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
                    if (mAssListener != null) {
                        mUuidHashToAppearance.put(uuidHash, shortName);
                        // This was received after discover was enabled so let the UUID listener know.
                        mAssListener.newAppearance(uuidHash, appearance, shortName);
                    }
                    break;
                }
                case MeshService.MESSAGE_DEVICE_ASSOCIATED: {
                    // New device has been associated and is telling us its device id.
                    // Request supported models before adding to DeviceStore, and the UI.
                    int deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                    int uuidHash = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
                    Log.d(TAG, "New device associated with id " + String.format("0x%x", deviceId));

                    if (mDeviceStore.getDevice(deviceId) == null) {
                        // Save the device id with the UUID hash so that we can store the UUID hash in the device
                        // object when MESSAGE_CONFIG_MODELS is received.
                        mDeviceIdtoUuidHash.put(deviceId, uuidHash);

                        // We add the device with no supported models. We will update that once we get the info.
                        if (uuidHash != 0) {
                            addDevice(deviceId, uuidHash, null, 0, false);
                        }

                        // If we don't already know about this device request its model support.
                        // We only need the lower 64-bits, so just request those.
                        ConfigModelApi.getInfo(deviceId, ConfigModelApi.DeviceInfo.MODEL_LOW);
                    }
                    break;
                }
                case MeshService.MESSAGE_CONFIG_DEVICE_INFO: {
                    int deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                    int uuidHash = mDeviceIdtoUuidHash.get(deviceId);

                    ConfigModelApi.DeviceInfo infoType =
                            ConfigModelApi.DeviceInfo.values()[msg.getData().getByte(MeshService.EXTRA_DEVICE_INFO_TYPE)];
                    if (infoType == ConfigModelApi.DeviceInfo.MODEL_LOW) {
                        long bitmap = msg.getData().getLong(MeshService.EXTRA_DEVICE_INFORMATION);
                        // If the uuidHash was saved for this device id then this is an expected message, so process it.
                        if (uuidHash != 0) {
                            // Remove the uuidhash from the array as we have received its model support now.
                            mDeviceIdtoUuidHash
                                    .removeAt(mDeviceIdtoUuidHash.indexOfKey(deviceId));
                            String shortName = mUuidHashToAppearance.get(uuidHash);
                            if (shortName != null) {
                                mUuidHashToAppearance.remove(uuidHash);
                            }
                            addDevice(deviceId, uuidHash, shortName, bitmap, true);
                            deviceAssociated(true, null);
                        } else if (mDeviceIdtoUuidHash.size() == 0) {
                            if (mInfoListener != null) {
                                SingleDevice device = mDeviceStore.getSingleDevice(deviceId);
                                if (device != null) {
                                    device.setModelSupport(bitmap, 0);
                                    mDeviceStore.addDevice(device);
                                    mInfoListener.onDeviceConfigReceived(true);
                                } else {
                                    mInfoListener.onDeviceConfigReceived(false);
                                }


                            }
                        }
                    } else if (infoType == ConfigModelApi.DeviceInfo.VID_PID_VERSION) {
                        vid = msg.getData().getByteArray(MeshService.EXTRA_VID_INFORMATION);
                        pid = msg.getData().getByteArray(MeshService.EXTRA_PID_INFORMATION);
                        version = msg.getData().getByteArray(MeshService.EXTRA_VERSION_INFORMATION);
                        if (mDeviceStore.getSingleDevice(deviceId).isModelSupported(BatteryModelApi.MODEL_NUMBER)) {
                            getBatteryState(mInfoListener);
                        } else if (mInfoListener != null) {
                            mInfoListener.onDeviceInfoReceived(vid, pid, version, GroupAssignActivity.UNKNOWN_BATTERY_LEVEL,GroupAssignActivity.UNKNOWN_BATTERY_STATE, deviceId, true);
                        } else {
                            // shouldn't happen. Just in case for avoiding endless loops.
                            ((MainActivity)mcontext).hideProgress();
                        }

                    }
                    break;
                }
                case MeshService.MESSAGE_BATTERY_STATE: {

                    int deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                    byte batteryLevel = msg.getData().getByte(MeshService.EXTRA_BATTERY_LEVEL);
                    byte batteryState = msg.getData().getByte(MeshService.EXTRA_BATTERY_STATE);



                    if (mInfoListener != null) {
                        mInfoListener.onDeviceInfoReceived(vid, pid, version, batteryLevel,batteryState, deviceId, true);
                    } else {
                        // shouldn't happen. Just in case for avoiding endless loops.
                        ((MainActivity)mcontext). hideProgress();
                    }
                    break;
                }
                case MeshService.MESSAGE_GROUP_NUM_GROUPIDS: {
                    if (mGroupAckListener != null) {
                        int numIds = msg.getData().getByte(MeshService.EXTRA_NUM_GROUP_IDS);
                        int modelNo = msg.getData().getByte(MeshService.EXTRA_MODEL_NO);
                        int expectedModelNo = mModelsToQueryForGroups.peek();
                        int deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);

                        if (expectedModelNo == modelNo) {
                            SingleDevice currentDev = mDeviceStore.getSingleDevice(deviceId);
                            if (currentDev != null) {
                                currentDev.setNumSupportedGroups(numIds, modelNo);
                                mDeviceStore.addDevice(currentDev);
                                // We know how many groups are supported for this model now so remove it from the queue.
                                mModelsToQueryForGroups.remove();
                                if (mModelsToQueryForGroups.isEmpty()) {
                                    // If there are no more models to query then we can assign groups now.
                                    assignGroups(currentDev.getMinimumSupportedGroups());
                                } else {
                                    // Otherwise ask how many groups the next model supports, by taking the next model number from the queue.
                                    GroupModelApi.getNumModelGroupIds(DeviceManger.mSendDeviceId, mModelsToQueryForGroups.peek());
                                }
                            } else {
                                mGroupAckListener.groupsUpdated(DeviceManger.mSendDeviceId, false, mcontext.getString(R.string.group_query_fail));
                            }
                        }
                    }
                    break;
                }
                case MeshService.MESSAGE_GROUP_MODEL_GROUPID: {
                    // This is the ACK returned after calling setModelGroupId.
                    if (mGroupAckListener != null && mGroupAcksWaiting > 0) {
                        mGroupAcksWaiting--;
                        int index = msg.getData().getByte(MeshService.EXTRA_GROUP_INDEX);
                        int groupId = msg.getData().getInt(MeshService.EXTRA_GROUP_ID);
                        // Update the group membership of this device in the device store.
                        SingleDevice updatedDev = mDeviceStore.getSingleDevice(DeviceManger.mSendDeviceId);
                        try {
                            updatedDev.setGroupId(index, groupId);

                        } catch (IndexOutOfBoundsException exception) {
                            mGroupSuccess = false;
                        }
                        mDeviceStore.addDevice(updatedDev);


                        if (mGroupAcksWaiting == 0) {
                            // Tell the listener that the update was OK.
                            mGroupAckListener.groupsUpdated(
                                    DeviceManger.mSendDeviceId, true,
                                    mGroupSuccess ? mcontext.getString(R.string.group_update_ok) : mcontext.getString(R.string.group_update_with_problems));
                        }
                    }
                    break;
                }
                case MeshService.MESSAGE_FIRMWARE_VERSION:
                    mInfoListener.onFirmwareVersion(msg.getData().getInt(MeshService.EXTRA_DEVICE_ID), msg
                                    .getData().getInt(MeshService.EXTRA_VERSION_MAJOR),
                            msg.getData().getInt(MeshService.EXTRA_VERSION_MINOR), true);
                    mInfoListener = null;
                    break;
                case MeshService.MESSAGE_ACTUATOR_VALUE_ACK: {
                    if (mTemperatureListener == null) {
                        // do nothing
                        return;
                    }

                    // Clear mLastActuatorMeshId if this is the mesh Id we were expecting.
                    int meshRequestId = msg.getData().getInt(MeshService.EXTRA_MESH_REQUEST_ID);

                    if (mLastActuatorMeshId == meshRequestId) {
                        mPendingDesiredTemperatureRequest = false;
                        mLastActuatorMeshId = 0;

                        // notify to the listener
                        mTemperatureListener.confirmDesiredTemperature();
                    }

                    int deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                    // update device's temperature status
                    TemperatureStatus status = mTemperatureStatus.get(deviceId);
                    if (status == null) {
                        status = new TemperatureStatus();
                    }
                    status.setDesiredTemperatureConfirmed(true);
                    mTemperatureStatus.put(deviceId, status);


                    break;
                }

                case MeshService.MESSAGE_SENSOR_VALUE: {
                    int deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);

                    SensorValue value1 = (SensorValue) msg.getData().getParcelable(MeshService.EXTRA_SENSOR_VALUE1);
                    SensorValue value2 = null;
                    if (msg.getData().containsKey(MeshService.EXTRA_SENSOR_VALUE2)) {
                        value2 = (SensorValue) msg.getData().getParcelable(MeshService.EXTRA_SENSOR_VALUE2);
                    }

                    TemperatureStatus status = mTemperatureStatus.get(deviceId);
                    if (status == null) {
                        status = new TemperatureStatus();
                    }



                    storeAndNotifyNewSensorValue(value1,status,deviceId);
                    storeAndNotifyNewSensorValue(value2,status,deviceId);



                }
                break;
                case MeshService.MESSAGE_RECEIVE_STREAM_DATA:
                    if (mDataListener != null) {
                        int deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                        byte [] data = msg.getData().getByteArray(MeshService.EXTRA_DATA);
                        int sqn = msg.getData().getInt(MeshService.EXTRA_DATA_SQN);
                        if (deviceId == DeviceManger.mSendDeviceId && sqn + data.length < DATA_BUFFER_SIZE) {
                            System.arraycopy(data, 0, mData, sqn, data.length);
                        }
                    }
                    break;
                case MeshService.MESSAGE_ASSOCIATING_DEVICE:
                    int progress = msg.getData().getInt(MeshService.EXTRA_PROGRESS_INFORMATION);
                    notifyAssociationFragment(progress);
                    break;
                case MeshService.MESSAGE_RECEIVE_STREAM_DATA_END:
                    if (mDataListener != null) {
                        int  deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                        if (deviceId == DeviceManger.mSendDeviceId) {
                            mDataListener.dataReceived(deviceId, mData);
                        } else {
                            mDataListener.dataGroupReceived(deviceId);
                        }
                    }
                    break;
                case MeshService.MESSAGE_TRANSACTION_NOT_CANCELLED: {
                    Toast.makeText(mcontext, "Association couldn't be cancelled.", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MeshService.MESSAGE_TRANSACTION_CANCELLED: {
                    deviceAssociated(false, mcontext.getString(R.string.association_cancelled));
                    break;
                }
            }
        }
    }
    /**
     * Send group assign messages to the currently selected device using the groups contained in mNewGroups.
     */
    private void assignGroups(int numSupportedGroups) {
        if (DeviceManger.mSendDeviceId == Device.DEVICE_ID_UNKNOWN)
            return;
        // Check the number of supported groups matches the number requested to be set.
        if (numSupportedGroups >= mNewGroups.size()) {

            mGroupAcksWaiting = 0;

            // Make a copy of existing groups for this device.
            mGroupsToSend = mDeviceStore.getSingleDevice(DeviceManger.mSendDeviceId).getGroupMembershipValues();
            // Loop through existing groups.
            for (int i = 0; i < mGroupsToSend.size(); i++) {
                int groupId = mGroupsToSend.get(i);
                if (groupId != 0) {
                    int foundIndex = mNewGroups.indexOf(groupId);
                    if (foundIndex > -1) {
                        // The device is already a member of this group so remove it from the list of groups to add.
                        mNewGroups.remove(foundIndex);
                    }
                    else {
                        // The device should no longer be a member of this group, so set that index to -1 to flag
                        // that a message must be sent to update this index.
                        mGroupsToSend.set(i, -1);
                    }
                }
            }
            // Now loop through currentGroups, and for every index set to -1 or zero send a group update command for
            // that index with one of our new groups if one is available. If there are no new groups to set, then just
            // send a message for all indices set to -1, to set them to zero.
            boolean commandSent = false;
            for (int i = 0; i < mGroupsToSend.size(); i++) {
                int groupId = mGroupsToSend.get(i);
                if (groupId == -1 || groupId == 0) {
                    if (mNewGroups.size() > 0) {
                        int newGroup = mNewGroups.get(0);
                        mNewGroups.remove(0);
                        commandSent = true;
                        sendGroupCommands(DeviceManger.mSendDeviceId, i, newGroup);
                    }
                    else if (groupId == -1) {
                        commandSent = true;
                        sendGroupCommands(DeviceManger.mSendDeviceId, i, 0);
                    }
                }
            }
            if (!commandSent) {
                // There were no changes to the groups so no updates were sent. Just tell the listener
                // that the operation is complete.
                if (mGroupAckListener != null) {
                    mGroupAckListener.groupsUpdated(DeviceManger.mSendDeviceId, true, mcontext.getString(R.string.group_no_changes));
                }
            }
        }
        else {
            // Not enough groups supported on device.
            if (mGroupAckListener != null) {
                mGroupAckListener.groupsUpdated(DeviceManger.mSendDeviceId, false,
                        mcontext.getString(R.string.group_max_fail) + " " + numSupportedGroups + " " + mcontext.getString(R.string.groups));
            }
        }
    }




    private void sendGroupCommands(int deviceId, int index, int group) {
        mGroupSuccess = true;

        SingleDevice dev = mDeviceStore.getSingleDevice(deviceId);

        if (dev.isModelSupported(LightModelApi.MODEL_NUMBER) && dev.getNumSupportedGroups(LightModelApi.MODEL_NUMBER) != 0) {
            mGroupAcksWaiting++;
            GroupModelApi.setModelGroupId(deviceId, LightModelApi.MODEL_NUMBER,index, 0, group );
            // If a light also supports power then set groups for that too.
            if (dev.isModelSupported(LightModelApi.MODEL_NUMBER) && dev.getNumSupportedGroups(LightModelApi.MODEL_NUMBER) != 0) {
                mGroupAcksWaiting++;
                GroupModelApi.setModelGroupId(deviceId, PowerModelApi.MODEL_NUMBER, index, 0, group);
            }
        }
        else if (dev.isModelSupported(SwitchModelApi.MODEL_NUMBER) && dev.getNumSupportedGroups(SwitchModelApi.MODEL_NUMBER) != 0) {
            mGroupAcksWaiting++;
            GroupModelApi.setModelGroupId(deviceId, SwitchModelApi.MODEL_NUMBER, index, 0, group);
        }
        else if (dev.isModelSupported(SensorModelApi.MODEL_NUMBER) && dev.getNumSupportedGroups(SensorModelApi.MODEL_NUMBER) != 0) {
            mGroupAcksWaiting++;
            GroupModelApi.setModelGroupId(deviceId, SensorModelApi.MODEL_NUMBER, index, 0, group);
        }
        else if (dev.isModelSupported(ActuatorModelApi.MODEL_NUMBER) && dev.getNumSupportedGroups(ActuatorModelApi.MODEL_NUMBER) != 0) {
            mGroupAcksWaiting++;
            GroupModelApi.setModelGroupId(deviceId, ActuatorModelApi.MODEL_NUMBER, index, 0, group);
        }

        // Check if device supports data model and that it supports groups. If it does, then setModelGroupId
        if (dev.isModelSupported(DataModelApi.MODEL_NUMBER) &&
                dev.getNumSupportedGroups(DataModelApi.MODEL_NUMBER) != 0) {
            mGroupAcksWaiting++;
            GroupModelApi.setModelGroupId(deviceId, DataModelApi.MODEL_NUMBER, index, 0, group);
        }

    }

    /**
     * Called when a response is not seen to a sent command.
     *
     * @param expectedMessage
     *            The message that would have been received in the Handler if there hadn't been a timeout.
     */
    private void onMessageTimeout(int expectedMessage, int id, int meshRequestId) {
        switch (expectedMessage) {

            case MeshService.MESSAGE_ACTUATOR_VALUE_ACK: {
                // Clear mLastActuatorMeshId if this is the mesh Id we were expecting.
                if (mLastActuatorMeshId == meshRequestId) {
                    mPendingDesiredTemperatureRequest = false;
                    mLastActuatorMeshId = 0;
                }

            }
            case MeshService.MESSAGE_GROUP_MODEL_GROUPID:
                if (mGroupAcksWaiting > 0) {
                    if (mGroupAckListener != null) {
                        // Timed out waiting for group update ACK.
                        mGroupAckListener.groupsUpdated(DeviceManger.mSendDeviceId, false,
                                mcontext.getString(R.string.group_timeout));
                    }
                    mGroupAcksWaiting = 0;
                }
                break;
            case MeshService.MESSAGE_DEVICE_ASSOCIATED:
                // Fall through.
            case MeshService.MESSAGE_CONFIG_MODELS:
                // If we couldn't find out the model support for the device then we have to report association failed.
                deviceAssociated(false, mcontext.getString(R.string.association_failed));
                if (mInfoListener!= null) {
                    mInfoListener.onDeviceConfigReceived(false);
                }
                break;
            case MeshService.MESSAGE_FIRMWARE_VERSION:
                if (mInfoListener != null) {
                    mInfoListener.onFirmwareVersion(0, 0, 0, false);
                }
                break;
            case MeshService.MESSAGE_BATTERY_STATE:
                if (mInfoListener!= null) {
                    mInfoListener.onDeviceInfoReceived(vid,pid,version, GroupAssignActivity.UNKNOWN_BATTERY_LEVEL,GroupAssignActivity.UNKNOWN_BATTERY_STATE, DeviceManger.mSendDeviceId, true);
                }
                break;
            case MeshService.MESSAGE_GROUP_NUM_GROUPIDS:
                if (mGroupAckListener != null) {
                    mGroupAckListener.groupsUpdated(DeviceManger.mSendDeviceId, false, mcontext.getString(R.string.group_query_fail));
                }
                break;
            case MeshService.MESSAGE_CONFIG_DEVICE_INFO:

                // if we were waiting to get the configModels once we associate the device, we just assume we couldn't get the models
                // that the device support, but the association was successful.
                if (mDeviceIdtoUuidHash.size() > 0) {

                    Device device =mDeviceStore.getDevice(mDeviceIdtoUuidHash.keyAt(0));
                    mDeviceIdtoUuidHash.removeAt(0);
                    if (device != null) {
                        String name = device.getName();
                        Toast.makeText(mcontext,
                                name == null ? "Device" : name + " " + mcontext.getString(R.string.added),
                                Toast.LENGTH_SHORT).show();
                    }
                    deviceAssociated(true,null);
                }
                if (mInfoListener!= null) {
                    mInfoListener.onDeviceConfigReceived(false);
                }
                if (mInfoListener != null) {
                    mInfoListener.onDeviceInfoReceived(new byte[0],new byte[0],new byte[0],GroupAssignActivity.UNKNOWN_BATTERY_LEVEL,GroupAssignActivity.UNKNOWN_BATTERY_STATE, 0, false);
                }
                break;
        }
    }

    private void storeAndNotifyNewSensorValue (SensorValue value, TemperatureStatus status, int deviceId) {

        if (value == null) return;

        if (value instanceof InternalAirTemperature) {
            // store the temperature in the status array.
            double tempCelsius = ((InternalAirTemperature) value).getCelsiusValue();
            status.setCurrentTemperature(tempCelsius);
            mTemperatureStatus.put(deviceId, status);

            // notify to temperatureFragment if the info received is related to the selected device.
            if (mTemperatureListener != null && deviceId == DeviceManger.mSendDeviceId) {
                mTemperatureListener.setCurrentTemperature(tempCelsius);
            }
        }
        else if (value instanceof DesiredAirTemperature) {
            double tempCelsius = ((DesiredAirTemperature) value).getCelsiusValue();
            status.setDesiredTemperature(tempCelsius);
            status.setDesiredTemperatureConfirmed(true);

            // notify to temperatureFragment if the info received is related to the selected device.
            if (mTemperatureListener != null && deviceId == DeviceManger.mSendDeviceId && !mPendingDesiredTemperatureRequest) {

                mTemperatureListener.setDesiredTemperature(tempCelsius);

            }
        }




    }


    // Runnables that execute after a timeout /////

    /**
     * This is the implementation of the periodic timer that will call sendLightRgb() every TRANSMIT_PERIOD_MS if
     * mNewColor is set to TRUE.
     */
    private Runnable transmitColorCallback = new Runnable() {
        @Override
        public void run() {
            if (mNewColor) {
                if (DeviceManger.mSendDeviceId != Device.DEVICE_ID_UNKNOWN) {
                    byte red = (byte) (Color.red(mColorToSend) & 0xFF);
                    byte green = (byte) (Color.green(mColorToSend) & 0xFF);
                    byte blue = (byte) (Color.blue(mColorToSend) & 0xFF);

                    LightModelApi.setRgb(DeviceManger.mSendDeviceId, red, green, blue, (byte)0xFF, 0, false);

                    Device light = mDeviceStore.getDevice(DeviceManger.mSendDeviceId);
                    LightState state = (LightState)light.getState(DeviceState.StateType.LIGHT);
                    if (light != null) {
                        state.setRed(red);
                        state.setGreen(green);
                        state.setBlue(blue);
                        state.setStateKnown(true);
                        light.setState(state);
                        mDeviceStore.addDevice(light);
                    }
                }
                // Colour sent so clear the flag.
                mNewColor = false;
            }
            mMeshHandler.postDelayed(this, TRANSMIT_COLOR_PERIOD_MS);
        }
    };

    /**
     * This is the implementation of the periodic temperature timer that will call setDesiredTemperature() every TRANSMIT_TEMPERATURE_PERIOD_MS if
     * mNewTemperature is set to TRUE.
     */
    private Runnable transmitTempCallback = new Runnable() {
        @Override
        public void run() {

            if (mLastActuatorMeshId != 0) {
                mService.killTransaction(mLastActuatorMeshId);
            }

            if (DeviceManger.mSendDeviceId != Device.DEVICE_ID_UNKNOWN && mTemperatureToSend != null) {
                mLastActuatorMeshId = ActuatorModelApi.setValue(DeviceManger.mSendDeviceId, mTemperatureToSend, true);
            }

            // update device's temperature status
            TemperatureStatus status = mTemperatureStatus.get(DeviceManger.mSendDeviceId);
            if (status == null) {
                status = new TemperatureStatus();
            }
            status.setDesiredTemperatureConfirmed(false);
            double celsiusValue = ((DesiredAirTemperature) mTemperatureToSend).getCelsiusValue();
            status.setDesiredTemperature(celsiusValue);
            mTemperatureStatus.put(DeviceManger.mSendDeviceId,status);
        }
    };


    private Runnable removeDeviceTimeout = new Runnable() {
        @Override
        public void run() {
            // Handle timeouts on removing devices.
            if (mRemovedListener != null) {
                // Timed out waiting for device UUID that indicates device removal happened.
                mRemovedListener.onDeviceRemoved(mRemovedDeviceId, false);
                mRemovedListener = null;
                mRemovedUuidHash = 0;
                mRemovedDeviceId = 0;
                mService.setDeviceDiscoveryFilterEnabled(false);
            }
        }
    };
    /**
     * Start transmitting colours and temperatures to the current send address. Colours are sent every TRANSMIT_PERIOD_MS ms and temperature values every TRANSMIT_TEMP_PERIOD_MS ms.
     */
    private void startPeriodicTransmit() {
        mMeshHandler.postDelayed(transmitColorCallback, TRANSMIT_COLOR_PERIOD_MS);
    }

    /**
     * Add a device to the device store, creating state based on model support.
     * @param deviceId Device id of the device to add.
     * @param uuidHash 31-bit UUID hash of the device to add.
     * @param shortName Appearance short name if known, otherwise null.
     * @param modelSupportBitmapLow The low part of the model support bitmap. Currently the only part we care about.
     */
    private void addDevice(int deviceId, int uuidHash, String shortName, long modelSupportBitmapLow, boolean showToast) {
        String name = null;
        if (shortName != null) {
            int id = deviceId - Device.DEVICE_ADDR_BASE;
            name = String.format(shortName.trim() + " %d", id);
        }
        SingleDevice device = new SingleDevice(deviceId, name, uuidHash, modelSupportBitmapLow, 0);
        mDeviceStore.addDevice(device);

        if (showToast) {
            Toast.makeText(mcontext, device.getName() + " " + mcontext.getString(R.string.added),
                    Toast.LENGTH_SHORT).show();

            checkIfDeviceRequiredAction();
        }

    }


    /**
     * Check if there is any device which needs any user action and create the notification for it.
     */
    private void checkIfDeviceRequiredAction() {
        List<Device> devices = mDeviceStore.getAllSingleDevices();
        for (Device device : devices) {
            SingleDevice singleDevice = (SingleDevice) device;

            // check if there is any sensor or actuator device which haven't been grouped yet.
            if ((singleDevice.isModelSupported(SensorModelApi.MODEL_NUMBER) || singleDevice.isModelSupported(ActuatorModelApi.MODEL_NUMBER))
                    && (singleDevice.getGroupMembership().size() == 0)) {
//                showNotificationFragment(mcontext.getString(R.string.user_action_req),
//                        mcontext.getString(R.string.need_device_group),
//                        POSITION_GROUP_CONFIG, false, true);
                Log.e(TAG,mcontext.getString(R.string.user_action_req) +"-----"+ mcontext.getString(R.string.need_device_group));

            }

        }
    }

    /**
     * Restore app settings including devices and groups.
     */
    private void restoreSettings(int settingsID) {

        // Try to get the settings if we know the ID.
        if (settingsID != Setting.UKNOWN_ID) {
            mDeviceStore.loadSetting(settingsID);
        }
        // save in sharePreferences the last settings used.
        SharedPreferences activityPrefs = mcontext.getSharedPreferences(mcontext.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPrefs.edit();

        if (mDeviceStore.getSetting() != null) {

            // set the networkKey to MeshService.
            if (mDeviceStore.getSetting().getNetworkKey() != null) {
                mService.setNetworkPassPhrase(mDeviceStore.getSetting().getNetworkKey());
            }

            // save in sharePreferences the last settings used.
            editor.putInt(SETTING_LAST_ID, settingsID);
            editor.commit();

            // get all the SingleDevices and GroudDevices from the dataBase.
            mDeviceStore.loadAllDevices();

            // set next device id to be used according with the last device used in the database.
            mService.setNextDeviceId(mDeviceStore.getSetting().getLastDeviceIndex()+1);

            // set TTL to the library
            mService.setTTL(mDeviceStore.getSetting().getTTL());
        }
        else {
            // No setting founded. We need to create one...
            Setting setting = new Setting();
            setting.setLastGroupIndex(Device.GROUP_ADDR_BASE + 5);
            mDeviceStore.setSetting(setting, true);

            // add group devices. By default we add 5 groups (1 for "All" with id=0 and 4 extra with ids 1-4).
//        	for (int i=0; i < 5 ; i++) {
//        		GroupDevice group;
//        		if (i==0) {
//        			group = new GroupDevice(Device.GROUP_ADDR_BASE, mcontext.getString(R.string.all_lights));
//        		}
//        		else {
//        			group = new GroupDevice(Device.GROUP_ADDR_BASE + i, mcontext.getString(R.string.group) + " " + i);
//        		}
//
//        		// store the group in the database.
//        		mDeviceStore.addGroupDevice(group,true);
//        	}
            //yaohaijun add for delate group
            GroupDevice group;
            group = new GroupDevice(Device.GROUP_ADDR_BASE, mcontext.getString(R.string.all_lights));
            mDeviceStore.addGroupDevice(group,true);
            // save in sharePreferences the last settings used.
            editor.putInt(SETTING_LAST_ID, mDeviceStore.getSetting().getId());
            editor.commit();
            editor.putBoolean("FAST_LAUNCH", false);
            editor.commit();


        }

    }






    private void getBatteryState(InfoListener listener){
        mInfoListener = listener;
        BatteryModelApi.getState(DeviceManger.mSendDeviceId);
    }


    /**
     * Notify device association has finished.
     * @param success
     */
    private void deviceAssociated(boolean success, String message) {
        // device associated so we need to clean the transaction id.
        mAssociationTransactionId = -1;

        if (mAssListener != null) {
            mAssListener.deviceAssociated(success, message);
        }
        if (success) {
            // Reload configuration fragment.
        }
    }



    private void confirmReplacingDatabase(final String json) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setMessage(mcontext.getString(R.string.confirm_replacing_db)).setCancelable(false)
                .setPositiveButton(mcontext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {

                                // read json and set to the database.
                                final boolean success = mDeviceStore.setConfigurationFromJson(json.toString(),mDeviceStore.getSetting().getNetworkKey());
                                new Thread(new Runnable() {
                                    public void run() {

                                        // notify to the user.
                                        Toast.makeText(mcontext, success?mcontext.getString(R.string.import_config_complete):mcontext.getString(R.string.import_config_error), Toast.LENGTH_SHORT).show();

                                        // reload settings.
                                        SharedPreferences activityPrefs = mcontext.getSharedPreferences(mcontext.getPackageName(),Context.MODE_PRIVATE);
                                        int lastIdUsed = activityPrefs.getInt(SETTING_LAST_ID, Setting.UKNOWN_ID);
                                        restoreSettings(lastIdUsed);

                                        // reload configuration fragment.
                                    }
                                });
                            }
                        }).start();

                    }
                }).setNegativeButton(mcontext.getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    public void disconnectBridge(){
        mService.disconnectBridge();
    }


    public void remove_Handle(){
        mService.setHandler(null);
        mMeshHandler.removeCallbacksAndMessages(null);
    }


    public int cancelTransaction(){
        mService.cancelTransaction(mAssociationTransactionId);
        return mAssociationTransactionId;
    }


    public void unbindService(){
        mcontext.unbindService(mServiceConnection);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == SCANCODE_RESULT_CODE) {
            if (resultCode == ((MainActivity)mcontext).RESULT_OK) {
                String url = data.getStringExtra("SCAN_RESULT");
                long auth = 0;
                UUID uuid = null;


                Uri uri=Uri.parse(url);
                String uuidS = uri.getQueryParameter("UUID");
                String ac = uri.getQueryParameter("AC");

                // Trying to get the UUID and AC from a URL
                if (uuidS != null && ac != null && uuidS.length() == 32 && ac.length() == 16) {
                    long uuidMsb =
                            ((Long.parseLong(uuidS.substring(0, 8), 16) & 0xFFFFFFFFFFFFFFFFL) << 32)
                                    | ((Long.parseLong(uuidS.substring(8,16),16) & 0xFFFFFFFFFFFFFFFFL));
                    long uuidLsb =
                            ((Long.parseLong(uuidS.substring(16,24),16) & 0xFFFFFFFFFFFFFFFFL) << 32)
                                    | ((Long.parseLong(uuidS.substring(24),16) & 0xFFFFFFFFFFFFFFFFL));

                    auth = ((Long.parseLong(ac.substring(0,8), 16) & 0xFFFFFFFFFFFFFFFFL) << 32)
                            | ((Long.parseLong(ac.substring(8), 16) & 0xFFFFFFFFFFFFFFFFL));

                    uuid = new UUID(uuidMsb, uuidLsb);

                }
                else { // trying to get the UUID and AC directly from params.

                    Pattern pattern =
                            Pattern.compile("&UUID=([0-9A-F]{8})"
                                            + "([0-9A-F]{8})([0-9A-F]{8})([0-9A-F]{8})"
                                            + "&AC=([0-9A-F]{8})([0-9A-F]{8})",
                                    Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(url);
                    if (matcher.find()) {
                        long uuidMsb =
                                ((Long.parseLong(matcher.group(1), 16) & 0xFFFFFFFFFFFFFFFFL) << 32)
                                        | ((Long.parseLong(matcher.group(2), 16) & 0xFFFFFFFFFFFFFFFFL));
                        long uuidLsb =
                                ((Long.parseLong(matcher.group(3), 16) & 0xFFFFFFFFFFFFFFFFL) << 32)
                                        | ((Long.parseLong(matcher.group(4), 16) & 0xFFFFFFFFFFFFFFFFL));

                        uuid = new UUID(uuidMsb, uuidLsb);

                        auth = ((Long.parseLong(matcher.group(5), 16) & 0xFFFFFFFFFFFFFFFFL) << 32)
                                | ((Long.parseLong(matcher.group(6), 16) & 0xFFFFFFFFFFFFFFFFL));


                    }

                }

                // checking if we got the uuid
                if (uuid != null && mService != null) {
                    if (mAssStartedListener != null) {
                        mAssStartedListener.associationStarted();
                        mAssociationTransactionId = mService.associateDevice(MeshService.getDeviceHashFromUuid(uuid), auth, true);
                    }

                } else {

                    // bad QR code
                    Toast.makeText(mcontext, mcontext.getString(R.string.qr_to_uuid_fail), Toast.LENGTH_LONG).show();
                }

            }
        } else if (requestCode == SHARING_RESULT_CODE) {
            if (tmpSharingFile != null) {
                tmpSharingFile.delete();
            }
        } else if (requestCode == PICKFILE_RESULT_CODE) {



            if (data == null || data.getData() == null) {
                Toast.makeText(mcontext, mcontext.getString(R.string.error_opening_file), Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri =data.getData();
            File file = new File(uri.getPath());

            // Check the extension of the file. App only accept .json extensions.
            if (!Utils.getFileExtension(file).equalsIgnoreCase(".json")) {
                Toast.makeText(mcontext,mcontext.getString(R.string.invalid_file_extension), Toast.LENGTH_SHORT).show();

                // no continue.
                return;
            }

            //Start reading a file...
            final StringBuilder json = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    json.append(line);
                }
                br.close();
            }
            catch (IOException e) {
                Toast.makeText(mcontext, mcontext.getString(R.string.error_opening_file), Toast.LENGTH_SHORT).show();
                return;
            }
            // end reading file

            // Confirm replacing database.
            confirmReplacingDatabase(json.toString());
        }
    }




    private Runnable progressTimeOut = new Runnable() {
        @Override
        public void run() {

            if (mDataListener != null) {
                mDataListener.UITimeout();
            }

        }
    };



    private void notifyAssociationFragment(int progress) {
//        showNotificationFragment(mcontext.getString(R.string.association_progress) + progress + "%",
//                mcontext.getString(R.string.association_notify),
//                POSITION_ASSOCIATION, true, false);
        Log.e(TAG,mcontext.getString(R.string.association_progress) + progress + "%"+"-----"+mcontext.getString(R.string.association_notify));
    }

    public TemperatureStatus getTemperatureStatus() {
        return mTemperatureStatus.get(DeviceManger.mSendDeviceId);
    }

    public CsrStateChangeInterface.ConnectionStateChange getmConnectionStateChange() {
        return mConnectionStateChange;
    }

    public void setmConnectionStateChange(CsrStateChangeInterface.ConnectionStateChange mConnectionStateChange) {
        this.mConnectionStateChange = mConnectionStateChange;
    }

    public CsrStateChangeInterface.SecurityCallback getmSecurityCallback() {
        return mSecurityCallback;
    }

    public void setmSecurityCallback(CsrStateChangeInterface.SecurityCallback SecurityCallback) {
        this.mSecurityCallback = SecurityCallback;
    }
}
