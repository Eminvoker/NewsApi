package com.invok.newsapi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.invok.newsapi.bean.Channel;

import java.util.ArrayList;
import java.util.List;

//参考http://blog.csdn.net/u013519989/article/details/52315672
//2主要是用来解决标签长度不同的问题

public class ViewPagerIndicate extends HorizontalScrollView implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private int mTotalWidth; //总宽度
    //private int mTabWidth;
    private int mTabHeight; //1个标签的宽和高 ，默认都一样长度，长度不一样的话，进行数组存储
    private int mNormalColor,mHighlightColor;//标签正常颜色和高亮颜色
    private LinearLayout mWapper; //HorizontalScrollView只能有1个子View（这里用线性布局）
    private Paint mPaint;
    private float mTranslateX; //下划线的位置
    private List<TextView> mTextViews; //每一个标签为一个TextView，要修改则对布局和布局载入相关代码进行修改
    private boolean isMeasureOk;//onMeasure是否准备完成
    private boolean isDrawOk;//是否可以绘制下划线
    private ViewPager mViewPager;


    public ViewPagerIndicate(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPaint = new Paint();
        isMeasureOk = false;
        isDrawOk = false;
    }

    /**
     * 高亮对应位置的标签颜色
     * @param pos 对应位置
     */
    public void setHighlightText(int pos){
        for (int i = 0; i < mTextViews.size(); i++) {
            if(i == pos)
                mTextViews.get(i).setTextColor(mHighlightColor);
            else
                mTextViews.get(i).setTextColor(mNormalColor);
        }
    }

    public void drawUnderline(int pos){
        //mTranslateX = pos * mTabWidth;
        mTranslateX = getPosWidth(pos);
        invalidate();
    }

    public void drawUnderline(int pos , float posOffset){
        //mTranslateX = (pos + posOffset) * mTabWidth;
        mTranslateX = getPosWidth(pos) + posOffset * mTextViews.get(pos).getMeasuredWidth();
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(isDrawOk){
            canvas.translate(mTranslateX,0);
            //canvas.drawLine(0,mTabHeight ,mTabWidth, mTabHeight,mPaint);
            canvas.drawLine(0,mTabHeight ,mTextViews.get(mViewPager.getCurrentItem()).getMeasuredWidth(), mTabHeight,mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //必须放在super.onMeasure的前面，否则后面无法获得子View的宽高
        if(isMeasureOk){
            removeAllViews();
            mWapper = new LinearLayout(mContext);
            for(TextView tv : mTextViews){
                mWapper.addView(tv);
            }
            addView(mWapper);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isMeasureOk){
            if(mTextViews.size()>0){
                mTotalWidth = mWapper.getMeasuredWidth();//getMeasuredWidth(),不是getWidth
                TextView tv = (TextView) mWapper.getChildAt(0);
               // mTabWidth = tv.getMeasuredWidth();
                mTabHeight = tv.getMeasuredHeight();
            }
            isMeasureOk = false;
        }
    }

    /**
     * 重置标签的布局样式、标题和颜色
     * @param layoutId, 布局样式id
     * @param titles 标题集合
     * @param normalColor,highlightColor 颜色
     */
    public void resetText(int layoutId, List<Channel> titles, int normalColor, int highlightColor){
        mNormalColor = normalColor;
        mHighlightColor = highlightColor;
        mTextViews = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            TextView tv = (TextView) mInflater.inflate(layoutId,null);
            tv.setTag(i);
            tv.setText(titles.get(i).getName());
            tv.setOnClickListener(this);
            mTextViews.add(tv);
        }
        setHighlightText(0);
    }

    /**
     * 重置下划线的粗细和颜色
     * @param size 下划线粗细
     * @param color 下划线颜色
     */
    public void resetUnderline(int size,int color){
        mPaint.setStrokeWidth((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, size, mContext.getResources().getDisplayMetrics()));
        mPaint.setColor(color);
    }

    public void resetViewPager(ViewPager viewPager){
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                drawUnderline(position,positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                setHighlightText(position);
                //smoothScrollTo((position-3)*mTabWidth,0);
                smoothScrollTo(getPosWidth(position-2),0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 标签点击事件监听
     */
    @Override
    public void onClick(View view) {
        int pos = (int) view.getTag();
        setHighlightText(pos);
        //scrollTo 直接定位到对应的位置 而smoothScrollTo是平滑滚动过去的
        //并且 scrollTo在惯性滚动时不可以打断 而smoothScrollTo在惯性滚动时则可以打断
        //如果想在惯性滚动时打断 并且直接定位无动画
        //smoothScrollTo(getPosWidth(pos-3),0);//前面可以显示3个标签
        smoothScrollTo(getPosWidth(pos-2),0);
        drawUnderline(pos);
        mViewPager.setCurrentItem(pos,true);//false不会调用 setPageTransformer 进入动画，导致不正常显示
    }

    /**
     * 之所以设置此方法，主要是用于网络获取数据，而不是使用静态文本；
     * 当网络请求成功，手动调用该方法完成初始化
     */
    public void setOk(){
        isDrawOk = true;
        isMeasureOk = true;
        requestLayout();
    }

    //等到前面标签的总长度
    public int getPosWidth(int pos){
        int posWidth = 0;
        if (mTextViews.size() > 0) {
            for (int i = 0; i < pos; i++) {
                posWidth += mTextViews.get(i).getMeasuredWidth();
            }
        }
        return posWidth;
    }
}
