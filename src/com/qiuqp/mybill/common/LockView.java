package com.qiuqp.mybill.common;


import java.util.ArrayList;
import java.util.List;

import com.qiuqp.mybill.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

/**
 * 自定义控件
 * @author zhouchaoxin
 *
 */
public class LockView extends View {

	
	/**
	 * 绘制图形时所需要的画笔
	 */
	Paint mPaint = new Paint();
	/**
	 * 绘制连接线时所需要的画笔
	 */
	Paint mPathPaint = new Paint();
	/**
	 * 图案接口实例
	 */
	private OnPatternListener mOnPatternListener;
	/**
	 * 定义Cell对象s
	 */
	private ArrayList<Cell> mPatternNumber = new ArrayList<Cell>(9);
	/**
	 * 控件view的图案
	 */
	private boolean[][] mPatternDrawLookup = new boolean[3][3];
	/**
	 * 
	 */
	private static final int MILLIS_PER_CIRCLE_ANIMATING = 700;
	/**
	 * 初始图形
	 */
	private Bitmap mBitmapBtnDefault;
	/**
	 * 点击时的图形
	 */
	private Bitmap mBitmapCircleGreen;
	/**
	 * 箭头图形
	 */
	private Bitmap mBitmapArrowGreenUp;
	/**
	 * 图形的宽度
	 */
	private int mBitmapWidth;
	/**
	 * 图形的高度
	 */
    private int mBitmapHeight;
    /**
     * 动画周期
     */
    private long mAnimatingPeriodStart;
    
    /**
     * 正方形宽
     */
    private float mSquareWidth;
    /**
     * 正方形高
     */
    private float mSquareHeight;
    
    /**
     * Matrix控制图形的旋转(Rotate)、缩放(Scale)、移动(Translate)
     * 箭头矩阵
     */
    private final Matrix mArrowMatrix = new Matrix();
    /**
     * Matrix控制图形的旋转(Rotate)、缩放(Scale)、移动(Translate)
     * 圆形矩阵
     */
    private final Matrix mCircleMatrix = new Matrix();
    /**
     * 直径
     */
    private float mDiameterFactor = 0.10f;
    /**
     * 任意图形，如三角形、梯形等等
     * 
     */
    private final Path mCurrentPath = new Path();
    /**
     * 矩形
     */
    private final Rect mInvalidate = new Rect();
    /**
     * 设置绘制图形的透明度
     */
    private final int mStrokeAlpha = 128;
    /**
     * 方向
     */
    private int mAspect;
    /**
     * 视图的最小宽度/高
     */
    private static final int ASPECT_SQUARE = 0; // View will be the minimum of width/height
    /**
     * 固定宽度,高度将是最低的
     */
    private static final int ASPECT_LOCK_WIDTH = 1; // Fixed width; height will be minimum of (w,h)
    /**
     * 固定高度,宽度将是最低的
     */
    private static final int ASPECT_LOCK_HEIGHT = 2;// Fixed height; width will be minimum of (w,h)
    
    private float mHitFactor = 0.6f;
    
    private boolean mInputEnabled = true;
    
    private boolean mPatternInProgress = false;

    private boolean mEnableHapticFeedback = true;
    
    private float mInProgressX = -1;
    
    private float mInProgressY = -1;
    /**
     * 显示方式
     * Correct;正确
     * Wrong;错误
     * Animate;正在进行中
     */
    private DisplayMode mPatternDisplayMode = DisplayMode.Correct;
    
    private boolean mInStealthMode = false;
    
    private static final boolean PROFILE_DRAWING = false;
    
    private boolean mDrawingProfilingStarted = false;
    
