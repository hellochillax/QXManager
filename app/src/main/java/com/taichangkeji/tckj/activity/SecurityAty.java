package com.taichangkeji.tckj.activity;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.model.Equipment;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnFocusChange;

/**
 * Created by MAC on 15/12/27.
 *
 * 安防界面
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

    String Serial=null;


    int mType=8;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    showToast("状态转换成功");
                    switch (mType){
                        case 0:
                            change_infrared(false);
                            change_door(true);
                            break;
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
        if(Serial==null){
            switch (s){
                case 0:
                    change_infrared(false);
                    change_door(true);
                    break;
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
                if(Serial!=null){
                    try {
                        EZOpenSDK.getInstance().setDeviceDefence(Serial,s);
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

    RequestQueue mQueue;

    private class SecurityTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            mQueue.add(new StringRequest(Request.Method.POST, Config.getIDsAndLogin+"LoginName="+UserUtils.getUserId(context), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LogUtils.d(response);
                    if(response.contains("success")){
                        Pattern p=Pattern.compile("data\":(.*)\\}");
                        Matcher m=p.matcher(response);
                        if(m.find()){
                            Type type = new TypeToken<List<Equipment>>(){}.getType();
                            List<Equipment> list=new Gson().fromJson(m.group(1), type);
                            LogUtils.d(list.toString());
                            for (Equipment e:list){
                                if(e.getComment().equals("报警盒子")){
                                    Serial=e.getSRNo();
                                }
                            }
                        }
                    }else {
                        showToast("网络错误");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }));
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
        mQueue= Volley.newRequestQueue(context);
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
