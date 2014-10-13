package com.qiuqp.mybill.bean;
import com.qiuqp.mybill.AppContext;
import com.qiuqp.mybill.AppException;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.db.DBManager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class BillList extends Entity{
	private int catalog;
	private int pageSize;
	private int newsCount;
	private List<Bill> newslist = new ArrayList<Bill>();
	
	public int getCatalog() {
		return catalog;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int _pageSize) {
		 pageSize=_pageSize;
	}
	public int getNewsCount() {
		return newsCount;
	}
	public void setNewsCount(int _newsCount)
	{
		newsCount=_newsCount;
	}
	public List<Bill> getNewslist() {
		return newslist;
	}
	
	/**
	 * 
	 * @param mgr
	 * @param pageIndex
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static BillList getWonderfulList(DBManager mgr,int pageIndex) throws IOException, AppException {
		int Page_Size= AppContext.PAGE_SIZE;
		BillList newslist = new BillList();
		newslist=mgr.query(pageIndex,Page_Size);  
		newslist.pageSize=StringUtils.getCurPage(newslist.newsCount, pageIndex, Page_Size);
		newslist.setNotice(new Notice());
        return newslist;       
	}
	public static BillList getStatistics(DBManager mgr,int pageIndex,long selType) throws IOException, AppException {
		int Page_Size= AppContext.PAGE_SIZE;
		BillList newslist = new BillList();
		newslist=mgr.queryStatistics(pageIndex,Page_Size,selType);  
		newslist.pageSize=StringUtils.getCurPage(newslist.newsCount, pageIndex, Page_Size);
		newslist.setNotice(new Notice());
        return newslist;       
	}
	
}
