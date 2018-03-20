package com.zidoo.fileexplorer.main;

import android.app.Activity;
import android.app.Application;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.permissions.ZidooBoxPermissions;
import zidoo.model.BoxModel;

public class MyApplication extends Application {
    private Activity mRunningActivity = null;

    public void activityCreate(Activity activity) {
        this.mRunningActivity = activity;
        int model = BoxModel.getBoxModel(this);
        AppConstant.sIsSupportBlurayNavigation = ZidooBoxPermissions.isBluray(this);
        MyLog.d("The box model number is " + model);
    }

    public void activityResume(Activity activity) {
        if (activity.equals(this.mRunningActivity)) {
            AppConstant.sAppRunState = 1;
        }
    }

    public void activityPause(Activity activity) {
        if (activity.equals(this.mRunningActivity)) {
            AppConstant.sAppRunState = 2;
        }
    }

    public void activityDestroy(Activity activity) {
        if (activity.equals(this.mRunningActivity)) {
            AppConstant.sAppRunState = 0;
            this.mRunningActivity = null;
        }
    }
}
