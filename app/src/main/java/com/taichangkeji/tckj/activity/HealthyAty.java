package com.taichangkeji.tckj.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
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
import com.videogo.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.File;
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

    @OnClick(R.id.measure)
    void c_measure() {
        openMeasureAty();
    }


    @OnClick(R.id.add)
    void c_add() {
        openAddMumberAty();
    }


    @OnClick(R.id.delete)
    void c_delete() {
        showExitDialog("删除确认",null);
    }

    @Override
    protected void initDatas() {
        new MyTask().execute();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMemberList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Member member = mMemberList.get(position);
            ViewHolder holder = ViewHolder.get(context, convertView, R.layout.aty_healthy_list_item, position, parent);
            holder  .setText(R.id.name, member.getUserName())
                    .setText(R.id.relation, member.getRelations())
                    .setBgColor(member.isSelected()?"#4ce0a1":"#00000000");
            File file=new File(Config.cachePath);
            boolean isIconHasCache=false;
            for (File icon:file.listFiles()){
                if(icon.getName().equals(member.getHealthUserID()+".png")){
                    isIconHasCache=true;
                    ImageLoader.getInstance().displayImage("file:///"+Config.cachePath + "/" + icon.getName(), (ImageView) holder.getView(R.id.icon));
                    LogUtils.d(Config.cachePath + "/" + icon.getName());
                }
            }
            if(!isIconHasCache){
                LogUtils.d(Config.getUserIconPre+member.getFileFolder());
                ImageLoader.getInstance().displayImage(Config.getUserIconPre+member.getFileFolder(),(ImageView)holder.getView(R.id.icon));
            }
            return holder.getConvertView();
        }
        public void setCurrItem(int curr){
            if(mAdapter.getCount()==0){
                return;
            }
            for (Member member:mMemberList){
                member.setSelected(false);
            }
            mMemberList.get(curr).setSelected(true);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
//            case 21://left
            case 19://up
                doListViewUp();
                return true;
//            case 22://right
            case 20://down
                doListViewDown();
                return true;
//            case 66://ok
        }
        return super.onKeyDown(keyCode, event);
    }

    private int mCurrItem=-1;

    private void doListViewUp() {
        if(mAdapter.getCount()>1&&mCurrItem>0){
            mAdapter.setCurrItem(--mCurrItem);
        }
    }

    private void doListViewDown() {
        if(mAdapter.getCount()>1&&mCurrItem<mAdapter.getCount()-1){
            mAdapter.setCurrItem(++mCurrItem);
        }
    }

    @Override
    protected void initViews() {
        mMeasure.requestFocus();
        mMemberList = new ArrayList<>();
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_healthy;
    }

    @Override
    protected void onExit() {
        new DeleteTask().execute();
    }

    private void openMeasureAty() {
        Intent intent = new Intent(this, MeasureAty.class);
        if(mMemberList.size()>0){
            intent.putExtra("user",mMemberList.get(mCurrItem));
            startActivity(intent);
        }else {
            showToast("请先选择被测人");
        }

    }

    private void openAddMumberAty() {
        startActivityForResult(new Intent(this, AddMumberAty.class), 4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case 4:
                    new MyTask().execute();
                    break;
            }
        }
    }

    class DeleteTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String result = null;
            try {
                int id=mMemberList.get(mCurrItem).getHealthUserID();
                HttpURLConnection conn = (HttpURLConnection)
                        new URL(Config.delHealthUser + "FamilyID=" + UserUtils.getFamilyId(context)+"&HealthUserID="+id)
                                .openConnection();
                LogUtils.d(Config.delHealthUser + "FamilyID=" + UserUtils.getFamilyId(context)+id);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = br.readLine();
                LogUtils.d(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(o!=null&&o.toString().contains("success")){
                showToast("删除成功");
            }else {
                showToast("删除失败");
            }
        }
    }
    class MyTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String result = null;
            try {
                HttpURLConnection conn = (HttpURLConnection)
                        new URL(Config.getHealthUsers + "FamilyID=" + UserUtils.getFamilyId(context))
                                .openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = br.readLine();
                LogUtils.d(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o != null) {
                Type objectType = new TypeToken<List<Member>>() {
                }.getType();
                mMemberList.clear();
                for (Member member : (List<Member>) new Gson().fromJson(o.toString(), objectType)) {
                    mMemberList.add(member);
                }
                if(mMemberList.size()>0){
                    mAdapter.setCurrItem(mCurrItem=0);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                showToast("暂无数据");
            }
        }
    }

    @OnFocusChange(R.id.measure)
    void f_measure(View v, boolean b) {
        ImageView iv = (ImageView) v;
        if (b) {
            iv.setImageResource(R.mipmap.aty_healthy_measure_2);
        } else {
            iv.setImageResource(R.mipmap.aty_healthy_measure);
        }
    }

    @OnFocusChange(R.id.add)
    void f_add(View v, boolean b) {
        ImageView iv = (ImageView) v;
        if (b) {
            iv.setImageResource(R.mipmap.aty_healthy_add_2);
        } else {
            iv.setImageResource(R.mipmap.aty_healthy_add);
        }
    }

    @OnFocusChange(R.id.delete)
    void f_delete(View v, boolean b) {
        ImageView iv = (ImageView) v;
        if (b) {
            iv.setImageResource(R.mipmap.aty_healthy_delete_2);
        } else {
            iv.setImageResource(R.mipmap.aty_healthy_delete);
        }
    }
}
