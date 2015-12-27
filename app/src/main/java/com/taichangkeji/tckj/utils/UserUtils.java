package com.taichangkeji.tckj.utils;

import android.content.Context;

import com.videogo.openapi.bean.EZAccessToken;

/**
 * Created by MAC on 15/12/26.
 */
public class UserUtils {
    private static final String TOKEN="EZAccessToken";

    public static void setAccessToken(Context context,EZAccessToken token){
        context.getSharedPreferences(TOKEN,0).edit().putString("token",token.getAccessToken()).putInt("expire",token.getExpire()).commit();
    }
    public static EZAccessToken getAccessToken(Context context){
        EZAccessToken token=new EZAccessToken();
        String str1=context.getSharedPreferences(TOKEN,0).getString("token",null);
        int str2=context.getSharedPreferences(TOKEN,0).getInt("expire",0);
        if(str1==null){
            return null;
        }
        token.setAccessToken(str1);
        token.setExpire(str2);
        return token;
    }
}
