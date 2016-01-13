package com.taichangkeji.tckj.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.utils.LogUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MAC on 16/1/12.
 */
public class VideoAty extends BaseActivity implements SurfaceHolder.Callback {

    EZOpenSDK mSDKInstance;
    EZPlayer mSDKPlayer;
    List<EZCameraInfo> mCameraList;
    EZCameraInfo mCameraInfo;
    SurfaceHolder mSurfaceHolder;
    Handler mPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mToastView.setText(msg.obj.toString());
        }
    };
    @Bind(R.id.sv)
    SurfaceView mSurfaceView;
    @Bind(R.id.toast)
    TextView mToastView;

    @Override
    protected void initDatas() {
        mSDKInstance = EZOpenSDK.getInstance();
        new GetCameraInfoTask().execute();
    }

    @Override
    protected void initViews() {
// 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
            mSDKPlayer.setHandler(mPlayHandler);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(VideoAty.this);
            mSDKPlayer.setSurfaceHold(mSurfaceHolder);
            startRealPlay();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                mCameraList = mSDKInstance.getCameraList(0, 10);
                if (mCameraList != null && mCameraList.size() > 0) {
                    mCameraInfo = mCameraList.get(0);
                    LogUtils.d(mCameraInfo.toString());
                    mPlayHandler.obtainMessage(0,mCameraInfo).sendToTarget();
                }
            } catch (BaseException e) {
                LogUtils.e("VideoAty:" + e.getErrorCode());
                mPlayHandler.obtainMessage(0,e.getErrorCode()+"").sendToTarget();
                e.printStackTrace();
            }
            return null;
        }
    }

    private void startRealPlay() {
        System.out.println("online:" + mCameraInfo.getOnlineStatus());
        mSDKPlayer.startRealPlay();
    }
}
