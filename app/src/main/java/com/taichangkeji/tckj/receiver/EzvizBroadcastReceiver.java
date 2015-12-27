package com.taichangkeji.tckj.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.taichangkeji.tckj.activity.MainActivity;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.constant.Constant;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

/**
 * Created by MAC on 15/12/26.
 */
public class EzvizBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constant.OAUTH_SUCCESS_ACTION)) {
            EZAccessToken token=EZOpenSDK.getInstance().getEZAccessToken();
            UserUtils.setAccessToken(context,token);
            LogUtils.d("token:"+token.getAccessToken()+"\n"+token.getExpire());
            Intent i=new Intent(context,MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}