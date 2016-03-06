package com.taichangkeji.tckj.activity;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.pd.hid.HidMsg;
import com.pd.hid.IHidMsgCallback;
import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.cusview.MeasureIndicator;
import com.taichangkeji.tckj.model.BTUserInfo;
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
    private final static String TAG = "MeasureAty";
    public static BTUserInfo userInfo;
    HidMsg hidDevice = null;
    Handler mHandler;

    @Override
    protected void initDatas() {

        /**
         * TODO 初始化用户信息(有些测量项目需要下发用户基本信息，所以默认需要初始化用户一些基本身体参数)
         */
        userInfo = new BTUserInfo();
        userInfo.setAge("24");
        userInfo.setHeight("178");
        userInfo.setProfession("1");
        userInfo.setSex("1");
        userInfo.setStep("70");
        userInfo.setWeight("65");


        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mResult.setText(msg.obj.toString());
            }
        };


        openHid();
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
    /**
     * 打开hid设备读取
     */
    private void openHid() {
        hidDevice = new HidMsg(msgCallBack);
        new Thread(hidDevice).start();
    }

    /**
     * 关闭hid设备读取
     */
    private void closeHid() {
        HidMsg.isExit = true;
        if(hidDevice != null) {
            hidDevice.exit = false;
            hidDevice = null;
        }
    }

    /**
     * 接收数据
     */
    IHidMsgCallback msgCallBack = new IHidMsgCallback() {
        @Override
        public void onReceiveMessage(String content) {
            if(content != null) {
                LogUtils.d( "hid msg receive : " + content);
                Message msg = mHandler.obtainMessage();
                msg.obj = content;
                mHandler.sendMessage(msg);
            }
        }
    };


    protected void onStop() {
        super.onStop();
        closeHid();
    };
}
