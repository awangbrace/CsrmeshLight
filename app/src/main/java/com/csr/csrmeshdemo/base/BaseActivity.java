package com.csr.csrmeshdemo.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.ab.activity.AbActivity;
import com.csr.csrmeshdemo.Interface.CsrStateChangeInterface;
import com.csr.csrmeshdemo.newUI.AssociationActivity;
import com.csr.csrmeshdemo.newUI.SecuritySettingsActivity;

/**
 * Created by yaohj on 2017/3/2.
 */

public class BaseActivity extends AbActivity implements CsrStateChangeInterface.ConnectionStateChange,CsrStateChangeInterface.SecurityCallback {
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CsrManger.getInstance(this).setmConnectionStateChange(this);
        CsrManger.getInstance(this).setmSecurityCallback(this);

    }
    @Override
    public void OnConnected(boolean already_network_setting) {

        if (already_network_setting) {

        } else {
            Intent intent = new Intent();
            intent.setClass(this, SecuritySettingsActivity.class);
            startActivity(intent);
        }
//        ((MainActivity)mcontext).onNavigationItemSelected(POSITION_NETWORK_SETTINGS);
//        ((MainActivity)mcontext). onNavigationItemSelected(POSITION_TAB_MAIN);

    }

    @Override
    public void Security(boolean already_security) {
//        ((MainActivity)mcontext).onNavigationItemSelected(POSITION_TAB_MAIN);
//
//        ((MainActivity)mcontext).onNavigationItemSelected(POSITION_ASSOCIATION);

        if (already_security) {

        } else {
            Intent intent = new Intent();
            intent.setClass(this, AssociationActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Show a modal progress dialogue until hideProgress is called.
     *
     * @param message
     *            The message to display in the dialogue.
     */
    public void showProgress(String message) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(BaseActivity.this);
            mProgress.setMessage(message);
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    CsrManger.getInstance(BaseActivity.this).disconnectBridge();
                }
            });
            mProgress.show();
        }
    }

    /**
     * Hide the progress dialogue.
     */
    public void hideProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress=null;
        }
    }
}