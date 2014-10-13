package com.qiuqp.mybill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.bean.BillType;

/**
 * 新闻资讯Adapter类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewBillTypeAdapter extends BaseAdapter {
	private List<BillType> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	//private ListView mSwipeListView ;
	static class ListItemView{				//自定义控件集合  
	        public TextView typeName;
	        public TextView feeType;
	        //Button mBackEdit,mBackDelete ;
	        CheckBox chkbox;
	 }  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewBillTypeAdapter(Context context, List<BillType> data,int resource) {		
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		//this.mSwipeListView = mSwipeListView ;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//自定义视图
		ListItemView  listItemView = null;
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取list_item布局文件的视图
			listItemView.typeName = (TextView)convertView.findViewById(R.id.view_billtype);
			listItemView.feeType = (TextView)convertView.findViewById(R.id.view_feetype);
			listItemView.chkbox=(CheckBox)convertView.findViewById(R.id.view_billtype_check);
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		/*listItemView.mBackDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button b=(Button)v;
				mgr.deleteBillType(b.getTag().toString());
				mSwipeListView.closeAnimate(position);
				mSwipeListView.dismiss(position);
			}
		});
		listItemView.mBackEdit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				Button b=(Button)v;
				UIHelper.showEditBillType(context, b.getTag().toString(),"",0);
			}
		});*/
		//设置文字和图片
		BillType b = listItems.get(position);
		listItemView.typeName.setText(b.getTypeName());
		listItemView.typeName.setTag(b);
		String feeTypeStr=b.getFeeType()==1?"支出":"收入";
		listItemView.feeType.setText(feeTypeStr);
/*		listItemView.mBackEdit.setTag(b.getId());
		listItemView.mBackDelete.setTag(b.getId());*/
		//listItemView.chkbox.setChecked(listItems.get(position).getFlag());
		listItemView.chkbox.setOnCheckedChangeListener(null);
		listItemView.chkbox.setChecked(b.getFlag());
		listItemView.chkbox.setOnCheckedChangeListener(setOnCheckedChangeListener1(b));
		return convertView;
	}
	private CompoundButton.OnCheckedChangeListener setOnCheckedChangeListener1(final BillType b) {
		return new  CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				b.setFalg(isChecked);
			}
			
		};
		
	}
	 public void refresh(List<BillType> list) {  
		    listItems = list;  
	        notifyDataSetChanged();  
	    }  
 }
