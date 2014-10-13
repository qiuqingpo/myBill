package com.qiuqp.mybill.fragment;


import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qiuqp.mybill.R;
import com.qiuqp.mybill.adapter.ListViewBillAdapter;
import com.qiuqp.mybill.bean.Bill;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.UIHelper;
import com.qiuqp.mybill.db.DBManager;
import com.qiuqp.mybill.ui.MainActivity;
public class TodayFragment extends Fragment  {
	private ListViewBillAdapter lvBillAdapter;
	private ListView lvBill;
	private List<Bill> lvBillData;
	private View lv_footer;
	private TextView tvTotal;
	private LinearLayout lin_footer_total;
	private DBManager mgr;
	private View messageLayout;
	private Button lv_add;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 messageLayout = inflater.inflate(R.layout.today,
				container, false);
		 lv_footer = inflater.inflate(R.layout.listview_footer,
					null);
		return messageLayout;
	}
	  @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	    	initBindData();
			initBillListView();
	    }
	private void initBindData()
	{
		MainActivity activity = ((MainActivity)getActivity());
		mgr=activity.dbmgr;
		lvBillData=mgr.getBillTodayList();
	}
    /** 监听添加按钮 */
	private OnClickListener btnAddListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	     UIHelper.showRecordEditor(v.getContext(),"");
		}
	};
	private void initBillListView() {
		lvBillAdapter = new ListViewBillAdapter(getActivity(), lvBillData,
				R.layout.list_item_detail);
		lvBill = (ListView) messageLayout.findViewById(R.id.todaylistview);
		
		
		lin_footer_total=(LinearLayout)lv_footer.findViewById(R.id.lin_footer_total);
	    lv_add=(Button)lv_footer.findViewById(R.id.btn_addbill);
	    lv_add.setOnClickListener(btnAddListener);
	    tvTotal=(TextView)lv_footer.findViewById(R.id.list_total_today);
	    //tvTotal.setText(mgr.queryBillTodayCount());
	    lvBill.addFooterView(lv_footer);
	/*	if(lvBillData.size()!=0)
		{
			lin_footer_total.setVisibility(View.VISIBLE);
		}*/
		lvBill.setAdapter(lvBillAdapter);
		lvBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		   @Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击头部、底部栏无效
				/*if (position == 0)
					return;*/

				Bill b = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					b = (Bill) view.getTag();
				} else {
					TextView tv = (TextView) view
							.findViewById(R.id.view_type);
					if(tv!=null)
					b = (Bill) tv.getTag();
				}
				if (b == null)
					return;
				UIHelper.showRecordEditor(view.getContext(),b.getId());
				// 跳转到新闻详情
				//UIHelper.showHome((Activity)Today.class);
			}
		});
		ItemOnLongClick2();
        lvBillAdapter.notifyDataSetChanged();
	}
    private void ItemOnLongClick2() { 
    	lvBill.setOnItemLongClickListener(new OnItemLongClickListener() { 
       @Override
       public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) { 
         //lvBillData.remove(arg2); 
           if(arg3==-1)
           	return false;
         new AlertDialog.Builder(getActivity()) .setTitle(R.string.sure_message) 
         .setItems(R.array.arrcontent, new DialogInterface.OnClickListener() { 
            @Override
			public void onClick(DialogInterface dialog, 
               int which) { 
              String[] PK = getResources().getStringArray(R.array.arrcontent); 
                 if (PK[which].equals(getResources().getString(R.string.delete))) { // 按照这种方式做删除操作，这个if内的代码有bug，实际代码中按需操作 
                	 String id=lvBillData.get(arg2).getId();
                	 String feetype=lvBillData.get(arg2).getFeeType()==1?"-":"";
                	 String price=feetype+lvBillData.get(arg2).getPrice();
                	 mgr.deleteBill(id);
                	 lvBillData.remove(arg2); 
                	 if(lvBillData.size()==0)
                	 {
                		 lin_footer_total.setVisibility(View.GONE);
                	 }
                	 else
                	 {
                		 String totalPrice=tvTotal.getText().toString();
                		 double nowPrice=StringUtils.toDouble(totalPrice)-StringUtils.toDouble(price);
                		 tvTotal.setText(StringUtils.subZeroAndDot(String.valueOf(nowPrice)));
                	 }
                	  lvBillAdapter.refresh(lvBillData);
                      lvBill.setAdapter(lvBillAdapter);  
                	 //lvBillAdapter.notifyDataSetChanged(); // 实现数据的实时刷新 
                               } 
                           } 
                    }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() { 
                    @Override
					public void onClick(DialogInterface dialog, int which) { 
                      // TODO Auto-generated method stub 
                    	 dialog.dismiss();
                         } 
                         }).show(); 
                     return true; 
                     } 
        }); 

} 
   @Override
	public void onResume() {  
        super.onResume();  
        initBindData();
        tvTotal.setText(mgr.queryBillTodayCount());
        if(lvBillData.size()>0)
        {
        	lin_footer_total.setVisibility(View.VISIBLE);
        }
        lvBillAdapter.refresh(lvBillData);
        lvBill.setAdapter(lvBillAdapter);  
    } 
}

