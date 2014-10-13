package com.qiuqp.mybill.fragment;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.AppException;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.adapter.ListViewStatAdapter;
import com.qiuqp.mybill.bean.Bill;
import com.qiuqp.mybill.bean.BillList;
import com.qiuqp.mybill.bean.Notice;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.UIHelper;
import com.qiuqp.mybill.db.DBManager;
import com.qiuqp.mybill.ui.MainActivity;
public class StatisticsFragment extends Fragment  {
 private Spinner spinner_seltype;
 private ArrayAdapter<?> adapter_seltype;
	private AppContext appContext;// 全局Context
	private boolean isClearNotice = false;
	private int lvNewsSumData;

	private PullToRefreshListView lvNews;
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;

	private ListViewStatAdapter lvNewsAdapter;
	private List<Bill> lvNewsData = new ArrayList<Bill>();
	private Handler lvNewsHandler;
	private DBManager mgr;
	private View messageLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 messageLayout = inflater.inflate(R.layout.statistics,
				container, false);
		 lvNews_footer = inflater.inflate(R.layout.listview_footer_detail,
					null);
		return messageLayout;
	} 
	  @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        spinner_seltype=(Spinner)messageLayout.findViewById(R.id.Spinner_SelType);
			bindSelType();
		MainActivity activity = ((MainActivity)getActivity());
		mgr=activity.dbmgr;
		appContext = (AppContext)getActivity().getApplicationContext();
		initNewsListView();
		initFrameListViewData();
	    }
	private void bindSelType()
	{
		//将可选内容与ArrayAdapter连接起来
		adapter_seltype = ArrayAdapter.createFromResource(getActivity(), R.array.sel_type, R.layout.spinner_item);
   //设置下拉列表的风格
		adapter_seltype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter2 添加到spinner中
		spinner_seltype.setAdapter(adapter_seltype);
        //添加事件Spinner事件监听 
		spinner_seltype.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
        //设置默认值
		spinner_seltype.setVisibility(View.VISIBLE);
	}
	//使用XML形式操作
	
    class SpinnerXMLSelectedListener implements OnItemSelectedListener{
	        @Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	                long arg3) {
	        	loadLvNewsData(0, lvNewsHandler,
						UIHelper.LISTVIEW_ACTION_INIT,arg2);
	            //view2.setText("你使用什么样的手机："+adapter_seltype.getItem(arg2));
	        	//Toast.makeText(getApplicationContext(), String.valueOf(arg2), Toast.LENGTH_LONG).show();
	        }
	        @Override
			public void onNothingSelected(AdapterView<?> arg0) {
	             
	        }
	    }
    /**
	 * 初始化所有ListView数据
	 */
	private void initFrameListViewData() {
		// 初始化Handler
		lvNewsHandler = this.getLvHandler(lvNews, lvNewsAdapter,
				lvNews_foot_more, lvNews_foot_progress, AppContext.PAGE_SIZE);
		// 加载资讯数据
	/*	if (lvNewsData.isEmpty()) {
			loadLvNewsData(0, lvNewsHandler,
					UIHelper.LISTVIEW_ACTION_INIT,0);
		}*/
	}
	/**
	 * 初始化新闻列表
	 */
	private void initNewsListView() {
		lvNewsAdapter = new ListViewStatAdapter(getActivity(), lvNewsData,
				R.layout.list_item_statistic);
		
		lvNews_foot_more = (TextView) lvNews_footer
				.findViewById(R.id.listview_foot_more);
		lvNews_foot_progress = (ProgressBar) lvNews_footer
				.findViewById(R.id.listview_foot_progress);
		lvNews = (PullToRefreshListView) messageLayout.findViewById(R.id.liststatistics);
		ListView lv=lvNews.getRefreshableView();
		lv.addFooterView(lvNews_footer);
		//lvNews.addFooterView(lvNews_footer);// 添加底部视图 必须在setAdapter前
		lvNews.setAdapter(lvNewsAdapter);
		lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || view == lvNews_footer)
					return;

				Bill news = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					news = (Bill) view.getTag();
				} else {
					TextView tv = (TextView) view
							.findViewById(R.id.view_stat_amount);
					news = (Bill) tv.getTag();
				}
				if (news == null)
					return;

				// 跳转到新闻详情
				//UIHelper.showNewsRedirect(view.getContext(), news);
			}
		});
		lvNews.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				// 数据为空--不用继续下面代码了
				if (lvNewsData.isEmpty())
					return;
				int lvDataState = StringUtils.toInt(lvNews.getTag());
				if (lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvNews_foot_more.setText(R.string.load_ing);
					lvNews_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
					loadLvNewsData(pageIndex, lvNewsHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL,spinner_seltype.getSelectedItemId());
			}
		   }
		});
		lvNews.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadLvNewsData(0, lvNewsHandler,
						UIHelper.LISTVIEW_ACTION_REFRESH,spinner_seltype.getSelectedItemId());
			}

		});
	}
	/**
	 * 线程加载新闻数据
	 * 
	 * @param catalog
	 *            分类
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	private void loadLvNewsData(final int pageIndex,
			final Handler handler, final int action,final long seltype) {
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
	/*			boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;*/
				try {
					BillList list = appContext.getStatistics(mgr, pageIndex, seltype);
					msg.what = list.getPageSize();
					msg.obj = list;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				handler.sendMessage(msg);
			}
		}.start();
	}
	/**
	 * 获取listview的初始化Handler
	 * 
	 * @param lv
	 * @param adapter
	 * @return
	 */
	private Handler getLvHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress, final int pageSize) {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					// listview数据处理
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg1);
					if (msg.what < pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);

						// 特殊处理-热门动弹不能翻页
					/*	if (lv == lvTweet) {
							TweetList tlist = (TweetList) msg.obj;
							if (lvTweetData.size() == tlist.getTweetCount()) {
								lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
								more.setText(R.string.load_full);
							}
						}*/
					}
					// 发送通知广播
					if (notice != null) {
						UIHelper.sendBroadCast(lv.getContext(), notice);
					}
					// 是否清除通知信息
					if (isClearNotice) {
						//ClearNotice(curClearNoticeType);
						isClearNotice = false;// 重置
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(getActivity());
				}
				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(View.GONE);
			//	mHeadProgress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv.onRefreshComplete();
					//lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)+ TimeUtils.getFormatTime(new Date()));
					lv.setSelected(true);
					//lv.setSelection(0);
				}
			}
		};
	}
	/**
	 * listview数据处理
	 * 
	 * @param what
	 *            数量
	 * @param obj
	 *            数据
	 * @param actiontype
	 *            操作类型
	 * @return notice 通知信息
	 */
	private Notice handleLvData(int what, Object obj,
			int actiontype) {
		Notice notice = null;
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			   //int newdata = 0;// 新加载数据-只有刷新动作才会使用到
				BillList nlist = (BillList) obj;
				notice = nlist.getNotice();
				lvNewsSumData = what;
				lvNewsData.clear();// 先清除原有数据
				lvNewsData.addAll(nlist.getNewslist());
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
				BillList list = (BillList) obj;
				notice = list.getNotice();
				lvNewsSumData += what;
				if (lvNewsData.size() > 0) {
					for (Bill news1 : list.getNewslist()) {
						boolean b = false;
						for (Bill news2 : lvNewsData) {
							if (news1.getDate().equals(news2.getDate())) {
								b = true;
								break;
							}
						}
						if (!b)
							lvNewsData.add(news1);
					}
				} else {
					lvNewsData.addAll(list.getNewslist());
			}
			break;
		}
		return notice;
	}
	 @Override
		public void onResume() {  
	        super.onResume();  
	        // 判断当前fragment是否显示
	        if (getUserVisibleHint()) 
	        {
	        loadLvNewsData(0, lvNewsHandler,
					UIHelper.LISTVIEW_ACTION_REFRESH,0);
	        }
	    } 
}
