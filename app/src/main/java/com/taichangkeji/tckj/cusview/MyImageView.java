package com.taichangkeji.tckj.cusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.taichangkeji.tckj.R;
import com.taichangkeji.tckj.config.Config;
import com.taichangkeji.tckj.utils.CommonUtils;
import com.taichangkeji.tckj.utils.ScreenUtil;

/**
 * Created by Chillax on 2015/8/14.
 */
public class MyImageView extends ImageView {


    public boolean moveOnKeyLeft() {
        if(left>options.offset){
            left-=options.offset;
            right-=options.offset;
            invalidate();
            return true;
        }
        return false;
    }
    public boolean moveOnKeyRight() {
        if(right+options.offset<width){
            right+=options.offset;
            left+=options.offset;
            invalidate();
            return true;
        }
        return false;
    }
    public boolean moveOnKeyUp() {
        if(top>options.offset){
            top-=options.offset;
            bottom-=options.offset;
            invalidate();
            return true;
        }
        return false;
    }
    public boolean moveOnKeyDown() {
        if(bottom+options.offset<height){
            bottom+=options.offset;
            top+=options.offset;
            invalidate();
            return true;
        }
        return false;
    }

    public class Options {
        public int stroke = 2;//描边的宽度
        public int size ;//截图的大小(边长)
        public int color = Color.parseColor("#00ff00");
        public int offset=20;//每次平移的大小
    }
    private Bitmap result;
    int width ;
    int height ;
    private Paint paint;
    private Options options = new Options();
    public Options getOptions(){
        return options;
    }
    public void clip(){
        clip=true;
        invalidate();
        clip2();
    }

    private void clip2(){
        setDrawingCacheEnabled(true);
        Bitmap bp=getDrawingCache();
//        System.out.println((width-options.width)/2);
//        System.out.println((height - options.height) / 2);
//        System.out.println(options.width+"L"+options.height);
        result=Bitmap.createBitmap(bp,left,top,options.size,options.size);
        setDrawingCacheEnabled(false);
        try {
            CommonUtils.writeBitmap2File(result,Config.iconCache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean clip=false;
    int left,top,right,bottom;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!clip){
            canvas.drawRect( left,top,right,bottom, paint);
        }

    }

    private Context context;
    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        options.size= ScreenUtil.dp2px(context, 400);//
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.MyImageView);
        options.color=Color.parseColor(ta.getString(R.styleable.MyImageView_color_));
        options.stroke=(int)ta.getDimension(R.styleable.MyImageView_stroke,4);
        ta.recycle();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(options.stroke);
        paint.setColor(options.color);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        width=ScreenUtil.getScreenWidth(context);
        height=ScreenUtil.getScreenHeight(context);
        options.size=height/2;
        //绘制方形框
        left=(width-options.size)/2;
        top=(height-options.size)/2;
        right=(width+options.size)/2;
        bottom=(height+options.size)/2;
    }
}
