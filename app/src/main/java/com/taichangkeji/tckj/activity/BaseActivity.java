package com.taichangkeji.tckj.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.taichangkeji.tckj.R;

import butterknife.ButterKnife;

/**
 * Created by MAC on 15/12/1.
 */
public abstract class BaseActivity extends Activity {

    private Dialog mExitDialog;
    protected Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        if (initLayoutRes() != 0) {
            setContentView(initLayoutRes());
        }else {
            throw new NullPointerException("you may have forgot to set layout resource id on function initLayoutRes()");
        }
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    /**
     * 在此函数中初始化数据
     */
    protected abstract void initDatas();

    /**
     * 在此函数中初始化视图
     */
    protected abstract void initViews();

    /**
     * 在此函数中给出本Activity所用的布局
     */
    protected abstract int initLayoutRes();

    /**
     * 用户确认退出界面时回调的方法
     */
    protected abstract void onExit();

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 实现经典的Activity打开动画
     */
    protected void playOpenAnimation(){
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_clam);
    }
    /**
     * 实现经典的Activity退出动画
     */
    protected void playExitAnimation(){
        overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_right);
    }

    protected void showExitDialog(String title,String msg){
        if(mExitDialog==null){
            mExitDialog=new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(msg).setCancelable(false)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onExit();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
        }
        mExitDialog.show();
    }
}
