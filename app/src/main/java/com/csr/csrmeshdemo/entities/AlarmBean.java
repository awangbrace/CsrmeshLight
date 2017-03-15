/******************************************************************************
 * File: Alarm.java
 * Author: 姚海军
 * Create Date : 2015年1月6日
 * JDK version used: <JDK1.6> 
 * Version : V1.0
 * Description : 
 * 
 * 
 * 
 * History :
 * 1. wuyj add for the first release , 2015年1月6日 
 *
 * 
 * Copyright (C), 2012-2013, Xi'an TCL Software Development Co.,Ltd
 * All rights reserved
 ******************************************************************************/
package com.csr.csrmeshdemo.entities;

import android.text.TextUtils;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;
import com.csr.csrmeshdemo.util.Constant;
import com.csr.csrmeshdemo.util.L;

import java.io.Serializable;
import java.util.UUID;

/**
 * @description
 * @author 姚海军
 * @date 2015年1月6日 上午10:17:17
 */
/**
 * @author 姚海军
 *
 */
@Table(name = "Alarm")
public class AlarmBean implements Serializable,Cloneable{
	@Id
	@Column(name = "_id")
	private int _id;
	/**
	 * 
	 * 闹钟关闭
	 */

	public static final int ARLARM_OFF = 0;
	/**
	 * 
	 * 闹钟打开
	 */

	public static final int ARLARM_ON = 1;
	/**
	 * 
	 * 贪睡关闭
	 */

	public static final int SLEEP_OFF = 0;
	/**
	 * 
	 * 贪睡打开
	 */

	public static final int SLEEP_ON = 1;

	/**
	 * 
	 * 闹钟的有效次数为一次
	 */

	public static final int REPEAT_MODE_ONCE = 0; // once;

	/**
	 * 闹钟周期为用户自定义
	 */

	public static final int REPEAT_MODE_CUSTOM = 1; // custom

	public static final int WEEKDAY_MONDAY = 1 << 6;// 1000000;
	public static final int WEEKDAY_TUESDAY = 1 << 5;// 0100000;
	public static final int WEEKDAY_WEDNESDAY = 1 << 4;// 0010000;
	public static final int WEEKDAY_THURSDAY = 1 << 3;// 0001000;
	public static final int WEEKDAY_FRIDAY = 1 << 2;// 0000100;
	public static final int WEEKDAY_SATURDAY = 1 << 1;// 0000010;
	public static final int WEEKDAY_SUNDAY = 1 << 0;// 0000001;

	public static final int[] WEEKDAY_ARRAY = { WEEKDAY_MONDAY,
			WEEKDAY_TUESDAY, WEEKDAY_WEDNESDAY, WEEKDAY_THURSDAY,
			WEEKDAY_FRIDAY, WEEKDAY_SATURDAY, WEEKDAY_SUNDAY };

	/**
	 * 不运行当前闹钟
	 */
	public static final int ALARM_CONFIRM_CANCLE = 0;
	/**
	 * 运行当前闹钟
	 */
	public static final int ALARM_CONFIRM_OK = 1;

	/**
	 * 闹钟实例的编号
	 */
	@Column(name = "timer_id", type = "INTEGER")
	private int timer_id = -1;
	/**
	 * 闹钟的作用的时间
	 */
	@Column(name = "time", length = 256)
	private String time = null;
	/**
	 * 闹钟的停止作用的时间
	 */
	@Column(name = "stop_time", length = 256)
	private String stop_time = null;
	// private String queue_id = ALARM_QUEUE_DEFAULT;
	@Column(name = "sleep", type = "INTEGER")
	private int sleep = SLEEP_ON;
	/**
	 * 闹钟的激活状态
	 */
	@Column(name = "active", type = "INTEGER")
	private int active = ARLARM_OFF;// 整型，1激活，0 停用
	/**
	 * 闹钟的响铃时长
	 */
	@Column(name = "duration", type = "INTEGER")
	private int duration = -1;
	/**
	 * 闹钟的重复周期
	 */
	@Column(name = "weekday", length = 7)
	private String weekday = "0000000";
	private int repeat = 0;
	/**
	 * 闹钟定时的时间
	 */
	@Column(name = "hour", type = "INTEGER")
	private int hour = 0;
	@Column(name = "minute", type = "INTEGER")
	private int minute = 0;
	@Column(name = "name", length = 256)
	private String name = "defual";

