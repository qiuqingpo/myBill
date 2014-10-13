package com.qiuqp.mybill.ui;

import com.qiuqp.mybill.AppException;
import com.qiuqp.mybill.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.bean.Result;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.UIHelper;

public class AboutUs extends BaseActivity {
	private AppContext appContext;
	private TextView aboutus;
	public EditText qustionNote;
	public EditText nickName;
	public EditText contactaddress;
	public Spinner contacttype;
	public Button  btnsubmit;
    public Button btnback;
	private LinearLayout linquestion;
	public String hintString;
	private ProgressDialog mProgress;
	//变量值
	private String nickNameStr;
	private String contactType;
	private String contactAddress;
	private String notes;
	private int newTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.aboutus);
		appContext=(AppContext)getApplication();
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //加载时不显示键盘
		findIdByView();
		setTitle();
		textChangeBind();
		bindType();
		setListener();
		initActionBar();
		}
	private void findIdByView()
	{
		aboutus=(TextView)findViewById(R.id.aboutustext);
		linquestion=(LinearLayout)findViewById(R.id.linquestion);
		qustionNote=(EditText)findViewById(R.id.question_input);
		nickName=(EditText)findViewById(R.id.nick_name);
		contactaddress=(EditText)findViewById(R.id.contact_address);
		contacttype=(Spinner)findViewById(R.id.contacttype);
		btnsubmit=(Button)findViewById(R.id.submit);
		btnback=(Button)findViewById(R.id.back);
		hintString=qustionNote.getHint().toString();
	}
	private void setTitle()
	{
		Intent intent=getIntent();
		String type=intent.getStringExtra("type");
		if(type.equals("0"))
		{
			//getResources().getString(id)
			newTitle=R.string.set_aboutus;
			//title.setText(R.string.set_aboutus);
			linquestion.setVisibility(View.GONE);
		}
		else if(type.equals("1"))
		{
			aboutus.setVisibility(View.GONE);
			newTitle=R.string.set_qustion;
			//title.setText(R.string.set_qustion);
		}
	}
    private void textChangeBind()
    {
    	qustionNote.setOnFocusChangeListener(new OnFocusChangeListener(){

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(hasFocus){
                	qustionNote.setHint(null);
                }else{
                	qustionNote.setHint(hintString);
                }
            }
            
        });
    }
    private void bindType()
    {
    	ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.arrcontact));
    	provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    	contacttype.setAdapter(provinceAdapter);
    }
	private void setListener()
	{
		btnsubmit.setOnClickListener(submitListener);
		btnback.setOnClickListener(backListener);
	}
	private OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			 nickNameStr=nickName.getText().toString();
			 contactType=contacttype.getSelectedItem().toString();
			 contactAddress=contactaddress.getText().toString();
			 notes=qustionNote.getText().toString();
			if(StringUtils.isEmpty(nickNameStr)){
					UIHelper.ToastMessage(v.getContext(), "请输入昵称!");
					return;
				}
			if(StringUtils.isEmpty(notes)){
				UIHelper.ToastMessage(v.getContext(), "请输入意见及反馈!");
				return;
			}
			
			//String newstr=String.format("昵称：{0}<br />，{1}：{2}<br />，问题及反馈：{3}", nickNameStr,contactType,contactAddress,notes);
			mProgress = ProgressDialog.show(v.getContext(), null, "提交中···",true,true); 	
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(AboutUs.this, res.getErrorMessage());
						/*if(res.OK()){
							
						}*/
					}
					else {
						((AppException)msg.obj).makeToast(AboutUs.this);
					}
				}
			};
			new Thread(){
				@Override
				public void run() {
					Message msg = new Message();
					Result res = new Result();
					try {
						res = appContext.PostQuestion(nickNameStr, contactType, contactAddress, notes, 1);
						msg.what = 1;
						msg.obj = res;
		            } catch (AppException e) {
		            	e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
		            }
					handler.sendMessage(msg);
				}
			}.start();
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
		ActionBar actionBar =getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(newTitle);
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
