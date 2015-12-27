package com.taichangkeji.tckj.activity;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.taichangkeji.tckj.R;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class MainActivity extends BaseActivity{

    final int OFFSET_FOCUS_SIZE_CHANGE = 150;

    @Bind(R.id.main_security)
    ImageView mSecurity;
    @Bind(R.id.main_healthy)
    ImageView mHealthy;
    @Bind(R.id.main_video)
    ImageView mVideo;
    @Bind(R.id.main_community)
    ImageView mCommunity;
    @Bind(R.id.main_shop)
    ImageView mShop;
    @Bind(R.id.main_service)
    ImageView mService;

    @OnFocusChange({R.id.main_security,R.id.main_healthy,R.id.main_video,R.id.main_community,R.id.main_shop,R.id.main_service})
    void on_focus_change(View v,boolean focus){
        onFocusChange(v,focus);
    }

    @OnClick(R.id.main_security)
    void to_security(){
        openSecurityAty();
    }
    @OnClick(R.id.main_healthy)
    void to_healthy(){
        openHealthyAty();
    }

    private void openHealthyAty() {
        startActivity(new Intent(this,HealthyAty.class));
    }

    private void openSecurityAty() {
        startActivity(new Intent(this,SecurityAty.class));
    }

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        initListeners();
    }

    private void initListeners() {
        mSecurity.post(new Runnable() {
            @Override
            public void run() {
                mSecurity.requestFocus();
            }
        });
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onExit() {

    }

    private void onFocusChange(View v, boolean hasFocus) {
        RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) v.getLayoutParams();
        if (hasFocus) {
            lp.width=v.getWidth() + OFFSET_FOCUS_SIZE_CHANGE;
            lp.height=v.getHeight() + OFFSET_FOCUS_SIZE_CHANGE;

        } else {
            lp.width=v.getWidth() - OFFSET_FOCUS_SIZE_CHANGE;
            lp.height=v.getHeight() - OFFSET_FOCUS_SIZE_CHANGE;
        }
        v.requestLayout();
    }

}
