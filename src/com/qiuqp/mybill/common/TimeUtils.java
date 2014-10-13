package com.qiuqp.mybill.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	//private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat("yyyy-MM-dd");
		
		private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
		};
		/*private final static ThreadLocal<SimpleDateFormat> dateFormater_short = new ThreadLocal<SimpleDateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm");
			}
		};*/
		private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat("yyyy-MM-dd");
			}
		};
		//格式化时间
		public static String getFormatTime(Date date)
		{     
			return  dateFormater.get().format(date);   
		}
		public static int compare_date(String DATE1, String DATE2) 
		{
			try {
			Date dt1 = dateFormater.get().parse(DATE1);
			Date dt2 = dateFormater.get().parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
			return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
			return -1;
			} else {
			return 0;
			}
			} catch (Exception exception) {
			exception.printStackTrace();
			}
			return 0;
			
			}
		/**
		 * 将字符串转位日期类型
		 * @param sdate
		 * @return
		 */
		public static Date toDate(String sdate) {
			try {
				if(sdate==""||sdate==null)
					return null;
				return dateFormater.get().parse(sdate);
			} catch (ParseException e) {
				return null;
			}
		}
		public static String toShortDate(String sdate)
		{
			try {
				if(sdate==""||sdate==null)
					return null;
				Date date=dateFormater2.get().parse(sdate);
				return dateFormater2.get().format(date);
						//sdate.substring(0,sdate.lastIndexOf(":"));
			} catch (Exception e) {
				return null;
			}
		}
		/**
		 * 以友好的方式显示时间
		 * @param sdate
		 * @return
		 */
		public static String friendly_time(String sdate) {
			Date time = toDate(sdate);
			if(time == null) {
				return "Unknown";
			}
			String ftime = "";
			Calendar cal = Calendar.getInstance();
			
			//判断是否是同一天
			String curDate = dateFormater2.get().format(cal.getTime());
			String paramDate = dateFormater2.get().format(time);
			if(curDate.equals(paramDate)){
				int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
				if(hour == 0)
					ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000,1)+"分钟前";
				else 
					ftime = hour+"小时前";
				return ftime;
			}
			
			long lt = time.getTime()/86400000;
			long ct = cal.getTimeInMillis()/86400000;
			int days = (int)(ct - lt);		
			if(days == 0){
				int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
				if(hour == 0)
					ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000,1)+"分钟前";
				else 
					ftime = hour+"小时前";
			}
			else if(days == 1){
				ftime = "昨天";
			}
			else if(days == 2){
				ftime = "前天";
			}
			else if(days > 2 && days <= 10){ 
				ftime = days+"天前";			
			}
			else if(days > 10){			
				ftime = dateFormater2.get().format(time);
			}
			return ftime;
		}
		
		/**
		 * 判断给定字符串时间是否为今日
		 * @param sdate
		 * @return boolean
		 */
		public static boolean isToday(String sdate){
			boolean b = false;
			Date time = toDate(sdate);
			Date today = new Date();
			if(time != null){
				String nowDate = dateFormater2.get().format(today);
				String timeDate = dateFormater2.get().format(time);
				if(nowDate.equals(timeDate)){
					b = true;
				}
			}
			return b;
		}
		public static String getLastDayByMonth(int year,int month) {
			 Calendar cal = Calendar.getInstance();  // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
			 cal.set(Calendar.YEAR,year);  
			 cal.set(Calendar.MONTH, month); 
			 cal.set(Calendar.DAY_OF_MONTH, 1); 
			 cal.add(Calendar.DAY_OF_MONTH, -1);
			 //cal.set(Calendar.DAY_OF_MONTH, 1);
			 return  String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));    
		}
	    /**
	     * 判断当前日期是星期几    * @param pTime 修要判断的时间<br>
	     * @return dayForWeek 判断结果<br>
	     * @throws ParseException 
	     * @Exception 发生异常<br>
	     */
	 public static String dayForWeek(String pTime) throws ParseException{
	  String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
	  Calendar c = Calendar.getInstance();
	  c.setTime(dateFormater.get().parse(pTime));
	  /*int dayForWeek = 0;
	  if(c.get(Calendar.DAY_OF_WEEK) == 1){
	   dayForWeek = 7;
	  }else{
	   dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
	  }*/
	  int dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
	  return weekDays[dayForWeek];
	 }
	 /**
	  * 判断日期是几号
	  * @param pTime
	  * @return
	 * @throws ParseException 
	  * @throws Exception
	  */
	 public static String dayForMonth(String pTime) throws ParseException {
		  Calendar c = Calendar.getInstance();
		  c.setTime(dateFormater.get().parse(pTime));
		  return StringUtils.flushRight('0',2,String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
		 }
}
