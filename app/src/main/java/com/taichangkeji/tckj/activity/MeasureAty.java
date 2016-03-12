package com.taichangkeji.tckj.activity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pd.hid.HidMsg;
import com.pd.hid.IHidMsgCallback;
import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.model.BTUserInfo;
import com.taichangkeji.tckj.model.Member;
import com.taichangkeji.tckj.utils.LogUtils;
import com.videogo.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    RequestQueue mQueue;

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
                String result=msg.obj.toString();
                if(result.contains("体重测量数据")){
                    mType.setText("体重");
                    mType2.setText("千克");
                    mType2.setVisibility(View.VISIBLE);
                    mResult.setText(result.replaceAll("[^\\d\\.]",""));
                    updateTizhong(result.replaceAll("[^\\d\\.]",""),mMember.getHealthUserID()+"");

                }else if(result.contains("耳温枪测量数据")){
                    mType.setText("体温");
                    mType2.setText("摄氏度");
                    mType2.setVisibility(View.VISIBLE);
                    mResult.setText(result.replaceAll("[^\\d\\.]",""));
                    updateWendu(result.replaceAll("[^\\d\\.]",""),mMember.getHealthUserID()+"");
                }else if(result.contains("血压计测量数据")){
                    mType.setText("血压");
                    mResult.setText(result.replaceAll(",||","\r\n").replaceAll("接收到血压计测量数据。",""));
                    mType2.setVisibility(View.GONE);
                    Pattern pattern=Pattern.compile("[^\\d]*([\\d\\.]*)[^\\d]*([\\d\\.]*)[^\\d]*([\\d\\.]*)");
                    Matcher matcher=pattern.matcher(result);
                    if(matcher.find()){
                        updatePressure(matcher.group(1),matcher.group(2),matcher.group(3),""+mMember.getHealthUserID());
                    }
                }else if(result.contains("脂肪仪测量数据")){
                    mType.setText("脂肪");
                    mType2.setVisibility(View.GONE);
                    mResult.setText(result.replaceAll("接收到脂肪仪测量数据。","").replaceAll(",","\r\n"));
                    Pattern pattern=Pattern.compile("[^\\d]*([\\d\\.]*)[^\\d]*([\\d\\.]*)[^\\d]*([\\d\\.]*)[^\\d]*([\\d\\.]*)");
                    Matcher matcher=pattern.matcher(result);
                    if(matcher.find()){
                        updateZhifdang(matcher.group(2),matcher.group(1),matcher.group(3),matcher.group(4),""+mMember.getHealthUserID());
                    }
                }else if (result.contains("血糖仪测量数据")){
                    mType.setText("血糖");
                    mResult.setText(result.replaceAll("[^\\d\\.]",""));
                    mType2.setVisibility(View.GONE);
                    updateXueTang(result.replaceAll("[^\\d\\.]",""),mMember.getHealthUserID()+"");
                }
            }
        };


        openHid();
    }

    private void updateZhifdang(String FatRate,String FatClass,String Muscle,String Water,String HealthUserID) {
        String url=Config.updateZhifang
                +"FatRate="+FatRate+"&FatClass="+FatClass+"&Muscle="+Muscle+"&Water="+Water+"&HealthUserID="+HealthUserID;
        mQueue.add(new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d("updateZhifdang:"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d("updateZhifdang:"+error);
            }
        }));
        LogUtils.d(url);
    }

    private void updateTizhong(String Weight, String HealUserID) {
        String url=Config.updateWeight
                +"Weight="+Weight+"&HealthUserID="+HealUserID;
        mQueue.add(new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d("updateTizhong:"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d("updateTizhong:"+error);
            }
        }));
        LogUtils.d(url);
    }

    private void updateWendu(String Temperature,String HealUserID) {
        String url=Config.updateWenDu
                +"Temperature="+Temperature+"&HealthUserID="+HealUserID;
        mQueue.add(new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d("updateWendu:"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d("updateWendu:"+error);
            }
        }));
        LogUtils.d(url);
    }

    private void updateXueTang(String BloodGlucose,String HealthUserID) {
        String url=Config.updateXuetang
                +"BloodGlucose="+BloodGlucose+"&HealthUserID="+HealthUserID;
        mQueue.add(new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d("updateXueTang:"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d("updateXueTang:"+error);
            }
        }));
        LogUtils.d(url);
    }

    private void updatePressure(String HighPressure,String LowPressure,String Pulse,String HealthUserID ) {
        String url=Config.updatePesssure
                +"HighPressure="+HighPressure+"&LowPressure="+LowPressure+"&Pulse="+Pulse+"&HealthUserID="+HealthUserID;
        mQueue.add(new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d("updatePressure:"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d("updatePressure:"+error);
            }
        }));
        LogUtils.d(url);
    }

    @Override
    protected void initViews() {
        mBottomL.requestFocus();
        mMember=getIntent().getParcelableExtra("user");
        LogUtils.d("MeasureAty:"+mMember);
        setUserIcon();
        mUserRelation.setText(mMember.getRelations());
        mQueue= Volley.newRequestQueue(context);
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
        mQueue.cancelAll(null);
    };
}
