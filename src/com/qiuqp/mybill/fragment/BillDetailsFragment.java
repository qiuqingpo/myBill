package com.qiuqp.mybill.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.qiuqp.mybill.R;
import com.qiuqp.mybill.adapter.ListViewBillDetailAdapter;
import com.qiuqp.mybill.bean.Bill;
import com.qiuqp.mybill.bean.BillTotal;
import com.qiuqp.mybill.common.DoubleUtils;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.UIHelper;
import com.qiuqp.mybill.db.DBManager;
import com.qiuqp.mybill.ui.MainActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

public class BillDetailsFragment extends Fragment {
	private List<BillTotal> groupArray;
	private List<List<Bill>> childArray;
	private PullToRefreshExpandableListView mPullRefreshListView;
	private ListViewBillDetailAdapter mAdapter;
	private View lv_header;
	private int cur_Year;
	private int now_Year;
	private int now_Month;
	private Double all_income = 0.0;
	private Double all_consume = 0.0;
	private TextView txtbalance;
	private TextView txtincome;
	private TextView txtconsume;
	private TextView txttotal;
	private DoubleUtils dUtils;
	private DBManager mgr;
	private MainActivity mainactivity;
	private Thread mThread;
	 @Override 
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
         View v = inflater.inflate(R.layout.billdetails, null);  
	     mPullRefreshListView = (PullToRefreshExpandableListView) v.findViewById(R.id.listdetails);  
	     lv_header = inflater.inflate(R.layout.billdetails_head, null);
	     return v;  
     }     
	  @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        initTime();
			findViewById();
			initData();
			mAdapter = new ListViewBillDetailAdapter(
					getActivity(), groupArray, childArray);
		 
	        /**
	         * 添加头
	         */
	        ExpandableListView lv = mPullRefreshListView.getRefreshableView();
	        lv.addHeaderView(lv_header);
	        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
	        mPullRefreshListView.getRefreshableView().setAdapter(mAdapter);
	     //	mPullRefreshListView.getRefreshableView().addHeaderView(lv_header);
			mPullRefreshListView.getRefreshableView().setGroupIndicator(null);
			mPullRefreshListView.getRefreshableView().expandGroup(0);
	    	mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {

				@Override
				public void onPullDownToRefresh(
						PullToRefreshBase<ExpandableListView> refreshView) {
					// TODO Auto-generated method stub
					now_Year++;
					updateData();
				}

				@Override
				public void onPullUpToRefresh(
						PullToRefreshBase<ExpandableListView> refreshView) {
					// TODO Auto-generated method stub
					now_Year--;
					updateData();
				}
			
			});
	    	mPullRefreshListView.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ExpandableListView>() {

				@Override
				public void onPullEvent(
						PullToRefreshBase<ExpandableListView> refreshView, State state,
						Mode direction) {
					// TODO Auto-generated method stub
					 if (state.equals(State.PULL_TO_REFRESH)) 
					 {
						// mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("正在加载"+(now_Year+2)+"年数据");
					 }
					 else
					 {
						 if(direction.equals(Mode.PULL_FROM_START))
							 mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel((now_Year+1)+"年流水");
						 else
						 mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel((now_Year-1)+"年流水");
					 }
				}
	
	    	});
	        setChildItemClick();
	    }
private void setChildItemClick()
{
	mPullRefreshListView.getRefreshableView()
	.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent,
				View view, int groupPosition, int childPosition,
				long id) {
			Bill b = null;
			// 判断是否是TextView
			if (view instanceof TextView) {
				b = (Bill) view.getTag();
			} else {
				TextView tv = (TextView) view
						.findViewById(R.id.view_type);
				if (tv != null)
					b = (Bill) tv.getTag();
			}
			if (b == null || b.getFlag() == 1)
				return false;
			UIHelper.showRecordEditor(view.getContext(), b.getId());
			return true;
		}
	});

}
    Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		if (msg.what == 1) {
			mAdapter.refresh(groupArray, childArray);
			mPullRefreshListView.getRefreshableView().setAdapter(mAdapter);
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			mPullRefreshListView.getRefreshableView().expandGroup(0);
		}
	}
};
	/**
	 * 初始化日期
	 */
	private void initTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		cur_Year = now_Year = c.get(Calendar.YEAR);
		now_Month = c.get(Calendar.MONTH) + 1;
	}

	private void findViewById() {
		// lv_header =
		// getLayoutInflater().inflate(R.layout.billdetails_head,null);
		txtbalance= (TextView) lv_header.findViewById(R.id.txtbalance);
		txtincome = (TextView) lv_header.findViewById(R.id.txtIncome);
		txtconsume = (TextView) lv_header.findViewById(R.id.txtConsume);
		txttotal = (TextView) lv_header.findViewById(R.id.txtTotal);
		dUtils = new DoubleUtils();
		mainactivity = ((MainActivity) getActivity());
		mgr = mainactivity.dbmgr;
	}
    private void updateData()
    {
	if (mThread == null || !mThread.isAlive()) {
		mThread = new Thread() {
			@Override
			public void run() {
				initData();
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		};
		mThread.run();
	}
}
	private void initData() {
		groupArray = new ArrayList<BillTotal>();
		childArray = new ArrayList<List<Bill>>();
		int monthLen = 12;
		if (cur_Year == now_Year)// 说明是当前年，显示到当前月就可以了
		{
			monthLen = now_Month;
		}
		BillTotal bt;
		all_income =0.0;
		all_consume=0.0;
		String msgYearStr=getResources().getString(R.string.msgbalance).replace("%d", String.valueOf(now_Year));
		txtbalance.setText(msgYearStr);
		//mainactivity.setTitle(String.valueOf(now_Year));
		for (int i = monthLen; i > 0; i--) {
			String month = now_Year + "-"
					+ StringUtils.flushRight('0', 2, String.valueOf(i));
			String[] totalPriceArr = mgr.queryBillPerMonth(month);
			List<Bill> billDetail = mgr.queryBillPerMonthDay(month);
			String income = totalPriceArr[0];
			String consume = totalPriceArr[1];
			all_income += StringUtils.toDouble(income);
			all_consume += StringUtils.toDouble(consume);
			bt = new BillTotal();
			bt.setYear(now_Year);
			bt.setMonth(i);
			bt.setIncome(income);
			bt.setConsume(consume);
			groupArray.add(bt);
			childArray.add(billDetail);
		}
		txtincome.setText(StringUtils.subZeroAndDot(all_income.toString()));
		txtconsume.setText(StringUtils.subZeroAndDot(all_consume.toString()));
		Double total = dUtils.sub(all_income, all_consume);
		txttotal.setText(StringUtils.subZeroAndDot(total.toString()));
	}
}
