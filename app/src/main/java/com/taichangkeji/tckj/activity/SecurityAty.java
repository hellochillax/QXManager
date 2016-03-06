package com.taichangkeji.tckj.activity;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.List;

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
    EZDeviceInfo mInfo;
    int mType=8;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    showToast("状态转换成功");
                    switch (mType){
                        case 0:
                        case 16:
                            change_infrared(true);
                            change_door(true);
                            break;
                        case 8:
                            change_infrared(false);
                            change_door(false);
                            break;
                    }
                    break;
                case 1:
                    showToast("未找到A1设备");
                    break;
                case 2:
                    showToast("网络错误,请重试");
                    break;
            }
        }
    };

    @Override
    protected void initDatas() {
        new SecurityTask().execute();
    }

    public void setA1States(final int s) {
        if(mInfo==null){
            switch (s){
                case 0:
                case 16:
                    change_infrared(true);
                    change_door(true);
                    break;
                case 8:
                    change_infrared(false);
                    change_door(false);
                    break;
            }
            return;
        }
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                /**
                 * 布防状态, IPC布防状态只有0和1，
                 A1有0:睡眠 8:在家 16:外出
                 */
                if(mInfo!=null){
                    try {
                        EZOpenSDK.getInstance().setDeviceDefence(mInfo.getDeviceSerial(),s);
                        mHandler.obtainMessage(0).sendToTarget();
                    } catch (BaseException e) {
                        e.printStackTrace();
                        mHandler.obtainMessage(2).sendToTarget();
                    }
                }else {

                }
                return null;
            }
        }.execute();
    }

    private class SecurityTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<EZDeviceInfo> deviceList= EZOpenSDK.getInstance().getDeviceList(0,10);
                for (EZDeviceInfo info:deviceList){
                    if(info.getDeviceName().contains("A1")){
                        mInfo=info;
                        LogUtils.d(info.toString());
                        System.out.println("dd"+EZOpenSDK.getInstance().getDetectorList(mInfo.getDeviceSerial()));
                    }
                }
                if(mInfo==null){
                    mHandler.obtainMessage(1).sendToTarget();
                }
            } catch (BaseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void initViews() {
        int type= UserUtils.getDefenceType(context);
        switch (type){
            case 0:
                mSleep.requestFocus();
                break;
            case 8:
                mHome.requestFocus();
                break;
            case 16:
                mOut.requestFocus();
                break;
        }
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
            mType=16;
            setA1States(mType);
        }else {
            mOut.setImageResource(R.mipmap.security_out);
        }
    }



    @OnFocusChange(R.id.home) void change_home(boolean b){
        if(b){
            mHome.setImageResource(R.mipmap.security_home_2);
            mType=8;
            setA1States(mType);
        }else {
            mHome.setImageResource(R.mipmap.security_home);
        }

    }
    @OnFocusChange(R.id.sleep) void change_sleep(boolean b){
        if(b){
            mSleep.setImageResource(R.mipmap.security_sleep_2);
            mType=0;
            setA1States(mType);
        }else {
            mSleep.setImageResource(R.mipmap.security_sleep);
        }

    }
    void change_infrared(boolean b){
        if(b){
            mInfraredL.setImageResource(R.mipmap.security_infrared_2);
            mInfraredR.setImageResource(R.mipmap.security_switch_2);
        }else {
            mInfraredL.setImageResource(R.mipmap.security_infrared);
            mInfraredR.setImageResource(R.mipmap.security_switch);
        }
    }
    void change_door(boolean b){
        if(b){
            mDoorL.setImageResource(R.mipmap.security_door_2);
            mDoorR.setImageResource(R.mipmap.security_switch_2);
        }else {
            mDoorL.setImageResource(R.mipmap.security_door);
            mDoorR.setImageResource(R.mipmap.security_switch);
        }
    }
//    void change_smoke(boolean b){
//        if(b){
//            mSmokeL.setImageResource(R.mipmap.security_smoke_2);
//            mSmokeR.setImageResource(R.mipmap.security_switch_2);
//        }else {
//            mSmokeL.setImageResource(R.mipmap.security_smoke);
//            mSmokeR.setImageResource(R.mipmap.security_switch);
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UserUtils.setDefenceType(context,mType);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
//            case 21://left
            case 19://up
//            case 22://right
            case 20://down
                return true;
//            case 66://ok
        }
        return super.onKeyDown(keyCode, event);
    }
}
