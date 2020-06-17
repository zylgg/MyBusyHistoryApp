package com.example.mybusyhistoryapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.core.view.ViewCompat;


/**
 * Created by TFHR02 on 2017/7/10.
 * 曲线图
 */
public class BusyFlowChartView extends View {
    private static final String TAG = "CurveLineChartView";
    /**
     * 画笔
     */
    private Paint mPaint;
    private Typeface typeface = Typeface.MONOSPACE;//字体样式，默认是等宽字体
    /**
     * 曲线颜色
     */
    private int curve_color;
    /**
     * 是否显示x轴
     */
    private boolean is_showXAxis;
    /**
     * 是否显示x轴数据
     */
    private boolean is_showXValues;
    /**
     * 是否显示虚线
     */
    private boolean is_showBrokenLine;
    /**
     * 数据单位
     */
    private String unit;
    private float horiz_space;
    /**
     * Y轴画几个值
     */
    private int yvaluecount = 3;
    /**
     * x轴画几个值
     */
    private int xvaluecount = 6;
    /**
     * 小圆半径
     */
    private int smallcircleradius;
    /**
     * y轴数据
     */
    private float Yarrays[] = null;
    /**
     * 线宽
     */
    private float StrokeWidth;
    /**
     * 字体大小
     */
    private float textsize;
    /**
     * 曲线区域 距离边界多少
     */
    private float marginright, marginleft, marginbottom, margintop;
    /**
     * 手势解析
     */
    private GestureDetector detector;
    /**
     * 图标数据
     */
    private chartdata datas=null;
    /**
     * 滚动计算器
     */
    private Scroller scroller;
    /**
     * 画布X轴的平移，用于实现曲线图的滚动效果
     */
    private float translateX = 0;
    private int minX, maxX;
    /**
     * 柱状图线条宽度
     */
    private int lineWidth=0;

