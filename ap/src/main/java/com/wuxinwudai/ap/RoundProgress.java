package com.wuxinwudai.ap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * ChangeColorSpinProgress 可变色旋转进度显示控件，不用于显示实际进度
 * @author 吾心无待 于2016年06月24日
 */
public class RoundProgress extends View{
    //region 私有成员
    private final static String TAG = "RoundProgress";
    private int mTextColor = 0xFFFFCCC0;//字体颜色
    private int mTextSize = 20;//字体大小，sp
    private int mBarColor = 0xFF4089E8;//滚动条颜色
    private int mRingColor = 0xFFCCCCCC;//圆圈颜色
    private int mRingWidth = 5;//圆圈宽度
    private int mCircleRadius = 100;//园圈半径
    private int mProgress = 0;//进度
    //private boolean mWaveStyle;//是否使用波浪样式
    private RectF mCircleBounds = new RectF();//绘制圆圈
    private Paint mBarPaint = new Paint();
    private Paint mRingPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private OnProgressListener mOnProgressListener;
    private OnProgressCompletedListener mOnProgressCompletedListener;
    //endregion

    //region 构造函数
    /**
     * 构造函数，初始化 RoundProgress 类的一个新实例
     * @param context 上下文对象
     */
    public RoundProgress(Context context) {
        this(context,null);
    }

