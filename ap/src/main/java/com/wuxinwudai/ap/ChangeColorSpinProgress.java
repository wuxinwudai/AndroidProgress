package com.wuxinwudai.ap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;


/**
 * ChangeColorSpinProgress 可变色旋转进度显示控件，不用于显示实际进度
 * @author 吾心无待 于2016年06月21日
 */
public class ChangeColorSpinProgress extends View {
    //region 构造函数

    /**
     * 构造函数，初始化 ChangeColorSpinProgress 类的一个新实例
     *
     * @param context 上下文对象
     */
    public ChangeColorSpinProgress(Context context) {
        this(context, null);
    }

    /**
     * 构造函数，初始化 ChangeColorSpinProgress 类的一个新实例
     *
     * @param context      上下文对象
     * @param attrs        属性列表
     * @param defStyleAttr 默认样式
     */
    public ChangeColorSpinProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChangeColorSpinProgress, defStyleAttr, 0);
        parseAttributes(ta);
        setAnimationEnabled();
    }

    /**
     * 构造函数，初始化 ChangeColorSpinProgress 类的一个新实例
     *
     * @param context 上下文对象
     * @param attrs   属性列表
     */
    public ChangeColorSpinProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    //endregion

    //region 受保护成员
    protected int mBarLength = 16;//旋转条长度
    protected int mBarMaxLength = 270;//最长旋转条长度
    protected int mCircleRadius = 28;//圆环半径
    protected int mBarWidth = 3;//进度条宽度，px 为单位
    protected int mRingWidth = 3;//圆环宽度,px 位单位
    protected int[] mBarColors = new int[]{ 0xFFAEDEF4,0xFF009688,0xFFA5DC86,0xFF80CBC4,0xFF37474F,0xFFF8BB86,0xFFA5DC86};//旋转条颜色
    private int mColorIndex = 0;//当前颜色索引
    protected int mRingColor = 0x00FFFFFF;//圆圈颜色
    protected Paint mBarPaint = new Paint();
    protected Paint mRingPaint = new Paint();
    protected RectF mCircleBounds = new RectF();//绘制区域
    private boolean mShouldAnimate = true;
    private float mSpinSpeed = 230.0f;//旋转速度为 230 度/秒
    private double mSpinCycleTime = 450;//旋转一圈用时，毫秒
    private boolean mIsSpinning = true;
    private float mProgress = 0.0f;//当前位置
    private float mTargetProgress = 0.0f;//目标位置
    private long mLastTimeAnimated = 0;
    private float mBarExtraLength = 0;
    private boolean mBarGrowingFromFront = true;
    private double mTimeStartGrowing = 0;
    private final long mPauseGrowingTime = 200;
    private long mPausedTimeWithoutGrowing = 0;
    private long mRecordTime = SystemClock.uptimeMillis();
    //endregion

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setAnimationEnabled() {
        int currentApiVersion = Build.VERSION.SDK_INT;

        float animationValue;
        if (currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            animationValue = Settings.Global.getFloat(getContext().getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1);
        } else {
            animationValue = Settings.System.getFloat(getContext().getContentResolver(),
                    Settings.System.ANIMATOR_DURATION_SCALE, 1);
        }

        mShouldAnimate = animationValue != 0;
    }

    //重新测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = mCircleRadius * 2 + this.getPaddingLeft() + this.getPaddingRight();
        int viewHeight = mCircleRadius * 2 + this.getPaddingTop() + this.getPaddingBottom();
        setMeasuredDimension(viewWidth, viewHeight);//只支持圆形一种模式，不知道 wrap_content，match_parent 等导致的变形
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int width;
//        int height;
//        if (widthMode == MeasureSpec.EXACTLY) {
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            width = Math.min(viewWidth, widthSize);
//        } else {
//            width = viewWidth;
//        }
//        if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
//            height = heightSize;
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            height = Math.min(viewHeight, heightSize);
//        } else {
//            height = viewHeight;
//        }
//        setMeasuredDimension(width, height);
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
        mBarPaint.setColor(mBarColors[mColorIndex]);
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStrokeWidth(mBarWidth);

        mRingPaint.setColor(mRingColor);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mRingWidth);
    }

    //设置绘制区域
    private void setupBounds(int layout_width, int layout_height) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        mCircleBounds = new RectF(paddingLeft + mBarWidth, paddingTop + mBarWidth,
                layout_width - paddingRight - mBarWidth, layout_height - paddingBottom - mBarWidth);
    }

    //转换 TypedArray
    private void parseAttributes(TypedArray a) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mBarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBarWidth, metrics);
        mRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRingWidth, metrics);
        mCircleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCircleRadius, metrics);
        mCircleRadius = (int) a.getDimension(R.styleable.ChangeColorSpinProgress_ProgressCircleRadius, mCircleRadius);
        mBarWidth = (int) a.getDimension(R.styleable.ChangeColorSpinProgress_ProgressBarWidth, mBarWidth);
        mRingWidth = (int) a.getDimension(R.styleable.ChangeColorSpinProgress_ProgressRingWidth, mRingWidth);
        float baseSpinSpeed =
                a.getFloat(R.styleable.ChangeColorSpinProgress_ProgressSpinSpeed, mSpinSpeed / 360.0f);
        mSpinSpeed = baseSpinSpeed * 360;
        mSpinCycleTime = a.getInt(R.styleable.ChangeColorSpinProgress_ProgressSpinCycleTime, (int) mSpinCycleTime);
        int colorArrResId = a.getResourceId(R.styleable.ChangeColorSpinProgress_ProgressBarColors, NO_ID);
        if (colorArrResId != NO_ID){
            mBarColors = getResources().getIntArray(colorArrResId);
        }
        mRingColor = a.getColor(R.styleable.ChangeColorSpinProgress_ProgressRingColor, mRingColor);
        a.recycle();
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(mCircleBounds, 360, 360, false, mRingPaint);
        if (!mShouldAnimate) {
            return;
        }
        if (mIsSpinning) {
            long deltaTime = (SystemClock.uptimeMillis() - mLastTimeAnimated);//时间变化
            float deltaNormalized = deltaTime * mSpinSpeed / 1000.0f;//目标变化角度
            updateBarLength(deltaTime);
            mProgress += deltaNormalized;//目标角度
            if (mProgress > 360) {
                mProgress -= 360f;
            }
            mLastTimeAnimated = SystemClock.uptimeMillis();
            float from = mProgress - 90;
            float length = mBarLength + mBarExtraLength;
            if (isInEditMode()) {
                from = 0;
                length = 135;
            }
            mBarPaint.setColor(mBarColors[(int)(mLastTimeAnimated - mRecordTime) / 1000 % mBarColors.length]);//设置画笔
            canvas.drawArc(mCircleBounds, from, length, false, mBarPaint);
        } else {
            float oldProgress = mProgress;
            if (mProgress != mTargetProgress) {
                //We smoothly increase the progress bar
                float deltaTime = (float) (SystemClock.uptimeMillis() - mLastTimeAnimated) / 1000;
                float deltaNormalized = deltaTime * mSpinSpeed;
                mProgress = Math.min(mProgress + deltaNormalized, mTargetProgress);
                mLastTimeAnimated = SystemClock.uptimeMillis();
            }
            float offset = 0.0f;
            float progress = mProgress;
            float factor = 2.0f;
            offset = (float) (1.0f - Math.pow(1.0f - mProgress / 360.0f, 2.0f * factor)) * 360.0f;
            progress = (float) (1.0f - Math.pow(1.0f - mProgress / 360.0f, factor)) * 360.0f;
            if (isInEditMode()) {
                progress = 360;
            }
            mBarPaint.setColor(mBarColors[(int)(mLastTimeAnimated - mRecordTime) / 1000 % mBarColors.length]);//设置画笔
            canvas.drawArc(mCircleBounds, offset - 90, progress, false, mBarPaint);
        }
        invalidate();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == VISIBLE) {
            mLastTimeAnimated = SystemClock.uptimeMillis();
        }
    }

    private void updateBarLength(long deltaTimeInMilliSeconds) {
        if (mPausedTimeWithoutGrowing >= mPauseGrowingTime) {
            mTimeStartGrowing += deltaTimeInMilliSeconds;//开始增加的时间

            if (mTimeStartGrowing > mSpinCycleTime) {
                // We completed a size change cycle
                // (growing or shrinking)
                mTimeStartGrowing -= mSpinCycleTime;
                //if(mBarGrowingFromFront) {
                mPausedTimeWithoutGrowing = 0;
                //}
                mBarGrowingFromFront = !mBarGrowingFromFront;
            }

            float distance =
                    (float) Math.cos((mTimeStartGrowing / mSpinCycleTime + 1) * Math.PI) / 2 + 0.5f;
            float destLength = (mBarMaxLength - mBarLength);

            if (mBarGrowingFromFront) {
                mBarExtraLength = distance * destLength;
            } else {
                float newLength = destLength * (1 - distance);
                mProgress += (mBarExtraLength - newLength);
                mBarExtraLength = newLength;
            }
        } else {
            mPausedTimeWithoutGrowing += deltaTimeInMilliSeconds;
        }
    }
    //region 公有属性
    /**
     * 检查当前是否正在旋转
     */
    public boolean isSpinning() {
        return mIsSpinning;
    }

    /**
     * 停止旋转
     */
    public void stopSpinning() {
        mIsSpinning = false;
        mProgress = 0.0f;
        mTargetProgress = 0.0f;
        invalidate();
    }

    /**
     * 开始旋转
     */
    public void spin() {
        mLastTimeAnimated = SystemClock.uptimeMillis();
        mIsSpinning = true;
        invalidate();
    }

    /**
     * 获取旋转圆的半径
     *
     * @return 旋转圆的半径
     */
    public int getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * 设置返回旋转圆的半径
     *
     * @param circleRadius the expected radius, in pixels
     */
    public void setCircleRadius(int circleRadius) {
        this.mCircleRadius = circleRadius;
        if (!mIsSpinning) {
            invalidate();
        }
    }

    /**
     * 获取滚动条的宽度
     *
     * @return 滚动条的宽度
     */
    public int getBarWidth() {
        return mBarWidth;
    }

    /**
     * 设置滚动条的宽度
     *
     * @param barWidth 滚动条的宽度
     */
    public void setBarWidth(int barWidth) {
        this.mBarWidth = barWidth;
        if (!mIsSpinning) {
            invalidate();
        }
    }

    /**
     * 获取滚动条颜色
     * @return 滚动条的颜色列表
     */
    public int[] getBarColors() {
        return mBarColors;
    }

    /**
     * 设置滚动条的颜色
     * @param barColors 滚动条的颜色列表
     */
    public void setBarColor(int[] barColors) {
        if (barColors == null || barColors.length == 0){
            throw new IllegalArgumentException();
        }
        this.mBarColors = barColors;
        mColorIndex = 0;
        setupPaints();
        if (!mIsSpinning) {
            invalidate();
        }
    }

    /**
     * 获取圆环颜色
     *
     * @return 圆环颜色
     */
    public int getRingColor() {
        return mRingColor;
    }

    /**
     * 设置圆环颜色
     *
     * @param rimColor 圆环颜色
     */
    public void setRingColor(@ColorInt int rimColor) {
        this.mRingColor = rimColor;
        setupPaints();
        if (!mIsSpinning) {
            invalidate();
        }
    }

    /**
     * 获取旋转速度，以 “度/秒” 为单位
     * @return 每秒旋转的角度值
     */
    public float getSpinSpeed() {
        return mSpinSpeed;
    }

    /**
     * 设置旋转速度，以 “度/秒” 为单位
     * @param spinSpeed 每秒旋转的角度值
     */
    public void setSpinSpeed(float spinSpeed) {
        this.mSpinSpeed = spinSpeed;
    }

    /**
     * 获取圆环的宽度
     *
     * @return 圆环的宽度
     */
    public int getRingWidth() {
        return mRingWidth;
    }

    /**
     * 设置圆环的宽度
     *
     * @param mRingWidth rim的宽度，px 为单位
     */
    public void setRingWidth(int mRingWidth) {
        this.mRingWidth = mRingWidth;
        if (!mIsSpinning) {
            invalidate();
        }
    }
    //endregion
}