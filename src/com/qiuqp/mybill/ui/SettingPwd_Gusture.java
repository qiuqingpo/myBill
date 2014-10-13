package com.qiuqp.mybill.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.common.LockView;
import com.qiuqp.mybill.common.LockView.Cell;
import com.qiuqp.mybill.common.LockView.DisplayMode;
import com.qiuqp.mybill.common.LockView.OnPatternListener;
import com.qiuqp.mybill.common.TimeUtils;
import com.qiuqp.mybill.common.UIHelper;
import com.qiuqp.mybill.common.Utils;

public class SettingPwd_Gusture extends BaseActivity {
	private AppContext appContext;
	private TextView forgetview;
	private TextView backview;
	private TextView guesturetitle;
	private LockView lockView;
	Utils utils;
	private int errNum;
	private String type;
	private int curStep=1;
	private String curCellValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingpwd_gusture);
		appContext=(AppContext)getApplication();
		getSupportActionBar().hide();//提前隐藏
		findIdByView();
		setTitle();
		setListener();
		if(!appContext.isCanUse(appContext.getPwdLimitTime())&&type.equals("1"))//不可用
		{
			UIHelper.ToastMessage(appContext, "已经输入错误达到最大次数,系统将锁定30分钟",Toast.LENGTH_SHORT);
			return;
		}
		setLockView();
		
		}
	private void findIdByView()
	{
		forgetview=(TextView)findViewById(R.id.forgetpwd);
		backview=(TextView)findViewById(R.id.back);
		guesturetitle=(TextView)findViewById(R.id.gusturepwd_title);
		lockView = (LockView) findViewById(R.id.lpv_lock);
		utils = new Utils(this);
	}
	private void setTitle()
	{
		Intent intent=getIntent();
	    type=intent.getStringExtra("type");
		if(type.equals("0"))
		{
			forgetview.setVisibility(View.GONE);
			backview.setVisibility(View.VISIBLE);
		}
		else if(type.equals("1"))
		{
			forgetview.setVisibility(View.VISIBLE);
			backview.setVisibility(View.GONE);
			guesturetitle.setText(R.string.input_gusturepwd);
		}
		else if(type.equals("2"))
		{
			forgetview.setVisibility(View.GONE);
			backview.setVisibility(View.GONE);
			guesturetitle.setText(R.string.set_gusturepwd_again);
		}
	}
	public void setting_back(View v)
	{
		finish();
	}
	private void setListener()
	{
		forgetview.setOnClickListener(submitListener);
		backview.setOnClickListener(backListener);
	}
	private OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UIHelper.showSettingPwd_Answer(v.getContext(), "1");
		}
	};
	private OnClickListener backListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	private void setLockView()
	{
	        lockView.setOnPatternListener(new OnPatternListener() {
			
			@Override
			public void onPatternStart(int size) {
				
				/*Log.i("lock", "onPatternStart=\\\\\\\\\\\\\\\\");
				Log.v("lock", "输入5次提示==size"+size);*/
			}
			
			@Override
			public void onPatternDetected(List<Cell> pattern) {
				
				if(type.equals("0")||type.equals("2"))
				{
					if(curStep==1)
					{
						curCellValue=Utils.patternToString(pattern);
						curStep++;
						//设置显示手势方式，同时可清除箭头和连接线
						lockView.setDisplayMode(DisplayMode.Wrong);
						UIHelper.ToastMessage(appContext, "请再次确认密码",Toast.LENGTH_SHORT);
					}
					else
					{
						if(curCellValue.equals(Utils.patternToString(pattern)))
						{
							utils.saveLockPattern(pattern);
							//设置显示手势方式，同时可清除箭头和连接线
							lockView.setDisplayMode(DisplayMode.Correct);
							appContext.setPwdLimitTime("");
							UIHelper.ToastMessage(appContext, "密码设置成功",Toast.LENGTH_SHORT);
							if(type.equals("2"))
							UIHelper.showHome(SettingPwd_Gusture.this);
							else
							UIHelper.showSettingPwd(SettingPwd_Gusture.this);
						}
						else
						{
						curStep=1;//清空用户输入
						curCellValue="";
						//设置显示手势方式，同时可清除箭头和连接线
						lockView.setDisplayMode(DisplayMode.Wrong);
						UIHelper.ToastMessage(appContext, "两次密码不一致，请重新输入",Toast.LENGTH_SHORT);
						}
					}
				}
				else
				{
					curCellValue=Utils.patternToString(pattern);
					String pwd=Utils.getLockPaternString();
					if(curCellValue.equals(pwd))
					{
						UIHelper.showHome(SettingPwd_Gusture.this);
					}
					else
					{
						errNum++;
						if(errNum>4)
						{
							
							Calendar c = Calendar.getInstance();
					        c.setTime(new Date());   //设置当前日期
					        c.add(Calendar.MINUTE, 30); //日期加1
					        Date date = c.getTime(); //结果
							appContext.setPwdLimitTime(TimeUtils.getFormatTime(date));
							UIHelper.ToastMessage(appContext, "已经输入错误达到最大次数,系统将锁定30分钟",Toast.LENGTH_SHORT);
							return;
						}
						UIHelper.ToastMessage(appContext, String.format("已输入错误%s次,还有%s次机会",errNum,5-errNum),Toast.LENGTH_SHORT);
					}
				}
			
			}
			
			@Override
			public void onPatternCleared() {
				
				Log.i("lock", "onPatternCleared=\\\\\\\\\\\\\\\\");
			}
			
			@Override
			public void onPatternCellAdded(List<Cell> pattern) {
				
				Log.i("lock", "onPatternCellAdded=\\\\\\\\\\\\\\\\");
				
			}
		});
	}
}