    public BusyFlowChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initattr(context, attrs);
        initpaint();
        scroller = new Scroller(context);
        detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            float lastScrollX = 0f;

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (Math.abs(distanceX / distanceY) > 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float transx = translateX - 1.5f*distanceX;
                    if (transx < minX || transx > maxX) {
                        return false;
                    }
                    moveTo((int) transx);
                    if (e1.getX() != lastScrollX) {
                        lastScrollX = e1.getX();
                        if (chartListener != null) {
                            chartListener.onMove(transx);
                        }
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                scroller.fling((int) getTranslateX(), 0, (int) velocityX, 0, minX, maxX, 0, 0);
//                ViewCompat.postInvalidateOnAnimation(CurveLineChartView.this);
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                //强制结束本次滑屏操作
                scroller.forceFinished(true);
                ViewCompat.postInvalidateOnAnimation(BusyFlowChartView.this);
                return true;
            }
        });
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initattr(Context context, AttributeSet attrs) {
        TypedArray arrays = context.obtainStyledAttributes(attrs, R.styleable.CurveLineChartView);
        curve_color = arrays.getColor(R.styleable.CurveLineChartView_curve_color, Color.WHITE);
        is_showXAxis = arrays.getBoolean(R.styleable.CurveLineChartView_is_showXAxis, true);
        is_showXValues = arrays.getBoolean(R.styleable.CurveLineChartView_is_showXValues, true);
        is_showBrokenLine = arrays.getBoolean(R.styleable.CurveLineChartView_is_showBrokenLine, false);
        unit = arrays.getString(R.styleable.CurveLineChartView_unit);
        arrays.recycle();

        horiz_space = context.getResources().getDimensionPixelSize(R.dimen.x108);
        StrokeWidth = context.getResources().getDimensionPixelSize(R.dimen.x2);
        smallcircleradius = context.getResources().getDimensionPixelSize(R.dimen.x8);
        textsize = context.getResources().getDimensionPixelSize(R.dimen.x24);

        marginright = context.getResources().getDimensionPixelSize(R.dimen.x24);
        marginleft = context.getResources().getDimensionPixelSize(R.dimen.x24);

        marginbottom = context.getResources().getDimensionPixelSize(R.dimen.y66);
        margintop = context.getResources().getDimensionPixelSize(R.dimen.y66);

        lineWidth=context.getResources().getDimensionPixelOffset(R.dimen.x26);
    }

    /**
     * 初始化画笔
     */
    private void initpaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(StrokeWidth);
        mPaint.setTypeface(typeface);
        mPaint.setTextSize(textsize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width;
//        if (datas == null) {
            width = getDefaultSize(400, widthMeasureSpec);
//        } else {
//            width = (int) ((datas.getPoints().size() - 1) * horiz_space + marginleft + marginright);
//        }
        setMeasuredDimension((int) width, getDefaultSize(400, heightMeasureSpec));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            moveTo(scroller.getCurrX());
        }
    }

    /**
     * 传入数据源
     *
     * @param datas
     */
    public void setData(chartdata datas) {
        this.datas=null;
        this.datas = datas;

        this.is_firstdraw = true;
        //当水平间距固定，view宽度过长可滑动展示时，view总宽度
//        width = (int) ((datas.getPoints().size() - 1) * horiz_space + marginleft + marginright);

        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 重新计算Y轴(最小值，最大值)数据
     */
    private void initYvalues() {
        Yarrays = new float[yvaluecount];
        float minY = datas.getYminvalues();
        //若最大值%最小值，除不尽；则将（除后的商+1，在*最小值）作为最大值
        float maxY;
        if (minY != 0) {
            maxY = datas.getYmaxvalues() % minY > 0 ? (datas.getYmaxvalues() / datas.getYminvalues() + 1) * datas.getYminvalues() : datas.getYmaxvalues();
        } else {
            maxY = datas.getYmaxvalues();
        }
        //每个y轴之间间隔值（需要用精确计算）
        float yyyy = (maxY - minY) / (yvaluecount - 1);
        //最小值作为第一个值
        Yarrays[0] = minY;
        for (int i = 1; i < yvaluecount; i++) {
            Yarrays[i] = minY + i * yyyy;
        }
        //最大值作为最后一个数
        Yarrays[Yarrays.length - 1] = maxY;
    }

    //数据宽，布局高
    private int width, height;
    boolean is_firstdraw = true;

    @Override
    protected void onDraw(Canvas canvas) {

        if (datas == null) {
            return;
        }
        height = getHeight();
        //当view宽度固定，view不可滑动展示时，每个数据水平间距
        width=getWidth();
        horiz_space=(float) ((width*1.0f-marginleft-marginright-datas.getPoints().size()*lineWidth)/(datas.getPoints().size() - 1));


        if (is_firstdraw) {
            minX = (-(width - SystemAppUtils.getScreenWidth(getContext()) + 5 * (int) marginright));
            maxX = 0;
            this.translateX = minX;
            is_firstdraw = false;
        }
        //移动画布圆点使 曲线移动到屏幕最右端
//        canvas.translate(getTranslateX(), 0);
        //画x轴
        if (is_showXAxis) {
            canvas.drawLine(marginleft, height - marginbottom, width - marginright, height - marginbottom, mPaint);
        }

        //重新计算Y轴(最小值，最大值)数据
        initYvalues();

        //画繁忙程度分水岭-文字&虚线
        Rect textRect0 = new Rect();
        mPaint.setColor(getResources().getColor(R.color.color_F4BEBE));
        mPaint.getTextBounds("较繁忙", 0, 3, textRect0);
        canvas.drawText("较繁忙",marginleft,Dealvalues_to_px(400)+textRect0.height()*1.0f/2-3,mPaint);
        Paint paint22 = new Paint();
        paint22.setStyle(Paint.Style.STROKE);
        paint22.setColor(getResources().getColor(R.color.color_F4BEBE));
        paint22.setStrokeWidth(3);
        PathEffect effects = new DashPathEffect(new float[]{16, 4, 16, 4}, 1);
        paint22.setPathEffect(effects);
        Path path = new Path();
        path.moveTo(marginleft+textRect0.width(), Dealvalues_to_px(400));
        path.lineTo(width - marginright, Dealvalues_to_px(400));
        canvas.drawPath(path, paint22);

        Point numlists[] = new Point[datas.getPoints().size()];
        for (int i = datas.getPoints().size() - 1; i >= 0; i--) {//数据从坐标最右边开始绘制
            //画x轴文字
            Rect textrect = new Rect();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            String xtext = datas.getPoints().get(i).getX();
            mPaint.getTextBounds(xtext, 0, xtext.length(), textrect);
            if (is_showXValues&&(i+1)%4==0) {//x文字左边坐标：总宽-右间距-线宽-文字半宽-线条半宽
                canvas.drawText(xtext,
                        width - marginright - (datas.getPoints().size() - 1 - i) * horiz_space -(datas.getPoints().size() - i)*lineWidth +lineWidth*1.0f/2- (textrect.width()*1.0f / 2)-StrokeWidth/2,
                        height - getContext().getResources().getDimensionPixelSize(R.dimen.y30),
                        mPaint);
            }
            float Yvalues = datas.getPoints().get(i).getY();
            //画结点圆圈
            float x = width - marginright - (datas.getPoints().size() - 1 - i) * horiz_space-(datas.getPoints().size() - i)*lineWidth;
            float y = Dealvalues_to_px(Yvalues);
//            canvas.drawCircle(x+lineWidth/2, y, smallcircleradius, mPaint);

            //画点上面的数据
            String Ytext = String.valueOf((int) Yvalues);
            Rect textRect = new Rect();
            mPaint.getTextBounds(Ytext, 0, Ytext.length(), textRect);
            float circle_text_space = getResources().getDimensionPixelSize(R.dimen.y12);//数据和圆点之间的距离
            canvas.drawText(Ytext, x +lineWidth*1.0f/2- (textRect.width() / 2.0f)-StrokeWidth/2, y - circle_text_space, mPaint);

            //画虚线
//            Paint paint22 = new Paint();
//            paint22.setStyle(Paint.Style.STROKE);
//            paint22.setColor(Color.WHITE);
//            paint22.setStrokeWidth(3);
//            PathEffect effects = new DashPathEffect(new float[]{20, 5, 20, 5}, 1);
//            paint22.setPathEffect(effects);
//            Path path = new Path();
//            path.moveTo(x, y);
//            path.lineTo(x, height - marginbottom);
//            if (is_showBrokenLine) {
//                canvas.drawPath(path, paint22);
//            }
            //画柱状图  lineWidth
            mPaint.setColor(Yvalues>=400?getResources().getColor(R.color.color_dc2828):Color.WHITE);
            canvas.drawRoundRect(new RectF(x,y,x+lineWidth,height - marginbottom),4,4,mPaint);

            //为画曲线准备数据
            numlists[i] = new Point((int) x, (int) y);
        }
        //画曲线
//        Paint paint = new Paint();
//        paint.setColor(curve_color);
//        paint.setStrokeWidth(StrokeWidth);
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        polyBezier(numlists, numlists.length, canvas, paint);

    }

    /**
     * 平移画布
     *
     * @param x
     */
    public void moveTo(int x) {
        translateX = x;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public float getTranslateX() {
        return translateX;
    }

    /**
     * 画曲线
     *
     * @param numList
     * @param count
     * @param can
     * @param mPaint
     */
    private void polyBezier(Point numList[], int count, Canvas can, Paint mPaint) {
        Point startp, endp;
        for (int i = 0; i < numList.length - 1; i++) {
            startp = numList[i];
            endp = numList[i + 1];
            int wt = (startp.x + endp.x) / 2;
            Point p3 = new Point();
            Point p4 = new Point();
            p3.y = startp.y;
            p3.x = wt;
            p4.y = endp.y;
            p4.x = wt;
            Path path = new Path();
            path.moveTo(startp.x, startp.y);
            path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            can.drawPath(path, mPaint);
        }
    }

    /**
     * 将y坐标值按比例传化成对应的像素
     *
     * @param yvalues 点的y轴数据
     * @return
     */
    private float Dealvalues_to_px(float yvalues) {
        //计算的原点到目标点像素
        if (yvalues != 0) {
            yvalues = (yvalues * (height - marginbottom - margintop)) / (Yarrays[Yarrays.length - 1] - Yarrays[0]);
        }
        //计算 容器顶部到目标点的像素
        yvalues = height - yvalues - marginbottom;

        return yvalues;
    }

    /**
     * 曲线图事件监听器
     */
    private ChartListener chartListener;

    public void setChartListener(ChartListener chartListener) {
        this.chartListener = chartListener;
    }

    public interface ChartListener {
        void onMove(float transx);
    }
}
