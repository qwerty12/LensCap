package com.ownzordage.chrx.lenscap;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.app.admin.IDevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.widget.Toast;

import com.rosan.dhizuku.api.Dhizuku;
import com.rosan.dhizuku.api.DhizukuBinderWrapper;
import com.rosan.dhizuku.shared.DhizukuVariables;

import java.lang.reflect.Field;

/**
 * Helper class to activate and deactivate Lens Cap
 */

public class LensCapActivator {

    public static enum Status {
        CAMERA_DISABLED, CAMERA_ENABLED, DEVICE_ADMIN_DISABLED
    }

    public static void toggleLensCap(Context context) {
        String status = context.getResources().getString(R.string.error_no_device_admin);

        if (Dhizuku.init(context) && Dhizuku.isPermissionGranted()) {
            DevicePolicyManager mDPM = binderWrapperDevicePolicyManager(context);

            Status cameraStatus = getStatus(context);
            switch (cameraStatus) {
                case CAMERA_DISABLED:
                    mDPM.setCameraDisabled(DhizukuVariables.COMPONENT_NAME, false);
                    status = context.getResources().getString(R.string.lens_cap_status_off);
                    break;
                case CAMERA_ENABLED:
                    mDPM.setCameraDisabled(DhizukuVariables.COMPONENT_NAME, true);
                    status = context.getResources().getString(R.string.lens_cap_status_on);
                    break;
            }
        }

        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    public static Status getStatus(Context context) {
        if (Dhizuku.init(context) && Dhizuku.isPermissionGranted()) {
            DevicePolicyManager mDPM = binderWrapperDevicePolicyManager(context);

            // If the camera is disabled and the method is told to re-enable it
            if (mDPM.getCameraDisabled(DhizukuVariables.COMPONENT_NAME)) {
                return Status.CAMERA_DISABLED;
            } else {
                return Status.CAMERA_ENABLED;
            }
        }

        return Status.DEVICE_ADMIN_DISABLED;
    }

    // https://github.com/iamr0s/Dhizuku-API/blob/main/demo-binder_wrapper/src/main/java/com/rosan/dhizuku/demo/MainActivity.java#L59
    @SuppressLint("SoonBlockedPrivateApi")
    private static DevicePolicyManager binderWrapperDevicePolicyManager(Context context) {
        try {
            context = context.createPackageContext(DhizukuVariables.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
            DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            Field field = manager.getClass().getDeclaredField("mService");
            field.setAccessible(true);
            IDevicePolicyManager oldInterface = (IDevicePolicyManager) field.get(manager);
            if (oldInterface instanceof DhizukuBinderWrapper) return manager;
            assert oldInterface != null;
            IBinder oldBinder = oldInterface.asBinder();
            IBinder newBinder = Dhizuku.binderWrapper(oldBinder);
            IDevicePolicyManager newInterface = IDevicePolicyManager.Stub.asInterface(newBinder);
            field.set(manager, newInterface);
            return manager;
        } catch (NoSuchFieldException |
                 IllegalAccessException |
                 PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