    /**
     * 构造函数，初始化 RoundProgress 类的一个新实例
     * @param context 上下文对象
     * @param attrs 属性列表
     */
    public RoundProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 构造函数，初始化 RoundProgress 类的一个新实例
     * @param context 上下文对象
     * @param attrs 属性列表
     * @param defStyleAttr 样式 id
     */
    public RoundProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundProgress, defStyleAttr, 0);
        parseAttributes(ta);
    }
    //endregion
    //内层半径
    private float getInnerRadius(){
        return mCircleRadius - mRingWidth / 2 ;
    }
    //转换 TypedArray
    private void parseAttributes(TypedArray ta) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRingWidth, metrics);
        mCircleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCircleRadius, metrics);
        mCircleRadius = (int) ta.getDimension(R.styleable.RoundProgress_ProgressCircleRadius, mCircleRadius);
        mRingWidth = (int) ta.getDimension(R.styleable.RoundProgress_ProgressRingWidth, mRingWidth);
        mRingColor = ta.getColor(R.styleable.RoundProgress_ProgressRingColor, mRingColor);
        mTextColor = ta.getColor(R.styleable.RoundProgress_ProgressTextColor,mTextColor);
        mTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,mTextSize,metrics);
        mTextSize = (int)ta.getDimension(R.styleable.RingProgress_ProgressTextSize,mTextSize);
        mProgress = ta.getInt(R.styleable.RoundProgress_Progress,mProgress);
        ta.recycle();
    }
    //设置画笔
    private void setupPaints() {
        mBarPaint.setColor(mBarColor);
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setStrokeWidth(mCircleRadius + mRingWidth);

        mRingPaint.setColor(mRingColor);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mRingWidth);

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

    }
    //设置绘制区域
    private void setupBounds(int layout_width, int layout_height) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        mCircleBounds = new RectF(paddingLeft + mRingWidth, paddingTop +mRingWidth,
                layout_width - paddingRight - mRingWidth, layout_height - paddingBottom - mRingWidth);
    }
    //重新绘制大小
    private void rePaint(){
        setupPaints();
        invalidate();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupBounds(w,h);
        setupPaints();
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        float radius = getInnerRadius();
        float height = mProgress * radius / 50;
        float angle = (float) (Math.acos((radius - height) / radius) * 180 / Math.PI);
        //绘制文本
        String text = mProgress + "%";
        float textWidth = mTextPaint.measureText(text);
        canvas.save();
        float textX = getPaddingLeft()+ mCircleRadius - textWidth / 2;
        float textY = getPaddingTop() + mCircleRadius - Utils.getTextBaseLineHeight(mTextPaint) / 2;
//        if (mWaveStyle){
//        }
        canvas.rotate(180, getPaddingLeft()+mCircleRadius , getPaddingTop() + mCircleRadius);
        canvas.drawArc(mCircleBounds, 270 - angle, angle * 2, false, mBarPaint);

        canvas.drawArc(mCircleBounds,0,360,false,mRingPaint);
//        if (mWaveStyle){
//            float left = getPaddingLeft()+ (radius - (float) Math.pow(height*(2 * radius-height),0.5));
//        }
        canvas.restore();
        Log.i(TAG,"textX:"+textX);
        Log.i(TAG,"textY:"+textY);
        canvas.drawText(text,textX, textY - 2,mTextPaint);
    }
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = getPaddingLeft() + getPaddingRight() + mCircleRadius * 2;
        int viewHeight = getPaddingTop() + getPaddingBottom() + mCircleRadius * 2;
        setMeasuredDimension(viewWidth,viewHeight);//只支持圆形一种模式，不知道 wrap_content，match_parent 等导致的变形
    }

    //region 公有属性

    /**
     * 获取圆半径
     * @return 圆半径
     */
    public int getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * 设置圆半径
     * @param circleRadius 圆半径
     */
    public void setCircleRadius(int circleRadius) {
        mCircleRadius = circleRadius;
    }

    /**
     * 获取字体颜色
     * @return 字体颜色
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 设置字体颜色
     * @param textColor 字体颜色
     */
    public void setTextColor(int textColor) {
        mTextColor = textColor;
        rePaint();
    }

    /**
     * 获取字体大小
     * @return 字体大小
     */
    public int getTextSize() {
        return mTextSize;
    }

    /**
     * 设置字体大小
     * @param textSize 字体大小
     */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    /**
     * 获取进度条颜色
     * @return 进度条颜色
     */
    public int getBarColor() {
        return mBarColor;
    }

    /**
     * 设置进度条颜色
     * @param barColor 进度条颜色
     */
    public void setBarColor(int barColor) {
        mBarColor = barColor;
        rePaint();
    }

    /**
     * 获取圆环颜色
     * @return 圆环颜色
     */
    public int getRingColor() {
        return mRingColor;
    }

    /**
     * 设置圆环颜色
     * @param rimColor 圆环颜色
     */
    public void setRingColor(int rimColor) {
        mRingColor = rimColor;
        rePaint();
    }

    /**
     * 获取圆环宽度
     * @return 圆环宽度
     */
    public int getRingWidth() {
        return mRingWidth;
    }

    /**
     * 设置圆环宽度
     * @param rimWidth 圆环宽度
     */
    public void setRingWidth(int rimWidth) {
        mRingWidth = rimWidth;
        rePaint();
    }

    /**
     * 设置进度前进 progress
     * @param progress 前进进度
     */
    public void progress(int progress) {
        mProgress += progress;
        if (mProgress > 100){
            mProgress = 100;
        }else if(mProgress < 0){
            mProgress = 0;
        }
        invalidate();
        if (mOnProgressListener != null){
            mOnProgressListener.progress(mProgress);
        }
        if (mProgress == 100 && mOnProgressCompletedListener != null){
            mOnProgressCompletedListener.complete();
        }
    }

    /**
     * 设置进度加 1
     */
    public void progress(){
        progress(1);
    }

    /**
     * 获取进度
     * @return 当前进度
     */
    public int getProgress(){
        return mProgress;
    }

    /**
     * 获取进度监听器
     * @return 进度监听器
     */
    public OnProgressListener getOnProgressListener() {
        return mOnProgressListener;
    }

    /**
     * 设置进度监听器
     * @param onProgressListener 进度监听器
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }

    /**
     * 获取进度完成监听器
     * @return 进度完成监听器
     */
    public OnProgressCompletedListener getOnProgressCompletedListener() {
        return mOnProgressCompletedListener;
    }

    /**
     * 设置进度完成监听器
     * @param onProgressCompletedListener 进度完成监听器
     */
    public void setOnProgressCompletedListener(OnProgressCompletedListener onProgressCompletedListener) {
        mOnProgressCompletedListener = onProgressCompletedListener;
    }
    //endregion

    //region 保存状态
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_BAR_COLOR = "bar_color";
    private static final String INSTANCE_RING_COLOR = "ring_color";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_CIRCLE_RADIUS = "circle_radius";
    private static final String INSTANCE_RING_WIDTH = "ring_width";

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_BAR_COLOR, getBarColor());
        bundle.putInt(INSTANCE_RING_COLOR, getRingColor());
        bundle.putInt(INSTANCE_CIRCLE_RADIUS, getCircleRadius());
        bundle.putInt(INSTANCE_RING_WIDTH,getRingWidth());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            mTextSize = bundle.getInt(INSTANCE_TEXT_SIZE);
            mRingColor = bundle.getInt(INSTANCE_RING_COLOR);
            mBarColor = bundle.getInt(INSTANCE_BAR_COLOR);
            mCircleRadius = bundle.getInt(INSTANCE_CIRCLE_RADIUS);
            mRingWidth = bundle.getInt(INSTANCE_RING_WIDTH);
            mProgress = bundle.getInt(INSTANCE_PROGRESS);
            setupPaints();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
    //endregion
}
