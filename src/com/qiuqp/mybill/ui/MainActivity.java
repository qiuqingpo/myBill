package com.qiuqp.mybill.ui;

import com.qiuqp.mybill.AppManager;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.common.UIHelper;
import com.qiuqp.mybill.db.DBManager;
import com.qiuqp.mybill.fragment.BillDetailsFragment;
import com.qiuqp.mybill.fragment.SettingFragment;
import com.qiuqp.mybill.fragment.StatisticsFragment;
import com.qiuqp.mybill.fragment.TodayFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

//public class Main extends BaseActivity implements  OnCheckedChangeListener{
public class MainActivity extends  BaseActivity {
	    public DBManager dbmgr;
	    private RadioGroup mainradio;
	    /**
		 * 用于展示消息的Fragment
		 */
		private TodayFragment todayFragment;

		/**
		 * 用于展示联系人的Fragment
		 */
		private BillDetailsFragment billDetailsFragment;

		/**
		 * 用于展示动态的Fragment
		 */
		private StatisticsFragment statisticsFragment;

		/**
		 * 用于展示设置的Fragment
		 */
		private SettingFragment settingFragment;

		
		private Fragment[] fragments;
		//private int index=0;
		private int currentTabIndex=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//mgr=new DBManager(this);
		dbmgr=mgr;
		initActionBar();
		initView();
		initTab();
	}
	private void initActionBar()
	{
		//actionbar初始化
		setOverflowShowAlways();
		//actionBarTitle  = getTitle();
		ActionBar actionBar =getSupportActionBar();
		/*actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);*/
		//actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(getTitle());
		actionBar.setDisplayUseLogoEnabled(false);
	
		/**
		 * 以下为自定义标题栏布局
		 */
		/*getSupportActionBar().setCustomView(LayoutInflater.from(this).inflate(R.layout.title, null));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);*/

	}
	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initView() {
		mainradio=(RadioGroup)findViewById(R.id.main_radio);
		mainradio.setOnCheckedChangeListener(onRadioCheckedChanged);
	}
	  public OnCheckedChangeListener onRadioCheckedChanged=new OnCheckedChangeListener()
	  {
		@Override
		public void onCheckedChanged(RadioGroup arg0, int checkedId) {
			// TODO Auto-generated method stub
			RadioButton btn=(RadioButton)arg0.findViewById(checkedId);
			btn.setChecked(true);
			int index = arg0.indexOfChild(btn);
			if(index==currentTabIndex)
				return;
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.content, fragments[index]);
			}
			trx.show(fragments[index]).commit();
			currentTabIndex = index;
		}
	  };
	
	private void initTab(){
		todayFragment = new TodayFragment();
		billDetailsFragment = new BillDetailsFragment();
		statisticsFragment = new StatisticsFragment();
		settingFragment = new SettingFragment();
		fragments = new Fragment[] {todayFragment, billDetailsFragment, statisticsFragment,settingFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.content, todayFragment).
			add(R.id.content, billDetailsFragment).hide(billDetailsFragment).show(todayFragment).commit();
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
				case R.id.action_add:
					UIHelper.showRecordEditor(this, "");
					break;
				case R.id.action_settings:
					mainradio.check(R.id.radio_button3);
					break;
				case R.id.action_exit:
					UIHelper.Exit(this);
					break;
		         }
				return true;
		}
			private long exitTime = 0;
			  @Override
			    public void onBackPressed() {
			            // TODO Auto-generated method stub
			            if((System.currentTimeMillis() - exitTime) > 2000){
			                Toast.makeText(this, R.string.msg_tip_exit, Toast.LENGTH_SHORT).show();
			                exitTime = System.currentTimeMillis();
			        }else{
			        	// 是否退出应用
			        	AppManager.getAppManager().AppExit(this);
			        }
			       
			    }
}
