/**
 * @Title:SmartAlarmEditActivity.java
 * @Description:
 * @author 姚海军
 * @date 2016年3月19日下午5:23:20
 * @version V1.0
 * History :
 *  1. Yaohaijun add for the first release ,2016年3月19日  
 *
 *
 * Copyright (C), Tonly electronics Holdincs Limited
 * All rights reserved
 ******************************************************************************/
package com.csr.csrmeshdemo.newUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.csr.csrmeshdemo.view.CustomDialog;
import com.csr.csrmeshdemo.Db.DbManger;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.adapter.AlarmEditMenuAdapter;
import com.csr.csrmeshdemo.entities.AlarmBean;
import com.csr.csrmeshdemo.util.Constant;
import com.csr.csrmeshdemo.util.L;
import com.csr.csrmeshdemo.view.wheelView.OnWheelChangedListener;
import com.csr.csrmeshdemo.view.wheelView.OnWheelScrollListener;
import com.csr.csrmeshdemo.view.wheelView.WheelView;
import com.csr.csrmeshdemo.view.wheelView.adapter.AbstractWheelTextAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


/**
 * @author 姚海军
 *
 */
public class SmartAlarmEditActivity extends AbActivity implements
        OnClickListener, OnItemClickListener {
    public AbTitleBar mAbTitleBar = null;

    private String mHour = "";
    private String mMin = "";

    private AlarmBean mAlarm = null;
    private TextView mBtnok;
    private AlarmEditMenuAdapter mEditMenuAdapter;
    private ListView mLvAlarmMenu;
    private LinearLayout mRenameAlarm;
    private TextView mRenameText;
    private static final int ALARM_REPEAT = 0;
    private String[] mWeekArray;
    private String mShowWeekDay = "";
    private String mAlarmName = "";

//    private SmallRadarManager smallRadarManager;
//    private SmallRadar defualSmallRadar = new SmallRadar();
    private boolean[] mRepeatIsSelectArray = new boolean[] { false, false,
            false, false, false, false, false };
    private WheelView wvshours;
    private WheelView wvsminutes;
    private ArrayList<String> arrsHours = new ArrayList<String>();
    private ArrayList<String> arrsMinutes = new ArrayList<String>();
    private TextAdapter shoursAdapter;
    private TextAdapter sminutesAdapter;
    private int maxsize = 20;
    private int minsize = 14;
    private int alarmId = -1;
    private boolean alarmedit;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.coolplay_smart_alarm_edit_layout);
        initView();
        initlistener();
        initData();
    }

    private void initView() {
        // TODO Auto-generated method stub
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setLogo(R.drawable.icon_back);
        mAbTitleBar.setTitleBarBackground(R.color.title_bar_background_color);
        mAbTitleBar.setPadding((int)getResources().getDimension(R.dimen.titlebar_logo_padding),16,0,0);
        mAbTitleBar
                .setTitleText(getResources().getString(R.string.smart_alarm));
        mAbTitleBar.clearRightView();
        View rightViewMore = mInflater
                .inflate(R.layout.ok, null);
        mAbTitleBar.addRightView(rightViewMore);
        mBtnok = (TextView) rightViewMore.findViewById(R.id.finish);
        mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
        mLvAlarmMenu = (ListView) findViewById(R.id.timing_lv_menu);
        mRenameAlarm = (LinearLayout) findViewById(R.id.layout_rename_alarm);
        mRenameText = (TextView) findViewById(R.id.tv_rename_alarm);
        wvshours = (WheelView) findViewById(R.id.hour);
        wvsminutes = (WheelView) findViewById(R.id.minute);
    }



    /**
     *
     */
    private void initlistener() {
        // TODO Auto-generated method stub
        mAbTitleBar.setLogoOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//				finish();
                if(alarmedit){

                    showBackDia();
                }else{
                    finish();
                }
            }
        });

        mBtnok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mAlarm.setActive(AlarmBean.ARLARM_ON);
                updateAlarmTime();
