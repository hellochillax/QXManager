package com.taichangkeji.tckj.activity;

import android.content.Intent;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.openapi.EZOpenSDK;

/**
 * Created by MAC on 15/12/26.
 */
public class SplashAty extends BaseActivity {

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        if(UserUtils.getAccessToken(this)!=null){
            startActivity(new Intent(this, MainActivity.class));
            onBackPressed();
        }else{
            EZOpenSDK.getInstance().openLoginPage();
        }
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_splash;
    }

    @Override
    protected void onExit() {

    }
}
