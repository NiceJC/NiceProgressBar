package com.yaochi.niceprogressbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import java.util.List;

import static com.yaochi.niceprogressbar.LeafFactory.CYCLE_MILLI;

public class NiceProgressBar extends View {

    private int progress_MAX = 100;
    private int mProgress = 100;
    private int mWidth, mHeight;

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint leafPaint;
    private Paint wheelPaint;

    private int backgroundColor = Color.WHITE;
    private int progressColor =0xffFF9800;

    private RectF mBackRectF;//背景总的矩形
    private RectF mProgressF; //内部进度条占据的矩形(左边半圆+长条矩形 因为右边是个转动的轮子 所以 进度条不包括右边的半圆)
    private RectF mProgressCirCleF; //内部进度条左边半圆占据的矩形
    private Rect mWheelRectF; //右边轮子占据的矩形
    private Rect mWheelImageRect; //图片要绘制的部分


    private int progressPadding = 12; //进度条与背景的间隙宽度
    private int progressWidth;//进度条总长度
    private int progressRadium;//进度条半径
    private int onlyCircleProgress;//填满半圆的临界进度
    private Context mContext;

    private Matrix wheelMatrix;

    private Bitmap leafPic;
    private Bitmap wheelPic;

    private long startMillis;//开始时间
    private boolean isProgressing = false;//是否在进度中（开始之后，结束之前）
    private List<LeafBean> leafs;


    public NiceProgressBar(Context context) {
        this(context, null);
    }

    public NiceProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NiceProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initPaint();
        initParam();

    }


    private void initPaint() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setAntiAlias(true);

        wheelPaint = new Paint();
        wheelPaint.setColor(0xFFFFFF00);
        wheelPaint.setStyle(Paint.Style.FILL);
        wheelPaint.setAntiAlias(true);
    }

    private void initParam() {
        leafPic = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.leaf);
        wheelPic = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.fengshan);
        mWheelImageRect = new Rect(0, 0, wheelPic.getWidth(), wheelPic.getHeight());
        leafs = new LeafFactory().generateLeafs();
    }

    /**
     * 对外暴露方法
     */

    //设置最大值
    public void setMax(int max) {
        progress_MAX = max;
        onlyCircleProgress = (int) (progressRadium / (float) progressWidth * progress_MAX);

    }

    //开始监听进度 启动动画
    public void startProgress() {
        isProgressing = true;
        startMillis = System.currentTimeMillis();
    }

    //设置当前进度
    public void setProgress(int progress) {

        if (!isProgressing) {
            startProgress();
        }
        mProgress = progress;
        if (mProgress < 0) {
            mProgress = 0;
        } else if (mProgress > progress_MAX) {
            mProgress = progress_MAX;
        }
        if (mProgress == progress_MAX) {
            //完成进度
            isProgressing = false;
        }
        postInvalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        //进度条总长
        //进度条半径
        //进度条半径
        mBackRectF = new RectF(0, 0, mWidth, mHeight);
        mProgressF = new RectF(progressPadding, progressPadding, mWidth - mHeight / 2, mHeight - progressPadding);
        mProgressCirCleF = new RectF(progressPadding, progressPadding, mHeight - progressPadding, mHeight - progressPadding);


        progressWidth = mWidth - progressPadding - mHeight / 2;
        progressRadium = mHeight / 2 - progressPadding;
        onlyCircleProgress = (int) (progressRadium / (float) progressWidth * progress_MAX);
        wheelMatrix = new Matrix();
        wheelMatrix.postTranslate(mWidth - mHeight, 0);
        mWheelRectF = new Rect(-progressRadium, -progressRadium, progressRadium, progressRadium);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawbackGround(canvas);

        drawLeafs(canvas);
        drawProgress(canvas);
        drawWheel(canvas);
    }

    private void drawbackGround(Canvas canvas) {
        canvas.drawRoundRect(mBackRectF, mHeight / 2, mHeight / 2, backgroundPaint);
    }

    //叶子会一边沿着一条弧线飞行，一边旋转
    //从右飞到左边之后 又重新开始（需先经历等待时间）
    //leafPeriod=2000ms(叶子也刚好旋转一圈) CYCLE_MILLI
    private void drawLeafs(Canvas canvas) {
        if (!isProgressing) {
            return;
        }


        for (LeafBean leaf : leafs
        ) {

            //CYCLE_MILLI+waitTime就是一个周期
            //我们绘制currentTime处于0-CYCLE_MILLI这段时间（飞行时间）内的叶子
            long currentTime = ((System.currentTimeMillis() - startMillis) % (CYCLE_MILLI + leaf.waitTime)) - leaf.waitTime;
            if (currentTime > 0) {
                Matrix matrix = new Matrix();
                canvas.save();
                //过了等待时间之后 才进行绘制

                canvas.translate(progressWidth + progressPadding, progressRadium + progressPadding - (float) leafPic.getHeight() / 2);
                canvas.translate(-progressWidth / (float) CYCLE_MILLI * currentTime, (float) (Math.sin(Math.PI * ((float) currentTime / CYCLE_MILLI * 2)) * (progressRadium - (float) leafPic.getHeight() / 2)) * leaf.yFactory);
                if (leaf.rotateDirection == 1) {
                    matrix.postRotate((float) 360 / CYCLE_MILLI * currentTime + leaf.rotateAngle, (float) leafPic.getWidth() / 2, (float) leafPic.getHeight() / 2);
                } else {
                    matrix.postRotate(-(float) 360 / CYCLE_MILLI * currentTime + leaf.rotateAngle, (float) leafPic.getWidth() / 2, (float) leafPic.getHeight() / 2);
                }
                //叶子宽度为 进度条半径最佳
                matrix.postScale(progressRadium / (float) leafPic.getWidth(), progressRadium / (float) leafPic.getWidth());
                canvas.drawBitmap(leafPic, matrix, null);
                canvas.restore();

            }
        }
    }


    // 进度条   长度mWidth-progressPadding-mHeight/2
    private void drawProgress(Canvas canvas) {

//        if(System.currentTimeMillis()-startMillis<CYCLE_MILLI){
//            return;
//        }


        if (mProgress <= onlyCircleProgress) {
            //只需画半圆
            int angle = (int) Math.toDegrees(Math.acos((onlyCircleProgress - mProgress) / (float) onlyCircleProgress));
            canvas.drawArc(mProgressCirCleF, 180f - angle, 2 * angle, false, progressPaint);

        } else {
            //画完半圆再根据进度画一段矩形
            canvas.drawArc(mProgressCirCleF, 90f, 180f, false, progressPaint);
            int right = progressWidth * mProgress / progress_MAX + progressPadding;
            canvas.drawRect(progressRadium + progressPadding, progressPadding, right, mHeight - progressPadding, progressPaint);
        }
    }

    //右边旋转的轮子
    //默认6s 转一圈；即 6000ms转360角度 每毫秒 0.06角度
    private void drawWheel(Canvas canvas) {
        long durationMillis = (System.currentTimeMillis() - startMillis) % 6000;

        canvas.save();
        canvas.translate(progressWidth + progressPadding, progressRadium + progressPadding);
        canvas.rotate(-durationMillis * 0.06f);
        canvas.drawCircle(0, 0, mHeight / 2, backgroundPaint);
        canvas.drawCircle(0, 0, mHeight / 2 - 7, progressPaint);
        canvas.drawBitmap(wheelPic, mWheelImageRect, mWheelRectF, null);
        canvas.restore();

    }


}
