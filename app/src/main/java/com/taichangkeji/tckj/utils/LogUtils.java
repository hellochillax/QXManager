package com.taichangkeji.tckj.utils;

import android.util.Log;

import com.taichangkeji.tckj.config.Config;


/**
 * Created by MAC on 15/12/12.
 *
 *   自定义日志,当在release版本时可以不输出日志
 */
public class LogUtils {
    private static String TAG="LogUtils";
    public static void d(String log){
        if(Config.DEBUG){
            Log.d(TAG,log==null?"log is null.....":log);
        }
    }
    public static void w(String log){
        if(Config.DEBUG){
            Log.w(TAG,log==null?"log is null.....":log);
        }
    }
    public static void e(String log){
        if(Config.DEBUG){
            Log.e(TAG,log==null?"log is null.....":log);
        }
    }
}
