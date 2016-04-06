package com.taichangkeji.tckj.activity;

import android.app.Application;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import com.taichangkeji.tckj.utils.AudioPlayUtil;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.ScreenUtil;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.util.LocalInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MAC on 16/1/12.
 */
public class VideoAty extends BaseActivity implements SurfaceHolder.Callback {

    EZOpenSDK mSDKInstance;
    EZPlayer mSDKPlayer;
//    List<Equipment> mEquipmentList;
    List<String> mEquipmentList;
    List<EZCameraInfo> mCameraInfoList;
    EZCameraInfo mCameraInfo;

    SurfaceHolder mSurfaceHolder;


    Handler mPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mToastView.setText(msg.obj==null?"null":msg.obj.toString());
            switch (msg.what){
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                    LogUtils.d("播放成功");
                    break;
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
                    LogUtils.d("播放失败:"+msg.what+":"+msg.arg1);
                    break;
            }
        }
    };

    Handler mUtilHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0://未找到摄像头设备
                    showAlertDialog("未找到摄像头设备");
                    break;
                case 1://确定了要播放的Camera的Info,开始播放
                    LogUtils.d(mCameraInfo.getCameraId() + ":" + mCameraInfo.getCameraName());
                    mSDKPlayer = mSDKInstance.createPlayer(context, mCameraInfo.getCameraId());
                    if (mSDKPlayer==null)return;
                    mSDKPlayer.setHandler(mPlayHandler);
                    mSDKPlayer.setSurfaceHold(mSurfaceHolder);
                    mSDKPlayer.startRealPlay();
                    break;
            }
        }
    };

    @Bind(R.id.sv)
    SurfaceView mSurfaceView;
    @Bind(R.id.toast)
    TextView mToastView;
