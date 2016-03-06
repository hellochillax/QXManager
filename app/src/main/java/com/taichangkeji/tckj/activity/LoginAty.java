package com.taichangkeji.tckj.activity;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by MAC on 16/3/6.
 */
public class LoginAty extends BaseActivity {

    @Bind(R.id.login_id)
    EditText mLoginId;
    @Bind(R.id.login_ok)
    Button mLoginOk;

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {

    }

    @OnClick(R.id.login_ok)
    void to_login(){
        String phone=mLoginId.getText().toString();
        if(TextUtils.isEmpty(phone)){
            showToast("账号不能为空");
        }else if(!phone.matches(Config.phoneRegular)){
            showToast("请输入正确的手机号");
        }else {
            loginToGetIds();
        }
    }

    private void loginToGetIds() {
        new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] params) {

                return null;
            }
        }.execute();
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_login;
    }

    @Override
    protected void onExit() {

    }

}
