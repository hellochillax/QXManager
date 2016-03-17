package com.taichangkeji.tckj.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.model.Equipment;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UserUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by MAC on 16/3/6.
 *
 * 首次使用时出现的登录界面
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
            loginToGetIds(phone);
        }
    }

    private void loginToGetIds(final String phone) {
        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.POST, Config.getIDsAndLogin+"LoginName="+phone, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d(response);
                if(response.contains("success")){
                    String str=phone;
                    UserUtils.setUserId(context,str);
                    Pattern p=Pattern.compile("data\":(.*)\\}");
                    Matcher m=p.matcher(response);
                    if(m.find()){
                        Type type = new TypeToken<List<Equipment>>(){}.getType();
                        List<Equipment> list=new Gson().fromJson(m.group(1), type);
                        LogUtils.d(list.toString());
                        for (Equipment e:list){
                            if(e.getComment().equals("网络摄像头")){
                                UserUtils.setCameraId(context,e.getSRNo());
                            }
                        }
                    }
                    openMainAty();
                }else {
                    showToast("账号错误");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d(error.getMessage());
            }
        }));
    }

    private void openMainAty() {
        startActivity(new Intent(context,MainActivity.class));
        onBackPressed();
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_login;
    }

    @Override
    protected void onExit() {

    }

}