//                if (defualSmallRadar != null) {
//                    defualSmallRadar.AlarmEdit(mAlarm);
//                    defualSmallRadar.addAlarms(mAlarm);
//                }
                DbManger.getInstance(SmartAlarmEditActivity.this).saveDbDeviceInfoData(mAlarm);
                finish();
            }
        });
        mRenameAlarm.setOnClickListener(this);
        mLvAlarmMenu.setOnItemClickListener(this);

        wvshours.setCyclic(true);
        wvshours.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) shoursAdapter.getItemText(wheel
                        .getCurrentItem());
                mHour = currentText;
                setTextviewSize(currentText, shoursAdapter);
                Log.e("addChangingListener1", "addChangingListener1");

            }
        });
        wvshours.addScrollingListener(new OnWheelScrollListener() {

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
                updateAlarmTime();
                alarmedit = true;
            }
        });
        wvsminutes.setCyclic(true);
        wvsminutes.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) sminutesAdapter.getItemText(wheel
                        .getCurrentItem());
                mMin = currentText;
                setTextviewSize(currentText, sminutesAdapter);
            }
        });
        wvsminutes.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) sminutesAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, sminutesAdapter);
                updateAlarmTime();
                alarmedit = true;
            }
        });
    }

    /**
     *
     */
    private void initData() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        alarmId = intent.getIntExtra("ALARMID", -1);
        String[] coolplay_list_item_text_array = new String[] {};
        coolplay_list_item_text_array = getResources().getStringArray(
                R.array.alarm_menu_array);

        ArrayList<String> itemNameList = new ArrayList<String>(
                Arrays.asList(coolplay_list_item_text_array));
        mEditMenuAdapter = new AlarmEditMenuAdapter(this, itemNameList,
                AlarmBean.SLEEP_OFF);
        mLvAlarmMenu.setAdapter(mEditMenuAdapter);
        mWeekArray = getResources().getStringArray(R.array.WeekInfoArr);
