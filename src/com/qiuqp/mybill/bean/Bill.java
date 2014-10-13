package com.qiuqp.mybill.bean;

import com.qiuqp.mybill.common.StringUtils;

public class Bill {

	private String id;
	private int feetype;
	private String type;
	private String price;
	private String date;
	private String notes;
	private int flag;

	public Bill(){
	}	
	public String getId() {
		return id;
	}
	public void setId(String _id) {
		this.id = _id;
	}
	public int getFeeType() {
		return feetype;
	}
	public void setFeeType(int _feetype) {
		this.feetype = _feetype;
	}
	public String getType() {
		return type;
	}
	public void setType(String _type) {
		this.type = _type;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String _price) {
		this.price = StringUtils.subZeroAndDot(_price);
	}
	public String getDate() {
		return date;
	}
	public void setDate(String _date) {
		this.date = _date;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String _notes) {
		this.notes = _notes;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int _flag) {
		this.flag = _flag;
	}
/*	public static News getWonderful(DBManager mgr,final long newsid) throws IOException, AppException {
		News news= new News();
		news=(News)mgr.queryOneNews(newsid);  
		

		news.setNotice(new Notice());
        return news;       
	}
	public static boolean setAgreeCount(DBManager mgr,final long newsid,String type)
	{
		return mgr.updateAgreeCount(newsid, type);
	}*/
}
