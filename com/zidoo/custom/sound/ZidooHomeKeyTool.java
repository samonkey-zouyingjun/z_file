package com.zidoo.custom.sound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

public class ZidooHomeKeyTool {
    private BroadcastReceiver mBroadcastReceiver = null;
    private Context mContext = null;
    private HomeKeyListener mHomeKeyListener = null;

    public interface HomeKeyListener {
        void homeKey();
    }

    public ZidooHomeKeyTool(Context mContext, HomeKeyListener mHomeKeyListener) {
        this.mContext = mContext;
        this.mHomeKeyListener = mHomeKeyListener;
        initData();
    }

    private void initData() {
        this.mBroadcastReceiver = new BroadcastReceiver() {
            String SYSTEM_HOME_KEY = "homekey";
            String SYSTEM_HOME_KEY_LONG = "recentapps";
            String SYSTEM_REASON = "reason";

            public void onReceive(Context context, Intent intent) {
                try {
                    String action = intent.getAction();
                    System.out.println("bob   home key  action = " + action);
                    if (action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                        String reason = intent.getStringExtra(this.SYSTEM_REASON);
                        if (!TextUtils.equals(reason, this.SYSTEM_HOME_KEY)) {
                            TextUtils.equals(reason, this.SYSTEM_HOME_KEY_LONG);
                        } else if (ZidooHomeKeyTool.this.mHomeKeyListener != null) {
                            ZidooHomeKeyTool.this.mHomeKeyListener.homeKey();
                        }
                    }
                } catch (Exception e) {
                }
            }
        };
        this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void release() {
        try {
            if (this.mBroadcastReceiver != null) {
                this.mContext.unregisterReceiver(this.mBroadcastReceiver);
                this.mBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
