package com.taichangkeji.tckj.activity;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.cusview.MeasureIndicator;
import com.taichangkeji.tckj.model.Member;
import com.taichangkeji.tckj.utils.LogUtils;
import com.videogo.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.Bind;

/**
 * Created by MAC on 16/1/14.
 */
public class MeasureAty extends BaseActivity {

    @Bind(R.id.user_icon)
    ImageView mUserIcon;
    @Bind(R.id.user_relation)
    TextView mUserRelation;
    @Bind(R.id.type)
    TextView mType;
    @Bind(R.id.result)
    TextView mResult;
    @Bind(R.id.type2)
    TextView mType2;
    @Bind(R.id.indicator)
    MeasureIndicator mIndicator;
    @Bind(R.id.bottom_l)
    ImageView mBottomL;
    @Bind(R.id.bottom_c)
    ImageView mBottomC;
    @Bind(R.id.bottom_r)
    ImageView mBottomR;
    Member mMember;


    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        mBottomL.requestFocus();
        mMember=getIntent().getParcelableExtra("user");
        LogUtils.d("MeasureAty:"+mMember);
        setUserIcon();
        mUserRelation.setText(mMember.getRelations());
    }

    private void setUserIcon() {
        File file=new File(Config.cachePath);
        boolean isIconHasCache=false;
        for (File icon:file.listFiles()){
            if(icon.getName().equals(mMember.getHealthUserID()+".png")){
                isIconHasCache=true;
                ImageLoader.getInstance().displayImage("file:///"+Config.cachePath + "/" + icon.getName(), mUserIcon);
                LogUtils.d(Config.cachePath + "/" + icon.getName());
            }
        }
        if(!isIconHasCache){
            LogUtils.d(Config.getUserIconPre+mMember.getFileFolder());
            ImageLoader.getInstance().displayImage(Config.getUserIconPre+mMember.getFileFolder(),mUserIcon);
        }
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_measure;
    }

    @Override
    protected void onExit() {

    }

}
