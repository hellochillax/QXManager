package com.taichangkeji.tckj.activity;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.taichangkeji.tckj.R;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class MainActivity extends BaseActivity{

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

    @OnClick(R.id.main_security)
    void to_security(){
        openSecurityAty();
    }
    @OnClick(R.id.main_healthy)
    void to_healthy(){
        openHealthyAty();
    }
    @OnClick(R.id.main_video)
    void to_video(){openVideoAty();}

    private void openVideoAty() {
        startActivity(new Intent(this,VideoAty.class));
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
    }


    @Override
    protected int initLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onExit() {

    }

    @OnFocusChange(R.id.main_security)
    void main_security(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.security_highlighted);
        }else {
            iv.setImageResource(R.mipmap.security);
        }
    }
    @OnFocusChange(R.id.main_healthy)
    void main_healthy(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.healthy_highlighted);
        }else {
            iv.setImageResource(R.mipmap.healthy);
        }
    }
    @OnFocusChange(R.id.main_community)
    void main_community(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.community_highlighted);
        }else {
            iv.setImageResource(R.mipmap.community);
        }
    }
    @OnFocusChange(R.id.main_shop)
    void main_shop(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.shop_highlighted);
        }else {
            iv.setImageResource(R.mipmap.shop);
        }
    }
    @OnFocusChange(R.id.main_service)
    void main_service(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.service_highlighted);
        }else {
            iv.setImageResource(R.mipmap.service);
        }
    }
    @OnFocusChange(R.id.main_video)
    void main_video(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.video_highlighted);
        }else {
            iv.setImageResource(R.mipmap.video);
        }
    }

}
