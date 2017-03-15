
/******************************************************************************
 Copyright Cambridge Silicon Radio Limited 2014 - 2015.
 ******************************************************************************/

package com.csr.csrmeshdemo.newUI;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.csr.csrmeshdemo.util.DeviceController;
import com.csr.csrmeshdemo.R;
import com.csr.csrmeshdemo.base.CsrManger;

/**
 * Fragment to allow user to set network phrase and enable/disable authorisation.
 *
 */
public class SecuritySettingsActivity extends AbActivity {

    private DeviceController mController;
    private static String phrase;
    private static boolean auth;
    private static int ttl;
    private EditText passPhraseView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.security_settings_fragment);
        mController = CsrManger.getInstance(this);
        initView();
        initTitleBar();
    }

    private void initTitleBar(){
        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(getString(R.string.setting_password));
        mAbTitleBar.setLogo(R.drawable.icon_back);
        mAbTitleBar.setTitleBarBackground(R.color.title_bar_background_color);
        mAbTitleBar.setPadding((int)getResources().getDimension(R.dimen.titlebar_logo_padding),16,0,0);
//        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//        mAbTitleBar.setLogoLine(R.drawable.line);
//        mAbTitleBar.getLogoView().setBackgroundResource(R.drawable.button_selector_menu);
        View rightViewMore = mInflater.inflate(R.layout.next, null);
        TextView next = (TextView) rightViewMore.findViewById(R.id.next);
        mAbTitleBar.clearRightView();
        mAbTitleBar.addRightView(rightViewMore);
        mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newTTL = -1;
                try {
                    newTTL = ttl;
                } catch (Exception e) {
                }

                if (newTTL < 0 || newTTL > 0xFF) {
                    Toast.makeText(SecuritySettingsActivity.this, "Time to live (TTL) value can't be lower than 0 or higher than 127", Toast.LENGTH_SHORT)
                            .show();
                } else if (passPhraseView.getText().toString().trim().length() > 0) {
                    // Hide soft keyboard.
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passPhraseView.getWindowToken(), 0);
                    // Tell MainActivity about security settings. This will also switch to another Fragment.
                    mController.setSecurity(passPhraseView.getText().toString(), auth);
                    mController.setTTLForMCP(newTTL);
                } else {
                    Toast.makeText(SecuritySettingsActivity.this, getResources().getString(R.string.key_required), Toast.LENGTH_SHORT)
                            .show();
                }
                finish();
            }
        });

        mAbTitleBar.getLogoView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    private void initView() {
        phrase = mController.getNetworkKeyPhrase();
        auth = mController.isAuthRequired();
        ttl = mController.getTTLForMCP();

//        Button okBtn = (Button) findViewById(R.id.network_assocoiation_ok);
//        final CheckBox deviceAuthenticated = (CheckBox) findViewById(R.id.checkbox);
//        deviceAuthenticated.setChecked(auth);
//        final EditText ttl = (EditText) findViewById(R.id.ttl);
            passPhraseView = (EditText) findViewById(R.id.network_pass);
//        final TextView bridgeAddress = (TextView) findViewById(R.id.textBridgeAddress);
//        bridgeAddress.setText(mController.getBridgeAddress());
//        ttl.setText("" + mController.getTTLForMCP());
        if (phrase != null) {
            passPhraseView.setText(phrase);
        }
//        okBtn.setClickable(true);


    }}