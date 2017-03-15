package com.csr.csrmeshdemo;

import android.app.Application;

/**
 * Created by Shadow Blade on 2016-12-19.
 */

public class CsrMeshApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
//        CsrmeshManger.getInstance(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
