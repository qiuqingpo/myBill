package com.qiuqp.mybill;


import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.UIHelper;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;


/**
 /**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppStart extends Activity {
	
	private AppContext appContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	appContext = (AppContext)getApplication();
    	String isFirstLogin = appContext.getProperty("isFirstIn");
    	String oldVersion = appContext.getProperty("curVersion");
    	int oldVerson_int=oldVersion==null?1:StringUtils.toInt(oldVersion);
    	int curVersion=appContext.getCurrentVersion();//当前版本大于旧的版本时进入 引导页
    	if(!StringUtils.toBool(isFirstLogin)||curVersion>oldVerson_int)
    	{
    		UIHelper.showGuide(AppStart.this);
    	}
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);
		//渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) {
				if(appContext.isOpenPwd())
				{
					UIHelper.showSettingPwd_Gusture(AppStart.this, "1");
				}
				else
				{
				UIHelper.showHome(AppStart.this);
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
			
		});
	
    }
}