package com.qiuqp.mybill.common;

import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.bean.Notice;
import com.qiuqp.mybill.AppManager;
import com.qiuqp.mybill.R;
import com.qiuqp.mybill.ui.AboutUs;
import com.qiuqp.mybill.ui.BillTypeEdit;
import com.qiuqp.mybill.ui.BillTypeList;
import com.qiuqp.mybill.ui.GuideActivity;
import com.qiuqp.mybill.ui.MainActivity;
import com.qiuqp.mybill.ui.RecordEditor;
import com.qiuqp.mybill.ui.SettingPwd;
import com.qiuqp.mybill.ui.SettingPwd_Answer;
import com.qiuqp.mybill.ui.SettingPwd_Gusture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;


public class UIHelper {

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;
	
	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
	/**
	 * 显示首页
	 * 
	 * @param activity
	 */
	public static void showHome(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}
	public static void showGuide(Activity activity) {
		Intent intent = new Intent(activity, GuideActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}
	/**
	 * 显示新闻详情
	 * 
	 * @param context
	 * @param newsId
	 */
	public static void showRecordEditor(Context context,String id) {
		Intent intent = new Intent(context, RecordEditor.class);
		intent.putExtra("id", id);
		context.startActivity(intent);
		//Log.v("newsopen", "要打开新的页面");
	}
	public static void showAboutUs(Context context,String type) {
		Intent intent = new Intent(context, AboutUs.class);
		intent.putExtra("type", type);
		context.startActivity(intent);
		//Log.v("newsopen", "要打开新的页面");
	}
	public static void showBillType(Context context) {
		Intent intent = new Intent(context, BillTypeList.class);
		context.startActivity(intent);
	}
	public static void showEditBillType(Context context,String id,String from,int resCode) {
		Intent intent = new Intent(context, BillTypeEdit.class);
		intent.putExtra("id", id);
		intent.putExtra("from", from);
		Activity a=(Activity)context;
		a.startActivityForResult(intent,resCode);
		//Log.v("newsopen", "要打开新的页面");
	}
	public static void showSettingPwd(Context context) {
		Intent intent = new Intent(context, SettingPwd.class);
		context.startActivity(intent);
	}
	/**
	 * type 0:密码设置，1：用户登录
	 * @param context
	 * @param type
	 */
	public static void showSettingPwd_Gusture(Context context,String type) {
		Intent intent = new Intent(context, SettingPwd_Gusture.class);
		intent.putExtra("type", type);
		context.startActivity(intent);
	}
	/**
	 * type 0:密保设置，1：找回密码
	 * @param context
	 * @param type
	 */
	public static void showSettingPwd_Answer(Context context,String type) {
		Intent intent = new Intent(context, SettingPwd_Answer.class);
		intent.putExtra("type", type);
		context.startActivity(intent);
	}
	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}
	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "qiui2@126.com" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"记账宝Android客户端 - 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}

	/**
	 * 退出程序
	 * 
	 * @param cont
	 */
	public static void Exit(final Context cont) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(R.drawable.exit);
		builder.setTitle(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}
	/**
	 * 发送通知广播
	 * 
	 * @param context
	 * @param notice
	 */
	public static void sendBroadCast(Context context, Notice notice) {
		if (!((AppContext) context.getApplicationContext()).isLogin()
				|| notice == null)
			return;
		Intent intent = new Intent("com.qiuqp.i_love_smile.action.APPWIDGET_UPDATE");
		intent.putExtra("atmeCount", notice.getAtmeCount());
		intent.putExtra("msgCount", notice.getMsgCount());
		intent.putExtra("reviewCount", notice.getReviewCount());
		intent.putExtra("newFansCount", notice.getNewFansCount());
		context.sendBroadcast(intent);
	}
}
