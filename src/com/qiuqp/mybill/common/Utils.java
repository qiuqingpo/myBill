package com.qiuqp.mybill.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
public class Utils {
	private final static String KEY_LOCK_PWD = "lock_pwd";
	
	private static Context mContext;

	private static SharedPreferences preference;
    private static long lastClickTime;  
	
	 public Utils(Context context) {
	        mContext = context;
	        preference = PreferenceManager.getDefaultSharedPreferences(mContext);
	 }
	
	public static List<LockView.Cell> stringToPattern(String string) {
        List<LockView.Cell> result = new ArrayList<LockView.Cell>();

        final byte[] bytes = string.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            result.add(LockView.Cell.of(b / 3, b % 3));
        }
        Log.i("lock", "Utils============result"+result);
        return result;
    }
	
	
	public static String patternToString(List<LockView.Cell> pattern) {
        if (pattern == null) {
            return "";
        }
        final int patternSize = pattern.size();

        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        return Arrays.toString(res);
    }
	
	/**
     * 保存密码
     * @param pattern
     */
    public void saveLockPattern(List<LockView.Cell> pattern){
    	Editor editor = preference.edit();
    	editor.putString(KEY_LOCK_PWD, patternToString(pattern));
    	editor.commit();
    }
    
    /**
     * 获取密码
     * @return
     */
    public static String getLockPaternString(){
    	try
    	{
    	return preference.getString(KEY_LOCK_PWD, "");
    	}
    	catch(Exception ex)
    	{
    	}
    	return "";
    }
    /**
     * 防止按钮重复单击提交
     * @return
     */
     public static boolean isFastDoubleClick() {  
        long time = System.currentTimeMillis();  
        long timeD = time - lastClickTime;  
        if (0 < timeD && timeD < 10000) {  
            return true;  
        }  
        lastClickTime = time;  
        return false;  
    }  
}
