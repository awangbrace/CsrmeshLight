/**
 * 
 */
package com.csr.csrmeshdemo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
/**
 * @author Shadow Blade
 *
 */

public class NormalViewPager extends ViewPager {


	public NormalViewPager(Context context) {
		super(context);
	}

	public NormalViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}


	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		// TODO Auto-generated method stub

		super.setCurrentItem(item);
	}
}
