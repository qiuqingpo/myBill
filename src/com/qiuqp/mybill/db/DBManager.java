package com.qiuqp.mybill.db;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.qiuqp.mybill.bean.Bill;
import com.qiuqp.mybill.bean.BillList;
import com.qiuqp.mybill.bean.BillType;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.db.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
private DBHelper helper;
private SQLiteDatabase db;  
public DBManager(Context context)
{
	helper=new DBHelper(context);
	//因为getwritableDatabase内部调用了mContext.openOrCreateDatabase(mName,0,mFactory);
	//所以要确保context已初始化，我们可以把实例DBManager的步骤放在Activity的onCreate里
	 getWriteDataBase();
}
/*public DBManager(Context context,boolean flag)
{
	helper=new DBHelper(context);
	//因为getwritableDatabase内部调用了mContext.openOrCreateDatabase(mName,0,mFactory);
	//所以要确保context已初始化，我们可以把实例DBManager的步骤放在Activity的onCreate里
}*/
public void CreateDataBase()
{
	try
	{
		helper.createDataBase();
	}
	catch(IOException e)
	{
		throw new Error("Create file fails");
	}
	creatTable();
	//db=helper.getWritableDatabase();
	
}
private void creatTable()
{
	  db=helper.getWritableDatabase();
	  db.execSQL("CREATE TABLE IF NOT EXISTS mybill" +  
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, price REAL,date TIMESTAMP default (datetime('now', 'localtime')),note Varchar,feetype INTEGER default(1) NOT NULL )");
	  db.execSQL("CREATE TABLE IF NOT EXISTS mybilltype" +  
	            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR,feetype INTEGER default(1) NOT NULL)");
	  if(checkBtypeRepeat("",0))//数据库中已经存在数据，此时不要再次插入
	  {
		  return;
	  }
	  //初始化类型值，以便为用户提供更好的体验
	  String sql="insert into mybilltype(type,feetype) values ('用餐',1)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('公交',1)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('话费',1)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('服饰',1)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('购物',1)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('旅游',1)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('娱乐',1)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('租房',1)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('其它',1)";
	  db.execSQL(sql);
	  //收入分类
	  sql="insert into mybilltype(type,feetype) values ('工资',2)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('奖金',2)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('分红',2)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('外快',2)";
	  db.execSQL(sql);
	  sql="insert into mybilltype(type,feetype) values ('其它',2)";
	  db.execSQL(sql);
}
public List<BillType> getBillType(int feetype)
{
	List<BillType> myList=new ArrayList<BillType>();
	BillType b=null;
	Cursor c=queryBillTypeCursor(feetype);
	while(c.moveToNext()){
	b=new BillType();
	b.setId(c.getString(c.getColumnIndex("_id")));
	b.setFeeType(c.getInt(c.getColumnIndex("feetype")));
	b.setTypeName(c.getString(c.getColumnIndex("type")));
		myList.add(b);
	}
	c.close();
	return myList;
}
public Cursor queryBillTypeCursor(int feetype)
{
	String sql="select _id,type,feetype from mybilltype where feetype="+feetype+"";
	if(feetype==0)
		sql="select _id,type,feetype from mybilltype";
	Cursor c=db.rawQuery(sql,null);
	return c;
}
public Bill getOneBill(String id)
{
	String sql="select _id,type,price,date,note,feetype from mybill where _id='"+id+"' ";
	Cursor c=db.rawQuery(sql,null);
	Bill n=null;
	while(c.moveToNext())
	{
		n= setBillByCursor(c,0);
	}
	c.close();
	return n;
}
/**
 * 一条类型信息
 * @param id
 * @return
 */
public BillType getOneBillType(String id)
{
	String sql="select _id,type,feetype from mybilltype where _id='"+id+"' ";
	Cursor c=db.rawQuery(sql,null);
	BillType b=null;
	while(c.moveToNext())
	{
		b=new BillType();
		b.setId(c.getString(c.getColumnIndex("_id")));
		b.setFeeType(c.getInt(c.getColumnIndex("feetype")));
		b.setTypeName(c.getString(c.getColumnIndex("type")));
	}
	c.close();
	return b;
}
public Cursor queryBillTodayCursor()
{
	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd");     
	String   date   =   sDateFormat.format(new   java.util.Date());  
	String sql="select _id,type,price,date,note,feetype from mybill where datetime(date)>=datetime('"+date+"') order by date desc";
	Cursor c=db.rawQuery(sql,null);
	return c;
}

