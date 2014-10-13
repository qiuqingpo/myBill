package com.qiuqp.mybill.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.bean.BillType;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.UIHelper;

public class BillTypeEdit extends BaseActivity  {
	private AppContext appContext;
	public ImageView btnAdd;
	public Button btnSave;
	public Button btnBack;
    public EditText consumeType;
    public TextView tipView;
    public RadioGroup radioFeeType;
    public RadioButton radioSel;
    public  String id;
    public String from;
    public int feetype;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.billtype_editor);
		appContext = (AppContext) getApplication();
		findIdByView();
		setListener();
		setText();
		initActionBar();
		}
	public void setting_back(View v)
	{
		finish();
	}
	private void setText()
	{
		Intent intent=getIntent();
		id=intent.getStringExtra("id");
		from=intent.getStringExtra("from");
		if(!StringUtils.isEmpty(id))
		{
			BillType b=mgr.getOneBillType(id);
			consumeType.setText(b.getTypeName());
			if(b.getFeeType()==1)
			{
			radioFeeType.check(R.id.consume_id);
			}
			else 
			radioFeeType.check(R.id.income_id);
		}
		else
		{
			radioFeeType.check(R.id.consume_id);
		}
		
	}
    private void findIdByView()
	{
   /*	 btnAdd=(ImageView)findViewById(R.id.addrecord);
   	 btnAdd.setVisibility(View.GONE);*/
 	 btnSave=(Button)findViewById(R.id.save);
 	 btnBack=(Button)findViewById(R.id.back);
	 consumeType=(EditText)findViewById(R.id.view_type);
	 tipView=(TextView)findViewById(R.id.billtipmsg);
	 radioFeeType=(RadioGroup)findViewById(R.id.radioGroup_feetype_id);
	}
    private void setListener()
    {
    	btnSave.setOnClickListener(submitListener);
    	btnBack.setOnClickListener(backListener);
    }
    /** 监听注册返回按钮 */
  	private OnClickListener backListener = new OnClickListener() {
  		@Override
  		public void onClick(View v) {
  	     finish();
  		}
  	};
    /** 监听提交按钮 */
   	private OnClickListener submitListener = new OnClickListener() {
   		@Override
   		public void onClick(View v) {
   	    	radioSel=(RadioButton)findViewById(radioFeeType.getCheckedRadioButtonId());
   		    feetype=StringUtils.toInt(radioSel.getTag().toString());
   			String strCusumeType=consumeType.getText().toString();
   			BillType b=new BillType();
   			b.setFeeType(feetype);
   			b.setTypeName(strCusumeType);
   			//空值验证
   			if(StringUtils.isEmpty(strCusumeType))
   			{
   				tipView.setText(R.string.msg_tip_empty);
   				return;
   			}
   			tipView.setText(R.string.msg_tip_check);
	   		if(mgr.checkBtypeRepeat(strCusumeType,feetype))//已经存在了
	   		{
	   				tipView.setText(R.string.msg_tip_error);
	   				consumeType.setFocusable(true);
	   				return;
	   		}
	   		tipView.setText(R.string.msg_tip_success);
	   		
   			if(StringUtils.isEmpty(id))
   			{
   			   mgr.addBillType(b);	
   			}
   			else
   			{
   			   mgr.updateBillType(b, id);
   			}
   			if(from.equals("add"))
   			{
   				Intent intent = new Intent(appContext, RecordEditor.class);
   				intent.putExtra("type", strCusumeType);
   				intent.putExtra("feetype", feetype);
   	   		    setResult(1, intent);  
   			}
   			else
   			{
   			Intent intent = new Intent(appContext, BillTypeList.class);
   		    setResult(1, intent);  
   			}
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
		actionBar.setTitle(R.string.set_billtype);
	}
            @Override
			public boolean onCreateOptionsMenu(Menu menu) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.main, menu);
				return super.onCreateOptionsMenu(menu);
			}
			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
			
				switch (item.getItemId()) {
				case android.R.id.home:
					finish();
			        break;
				case R.id.action_add:
					UIHelper.showRecordEditor(this, "");
					break;
				case R.id.action_settings:
					finish();
					break;
				case R.id.action_exit:
					UIHelper.Exit(this);
					break;
		         }
				return true;
		}
}
