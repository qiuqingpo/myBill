package com.qiuqp.mybill.common;
import java.util.regex.Pattern;

/** 
 * 字符串操作工具包
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils 
{
	private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	
	/**
	 * 判断给定字符串是否空白串。
	 * 空白串是指由空格、制表符、回车符、换行符组成的字符串
	 * 若输入字符串为null或空字符串，返回true
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty( String input ) 
	{
		if ( input == null || "".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		if(email == null || email.trim().length()==0) 
			return false;
	    return emailer.matcher(email).matches();
	}
	/**
	 * 字符串转整数
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try{
			return Integer.parseInt(str);
		}catch(Exception e){}
		return defValue;
	}
	/**
	 * 对象转整数
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if(obj==null) return 0;
		return toInt(obj.toString(),0);
	}
	/**
	 * 对象转整数
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try{
			return Long.parseLong(obj);
		}catch(Exception e){}
		return 0;
	}
	///
	public static Double toDouble(String obj) {
		try{
			return Double.parseDouble(obj);
		}catch(Exception e){}
		return 0.00;
	}
	/**
	 * 字符串转布尔值
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try{
			return Boolean.parseBoolean(b);
		}catch(Exception e){}
		return false;
	}
	/**
	 * 截取指定长度的字符串
	 * @param obj
	 * @param len
	 * @return
	 */
	public static String toSubString(String obj,int len)
	{
	String str=obj;
	if(obj.length()>len)
		str=obj.substring(0,len)+"...";
	return str;
	}
	  /** 
     * 使用java正则表达式去掉多余的.与0 
     * @param s 
     * @return  
     */  
    public static String subZeroAndDot(String s){  
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }  
        return s;  
    } 
    /* c 要填充的字符   
     *  length 填充后字符串的总长度   
     *  content 要格式化的字符串  
     *  格式化字符串，左对齐
     * */
  public static String flushLeft(char c, long length, String content){           
        String str = "";
        String cs = "";   
        if (content.length() >=length){   
             str = content;   
        }else{  
             for (int i = 0; i < length - content.length(); i++){   
                 cs = cs + c;   
             }
           }
         str = content + cs;    
         return str;    
    }     
  /* c 要填充的字符   
   *  length 填充后字符串的总长度   
   *  content 要格式化的字符串  
   *  格式化字符串，左对齐
   * */
    public static String flushRight(char c, long length, String content){           
      String str = "";
      String cs = "";   
      if (content.length() >=length){   
           str = content;   
      }else{  
           for (int i = 0; i < length - content.length(); i++){   
               cs = cs + c;   
           }
         }
       str = cs+content;    
       return str;    
  }
    /**
     * 是否以数字开头
     * @param strtext
     * @return
     */
 /*   public static boolean isNumeric(String strtext)
    {
    	String regex="^[0-9]*$";
    	if(strtext.matches(regex))
    		return true;
    	return false;
    }*/
    //定义一个可以输入多个条件方法。
    public static String getMutlWhere(String strtext, String colsName)
    {
        StringBuilder where = new StringBuilder();
        String newstr = where.toString();
        if (strtext!=""&&strtext!=null)
        {
        	String[] modelnets = strtext.split(",|，");
            where.append(" and (  ");
            for(String modelnet : modelnets)
            {
                where.append(String.format(" %s like '%s' or", colsName, "%"+modelnet+"%"));
            }
            newstr = where.toString().substring(0, where.toString().length() - 2) + ")";
            // where.AppendFormat(" and ModelNet like '%{0}%'", txtModelNet.Text);
        }
        return newstr;
    }
    /**
     * 取当前页的方法
     * @param pageCount
     * @param pageIndex
     * @param Page_Size
     * @return
     */
    public static int getCurPage(int pageCount,int pageIndex,  int Page_Size)
    {
		int allPageSize;
		int tempPageSize=pageCount/Page_Size;
		allPageSize=(pageCount%Page_Size==0)?tempPageSize:tempPageSize+1;
		int curPageSize=(allPageSize-pageIndex-1>0)?Page_Size:(pageCount-(pageIndex)*Page_Size);
		return curPageSize;
    }
}
