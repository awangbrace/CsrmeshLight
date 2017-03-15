package com.csr.csrmeshdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csr.csrmeshdemo.R;

public class Gridadapter extends BaseAdapter {
    private LayoutInflater inflater;
    String[] name;
    private Context mContext;
    private int select_item  = -1;
//    int[] iconarray;
 
    public Gridadapter(Context context, String[] name, int[] iconarray) {
        this.inflater = LayoutInflater.from(context);
        this.name = name;
        mContext = context;
//        this.iconarray = iconarray;
    }
 
    @Override
    public int getCount() {
        return name.length;
    }
 
    @Override
    public Object getItem(int position) {
        return position;
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            holder=new ViewHolder();
            convertView=this.inflater.inflate(R.layout.grid_item, null);
            holder.tv=(TextView) convertView.findViewById(R.id.gridview_text);
        }
        else {
           holder=(ViewHolder) convertView.getTag();
        }
//        holder.iv.setImageResource(iconarray[position]);
        holder.tv.setText(name[position]);
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();


        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) holder.tv.getLayoutParams();
        //???????????
        params.height=width/3;//???????????
        holder.tv.setLayoutParams(params);//???????????????
        if(select_item == position){
            convertView.setBackgroundResource(R.drawable.bottom_bg_h);
            holder.tv.setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
            convertView.setBackgroundResource(R.drawable.portal_navigation_1bottom);
            holder.tv.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        convertView.setTag(holder);
        return convertView;
    }
    private class ViewHolder{
        TextView tv;
    }

   public  void updateSelect(int position){
       select_item  = position;
       notifyDataSetChanged();

   }
 
}