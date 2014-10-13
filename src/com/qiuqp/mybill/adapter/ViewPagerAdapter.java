package com.qiuqp.mybill.adapter;

import java.util.List;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.AppStart;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.TimeUtils;
import com.qiuqp.mybill.db.DBManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @{# ViewPagerAdapter.java Create on 2013-5-2 下午11:03:39
 * 
 *     class desc: 引导页面适配器
 * 
 *     <p>
 *     Copyright: Copyright(c) 2013
 *     </p>
 * @Version 1.0
 * @Author <a href="mailto:gaolei_xj@163.com">Leo</a>
 * 
 * 
 */
public class ViewPagerAdapter extends PagerAdapter {

    // 界面列表
    private List<View> views;
    private Activity activity;
    private AppContext appContext;

    public ViewPagerAdapter(List<View> views, Activity _activity,AppContext _appContext) {
        this.views = views;
        this.activity = _activity;
        this.appContext=_appContext;
    }

    // 销毁arg1位置的界面
    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(views.get(arg1));
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    // 获得当前界面数
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    // 初始化arg1位置的界面
    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(views.get(arg1), 0);
        if (arg1 == views.size() - 1) {
            Button mStartWeiboImageButton = (Button) arg0
                    .findViewById(R.id.iv_start_mybill);
            mStartWeiboImageButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 设置已经引导
                    setGuided();
                    goHome();
                }

            });
        }
        return views.get(arg1);
    }

    private void goHome() {
        // 跳转
        Intent intent = new Intent(activity, AppStart.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 
     * method desc：设置已经引导过了，下次启动不用再次引导
     */
    private void setGuided() {
    	
       /* SharedPreferences preferences = activity.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        // 存入数据
        editor.putBoolean("isFirstIn", false);
        // 提交修改
        editor.commit();*/
    	if(!StringUtils.toBool(appContext.getProperty("isFirstIn")))
    	{
    		DBManager mgr=new DBManager(appContext);
    		mgr.CreateDataBase();
    	}
    	
		appContext.setUpdateLimitTime(TimeUtils.getFormatTime(appContext.getMaxUpdateDate(7)));
    	appContext.setProperty("isFirstIn", String.valueOf(true));
    	appContext.setProperty("curVersion",String.valueOf(appContext.getCurrentVersion()));
    }

    // 判断是否由对象生成界面
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

}