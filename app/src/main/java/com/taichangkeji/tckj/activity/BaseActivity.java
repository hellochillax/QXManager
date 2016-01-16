package com.taichangkeji.tckj.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.utils.ScreenUtil;

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
            View view= LayoutInflater.from(this).inflate(R.layout.exit_dialog_layout, null);
            mExitDialog=new Dialog(this,R.style.exit_dialog_style);
            mExitDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            WindowManager.LayoutParams lp = mExitDialog.getWindow().getAttributes();
            lp.height=ScreenUtil.getScreenHeight(this)/2;
            lp.width=ScreenUtil.getScreenWidth(this)/2;
            mExitDialog.onWindowAttributesChanged(lp);
            mExitDialog.setContentView(view);
            final ImageView yes= (ImageView) view.findViewById(R.id.yes);
            final ImageView no= (ImageView) view.findViewById(R.id.no);
            ((TextView)view.findViewById(R.id.title)).setText(title);
            yes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        yes.setImageResource(R.mipmap.dialog_yes_2);
                    }else {
                        yes.setImageResource(R.mipmap.dialog_yes);
                    }
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExitDialog.dismiss();
                    onExit();
                }
            });
            no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        no.setImageResource(R.mipmap.dialog_no_2);
                    } else {
                        no.setImageResource(R.mipmap.dialog_no);
                    }
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExitDialog.dismiss();
                }
            });
        }
        mExitDialog.show();
    }
}
