package com.qiuqp.mybill.bean;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 接口URL实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class URLs implements Serializable {
	
	public final static String HOST = "www.265xiaoxiao.com";//192.168.1.213  www.oschina.net
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	private final static String URL_SPLITTER = "/";
	
	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;
	public final static String LOGIN_VALIDATE_HTTP = HTTP + HOST + URL_SPLITTER + "action/api/login_validate";
    public final static String LOGIN_VALIDATE_HTTPS = HTTPS + HOST + URL_SPLITTER + "action/api/login_validate";
	public final static String NEWS_LIST = URL_API_HOST+"action/api/news_list";
	
	public final static String UPDATE_VERSION = URL_API_HOST+"Mobile/MobileAppVersion.xml";
	public final static String Service_Url = URL_API_HOST+"ashx/hld_Mobile.ashx";
	private final static String URL_HOST = "265xiaoxiao.com";
	
	
	private long objId;
	private String objKey = "";
	private int objType;
	
	public long getObjId() {
		return objId;
	}
	public void setObjId(long objId) {
		this.objId = objId;
	}
	public String getObjKey() {
		return objKey;
	}
	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}
	public int getObjType() {
		return objType;
	}
	public void setObjType(int objType) {
		this.objType = objType;
	}
	
	/**
	 * 转化URL为URLs实体
	 * @param path
	 * @return 不能转化的链接返回null
	 */
	/*public final static URLs parseURL(String path) {
		if(StringUtils.isEmpty(path))return null;
		path = formatURL(path);
		URLs urls = null;
		String objId = "";
		try {
			URL url = new URL(path);
			//站内链接
			if(url.getHost().contains(URL_HOST)){
				urls = new URLs();
				//www
				if(path.contains(URL_WWW_HOST )){
					//新闻  www.oschina.net/news/27259/mobile-internet-market-is-small
					if(path.contains(URL_TYPE_NEWS)){
						objId = parseObjId(path, "id");
						urls.setObjId(StringUtils.toLong(objId));
						urls.setObjType(URL_OBJ_TYPE_NEWS);
					}
					//软件  www.oschina.net/p/jx
					else if(path.contains(URL_TYPE_SOFTWARE)){
						urls.setObjKey(parseObjKey(path, URL_TYPE_SOFTWARE));
						urls.setObjType(URL_OBJ_TYPE_SOFTWARE);
					}
					//问答
					else if(path.contains(URL_TYPE_QUESTION)){
						//问答-标签  http://www.oschina.net/question/tag/python
						if(path.contains(URL_TYPE_QUESTION_TAG)){
							urls.setObjKey(parseObjKey(path, URL_TYPE_QUESTION_TAG));
							urls.setObjType(URL_OBJ_TYPE_QUESTION_TAG);
						}
						//问答  www.oschina.net/question/12_45738
						else{
							objId = parseObjId(path, URL_TYPE_QUESTION);
							String[] _tmp = objId.split(URL_UNDERLINE);
							urls.setObjId(StringUtils.toInt(_tmp[1]));
							urls.setObjType(URL_OBJ_TYPE_QUESTION);
						}
					}
					//other
					else{
						urls.setObjKey(path);
						urls.setObjType(URL_OBJ_TYPE_OTHER);
					}
				}
				//my
				else if(path.contains(URL_MY_HOST)){					
					//博客  my.oschina.net/szpengvictor/blog/50879
					if(path.contains(URL_TYPE_BLOG)){
						objId = parseObjId(path, URL_TYPE_BLOG);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_BLOG);
					}
					//动弹  my.oschina.net/dong706/tweet/612947
					else if(path.contains(URL_TYPE_TWEET)){
						objId = parseObjId(path, URL_TYPE_TWEET);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_TWEET);
					}
					//个人专页  my.oschina.net/u/12
					else if(path.contains(URL_TYPE_ZONE)){
						objId = parseObjId(path, URL_TYPE_ZONE);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_ZONE);
					}
					else{
						//另一种个人专页  my.oschina.net/dong706
						int p = path.indexOf(URL_MY_HOST+URL_SPLITTER) + (URL_MY_HOST+URL_SPLITTER).length();
						String str = path.substring(p);
						if(!str.contains(URL_SPLITTER)){
							urls.setObjKey(str);
							urls.setObjType(URL_OBJ_TYPE_ZONE);
						}
						//other
						else{
							urls.setObjKey(path);
							urls.setObjType(URL_OBJ_TYPE_OTHER);
						}
					}
				}
				//other
				else{
					urls.setObjKey(path);
					urls.setObjType(URL_OBJ_TYPE_OTHER);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			urls = null;
		}
		return urls;
	}*/
	/**
	 * 解析url获得objKey
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type){
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains("?")){
			tmp = str.split("?");
			objKey = tmp[0];
		}else{
			objKey = str;
		}
		return objKey;
	}
	
	/**
	 * 对URL进行格式处理
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if(path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}	
}
