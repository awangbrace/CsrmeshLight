/**   
 * @Title:SmartAlarmAdapter.java
 * @Package com.huawei.smallRadar.CoolPlay.adapter
 * @Description: 
 * @author 姚海军  
 * @date 2016年3月29日上午11:05:33
 * @version V1.0   
 * History :
 *  1. Yaohaijun add for the first release ,2016年3月29日  
 *
 * 
 * Copyright (C), Tonly electronics Holdincs Limited
 * All rights reserved
 ******************************************************************************/
package com.csr.csrmeshdemo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csr.csrmeshdemo.view.CustomDialog;
import com.csr.csrmeshdemo.Db.DbManger;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.entities.AlarmBean;
import com.csr.csrmeshdemo.util.L;

import java.util.LinkedList;
import java.util.List;


/**
 * @author 姚海军
 * 智能闹钟主界面ExpandAdapter
 *
 */
public class SmartAlarmAdapter extends BaseAdapter {
	private Context mContext;
	private List<AlarmBean> malarms = new LinkedList<AlarmBean>();
	private String[] mWeekArray;
	private boolean edit_open = false;
	private ViewHolder viewHolder = null;

	/**
	 * 
	 */
	public SmartAlarmAdapter(Context context, List<AlarmBean> mAlarms2) {
		// TODO Auto-generated constructor stub
		mContext = context;
		malarms = mAlarms2;
		mWeekArray = context.getResources().getStringArray(R.array.WeekInfoArr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return malarms.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return malarms.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.common_alarm_listview_item, parent, false);
			viewHolder.SmartAlarmIcon = (ImageView) view
					.findViewById(R.id.imgAlarmIcon);
			viewHolder.SmartAlarmName = (TextView) view
					.findViewById(R.id.tvAlarmName);
			viewHolder.SmartAlarmTime = (TextView) view
					.findViewById(R.id.tvAlarmTime);
			viewHolder.SmartAlarmWeekday = (TextView) view
					.findViewById(R.id.tvAlarmWeekday);
			viewHolder.SmartAlarmSwitch = (CheckBox) view
					.findViewById(R.id.switch_alarm);
			viewHolder.SmartAlarmDelete = (ImageView) view
					.findViewById(R.id.img_alarm_close);
			viewHolder.delete_lay = (LinearLayout) view.findViewById(R.id.delete_lay);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.SmartAlarmName.setText(malarms.get(position).getName());
		viewHolder.SmartAlarmTime.setText(malarms.get(position).getTime());
		viewHolder.SmartAlarmWeekday.setText(value2customWeekdayShow(malarms
				.get(position).getWeekday()));

		viewHolder.SmartAlarmSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (malarms.get(position).getActive()==AlarmBean.ARLARM_OFF) {
					switchAlarmControl(malarms.get(position),
							AlarmBean.ARLARM_ON);
					L.e("闹钟打开");
					notifyDataSetChanged();
				} else {
					switchAlarmControl(malarms.get(position),
							AlarmBean.ARLARM_OFF);
					malarms.get(position).setActive(AlarmBean.ARLARM_OFF);
					L.e("闹钟关闭");
					notifyDataSetChanged();
				}
			}
		});

		if (edit_open) {
			viewHolder.SmartAlarmDelete.setVisibility(View.VISIBLE);
			viewHolder.delete_lay.setVisibility(View.VISIBLE);
			viewHolder.SmartAlarmSwitch.setVisibility(View.GONE);
			viewHolder.delete_lay
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
							builder.setTitle(mContext.getResources().getString(R.string.delete_sure)+"“"+malarms.get(position).getName()+"”"+mContext.getResources().getString(R.string.if_sure));
							builder.setNegativeButton(mContext.getResources().getString(R.string.cancle),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});

							builder.setPositiveButton(mContext.getResources().getString(R.string.ok),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
//											SmallRadar defualSmallRadar = SmallRadarManager
//													.getInstance().getDefaultSmallRadar();
//											if (defualSmallRadar != null)
//												defualSmallRadar.AlarmDelete(malarms.get(
//														position).getTimerId());
//											defualSmallRadar.removeAlarmById(malarms.get(
//													position).getTimerId());
//											SmartAlarmActivity clockActivity = (SmartAlarmActivity)mContext;
//											Message msg = new Message();
//											msg.what = 0;
//											msg.obj = defualSmallRadar;
//											clockActivity.Handler.sendMessage(msg);
										}
									});
							builder.customCreate().show();
			
						}
					});

		} else {
			if (malarms.get(position).getActive() == 1) {
				viewHolder.SmartAlarmSwitch.setChecked(true);
			} else {
				viewHolder.SmartAlarmSwitch.setChecked(false);
			}
			viewHolder.SmartAlarmDelete.setVisibility(View.GONE);
			viewHolder.delete_lay.setVisibility(View.GONE);
			viewHolder.SmartAlarmSwitch.setVisibility(View.VISIBLE);
			if(viewHolder.SmartAlarmSwitch.isChecked()){
				viewHolder.SmartAlarmIcon.setImageResource(R.drawable.icon_light);
			}else{
				viewHolder.SmartAlarmIcon.setImageResource(R.drawable.icon_light);
			}
		}

		return view;
	}

	final static class ViewHolder {
		ImageView SmartAlarmIcon;
		TextView SmartAlarmName;
		TextView SmartAlarmTime;
		TextView SmartAlarmWeekday;
		CheckBox SmartAlarmSwitch;
		ImageView SmartAlarmDelete;
		LinearLayout delete_lay;
	}

	private String value2customWeekdayShow(String weekday) {
		String show = "";

		if (weekday == null || weekday.length() != 7) {
			return null;
		}

		if (weekday.equalsIgnoreCase("1111111")) {
			show = mContext.getString(R.string.alarm_repeat_everyday);
		} else if (weekday.equalsIgnoreCase("0000000")) {
			show = mContext.getString(R.string.alarm_repeat_once);
		} else if (weekday.equalsIgnoreCase("1111100")) {
			show = mContext.getString(R.string.alarm_repeat_weekday);
		} else if (weekday.equalsIgnoreCase("0000011")) {
			show = mContext.getString(R.string.alarm_repeat_weekend);
		} else {

			boolean[] mSelectDialogItems = new boolean[7];

			for (int i = 0; i < weekday.length(); i++) {
				char c = weekday.charAt(i);
				if ("1".equalsIgnoreCase(String.valueOf(c))) {
					mSelectDialogItems[i] = true;
				} else if ("0".equalsIgnoreCase(String.valueOf(c))) {
					mSelectDialogItems[i] = false;
				}
			}

			for (int i = 0; i < mSelectDialogItems.length; i++) {
				if (mSelectDialogItems[i]) {
					show = show + mWeekArray[i] + " ";
				}
			}

		}
		return show;
	}

	public void EditAlarm(boolean isEdit) {
		edit_open = isEdit;
		notifyDataSetChanged();
	}

	/**
	 * 
	 */
	public void updateSmartAlarmData(List<AlarmBean> alarms) {
		// TODO Auto-generated method stub
		malarms = alarms;
		notifyDataSetChanged();
	}

	private void switchAlarmControl(AlarmBean mAlarm, int active) {
		// TODO Auto-generated method stub
		mAlarm.setActive(active);
//			defualSmallRadar.AlarmEdit(AlarmBean);
		DbManger.getInstance(mContext).saveDbDeviceInfoData(mAlarm);
	}
}
