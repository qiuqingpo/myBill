package com.qiuqp.mybill.fragment;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.common.UIHelper;
import com.qiuqp.mybill.common.UpdateManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingFragment extends Fragment {
	private AppContext appContext;// 全局Context
	private RelativeLayout billtype_set;
	private RelativeLayout voice_set;
	private RelativeLayout pwd_set;
	private RelativeLayout checkup_set;
	private RelativeLayout aboutus_set;
	private RelativeLayout question_set;
	private ImageView imgSetVoice;
	private TextView txtVersion;
	//private DBManager mgr;
	private View messageLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 messageLayout = inflater.inflate(R.layout.setting,
				container, false);
		 appContext = (AppContext)getActivity().getApplicationContext();
		 findViewsById();
		 setListener();
		return messageLayout;
	} 
	/*@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		appContext = (AppContext) getApplication();
		findViewsById();
		setListener();
		}*/
	private void  findViewsById()
	{
		billtype_set=(RelativeLayout)messageLayout.findViewById(R.id.set_billtype);
		voice_set=(RelativeLayout)messageLayout.findViewById(R.id.set_voice);
		pwd_set=(RelativeLayout)messageLayout.findViewById(R.id.set_pwd);
		checkup_set=(RelativeLayout)messageLayout.findViewById(R.id.set_checkup);
		aboutus_set=(RelativeLayout)messageLayout.findViewById(R.id.set_aboutus);
		question_set=(RelativeLayout)messageLayout.findViewById(R.id.set_qustion);
		imgSetVoice=(ImageView)messageLayout.findViewById(R.id.imgSetVoice);
		txtVersion=(TextView)messageLayout.findViewById(R.id.txtVersion);
		txtVersion.setText("版本：V"+appContext.getPackageInfo().versionName);
		setImageViewBackgroud(imgSetVoice,appContext.isVoice());
	}
	private void setListener()
	{
		billtype_set.setOnClickListener(billtypeListener);
		voice_set.setOnClickListener(voiceListener);
		pwd_set.setOnClickListener(pwdListener);
		checkup_set.setOnClickListener(checkupListener);
		aboutus_set.setOnClickListener(aboutusListener);
		question_set.setOnClickListener(questionListener);
		
	}
	private OnClickListener billtypeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UIHelper.showBillType(v.getContext());
		}
	};
	private OnClickListener voiceListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean ischecked=appContext.isVoice()==false?true:false;
 			appContext.setConfigVoice(ischecked);
 			setImageViewBackgroud(imgSetVoice,ischecked);
		}
	};
	private OnClickListener pwdListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			UIHelper.showSettingPwd(v.getContext());
		}
	};
	private OnClickListener checkupListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UpdateManager.getUpdateManager().checkAppUpdate(getActivity(),appContext,
					true);
		}
	};
	private OnClickListener aboutusListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UIHelper.showAboutUs(v.getContext(), "0");
		}
	};
	private OnClickListener questionListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UIHelper.showAboutUs(v.getContext(), "1");
		}
	};
	private void setImageViewBackgroud(ImageView imgView,boolean ischecked)
	{
		if(ischecked)
		{
			imgView.setBackgroundResource(R.drawable.btn_check_on_normal);
		}
		else
		{
			imgView.setBackgroundResource(R.drawable.btn_check_off_normal);
		}
	}
}
