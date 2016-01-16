package com.taichangkeji.tckj.cusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.taichangkeji.tckj.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MAC on 16/1/16.
 */
public class MeasureIndicator extends LinearLayout {

    @Bind(R.id.pointer)
    View mPointer;
    private Context mContext;
    private View mRootView;
    private float mLeftCount;
    private float mRightCount;
    private float mOffset;  //一个平移单位对应的像素数
    private float mWidth;   //屏幕宽度
    private Scroller mScroller;

    public MeasureIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScroller=new Scroller(context);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MeasureIndicator);
        mLeftCount = ta.getFloat(R.styleable.MeasureIndicator_left_max_count, 100);
        mRightCount = ta.getFloat(R.styleable.MeasureIndicator_right_max_count, 100);
        if(mLeftCount<0)mLeftCount=100;
        if(mRightCount<0)mRightCount=100;
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setOrientation(VERTICAL);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.measure_indicator, null);
        addView(mRootView);
        ButterKnife.bind(this);
        mWidth=getWidth();
        mOffset=mWidth/(mLeftCount+mRightCount);
    }


    public void setLeftCount(float count){
        if(count<0){
            return;
        }
        smoothlyScrollTo((int) (mWidth/2-mOffset*count));
    }

    public void setRightCount(float count){
        if(count<0){
            return;
        }
        smoothlyScrollTo((int) (mWidth/2+mOffset*count));
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            mPointer.setTranslationX(mScroller.getCurrX());
            postInvalidate();
        }
    }

    private void smoothlyScrollTo(int offset){
        int x= (int) mPointer.getX();
        int y= (int) mPointer.getY();
        mScroller.startScroll(x,y,offset-x,0);
    }

}