//    private AudioPlayUtil mAudioPlayUtil = null;
//    private LocalInfo mLocalInfo = null;
    @Override
    protected void initDatas() {
        mSDKInstance = EZOpenSDK.getInstance();
    }
    LocalInfo mLocalInfo;
    AudioPlayUtil mAudioPlayUtil;
    @Override
    protected void initViews() {
        mCameraInfoList=new ArrayList<>();
        mEquipmentList=new ArrayList<>();
// 保持屏幕常亮
        mQueue= Volley.newRequestQueue(context);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSurfaceHolder=mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
// 获取本地信息
        Application application = (Application) getApplication();
        mAudioPlayUtil = AudioPlayUtil.getInstance(application);
        // 获取配置信息操作对象
        mLocalInfo = LocalInfo.getInstance();
        // 获取屏幕参数
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mLocalInfo.setScreenWidthHeight(metric.widthPixels, metric.heightPixels);
        mLocalInfo.setNavigationBarHeight((int) Math.ceil(25 * getResources().getDisplayMetrics().density));
//        new AsyncTask(){
//
//            @Override
//            protected void onPostExecute(Object o) {
//                LogUtils.d("mCameraInfo:"+mCameraInfo);
//                if (mCameraInfo != null) {
//                    mSDKPlayer = mSDKInstance.createPlayer(context, mCameraInfo.getCameraId());
//                    if(mSDKPlayer == null)
//                        return;
//                    mSDKPlayer.setHandler(mPlayHandler);
//                    mSDKPlayer.setSurfaceHold(mSurfaceHolder);
//                    mSDKPlayer.startRealPlay();
//                }
//            }
//
//            @Override
//            protected Object doInBackground(Object[] params) {
//                try {
//                    LogUtils.d("camear_id:"+UserUtils.getCameraId(context));
//                    mCameraInfo=EZOpenSDK.getInstance().getCameraInfo(UserUtils.getCameraId(context));
//                    LogUtils.d("haha");
//                    LogUtils.d("瞅瞅"+EZOpenSDK.getInstance().getCameraList(0,10).toString());
//
//                } catch (BaseException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();
        new GetCameraInfoTask().execute();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mCameraInfo==null){
                if(mCameraInfoList.size()==1){
                    mCameraInfo=mCameraInfoList.get(0);
                }else {
                    showCameraChooseDialog();
                    return;
                }
            }
            mUtilHandler.obtainMessage(1).sendToTarget();
        }
    };

    private Dialog mCameraChooseDialog;
    private void showCameraChooseDialog() {
        if(mCameraChooseDialog==null){
            View view= LayoutInflater.from(this).inflate(R.layout.camera_choose_dialog, null);
            mCameraChooseDialog=new Dialog(this,R.style.exit_dialog_style);
            mCameraChooseDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mCameraChooseDialog.setCancelable(false);
            mCameraChooseDialog.setCanceledOnTouchOutside(false);
            WindowManager.LayoutParams lp = mCameraChooseDialog.getWindow().getAttributes();
            lp.height= ScreenUtil.getScreenHeight(this)/3*2;
            lp.width=ScreenUtil.getScreenWidth(this)/3*2;
            mCameraChooseDialog.onWindowAttributesChanged(lp);
            mCameraChooseDialog.setContentView(view);
            ((TextView)view.findViewById(R.id.title)).setText("请选择摄像头");
            ListView lv= (ListView) view.findViewById(R.id.list);
            List<Map<String,String>> datas=new ArrayList<>();
            for (EZCameraInfo info:mCameraInfoList){
                Map<String,String> map=new HashMap<>();
                map.put("name",info.getCameraName());
                datas.add(map);
            }
            lv.setAdapter(new SimpleAdapter(VideoAty.this,datas,R.layout.camera_choose_list_item,new String[]{"name"},new int[]{R.id.name}));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCameraInfo=mCameraInfoList.get(position);
                    mCameraChooseDialog.dismiss();
                    mUtilHandler.obtainMessage(1).sendToTarget();
                    LogUtils.d("Chose Dialog:"+mCameraInfo);
                }
            });
        }
        mCameraChooseDialog.show();
    }


    @Override
    protected int initLayoutRes() {
        return R.layout.aty_video;
    }

    @Override
    protected void onExit() {
        onBackPressed();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mSDKPlayer != null) {
            mSDKPlayer.setSurfaceHold(holder);
        }
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mSDKPlayer != null) {
            mSDKPlayer.setSurfaceHold(null);
        }
        mSurfaceHolder = null;
    }

    RequestQueue mQueue;

    private class GetCameraInfoTask extends AsyncTask {
//        @Override
//        protected void onPostExecute(Object o) {
//            if (mCameraInfo == null) {
//                return;
//            }
//            LogUtils.d(mCameraInfo.getCameraId() + ":" + mCameraInfo.getCameraName());
//            mSDKPlayer = mSDKInstance.createPlayer(context, mCameraInfo.getCameraId());
//            if (mSDKPlayer==null)return;
//            mSDKPlayer.setHandler(mPlayHandler);
//            mSDKPlayer.setSurfaceHold(mSurfaceHolder);
//            mSDKPlayer.startRealPlay();
//        }
        @Override
        protected Object doInBackground(Object[] params) {
            mQueue.add(new StringRequest(Request.Method.POST, Config.getCameraDeviceSerial+"UserID="+UserUtils.getFamilyId(context), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LogUtils.d(response);
                    if(response.contains("success")){
//                        Pattern p=Pattern.compile("data\":(.*)\\}");
//                        Matcher m=p.matcher(response);
//                        if(m.find()){
//                            Type type = new TypeToken<List<Equipment>>(){}.getType();
//                            List<Equipment> list=new Gson().fromJson(m.group(1), type);
//                            LogUtils.d(list.toString());
//                            mEquipmentList.clear();
//                            for (final Equipment e:list){
//                                if(e.getComment().equals("摄像头")){
//                                    mEquipmentList.add(e);
//                                }
//                            }
//                            if(mEquipmentList.size()>0){
//                                try {
//                                    new AsyncTask(){
//
//                                        @Override
//                                        protected void onPostExecute(Object o) {
//                                            handler.obtainMessage().sendToTarget();
//                                        }
//
//                                        @Override
//                                        protected Object doInBackground(Object[] params) {
//                                            try {
//                                                mCameraInfoList.clear();
//                                                for (Equipment e:mEquipmentList){
//                                                    mCameraInfoList.add(EZOpenSDK.getInstance().getCameraInfo(e.getSRNo()));
//                                                }
//                                                LogUtils.d(mCameraInfoList.toString());
//                                            } catch (BaseException e3) {
//                                                e3.printStackTrace();
//                                                LogUtils.e("VideoAty:" + e3.getErrorCode());
//                                                mPlayHandler.obtainMessage(0,e3.getErrorCode()+"").sendToTarget();
//                                            }
//                                            return null;
//                                        }
//                                    }.execute();
//                                } catch (Exception e1) {
//                                    e1.printStackTrace();
//                                }
//                            }else {
//                                mUtilHandler.obtainMessage(0).sendToTarget();
//                            }
//                        }
                        Pattern p=Pattern.compile("SRNo\":\"(\\d*)");
                        Matcher m=p.matcher(response);
                        while (m.find()){
                            mEquipmentList.add(m.group(1));
                        }
                        LogUtils.d(mEquipmentList.toString());
                        if(mEquipmentList.size()>0) {
                            try {
                                new AsyncTask() {

                                    @Override
                                    protected void onPostExecute(Object o) {
                                        handler.obtainMessage().sendToTarget();
                                    }

                                    @Override
                                    protected Object doInBackground(Object[] params) {
                                        try {
                                            mCameraInfoList.clear();
                                            for (String s : mEquipmentList) {
                                                mCameraInfoList.add(EZOpenSDK.getInstance().getCameraInfo(s));
                                            }
                                            LogUtils.d(mCameraInfoList.toString());
                                        } catch (BaseException e3) {
                                            e3.printStackTrace();
                                            LogUtils.e("VideoAty:" + e3.getErrorCode());
                                            mPlayHandler.obtainMessage(0, e3.getErrorCode() + "").sendToTarget();
                                        }
                                        return null;
                                    }
                                }.execute();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                    }else {//未找到相关设备
                        mUtilHandler.obtainMessage(0).sendToTarget();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }));
//            try {
//
////                LogUtils.d("camear_id:"+UserUtils.getCameraId(context));
//
////                mCameraList = mSDKInstance.getCameraList(0, 10);
////                if (mCameraList != null && mCameraList.size() > 0) {
////                    mCameraInfo = mCameraList.get(0);
////                    LogUtils.d(mCameraInfo.toString());
////                    mPlayHandler.obtainMessage(0,mCameraInfo).sendToTarget();
////                }
//            } catch (BaseException e) {
//                LogUtils.e("VideoAty:" + e.getErrorCode());
//                mPlayHandler.obtainMessage(0,e.getErrorCode()+"").sendToTarget();
//                e.printStackTrace();
//            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mQueue!=null){
            mQueue.cancelAll(null);
        }
    }
}
