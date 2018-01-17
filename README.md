# MyDownLoader2
panzq
1.Tomcat服务搭建
--------
  将要下载的文件放入到Tomcat安装目录下 C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\apks
  对应的地址为： String url = "http://192.168.12.80:8080/apks/com.tencent.mm_561.apk";
2.动态授权问题
--------
  android6.0及以上版本部分权限增加了动态权限机制，需要用户点击授权。
  比如：Manifest.permission.WRITE_EXTERNAL_STORAGE
  代码
  -------
  public PermissionsChecker(Context context){
        mContext = context.getApplicationContext();
    }
    /**
     * 判断权限：所有指定的所有权限，只要有一个没有授权，就提示用户授权。
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
    /**
    *请求权限后会申请
    **/
    
    
    实现下载
    ------------
    /**
     *设置下载请求参数
     * DownloadManager.Request
     */
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
