package com.qiuqp.mybill.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.MotionEvent;  
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;

import com.qiuqp.mybill.R;
import com.qiuqp.mybill.bean.Bill;
import com.qiuqp.mybill.bean.BillType;
import com.qiuqp.mybill.common.CalExpression;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.UIHelper;

public class RecordEditor extends BaseActivity {
	   public final static int SHOW_DATEPICKER = 1;  
	   public final static int SHOW_TimePICKER = 2;  
	   
	   public String hintString;
	   public Button showDate;
	   public Button showTime;
	   public Button btnSave;
	   public Button btnBack;
	   public EditText consumeNote;
	   public EditText consumePrice;
	   public Spinner consumetype;
	   public ImageView btnTypeAdd;
	   public RadioGroup radioFeeType;
	   public RadioButton radioSel;
	   public String bid;
	   public String allPrice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_editor);
		findViewById();
		bindShowDateTime();
	
		setListener();
		setInitData();
		textChangeBind();
		//标题栏
		initActionBar();
		}
	private void findViewById()
	{
		showDate=(Button)findViewById(R.id.show_date);
		showTime=(Button)findViewById(R.id.show_time);
		btnSave=(Button)findViewById(R.id.save);
		btnBack=(Button)findViewById(R.id.back);
		btnTypeAdd=(ImageView)findViewById(R.id.add_type);
		consumeNote=(EditText)findViewById(R.id.consume_desc);
		consumePrice=(EditText)findViewById(R.id.consume_amount);
		radioFeeType=(RadioGroup)findViewById(R.id.radioGroup_feetype_id);
		consumetype=(Spinner)findViewById(R.id.consume_type);
		hintString=consumeNote.getHint().toString();
	}
	private void setInitData()
	{
		Intent intent=getIntent();
		bid=intent.getStringExtra("id");
		if(!StringUtils.isEmpty(bid))
		{
			Bill b=mgr.getOneBill(bid);
			if(b!=null)
			{
			     if(b.getFeeType()==1)
			     radioFeeType.check(R.id.consume_id);
				 else
				 radioFeeType.check(R.id.income_id);
			     bindType(b.getFeeType());
				  setSpinnerItemSelectedByValue(consumetype,b.getType());
				  consumePrice.setText(b.getPrice());
				  consumeNote.setText(b.getNotes());
				  showDate.setText(b.getDate().toString().substring(0, 10));
				  showTime.setText(b.getDate().toString().substring(11, 16));
			}
		}
		else
		{
			radioFeeType.check(R.id.consume_id);
			radioSel=(RadioButton)findViewById(radioFeeType.getCheckedRadioButtonId());
			bindType(StringUtils.toInt(radioSel.getTag().toString()));
		}
	}
	private void bindShowDateTime()
	{
		/*SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd hh:mm");     
		String   date   =   sDateFormat.format(new   java.util.Date());  */
		Calendar c = Calendar.getInstance(); 
		
		showDate.setText(c.get(Calendar.YEAR)+"-"+ String.format("%02d", (c.get(Calendar.MONTH)+1))+"-"+String.format("%02d", (c.get(Calendar.DAY_OF_MONTH))));
		showTime.setText(String.format("%02d", (c.get(Calendar.HOUR_OF_DAY)))+":"+String.format("%02d", (c.get(Calendar.MINUTE))));
		showDate.setOnTouchListener(new OnTouchListener() {  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                // 调用Activity的showDialog(id)方法显示日期选择对话框。  
                showDialog(SHOW_DATEPICKER);  
                return false;  
            }  
        });  
		 showTime.setOnTouchListener(new OnTouchListener() {  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                // 调用Activity的showDialog(id)方法显示日期选择对话框。  
                showDialog(SHOW_TimePICKER);  
                return false;  
            }  
        });  
	}
	 // 复写Activity的onCreateDialog()方法，创建一个日期选择对话框  
    // 点击输入框时，弹出日期选择对话框。  
    // 如果输入框中有日期，则显示输入的日期；否则，显示当前日期。  
    @Override  
    protected Dialog onCreateDialog(int id) {  
        switch(id) {  
        case SHOW_DATEPICKER:  
            int year,month,day;  
            if (showDate.getText().toString() != null && showDate.getText().toString().matches("^\\d{4}-\\d{2}-\\d{2}$")) {  
                year = Integer.valueOf(showDate.getText().toString().substring(0,4));  
                month = Integer.valueOf(showDate.getText().toString().substring(5,7))-1;  
                day = Integer.valueOf(showDate.getText().toString().substring(8,10));  
            }  
            else {  
                Calendar c = Calendar.getInstance();  
                year = c.get(Calendar.YEAR);  
                month = c.get(Calendar.MONTH);  
                day = c.get(Calendar.DAY_OF_MONTH);  
            }  
            DatePickerDialog dp = new DatePickerDialog(this, dateChangeListener, year, month, day);  
            return dp;  
        case SHOW_TimePICKER:
            int hour,minute;  
            if (showTime.getText().toString() != null && showTime.getText().toString().matches("^\\d{2}:\\d{2}$")) {  
            	hour = Integer.valueOf(showTime.getText().toString().substring(0,2));  
            	minute = Integer.valueOf(showTime.getText().toString().substring(3));  
            }  
            else {  
                Calendar c = Calendar.getInstance();  
                hour = c.get(Calendar.HOUR_OF_DAY);  
                minute = c.get(Calendar.MINUTE);  
            }  
            TimePickerDialog tp=new TimePickerDialog(this, timeChangeListener, hour,minute,true);  
           // DatePickerDialog dp = new DatePickerDialog(this, dateChangeListener, year, month, day);  
            return tp;
        }  
        return null;  
    }  
  
      // 事件监听。当用户选择好日期后，单击Set按钮时触发。  
      // 这里用户选择好日期后，单击Set按钮，将用户选择的日期显示到输入框。  
        DatePickerDialog.OnDateSetListener dateChangeListener = new OnDateSetListener() {  
        @Override  
        public void onDateSet(DatePicker view, int year, int monthOfYear,  
                int dayOfMonth) {  
        	showDate.setText(year + "-" + String.format("%02d",(monthOfYear + 1)) + "-" + String.format("%02d",dayOfMonth));  
        }  
    };  
     TimePickerDialog.OnTimeSetListener timeChangeListener = new OnTimeSetListener() {  
        @Override  
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {  
        	showTime.setText(String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute));  
        }  
    };  
    private void textChangeBind()
    {
    	consumeNote.setOnFocusChangeListener(new OnFocusChangeListener(){

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(hasFocus){
                	consumeNote.setHint(null);
                }else{
                	consumeNote.setHint(hintString);
                }
            }
            
        });
    	
    	/*consumePrice.setOnFocusChangeListener(new OnFocusChangeListener(){

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(!hasFocus){
                	String strPrice=consumePrice.getText().toString();
        			if(StringUtils.isEmpty(strPrice)){
        				UIHelper.ToastMessage(arg0.getContext(), "消费金额不能为空!");
        				return;
        			}
                	CalExpression cal=new CalExpression();
                       allPrice=StringUtils.subZeroAndDot(cal.calculate(strPrice).toString());
                 
                	UIHelper.ToastMessage(arg0.getContext(), allPrice.toString());
                }
            }
            
        });*/
    }
    private List<String> getbilllist(int feetype)
    {
    	List<String> btype= new ArrayList<String>();
    	List<BillType> billtype= mgr.getBillType(feetype);
    	if(billtype.size()>0)
    	{
    	 for(BillType b :billtype)
    	 {
    		 btype.add(b.getTypeName());
    	 }
    	}
    	return btype;
    }
    private void bindType(int feetype)
    {
    	   List<String> billlist= getbilllist(feetype);
    	   ArrayAdapter provinceAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,billlist);
           provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 样式
           consumetype.setAdapter(provinceAdapter);
    }
    private void setListener() {
    	btnSave.setOnClickListener(submitListener);
    	btnBack.setOnClickListener(backListener);
    	btnTypeAdd.setOnClickListener(billypeListener);
    	radioFeeType.setOnCheckedChangeListener(radioFeeChangeListener);
	}
    /** 监听注册提交按钮 */
	private OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			radioSel=(RadioButton)findViewById(radioFeeType.getCheckedRadioButtonId());
			int feetype=StringUtils.toInt(radioSel.getTag().toString());
			String strCusumeType=consumetype.getSelectedItem().toString();
			String strPrice=consumePrice.getText().toString();
			
			if(StringUtils.isEmpty(strPrice)){
				UIHelper.ToastMessage(v.getContext(), "收支金额不能为空!");
				return;
			}
			strPrice=strPrice.replaceFirst("^[\\+|\\-]*", "");
		/*	if(!StringUtils.isNumeric(strPrice))
			{
				UIHelper.ToastMessage(v.getContext(), "收支金额必需以数字开头!");
				return;
			}*/
			
        	CalExpression cal=new CalExpression();
               allPrice=StringUtils.subZeroAndDot(cal.calculate(strPrice).toString());
			String strCusumeDate=showDate.getText().toString()+" "+showTime.getText().toString()+":00";
			String strCusumeNote=consumeNote.getText().toString();
			Bill bill=new Bill();
			bill.setFeeType(feetype);
			bill.setType(strCusumeType);
			bill.setPrice(allPrice);
			bill.setDate(strCusumeDate);
			bill.setNotes(strCusumeNote);
			if(!StringUtils.isEmpty(bid))
			{
				bill.setId(bid);
				mgr.updateBill(bill);
			}
			else
			{
				mgr.addBill(bill);//添加
			}
			UIHelper.showHome(RecordEditor.this);
		}
	};
    /** 监听注册返回按钮 */
	private OnClickListener backListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	     finish();
		}
	};
    /** 监听添加消费类型按钮 */
	private OnClickListener billypeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	     UIHelper.showEditBillType(v.getContext(), "", "add", 0);
		}
	};
	private OnCheckedChangeListener radioFeeChangeListener=new OnCheckedChangeListener(){
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			//在这个函数里面用来改变选择的radioButton的数值，以及与其值相关的 //任何操作，详见下文
			radioSel=(RadioButton)findViewById(checkedId);
			bindType(StringUtils.toInt(radioSel.getTag().toString()));
			}
	};
	public void setting_back(View v)
	{
		finish();
	}
/*	@Override
	protected void onResume() {  
        super.onResume();  
        bindType();
        setSpinnerItemSelectedByValue()
    } */
	@Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data)//重写onActivityResult方法
    {
          switch (resultCode)
         {
                case 1:
                	
                	 Bundle bundle = data.getExtras();  
                     String type = bundle.getString("type");  
                     int feetype=bundle.getInt("feetype");
                     bindType(feetype);
                     setSpinnerItemSelectedByValue(consumetype,type);
                break;
         }
   }
	/**
	 * 根据值, 设置spinner默认选中:
	 * @param spinner
	 * @param value
	 */
	public static void setSpinnerItemSelectedByValue(Spinner spinner,String value){
	    SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
	    int k= apsAdapter.getCount();
	    for(int i=0;i<k;i++){
	        if(value.equals(apsAdapter.getItem(i).toString())){
	            spinner.setSelection(i,true);// 默认选中项
	            break;
	        }
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
		actionBar.setTitle(R.string.action_new);
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

