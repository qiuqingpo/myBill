package com.qiuqp.mybill;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;



import com.qiuqp.mybill.api.ApiClient;
import com.qiuqp.mybill.bean.BillList;
import com.qiuqp.mybill.bean.Result;
import com.qiuqp.mybill.common.MethodsCompat;
import com.qiuqp.mybill.common.StringUtils;
import com.qiuqp.mybill.common.TimeUtils;
import com.qiuqp.mybill.common.UIHelper;
import com.qiuqp.mybill.db.DBManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;




//import com.qiuqp.i_love_smile.common.ApiException;
//import android.webkit.CacheManager;

/**
* 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
* @author liux (http://my.oschina.net/liux)
* @version 1.0
* @created 2012-3-21
*/
public class AppContext extends Application {
	
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 20;//默认分页大小
	private static final int CACHE_TIME = 60*60000;//缓存失效时间
	
	private boolean login = false;	//登录状态
	private int loginUid = 0;	//登录用户的id
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	
	private String saveImagePath;//保存图片路径

	private Handler unLoginHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
				//UIHelper.showLoginDialog(AppContext.this);
			}
		}		
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
      //注册App异常崩溃处理器
      Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
      init();
	}

	/**
	 * 初始化
	 */
	private void init(){
		//设置保存图片的路径
		saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
		if(StringUtils.isEmpty(saveImagePath)){
			setProperty(AppConfig.SAVE_IMAGE_PATH, AppConfig.DEFAULT_SAVE_IMAGE_PATH);
			saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
		}
	}
	
	/**
	 * 检测当前系统声音是否为正常模式
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE); 
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}
	
	/**
	 * 应用程序是否发出提示音
	 * @return
	 */
	public boolean isAppSound() {
		//return true;
		return isAudioNormal() && isVoice();
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		try
		{
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting()&&isAccessNet();
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	/**
	 * 当前版本 
	 * @return
	 */
	public int getCurrentVersion()
	{
		PackageInfo pi=getPackageInfo();
		return pi.versionCode;
	}
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	
	/**
	 * 用户是否登录
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}
	
	/**
	 * 获取登录用户id
	 * @return
	 */
	public int getLoginUid() {
		return this.loginUid;
	}
	
	/**
	 * 用户注销
	 */
	public void Logout() {
		//ApiClient.cleanCookie();
		this.cleanCookie();
		this.login = false;
		this.loginUid = 0;
	}
	
	/**
	 * 未登录或修改密码后的处理
	 */
	public Handler getUnLoginHandler() {
		return this.unLoginHandler;
	}
	
	/**
	 * 初始化用户登录信息
	 */
	public void initLoginInfo() {
	/*	User loginUser = getLoginInfo();
		if(loginUser!=null && loginUser.getUid()>0 && loginUser.isRememberMe()){
			this.loginUid = loginUser.getUid();
			this.login = true;
		}else{
			this.Logout();
		}*/
	}
	
	
	/**
	 * 获取用户通知信息
	 * @param uid
	 * @return
	 * @throws AppException
	 *//*
	public Notice getUserNotice(int uid) throws AppException {
		return ApiClient.getUserNotice(this, uid);
	}
	*//**
	 * 清空通知消息
	 * @param uid
	 * @param type 1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
	 * @return
	 * @throws AppException
	 *//*
	public Result noticeClear(int uid, int type) throws AppException {
		return ApiClient.noticeClear(this, uid, type);
	}
	*//**
	 * 新闻列表
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws ApiException
	 *//*
	public NewsList getNewsList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		NewsList list = null;
		String key = "newslist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getNewsList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					list.setCacheKey(key);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (NewsList)readObject(key);
				if(list == null)
					throw e;
			}		
		} else {
			list = (NewsList)readObject(key);
			if(list == null)
				list = new NewsList();
		}
		return list;
	}
	*//**
	 * 得到精选的数据
	 * @param catalog
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 *//*
	 public NewsList getWonderfulList(DBManager mgr,int catalog, int pageIndex, boolean isRefresh) throws AppException {
		NewsList list = null;
			try{
				list = ApiClient.getWonderfulList(this,mgr,catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(notice);
				}
			}catch(AppException e){
					throw e;
			}		
		return list;
	}
	 public NewsList getAboutWonderfulList(DBManager mgr,final long newsid, String  label) throws AppException {
			NewsList list = null;
				try{
					list = ApiClient.getAboutWonderfulList(this,mgr,newsid, label);
					if(list != null){
						Notice notice = list.getNotice();
						list.setNotice(notice);
					}
				}catch(AppException e){
						throw e;
				}		
			return list;
		}
	*//**
	 * 新闻详情
	 * @param news_id
	 * @return
	 * @throws ApiException
	 *//*
	public News getNews(DBManager mgr,long news_id, boolean isRefresh) throws AppException {		
		News news = null;
		String key = "news_"+news_id;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try{
				news = ApiClient.getNewsDetail(mgr,this, news_id);
				if(news != null){
					Notice notice = news.getNotice();
					news.setNotice(null);
					news.setCacheKey(key);
					saveObject(news, key);
					news.setNotice(notice);
				}
			}catch(AppException e){
				news = (News)readObject(key);
				if(news == null)
					throw e;
			}
		} else {
			news = (News)readObject(key);
			if(news == null)
				news = new News();
		}
		return news;		
		News news = null;
		try{
			news = ApiClient.getNewsDetail(mgr,this, news_id);
			if(news != null){
				Notice notice = news.getNotice();
				news.setNotice(notice);
			}
		}
		catch(AppException e)
		{
			throw e;
		}
		return news;
	}
	
	
	
	*//**
	 * 读取帖子详情
	 * @param post_id
	 * @return
	 * @throws ApiException
	 *//*
	public Post getPost(int post_id, boolean isRefresh) throws AppException {		
		Post post = null;
		String key = "post_"+post_id;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {	
			try{
				post = ApiClient.getPostDetail(this, post_id);
				if(post != null){
					Notice notice = post.getNotice();
					post.setNotice(null);
					post.setCacheKey(key);
					saveObject(post, key);
					post.setNotice(notice);
				}
			}catch(AppException e){
				post = (Post)readObject(key);
				if(post == null)
					throw e;
			}
		} else {
			post = (Post)readObject(key);
			if(post == null)
				post = new Post();
		}
		return post;		
	}

	*//**
	 * 评论列表
	 * @param catalog 1新闻 2帖子 3动弹 4动态
	 * @param id 某条新闻，帖子，动弹的id 或者某条留言的friendid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 *//*
	public CommentList getCommentList(int catalog, long id, int pageIndex, boolean isRefresh) throws AppException {
		CommentList list = null;
		String key = "commentlist_"+catalog+"_"+id+"_"+pageIndex+"_"+PAGE_SIZE;		
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getCommentList(this, catalog, id, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					list.setCacheKey(key);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (CommentList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (CommentList)readObject(key);
			if(list == null)
				list = new CommentList();
		}
		return list;
	}*/
	/**
	 * 是否启动检查更新
	 * @return
	 */
	public boolean isCheckUp()
	{
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
		//默认是开启
		if(StringUtils.isEmpty(perf_checkup))
			return true;
		else
			return StringUtils.toBool(perf_checkup);
	}
	
	/**
	 * 设置启动检查更新
	 * @param b
	 */
	public void setConfigCheckUp(boolean b)
	{
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}
	
	/**
	 * 是否左右滑动
	 * @return
	 */
	public boolean isScroll()
	{
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		//默认是关闭左右滑动
		if(StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}
	
	/**
	 * 设置是否左右滑动
	 * @param b
	 */
	public void setConfigScroll(boolean b)
	{
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}
	
	/**
	 * 是否Https登录
	 * @return
	 */
	public boolean isHttpsLogin()
	{
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		//默认是http
		if(StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}
	
	/**
	 * 设置是是否Https登录
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b)
	{
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}
	
	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie()
	{
		removeProperty(AppConfig.CONF_COOKIE);
	}
	/**
	 * 是否加载显示文章图片
	 * @return
	 */
	public boolean isLoadImage()
	{
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		//默认是加载的
		if(StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}
	
	/**
	 * 设置是否加载文章图片
	 * @param b
	 */
	public void setConfigLoadimage(boolean b)
	{
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}
	
	/**
	 * 是否发出提示音
	 * @return
	 */
	public boolean isVoice()
	{
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		//默认是开启提示声音
		if(StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}
	/**
	 * 设置是否发出声音
	 * @param b
	 */
	public void setConfigVoice(boolean b)
	{
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}
	/**
	 * 是否开启手势密码
	 * @return
	 */
	public boolean isOpenPwd()
	{
		String perf_openpwd = getProperty(AppConfig.CONF_OPENPWD);
		//默认是关闭密码保护
		if(StringUtils.isEmpty(perf_openpwd))
			return false;
		else
			return StringUtils.toBool(perf_openpwd);
	}
	/**
	 * 设置是否开启手势密码
	 * @param b
	 */
	public void setConfigOpenPwd(boolean b)
	{
		setProperty(AppConfig.CONF_OPENPWD, String.valueOf(b));
	}
	/**
	 * 得到手势密码
	 * @return
	 */
	public String getPwdLimitTime()
	{
		return getProperty(AppConfig.CONF_PWDLIMITTIME);
	}
	/**
	 * 设置手势密码
	 * @param b
	 */
	public void setPwdLimitTime(String date)
	{
		setProperty(AppConfig.CONF_PWDLIMITTIME, date);
	}
	/**
	 * 得到更新日期
	 * @return
	 */
	public String getUpdateLimitTime()
	{
		return getProperty(AppConfig.CONF_UPDATELIMITTIME);
	}
	/**
	 * 设置更新日期
	 * @param b
	 */
	public void setUpdateLimitTime(String date)
	{
		setProperty(AppConfig.CONF_UPDATELIMITTIME, date);
	}
	/**
	 * 最大升级天数
	 * @return
	 */
	public Date getMaxUpdateDate(int days)
	{
		Calendar c = Calendar.getInstance();
        c.setTime(new Date());   //设置当前日期
        c.add(Calendar.DAY_OF_YEAR, days); //日期加1
        Date date = c.getTime(); //结果
        return date;
	}
	/**
	 * 得到密保答案
	 * @return
	 */
	public String getPwdAnswer()
	{
		return getProperty(AppConfig.CONF_ANSWERPWD);
	}
	/**
	 * 设置密保答案
	 * @param b
	 */
	public void setPwdAnswer(String ans)
	{
		setProperty(AppConfig.CONF_ANSWERPWD, ans);
	}
	/**
	 * 是否开启网络连接
	 * @return
	 */
	public boolean isAccessNet()
	{
		String perf_accessNet = getProperty(AppConfig.CONF_AccessNet);
		//默认是开启网络连接
		if(StringUtils.isEmpty(perf_accessNet))
			return true;
		else
			return StringUtils.toBool(perf_accessNet);
	}
	/**
	 * 设置是否网络连接
	 * @param b
	 */
	public void setConfigAccessNet(boolean b)
	{
		setProperty(AppConfig.CONF_AccessNet, String.valueOf(b));
	}
	public boolean isCanUse(String limitTime)
	{
		Calendar c = Calendar.getInstance();
        Date date = c.getTime(); //结果
        //String limitTime=appContext.getPwdLimitTime();
		if(!StringUtils.isEmpty(limitTime))
		{
			int dtspan=TimeUtils.compare_date(limitTime, TimeUtils.getFormatTime(date));
			if(dtspan==1)
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * 判断缓存数据是否可读
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile)
	{
		return readObject(cachefile) != null;
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile)
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 判断缓存是否失效
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile)
	{
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if(!data.exists())
			failure = true;
		return failure;
	}
	
	/**
	 * 清除app缓存
	 */
	public void clearAppCache()
	{
		//清除webview缓存
		/*File file = CacheManager.getCacheFileBaseDir();  
		if (file != null && file.exists() && file.isDirectory()) {  
		    for (File item : file.listFiles()) {  
		    	item.delete();  
		    }  
		    file.delete();  
		} */ 		  
		deleteDatabase("webview.db");  
		deleteDatabase("webview.db-shm");  
		deleteDatabase("webview.db-wal");  
		deleteDatabase("webviewCache.db");  
		deleteDatabase("webviewCache.db-shm");  
		deleteDatabase("webviewCache.db-wal");  
		//清除数据缓存
		clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		//2.2版本才有将应用缓存转移到sd卡的功能
		if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		}
		//清除编辑器保存的临时内容
		Properties props = getProperties();
		for(Object key : props.keySet()) {
			String _key = key.toString();
			if(_key.startsWith("temp"))
				removeProperty(_key);
		}
	}	
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	
	/**
	 * 将对象保存到内存缓存中
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}
	
	/**
	 * 从内存缓存中获取对象
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key){
		return memCacheRegion.get(key);
	}
	
	/**
	 * 保存磁盘缓存
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try{
			fos = openFileOutput("cache_"+key+".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 获取磁盘缓存数据
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try{
			fis = openFileInput("cache_"+key+".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}

	public boolean containsProperty(String key){
		Properties props = getProperties();
		 return props.containsKey(key);
	}
	
	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties(){
		return AppConfig.getAppConfig(this).get();
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * 获取内存中保存图片的路径
	 * @return
	 */
	public String getSaveImagePath() {
		return saveImagePath;
	}
	/**
	 * 设置内存中保存图片的路径
	 * @return
	 */
	public void setSaveImagePath(String saveImagePath) {
		this.saveImagePath = saveImagePath;
	}	
	public BillList getNewsList(DBManager mgr,int pageIndex)throws AppException {
		BillList list = null;
	    try {
			list = BillList.getWonderfulList(mgr, pageIndex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public BillList getStatistics(DBManager mgr,int pageIndex,long selType)throws AppException {
		BillList list = null;
	    try {
			list = BillList.getStatistics(mgr, pageIndex,selType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	//提交问题反馈
	public Result PostQuestion(String nickNameStr,String contactType,String contactAddress,String notes, int type) throws AppException {
		return ApiClient.PostQuestion(this, nickNameStr, contactType, contactAddress, notes, type);
	}
}
