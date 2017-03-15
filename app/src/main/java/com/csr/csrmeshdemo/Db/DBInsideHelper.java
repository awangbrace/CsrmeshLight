package com.csr.csrmeshdemo.Db;

import android.content.Context;

import com.ab.db.orm.AbDBHelper;
import com.csr.csrmeshdemo.entities.AlarmBean;
/**
 * © 2016 amsoft.cn
 * @名称：DBInsideHelper.java 
 * @描述：本地数据库 在data下面
 * @author 姚海军
 * @date：2016-3-12 下午4:12:36
 * @versi
 */
public class DBInsideHelper extends AbDBHelper {
	// 数据库名
	private static final String DBNAME = "smallradar.db";
    
    // 当前数据库的版本
	private static final int DBVERSION = 1;
	// 要初始化的表
	private static final Class<?>[] clazz = { AlarmBean.class};
	public DBInsideHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}



