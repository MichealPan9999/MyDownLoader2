package com.example.panzq.mydownloader2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 *由于android6.0以上版本可能出现某些权限需要动态授权的情况
 * 所以先要执行动态授权方法
 */
public class MainActivity extends Activity {

    private boolean isRequireCheck; // 是否需要系统权限检测
    private PermissionsChecker permissionsChecker;//检查并申请权限
    private static final int PERMISSION_REQUEST_CODE = 0;        // 系统权限返回码
    private static final String PACKAGE_URL_SCHEME = "package:";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionsChecker = new PermissionsChecker(this);
        permissionsChecker.applyPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
        } else {
            isRequireCheck = false;
            showPermissionDialog();
        }
        LogcatUtil.i("onRequestPermissionsResult isRequireCheck==== "+isRequireCheck);
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted( int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 提示对话框
     */
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("帮助");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-打开所需权限。");
        // 拒绝, 退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
//                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }
}
