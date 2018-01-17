package com.example.panzq.mydownloader2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

/**
 *由于android6.0以上版本可能出现某些权限需要动态授权的情况
 * 所以先要执行动态授权方法
 */
public class MainActivity extends Activity implements View.OnClickListener{
    /**
     * 用于用户授权的
     */
    private boolean isRequireCheck; // 是否需要系统权限检测
    private PermissionsChecker permissionsChecker;//检查并申请权限
    private static final int PERMISSION_REQUEST_CODE = 0;        // 系统权限返回码
    private static final String PACKAGE_URL_SCHEME = "package:";
    /**
     *用于下载的
     */
    //String url = "http://shouji.360tpcdn.com/150527/c90d7a6a8cded5b5da95ae1ee6382875/com.tencent.mm_561.apk";
    String url = "http://192.168.12.80:8080/apks/com.tencent.mm_561.apk";
    private long mReference = 0;
    private DownloadManager downloadManager;
    private Context mContext;
    private DownloadManager.Request request= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionsChecker = new PermissionsChecker(this);
        permissionsChecker.applyPermission(this);
        findViewById(R.id.start_bt).setOnClickListener(this);
        findViewById(R.id.cancle_bt).setOnClickListener(this);
        findViewById(R.id.look_bt).setOnClickListener(this);
        request = new DownloadManager.Request(Uri.parse(url));
        downloadRequest();
        //注册广播接收器
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void downloadRequest() {

        //下载网络需求  手机数据流量、wifi
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //设置是否允许漫游网络 建立请求 默认true
        request.setAllowedOverRoaming(true);
        //设置通知类型
        setNotification(request);
        //设置下载路径
        setDownloadFilePath(request);
        /*在默认的情况下，通过Download Manager下载的文件是不能被Media Scanner扫描到的 。
        进而这些下载的文件（音乐、视频等）就不会在Gallery 和  Music Player这样的应用中看到。
        为了让下载的音乐文件可以被其他应用扫描到，我们需要调用Request对象的
         */
        request.allowScanningByMediaScanner();
        /*如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
        我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true。*/
        request.setVisibleInDownloadsUi(true);
        //设置请求的Mime
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));
    }

    private void startDownload(DownloadManager.Request request) {
        //开始下载
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mReference = downloadManager.enqueue(request);
        /*
        下载管理器中有很多下载项，怎么知道一个资源已经下载过，避免重复下载呢？
        我的项目中的需求就是apk更新下载，用户点击更新确定按钮，第一次是直接下载，
        后面如果用户连续点击更新确定按钮，就不要重复下载了。
        可以看出来查询和操作数据库查询一样的
         */
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mReference);
        Cursor cursor = downloadManager.query(query);
        if (!cursor.moveToFirst()) {// 没有记录

        } else {
            //有记录
        }
    }

    /**
     * 设置下载文件存储目录
     */
    void setDownloadFilePath(DownloadManager.Request request) {
        /**
         * 方法1:
         * 目录: Android -> data -> com.app -> files -> Download -> 微信.apk
         * 这个文件是你的应用所专用的,软件卸载后，下载的文件将随着卸载全部被删除
         */
        //request.setDestinationInExternalFilesDir( this , Environment.DIRECTORY_DOWNLOADS ,  "微信.apk" );
        /**
         * 方法2:
         * 下载的文件存放地址  SD卡 download文件夹，pp.jpg
         * 软件卸载后，下载的文件会保留
         */
        //在SD卡上创建一个文件夹
        //request.setDestinationInExternalPublicDir(  "/mydownfile/"  , "weixin.apk" ) ;
        /**
         * 方法3:
         * 如果下载的文件希望被其他的应用共享
         * 特别是那些你下载下来希望被Media Scanner扫描到的文件（比如音乐文件）
         */
        //request.setDestinationInExternalPublicDir( Environment.DIRECTORY_MUSIC,  "笨小孩.mp3" );
        /**
         * 方法4
         * 文件将存放在外部存储的确实download文件内，如果无此文件夹，创建之，如果有，下面将返回false。
         * 系统有个下载文件夹，比如小米手机系统下载文件夹  SD卡--> Download文件夹
         */
        //创建目录
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        //设置文件存放路径
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "weixin.apk");
    }
    /**
     * 广播接受器, 下载完成监听器
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //下载完成了
                //获取当前完成任务的ID
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Toast.makeText(MainActivity.this, "下载完成了", Toast.LENGTH_SHORT).show();
                //自动安装应用
                Util util = new Util();
                util.openFile(context);
            }
            if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                //广播被点击了
                Toast.makeText(MainActivity.this, "广播被点击了", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_bt:
                //TODO implement
                startDownload(request);
                break;
            case R.id.cancle_bt:
                //取消下载， 如果一个下载被取消了，所有相关联的文件，部分下载的文件和完全下载的文件都会被删除。
                downloadManager.remove(mReference);
                break;
            case R.id.look_bt:
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(mReference);
                Cursor cursor = downloadManager.query(query);
                if (cursor == null) {
                    Toast.makeText(MainActivity.this, "Download not found!", Toast.LENGTH_LONG).show();
                } else {  //以下是从游标中进行信息提取
                    cursor.moveToFirst();
                    String msg = statusMessage(cursor);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**
     * 设置状态栏中显示Notification
     */
    void setNotification(DownloadManager.Request request) {
        //设置Notification的标题
        request.setTitle("微信下载");
        //设置描述
        request.setDescription("5.3.6");
        //request.setNotificationVisibility( Request.VISIBILITY_VISIBLE ) ;
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setNotificationVisibility( Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION ) ;
        //request.setNotificationVisibility( Request.VISIBILITY_HIDDEN ) ;
    }

    /**
     * 查询状态
     *
     * @param c
     * @return
     */
    private String statusMessage(Cursor c) {
        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                return "Download failed";
            case DownloadManager.STATUS_PAUSED:
                return "Download paused";
            case DownloadManager.STATUS_PENDING:
                return "Download pending";
            case DownloadManager.STATUS_RUNNING:
                return "Download in progress!";
            case DownloadManager.STATUS_SUCCESSFUL:
                return "Download finished";
            default:
                return "Unknown Information";
        }
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
