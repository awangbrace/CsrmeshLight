package com.csr.csrmeshdemo.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;



/**
 * 
 * @description :Class description goes here. 
 *
 * @author: 409136 
 * @Create Date :  2015年4月16日 
 * @version : V1.0 
 * @description :
 */
public class AlwaysMarqueeTextView extends TextView {
	
	/**
	 * 
	 * <p>Title: TODO</p> 
	 * <p>Description: TODO</p> 
	 * @param context
	 */
	public AlwaysMarqueeTextView(Context context) {
		super(context);
	}
	
	/**
	 * 
	 * <p>Title: TODO</p> 
	 * <p>Description: TODO</p> 
	 * @param context
	 * @param attrs
	 */
	public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * 
	 * <p>Title: TODO</p> 
	 * <p>Description: TODO</p> 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}