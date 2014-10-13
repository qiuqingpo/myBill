package com.qiuqp.mybill.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.UIHelper;
import com.qiuqp.mybill.common.Utils;


public class SettingPwd extends BaseActivity {
	private AppContext appContext;// 全局Context
	private RelativeLayout pwdopen_set;
	private RelativeLayout pwd_set;
	private RelativeLayout pwdanswer_set;
	private ImageView imgSetPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingpwd);
		appContext = (AppContext) getApplication();
		findViewsById();
		setListener();
		initActionBar();
		}
	private void  findViewsById()
	{
		pwdopen_set=(RelativeLayout)findViewById(R.id.set_pwdopen);
		pwd_set=(RelativeLayout)findViewById(R.id.set_pwd);
		pwdanswer_set=(RelativeLayout)findViewById(R.id.set_pwd_answer);
		imgSetPwd=(ImageView)findViewById(R.id.imgSetPwd);
		setImageViewBackgroud(imgSetPwd,appContext.isOpenPwd());
	}
	private void setListener()
	{
		pwdopen_set.setOnClickListener(pwdOpenListener);
		pwd_set.setOnClickListener(pwdSetListener);
		pwdanswer_set.setOnClickListener(pwdAnswerListener);
		
	}
	private OnClickListener pwdOpenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean ischecked=appContext.isOpenPwd()==false?true:false;
 			appContext.setConfigOpenPwd(ischecked);
 			setImageViewBackgroud(imgSetPwd,ischecked);
 			if(ischecked&&StringUtils.isEmpty(Utils.getLockPaternString()))
 			{
 				UIHelper.showSettingPwd_Gusture(v.getContext(), "0");
 			}
		}
	};
	private OnClickListener pwdSetListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UIHelper.showSettingPwd_Gusture(v.getContext(), "0");
		}
	};
	private OnClickListener pwdAnswerListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			UIHelper.showSettingPwd_Answer(v.getContext(),"0");
		}
	};
	private void setImageViewBackgroud(ImageView imgView,boolean ischecked)
	{
		if(ischecked)
		{
			imgView.setBackgroundResource(R.drawable.btn_check_on_normal);
		}
		else
		{
			imgView.setBackgroundResource(R.drawable.btn_check_off_normal);
		}
	}
	private void initActionBar()
	{
		//actionbar初始化
		setOverflowShowAlways();
		//actionBarTitle  = getTitle();
		ActionBar actionBar =getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(R.string.set_pwd);
	}

			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
			
				switch (item.getItemId()) {
				case android.R.id.home:
					finish();
			        break;
		}
				return true;
}
}
