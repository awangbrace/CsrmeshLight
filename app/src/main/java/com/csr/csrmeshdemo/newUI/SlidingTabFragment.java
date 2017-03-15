package com.csr.csrmeshdemo.newUI;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.fragment.AbFragment;
import com.ab.view.sliding.AbBottomTabView;
import com.ab.view.slidingmenu.SlidingMenu;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.base.BaseFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Shadow Blade on 2016-12-13.
 */

public class  SlidingTabFragment extends AbFragment {

    private SlidingMenu menu;
    public AbBottomTabView mBottomTabView;
    private List<BaseFragment> mFragments;
    private List<String> tabTexts = new ArrayList<String>();
    private List<Drawable> tabDrawables = null;
    private int currect_page = 0;
    private static SlidingTabFragment mSlidingTabFragment;

   public static SlidingTabFragment getInstance(){
       if(mSlidingTabFragment == null){
           mSlidingTabFragment = new SlidingTabFragment();
       }
      return mSlidingTabFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_main, null);
        showBottomBar(view);
        initListener();
        return view;
    }

    private void showBottomBar(View view) {
        // TODO Auto-generated method stub


        mBottomTabView = (AbBottomTabView) view.findViewById(R.id.mBottomTabView);
        mBottomTabView.getViewPager().setOffscreenPageLimit(1);
        BaseFragment page1 = new ColorModeFragment();
        BaseFragment page3 = new CustomFragment();
        BaseFragment page4 = new MicrophoneFragment();
        BaseFragment page5 = new SmartAlarmFragment();

        mFragments = new ArrayList<BaseFragment>();
        mFragments.add(page1);
        mFragments.add(page3);
        mFragments.add(page4);
        mFragments.add(page5);
        tabTexts.add(getResources().getString(R.string.colour));
        tabTexts.add(getResources().getString(R.string.mode));
        tabTexts.add(getResources().getString(R.string.microphone));
        tabTexts.add(getResources().getString(R.string.alarm));
        //
        mBottomTabView.setTabTextColor(getResources().getColor(
                R.color.bottom_bar_text_normal_color));
        mBottomTabView.setTabSelectColor(getResources().getColor(
                R.color.bottom_bar_text_select_color));
        mBottomTabView.setTabBackgroundResource(R.color.bottom_bar_background_color);
        mBottomTabView.setTabLayoutBackgroundResource(R.color.bottom_bar_background_color);
        mBottomTabView.setSlidingEnabled(false);
        // mBottomTabView.setLayoutParams(new
        // LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        tabDrawables = new ArrayList<Drawable>();
        tabDrawables.add(this.getResources().getDrawable(R.drawable.nav_color_normal));
        tabDrawables.add(this.getResources().getDrawable(R.drawable.nav_color_press));
        tabDrawables.add(this.getResources().getDrawable(R.drawable.nav_type_normal));
        tabDrawables.add(this.getResources().getDrawable(R.drawable.nav_type_press));
        tabDrawables.add(this.getResources().getDrawable(R.drawable.nav_micro_normal));
        tabDrawables.add(this.getResources().getDrawable(R.drawable.nav_micro_press));
        tabDrawables.add(this.getResources().getDrawable(R.drawable.nav_clock_normal));
        tabDrawables.add(this.getResources().getDrawable(R.drawable.nav_clock_press));
        mBottomTabView.setTabCompoundDrawablesBounds(0, 0, 60, 60);
        mBottomTabView.addItemViews(tabTexts,(new LinkedList<Fragment>(mFragments)),tabDrawables);
        mBottomTabView.setTabPadding(2, 10, 2, 2);
    }

    private void initListener(){
        mBottomTabView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int page) {
                currect_page = page;
                for(int i= 0;i<mFragments.size();i++){
                    if(i == page){
                        mFragments.get(i).fragment_select(true);
                    }else{
                        mFragments.get(i).fragment_select(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int page) {


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        for(int i= 0;i<mFragments.size();i++){
            if(i == currect_page){
                mFragments.get(i).fragment_select(true);
            }else{
                mFragments.get(i).fragment_select(false);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SlidingTabFragment","onDestroy------------------------");
    }
}
