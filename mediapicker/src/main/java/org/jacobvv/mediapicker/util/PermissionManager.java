package org.jacobvv.mediapicker.util;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jacob
 * @date 17-11-23
 */

public final class PermissionManager {

    public static final int STATE_GRANTED = 0;
    public static final int STATE_REQUEST = 1;

    public static final int PERMISSION_REQUEST_CODE = 2000;

    public static int checkPermission(ContextWrap context, String[] permissions) {
        List<String> permissionsNeedRequest = new ArrayList<>();
        int checkResult = STATE_GRANTED;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context.getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeedRequest.add(permission);
                checkResult = STATE_REQUEST;
            }
        }
        if (checkResult == STATE_REQUEST) {
            requestPermission(context,
                    permissionsNeedRequest.toArray(new String[permissionsNeedRequest.size()]));
        }
        return checkResult;
    }

    private static void requestPermission(ContextWrap context, String[] permissions) {
        if (context.getFragment() != null) {
            context.getFragment().requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(context.getActivity(), permissions,
                    PERMISSION_REQUEST_CODE);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                  int[] grantResults, PermissionRequestCallback callback) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            List<String> permissionsDenied = new ArrayList<>();
            for (int i = 0, len = permissions.length; i < len; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permissionsDenied.add(permissions[i]);
                }
            }
            if (permissionsDenied.isEmpty()) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied(permissionsDenied);
            }
        }
    }

    public interface PermissionRequestCallback {
        void onPermissionGranted();

        void onPermissionDenied(List<String> permissions);
    }

}
