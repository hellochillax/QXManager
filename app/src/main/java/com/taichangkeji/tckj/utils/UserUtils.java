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
    public static void setFamilyId(Context context,String id){
        context.getSharedPreferences(TOKEN,0).edit().putString("familyId",id).commit();
    }
    public static String getFamilyId(Context context){
        return context.getSharedPreferences(TOKEN,0).getString("familyId",null);
    }
    public static void setFamilyName(Context context,String name){
        context.getSharedPreferences(TOKEN,0).edit().putString("familyName",name).commit();
    }
    public static String getFamilyName(Context context){
        return context.getSharedPreferences(TOKEN,0).getString("familyName",null);
    }
}
