/**   
 * @Title:SmartAlarmActivity.java
 * @Package com.huawei.smallRadar.CoolPlay
 * @Description: 
 * @author ???  
 * @date 2016?3?19???5:23:20
 * @version V1.0   
 * History :
 *  1. Yaohaijun add for the first release ,2016?3?19?  
 *
 * 
 * Copyright (C), Tonly electronics Holdincs Limited
 * All rights reserved
 ******************************************************************************/
package com.csr.csrmeshdemo.newUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ab.view.titlebar.AbTitleBar;
import com.csr.csrmeshdemo.Db.DbManger;
import com.csr.csrmeshdemo.util.L;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.adapter.SmartAlarmAdapter;
import com.csr.csrmeshdemo.base.BaseFragment;
import com.csr.csrmeshdemo.entities.AlarmBean;

import java.util.LinkedList;
import java.util.List;


/**
 * @author ???
 *???????
 */
public class SmartAlarmFragment extends BaseFragment implements
		OnClickListener, OnItemClickListener {
	public AbTitleBar mAbTitleBar = null;
	private LinearLayout mAddAlarmLayout, com_list_lay, cool_clock_lay;
	private ListView lvAlarms = null;
	private SmartAlarmAdapter smartAlarmAdapter;
	private List<AlarmBean> mAlarms = new LinkedList<AlarmBean>();
//	private Button mBtnEdit;
	private boolean isEdit = false;
//	private SmallRadar defualSmallRadar;
	private Toast toast;
	private View mRootView;
	public Handler Handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				if (defualSmallRadar != null) {
//					mAlarms = defualSmallRadar.getAlarms();
					mAlarms =  DbManger.getInstance(getActivity()).queryDbDeviceInfoData();
					smartAlarmAdapter.updateSmartAlarmData(mAlarms);
					JudgeSenceList();
//				}

				break;

			default:

				break;
			}
			super.handleMessage(msg);
		}

	};


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView == null) {
			// The last two arguments ensure LayoutParams are inflated properly.
			mRootView = inflater.inflate(R.layout.coolplay_smart_alarm_layout, container, false);

		}
		initView(mRootView);
		initlistener();
		initData();
		return mRootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

//		try {
//			mController = (DeviceController) activity;
//		}
//		catch (ClassCastException e) {
//			throw new ClassCastException(activity.toString() + " must implement DeviceController callback interface.");
//		}
	}


	private void initView(View view) {
		// TODO Auto-generated method stub
//		mAbTitleBar = this.getTitleBar();
//		mAbTitleBar
//				.setTitleText(getResources().getString(R.string.smart_alarm));
//		mAbTitleBar.setLogo(R.drawable.icon_back_selector);
//		mAbTitleBar.setTitleBarBackground(R.color.title_bar_Color);
//		mAbTitleBar.clearRightView();
//
		mAddAlarmLayout = (LinearLayout) view.findViewById(R.id.layout_add_alarm);
		lvAlarms = (ListView) view.findViewById(R.id.lv_common_alarm_list);
//		View rightViewMore = mInflater.inflate(R.layout.right_logo_edit_btn,
//				null);
//		mAbTitleBar.addRightView(rightViewMore);
//		mBtnEdit = (Button) view.rightViewMore.findViewById(R.id.EditBtn);
//		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		com_list_lay = (LinearLayout) view.findViewById(R.id.com_list_lay);
		cool_clock_lay = (LinearLayout) view.findViewById(R.id.cool_clock_lay);
	}

	/**
	 * 
	 */
	private void initlistener() {
		// TODO Auto-generated method stub
		mAddAlarmLayout.setOnClickListener(this);
//		mAbTitleBar.setLogoOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
		lvAlarms.setOnItemClickListener(this);
//		mBtnEdit.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// isEdit = true;
//				// smartAlarmAdapter.EditAlarm(isEdit);
//				if (isEdit) {
//					isEdit = false;
//					smartAlarmAdapter.EditAlarm(isEdit);
//					mBtnEdit.setBackground(getResources().getDrawable(
//							R.drawable.icon_edit_selector));
//				} else {
//					isEdit = true;
//					smartAlarmAdapter.EditAlarm(isEdit);
//					mBtnEdit.setBackground(getResources().getDrawable(
//							R.drawable.icon_ok_selector));
//				}
//			}
//		});
	}

	/**
	 * 
	 */
	private void initData() {
		// TODO Auto-generated method stub
//		if (defualSmallRadar != null)
//			defualSmallRadar.getAlarmList();
		smartAlarmAdapter = new SmartAlarmAdapter(getActivity(), mAlarms);
		lvAlarms.setAdapter(smartAlarmAdapter);
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
		case R.id.layout_add_alarm:
			if (mAlarms.size() < 5) {
				Intent it = new Intent();
				it.setClass(getActivity(), SmartAlarmEditActivity.class);
				startActivity(it);
			} else {

				if (toast != null) {
					toast.cancel();
				}
				toast = Toast.makeText(getActivity(), getResources()
						.getString(R.string.alarm_prompt), Toast.LENGTH_SHORT);
				toast.show();
			}

			break;
		default:
			break;

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
		// TODO Auto-generated method stub

		Intent it = new Intent();
		it.putExtra("ALARMID", mAlarms.get(position).getTimerId());
		it.setClass(getActivity(), SmartAlarmEditActivity.class);
		startActivity(it);
	}



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Message msg = new Message();
		msg.what = 0;
//		msg.obj = mSmallRadarManager.getDefaultSmallRadar();
		Handler.sendMessage(msg);
		L.e("111111111111111111111111111","99999999999999999999999");
	}




	private void JudgeSenceList() {
		// TODO Auto-generated method stub
		if (mAlarms.size() > 0) {
			com_list_lay.setVisibility(View.VISIBLE);
			cool_clock_lay.setVisibility(View.GONE);
		} else {
			cool_clock_lay.setVisibility(View.VISIBLE);
			com_list_lay.setVisibility(View.GONE);
		}
	}
}
