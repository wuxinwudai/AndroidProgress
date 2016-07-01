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
import android.util.TypedValue;
import android.view.View;

/**
 * CircleProgress 为 Spin 风格的圆圈进度条
 * @author 吾心无待 于2016年06月21日
 */
public class RingProgress extends View{
    //region 构造函数
    /**
     * 构造函数，初始化 CircleProgress 类的一个新实例
     * @param context 上下文对象
     */
    public RingProgress(Context context) {
        super(context);
    }

    /**
     * 构造函数，初始化 CircleProgress 类的一个新实例
     * @param context 上下文对象
     * @param attrs 属性列表
     * @param defStyleAttr 样式
     */
    public RingProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RingProgress, defStyleAttr, 0);
        parseAttrubtes(ta);
    }
    /**
    * 构造函数，初始化 CircleProgress 类的一个新实例
    * @param context 上下文对象
    * @param attrs 属性列表
    */
    public RingProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    //endregion

    //region 私有成员
    private int mTextColor = 0xFF4089E8;//字体颜色
    private int mTextSize = 20;//字体大小，sp
    private int mBarColor = 0xFF4089E8;//滚动条颜色
    private int mBarWidth  = 8;//滚动条宽度
    private int mRingColor = 0xFFCCCCCC;//圆圈颜色
    private int mRingWidth = 5;//圆圈宽度
    private int mCircleRadius = 100;//园圈半径
    private int mProgress = 0;//进度
    private Paint mBarPaint = new Paint();
    private Paint mRingPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private RectF mCircleBounds = new RectF();
    private OnProgressListener mOnProgressListener;
    private OnProgressCompletedListener mOnProgressCompletedListener;
    //endregion

    //region 通用
    private void parseAttrubtes(TypedArray ta){
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mTextColor = ta.getColor(R.styleable.RingProgress_ProgressTextColor,mTextColor);
        mBarColor = ta.getColor(R.styleable.RingProgress_ProgressBarColor,mBarColor);
        mRingColor = ta.getColor(R.styleable.RingProgress_ProgressRingColor,mRingColor);
        mTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,mTextSize,metrics);
        mTextSize = (int)ta.getDimension(R.styleable.RingProgress_ProgressTextSize,mTextSize);
        mBarWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mBarWidth,metrics);
        mBarWidth = (int)ta.getDimension(R.styleable.RingProgress_ProgressBarWidth,mBarWidth);
        mRingWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mRingWidth,metrics);
        mRingWidth = (int)ta.getDimension(R.styleable.ChangeColorSpinProgress_ProgressRingWidth,mRingWidth);
        mCircleRadius = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mCircleRadius,metrics);
        mCircleRadius = (int)ta.getDimension(R.styleable.RingProgress_ProgressCircleRadius,mCircleRadius);
        mProgress = ta.getInt(R.styleable.RingProgress_Progress,mProgress);
        if (mRingWidth > mBarWidth){
            throw new UnsupportedOperationException("RingWidth 必须小于等于 BarWidth");
        }
        ta.recycle();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupBounds(w, h);
        setupPaints();
        invalidate();
    }

    //设置画笔
    private void setupPaints() {
        mBarPaint.setColor(mBarColor);
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStrokeWidth(mBarWidth);

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
        mCircleBounds = new RectF(paddingLeft + mBarWidth, paddingTop +mBarWidth,
                layout_width - paddingRight - mBarWidth, layout_height - paddingBottom - mBarWidth);
    }
    //endregion
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mCircleBounds, 360, 360, false, mRingPaint);//圆环始终不变
        String text = mProgress + "%";
        float textWidth = mTextPaint.measureText(text);
        //绘制文本
        float textX = getPaddingLeft()+ mCircleRadius - textWidth / 2;
        float textY = getPaddingTop() + mCircleRadius - Utils.getTextBaseLineHeight(mTextPaint) / 2;
        canvas.drawText(text,textX, textY - 2,mTextPaint);
        if (isInEditMode()) {
            canvas.drawArc(mCircleBounds,0, 270, false, mBarPaint);
        }else{
            canvas.drawArc(mCircleBounds, 0, mProgress * 3.6f, false, mBarPaint);
        }
        invalidate();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = getPaddingLeft() + getPaddingRight() + mCircleRadius * 2;
        int viewHeight = getPaddingTop() + getPaddingBottom() + mCircleRadius * 2;
        setMeasuredDimension(viewWidth,viewHeight);//只支持圆形一种模式，不知道 wrap_content，match_parent 等导致的变形
    }
    //重新绘制
    private void rePaint(){
        setupPaints();
        invalidate();
    }
    //region 公有属性

    /**
     * 获取文本颜色
     * @return 文本颜色
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 设置文本颜色
     * @param textColor 文本颜色
     */
    public void setTextColor(int textColor) {
        mTextColor = textColor;
        rePaint();
    }

    /**
     * 获取文本字体大小
     * @return 文本字体大小
     */
    public int getTextSize() {
        return mTextSize;
    }

    /**
     * 设置文本字体大小
     * @param textSize 文本字体大小
     */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
        rePaint();
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
     * 获取进度条宽度
     * @return 进度条宽度
     */
    public int getBarWidth() {
        return mBarWidth;
    }

    /**
     * 设置进度条宽度
     * @param barWidth 进度条宽度
     */
    public void setBarWidth(int barWidth) {
        mBarWidth = barWidth;
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
        if (mRingWidth > mBarWidth){
            throw new UnsupportedOperationException("RingWidth 必须小于等于 BarWidth");
        }
        rePaint();
    }

    /**
     * 获取圆环半径
     * @return 圆环半径
     */
    public int getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * 设置圆环半径
     * @param circleRadius 圆环半径
     */
    public void setCircleRadius(int circleRadius) {
        mCircleRadius = circleRadius;
        rePaint();
    }

    /**
     * 获取当前进度
     * @return 获取进度
     */
    public int getProgress() {
        return mProgress;
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
        if (mProgress==100 && mOnProgressCompletedListener != null){
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
    private static final String INSTANCE_BAR_WIDTH = "bar_width";
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
        bundle.putInt(INSTANCE_BAR_WIDTH,getBarWidth());
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
            mBarWidth = bundle.getInt(INSTANCE_BAR_WIDTH);
            mProgress = bundle.getInt(INSTANCE_PROGRESS);
            setupPaints();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
    //endregion
}
