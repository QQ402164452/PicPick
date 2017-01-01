package util;

import android.Manifest;

/**
 * Created by Jason on 2016/12/25.
 */

public class PermissionCodes {
    public final static int PERMISSIONS_REQUEST_STORAGE=10;
    public final static int PERMISSIONS_REQUEST_LOCATION=11;
    public final static int PERMISSIONS_REQUEST_PHONE=12;
    public final static int PERMISSIONS_REQUEST_ALL=30;

    public final static String[] perArr={Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE};

}