public String queryBillTodayCount()
{
	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd");     
	String   date   =   sDateFormat.format(new   java.util.Date());  
	String sql="select sum(case feetype when 1 then -price else price end) from mybill where datetime(date)>=datetime('"+date+"')";
	Cursor c=db.rawQuery(sql,null);
	String count=getNewCountByCursor(c);
	c.close();
	return count;
}
private Bill setBillByCursor(Cursor c,int flag)
{
	    Bill n=new Bill();
	    n.setId(c.getString(c.getColumnIndex("_id")));
	    n.setFeeType(c.getInt(c.getColumnIndex("feetype")));
		n.setType(c.getString(c.getColumnIndex("type")));
		n.setPrice(c.getString(c.getColumnIndex("price")));
		n.setDate(c.getString(c.getColumnIndex("date")));
		n.setNotes(c.getString(c.getColumnIndex("note")));
		n.setFlag(flag);
	    return n;
}
private Bill setStatisticsByCursor(Cursor c, Bill n)
{
	    n=new Bill();
		n.setPrice(c.getString(c.getColumnIndex("price")));
		n.setDate(c.getString(c.getColumnIndex("date")));
	    return n;
}
/**
 * 今天的账单
 * @return
 */
public List<Bill> getBillTodayList()
{
List<Bill> mylist=new ArrayList<Bill>();
Cursor c=queryBillTodayCursor();
Bill n=null;
while(c.moveToNext()){
	n=setBillByCursor(c,0);
	mylist.add(n);
}
c.close();
return mylist;
}
public BillList query(int pageIndex,int PAGE_SIZE)
{
	BillList newslist = new BillList();
	Cursor[] cursorArr=queryTheCursor(pageIndex,PAGE_SIZE);
	Cursor c=cursorArr[0];
	int totalCount=StringUtils.toInt(getNewCountByCursor(cursorArr[1]));
	Bill n=null;
	while(c.moveToNext()){
		n=setBillByCursor(c,1);
		newslist.getNewslist().add(n);
	}

	newslist.setNewsCount(totalCount);
	c.close();
	cursorArr[1].close();
	return newslist;
}
public BillList queryStatistics(int pageIndex,int PAGE_SIZE,long selType)
{
	BillList newslist = new BillList();
	Cursor[] cursorArr=queryStatisticsCursor(pageIndex,PAGE_SIZE,selType);
	Cursor c=cursorArr[0];
	String totalCount=String.valueOf(cursorArr[1].getCount());
			//getNewCountByCursor(cursorArr[1]);
	Bill n=null;
	while(c.moveToNext()){
		n=new Bill();
		n=setStatisticsByCursor(c,n);
		newslist.getNewslist().add(n);
	}
	newslist.setNewsCount(StringUtils.toInt(totalCount));
	c.close();
	cursorArr[1].close();
	return newslist;
}
private void getWriteDataBase()
{
	if(helper.checkDataBase()&&db==null)
	{
		db=helper.getWritableDatabase();
	}	
}
public void addBill(Bill b)
{
	db.execSQL("INSERT INTO mybill VALUES(null, ?, ?, ?,?,?)", new Object[]{b.getType(), b.getPrice(), b.getDate(),b.getNotes(),b.getFeeType()});
}
public void addBillType(BillType b)
{
	db.execSQL("insert into mybilltype(type,feetype) values (?,?)", new Object[]{b.getTypeName(),b.getFeeType()});
}
public void updateBill(Bill b)
{
	db.execSQL("update mybill set type=?,price=?,date=?,note=?,feetype=? where _id=?", new Object[]{b.getType(), b.getPrice(), b.getDate(),b.getNotes(),b.getFeeType(),b.getId()});
}
public void updateBillType(BillType b,String oldtype)
{
	db.execSQL("update mybilltype set type=?,feetype=? where _id=? and  (type!=? or feetype!=?)", new Object[]{b.getTypeName(),b.getFeeType(),oldtype,b.getTypeName(),b.getFeeType()});
}
public void deleteBill(String id)
{
	db.delete("mybill", "_id=?", new String[]{id});
}
public void deleteBillType(String id)
{
	db.delete("mybilltype", "_id=?", new String[]{id});
}
/**
 *  如果已经存在，则返回true,否则返回false
 * @param typeName
 * @return true|false
 */

