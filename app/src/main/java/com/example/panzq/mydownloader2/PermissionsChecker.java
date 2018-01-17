package com.example.panzq.mydownloader2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by panzq on 2018/1/17.
 */

public class PermissionsChecker {
    private Context mContext;
    static final String[] PERMISSIONS = new String[]{
            // Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    public PermissionsChecker(Context context){
        mContext = context.getApplicationContext();
    }

    /**
     * 判断权限
     */
    protected boolean judgePermissions(String...permissions){
        for(String permission:permissions){
            if(deniedPermission(permission)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少权限
     * PackageManager.PERMISSION_GRANTED 授予权限
     * PackageManager.PERMISSION_DENIED 缺少权限
     *
     */
    private boolean deniedPermission(String permission){
        return   ContextCompat.checkSelfPermission(mContext,permission)==  PackageManager.PERMISSION_DENIED;
    }

    /**
     * 申请权限
     * @param activity
     */
    public void applyPermission(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (judgePermissions(PERMISSIONS))
            {
                LogcatUtil.i("applyPermission :apply for Permission");
                ActivityCompat.requestPermissions(activity,PERMISSIONS,1);
            }else{
                LogcatUtil.d("applyPermission:has WRITE_EXTERNAL_STORAGE Permission");
            }
        }else{
            LogcatUtil.d("applyPermission:your version older than android 6.0");
        }
    }
}
