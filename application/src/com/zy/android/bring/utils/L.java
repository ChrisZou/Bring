package com.zy.android.bring.utils;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Helper class for android Logcat functions
 * @author chuang
 *
 */
@SuppressLint("DefaultLocale")
public class L {
 
	private static String TAG = "zyzy";
	private static boolean DEBUG = true; 
	private static final String CLASS_METHOD_LINE_FORMAT = "%s.%s() Line:%d---------%s";
	
	/**
	 * Log debug method.
	 * @param caller the Class that calls this method.
	 * @param msg
	 */
	public static void l() {
		if(DEBUG) {
			StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];// 从堆栈信息中获取当前被调用的方法信息
			String className = traceElement.getClassName();
			String simpleClassName = className.contains(".")?className.substring(className.lastIndexOf(".")+1):className;
			
			String logText = String.format(CLASS_METHOD_LINE_FORMAT, simpleClassName, traceElement.getMethodName(),
				traceElement.getLineNumber());
			
			Log.d(TAG, logText);
		}
	}
	
	/**
	 * Log debug method.
	 * @param caller the Class that calls this method.
	 * @param msg
	 */
	public static void l(Object msg) {
		if(DEBUG) {
			StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];// 从堆栈信息中获取当前被调用的方法信息
			String className = traceElement.getClassName();
			String simpleClassName = className.contains(".")?className.substring(className.lastIndexOf(".")+1):className;
			
			String logText = String.format(CLASS_METHOD_LINE_FORMAT, simpleClassName, traceElement.getMethodName(),
				traceElement.getLineNumber(), msg.toString());
			
			Log.d(TAG, logText);
		}
	}
	
	public static void trace() {
		StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
		Log.d(TAG, "trace-------------------------------------------------------------------------");
		int len = traceElements.length;
		for (int i=len-1; i>=0; i--) {
			StackTraceElement traceElement = traceElements[i];
			String logText = String.format(CLASS_METHOD_LINE_FORMAT, traceElement.getClassName(), traceElement.getMethodName(),
			traceElement.getLineNumber(), traceElement.getFileName());
			Log.d(TAG, logText);// 打印Log
		}
		Log.d(TAG, "end trace-------------------------------------------------------------------------");
	}
	
}
