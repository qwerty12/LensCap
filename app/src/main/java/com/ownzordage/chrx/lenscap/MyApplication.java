package com.ownzordage.chrx.lenscap;

import android.app.Activity;
import android.app.Application;

import com.rosan.dhizuku.api.Dhizuku;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

/**
 * Extend Application class to enable IAB
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HiddenApiBypass.addHiddenApiExemptions("Landroid/app/admin/DevicePolicyManager;");
        Dhizuku.init();
    }

    /**
     * Returns an instance of {@link MyApplication} attached to the passed activity.
     */
    public static MyApplication get(Activity activity) {
        return (MyApplication) activity.getApplication();
    }

}