	@Column(name = "stop_hour", type = "INTEGER")
	private int stop_hour = 0;
	@Column(name = "stop_minute", type = "INTEGER")
	private int stop_minute = 0;

	/**
	 * 主题音乐名
	 */
	// private String queueName = "";

	public AlarmBean() {
		setTimerId(createRandom());
		L.e("新创建的闹钟ID 为 " + timer_id);
		setActive(ARLARM_ON);
	}

	/**
	 * @return the _id
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * @param _id
	 *            the _id to set
	 */
	public void set_id(int _id) {
		this._id = _id;
	}

	public int getTimerId() {
		return timer_id;
	}

	public void setTimerId(int timer_id) {
		this.timer_id = timer_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStopTime() {
		return stop_time;
	}

	public void setStopTime(String stop_time) {
		this.stop_time = stop_time;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		
		
		
		this.weekday = weekday;
	}

	public int createRandom() {
		return (int) ((Math.random() * 255));
	}

	public String createGUID() {

		return UUID.randomUUID().toString();
	}

	/**
	 * @return the repeat
	 */
	public int getRepeat() {
		return repeat;
	}

	/**
	 * @param repeat
	 *            the repeat to set
	 */
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	/**
	 * 序列化重复模式
	 * 
	 * @Title: repeatToWeekday
	 * 
	 * @Description: TODO
	 * 
	 * @param repeat
	 *            是否重复
	 * @param weekdayValue
	 *            重复周期
	 * @return 二进制形式重复周期
	 * 
	 * @return String 返回类型
	 */
	public String repeatToWeekday(int repeat, int weekdayValue) {
		String weekday = null;
		switch (repeat) {
		case REPEAT_MODE_ONCE:
			weekday = "0000000";
			break;
		case REPEAT_MODE_CUSTOM:
			weekday = repeatCostomToWeekday(weekdayValue);
			break;
		default:
			break;
		}
		return weekday;
	}

	/**
	 * 序列化用户自定重复模式
	 * 
	 * @Title: repeatCostomToWeekday
	 * 
	 * @Description: TODO
	 * 
	 * @param value
	 *            用户选择的重复周期
	 * @return 二进制形式用户选择重复周期
	 * 
	 * @return String 返回类型
	 */
	private String repeatCostomToWeekday(int value) {
		String weekday = new String();
		String temp = Integer.toBinaryString(value);
		;
		if (temp == null) {
			return null;
		}
		for (int i = 0; i < Constant.TIMER_SETTING_WEEKDAY_FORMAT_NUM
				- temp.length(); i++) {
			weekday = weekday + "0";
		}
		weekday = weekday + temp;
		return weekday;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the hour
	 */
	public int getHour() {
		String[] timeArrary;
		if (!TextUtils.isEmpty(time)) {
			timeArrary = time.split(":");
			hour = Integer.valueOf(timeArrary[0]);
		}
		return hour;
	}

	/**
	 * @return the hour
	 */
	public int getStopHour() {
		String[] timeArrary;
		if (!TextUtils.isEmpty(stop_time)) {
			timeArrary = stop_time.split(":");
			stop_hour = Integer.valueOf(timeArrary[0]);
		}
		return stop_hour;
	}

	/**
	 * @return the minute
	 */
	public int getMinute() {
		String[] timeArrary;
		if (!TextUtils.isEmpty(time)) {
			timeArrary = time.split(":");
			minute = Integer.valueOf(timeArrary[1]);
		}
		return minute;
	}

	/**
	 * @return the minute
	 */
	public int getStopMinute() {
		String[] timeArrary;
		if (!TextUtils.isEmpty(stop_time)) {
			timeArrary = stop_time.split(":");
			stop_minute = Integer.valueOf(timeArrary[1]);
		}
		return stop_minute;
	}

	/**
	 * @return the sleep
	 */
	public int getSleep() {
		return sleep;
	}

	/**
	 * @param sleep
	 *            the sleep to set
	 */
	public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		AlarmBean alarmBean = null;  
        try  
        {  
        	alarmBean = (AlarmBean) super.clone();  
        } catch (CloneNotSupportedException e){  
            e.printStackTrace();  
        }  
        return alarmBean; 
	}

}