    /**
     * 标识，当密码错误时取消连线
     * 如果不设置此标识，则箭头和连线会一直显示在屏幕上
     */
    private boolean startLine = false;
    /**
	 * 构造函数
	 * @param context
	 */
	public LockView(Context context) {
		
	   this(context,null);
	   
	}
	public LockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		/*TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LockPatternView);

        final String aspect = a.getString(R.styleable.LockPatternView_aspect);
        
        if ("square".equals(aspect)) {
            mAspect = ASPECT_SQUARE;
        } else if ("lock_width".equals(aspect)) {
            mAspect = ASPECT_LOCK_WIDTH;
        } else if ("lock_height".equals(aspect)) {
            mAspect = ASPECT_LOCK_HEIGHT;
        } else {
            mAspect = ASPECT_SQUARE;
        }*/
        //view是否可以点击
        setClickable(true);

        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);
        mPathPaint.setColor(Color.argb(0, 74, 186, 239));   // TODO this should be from the style
        mPathPaint.setAlpha(mStrokeAlpha);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);
        
        
        mBitmapBtnDefault = getBitmapFor(R.drawable.sou1);
        
 		mBitmapCircleGreen = getBitmapFor(R.drawable.sou2);
 		
 		mBitmapArrowGreenUp = getBitmapFor(R.drawable.jian);
 		
 		final Bitmap bitmaps[] = { mBitmapBtnDefault,mBitmapCircleGreen, mBitmapCircleGreen };

 		for (Bitmap bitmap : bitmaps) {
 			//Math.max()返回两个 int 值中较大的一个
 			mBitmapWidth = Math.max(mBitmapWidth, bitmap.getWidth());
 			mBitmapHeight = Math.max(mBitmapHeight, bitmap.getHeight());
 		}
		
	}

	/**
	 * 重写onDraw，在view控件上画图形
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final ArrayList<Cell> pattern = mPatternNumber;
	    final int count = pattern.size();
	    
	    final boolean[][] drawLookup = mPatternDrawLookup;
	    startLine = false;
        /*if (mPatternDisplayMode == DisplayMode.Animate) {
        	
        	final int oneCycle = (count + 1) * MILLIS_PER_CIRCLE_ANIMATING;
            final int spotInCycle = (int) (SystemClock.elapsedRealtime() - mAnimatingPeriodStart) % oneCycle;
            final int numCircles = spotInCycle / MILLIS_PER_CIRCLE_ANIMATING;
        	clearPatternDrawLookup();
        	for (int i = 0; i < numCircles; i++) {
                final Cell cell = pattern.get(i);
                drawLookup[cell.getRow()][cell.getColumn()] = true;
            }
        	 final boolean needToUpdateInProgressPoint = numCircles > 0 && numCircles < count;
        	 if (needToUpdateInProgressPoint) {
                 final float percentageOfNextCircle =
                         ((float) (spotInCycle % MILLIS_PER_CIRCLE_ANIMATING)) /
                                 MILLIS_PER_CIRCLE_ANIMATING;

                 final Cell currentCell = pattern.get(numCircles - 1);
                 final float centerX = getCenterXForColumn(currentCell.column);
                 final float centerY = getCenterYForRow(currentCell.row);

                 final Cell nextCell = pattern.get(numCircles);
                 final float dx = percentageOfNextCircle *
                         (getCenterXForColumn(nextCell.column) - centerX);
                 final float dy = percentageOfNextCircle *
                         (getCenterYForRow(nextCell.row) - centerY);
                 mInProgressX = centerX + dx;
                 mInProgressY = centerY + dy;
             }
        	 invalidate();
        }*/
        final float squareWidth = mSquareWidth;
        final float squareHeight = mSquareHeight;
        
        /**
         * 顶部填充像素
         */
        final int paddingTop = getPaddingTop();
        /**
         * 左填充像素
         */
        final int paddingLeft = getPaddingLeft();
        //画出9个圆形
        for (int i = 0; i < 3; i++) {
            float topY = paddingTop + i * squareHeight;
            //float centerY = mPaddingTop + i * mSquareHeight + (mSquareHeight / 2);
            for (int j = 0; j < 3; j++) {
                float leftX = paddingLeft + j * squareWidth;
                //画圆形
                drawCircle(canvas, (int) leftX, (int) topY, drawLookup[i][j]);
            }
        }
        boolean oldFlag = (mPaint.getFlags() & Paint.FILTER_BITMAP_FLAG) != 0;
        //设置高质量
        //如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作，加快显示速度，本设置项依赖于dither和xfermode的设置  
        mPaint.setFilterBitmap(true);
        mPaint.setFilterBitmap(oldFlag); // restore default flag
        //-----------------------------画箭头和直线---------------------------
        final boolean drawPath = (!mInStealthMode || mPatternDisplayMode == DisplayMode.Wrong);
       
        final Path currentPath = mCurrentPath;
        currentPath.rewind();//清除任何直线和曲线路径
        //半径
        float radius = (squareWidth * mDiameterFactor * 0.5f);
        //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度 
        mPathPaint.setStrokeWidth(radius);
       
        if (drawPath) {

            boolean anyCircles = false;
            for (int i = 0; i < count; i++) {
            
            	System.out.println("i================================="+i);
            	
                Cell cell = pattern.get(i);

                // only draw the part of the pattern stored in
                // the lookup table (this is only different in the case
                // of animation).
                if (!drawLookup[cell.row][cell.column]) {
                    break;
                }
                anyCircles = true;

                float centerX = getCenterXForColumn(cell.column);
                float centerY = getCenterYForRow(cell.row);
                if (i == 0) {
                	 //moveTo()通过移动到指定坐标（以双精度指定），将一个点添加到路径中
                    currentPath.moveTo(centerX, centerY);
                    
                    
                } else {
                	//lineTo()通过绘制一条从当前坐标到新指定坐标（以双精度指定）的直线，将一个点添加到路径中
                    currentPath.lineTo(centerX, centerY);
                }
            }

            // add last in progress section
            if ((mPatternInProgress || mPatternDisplayMode == DisplayMode.Animate) && anyCircles) {
            	
                currentPath.lineTo(mInProgressX, mInProgressY);
            }
            
            
            if (!startLine) {
				//画直线
            	canvas.drawPath(currentPath, mPathPaint);
			}

		}
        
        if (drawPath) {
			

            for (int i = 0; i < count - 1; i++) {
                Cell cell = pattern.get(i);
                Cell next = pattern.get(i + 1);

                // only draw the part of the pattern stored in
                // the lookup table (this is only different in the case
                // of animation).
                if (!drawLookup[next.row][next.column]) {
                    break;
                }
                System.out.println("squareWidth===================="+squareWidth);
                System.out.println("squareHeight===================="+squareHeight);
                
                float leftX = paddingLeft + cell.column * squareWidth;
                float topY = paddingTop + cell.row * squareHeight;
               if (!startLine) {
                	
                	//画箭头
                    drawArrow(canvas, leftX, topY, cell, next);
                }
            }
        
		}
        
        
        
	}
	
	/**
     * 这个视图的大小发生了变化时调用
     * w-目前这个视图的宽度
     * h-目前这个视图的高度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	final int width = w - getPaddingLeft() - getPaddingRight();
        mSquareWidth = width / 3.0f;

        final int height = h - getPaddingTop() - getPaddingBottom();
        mSquareHeight = height / 3.0f;
    }
	/**
	 * 画圆圈
	 * @param canvas 画布
	 * @param leftX 左边距
	 * @param topY 上加距
	 * @param partOfPattern 这个圆是否是模式的一部分
	 */
	private void drawCircle(Canvas canvas, int leftX, int topY, boolean partOfPattern) {
		/**
		 * 外部的圆
		 */
		Bitmap outerCircle;
		/**
		 * 内部的圆
		 */
        Bitmap innerCircle;
        
        
        if (!partOfPattern || (mInStealthMode && mPatternDisplayMode != DisplayMode.Wrong)) {
            // unselected circle
            outerCircle = mBitmapBtnDefault;
            innerCircle = mBitmapBtnDefault;
        } else if (mPatternInProgress) {
            // user is in middle of drawing a pattern
            outerCircle = mBitmapCircleGreen;
            innerCircle = mBitmapCircleGreen;
        } else if (mPatternDisplayMode == DisplayMode.Wrong) {
        	startLine = true;
        	Log.i("lock", "LockView===startLine======"+startLine);
            // the pattern is wrong
            outerCircle = mBitmapBtnDefault;
            innerCircle = mBitmapBtnDefault;
        } else if (mPatternDisplayMode == DisplayMode.Correct ||
                mPatternDisplayMode == DisplayMode.Animate) {
            // the pattern is correct
            outerCircle = mBitmapCircleGreen;
            innerCircle = mBitmapBtnDefault;
        } else {
            throw new IllegalStateException("unknown display mode " + mPatternDisplayMode);
        }
     
        final int width = mBitmapWidth;
        final int height = mBitmapHeight;

        final float squareWidth = mSquareWidth;
        final float squareHeight = mSquareHeight;
        
        
        int offsetX = (int) ((squareWidth - width) / 2f);
        int offsetY = (int) ((squareHeight - height) / 2f);
        
        //返回两个 float 值中较小的一个
        float sx = Math.min(mSquareWidth / mBitmapWidth, 1.0f);
        float sy = Math.min(mSquareHeight / mBitmapHeight, 1.0f);
        
        mCircleMatrix.setTranslate(leftX + offsetX, topY + offsetY);
        mCircleMatrix.preTranslate(mBitmapWidth/2, mBitmapHeight/2);
        mCircleMatrix.preScale(sx, sy);
        mCircleMatrix.preTranslate(-mBitmapWidth/2, -mBitmapHeight/2);
        
        canvas.drawBitmap(outerCircle, mCircleMatrix, mPaint);
        canvas.drawBitmap(innerCircle, mCircleMatrix, mPaint);
	}
	
	/**
     * 绘制箭头
     * @param canvas
     * @param leftX
     * @param topY
     * @param start
     * @param end
     */
    private void drawArrow(Canvas canvas, float leftX, float topY, Cell start, Cell end) {
        //boolean green = mPatternDisplayMode != DisplayMode.Wrong;

        final int endRow = end.row;
        final int startRow = start.row;
        
        final int endColumn = end.column;
        final int startColumn = start.column;

        // offsets for centering the bitmap in the cell
        final int offsetX = ((int) mSquareWidth - mBitmapWidth) / 2;
        final int offsetY = ((int) mSquareHeight - mBitmapHeight) / 2;

        Bitmap arrow = mBitmapArrowGreenUp;
        final int cellWidth = mBitmapWidth;
        final int cellHeight = mBitmapHeight;

        // the up arrow bitmap is at 12:00, so find the rotation from x axis and add 90 degrees.
        //atan2将矩形坐标 (x, y) 转换成极坐标 (r, theta)，返回所得角 theta
        final float theta = (float) Math.atan2(endRow - startRow, endColumn - startColumn);
        //toDegrees将用弧度表示的角转换为近似相等的用角度表示的角
        final float angle = (float) Math.toDegrees(theta) + 90.0f;
        
        // compose matrix
        float sx = Math.min(mSquareWidth / mBitmapWidth, 1.0f);
        float sy = Math.min(mSquareHeight / mBitmapHeight, 1.0f);
        
       
        mArrowMatrix.setTranslate(leftX + offsetX, topY + offsetY); // transform to cell position
        mArrowMatrix.preTranslate(mBitmapWidth/2, mBitmapHeight/2);
        mArrowMatrix.preScale(sx, sy);
        mArrowMatrix.preTranslate(-mBitmapWidth/2, -mBitmapHeight/2);
        mArrowMatrix.preRotate(angle, cellWidth / 2.0f, cellHeight / 2.0f);  // rotate about cell center
        mArrowMatrix.preTranslate((cellWidth - arrow.getWidth()) / 2.0f, (cellHeight - arrow.getHeight()) / 2.0f); // translate to 12:00 pos
       
        if (!startLine) {
        	
        	canvas.drawBitmap(arrow, mArrowMatrix, mPaint);
		}
    }
	/**
	 * 提供对外访问接口方法
	 * @param onPatternListener
	 */
	public void setOnPatternListener(OnPatternListener onPatternListener) {
		
        mOnPatternListener = onPatternListener;
    }
	
	/**
	 * 对外开放图案接口
	 * @author zhouchaoxin
	 *
	 */
	public static interface OnPatternListener{
		/**
		 * 开始手势图案
		 * @param size
		 */
		void onPatternStart(int size);
		/**
		 * 清除手势图案
		 */
		void onPatternCleared();
		/**
		 * 添加手持图案
		 * @param pattern
		 */
		void onPatternCellAdded(List<Cell> pattern);
		/**
		 * 检测一个手势图案
		 * @param pattern
		 */
		void onPatternDetected(List<Cell> pattern);
		
	}
	/**
	 * 返回最小宽度建议应该使用的视图
	 */
	@Override
    protected int getSuggestedMinimumWidth() {
        // View should be large enough to contain 3 side-by-side target bitmaps
        return 3 * mBitmapWidth;
    }

	/**
	 * 返回最小高度建议应该使用的视图
	 */
    @Override
    protected int getSuggestedMinimumHeight() {
        // View should be large enough to contain 3 side-by-side target bitmaps
        return 3 * mBitmapHeight;
    }
    /**
     * 测试范围
     * @param measureSpec
     * @param desired
     * @return
     */
    private int resolveMeasured(int measureSpec, int desired)
    {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mInputEnabled || !isEnabled()) {
            return false;
        }

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: //按下
                handleActionDown(event);
                return true;
            case MotionEvent.ACTION_UP: //抬起
                handleActionUp(event);
                return true;
            case MotionEvent.ACTION_MOVE://移动
                handleActionMove(event);
                return true;
            case MotionEvent.ACTION_CANCEL://取消
                resetPattern();
                mPatternInProgress = false;
                notifyPatternCleared();
                if (PROFILE_DRAWING) {
                    if (mDrawingProfilingStarted) {
                        Debug.stopMethodTracing();
                        mDrawingProfilingStarted = false;
                    }
                }
                return true;
        }
        return false;
    }
    /**
     * 按下事件
     * @param event
     */
    private void handleActionDown(MotionEvent event) {
        resetPattern();
        final float x = event.getX();
        final float y = event.getY();
        
        final Cell hitCell = detectAndAddHit(x, y);
        
        final int patternSize = mPatternNumber.size();
        if (hitCell != null) {
            mPatternInProgress = true;
            mPatternDisplayMode = DisplayMode.Correct;
            notifyPatternStarted(patternSize);
        } else {
            mPatternInProgress = false;
            notifyPatternCleared();
        }
        if (hitCell != null) {
            final float startX = getCenterXForColumn(hitCell.column);
            final float startY = getCenterYForRow(hitCell.row);

            final float widthOffset = mSquareWidth / 2f;
            final float heightOffset = mSquareHeight / 2f;

            invalidate((int) (startX - widthOffset), (int) (startY - heightOffset),
                    (int) (startX + widthOffset), (int) (startY + heightOffset));
        }
        mInProgressX = x;
        mInProgressY = y;
        /*if (PROFILE_DRAWING) {
            if (!mDrawingProfilingStarted) {
            	//Android应用程序提供了不同的调试方法,包括跟踪和分配数量。
            	//开始跟踪方法,指定跟踪日志文件的名字
                Debug.startMethodTracing("LockPatternDrawing");
                
                mDrawingProfilingStarted = true;
            }
        }*/
    }
    
    /**
     * 抬起事件
     * @param event
     */
    private void handleActionUp(MotionEvent event) {
        // report pattern detected
        if (!mPatternNumber.isEmpty()) {
            mPatternInProgress = false;
            notifyPatternDetected();
            invalidate();
        }
        /*if (PROFILE_DRAWING) {
            if (mDrawingProfilingStarted) {
                Debug.stopMethodTracing();
                mDrawingProfilingStarted = false;
            }
        }*/
    }
    /**
     * 移动事件
     * @param event
     */
    private void handleActionMove(MotionEvent event) {
        // Handle all recent motion events so we don't skip any cells even when the device
        // is busy...
        final int historySize = event.getHistorySize();
        for (int i = 0; i < historySize + 1; i++) {
            final float x = i < historySize ? event.getHistoricalX(i) : event.getX();
            final float y = i < historySize ? event.getHistoricalY(i) : event.getY();
            final int patternSizePreHitDetect = mPatternNumber.size();
            //Log.v("lock", "patternSizePreHitDetect图案数量========="+patternSizePreHitDetect);
            //System.out.println("patternSizePreHitDetect图案数量========="+patternSizePreHitDetect);
            Cell hitCell = detectAndAddHit(x, y);
            final int patternSize = mPatternNumber.size();
            if (hitCell != null && patternSize == 1) {
                mPatternInProgress = true;
                notifyPatternStarted(patternSize);
            }
            // note current x and y for rubber banding of in progress patterns
            final float dx = Math.abs(x - mInProgressX);
            final float dy = Math.abs(y - mInProgressY);
            if (dx + dy > mSquareWidth * 0.01f) {
                float oldX = mInProgressX;
                float oldY = mInProgressY;

                mInProgressX = x;
                mInProgressY = y;

                if (mPatternInProgress && patternSize > 0) {
                    final ArrayList<Cell> pattern = mPatternNumber;
                    final float radius = mSquareWidth * mDiameterFactor * 0.5f;

                    final Cell lastCell = pattern.get(patternSize - 1);

                    float startX = getCenterXForColumn(lastCell.column);
                    float startY = getCenterYForRow(lastCell.row);

                    float left;
                    float top;
                    float right;
                    float bottom;

                    final Rect invalidateRect = mInvalidate;

                    if (startX < x) {
                        left = startX;
                        right = x;
                    } else {
                        left = x;
                        right = startX;
                    }

                    if (startY < y) {
                        top = startY;
                        bottom = y;
                    } else {
                        top = y;
                        bottom = startY;
                    }

                    // Invalidate between the pattern's last cell and the current location
                    invalidateRect.set((int) (left - radius), (int) (top - radius),
                            (int) (right + radius), (int) (bottom + radius));

                    if (startX < oldX) {
                        left = startX;
                        right = oldX;
                    } else {
                        left = oldX;
                        right = startX;
                    }

                    if (startY < oldY) {
                        top = startY;
                        bottom = oldY;
                    } else {
                        top = oldY;
                        bottom = startY;
                    }

                    // Invalidate between the pattern's last cell and the previous location
                    invalidateRect.union((int) (left - radius), (int) (top - radius),
                            (int) (right + radius), (int) (bottom + radius));

                    // Invalidate between the pattern's new cell and the pattern's previous cell
                    if (hitCell != null) {
                        startX = getCenterXForColumn(hitCell.column);
                        startY = getCenterYForRow(hitCell.row);

                        if (patternSize >= 2) {
                            // (re-using hitcell for old cell)
                            hitCell = pattern.get(patternSize - 1 - (patternSize - patternSizePreHitDetect));
                            oldX = getCenterXForColumn(hitCell.column);
                            oldY = getCenterYForRow(hitCell.row);

                            if (startX < oldX) {
                                left = startX;
                                right = oldX;
                            } else {
                                left = oldX;
                                right = startX;
                            }

                            if (startY < oldY) {
                                top = startY;
                                bottom = oldY;
                            } else {
                                top = oldY;
                                bottom = startY;
                            }
                        } else {
                            left = right = startX;
                            top = bottom = startY;
                        }

                        final float widthOffset = mSquareWidth / 2f;
                        final float heightOffset = mSquareHeight / 2f;

                        invalidateRect.set((int) (left - widthOffset),
                                (int) (top - heightOffset), (int) (right + widthOffset),
                                (int) (bottom + heightOffset));
                    }

                    invalidate(invalidateRect);
                } else {
                    invalidate();
                }
            }
        }
    }
    /**
     * 重新设置手势状态
     */
    private void resetPattern() {
    	mPatternNumber.clear();
        clearPatternDrawLookup();
        mPatternDisplayMode = DisplayMode.Correct;
        invalidate();
    }
    /**
     * 通知调用清除手势图案接口
     */
    private void notifyPatternCleared() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternCleared();
        }
        sendAccessEvent(R.string.lockscreen_access_pattern_cleared);
    }
    
    /**
     * 通知调用开始手势图案接口
     * @param size
     */
    private void notifyPatternStarted(int size) {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternStart(size);
        }
        sendAccessEvent(R.string.lockscreen_access_pattern_start);
    }
    /**
     * 通知调用添加手势图案接口
     */
    private void notifyCellAdded() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternCellAdded(mPatternNumber);
        }
        sendAccessEvent(R.string.lockscreen_access_pattern_cell_added);
    }
    /**
     * 通知调用检测手势图案接口
     */
    private void notifyPatternDetected() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternDetected(mPatternNumber);
        }
        sendAccessEvent(R.string.lockscreen_access_pattern_detected);
    }
    /**
     * 设置显示是否通过手势方式
     * 设置当前的显示手势方式的模式
     * @param displayMode
     */
    public void setDisplayMode(DisplayMode displayMode) {
        mPatternDisplayMode = displayMode;
        if (displayMode == DisplayMode.Animate) {
            if (mPatternNumber.size() == 0) {
                throw new IllegalStateException("you must have a pattern to "
                        + "animate if you want to set the display mode to animate");
            }
            mAnimatingPeriodStart = SystemClock.elapsedRealtime();
            final Cell first = mPatternNumber.get(0);
            mInProgressX = getCenterXForColumn(first.getColumn());
            mInProgressY = getCenterYForRow(first.getRow());
            clearPatternDrawLookup();
        }
        invalidate();
    }
    /**
     * 
     * @param displayMode
     * @param pattern
     */
    public void setPattern(DisplayMode displayMode, List<Cell> pattern) {
    	mPatternNumber.clear();
    	mPatternNumber.addAll(pattern);
        clearPatternDrawLookup();
        for (Cell cell : pattern) {
            mPatternDrawLookup[cell.getRow()][cell.getColumn()] = true;
        }

        setDisplayMode(displayMode);
    }
    /**
     * 保存密码
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState,
                Utils.patternToString(mPatternNumber),
                mPatternDisplayMode.ordinal(),
                mInputEnabled, mInStealthMode, mEnableHapticFeedback);
    }
    /**
     * 视图重新应用曾表示其内部状态
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setPattern(DisplayMode.Correct,Utils.stringToPattern(ss.getSerializedPattern()));
        mPatternDisplayMode = DisplayMode.values()[ss.getDisplayMode()];
        mInputEnabled = ss.isInputEnabled();
        mInStealthMode = ss.isInStealthMode();
        mEnableHapticFeedback = ss.isTactileFeedbackEnabled();
    }
    /**
     * 添加单元格
     * @param x
     * @param y
     * @return
     */
    private Cell detectAndAddHit(float x, float y) {
        final Cell cell = checkForNewHit(x, y);
        if (cell != null) {

            // check for gaps in existing pattern
            Cell fillInGapCell = null;
            final ArrayList<Cell> pattern = mPatternNumber;
            if (!pattern.isEmpty()) {
                final Cell lastCell = pattern.get(pattern.size() - 1);
                int dRow = cell.row - lastCell.row;
                int dColumn = cell.column - lastCell.column;

                int fillInRow = lastCell.row;
                int fillInColumn = lastCell.column;

                if (Math.abs(dRow) == 2 && Math.abs(dColumn) != 1) {
                    fillInRow = lastCell.row + ((dRow > 0) ? 1 : -1);
                }

                if (Math.abs(dColumn) == 2 && Math.abs(dRow) != 1) {
                    fillInColumn = lastCell.column + ((dColumn > 0) ? 1 : -1);
                }

                fillInGapCell = Cell.of(fillInRow, fillInColumn);
            }

            if (fillInGapCell != null &&
                    !mPatternDrawLookup[fillInGapCell.row][fillInGapCell.column]) {
                addCellToPattern(fillInGapCell);
            }
            addCellToPattern(cell);
            if (mEnableHapticFeedback) {
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                        | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            }
            return cell;
        }
        return null;
    }
    /**
     * 检查新的
     * @param x
     * @param y
     * @return
     */
    private Cell checkForNewHit(float x, float y) {

        final int rowHit = getRowHit(y);
        if (rowHit < 0) {
            return null;
        }
        final int columnHit = getColumnHit(x);
        if (columnHit < 0) {
            return null;
        }

        if (mPatternDrawLookup[rowHit][columnHit]) {
            return null;
        }
        return Cell.of(rowHit, columnHit);
    }
    /**
     * 获取单元格行
     * @param y
     * @return
     */
    private int getRowHit(float y) {

        final float squareHeight = mSquareHeight;
        float hitSize = squareHeight * mHitFactor;

        float offset = getPaddingTop() + (squareHeight - hitSize) / 2f;
        for (int i = 0; i < 3; i++) {

            final float hitTop = offset + squareHeight * i;
            if (y >= hitTop && y <= hitTop + hitSize) {
                return i;
            }
        }
        return -1;
    }
    /**
     * 获取单元格列
     * @param x
     * @return
     */
    private int getColumnHit(float x) {
        final float squareWidth = mSquareWidth;
        float hitSize = squareWidth * mHitFactor;

        float offset = getPaddingLeft() + (squareWidth - hitSize) / 2f;
        for (int i = 0; i < 3; i++) {

            final float hitLeft = offset + squareWidth * i;
            if (x >= hitLeft && x <= hitLeft + hitSize) {
                return i;
            }
        }
        return -1;
    }
    
    private void sendAccessEvent(int resId) {
        setContentDescription(getContext().getString(resId));
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        setContentDescription(null);
    }
    
    private float getCenterXForColumn(int column) {
    	
        return getPaddingLeft() + column * mSquareWidth + mSquareWidth / 2f;
    }

    private float getCenterYForRow(int row) {
        return getPaddingTop() + row * mSquareHeight + mSquareHeight / 2f;
    }
    
    private void addCellToPattern(Cell newCell) {
        mPatternDrawLookup[newCell.getRow()][newCell.getColumn()] = true;
        mPatternNumber.add(newCell);
        notifyCellAdded();
        
        
    }
    
    
    /**
     * 显示是否通过手势方式
     * How to display the current pattern.
     */
    public enum DisplayMode {

        /**
         * The pattern drawn is correct (i.e draw it in a friendly color)
         * 密码正确
         */
        Correct,

        /**
         * Animate the pattern (for demo, and help).
         */
        Animate,

        /**
         * The pattern is wrong (i.e draw a foreboding color)
         * 密码错误
         */
        Wrong
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int viewWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
        int viewHeight = resolveMeasured(heightMeasureSpec, minimumHeight);

        switch (mAspect) {
            case ASPECT_SQUARE:
                viewWidth = viewHeight = Math.min(viewWidth, viewHeight);
                break;
            case ASPECT_LOCK_WIDTH:
                viewHeight = Math.min(viewWidth, viewHeight);
                break;
            case ASPECT_LOCK_HEIGHT:
                viewWidth = Math.min(viewWidth, viewHeight);
                break;
        }
              Log.v("lock", "LockPatternView dimensions: " + viewWidth + "x" + viewHeight);
              setMeasuredDimension(viewWidth, viewHeight);
    }
        
	/**
	 * 添加图形资源返回Bitmap
	 * @param resId
	 * @return
	 */
	private Bitmap getBitmapFor(int resId) {
        return BitmapFactory.decodeResource(getContext().getResources(), resId);
    }
	
	/**
	 * 清除单元格
     * Clear the pattern lookup table.
     */
    private void clearPatternDrawLookup() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mPatternDrawLookup[i][j] = false;
            }
        }
    }
	/**
	 * 单元格类(由row\column组成)
	 * @author zhouchaoxin
	 * 3 X 3 = 9 个单元格
	 */
	public static class Cell{
		/**
		 * 行
		 */
		int row;
		/**
		 * 列
		 */
        int column;
        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }
        /**
         * 构造函数
         * @param row
         * @param column
         */
        private Cell(int row, int column) {
            checkRange(row, column);
            this.row = row;
            this.column = column;
        }
        /**
         * 初始化单元格为二维数组
         */
        static Cell[][] sCells = new Cell[3][3];
        static {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    sCells[i][j] = new Cell(i, j);
                }
            }
        }
        /**
         * 检查单元格 行\列的范围 是否在 0-2
         * @param row
         * @param column
         */
        private static void checkRange(int row, int column) {
            if (row < 0 || row > 2) {
                throw new IllegalArgumentException("row must be in range 0-2");
            }
            if (column < 0 || column > 2) {
                throw new IllegalArgumentException("column must be in range 0-2");
            }
        }
        
        /**
         * 
         * @param row
         * @param column
         * @return
         */
        public static synchronized Cell of(int row, int column) {
            checkRange(row, column);
            return sCells[row][column];
        }
        /**
         * 重写toString()
         */
        @Override
		public String toString() {
            return "(row=" + row + ",clmn=" + column + ")";
        }
	}
	
	/**
     * The parecelable for saving and restoring a lock pattern view.
     * 保存和恢复的parecelable锁模式的观点
     */
    private static class SavedState extends BaseSavedState {

        private final String mSerializedPattern;
        private final int mDisplayMode;
        private final boolean mInputEnabled;
        private final boolean mInStealthMode;
        private final boolean mTactileFeedbackEnabled;

        /**
         * Constructor called from {@link LockPatternView#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, String serializedPattern, int displayMode,
                boolean inputEnabled, boolean inStealthMode, boolean tactileFeedbackEnabled) {
            super(superState);
            mSerializedPattern = serializedPattern;
            mDisplayMode = displayMode;
            mInputEnabled = inputEnabled;
            mInStealthMode = inStealthMode;
            mTactileFeedbackEnabled = tactileFeedbackEnabled;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mSerializedPattern = in.readString();
            mDisplayMode = in.readInt();
            mInputEnabled = (Boolean) in.readValue(null);
            mInStealthMode = (Boolean) in.readValue(null);
            mTactileFeedbackEnabled = (Boolean) in.readValue(null);
        }

        public String getSerializedPattern() {
            return mSerializedPattern;
        }

        public int getDisplayMode() {
            return mDisplayMode;
        }

        public boolean isInputEnabled() {
            return mInputEnabled;
        }

        public boolean isInStealthMode() {
            return mInStealthMode;
        }

        public boolean isTactileFeedbackEnabled(){
            return mTactileFeedbackEnabled;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(mSerializedPattern);
            dest.writeInt(mDisplayMode);
            dest.writeValue(mInputEnabled);
            dest.writeValue(mInStealthMode);
            dest.writeValue(mTactileFeedbackEnabled);
        }

    }
}
