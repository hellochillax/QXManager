package com.taichangkeji.tckj.activity;

import android.app.Application;

import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.utils.LogUtils;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EzvizAPI;

/**
 * Created by MAC on 15/12/26.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final boolean init=EZOpenSDK.initLib(this, Config.APP_KEY,null);
        if(!init){
            LogUtils.e("App:"+"EZOpenSDK init is failed.....");
        }
    }
}
