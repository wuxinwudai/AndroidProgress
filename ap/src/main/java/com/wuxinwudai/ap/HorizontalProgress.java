package com.wuxinwudai.ap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * HorizontalProgress 水平进度条
 * @author 吾心无待 于2016年6月21日
 */
public class HorizontalProgress extends View {
    //region 受保护成员
    protected float mTextSize = 12;//字体大小，单位 sp
    protected int mTextColor = 0xFF4889E8;//字体颜色
    protected float mTextOffset = 2;//字的左右间距，单位 sp
    protected int mCompletedColor = 0xFF00FF00;//完成部分颜色
    protected int mUnCompletedColor = 0xFFFFCCC0;//未完成部分颜色
    protected float mCompletedHeight = 2.5f;//完成部分高度 dp
    protected float mUnCompletedHeight = 2;//未完成部分高度 dp
    protected int mProgress = 0;//当前进度
    protected Paint mTextPaint = new Paint();//文本画笔
    protected Paint mCompletedPaint = new Paint();//已完成进度条画笔
    protected Paint mUnCompletedPaint = new Paint();//未完成进度条画笔
    private final int MinWidth = 100;//最小宽度
    private OnProgressCompletedListener mOnProgressCompletedListener;
    private OnProgressListener mOnProgressListener;
    //endregion

    //region 构造函数

