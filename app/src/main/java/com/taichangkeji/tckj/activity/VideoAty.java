package com.taichangkeji.tckj.activity;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.utils.AudioPlayUtil;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.util.LocalInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MAC on 16/1/12.
 */
public class VideoAty extends BaseActivity implements SurfaceHolder.Callback {

    EZOpenSDK mSDKInstance;
    EZPlayer mSDKPlayer;
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
// 保持屏幕常亮
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

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_video;
    }

    @Override
    protected void onExit() {

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


    private class GetCameraInfoTask extends AsyncTask {
        @Override
        protected void onPostExecute(Object o) {
            if (mCameraInfo == null) {
                return;
            }
            LogUtils.d(mCameraInfo.getCameraId() + ":" + mCameraInfo.getCameraName());
            mSDKPlayer = mSDKInstance.createPlayer(context, mCameraInfo.getCameraId());
            if (mSDKPlayer==null)return;
            mSDKPlayer.setHandler(mPlayHandler);
            mSDKPlayer.setSurfaceHold(mSurfaceHolder);
            mSDKPlayer.startRealPlay();
        }
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                LogUtils.d("camear_id:"+UserUtils.getCameraId(context));
                    mCameraInfo=EZOpenSDK.getInstance().getCameraInfo(UserUtils.getCameraId(context));
//                mCameraList = mSDKInstance.getCameraList(0, 10);
//                if (mCameraList != null && mCameraList.size() > 0) {
//                    mCameraInfo = mCameraList.get(0);
//                    LogUtils.d(mCameraInfo.toString());
//                    mPlayHandler.obtainMessage(0,mCameraInfo).sendToTarget();
//                }
            } catch (BaseException e) {
                LogUtils.e("VideoAty:" + e.getErrorCode());
                mPlayHandler.obtainMessage(0,e.getErrorCode()+"").sendToTarget();
                e.printStackTrace();
            }
            return null;
        }
    }

}
