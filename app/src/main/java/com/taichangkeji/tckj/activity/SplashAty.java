package com.taichangkeji.tckj.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.ezsdk.EZToken;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by MAC on 15/12/26.
 *
 * 应用第一个进入的Activity,在这里实现了accessToken的获取以及判断用户是否登陆等功能
 */
public class SplashAty extends BaseActivity {

    private RequestQueue mRequestQueue;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //网络错误
            showAlertDialog("网络错误,请稍后重试");
        }
    };

    @Override
    protected void initDatas() {
        File file=new File(Config.cachePath);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    @Override
    protected void initViews() {
//        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#297a99"));
        mRequestQueue =  Volley.newRequestQueue(this);
        checkNextPage();
    }

    private void checkNextPage() {
        //进行登录检查操作
        String token=UserUtils.getAccessToken(this);
        if(token!=null){
            EZOpenSDK.getInstance().setAccessToken(new Gson().fromJson(token,EZToken.class).getAccessToken());
            checkIsLogin();
        }else{
            GetAccessToken();
        }
    }

    private void GetAccessToken() {
        StringRequest sq=new StringRequest(Request.Method.POST, Config.getAccessToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d(response);
                String token=new Gson().fromJson(response,EZToken.class).getAccessToken();
//                UserUtils.setAccessToken(context,response);
                //这里应该设置token,不带时间戳id!!!!!! accesstoken错误码:110002
                EZOpenSDK.getInstance().setAccessToken(token);
                checkIsLogin();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d("SplashAty:"+error.getMessage());
                mHandler.obtainMessage().sendToTarget();
            }
        });
        mRequestQueue.add(sq);
    }

    private void checkIsLogin() {
        if(UserUtils.getUserId(this)==null){
            openLoginAty();
        }else {
            openMainAty();
        }
    }

    private void openMainAty() {
        startActivity(new Intent(this,MainActivity.class));
        onBackPressed();
    }

    private void openLoginAty() {
        startActivity(new Intent(this,LoginAty.class));
        onBackPressed();
    }

    @Override
    protected int initLayoutRes() {
        return 0;
    }

    @Override
    protected void onExit() {
        Process.killProcess(Process.myPid());
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mRequestQueue.cancelAll(this);
    }
}
