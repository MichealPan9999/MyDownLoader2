package com.example.panzq.mydownloader2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class Util {

	/**
	 * apk自动安装
	 * @param context
	 * @param file
	 */
	public void openFile( Context context ) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile( new File("/sdcard/Download/weixin.apk")); //这里是APK路径
		intent.setDataAndType( uri , "application/vnd.android.package-archive" ) ;
		context.startActivity(intent);
	}
}
