package com.invok.newsapi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/10/12.
 */
public class CircleImageView extends ImageView{

    //基本的三个构造函数
    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        //空值判断，必要步骤，避免由于没有设置src导致的异常错误
        if(drawable == null){
            return;
        }
        //必要步骤，避免由于初始化之前导致的异常错误
        if(getWidth() == 0 || getHeight() == 0){
            return;
        }
        if(!(drawable instanceof BitmapDrawable)){
            return;
        }
        Bitmap b= ((BitmapDrawable)drawable).getBitmap();
        if(b == null){
            return;
        }

        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888,true);
        int radius = getWidth() < getHeight() ? getWidth() : getHeight();//取高和宽之间小的
        Bitmap roundBitmap = getCroppedBitmap(bitmap,radius);
        canvas.drawBitmap(roundBitmap,0,0,null);
    }

    /**
     * 初始Bitmap对象的缩放裁剪过程
     * @param bmp        初始Bitmap对象
     * @param radius    圆形图片直径大小
     * @return 返回一个圆形的缩放裁剪过后的Bitmap对象
     */
    private Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius ||bmp.getHeight() != radius){
            //可以根据原来的位图创建一个新的位图。API中是这样解释的： Creates a new bitmap, scaled from an existing bitmap.
            /*
            src	The source bitmap.
            dstWidth	The new bitmap's desired width.
            dstHeight	The new bitmap's desired height.
            filter	true if the source should be filtered. 设置为true不失真*/

            sbmp = Bitmap.createScaledBitmap(bmp,radius,radius,false);
        }else{
            sbmp = bmp;
        }
        //创建画布
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0,0,0,0);//背景透明

        Paint paint = new Paint();
        //第一个函数是用来防止边缘的锯齿，第二个函数是用来对位图进行滤波处理,第三个防抖动
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#FFE4C4"));

        canvas.drawCircle(sbmp.getWidth()/2,sbmp.getHeight()/2,sbmp.getWidth()/2,paint);
        //核心部分，设置两张图片的相交模式，在这里就是上面绘制的Circle和下面绘制的Bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//群英传P146
        //矩形 (left, top, right bottom)
        Rect rect = new Rect(0,0,sbmp.getWidth(),sbmp.getHeight());

        /*对图片剪接和限定显示区域
        drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint)；
        Rect src: 是对图片进行裁截，若是空null则显示整个图片
        RectF dst：是图片在Canvas画布中显示的区域，大于src则把src的裁截区放大，小于src则把src的裁截区缩小*/
        canvas.drawBitmap(sbmp,rect,rect,paint);
        return output;
    }

}
