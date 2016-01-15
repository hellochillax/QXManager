package com.taichangkeji.tckj.activity;

import android.widget.ImageView;

import com.taichangkeji.tckj.R;

import butterknife.Bind;
import butterknife.OnFocusChange;

/**
 * Created by MAC on 15/12/27.
 */
public class SecurityAty extends BaseActivity {

    @Bind(R.id.out)
    ImageView mOut;
    @Bind(R.id.home)
    ImageView mHome;
    @Bind(R.id.sleep)
    ImageView mSleep;
    @Bind(R.id.infrared_l)
    ImageView mInfraredL;
    @Bind(R.id.infrared_r)
    ImageView mInfraredR;
    @Bind(R.id.door_l)
    ImageView mDoorL;
    @Bind(R.id.door_r)
    ImageView mDoorR;
    @Bind(R.id.smoke_l)
    ImageView mSmokeL;
    @Bind(R.id.smoke_r)
    ImageView mSmokeR;

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        mHome.requestFocus();
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_security;
    }

    @Override
    protected void onExit() {

    }

    @OnFocusChange(R.id.out) void change_out(boolean b){
        if(b){
            mOut.setImageResource(R.mipmap.security_out_2);
        }else {
            mOut.setImageResource(R.mipmap.security_out);
        }
    }
    @OnFocusChange(R.id.home) void change_home(boolean b){
        if(b){
            mHome.setImageResource(R.mipmap.security_home_2);
        }else {
            mHome.setImageResource(R.mipmap.security_home);
        }
    }
    @OnFocusChange(R.id.sleep) void change_sleep(boolean b){
        if(b){
            mSleep.setImageResource(R.mipmap.security_sleep_2);
        }else {
            mSleep.setImageResource(R.mipmap.security_sleep);
        }
    }
    @OnFocusChange(R.id.infrared_r) void change_infrared(boolean b){
        if(b){
            mInfraredL.setImageResource(R.mipmap.security_infrared_2);
            mInfraredR.setImageResource(R.mipmap.security_switch_2);
        }else {
            mInfraredL.setImageResource(R.mipmap.security_infrared);
            mInfraredR.setImageResource(R.mipmap.security_switch);
        }
    }
    @OnFocusChange(R.id.door_r) void change_door(boolean b){
        if(b){
            mDoorL.setImageResource(R.mipmap.security_door_2);
            mDoorR.setImageResource(R.mipmap.security_switch_2);
        }else {
            mDoorL.setImageResource(R.mipmap.security_door);
            mDoorR.setImageResource(R.mipmap.security_switch);
        }
    }
    @OnFocusChange(R.id.smoke_r) void change_smoke(boolean b){
        if(b){
            mSmokeL.setImageResource(R.mipmap.security_smoke_2);
            mSmokeR.setImageResource(R.mipmap.security_switch_2);
        }else {
            mSmokeL.setImageResource(R.mipmap.security_smoke);
            mSmokeR.setImageResource(R.mipmap.security_switch);
        }
    }

}
