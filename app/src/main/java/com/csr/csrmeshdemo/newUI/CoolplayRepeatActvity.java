package com.csr.csrmeshdemo.newUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.csr.csrmeshdemo.view.CustomDialog;
import com.csr.csrmeshdemo.R;

import java.util.List;

public class CoolplayRepeatActvity extends AbActivity {
	private AbTitleBar mAbTitleBar = null;
	private ListView mAlarmRepears = null;
	private TextView mAlarmRepearTitle = null;
	private DialogCustomRepeatListviewAdapter mCustomRepeatAdapter = null;
	private String[] mRepeatArray;
	private boolean[] mRepeatIsSelectArray;
	private Button mBtnok;
	private Intent intent;
	private Bundle bunde;  
	private boolean repeat;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.common_list_layout);
		initView();
		initData();
		initListenner();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.alarm_repeat);
		// mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.color.title_bar_Color);
		// 获取ListView对象
		mAbTitleBar.setLogo(R.drawable.icon_back_selector);
		mAlarmRepears = (ListView) findViewById(R.id.lv_common_alarm_list);
		mAlarmRepearTitle = (TextView) findViewById(R.id.tv_common_alarm_title);
		mAlarmRepearTitle.setVisibility(View.GONE);
		View rightViewMore = mInflater.inflate(R.layout.right_logo_ok_btn, null);
		mAbTitleBar.addRightView(rightViewMore);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		mBtnok = (Button) rightViewMore.findViewById(R.id.okBtn);
	}

	/**
	 * 
	 */
	private void initData() {
		// TODO Auto-generated method stub
		intent = this.getIntent(); 
		bunde = intent.getExtras(); /* 取得Bundle对象中的数据 */
		mRepeatIsSelectArray = bunde.getBooleanArray("REPEAT");
		mRepeatArray = getResources().getStringArray(R.array.RepeatInfoArr);
		mCustomRepeatAdapter = new DialogCustomRepeatListviewAdapter(this,
				java.util.Arrays.asList(mRepeatArray));
		mAlarmRepears.setAdapter(mCustomRepeatAdapter);
		mCustomRepeatAdapter.setSelectItems(mRepeatIsSelectArray);
	}

	/**
	 * 
	 */
	private void initListenner() {
		// TODO Auto-generated method stub
		mAbTitleBar.setLogoOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(repeat){
					showBackDia();
				}else{
					finish();
				}
			}
		});
		mAlarmRepears.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mCustomRepeatAdapter.setSelectedPos(position);
				repeat = true;
			}
		});
		
		mBtnok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bunde.putBooleanArray("REPEAT", mCustomRepeatAdapter.getSelectItems());
				bunde.putInt("FUNCTION", 2);
				intent.putExtras(bunde);
				CoolplayRepeatActvity.this.setResult(RESULT_OK, intent); /* 关闭activity */  
				CoolplayRepeatActvity.this.finish();  
				finish();
			}
		});
	}

	public class DialogCustomRepeatListviewAdapter extends BaseAdapter {

		private Context mContext;
		private List<String> mItemList;
		private LayoutInflater mInflater;

		private boolean[] mSelectedPos;

		public DialogCustomRepeatListviewAdapter(Context context,
				List<String> list) {
			super();
			this.mContext = context;
			this.mItemList = list;
			this.mInflater = LayoutInflater.from(context);
			this.mSelectedPos = new boolean[7];
		}

		/**
		 * 
		 * @title:
		 * @description:
		 * @param:@return
		 * @return:boolean[]
		 * @throws
		 */
		public boolean[] getSelectItems() {
			return mSelectedPos;
		}

		/**
		 * 
		 * @title:
		 * @description:
		 * @param:@param items
		 * @return:void
		 * @throws
		 */
		public void setSelectItems(boolean[] items) {
			if (items != null && items.length == 7) {
				mSelectedPos = items;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mItemList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;

			if (null == mContext) {
				return null;
			}

			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.dialog_custom_repeat_listview_item, parent,
						false);
				viewHolder.tvItem = (TextView) convertView
						.findViewById(R.id.custom_tv_setting);
				viewHolder.imgSelected = (ImageView) convertView
						.findViewById(R.id.custom_img_check);
				viewHolder.tvItem.setText(mItemList.get(position));
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if (mSelectedPos[position]) {
				viewHolder.imgSelected.setImageResource(R.drawable.choice_a_normal);
			} else {
				viewHolder.imgSelected.setImageResource(R.color.transparent);
			}
			return convertView;
		}

		class ViewHolder {
			public TextView tvItem;
			public ImageView imgSelected;
		}

		public void setSelectedPos(int position) {
			mSelectedPos[position] = !mSelectedPos[position];
			super.notifyDataSetChanged();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if (keyCode == KeyEvent.KEYCODE_BACK ){
			if(repeat){
				showBackDia();
			}else{
				finish();
			}
		}
		return false;
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
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		CustomDialog customDialog = builder.customCreate();
		customDialog.show();
	}

}
