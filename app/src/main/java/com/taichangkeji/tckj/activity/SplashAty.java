package com.taichangkeji.tckj.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

import java.io.File;

/**
 * Created by MAC on 15/12/26.
 */
public class SplashAty extends BaseActivity {

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            checkNextPage();
        }
    };

    @Override
    protected void initDatas() {
        File file=new File(Config.cachePath);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    @Override
    protected void initViews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    mHandler.obtainMessage().sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkNextPage() {
        //进行登录检查操作
        EZAccessToken token=UserUtils.getAccessToken(this);
        if(token!=null){
            EZOpenSDK.getInstance().setAccessToken(token.getAccessToken());
            startActivity(new Intent(this, MainActivity.class));
        }else{
            EZOpenSDK.getInstance().openLoginPage();
        }
        onBackPressed();
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_splash;
    }

    @Override
    protected void onExit() {

    }
}
