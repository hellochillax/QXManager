package com.taichangkeji.tckj.activity;

import android.content.Intent;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

/**
 * Created by MAC on 15/12/26.
 */
public class SplashAty extends BaseActivity {

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
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
