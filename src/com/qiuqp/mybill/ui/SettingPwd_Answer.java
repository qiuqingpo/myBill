package com.qiuqp.mybill.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.common.UIHelper;

public class SettingPwd_Answer extends BaseActivity {
	private AppContext appContext;
	public EditText pwdAnswer;
	public Button  btnsubmit;
    public Button btnback;
	public String hintString;
	private String type;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingpwd_answer);
		appContext=(AppContext)getApplication();
		findIdByView();
		setTitle();
		setListener();
		textChangeBind();
		initActionBar();
		}
	private void findIdByView()
	{
		pwdAnswer=(EditText)findViewById(R.id.pwd_answer);
		btnsubmit=(Button)findViewById(R.id.submit);
		btnback=(Button)findViewById(R.id.back);
		hintString=pwdAnswer.getHint().toString();
	}
	private void setTitle()
	{
		Intent intent=getIntent();
	    type=intent.getStringExtra("type");
		if(type.equals("0"))
		{
			//getResources().getString(id)
			pwdAnswer.setText(appContext.getPwdAnswer());
		}
		else if(type.equals("1"))
		{
			//title.setText(R.string.set_qustion);
		}
	}
	private void textChangeBind()
    {
		pwdAnswer.setOnFocusChangeListener(new OnFocusChangeListener(){

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(hasFocus){
                	pwdAnswer.setHint(null);
                }else{
                	pwdAnswer.setHint(hintString);
                }
            }
            
        });
    }
	private void setListener()
	{
		btnsubmit.setOnClickListener(submitListener);
		btnback.setOnClickListener(backListener);
	}
	private OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String ans=pwdAnswer.getText().toString();
			if(type.equals("0"))
			{
			 appContext.setPwdAnswer(ans);
			 finish();
			}
			else if(type.equals("1"))
			{
				String oldans=appContext.getPwdAnswer();
				if(!ans.equals(oldans))
				{
					UIHelper.ToastMessage(appContext, R.string.ans_gusturepwd_errmsg);
				}
				else
				{
					UIHelper.showSettingPwd_Gusture(v.getContext(), "2");
				}
			}
		}
	};
	private OnClickListener backListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	private void initActionBar()
	{
		//actionbar初始化
		setOverflowShowAlways();
		//actionBarTitle  = getTitle();
		ActionBar actionBar =getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(R.string.ans_gusturepic);
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
