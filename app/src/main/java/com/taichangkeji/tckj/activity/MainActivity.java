package com.taichangkeji.tckj.activity;


import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.taichangkeji.tckj.R;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements View.OnFocusChangeListener {

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

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        initListeners();
    }

    private void initListeners() {
        mSecurity.setOnFocusChangeListener(this);
        mHealthy.setOnFocusChangeListener(this);
        mVideo.setOnFocusChangeListener(this);
        mCommunity.setOnFocusChangeListener(this);
        mShop.setOnFocusChangeListener(this);
        mService.setOnFocusChangeListener(this);
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
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
