package com.taichangkeji.tckj.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.taichangkeji.tckj.utils.LogUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZAlarmInfo;
import com.videogo.openapi.EZOpenSDK;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MAC on 16/3/11.
 */
public class BackService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            new AsyncTask(){

                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        infos=EZOpenSDK.getInstance().getAlarmListBySerial("518082291",0,20,null,null);
                    } catch (BaseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    LogUtils.d("AlarmList:"+infos.toString());
                    try {
                        Thread.sleep(1000*20);
                        handler.obtainMessage().sendToTarget();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    };

    List<EZAlarmInfo> infos;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        handler.obtainMessage().sendToTarget();
        return super.onStartCommand(intent, flags, startId);
    }
}
