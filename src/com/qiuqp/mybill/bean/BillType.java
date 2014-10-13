package com.qiuqp.mybill.bean;

public class BillType {
	private String id;
	private int feetype;
	private String typeName;
	private boolean flag=false;
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
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String _typeName) {
		this.typeName = _typeName;
	}
	public boolean getFlag()
	{
		return flag;
	}
	public void setFalg(boolean _flag)
	{
		this.flag=_flag;
	}
}
