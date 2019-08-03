package com.asurx.progressmanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MyProgressView extends View {
    private Context mContext;

    private Paint mPaintProgress,mPaintBubble, mPaintText;
    private PathMeasure mPathMeasure;
    private Path mPathBack, mPathProgress,mPathBubble;

    private int mHeight;
    private int mTextMargin;//气泡的边距

    private int mColorBack;       //进度条背景颜色
    private int mColorProgress;   //进度条进度颜色
//    private int mColorText;       //进度条文字颜色
    private float  mTextSize;//进度条文字大小
    private String mText = "0%";  //显示进度的字符串

    private float mProgress = 0;//进度

    private float mBubbleTriangleHeight = 10;//三角形高度
    private float mRad = 5;//气泡圆角
    private Paint.FontMetricsInt mFontMetricsInt;

    public MyProgressView(Context context) {
        this(context,null);
    }
    public MyProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public MyProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyProgressView);
//        total    = array.getInteger(R.styleable.MyProgressView_total, 100);
//        progress = array.getInteger(R.styleable.MyProgressView_progress, 0);
        mHeight = array.getInteger(R.styleable.MyProgressView_barHeight, 10);

        mColorBack      = array.getColor(R.styleable.MyProgressView_bgColor,context.getColor(R.color.grey));
        mColorProgress  = array.getColor(R.styleable.MyProgressView_pgColor,context.getColor(R.color.blue));
//        mColorText      = array.getColor(R.styleable.MyProgressView_textColor,context.getColor(R.color.white));
        mTextMargin  = array.getInteger(R.styleable.MyProgressView_textMargin, 20);
        mTextSize    = array.getInteger(R.styleable.MyProgressView_textSize, 35);
        initPaints();
    }




    private void initPaints(){
        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND);
        mPaintProgress.setStrokeWidth(mHeight);

        mPaintBubble = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBubble.setStyle(Paint.Style.FILL);
        mPaintBubble.setStrokeCap(Paint.Cap.ROUND);//设置线头为圆角
        mPaintBubble.setStrokeJoin(Paint.Join.ROUND);//设置拐角为圆角
        mPaintBubble.setColor(mColorProgress);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setDither(true);
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintText.setStrokeWidth(1);
//        mPaintText.setColor(mColorText);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(mTextSize);//设置字体大小
        mPaintText.setTextAlign(Paint.Align.CENTER);//将文字水平居中

        mPathBack = new Path();
        mPathProgress = new Path();
        mPathBubble = new Path();
        mPathMeasure = new PathMeasure();

        mFontMetricsInt = mPaintText.getFontMetricsInt();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        mProgress = (float)progress/total;
        mText = (int)(this.mProgress*100)+"%";
        //画进度条
        drawProgress(canvas);
        //画气泡
        drawBubble(canvas);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPathBack.moveTo(0,h/2);
        mPathBack.lineTo(    w,h/2);//进度条位置在控件整体底部，且距离控件左边和右边各20像素
        mPathMeasure.setPath(mPathBack,false);
        invalidate();
    }

    private void drawProgress(Canvas canvas) {
        mPathProgress.reset();
        mPaintProgress.setColor(mColorBack);
        canvas.drawPath(mPathBack, mPaintProgress);//绘制进度背景（灰色部分）
        float stop = mPathMeasure.getLength() * mProgress;//计算进度条的进度
        mPathMeasure.getSegment(0,stop,mPathProgress,true);//得到与进度对应的路径
        mPaintProgress.setColor(mColorProgress);
        canvas.drawPath(mPathProgress, mPaintProgress);//绘制进度
    }
    private void drawBubble(Canvas canvas) {
        Rect rect = new Rect();
        mPaintText.getTextBounds(mText,0,mText.length(),rect);//返回包围整个字符串的最小的一个Rect区域，以此计算出文字的高度和宽度
        int width  = rect.width() + mTextMargin;//计算字符串宽度(加上设置的边距)
        int height = rect.height() + mTextMargin;//计算字符串高度(加上设置的边距)

        mPathBubble.reset();
        float p[] = new float[2];//用于存储点坐标的数组
        float t[] = new float[2];
        float stop = mPathMeasure.getLength() * mProgress;//计算进度条的进度
        mPathMeasure.getPosTan(stop,p,t);//获取进度所对应点的左边
        mPathBubble.moveTo(p[0],p[1]- mHeight);
        mPathBubble.lineTo(p[0]+mBubbleTriangleHeight,p[1]-mBubbleTriangleHeight- mHeight);//假设底部小三角为等腰直角三角形，那么三角形的高度就等于底边长度的1/2
        mPathBubble.lineTo(p[0]-mBubbleTriangleHeight,p[1]-mBubbleTriangleHeight- mHeight);
        mPathBubble.close();//使路径闭合从而形成三角形
        //这里是计算文字所在矩形的位置及大小
        //left:因为设置的气泡底部三角形为等腰直角三角形，所以矩形的左边位置为，
        //      进度所在的横坐标 - 底部三角形高度 - 矩形圆角的半径(不减去圆角半径的话显得不够圆润)，
        //      而(mProgress*width)则是为了不断改变气泡底部的三角形与气泡顶部矩形的相对位置
        //      否则在进度条开始或结束位置可能为显示不全
        //top:进度所在的高度 - 底部三角形高度 - 进度条高度 - 矩形高度
        //right:矩形右边位置的计算原理与左边相同，同样((1-mProgress)*width)也是为了不断改变气泡底部的三角形与气泡顶部矩形的相对位置（与left相对应）
        //bottom:这个就简单了，与top相比小了一个矩形的高度
        RectF rectF = new RectF(p[0]-mBubbleTriangleHeight-mRad/2-(mProgress*width),p[1]-mBubbleTriangleHeight- mHeight -height,p[0]+mBubbleTriangleHeight+mRad/2+((1-mProgress)*width),p[1]-mBubbleTriangleHeight- mHeight);
        mPathBubble.addRoundRect(rectF,mRad,mRad, Path.Direction.CW);//添加矩形路径
        canvas.drawPath(mPathBubble,mPaintBubble);//绘制气泡
        int i = (mFontMetricsInt.bottom - mFontMetricsInt.ascent) / 2 - mFontMetricsInt.bottom;//让文字垂直居中
        canvas.drawText(mText,rectF.centerX(),rectF.centerY()+i,mPaintText);//绘制文字（将文字绘制在气泡矩形的中心点位置）
    }

//    public void setTotal(int total){
//        this.total = total;
//        invalidate();
//    }

    public void setProgress(float progress){
        this.mProgress = progress;
        invalidate();//ReDraw
    }

}
