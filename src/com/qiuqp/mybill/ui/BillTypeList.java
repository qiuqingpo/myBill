package com.qiuqp.mybill.ui;

import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.adapter.ListViewBillTypeAdapter;
import com.qiuqp.mybill.bean.BillType;
import com.qiuqp.mybill.common.UIHelper;

public class BillTypeList extends BaseActivity {
	private List<BillType> lvBillTypeData;
	public ListViewBillTypeAdapter lvBillTypeAdapter;
	public ListView lvBillType;
	public Button btnSelAll;
	public Button btnAnti_Election;
	public Button btnCancel;
	public Button btnDelete;
	public ImageView btnAdd;
	public int checkNum;
	//public static int deviceWidth ;
	public AppContext appContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.billtype);
		appContext = (AppContext) getApplication();
		findIdByView();
		setListener();
		//deviceWidth = getDeviceWidth();
		initSettingView();
		initActionBar();
		}
	private void initBindData()
	{
		lvBillTypeData=mgr.getBillType(0);
	}
	private void initSettingView()
	{
		initBindData();
		//LinearLayout lin_bottom=(LinearLayout)findViewById(R.id.lin_billtype_bottom);
		lvBillType = (ListView) findViewById(R.id.billtypelistview);
		//lvBillType.addFooterView(lin_bottom);
		lvBillTypeAdapter = new ListViewBillTypeAdapter(this, lvBillTypeData,
				R.layout.list_item_bill_billtype);
		lvBillType.setAdapter(lvBillTypeAdapter);
		ItemOnLongClick2();
		lvBillType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			   @Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(lvBillTypeData.get(position).getFlag())
					{
						lvBillTypeData.get(position).setFalg(false);
						 checkNum--;
					}
					else
					{
						lvBillTypeData.get(position).setFalg(true);
						 checkNum++;
					}
					dataChanged();
				}
			});
		//setSwipeListViewListener( new TestBaseSwipeListViewListener());
		//reload();
		lvBillTypeAdapter.notifyDataSetChanged();
	}
	 private void ItemOnLongClick2() { 
		 lvBillType.setOnItemLongClickListener(new OnItemLongClickListener() { 
	       @Override
	       public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) { 
	         //lvBillData.remove(arg2); 
	           if(arg3==-1)
	           	return false;
	         new AlertDialog.Builder(BillTypeList.this) .setTitle(R.string.sure_message) 
	         .setItems(R.array.arrcontent, new DialogInterface.OnClickListener() { 
	            @Override
				public void onClick(DialogInterface dialog, 
	               int which) { 
	              String[] PK = getResources().getStringArray(R.array.arrcontent); 
	                 if (PK[which].equals(getResources().getString(R.string.delete))) { // 按照这种方式做删除操作，这个if内的代码有bug，实际代码中按需操作 
	                	 String id=lvBillTypeData.get(arg2).getId();
	                	 mgr.deleteBillType(id);
	                	 lvBillTypeData.remove(arg2); 
	                	 lvBillTypeAdapter.notifyDataSetChanged(); // 实现数据的实时刷新 
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
/*	 private int getDeviceWidth() {
			return getResources().getDisplayMetrics().widthPixels;
		}*/

	/*	private void reload() {
			lvBillType.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
		//	lvBillType.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
			lvBillType.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
			//lvBillType.setSwipeActionRight(settings.getSwipeActionRight());
//			lvBillType.setOffsetLeft(deviceWidth * 1 / 3);
			//lvBillType.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
			lvBillType.setOffsetRight(deviceWidth * 1 / 3);
			lvBillType.setAnimationTime(0);
			lvBillType.setSwipeOpenOnLongPress(false);
	    }*/
		
		/*class TestBaseSwipeListViewListener extends OnClickListener{

			@Override
			public void onClickFrontView(int position) {
				super.onClickFrontView(position);
				if(lvBillTypeData.get(position).getFlag())
				{
					lvBillTypeData.get(position).setFalg(false);
					 checkNum--;
				}
				else
				{
					lvBillTypeData.get(position).setFalg(true);
					 checkNum++;
				}
				dataChanged();
				//Toast.makeText(getApplicationContext(), lvBillTypeData.get(position).getTypeName(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					lvBillTypeData.remove(position);
				}
				lvBillTypeAdapter.notifyDataSetChanged();
			}
		}*/
		private void findIdByView()
		{
			 btnSelAll=(Button)findViewById(R.id.btn_billtype_selall);
			 btnAnti_Election=(Button)findViewById(R.id.btn_billtype_anti_election);
			 btnCancel=(Button)findViewById(R.id.btn_billtype_cancel);
			 btnDelete=(Button)findViewById(R.id.btn_billtype_delete);
			 //btnAdd=(ImageView)findViewById(R.id.addrecord);
		}
	private void setListener()
		{
		    btnSelAll.setOnClickListener(selAllListener);
		    btnAnti_Election.setOnClickListener(anti_ElectionListener);
		    btnCancel.setOnClickListener(cancelListener);
		    btnDelete.setOnClickListener(delListener);
		    //btnAdd.setOnClickListener(addListener);
		    
		}
		private OnClickListener selAllListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				//遍历list长度将MyAdapter中的map值全部设为true
				for(BillType b :lvBillTypeData)
				{
					b.setFalg(true);
				}
				checkNum=lvBillTypeData.size();
			    dataChanged();
			}
		};
		private OnClickListener anti_ElectionListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				//遍历list长度将MyAdapter中的map值全部设为true
				for(BillType b :lvBillTypeData)
				{
					if(b.getFlag()==true)
					{
						b.setFalg(false);
						checkNum--;
					}
					else
					{
					b.setFalg(true);
					checkNum++;
					}
				}
			    dataChanged();
			}
		};
		private OnClickListener cancelListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				//遍历list长度将MyAdapter中的map值全部设为true
				for(BillType b :lvBillTypeData)
				{
					if(b.getFlag()==true)
					{
						b.setFalg(false);
						checkNum--;
					}
				}
			    dataChanged();
			}
		};
		private OnClickListener delListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				//遍历list长度将MyAdapter中的map值全部设为true
			    for (Iterator iter = lvBillTypeData.iterator(); iter.hasNext();)
				//for(BillType b :lvBillTypeData)
				{
			    	BillType b = (BillType)iter.next();
					if(b.getFlag()==true)
					{
						mgr.deleteBillType(b.getId());
						iter.remove();
						//lvBillTypeData.remove(b);
					}
				}
	                checkNum = 0;
	                // 通知列表数据修改
	                dataChanged();
			}
		};
		private void dataChanged()
		{
			lvBillTypeAdapter.notifyDataSetChanged();
			if(checkNum>0)
				btnDelete.setEnabled(true);
			else 
				btnDelete.setEnabled(false);
		}
		@Override 
	    protected void onActivityResult(int requestCode, int resultCode, Intent data)//重写onActivityResult方法
	    {
	          switch (resultCode)
	         {
	                case 1:
	                	initBindData();
	                	//lvBillTypeAdapter.notifyDataSetChanged();
	                	lvBillTypeAdapter.refresh(lvBillTypeData);
	                break;
	         }
	   }
		private void initActionBar()
		{
			//actionbar初始化
			setOverflowShowAlways();
			//actionBarTitle  = getTitle();
			ActionBar actionBar =getSupportActionBar();
			//actionBar.setLogo(R.drawable.webviewtab_back_normal);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setTitle(R.string.set_billtype);
			//actionBar.setDisplayUseLogoEnabled(false);

/*			actionBar.setIcon(R.drawable.webviewtab_back_normal);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(R.string.set_billtype);
			actionBar.setDisplayHomeAsUpEnabled(false);*/
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
						UIHelper.showEditBillType(this, "","",0);
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
