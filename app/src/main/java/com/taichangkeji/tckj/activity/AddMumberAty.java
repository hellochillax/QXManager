package com.taichangkeji.tckj.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
import com.taichangkeji.tckj.model.Member;
import com.taichangkeji.tckj.model.Relation;
import com.taichangkeji.tckj.utils.CommonUtils;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UploadUtil;
import com.taichangkeji.tckj.utils.UserUtils;
import com.videogo.universalimageloader.core.ImageLoader;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * Created by MAC on 16/1/14.
 */
public class AddMumberAty extends BaseActivity {

    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.name)
    EditText mName;
    @Bind(R.id.sex)
    EditText mSex;
    @Bind(R.id.age)
    EditText mAge;
    boolean hasSetIcon=false;
    List<Relation> relations;

    @Bind(R.id.spinner)
    Spinner mSpinner;

    @OnClick(R.id.icon)
    void c_icon() {
        openPictureSelecter();
    }
    ArrayAdapter<String> adapter;

    @OnClick(R.id.submit)
    void c_submit() {
        String name = mName.getText().toString();
        String sex = mSex.getText().toString();
        String age = mAge.getText().toString();
        String relation = relations.get(mSpinner.getSelectedItemPosition()).getRelationsID();
        if (!hasSetIcon) {
            showToast("用户头像不能为空");
        } else if (TextUtils.isEmpty(name)) {
            showToast("名字不能为空");
        } else if (TextUtils.isEmpty(sex)) {
            showToast("性别不能为空");
        } else if (TextUtils.isEmpty(age)) {
            showToast("年龄不能为空");
        } else if (TextUtils.isEmpty(relation)) {
            showToast("关系不能为空");
        } else {
            new MyTask(new Member(name, relation, sex, Integer.valueOf(age))).execute();
        }
    }


    @Override
    protected void initDatas() {

    }

    @Override
    protected void initViews() {
        adapter=new ArrayAdapter< String>(this,android.R.layout.simple_spinner_item);
        mSpinner.setAdapter(adapter);
        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.POST,Config.getRelations, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type type=new TypeToken<List<Relation>>(){}.getType();
                relations=new Gson().fromJson(response, type);
                LogUtils.d(relations.toString());
                for (Relation relation:relations){
                    adapter.add(relation.getRelationsName());
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("网络错误");
            }
        }));
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_add_mumber;
    }

    @Override
    protected void onExit() {

    }


    class MyTask extends AsyncTask {

        private Member member;

        public MyTask(Member member) {
            this.member = member;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            String url=Config.addHealthUser + "FamilyID=" + UserUtils.getUserId(context) + "&" + member.toString();
            String result=UploadUtil.uploadFile(new File(Config.iconCache),url);
            LogUtils.d(url);
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.toString().contains("success")) {
                showToast("联系人添加成功");
                Matcher matcher= Pattern.compile("HealthUserID\":\"(\\d*)").matcher(o.toString());
                File file=new File(Config.iconCache);
                if(matcher.find()){
                    String id=matcher.group(1);
                    LogUtils.d(id);
                    file.renameTo(new File(Config.cachePath+"/"+id+".png"));
                }
                onBackPressed();

            } else {
                showToast("联系人添加失败");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    startPhotoZoom(CommonUtils.getPath(context, data.getData()));
                    break;
                case 8:
                    hasSetIcon=true;
                    ImageLoader.getInstance().displayImage("file:///" + Config.iconCache, mIcon);
                    break;
            }
        }
    }

    private void startPhotoZoom(String path) {
        Intent intent = new Intent(this, ZoomImage.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, 8);
    }

    private void openPictureSelecter() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
//根据版本号不同使用不同的Action
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        startActivityForResult(intent, 2);
    }

    @OnFocusChange(R.id.submit)
    void f_submit(View v, boolean b) {
        ImageView iv = (ImageView) v;
        if (b) {
            iv.setImageResource(R.mipmap.ok_2);
        } else {
            iv.setImageResource(R.mipmap.ok);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
