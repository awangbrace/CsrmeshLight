package com.csr.csrmeshdemo.view.wheelView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class CheckboxLinearlayout extends LinearLayout{
	private boolean isTouch = true;
	public CheckboxLinearlayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CheckboxLinearlayout(Context context, AttributeSet attrs) {
	   super(context, attrs);
	   // TODO Auto-generated constructor stub
}
	@SuppressLint("NewApi")
	public CheckboxLinearlayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		return false;
	}
	public void setTouch(boolean touch){
		isTouch = touch;
	}
}
