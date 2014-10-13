package com.qiuqp.mybill.bean;

/**
 * 实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public abstract class Entity extends Base {

	protected long id;

	public long getId() {
		return id;
	}
	public void setId(long _id)
	{
	this.id=_id;	
	}
	protected String cacheKey;

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
}
