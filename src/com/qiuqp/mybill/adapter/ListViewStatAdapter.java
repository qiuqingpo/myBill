package com.qiuqp.mybill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.qiuqp.mybill.R;
import com.qiuqp.mybill.bean.Bill;

/**
 * 新闻资讯Adapter类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewStatAdapter extends BaseAdapter {
	private List<Bill> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	static class ListItemView{				//自定义控件集合  
	        public TextView price;
		    public TextView date;  
	 }  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewStatAdapter(Context context, List<Bill> data,int resource) {	
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	}
	
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	/**
	 * ListView Item设置
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//自定义视图
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取list_item布局文件的视图
			listItemView.price = (TextView)convertView.findViewById(R.id.view_stat_amount);
			listItemView.date= (TextView)convertView.findViewById(R.id.view_stat_time);
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		Bill b = listItems.get(position);
		listItemView.price.setText(b.getPrice().toString());
		listItemView.price.setTag(b);
		String showDate=b.getDate();
		listItemView.date.setText(showDate);
		return convertView;
	}
	/*
	 * 强制刷新功能
	 */
	 public void refresh(List<Bill> list) {  
		    listItems = list;  
	        notifyDataSetChanged();  
	    }  
}