public boolean checkBtypeRepeat(String typeName,int feetype)
{
	String sql="select _id from mybilltype where type='"+typeName+"' and feetype="+feetype+" ";
	if(StringUtils.isEmpty(typeName))
	{
		sql="select _id from mybilltype";
	}
	Cursor c=db.rawQuery(sql,null);
	String id=getNewCountByCursor(c);
	return id!=""?true:false;
}
public Cursor[] queryTheCursor(int pageIndex,int PAGE_SIZE)
{
	String swhere="";
	String[] sqlArr=LoadForPageSql(pageIndex,PAGE_SIZE,"mybill","*",swhere,"","date desc");
	Cursor c1=db.rawQuery(sqlArr[0], null);
	Cursor c2=db.rawQuery(sqlArr[1], null);
	return new Cursor[]{c1,c2};
}
public Cursor[] queryStatisticsCursor(int pageIndex,int PAGE_SIZE,long selType)
{
	String swhere="";
	String sColumn="";
	String sGroupBy="";
	String sOrderBy="";
	if(selType==0)
	{
	 sColumn="sum(case feetype when 1 then -price else price end) as price, strftime('%Y-%m-%d',date) as date";
	 sGroupBy="strftime('%Y-%m-%d',date)";
	 sOrderBy="strftime('%Y-%m-%d',date) desc";
	}
	else if(selType==1)
	{
		 sColumn="sum(case feetype when 1 then -price else price end) as price, strftime('%Y年第%W周',date) as date";
		 sGroupBy="strftime('%Y年第%W周',date)";
		 sOrderBy="strftime('%Y年第%W周',date) desc";
	}
	else if(selType==2)
	{
		 sColumn="sum(case feetype when 1 then -price else price end) as price, strftime('%Y-%m',date) as date";
		 sGroupBy="strftime('%Y-%m',date)";
		 sOrderBy="strftime('%Y-%m',date) desc";
	}
	else if(selType==3)
	{
		 sColumn="sum(case feetype when 1 then -price else price end) as price, strftime('%Y',date) as date";
		 sGroupBy="strftime('%Y',date)";
		 sOrderBy="strftime('%Y',date) desc";
	}
	String[] sqlArr=LoadForPageSql(pageIndex,PAGE_SIZE,"mybill",sColumn,swhere,sGroupBy,sOrderBy);
	Cursor c1=db.rawQuery(sqlArr[0], null);
	Cursor c2=db.rawQuery(sqlArr[1], null);
	return new Cursor[]{c1,c2};
}
private String  getNewCountByCursor(Cursor c)
{
	String totalCount="";
	while(c.moveToNext()){
		totalCount=c.getString(0);
	}
	c.close();
	return totalCount;
}
/*public void closeDB()
{
	db.close();
}*/
public void close() {
    // NOTE: openHelper must now be a member of CallDataHelper;
    // you currently have it as a local in your constructor
    if (db != null) {
    	db.close();
    }
}
public static String[] LoadForPageSql(int _iPage, int _PageNum, String _sTableName, String _sColumns, String _sWhere,String _sGroupby, String _sOrder)
{
	String sWhere = "";
    if (_sWhere != null && _sWhere.length() > 0) sWhere = " WHERE " + _sWhere;
    String sOrder ="";
    if (_sOrder != null && _sOrder.length() > 0) sOrder = "order by " + _sOrder;
    String sGroupBy ="";
    if (_sGroupby != null && _sGroupby.length() > 0) sGroupBy = "group by " + _sGroupby;
    String sTotalCount = String.format("SELECT COUNT(*) FROM %s %s %s", _sTableName, sWhere,sGroupBy);
    
    String sLimit="";
    if(_iPage==0)
    	sLimit=String.format("limit 0,%s", _PageNum);
    else
    	sLimit=String.format("limit %s,%s",(_iPage)*_PageNum,(_iPage+1)*_PageNum);
    String  sCommand = String.format("SELECT  %s FROM %s %s %s %s %s", _sColumns, _sTableName, sWhere,sGroupBy,sOrder,sLimit);
    
    return new String[]{sCommand,sTotalCount};
}
/**
 * 以下为2014-07-31后增加的,用于显示各月的明细
 */
public String[] queryBillPerMonth(String month)
{
	String income="0";
	String consume="0";
	String sql="select sum(price) as price,strftime('%Y-%m',date) as month,feetype from mybill where strftime('%Y-%m',date)='"+month+"' group by feetype,strftime('%Y-%m',date) order by month,feetype";
	Cursor c=db.rawQuery(sql,null);
	while(c.moveToNext())
	{
		int feetype=c.getInt(c.getColumnIndex("feetype"));
		String price=c.getString(c.getColumnIndex("price"));
		if(feetype==1)//消费
		{
			consume=price;
		}
		else
			income=price;
	
	}
	c.close();
	return new String[]{income,consume};
}
/**
 * 以下为2014-07-31后增加的,用于显示各月每天的明细
 */
public List<Bill> queryBillPerMonthDay(String month)
{
	String sql="select _id,type,price,date,note,feetype from mybill where strftime('%Y-%m',date)='"+month+"'  order by date desc,feetype";
	Cursor c=db.rawQuery(sql,null);
	List<Bill> mylist=new ArrayList<Bill>();
	Bill n=null;
	if(c.getCount()==0)
	{
		n=new Bill();
		n.setFlag(1);
		mylist.add(n);
	}
	else
	{
	while(c.moveToNext()){
		n=setBillByCursor(c,0);
		mylist.add(n);
	}
	}
	c.close();
	return mylist;
}
}
