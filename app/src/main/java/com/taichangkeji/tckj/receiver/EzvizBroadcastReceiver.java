package com.taichangkeji.tckj.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.taichangkeji.tckj.activity.MainActivity;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.model.Family;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.constant.Constant;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by MAC on 15/12/26.
 */
public class EzvizBroadcastReceiver extends BroadcastReceiver {

    private Context context;
    private EZAccessToken token;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        if (intent.getAction().equals(Constant.OAUTH_SUCCESS_ACTION)) {
            token=EZOpenSDK.getInstance().getEZAccessToken();
            UserUtils.setAccessToken(context,token);
            LogUtils.d("token:"+token.getAccessToken()+"\n"+token.getExpire());
            new MyTask().execute();
        }
    }
    class MyTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            String result=null;
            try {
                HttpURLConnection conn= (HttpURLConnection) new URL(Config.getFamilyInfo+"accessToken="+token.getAccessToken()).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result=br.readLine();
                LogUtils.d(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            Family family=new Gson().fromJson(o.toString(),Family.class);
            UserUtils.setFamilyId(context,family.getId());
            UserUtils.setFamilyName(context,family.getName());
            Intent i=new Intent(context,MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}