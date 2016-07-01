package com.wuxinwudai.ap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * ChangeColorSpinProgress 可变色旋转进度显示控件，不用于显示实际进度
 * @author 吾心无待 于2016年06月24日
 */
public class ArcProgress extends View{
    //region 构造函数

    /**
     * 构造函数，初始化 ArcProgress 类的一个新实例
     * @param context 上下文对象
     */
    public ArcProgress(Context context) {
        super(context);
    }

    /**
     * 构造函数，初始化 ArcProgress 类的一个新实例
     * @param context 上下文对象
     * @param attrs 属性列表
     * @param defStyleAttr 默认样式
     */
    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
        parseAttributes(ta);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    //endregion
    //region 私有成员
    private final static String TAG = "ArcProgress";
    private int mTextColor = 0xFF4089E8;//字体颜色
    private int mTitleTextColor = 0xFF4089E8;//标题字体颜色
    private int mTextSize = 32;//字体大小，sp
    private int mTitleTextSize = 26;//标题字体大小
    private int mBarColor = 0xFF4089E8;//滚动条颜色
    private int mRingColor = 0xFFCCCCCC;//圆圈颜色
    private int mRingWidth = 8;//圆圈宽度
    private int mCircleRadius = 100;//园圈半径
    private int mProgress = 0;//进度
    private int mArcAngle = 300;//圆环角度
    private int mStartAngle = 0;//开始绘制的角度
    private float mArcBottomHeight = 0;
    private String mTitle = "进度";//
    private RectF mCircleBounds = new RectF();//绘制圆圈
    private Paint mBarPaint = new Paint();
    private Paint mRingPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private Paint mTitlePaint = new Paint();
    private OnProgressListener mOnProgressListener;
    private OnProgressCompletedListener mOnProgressCompletedListener;
    //endregion
    //region 公有属性

    /**
     * 获取绘制的角度
     * @return 绘制的角度
     */
    public int getArcAngle(){
        return mArcAngle;
    }

    /**
     * 设置绘制的角度
     * @param arcAngle 绘制的角度
     */
    public void setArcAngle(int arcAngle){
        mArcAngle = arcAngle;
        rePaint();
    }

    /**
     * 获取进度文本颜色
     * @return 文本颜色
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 设置进度文本颜色
     * @param textColor 文本颜色
     */
    public void setTextColor(int textColor) {
        mTextColor = textColor;
        rePaint();
    }

    /**
     * 获取标题文本颜色
     * @return 标题文本颜色
     */
    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    /**
     * 设置标题文本颜色
     * @param titleTextColor 标题文本颜色
     */
    public void setTitleTextColor(int titleTextColor) {
        mTitleTextColor = titleTextColor;
        rePaint();
    }

    /**
     * 获取进度文本字体大小
     * @return 进度文本字体大小
     */
    public int getTextSize() {
        return mTextSize;
    }

