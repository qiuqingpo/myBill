package com.qiuqp.mybill.db;


import java.io.File;
import java.io.IOException;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBHelper extends SQLiteOpenHelper{
	
	  //如果你想把数据库文件存放在SD卡的话
  private static final String DATABASE_PATH     = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
	                                          + "/com.mybill/databases/";
	 
	private static final  String DB_NAME= "mybill.db";
 
  private static final int DATABASE_VERSION = 2;  
 // private final Context myContext; 
  public DBHelper(Context context) {  
      //CursorFactory设置为null,使用默认值 
      super(context, DATABASE_PATH+DB_NAME, null, DATABASE_VERSION);  
     // this.myContext=context;
  }  
//数据库第一次被创建时onCreate会被调用  
  @Override  
  public void onCreate(SQLiteDatabase db) {  
  	/*try
  	{
  	  createDataBase();
  	}
  	catch(IOException e)
  	{
  		 throw new Error("数据库创建失败");
  	}*/
     /* db.execSQL("CREATE TABLE IF NOT EXISTS Joke" +  
              "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, info TEXT,date DATETIME )");  */
  }  

  //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade  
  @Override  
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
      //db.execSQL("ALTER TABLE Joke ADD COLUMN other STRING");  
	  if(oldVersion==1)
	  {
		  db.execSQL("ALTER TABLE mybill ADD COLUMN feetype INTEGER default(1) NOT NULL "); 
		  db.execSQL("ALTER TABLE mybilltype ADD COLUMN feetype INTEGER default(1) NOT NULL ");
		  db.execSQL("update mybill set feetype=1 "); 
		  db.execSQL("update mybill set feetype=1 ");
		  //收入分类
		  String sql="insert into mybilltype(type,feetype) values ('工资',2)";
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
  }  
  public void createDataBase() throws IOException{
	  String storageState = Environment.getExternalStorageState();	
	  if(!storageState.equals(Environment.MEDIA_MOUNTED))
	  {
		  return;
	  }
      boolean dbExist = checkDataBase();
      if(dbExist){
          //数据库已存在，do nothing.
      }else{
    	  File dir = new File(DATABASE_PATH);
		   if(!dir.exists()){
		       dir.mkdirs();
		   }
		   File dbf = new File(DATABASE_PATH + DB_NAME);
		   if(dbf.exists()){
		       dbf.delete();
		   }
		   SQLiteDatabase.openOrCreateDatabase(dbf, null);
		   // 复制asseets中的db文件到DB_PATH下
		   //copyDataBase();
      }
  }
  //检查数据库是否有效
  public boolean checkDataBase(){
      SQLiteDatabase checkDB = null;
      String myPath = DATABASE_PATH + DB_NAME;
      try{            
          checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
      }catch(SQLiteException e){
          //database does't exist yet.
      }
      if(checkDB != null){
          checkDB.close();
      }
       return checkDB != null ? true : false;
  }
}
