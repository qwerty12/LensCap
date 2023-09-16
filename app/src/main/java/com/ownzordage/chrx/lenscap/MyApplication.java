package com.ownzordage.chrx.lenscap;

import android.app.Activity;
import android.app.Application;

/**
 * Extend Application class to enable IAB
 */

public class MyApplication extends Application {

    /**
     * Returns an instance of {@link MyApplication} attached to the passed activity.
     */
    public static MyApplication get(Activity activity) {
        return (MyApplication) activity.getApplication();
    }

}