    /**
     * 设置进度文本字体大小
     * @param textSize 进度文本字体大小
     */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
        rePaint();
    }

    /**
     * 获取标题文本大小
     * @return 标题文本大小
     */
    public int getTitleTextSize() {
        return mTitleTextSize;
    }

    /**
     * 设置标题文本字体大小
     * @param titleTextSize 标题文本字体大小
     */
    public void setTitleTextSize(int titleTextSize) {
        mTitleTextSize = titleTextSize;
        rePaint();
    }

    /**
     * 获取已完成部分进度条颜色
     * @return 已完成部分进度条颜色
     */
    public int getBarColor() {
        return mBarColor;
    }

    /**
     * 设置已完成部分进度条颜色
     * @param barColor 已完成部分进度条颜色
     */
    public void setBarColor(int barColor) {
        mBarColor = barColor;
        rePaint();
    }

    /**
     * 获取未完成部分进度条颜色
     * @return 未完成部分进度条颜色
     */
    public int getRingColor() {
        return mRingColor;
    }

    /**
     * 设置未完成部分进度条颜色
     * @param ringColor 未完成部分进度条颜色
     */
    public void setRingColor(int ringColor) {
        mRingColor = ringColor;
        rePaint();
    }

    /**
     * 获取进度条宽度
     * @return 进度条宽度
     */
    public int getRingWidth() {
        return mRingWidth;
    }

    /**
     * 设置进度条宽度
     * @param ringWidth 进度条宽度
     */
    public void setRingWidth(int ringWidth) {
        mRingWidth = ringWidth;
        rePaint();
    }

    /**
     * 获取圆环半径
     * @return  圆环半径
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
     * 获取进度条标题
     * @return 进度条标题
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * 设置进度条标题
     * @param title 标题
     */
    public void setTitle(String title) {
        mTitle = title;
        invalidate();
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
    //region 其他方法
    //转换 TypedArray
    private void parseAttributes(TypedArray ta) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRingWidth, metrics);
        mCircleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCircleRadius, metrics);
        mCircleRadius = (int) ta.getDimension(R.styleable.ArcProgress_ProgressCircleRadius, mCircleRadius);
        mRingWidth = (int) ta.getDimension(R.styleable.ArcProgress_ProgressRingWidth, mRingWidth);
        mRingColor = ta.getColor(R.styleable.ArcProgress_ProgressRingColor, mRingColor);
        mTextColor = ta.getColor(R.styleable.ArcProgress_ProgressTextColor,mTextColor);
        mTitleTextColor = ta.getColor(R.styleable.ArcProgress_TitleTextColor,mTitleTextColor);
        mTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,mTextSize,metrics);
        mTextSize = (int)ta.getDimension(R.styleable.ArcProgress_ProgressTextSize,mTextSize);
        mTitleTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,mTitleTextSize,metrics);
        mTitleTextSize = (int)ta.getDimension(R.styleable.ArcProgress_TitleTextSize,mTitleTextSize);
        mProgress = ta.getInt(R.styleable.ArcProgress_Progress,mProgress);
        mTitle = ta.getString(R.styleable.ArcProgress_Title);
        mArcAngle = ta.getInt(R.styleable.ArcProgress_ArcAngle,mArcAngle);
        ta.recycle();
    }
    //设置画笔
    private void setupPaints() {
        mBarPaint.setColor(mBarColor);
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStrokeWidth(mRingWidth);

        mRingPaint.setColor(mRingColor);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mRingWidth);

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        mTitlePaint.setColor(mTitleTextColor);
        mTitlePaint.setTextSize(mTitleTextSize);
        mTitlePaint.setAntiAlias(true);
    }
    //设置进度条绘制区域
    private void setupBounds(int layout_width, int layout_height) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        mCircleBounds = new RectF(paddingLeft + mRingWidth, paddingTop + mRingWidth,
                layout_width - paddingRight - mRingWidth, layout_height - paddingBottom - mRingWidth);
        float angle = (360 - mArcAngle)/2f;
        mArcBottomHeight = mCircleRadius * (float)(1-Math.cos(angle / 180 * Math.PI));
    }
    //重新绘制
    private void rePaint(){
        setupPaints();
        invalidate();
    }
    //endregion
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupBounds(w, h);
        setupPaints();
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        float progressAngle = mProgress * 300 / 100f;
        mStartAngle = 270 - mArcAngle / 2;
        canvas.drawArc(mCircleBounds, mStartAngle, mArcAngle, false, mRingPaint);
        canvas.drawArc(mCircleBounds, mStartAngle, progressAngle, false, mBarPaint);
        //绘制文本
        String text = mProgress + "%";
        float textWidth = mTextPaint.measureText(text);
        float textX = getPaddingLeft()+ mCircleRadius - textWidth / 2;
        float textY = getPaddingTop() + mCircleRadius - Utils.getTextBaseLineHeight(mTextPaint) / 2;
        canvas.drawText(text,textX, textY - 2,mTextPaint);
        if (isInEditMode()){
            mTitle = "ArcProgress";
        }
        if (!TextUtils.isEmpty(mTitle)){
            float titleX = getPaddingLeft() + mCircleRadius - mTitlePaint.measureText(mTitle) / 2;
            float titleY = getPaddingTop()+ mCircleRadius * 2 - mArcBottomHeight - Utils.getTextBaseLineHeight(mTitlePaint) /2;
            canvas.drawText(mTitle,titleX,titleY,mTitlePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = getPaddingLeft() + getPaddingRight() + mCircleRadius * 2;
        int viewHeight = getPaddingTop() + getPaddingBottom() + mCircleRadius * 2;
        setMeasuredDimension(viewWidth,viewHeight);//只支持圆形一种模式，不知道 wrap_content，match_parent 等导致的变形
        float angle = (360 - mArcAngle) / 2f;
        mArcBottomHeight = mCircleRadius * (float)(1-Math.cos(angle / 180 * Math.PI));
    }

    //region 保存状态
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_BAR_COLOR = "bar_color";
    private static final String INSTANCE_RING_COLOR = "ring_color";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_CIRCLE_RADIUS = "circle_radius";
    private static final String INSTANCE_RING_WIDTH = "ring_width";
    private static final String INSTANCE_TITLE= "title";
    private static final String INSTANCE_TITLE_TEXT_SIZE = "title_text_size";
    private static final String INSTANCE_TITLE_TEXT_COLOR = "title_text_color";
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
        bundle.putString(INSTANCE_TITLE,getTitle());
        bundle.putInt(INSTANCE_TITLE_TEXT_SIZE,getTitleTextSize());
        bundle.putInt(INSTANCE_TITLE_TEXT_COLOR,getTextColor());
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
            mTitle = bundle.getString(INSTANCE_TITLE);
            mTitleTextSize = bundle.getInt(INSTANCE_TITLE_TEXT_SIZE);
            mTitleTextColor = bundle.getInt(INSTANCE_TITLE_TEXT_COLOR);
            setupPaints();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
    //endregion
}
