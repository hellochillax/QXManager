package com.taichangkeji.tckj.utils;

import android.content.Context;

import com.videogo.openapi.bean.EZAccessToken;

/**
 * Created by MAC on 15/12/26.
 *
 * 这里缓存了一些关于用户的数据
 */
public class UserUtils {
    private static final String TOKEN="EZAccessToken";

    public static void setAccessToken(Context context,String token){
        context.getSharedPreferences(TOKEN,0).edit().putString("token",token).commit();
    }
    public static String getAccessToken(Context context){
        String str1=context.getSharedPreferences(TOKEN,0).getString("token",null);
        return str1;
    }

    public static void setCameraId(Context context,String id){
        context.getSharedPreferences(TOKEN,0).edit().putString("camearid",id).commit();
    }
    public static String getCameraId(Context context){
        return  context.getSharedPreferences(TOKEN,0).getString("camearid",null);
    }
    //缓存用户的手机号
    public static void setUserId(Context context,String id){
        context.getSharedPreferences(TOKEN,0).edit().putString("user_phone",id).commit();
    }
    public static String getUserId(Context context){
        return context.getSharedPreferences(TOKEN,0).getString("user_phone",null);
    }
//    public static void setFamilyId(Context context,String id){
//        context.getSharedPreferences(TOKEN,0).edit().putString("familyId",id).commit();
//    }
//    public static String getFamilyId(Context context){
//        return context.getSharedPreferences(TOKEN,0).getString("familyId",null);
//    }
//    public static void setFamilyName(Context context,String name){
//        context.getSharedPreferences(TOKEN,0).edit().putString("familyName",name).commit();
//    }
//    public static String getFamilyName(Context context){
//        return context.getSharedPreferences(TOKEN,0).getString("familyName",null);
//    }
    public static void setDefenceType(Context c,int t){
        c.getSharedPreferences(TOKEN,0).edit().putInt("DefenceType",t).commit();
    }
    public static int getDefenceType(Context c){
        return c.getSharedPreferences(TOKEN,0).getInt("DefenceType",8);
    }
}
