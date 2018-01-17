/**  
 * Copyright © 2015ktc. All rights reserved.
 *
 * @Title: LogcatUtil.java
 * @Description: 调试信息工具文件
 * @author: nathan.liao  
 * @date: 2015-6-4 
 * @modifier:
 * @version: V1.0  
 */
package com.example.panzq.mydownloader2;

import android.util.Log;

/**
 * @ClassName: LogcatUtil
 * @Description: 用于程序中调试信息输出的工具类
 * @author: nathan.liao
 * @date: 2015-6-4 
 */
public class LogcatUtil {
	
	private static LogcatUtil mLogcatUtil;
	/**
	 * 是否输出打印信息开关
	 * true：输出打印信息
	 * false：不输出打印信息
	 */
	private final static boolean DEBUG_ENABLE = true;
	/**
	 * 默认输出log信息的Tag标识
	 */
	private final static String DEBUG_TAG = "panzqw";
	
	/**
	 * 以下为采用默认Tag输出Log信息的方法
	 */
	public static void v(String msg) {
		if(DEBUG_ENABLE) {
			Log.v(DEBUG_TAG, msg);
		}
	}
	
	public static void d(String msg) {
		if(DEBUG_ENABLE) {
			Log.d(DEBUG_TAG, msg);
		}
	}
	
	public static void i(String msg) {
		if(DEBUG_ENABLE) {
			Log.i(DEBUG_TAG, msg);
		}
	}
	
	public static void w(String msg) {
		if(DEBUG_ENABLE) {
			Log.e(DEBUG_TAG, msg);
		}
	}
	
	public static void e(String msg) {
		if(DEBUG_ENABLE) {
			Log.e(DEBUG_TAG, msg);
		}
	}
	
	
	
	/**
	 * 以下为采用用户自定义Tag输出Log信息的方法
	 */
	public static void v(String tag, String msg) {
		if(DEBUG_ENABLE) {
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if(DEBUG_ENABLE) {
			Log.d(tag, msg);
		}
	}
	
	public static void i(String tag, String msg) {
		if(DEBUG_ENABLE) {
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag, String msg) {
		if(DEBUG_ENABLE) {
			Log.e(tag, msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if(DEBUG_ENABLE) {
			Log.e(tag, msg);
		}
	}
}
