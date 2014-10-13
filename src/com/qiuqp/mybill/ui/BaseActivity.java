
package com.qiuqp.mybill.ui;
//import com.qiuqp.i_love_smile.AppManager;
//import com.qiuqp.i_love_smile.db.DBManager;
import java.lang.reflect.Field;

import com.qiuqp.mybill.AppManager;
import com.qiuqp.mybill.db.*;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.ViewConfiguration;

/**
 *应用程序Activity的基类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-9-18
 */
public class BaseActivity extends ActionBarActivity {

	// 是否允许全屏
	private boolean allowFullScreen = true;

	// 是否允许销毁
	//private boolean allowDestroy = true;

	//private View view;

	public DBManager mgr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		super.onCreate(savedInstanceState);
		allowFullScreen = true;
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		//初始化DBManager
	    mgr=new DBManager(this);
	   
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		 if (mgr  != null) {  
			 mgr.close();  
			 }
		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	public boolean isAllowFullScreen() {
		return allowFullScreen;
	}

	/**
	 * 设置是否可以全屏
	 * 
	 * @param allowFullScreen
	 */
	public void setAllowFullScreen(boolean allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}

/*	public void setAllowDestroy(boolean allowDestroy) {
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view) {
		this.allowDestroy = allowDestroy;
		this.view = view;
	}*/
    @Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}
	@Override
    public boolean	onMenuOpened(int featureId, Menu menu)
	{
		//setOverflowIconVisible(featureId, menu);
		return true;
	}
	public void setOverflowShowAlways() {
		try {
			ViewConfiguration viewConfiguration = ViewConfiguration.get(this);
			Field field = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			field.setAccessible(true);
			field.setBoolean(viewConfiguration, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*public static void setOverflowIconVisible(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
	}*/
/*	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
			view.onKeyDown(keyCode, event);
			if (!allowDestroy) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}*/
	/*private long exitTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
             if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                             //do something
                 if((System.currentTimeMillis() - exitTime) > 2000){
                     Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();
                     exitTime = System.currentTimeMillis();
                     return true;
             }
              else
            		// 是否退出应用
      			UIHelper.Exit(this);
             }
             return super.dispatchKeyEvent(event);
    }*/
	/* @Override
     public void onBackPressed() {
             // TODO Auto-generated method stub
             if((System.currentTimeMillis() - exitTime) > 2000){
                 Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();
                 exitTime = System.currentTimeMillis();
         }else{
                 super.onBackPressed();
         }
     }*/
}
