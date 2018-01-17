# MyDownLoader2
panzq
1.Tomcat服务搭建
  将要下载的文件放入到Tomcat安装目录下 C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\apks
  对应的地址为： String url = "http://192.168.12.80:8080/apks/com.tencent.mm_561.apk";
2.动态授权问题
  android6.0及以上版本部分权限增加了动态权限机制，需要用户点击授权。
  比如：Manifest.permission.WRITE_EXTERNAL_STORAGE
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
    
