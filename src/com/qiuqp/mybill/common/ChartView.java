package com.qiuqp.mybill.common;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * customized  chartsView
 * 自定义饼状图
 * @author Hogan 2012-11-07 
 *
 */

public class ChartView extends View
{
	private boolean mAa;
	private int mChartsNum;
	private ArrayList<ChartProp> mChartProps;
	private Point mCenterPoint;
	private int mR;
	private float mStartAngle;
	private int mWizardLineLength;
	private int mScreenWidth;
	private int mScreenHeight;
	
	public ChartView(Context context)
	{
		super(context);
		initParams();
	}

	
	public ChartView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initParams();
	}

	
	public ChartView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initParams();
	}

	/**
	 * initial some params 
	 * 初始化默认参数*
	 */
	private void initParams()
	{
		mAa = true;
		mChartsNum = 1;
		mChartProps = new ArrayList<ChartProp>();
		mCenterPoint = new Point(100, 100);
		mR = 50;
		mStartAngle = 0;
		mWizardLineLength = 10;
		
		WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();     
		mScreenHeight = wm.getDefaultDisplay().getHeight();   
	}

	/**
	 * create charts' property
	 * 创建饼状图的属性
	 * @param chartsNum charts' number 饼状图的个数
	 * @return charts' property's list 饼状图属性的list
	 */
	public ArrayList<ChartProp> createCharts(int chartsNum)
	{
		mChartsNum = chartsNum;
		createChartProp(chartsNum);
		return mChartProps;
	}
	
	/**
	 * set the first chart's start angle when draw
	 * 设置第一个扇形绘制时的起始角度
	 * @param startAngle the first chart's start angle 第一个扇形绘制时的起始角度
	 */
	public void setStartAngle(float startAngle)
	{
		mStartAngle  = startAngle;
		invalidate();
	}

	/**
	 * set the view anti alias.
	 * 设置是否抗锯齿。
	 * @param aa true means will draw hightly; true 意味着高质量绘图
	 */
	public void setAntiAlias(boolean aa)
	{
		mAa = aa;
		invalidate();
	}
	
	/**
	 * set chart's center point
	 * 设置饼状图的中心点
	 * @param centerPoint chart's center point 饼状图的中心点坐标
	 */
	public void setCenter(Point centerPoint)
	{
		mCenterPoint = centerPoint;
		invalidate();
	}
	
	/**
	 * set chart's radius
	 * 设置饼状图半径
	 * @param r chart's radius 饼状图的半径
	 */
	public void setR(int r)
	{
		mR  = r;
		invalidate();
	}
	
	/**
	 * set wizard line's length
	 * 设置引导线的长度。斜着的和横着的是一样长的。
	 * @param length line's length 引导线的长度
	 */
	public void setWizardLineLength(int length)
	{
		mWizardLineLength = length;
		invalidate();
	}
	
	/**
	 * actually create chartProp objects.
	 * 真正创建扇形属性的方法 
	 * @param chartsNum charts' number 饼状图的个数
	 */
	private void createChartProp(int chartsNum)
	{
		for(int i = 0; i < chartsNum; i++)
		{
			ChartProp chartProp = new ChartProp(this);
			chartProp.setId(i);
			mChartProps.add(chartProp);
		}
	}
	
	/**
	 * onTouchEvent hanlde the UP action
	 * 处理抬起事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		switch (action)
			{
			case MotionEvent.ACTION_UP:
				float x = event.getX();
				float y = event.getY();
				ChartProp clickChart = getUpChartProp(x,y);
				if(clickChart != null)
					Toast.makeText(getContext(), "You clicked Up in " + clickChart.getName(), 3000).show();
				else
					Toast.makeText(getContext(), "You clicked Up Not in any Chart!", 3000).show();
				break;

			default:
				break;
			}
		return true;
	}





	/**
	 * get the chartProp when Action_UP happened
	 * 获取当抬起时，坐标所在的charProp
	 * @param x action_up's x  up时的x坐标
	 * @param y action_up's y  up时的y坐标
	 * @return chartProp If equals null, means not in any charts! 如果返回值为null，说明不在任何的扇形内。
	 */
	private ChartProp getUpChartProp(float x, float y)
	{
		double angle = Math.atan2(y-mCenterPoint.y, x - mCenterPoint.x) * 180 / Math.PI;
		if(angle < 0)
		{
			angle = 360 + angle;
		}
		Log.d("test" , "up angle = " + angle);
		
		ChartProp chartPropPosible = getPosibleChartProp(angle);
		if(chartPropPosible != null && inChartZone(x,y))
		{
			return chartPropPosible;
		}
		
		return null;
	}

	/**
	 * judge if the action X Y in the circle.
	 * 判断抬起时，坐标是否在圆内。
	 * @param x action_up's x  up时的x坐标
	 * @param y action_up's y  up时的y坐标
	 * @return true means in circle. 返回值为true，表示在圆内。
	 */
	private boolean inChartZone(float x, float y)
	{
		float a2 = (x - mCenterPoint.x) * (x - mCenterPoint.x);
		float b2 = (y - mCenterPoint.y) * (y - mCenterPoint.y);
		float R2 = mR * mR;
		if(a2 + b2 <= R2)
		{
			return true;
		}
		return false;
	}

	/**
	 * judge if the action_up's angle is in one chartProp 
	 * 根据抬起时的角度，获取可能的ChartProp
	 * @param angle the action_up's angle 抬起时的角度
	 * @return the posible chartProp 可能的charProp。因为还要判断是不是在圆内。
	 */
	private ChartProp getPosibleChartProp(double angle)
	{
		int size = mChartProps.size();
		for(int i =0 ; i < size; i++)
		{
			ChartProp chartProp = mChartProps.get(i);
			Log.i("test" , "chartProp S angle = " + chartProp.getStartAngle() + ", chartProp E angle = " + chartProp.getEndAngle());
			if((angle > chartProp.getStartAngle() && angle <= chartProp.getEndAngle())
			 ||(angle + 360 > chartProp.getStartAngle() && angle + 360 <= chartProp.getEndAngle()))
			{
				return chartProp;
			}
		}
		return null;
	}

	/**
	 * draw
	 * 具体绘制
	 * TODO 绘制时，可能会出现超过屏幕高度的情况。这里需要进一步改善。
	 * 改善方法，可参考代码中画省略号的代码部分。（305 ~ 373 行）
	 */
	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		
		Paint paint = new Paint();
		paint.setAntiAlias(mAa);
		
		float startAngle = mStartAngle;
		int size = mChartProps.size();
		RectF oval = new RectF(mCenterPoint.x - mR, 
								mCenterPoint.y - mR, 
								mCenterPoint.x + mR, 
								mCenterPoint.y + mR);
		
		for(int i= 0; i < size; i++)
		{
			ChartProp chartProp = mChartProps.get(i);
			
			//drawArc
			paint.setColor(chartProp.getColor());
			float sweepAngle = chartProp.getSweepAngle();
			canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
			
			
			//drawWizardLines -----splash line
			float wizardLineAngle =  (float) ((startAngle + sweepAngle / 2) * Math.PI / 180);
			float deltaR = mR - mWizardLineLength/2;
			double sinAngle =  Math.sin(wizardLineAngle);
			double cosAngle =  Math.cos(wizardLineAngle);
			int deltaXs = (int) (deltaR * cosAngle);
			int deltaYs = (int) (deltaR * sinAngle);
			int deltaXl = (int) (mWizardLineLength * cosAngle);
			int deltaYl = (int) (mWizardLineLength * sinAngle);
			Point lineSplashStart = new Point(mCenterPoint.x + deltaXs, mCenterPoint.y + deltaYs);
			Point lineSplashEnd = new Point(lineSplashStart.x + deltaXl, lineSplashStart.y + deltaYl);
			paint.setColor(Color.WHITE);
			canvas.drawLine(lineSplashStart.x, lineSplashStart.y, lineSplashEnd.x, lineSplashEnd.y, paint);
			
			//drawWizardLines -----horizonal line
			if(lineSplashEnd.x <= mCenterPoint.x) //in left of circle
			{
				canvas.drawLine(lineSplashEnd.x - mWizardLineLength, lineSplashEnd.y, lineSplashEnd.x, lineSplashEnd.y, paint);
			}
			else //in right of circle
			{
				canvas.drawLine(lineSplashEnd.x , lineSplashEnd.y, lineSplashEnd.x + mWizardLineLength, lineSplashEnd.y, paint);
			}
			
			//drawText
			String name = chartProp.getName();
			int nameLen = name.length();
			
			paint.setTextSize(chartProp.getFontSize());
			Rect rect = new Rect();
			paint.getTextBounds(name, 0, nameLen, rect); 
			int nameWidth = rect.width();
			int nameHeight = rect.height();
			
			String slStr = "...";
			paint.getTextBounds(slStr, 0, slStr.length(), rect); 
			int slWidth = rect.width();
			
			if(lineSplashEnd.x <= mCenterPoint.x) //in left of circle
			{
				int endX = lineSplashEnd.x - mWizardLineLength;
				int endY = lineSplashEnd.y;
				if(nameWidth > endX)
				{
					int j = nameLen - 1;
					while(j >= 0)
					{
						String subNameString = name.substring(0, j);
						paint.getTextBounds(subNameString, 0, subNameString.length(), rect); 
						int subNameStrWidth = rect.width();
						if(subNameStrWidth + slWidth <= endX)
						{
							break;
						}
						j--;
					}
					String drawTextString = name.substring(0, j) + slStr;
					paint.getTextBounds(drawTextString, 0, drawTextString.length(), rect); 
					canvas.drawText(drawTextString , endX - rect.width(), endY + nameHeight / 2, paint);
				}
				else 
				{
					
					canvas.drawText(name, endX - nameWidth, endY + nameHeight / 2, paint);
				}
			}
			else //in right of circle
			{
				int endX = lineSplashEnd.x + mWizardLineLength;
				int endY = lineSplashEnd.y;
				if(nameWidth + endX > mScreenWidth)
				{
					int j = nameLen - 1;
					while(j >= 0)
					{
						String subNameString = name.substring(0, j);
						paint.getTextBounds(subNameString, 0, subNameString.length(), rect); 
						int subNameStrWidth = rect.width();
						if(subNameStrWidth + slWidth + endX < mScreenWidth)
						{
							break;
						}
						j--;
					}
					String drawTextString = name.substring(0, j) + slStr;
					paint.getTextBounds(drawTextString, 0, drawTextString.length(), rect); 
					canvas.drawText(drawTextString , endX, endY + nameHeight / 2, paint);
				}
				else 
				{
					canvas.drawText(name, endX, endY + nameHeight / 2, paint);
				}
			}
			
			//add startAngle
			chartProp.setStartAngle(startAngle);
			startAngle += sweepAngle; 
			chartProp.setEndAngle(startAngle);
		}
	}

}
