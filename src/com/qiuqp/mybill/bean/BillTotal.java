package com.qiuqp.mybill.bean;

import com.qiuqp.mybill.common.StringUtils;

public class BillTotal {

	private int year;
	private int month;
	private String income;
	private String consume;

	public BillTotal(){
	}	
	public int getYear() {
		return year;
	}
	public void setYear(int _year) {
		this.year = _year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int _month) {
		this.month = _month;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String _income) {
		this.income =  StringUtils.subZeroAndDot(_income);
	}
	public String getConsume() {
		return consume;
	}
	public void setConsume(String _consume) {
		this.consume = StringUtils.subZeroAndDot(_consume);
	}
}
