package com.taichangkeji.tckj.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by MAC on 15/12/26.
 */
public class SplashAty extends BaseActivity {

    @Override
    protected void initDatas() {
        File file=new File(Config.cachePath);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    @Override
    protected void initViews() {
        checkNextPage();
    }

    private void checkNextPage() {
        //进行登录检查操作
        EZAccessToken token=UserUtils.getAccessToken(this);
        if(token!=null){
            EZOpenSDK.getInstance().setAccessToken(token.getAccessToken());
            startActivity(new Intent(this, MainActivity.class));
        }else{
            GetAccessToken();
        }
        onBackPressed();
    }

    private void GetAccessToken() {
        new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    HttpsURLConnection conn= (HttpsURLConnection) new URL("").openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    protected int initLayoutRes() {
        return 0;
    }

    @Override
    protected void onExit() {

    }


}
