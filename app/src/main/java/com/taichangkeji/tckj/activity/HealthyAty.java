package com.taichangkeji.tckj.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.adapter.BaseAdapter;
import com.taichangkeji.tckj.adapter.ViewHolder;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.model.Family;
import com.taichangkeji.tckj.model.Member;
import com.taichangkeji.tckj.utils.LogUtils;
import com.taichangkeji.tckj.utils.UserUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * Created by MAC on 15/12/27.
 */
public class HealthyAty extends BaseActivity {


    @Bind(R.id.measure)
    ImageView mMeasure;
    @Bind(R.id.list)
    ListView mListView;
    List<Member> mMemberList;
    MyAdapter mAdapter;

    @OnClick(R.id.measure) void c_measure(){
        openMeasureAty();
    }



    @OnClick(R.id.add) void c_add(){
        openAddMumberAty();
    }


    @OnClick(R.id.delete) void c_delete(){

    }

    @Override
    protected void initDatas() {
        new MyTask().execute();
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mMemberList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Member member=mMemberList.get(position);
            ViewHolder holder=ViewHolder.get(context,convertView,R.layout.aty_healthy_list_item,position,parent);
            holder.setImageResource(R.id.icon,R.mipmap.ic_launcher)
                    .setText(R.id.name,member.getUserName())
                    .setText(R.id.relation,member.getRelations());
            return holder.getConvertView();
        }
    }

    @Override
    protected void initViews() {
        mMemberList=new ArrayList<>();
        mAdapter=new MyAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_healthy;
    }

    @Override
    protected void onExit() {

    }

    private void openMeasureAty() {
        Intent intent=new Intent(this,MeasureAty.class);
        startActivity(intent);
    }

    private void openAddMumberAty() {
        startActivity(new Intent(this,AddMumberAty.class));
    }

    class MyTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String result=null;
            try {
                HttpURLConnection conn= (HttpURLConnection) new URL(Config.getHealthUsers+"FamilyId="+UserUtils.getFamilyId(context)).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result=br.readLine();
                LogUtils.d(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(o!=null){
                Type objectType = new TypeToken<List<Member>>() {}.getType();
                for (Member member:(List<Member>)new Gson().fromJson(o.toString(),objectType)){
                    System.out.println(member.getUserName());
                }
            }else {
                showToast("暂无数据");
            }
        }
    }


    @OnFocusChange(R.id.measure) void f_measure(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.aty_healthy_measure_2);
        }else {
            iv.setImageResource(R.mipmap.aty_healthy_measure);
        }
    }
    @OnFocusChange(R.id.add) void f_add(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.aty_healthy_add_2);
        }else {
            iv.setImageResource(R.mipmap.aty_healthy_add);
        }
    }
    @OnFocusChange(R.id.delete) void f_delete(View v,boolean b){
        ImageView iv= (ImageView) v;
        if(b){
            iv.setImageResource(R.mipmap.aty_healthy_delete_2);
        }else {
            iv.setImageResource(R.mipmap.aty_healthy_delete);
        }
    }
}
