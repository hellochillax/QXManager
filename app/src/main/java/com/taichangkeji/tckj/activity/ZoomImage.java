package com.taichangkeji.tckj.activity;

import android.view.KeyEvent;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.cusview.MyImageView;
import com.videogo.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Chillax on 2015/8/14.
 *
 *   图片剪辑界面
 *
 */
public class ZoomImage extends BaseActivity {

    @Bind(R.id.iv)
    MyImageView iv;

    private PhotoViewAttacher attacher;
    private String path;


    private void initPhotoViews() {
        attacher = new PhotoViewAttacher(iv);
        attacher.setAllowParentInterceptOnEdge(true);
    }

    @Override
    protected void initDatas() {
        path = "file:///" + getIntent().getStringExtra("path");
        ImageLoader.getInstance().displayImage(path, iv);
    }

    @Override
    protected void initViews() {
        initPhotoViews();
    }

    @Override
    protected int initLayoutRes() {
        return R.layout.aty_zoom_image;
    }

    @Override
    protected void onExit() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 21://left
                return iv.moveOnKeyLeft();
            case 19://up
                return iv.moveOnKeyUp();
            case 22://right
                return iv.moveOnKeyRight();
            case 20://down
                return iv.moveOnKeyDown();
            case 66://ok
                iv.clip();
                setResult(RESULT_OK);
                onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

}
