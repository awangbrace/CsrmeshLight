/**   
 * @Title:AlarmEditMenuAdapter.java
 * @Package com.huawei.smallRadar.CoolPlay.adapter
 * @Description: 
 * @author 姚海军  
 * @date 2016年3月23日上午11:14:01
 * @version V1.0   
 * History :
 *  1. Yaohaijun add for the first release ,2016年3月23日  
 *
 * 
 * Copyright (C), Tonly electronics Holdincs Limited
 * All rights reserved
 ******************************************************************************/
package com.csr.csrmeshdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.newUI.SmartAlarmEditActivity;
import com.csr.csrmeshdemo.entities.AlarmBean;
import com.csr.csrmeshdemo.util.L;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 姚海军
 *
 */
public class AlarmEditMenuAdapter extends BaseAdapter {

	private List<String> mitemNameList = new LinkedList<String>();
	private SmartAlarmEditActivity mContext;
	private LayoutInflater mInflater;
	private String valueTv = "";
	private ViewHolder viewHolder = null;
	private int msoonze = 0;
	/**
	 * 
	 */
	public AlarmEditMenuAdapter(SmartAlarmEditActivity context, List<String> itemNameList, int soonze) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mitemNameList = itemNameList;
		this.mInflater = LayoutInflater.from(mContext);
		this.msoonze = soonze;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mitemNameList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mitemNameList.get(position);
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
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if (view == null) {
			viewHolder = new ViewHolder();
			view = mInflater.inflate(R.layout.common_listview_item, parent, false);
			viewHolder.listview_item_top_text = (TextView) view
					.findViewById(R.id.common_listview_item_top_text);
			viewHolder.listview_item_value = (TextView) view
					.findViewById(R.id.common_listview_item_value);
			viewHolder.layout_alarm_info = (LinearLayout) view
					.findViewById(R.id.common_listview_info);
			viewHolder.layout_alarm_switch = (LinearLayout) view
					.findViewById(R.id.common_listview_switch);
			viewHolder.switch_snooze = (CheckBox) view
					.findViewById(R.id.switch_snooze);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		if (position == 0) {
			viewHolder.layout_alarm_info.setVisibility(View.VISIBLE);
			viewHolder.layout_alarm_switch.setVisibility(View.GONE);
		} else if (position == 1) {
			viewHolder.layout_alarm_info.setVisibility(View.GONE);
			viewHolder.layout_alarm_switch.setVisibility(View.VISIBLE);
		}
		if (msoonze == 1) {
			viewHolder.switch_snooze.setChecked(true);
		} else {
			viewHolder.switch_snooze.setChecked(false);
		}
		viewHolder.switch_snooze.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (msoonze == AlarmBean.SLEEP_ON) {
					msoonze= AlarmBean.SLEEP_OFF;
					mContext.updateSoonze(AlarmBean.SLEEP_OFF);
					L.e("贪睡关闭");
				} else {
					msoonze= AlarmBean.SLEEP_ON;
					mContext.updateSoonze(AlarmBean.SLEEP_ON);
					L.e("贪睡打开");
				}
			}
		});

		viewHolder.listview_item_top_text.setText(mitemNameList.get(position));
		viewHolder.listview_item_value.setText(valueTv);
		return view;
	}

	final static class ViewHolder {
		TextView listview_item_top_text;
		TextView listview_item_value;
		ImageView listview_item_icon;
		LinearLayout layout_alarm_info;
		LinearLayout layout_alarm_switch;
		CheckBox switch_snooze;
	}

	public void RefreshValue(String str) {
		valueTv = str;
	}
	
	public void RefreshSnoozeValue(int soonze) {
		msoonze = soonze;
		notifyDataSetChanged();
	}

}
