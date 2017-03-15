/**   
 * @Title:DbManger.java
 * @Package com.huawei.smallRadar.Sdk.Db
 * @Description: 
 * @author 姚海军  
 * @date 2016年3月14日上午9:29:04
 * @version V1.0   
 * History :
 *  1. Yaohaijun add for the first release ,2016年3月14日  
 *
 * 
 * Copyright (C), Tonly electronics Holdincs Limited
 * All rights reserved
 ******************************************************************************/
package com.csr.csrmeshdemo.Db;

import android.content.Context;

import com.ab.db.storage.AbSqliteStorage;
import com.csr.csrmeshdemo.Db.Dao.AlarmInsideDao;
import com.csr.csrmeshdemo.entities.AlarmBean;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 姚海军
 *
 */
public class DbManger {
	// 定义数据库操作实现类
	private AlarmInsideDao mAlarmInsideDao = null;
	// 数据库操作类
	private AbSqliteStorage mAbSqliteStorage = null;
	private static DbManger mDbManger;
	private int totalCount = 0;
	private boolean del_ok = false;
	List<AlarmBean> deviceInfoBeans = new LinkedList<AlarmBean>();

	/**
	 * 
	 */
	private DbManger(Context context) {
		// TODO Auto-generated constructor stub
		mAlarmInsideDao = new AlarmInsideDao(context);
		// 初始化AbSqliteStorage
		mAbSqliteStorage = AbSqliteStorage.getInstance(context);
	}

	/**
	 * 
	 */
	public static DbManger getInstance(Context context) {
		synchronized (DbManger.class) {
			if (mDbManger == null)
				mDbManger = new DbManger(context);
		}
		return mDbManger;
		// TODO Auto-generated method stub

	}

	public void saveDbDeviceInfoData(AlarmBean alarmBean) {
		synchronized (DbManger.class) {
			if(AlarmExist(Integer.toString(alarmBean.getTimerId()))){
				updateDbDeviceInfoData(alarmBean);
				return;
			}
			mAlarmInsideDao.startWritableDatabase(false);
			// (2)执行查询
			mAlarmInsideDao.insert(alarmBean);
			// (3)关闭数据库
			mAlarmInsideDao.closeDatabase();
		}
	}

	/**
	 * 
	 * 描述：查询数据
	 * 
	 * @throws
	 */
	public List<AlarmBean> queryDbDeviceInfoData() {
		// (1)获取数据库
			synchronized (DbManger.class) {
			mAlarmInsideDao.startReadableDatabase();
			// (2)执行查询
			List<AlarmBean> deviceInfoBeans = mAlarmInsideDao.queryList();
			// (3)关闭数据库
			mAlarmInsideDao.closeDatabase();
			return deviceInfoBeans;
		}
	}

	/**
	 * 
	 * 描述：查询数据
	 * 
	 * @throws
	 */
	public AlarmBean queryData(String TimerId) {
		synchronized (DbManger.class) {
			// (1)获取数据库
			mAlarmInsideDao.startReadableDatabase();
			// (2)执行查询
			List<AlarmBean> userListNew = new LinkedList<AlarmBean>();
			userListNew = mAlarmInsideDao.queryList(null, "timer_id=?",
					new String[] { TimerId }, null, null, null, null);
			// (3)关闭数据库
			mAlarmInsideDao.closeDatabase();
//			if (userListNew.size() == 0)
			return userListNew.get(0);

		}
	}

	/**
	 *
	 * 描述：查询数据
	 *
	 * @throws
	 */
	public boolean AlarmExist(String TimerId) {
		synchronized (DbManger.class) {
			// (1)获取数据库
			mAlarmInsideDao.startReadableDatabase();
			// (2)执行查询
			List<AlarmBean> userListNew = new LinkedList<AlarmBean>();
			userListNew = mAlarmInsideDao.queryList(null, "timer_id=?",
					new String[] { TimerId }, null, null, null, null);
			// (3)关闭数据库
			mAlarmInsideDao.closeDatabase();
			if (userListNew.size() == 0)
			return false;
			else
			return true;

		}
	}

	/**
	 * 
	 * 描述：查询数量
	 * 
	 * @throws
	 */
	public int queryDbDeviceInfoDataCount() {
		synchronized (DbManger.class) {
			// (1)获取数据库
			mAlarmInsideDao.startReadableDatabase();
			// (2)执行查询
			int totalCount = mAlarmInsideDao.queryCount(null, null);
			// (3)关闭数据库
			mAlarmInsideDao.closeDatabase();
			return totalCount;
			// 查询数据
		}
	}

	/**
	 * 更新数据 描述：TODO
	 * 
	 * @param u
	 */
	public void updateDbDeviceInfoData(AlarmBean u) {
		// (1)获取数据库
		synchronized (DbManger.class) {
			mAlarmInsideDao.startWritableDatabase(false);
			mAlarmInsideDao.update(u);
			mAlarmInsideDao.closeDatabase();
		}
	}

	/**
	 * 
	 * 描述：根据ID查询数据
	 * 
	 * @param id
	 * @return
	 */
	public AlarmBean queryDbDeviceInfoDataById(int id) {
		synchronized (DbManger.class) {
		// (1)获取数据库
			mAlarmInsideDao.startReadableDatabase();
			AlarmBean u = (AlarmBean) mAlarmInsideDao.queryOne(id);
			mAlarmInsideDao.closeDatabase();
		return u;
		}
	}

	/**
	 * 
	 * 描述：删除数据
	 * 
	 * @param id
	 */
	public void delDbDeviceInfoData(int id) {
		synchronized (DbManger.class) {
			// (1)获取数据库
			mAlarmInsideDao.startWritableDatabase(false);
			// (2)执行查询
			mAlarmInsideDao.delete(id);
			// (3)关闭数据库
			mAlarmInsideDao.closeDatabase();
	
			queryDbDeviceInfoData();
		}
	}
}