    /**
     * 构造函数，初始化 HorizontalProgress 类的一个新实例
     * @param context 上下文对象
     * @param attrs 属性列表
     */
    public HorizontalProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    /**
     * 构造函数，初始化 HorizontalProgress 类的一个新实例
     * @param context 上下文对象
     */
    public HorizontalProgress(Context context) {
        this(context,null);
    }
    /**
     * 构造函数，初始化 HorizontalProgress 类的一个新实例
     * @param context 上下文对象
     * @param attrs 属性列表
     * @param defStyleAttr 默认样式
     */
    public HorizontalProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalProgress, defStyleAttr, 0);
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mProgress = ta.getInt(R.styleable.HorizontalProgress_Progress,mProgress);
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, metrics);
        mTextSize = ta.getDimension(R.styleable.HorizontalProgress_ProgressTextSize,mTextSize);
        mTextOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextOffset, metrics);
        mTextOffset = ta.getDimension(R.styleable.HorizontalProgress_ProgressTextOffset,mTextOffset);
        mCompletedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCompletedHeight, metrics);
        mCompletedHeight = ta.getDimension(R.styleable.HorizontalProgress_ProgressCompletedHeight,mCompletedHeight);
        mUnCompletedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mUnCompletedHeight, metrics);
        mUnCompletedHeight = ta.getDimension(R.styleable.HorizontalProgress_ProgressUnCompletedHeight,mUnCompletedHeight);
        mTextColor = ta.getColor(R.styleable.HorizontalProgress_ProgressTextColor,mTextColor);
        mCompletedColor = ta.getColor(R.styleable.HorizontalProgress_ProgressCompletedColor,mCompletedColor);
        mUnCompletedColor = ta.getColor(R.styleable.HorizontalProgress_ProgressUnCompletedColor,mUnCompletedColor);
        ta.recycle();
    }
    //endregion

    //region 公有属性


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
     * 获取当前进度
     * @return 当前进度
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * 设置当前进度前进
     * @param progress 前进数
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
     * 进度进 1
     */
    public void progress(){
        progress(1);
    }

    /**
     * 获取文本大小
     * @return 文本大小，sp 为单位
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * 设置文本大小
     * @param mTextSize 文本大小，sp 为单位
     */
    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        rePaint();
    }

    /**
     * 获取文本颜色
     * @return 文本颜色
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 设置文本颜色
     * @param mTextColor 文本颜色
     */
    public void setTextColor(@ColorInt int mTextColor) {
        this.mTextColor = mTextColor;
        rePaint();
    }

    /**
     * 获取文本的偏移量，左右均为这个值
     * @return 文本偏移量，左右均为这个值
     */
    public float getTextOffset() {
        return mTextOffset;
    }

    /**
     * 设置文本偏移量，左右均为这个值
     * @param mTextOffset 文本偏移量，左右均为这个值
     */
    public void setTextOffset(float mTextOffset) {
        this.mTextOffset = mTextOffset;
        invalidate();
    }

    /**
     * 获取已完成部分进度条颜色
     * @return 已完成部分进度条颜色
     */
    public int getCompletedColor() {
        return mCompletedColor;
    }

    /**
     * 设置已完成部分进度条颜色
     * @param mCompletedColor 已完成部分进度条颜色
     */
    public void setCompletedColor(@ColorInt int mCompletedColor) {
        this.mCompletedColor = mCompletedColor;
        rePaint();
    }

    /**
     * 获取未完成部分进度条颜色
     * @return 未完成部分进度条颜色
     */
    public int getUnCompletedColor() {
        return mUnCompletedColor;
    }
    /**
     * 设置未完成部分进度条颜色
     * @param mUnCompletedColor 未完成部分进度条颜色
     */
    public void setUnCompletedColor(@ColorInt int mUnCompletedColor) {
        this.mUnCompletedColor = mUnCompletedColor;
        rePaint();
    }

    /**
     * 获取完成部分进度条高度
     * @return 完成部分进度条高度
     */
    public float getCompletedHeight() {
        return mCompletedHeight;
    }

    /**
     * 设置未完成部分进度条高度
     * @param mCompletedHeight 未完成进度条高度
     */
    public void setCompletedHeight(float mCompletedHeight) {
        this.mCompletedHeight = mCompletedHeight;
        rePaint();
    }

    /**
     * 获取未完成部分高度
     * @return 未完成部分进度条高度
     */
    public float getUnCompletedHeight() {
        return mUnCompletedHeight;
    }

    /**
     * 设置未完成部分进度条高度
     * @param mUnCompletedHeight 未完成部分进度条高度
     */
    public void setUnCompletedHeight(float mUnCompletedHeight) {
        this.mUnCompletedHeight = mUnCompletedHeight;
        rePaint();
    }
    //endregion


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupPaints();
        invalidate();
    }
    //设置画笔
    private void setupPaints() {
        mCompletedPaint.setColor(mCompletedColor);
        mCompletedPaint.setStyle(Paint.Style.STROKE);
        mCompletedPaint.setStrokeWidth(mCompletedHeight);

        mUnCompletedPaint.setColor(mUnCompletedColor);
        mUnCompletedPaint.setStyle(Paint.Style.STROKE);
        mUnCompletedPaint.setStrokeWidth(mUnCompletedHeight);

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
    }
    //重新绘制
    private void rePaint(){
        setupPaints();
        invalidate();
    }
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = (int)(mTextOffset + (int)mTextPaint.measureText("99%") + this.getPaddingLeft() + this.getPaddingRight()) + MinWidth;//获取模糊最大宽度
        int viewHeight = (int)(Math.max(Math.max(mCompletedHeight,mUnCompletedHeight),Utils.getTextHeight(mTextPaint)) + this.getPaddingTop() + this.getPaddingBottom());//获取模糊最大高度
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(viewWidth, widthSize);
        } else {
            width = viewWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(viewHeight, heightSize);

        } else {
            height = viewHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getPaddingLeft(),(getHeight()-getPaddingBottom() + getPaddingTop())/2);
        float progressWidth = getWidth() - getPaddingLeft() -getPaddingRight();//进度条宽度
        String text = getProgress() + "%";
        float textWidth = mTextPaint.measureText(text);
        float textY = 8;
        if(mProgress == 0){//无需绘制完成部分
            canvas.drawText(text,0,textY,mTextPaint);//绘制文本
            canvas.drawLine(textWidth + mTextOffset,0,progressWidth,0,mUnCompletedPaint);//此时completedWidth固定,反向绘制
        }else if(mProgress < 100){//政策绘制
            float lineWidth = progressWidth - textWidth - 2 * mTextOffset;
            float completedWidth = mProgress * lineWidth / 100f;
            canvas.drawLine(0,0,completedWidth,0,mCompletedPaint);
            canvas.drawText(text,completedWidth + mTextOffset,textY,mTextPaint);//绘制文本
            canvas.drawLine(completedWidth + textWidth + mTextOffset * 2,0,progressWidth,0,mUnCompletedPaint);
        }else{//无需绘制未完进度条
            canvas.drawLine(0,0,progressWidth - textWidth - mTextOffset,0,mCompletedPaint);//此时completedWidth固定,反向绘制
            canvas.drawText(text,progressWidth - textWidth,textY,mTextPaint);//绘制文本
        }
        invalidate();
    }

    //region 保存状态
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_COMPLETED_COLOR = "completed_color";
    private static final String INSTANCE_UNCOMPLETED_COLOR = "unCompleted_color";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_COMPLETED_HEIGHT = "completed_height";
    private static final String INSTANCE_UNCOMPLETED_HEIGHT = "uncompleted_height";

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_COMPLETED_COLOR, getCompletedColor());
        bundle.putInt(INSTANCE_UNCOMPLETED_COLOR, getUnCompletedColor());
        bundle.putFloat(INSTANCE_COMPLETED_HEIGHT, getCompletedHeight());
        bundle.putFloat(INSTANCE_UNCOMPLETED_HEIGHT,getUnCompletedHeight());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            mCompletedColor = bundle.getInt(INSTANCE_COMPLETED_COLOR);
            mUnCompletedColor = bundle.getInt(INSTANCE_UNCOMPLETED_COLOR);
            mCompletedHeight = bundle.getFloat(INSTANCE_COMPLETED_HEIGHT);
            mUnCompletedHeight = bundle.getFloat(INSTANCE_UNCOMPLETED_HEIGHT);
            mProgress = bundle.getInt(INSTANCE_PROGRESS);
            setupPaints();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
    //endregion
}
