package com.taichangkeji.tckj.config;

import android.os.Environment;

/**
 * Created by MAC on 15/12/26.
 */
public class Config {
    public static final String APP_KEY="dc7a22a0c1884476abbad754bf88b0b1";
    public static final String APP_SECRET_KEY="c6c60f76f5b03d92b6ed6e882a805f4d";

    public static final boolean DEBUG=true;

    private static final String localhost="http://101.201.29.225:8080";

    public static final String getAccessToken=localhost+"/getInfo";//获取AccessToken
    public static final String getIDsAndLogin=localhost+"/login?";//用户登录
    public static final String getRelations=localhost+"/beitai/getRelations";//获取家属列表
    public static final String getHealthUsers=localhost+"/beitai/getHealthUsers?";//获取家属列表
    public static final String addHealthUser=localhost+"/beitai/addHealthUser?";//添加家属成员
    public static final String delHealthUser=localhost+"/beitai/delHealthUser?";//删除家属成员
    public static final String getUserIconPre=localhost+"/uploads/";


    public static final String updatePesssure=localhost+"/beitai/health/savePressure?";//血压
    public static final String updateWeight=localhost+"/beitai/health/saveWeight?";//体重
    public static final String updateWenDu=localhost+"/beitai/health/saveTemperature?";//体温
    public static final String updateZhifang=localhost+"/beitai/health/saveFat?";//脂肪
    public static final String updateXuetang=localhost+"/beitai/health/saveBlood?";//血糖


    public static final String cachePath= Environment.getExternalStorageDirectory()+"/QiXing/caches";
    public static final String iconCache=cachePath+"/icon.png";






    //验证手机号的正则
    public static final String phoneRegular="^((13[0-9])|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$";

}