//        smallRadarManager = SmallRadarManager.getInstance();
//        defualSmallRadar = smallRadarManager.getDefaultSmallRadar();
        initsHours();
        initMinutes();
        updateSelectAlarm(alarmId);

        shoursAdapter = new TextAdapter(SmartAlarmEditActivity.this, arrsHours,
                getHoursItem(mHour), maxsize, minsize);
        wvshours.setVisibleItems(5);
        wvshours.setViewAdapter(shoursAdapter);
        wvshours.setCurrentItem(getHoursItem(mHour));

        sminutesAdapter = new TextAdapter(SmartAlarmEditActivity.this,
                arrsMinutes, getMinutesItem(mMin), maxsize, minsize);
        wvsminutes.setVisibleItems(5);
        wvsminutes.setViewAdapter(sminutesAdapter);
        wvsminutes.setCurrentItem(getMinutesItem(mMin));
    }

    private void showBackDia(){
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(
                R.string.finish_tips));
        builder.setNegativeButton(
                getResources().getString(R.string.cancle),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setPositiveButton(getResources().getString(R.string.giveup),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        CustomDialog customDialog = builder.customCreate();
        customDialog.show();
    }

    /**
     * 更新闹钟时间
     */
    private void updateAlarmTime() {
        String time = mHour + ":" + mMin;

        if (mAlarm != null) {
            mAlarm.setTime(time);
            L.e("闹钟时间修改为" + time);
        }
    }

    public void updateTimeingWeekday(boolean[] SelectDialogItems) {
        // TODO Auto-generated method stub
        mShowWeekDay = "";

        char[] week = new char[7];

        for (int i = 0; i < SelectDialogItems.length; i++) {
            if (SelectDialogItems[i]) {
                week[i] = '1';
            } else {
                week[i] = '0';
            }
        }
        String weekstr = new String(week);

        int customValue = selectCustomWeekdayToValue(SelectDialogItems);

        if (weekstr.equalsIgnoreCase("0000000")) {
            mAlarm.setRepeat(0);
            L.e("闹钟重复模式修改为" + AlarmBean.REPEAT_MODE_ONCE);
            String weekday = mAlarm.repeatToWeekday(AlarmBean.REPEAT_MODE_ONCE,
                    -1);
            mAlarm.setWeekday(weekday);
            L.e("闹钟重复周期修改为" + weekday);
        } else {
            mAlarm.setRepeat(1);
            L.e("闹钟重复模式修改为" + AlarmBean.REPEAT_MODE_CUSTOM);
            String weekday = mAlarm.repeatToWeekday(
                    AlarmBean.REPEAT_MODE_CUSTOM, customValue);
            mAlarm.setWeekday(weekday);
            L.e("闹钟重复周期修改为" + weekday);
        }
        if (weekstr.equalsIgnoreCase("1111111")) {
            mShowWeekDay = this.getString(R.string.alarm_repeat_everyday);
        } else if (weekstr.equalsIgnoreCase("0000000")) {
            mShowWeekDay = this.getString(R.string.alarm_repeat_Never);
        } else if (weekstr.equalsIgnoreCase("1111100")) {
            mShowWeekDay = this.getString(R.string.alarm_repeat_weekday);
        } else if (weekstr.equalsIgnoreCase("0000011")) {
            mShowWeekDay = this.getString(R.string.alarm_repeat_weekend);
        }
        mEditMenuAdapter.RefreshValue(mShowWeekDay);
        mEditMenuAdapter.notifyDataSetChanged();
    }

    private String value2customWeekdayShow(String weekday) {
        String show = "";

        if (weekday == null || weekday.length() != 7) {
            return null;
        }

        for (int i = 0; i < weekday.length(); i++) {
            char c = weekday.charAt(i);
            if ("1".equalsIgnoreCase(String.valueOf(c))) {
                mRepeatIsSelectArray[i] = true;
            } else if ("0".equalsIgnoreCase(String.valueOf(c))) {
                mRepeatIsSelectArray[i] = false;
            }
        }
        if (weekday.equalsIgnoreCase("1111111")) {
            show = this.getString(R.string.alarm_repeat_everyday);
        } else if (weekday.equalsIgnoreCase("0000000")) {
            show = this.getString(R.string.alarm_repeat_Never);
        } else if (weekday.equalsIgnoreCase("1111100")) {
            show = this.getString(R.string.alarm_repeat_weekday);
        } else if (weekday.equalsIgnoreCase("0000011")) {
            show = this.getString(R.string.alarm_repeat_weekend);
        } else {

            for (int i = 0; i < mRepeatIsSelectArray.length; i++) {
                if (mRepeatIsSelectArray[i]) {
                    show = show + mWeekArray[i];
                }
            }
        }
        return show;
    }

    public void updateAlarmName(String name) {
        if (name == null) {
            return;
        }
        if (mAlarm != null) {
            mAlarm.setName(name);
            mRenameText.setText(name);
        }

    }

    private int selectCustomWeekdayToValue(boolean[] selectItems) {
        int value = 0;
        if (selectItems == null) {
            return 0;
        }
        for (int i = 0; i < selectItems.length; i++) {
            if (selectItems[i]) {
                value = value | AlarmBean.WEEKDAY_ARRAY[i];
                mShowWeekDay = mShowWeekDay + mWeekArray[i];
            }
        }
        return value;
    }

    /**
     * 刷新选择的闹钟
     *
     * @return
     */
    private void updateSelectAlarm(int alarmId) {
        if (alarmId == -1) {
            mAlarm = new AlarmBean();
            Calendar c = Calendar.getInstance();
            if (String.valueOf(c.get(Calendar.HOUR_OF_DAY)).length() == 1) {

                mHour = "0" + c.get(Calendar.HOUR_OF_DAY);
                L.v(mHour + ":" + mMin);
            } else {
                mHour = "" + c.get(Calendar.HOUR_OF_DAY);
            }
            if (String.valueOf(c.get(Calendar.MINUTE)).length() == 1) {
                mMin = "0" + String.valueOf(c.get(Calendar.MINUTE));
            } else {
                mMin = String.valueOf(c.get(Calendar.MINUTE));
                L.v(mHour + ":" + mMin);
            }
            updateAlarmTime();
            updateAlarmName(getResources().getString(R.string.alarm));
            updateTimeingWeekday(mRepeatIsSelectArray);
            mEditMenuAdapter.RefreshSnoozeValue(mAlarm.getSleep());

        } else {
                mAlarm = DbManger.getInstance(SmartAlarmEditActivity.this).queryData(Integer.toString(alarmId));
//                mAlarm = new AlarmBean();
                int hour = mAlarm.getHour();

                int minute = mAlarm.getMinute();
                updateAlarmName(mAlarm.getName());
                mHour = String.valueOf(hour);
                if (String.valueOf(mHour).length() == 1) {
                    mHour = "0" + mHour;
                } else {

                }
                if (String.valueOf(minute).length() == 1) {
                    mMin = "0" + String.valueOf(minute);
                } else {
                    mMin = String.valueOf(minute);
                }
                updateAlarmTime();
                value2customWeekdayShow(mAlarm.getWeekday());
                updateTimeingWeekday(mRepeatIsSelectArray);
                mEditMenuAdapter.RefreshSnoozeValue(mAlarm.getSleep());

        }
    }

    /**
     *
     */
    public void updateSoonze(int snooze) {
        // TODO Auto-generated method stub
        L.e("贪睡模式修改为======" + snooze);
        mAlarm.setSleep(snooze);
        alarmedit = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.layout_rename_alarm:
                mAlarmName = mRenameText.getText().toString();

                final CustomDialog.Builder builder = new CustomDialog.Builder(this);
                builder.setEditText(mAlarmName);
                builder.setTitle(getResources().getString(R.string.name_for_alarm));
                builder.setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                mAlarmName = builder.message;
                                updateAlarmName(mAlarmName);
                                L.e("闹钟名字修改为" + mAlarmName);

                            }

                        });

                builder.setNegativeButton(
                        getResources().getString(R.string.cancle),
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.RenameCreate(Constant.RENAME_SENCE_NAME).show();
                alarmedit = true;
                break;

            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, intent);
        if (arg1 == RESULT_OK) {
            if (intent.getIntExtra("FUNCTION", 1) == 2) {
                Bundle bundle = intent.getExtras();
                mRepeatIsSelectArray = bundle.getBooleanArray("REPEAT");
                updateTimeingWeekday(mRepeatIsSelectArray);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent it = new Intent();
        // TODO Auto-generated method stub
        switch (position) {
            case ALARM_REPEAT:
                it.setClass(this, CoolplayRepeatActvity.class);
                Bundle bundle = new Bundle();
                bundle.putBooleanArray("REPEAT", mRepeatIsSelectArray);
                it.putExtras(bundle);
                startActivityForResult(it, 0);
                alarmedit = true;
                break;
            default:
                break;
        }
    }

    private class TextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected TextAdapter(Context context, ArrayList<String> list,
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

    public void initsHours() {

        for (int i = 0; i < 24; i++) {
            if (String.valueOf(i).length() == 1) {
                arrsHours.add("0" + i);
            } else {
                arrsHours.add("" + i);
            }

        }
    }

    public void initMinutes() {
        for (int i = 0; i < 60; i++) {
            if (String.valueOf(i).length() == 1) {
                arrsMinutes.add("0" + i);
            } else {
                arrsMinutes.add("" + i);
            }

        }
    }

    public int getHoursItem(String hours) {
        int size = arrsHours.size();
        int hoursIndex = 0;
        boolean nohours = true;
        for (int i = 0; i < size; i++) {
            if (hours.equals(arrsHours.get(i))) {
                nohours = false;
                return hoursIndex;
            } else {
                hoursIndex++;
            }
        }
        if (nohours) {
            mHour = "12";
            return arrsHours.size() / 2;
        }
        return hoursIndex;
    }

    public int getMinutesItem(String minutes) {
        int size = arrsMinutes.size();
        int minutesIndex = 0;
        boolean nominutes = true;
        for (int i = 0; i < size; i++) {
            if (minutes.equals(arrsMinutes.get(i))) {
                nominutes = false;
                return minutesIndex;
            } else {
                minutesIndex++;
            }
        }
        if (nominutes) {
            mMin = "00";
            return 0;
        }
        return minutesIndex;
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, TextAdapter adapter) {
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
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK ){
            if(alarmedit){
                showBackDia();
            }else{
                finish();
            }
        }
        return false;
    }
}
