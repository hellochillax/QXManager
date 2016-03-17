package com.taichangkeji.tckj.adapter;

/**
 * Created by MAC on 15/12/1.
 *
 * 系统自带的BaseAdapter的进一步封装
 */
public abstract class BaseAdapter extends android.widget.BaseAdapter {

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
