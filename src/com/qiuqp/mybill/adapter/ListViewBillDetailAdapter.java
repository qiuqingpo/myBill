package com.qiuqp.mybill.adapter;

import java.text.ParseException;
import java.util.List;

import com.qiuqp.mybill.R;
import com.qiuqp.mybill.adapter.base.ViewHolder;
import com.qiuqp.mybill.bean.Bill;
import com.qiuqp.mybill.bean.BillTotal;
import com.qiuqp.mybill.common.DoubleUtils;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.TimeUtils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewBillDetailAdapter extends  BaseExpandableListAdapter  {
	private Context 					context;//运行上下文
	private List<BillTotal> 					groupArray;//数据集合
	private  List<List<Bill>> childArray; 
	private LayoutInflater 				listContainer;//视图容器
	private DoubleUtils dUtils;
	
	    public  ListViewBillDetailAdapter(Context context, List<BillTotal> _groupArray,List<List<Bill>> _childArray)  
	    {  
	    	this.context = context;			
			this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
			this.groupArray = _groupArray;
			this.childArray=_childArray;
		    dUtils=new DoubleUtils();
	    } 
	    @Override
	    public  Object getChild(int  groupPosition, int  childPosition)  
	    {  
	        return  childArray.get(groupPosition).get(childPosition);  
	    } 
	    @Override
	    public  long  getChildId(int  groupPosition, int  childPosition)  
	    {  
	        return  childPosition;  
	    }  
	    @Override
	    public  int  getChildrenCount(int  groupPosition)  
	    {  
	        return  childArray.get(groupPosition).size();  
	    }
	    @Override
	    public  View getChildView(int  groupPosition, int  childPosition,  
	            boolean  isLastChild, View convertView, ViewGroup parent)  
	    {  
	    	if(convertView==null)
	    	{
	    		convertView = listContainer.inflate(R.layout.list_item_detail_child, null);
	    	}
	    	TextView date = ViewHolder.get(convertView, R.id.view_time);
	    	TextView week = ViewHolder.get(convertView, R.id.view_week);
	    	
	    	TextView protect = ViewHolder.get(convertView, R.id.view_feetype);
	    	TextView type = ViewHolder.get(convertView, R.id.view_type);
	    	
	    	TextView price = ViewHolder.get(convertView, R.id.view_amount);
	    	RelativeLayout rel_child= ViewHolder.get(convertView, R.id.rel_child);
	    	TextView txt_child_empty = ViewHolder.get(convertView, R.id.txt_child_empty);
	    	
			//设置文字和图片
			Bill b = childArray.get(groupPosition).get(childPosition);
			if(b.getFlag()==1)
	    	{
				rel_child.setVisibility(View.GONE);
				txt_child_empty.setVisibility(View.VISIBLE);
				type.setTag(null);
				return convertView;
	    	}
			else
			{
				rel_child.setVisibility(View.VISIBLE);
				txt_child_empty.setVisibility(View.GONE);
			}
			String castPrice;
			String feeType;
			if(b.getFeeType()==1)
			{
				price.setTextColor(context.getResources().getColor(R.color.green));
				castPrice="-"+b.getPrice().toString();
				feeType="支出";
			}
			else
			{
				price.setTextColor(context.getResources().getColor(R.color.red));
				castPrice="+"+b.getPrice().toString();
				feeType="收入";
			}
			try {
				date.setText(TimeUtils.dayForMonth(b.getDate()));
				week.setText(TimeUtils.dayForWeek(b.getDate()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			protect.setText(b.getType());
			type.setText("["+feeType+"]");
			type.setTag(b);//设置隐藏参数(实体类)
			price.setText(castPrice);

			return convertView;
	    }  
	    // group method stub   
	    @Override
	    public  Object getGroup(int  groupPosition)  
	    {  
	        return  groupArray.get(groupPosition);  
	    }  
	    @Override
	    public  int  getGroupCount()  
	    {  
	        return  groupArray.size();  
	    }  
	    @Override
	    public  long  getGroupId(int  groupPosition)  
	    {  
	        return  groupPosition;  
	    }  
	    @Override
	    public  View getGroupView(int  groupPosition, boolean  isExpanded,  
	            View convertView, ViewGroup parent)  
	    {  
	    	//自定义视图
			if (convertView == null) {
				//获取list_item布局文件的视图
				convertView = listContainer.inflate(R.layout.list_item_detail_parent, null);
			}
			//获取list_item布局文件的视图
			TextView month = ViewHolder.get(convertView, R.id.view_month);
			TextView date = ViewHolder.get(convertView, R.id.view_time);
			TextView income =ViewHolder.get(convertView, R.id.view_income);
			TextView consume= ViewHolder.get(convertView, R.id.view_consume);
			TextView balance= ViewHolder.get(convertView, R.id.view_balance);

			
			//设置文字和图片
			BillTotal b =  groupArray.get(groupPosition); 

			month.setText(String.valueOf(b.getMonth())+"月");
			month.setTag(b);//设置隐藏参数(实体类)
			String strmonth=StringUtils.flushRight('0',2,String.valueOf(b.getMonth()));
			date.setText(strmonth+"-01."+strmonth+"-"+TimeUtils.getLastDayByMonth(b.getYear(),b.getMonth()));
			income.setText(b.getIncome());
			income.setTextColor(context.getResources().getColor(R.color.red));
			consume.setText(b.getConsume());
			consume.setTextColor(context.getResources().getColor(R.color.green));
			//Double balance=StringUtils.toDouble(b.getIncome())-StringUtils.toDouble(b.getConsume());
			Double dobbalance=dUtils.sub(StringUtils.toDouble(b.getIncome()), StringUtils.toDouble(b.getConsume()));
			balance.setText(StringUtils.subZeroAndDot(String.valueOf(dobbalance)));
			return convertView;
	    }  
	    @Override
	    public  boolean  hasStableIds()  
	    {  
	        return  false ;  
	    }  
	    @Override
	    public  boolean  isChildSelectable(int  groupPosition, int  childPosition)  
	    {  
	        return  true ;  
	    }  
		 public void refresh(List<BillTotal> _groupArray,List<List<Bill>> _childArray) {  
			    groupArray = _groupArray;  
			    childArray=_childArray;
		        notifyDataSetChanged();  
		    } 
}
