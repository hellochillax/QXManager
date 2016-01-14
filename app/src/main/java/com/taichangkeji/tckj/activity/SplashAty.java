package com.taichangkeji.tckj.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

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
    }

    @Override
    protected void initViews() {
        //初始化蓝牙模块
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
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
