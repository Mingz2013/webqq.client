package com.mingz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import javax.jws.soap.SOAPBinding.Style;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.sun.jna.platform.win32.OaIdl.VARDESC;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class WebQQClient {
	
	private CloseableHttpClient httpclient;
	private CookieStore cookieStore;
	
	private String qq_number;
	private String password;
	
	
	private String cap_cd;
	private String verifycode;
	private String pt_uin;
	private String ptvfsession;
	private String ptwebqq;
	private String vfwebqq;
	private String psessionid;
	private String hash;
	
	
	private String check_sig_url;
	private String nick;
	private long clientid;
	private String status;
	private JSONObject self_info;
	
	private int msg_id;
	
	
 	WebQQClient(String qqNumber, String password){
		this.qq_number = qqNumber;
		this.password = password;
		this.clientid = Tools.getRand(8);
		this.msg_id = 4710001;
		System.out.println("init-->   QQ:->" + this.qq_number + " , password:->" + this.password + " , clientid:->" + this.clientid + " , msg_id:->" + this.msg_id);
		
//		this.httpclient = HttpClients.createDefault();
		
//		this.context = HttpClientContext.create();
		this.cookieStore = new BasicCookieStore();
	
//		this.context.setCookieStore(cookieStore);
		
		this.httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//		this.httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.ACCEPT_ALL); 
		
		
	}

	// 密码框获得焦点时，检查输入的QQ号是否需要输入
	boolean get_check(){
		String url = "https://ssl.ptlogin2.qq.com/check?pt_tea=1&uin=" + this.qq_number + "&appid=501004106&js_ver=10129&js_type=0&login_sig=&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html&r=0." + Tools.getRand(16);
		
//		BasicClientCookie cookie = new BasicClientCookie("chkuin", "3207662459");
//		this.cookieStore.addCookie(cookie);
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Cookie", "chkuin=" + this.qq_number);
		String context = "";
		try {
			CloseableHttpResponse response1 = this.httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
		    HttpEntity entity1 = response1.getEntity();
		    
		
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
		    
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
//		System.out.println("get_check: ->" + this.cookieStore);
		
		
//	    System.out.println(context);
	    // ptui_checkVC('0','!OUX','\x00\x00\x00\x00\xbf\x31\x0b\x7b','3d1188b3e9e6846382d10b7a3a83cccfbc573eb26c55bf6515890be33e29c6ac7fcec54a4c9c1c1adfb6c6dcb40c6348f4f7364116552052','0');
	    // ptui_checkVC('1','2xWvBdoghTDME62nPE5YxhBhFGhNkZVQLmdEXcg8gKHoPrsgnhtXRw**','\x00\x00\x00\x00\xbf\x31\x0b\x7b','','0');
	    String arr[] = context.split("','");
//	    this.textCode = Tools.getMidOfTwoText(context, "','", "','");
//	    this.pt_uin = context.right(35).left(32);
//	    this.cap_cd = arr[1];
	    this.pt_uin = arr[3];
//	    System.out.println(this.cap_cd + " " + this.pt_uin);
	    
	 // 根据获取的数据判断是否需要验证码
	    if(arr[1].length() == 4){
	    	// 不许要验证码
	    	System.out.println("get_check:-> 不需要输入验证码");
	    	this.verifycode = arr[1];
	        return false;
	    }else if(arr[1].length() > 4){
	    	// 需要验证码，获取验证码图片
	    	this.cap_cd = arr[1];
	        return true;
	    }else if(arr[1].length() == 0){
	    	// QQ号有错误
	        System.out.println("textcode长度为0，QQ号有错误，请重新输入");
	        return false;
	    }else{
	    	// 未知情况
	    	System.out.println("----------textcode长度在非指定范围内，----------");
	        return false;
	    }
	    
	}
	
	// 获取验证码图片
	void get_image(){
		String url = "https://ssl.captcha.qq.com/getimage?aid=501004106&r=0."+Tools.getRand(17)+"&uin="+this.qq_number+"&cap_cd=" + this.cap_cd;
		
		HttpGet httpGet = new HttpGet(url);
		
		try {
			CloseableHttpResponse response1 = this.httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			
			File storeFile = new File("getimage.jpeg");  
			FileOutputStream output = new FileOutputStream(storeFile);  
			HttpEntity entity1 = response1.getEntity();
	        //得到网络资源的字节数组,并写入文件  
//	        output.write(entity1);  
	        
			entity1.writeTo(output);
		    response1.close();
		    output.close(); 
		    
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	void setVerifycode(String verifycode){
		this.verifycode = verifycode;
	}
	
	// 密码加密，在get_login时用到
	String doMd5(String password, String pt_uin, String verifycode){
		try {
			
			ScriptEngineManager manager = new ScriptEngineManager();   
			ScriptEngine engine = manager.getEngineByName("javascript");     

			String jsFileName = "./js/encryption.js";   // 读取js文件   

			FileReader reader = new FileReader(jsFileName);   // 执行指定脚本   
			engine.eval(reader);   

			String md5_passwd = "";
			if(engine instanceof Invocable) {    
				Invocable invoke = (Invocable)engine;    // 调用merge方法，并传入两个参数    
		
				// c = merge(2, 3);    
		
				md5_passwd = (String)invoke.invokeFunction("md5_password", password, pt_uin, verifycode);    
		
//				System.out.println("md5_passwd = " + md5_passwd);   
			}   

			reader.close(); 
			
			return md5_passwd;
			
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	// 得到一个hash值，在获取好友列表 。。。等 时 用到
	String doHash(String qq_number, String ptwebqq){
		try {
			
			ScriptEngineManager manager = new ScriptEngineManager();   
			ScriptEngine engine = manager.getEngineByName("javascript");     

			String jsFileName = "./js/hash.js";   // 读取js文件   

			FileReader reader = new FileReader(jsFileName);   // 执行指定脚本   
			engine.eval(reader);   

			String md5_passwd = "";
			if(engine instanceof Invocable) {    
				Invocable invoke = (Invocable)engine;    // 调用merge方法，并传入两个参数    
		
				// c = merge(2, 3);    
		
				md5_passwd = (String)invoke.invokeFunction("u", qq_number, ptwebqq);    
		
//				System.out.println("md5_passwd = " + md5_passwd);   
			}   

			reader.close(); 
			
			return md5_passwd;
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	

	// 第一次login，登录
	void get_login(){

		String md5_password = this.doMd5(this.password, this.qq_number, this.verifycode);
	
		
		String url = "https://ssl.ptlogin2.qq.com/login?u="+this.qq_number+"&p="+md5_password+"&verifycode="+this.verifycode+"&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&h=1&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-23-161044&mibao_css=m_webqq&t=1&g=1&js_type=0&js_ver=10129&login_sig=&pt_randsalt=0&pt_vcode_v1=0&pt_verifysession_v1=" + this.pt_uin;
//		System.out.println(url);
		HttpGet httpGet = new HttpGet(url);
		
		String context = "";
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println(context);
		// ptuiCB('7','0','','0','提交参数错误，请检查。(2968519300)', '3207662459');
		// ptuiCB('4','0','','0','您输入的验证码不正确，请重新输入。', '3207662459');
		// ptuiCB('3','0','','0','您输入的帐号或密码不正确，请重新输入。', '3207662459');
		// ptuiCB('1','0','','0','登录失败，请稍后再试。(10000)', '3207662459');
		// ptuiCB('0','0','http://ptlogin4.web2.qq.com/check_sig?pttype=1&uin=3207662459&service=login&nodirect=0&ptsigx=72a04f8f7574a5a43db40458d6a2a19a3f9fb1eef25f992595ded735f6b89e8ec90595e152404566db686d711c1ab585eb7e79cedb76a9e370bf2db494a34637&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&f_url=&ptlang=2052&ptredirect=100&aid=501004106&daid=164&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=1&pt_aid=0&pt_aaid=0&pt_light=0&pt_3rd_aid=0','0','登录成功！', '测试QQ');
		int n = context.lastIndexOf("登录成功");
		if(n == -1){
			System.out.println("登录失败");
			return;
		}
		
		 String arr[] = context.split("','");
		 
//		 System.out.println(arr);
		 
		 this.check_sig_url = arr[2];
//		 System.out.println(this.check_sig_url);
		 
		 this.nick = Tools.getMidOfTwoText(context, "'登录成功！', '", "');");
		 	 
		 System.out.println("登录成功:->" + this.nick);
		 
		 

		 
//		this.printCookies();
		
	
	}
	
	void printCookies(){
		 List<Cookie> cookies = this.cookieStore.getCookies();
			System.out.println("cookies "+cookies.size());
			for(Cookie cookie: cookies)
	            //System.out.println(cookie.getName() + "=" + cookie.getValue() + ";");
	            System.out.println(cookie);
//			
//			if (!cookies.isEmpty()) {
//				
//				for (int i = cookies.size(); i > 0; i--) {
//					Cookie cookie = (Cookie) cookies.get(i - 1);
//					System.out.println(cookie.getName() + " " + cookie.getValue() );
//					if (cookie.getName().equalsIgnoreCase("ptvfsession")) {
//						this.ptvfsession = cookie.getValue();
//					}
//				}
//			}
	}
	
	// 需要调用这个请求，来获取一些cookie值
	void get_check_sig(){
		String url = this.check_sig_url;
		HttpGet httpGet = new HttpGet(url);
		String context = "";
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("get_check_sig->");
//		this.printCookies();
		
	}
	
	// 第二次login，通知其他人自己上线
	void post_login2(){
		String url = "http://d.web2.qq.com/channel/login2";
		HttpPost httpPost = new HttpPost(url);
		
		List<Cookie> cookies = this.cookieStore.getCookies();
		for(Cookie cookie: cookies)
			if (cookie.getName().equalsIgnoreCase("ptwebqq")){
				this.ptwebqq = cookie.getValue();
				break;
			}
		
		String referer = "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2";
		httpPost.addHeader("Referer", referer);
		// data : r:{"ptwebqq":"88d0d404f989a53085b03541eda9e28ae6448eb7cf4873db39e03eb116bdaafa","clientid":53999199,"psessionid":"","status":"online"}
		
		JSONObject obj_r = new JSONObject();
		obj_r.put("ptwebqq", this.ptwebqq);
		obj_r.put("clientid", this.clientid);
		obj_r.put("psessionid", "");
		obj_r.put("status", "online");	// TODO 这里可以改为配置
		
		
		// 创建参数队列    
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("r", obj_r.toString()));  
       
        
        String context = "";
        try {
        	UrlEncodedFormEntity uefEntity;  
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);  
			CloseableHttpResponse response = this.httpclient.execute(httpPost);
			
	       
	        HttpEntity entity = response.getEntity();  
	        context = EntityUtils.toString(entity);
	        response.close();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
//        System.out.println("post_login2-> " + context);
        // {"retcode":103,"errmsg":""}
        // {"retcode":0,"result":{"uin":3207662459,"cip":2738221922,"index":1075,"port":60120,"status":"online","vfwebqq":"db91f855abc3253473fd2bcc3cd8b04611828759f40444a194435206f82840a3a162924e9605832f","psessionid":"8368046764001d636f6e6e7365727665725f77656271714031302e3133392e372e31363400007f9100001819036e04007b0b31bf6d0000000a405034414876355637626d00000028db91f855abc3253473fd2bcc3cd8b04611828759f40444a194435206f82840a3a162924e9605832f","user_state":0,"f":0}}
        
        
        JSONObject obj = JSONObject.fromObject(context);
        
//        System.out.println(obj);
        
        int retcode = obj.getInt("retcode");
        if(retcode != 0) {
        	System.err.println("post_login2 retcode:->" + retcode);
        	return;
        }
        
        JSONObject result = obj.getJSONObject("result");
        this.status = result.getString("status");
        this.vfwebqq = result.getString("vfwebqq");
        this.psessionid = result.getString("psessionid");
        
        System.out.println("post_login2:->" + result);
        
	}
	
	// 获取好友列表
	void post_get_user_friends2(){
		String url = "http://s.web2.qq.com/api/get_user_friends2";
		// data r:{"vfwebqq":"47c9d9a68ad9e2dec7b8c4d3e61575c0876ac79ae6ca279f0c52e91845e3d66c455dea6a88da8b0d","hash":"0EFA077251440830"}
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Referer", referer);
		
		this.hash = this.doHash(this.qq_number, this.ptwebqq);
	
		JSONObject obj_r = new JSONObject();
		obj_r.put("vfwebqq", this.vfwebqq);
		obj_r.put("hash", this.hash);
		
		// 创建参数队列    
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("r", obj_r.toString()));  
       
        
        String context = "";
        try {
        	UrlEncodedFormEntity uefEntity;  
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);  
			CloseableHttpResponse response = this.httpclient.execute(httpPost);
			
	       
	        HttpEntity entity = response.getEntity();  
	        context = EntityUtils.toString(entity);
	        response.close();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        JSONObject obj = JSONObject.fromObject(context);
//        System.out.println(obj);
      
        int retcode = obj.getInt("retcode");
        if(retcode != 0) {
        	System.err.println("post_get_user_friends2 retcode:->" + retcode);
        	return;
        }
        
        JSONObject result = obj.getJSONObject("result");
        System.out.println("post_get_user_friends2:->" + result);
        
		
		
	}
	
	// 获取头像图片
	void get_face(long uin){
		String url = "http://face9.web.qq.com/cgi/svr/face/getface?cache=1&type=1&f=40&uin="+uin+"&t="+Tools.getCurrentTimeMillis()+"&vfwebqq=" + this.vfwebqq;
		
		HttpGet httpGet = new HttpGet(url);
		
		try {
			CloseableHttpResponse response1 = this.httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			
			File storeFile = new File("getface.jpeg");  
			FileOutputStream output = new FileOutputStream(storeFile);  
			HttpEntity entity1 = response1.getEntity();
	        //得到网络资源的字节数组,并写入文件  
//	        output.write(entity1);  
	        
			entity1.writeTo(output);
		    response1.close();
		    output.close(); 
		    
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	// 获取群列表
	void post_get_group_name_list_mask2(){
		String url = "http://s.web2.qq.com/api/get_group_name_list_mask2";
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Referer", referer);
		
	
		JSONObject obj_r = new JSONObject();
		obj_r.put("vfwebqq", this.vfwebqq);
		obj_r.put("hash", this.hash);
		
		// 创建参数队列    
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("r", obj_r.toString()));  
       
        
        String context = "";
        try {
        	UrlEncodedFormEntity uefEntity;  
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);  
			CloseableHttpResponse response = this.httpclient.execute(httpPost);
			
	       
	        HttpEntity entity = response.getEntity();  
	        context = EntityUtils.toString(entity);
	        response.close();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        JSONObject obj = JSONObject.fromObject(context);
//        System.out.println(obj);
      
        int retcode = obj.getInt("retcode");
        if(retcode != 0) {
        	System.err.println("post_get_group_name_list_mask2 retcode:->" + retcode);
        	return;
        }
        
        JSONObject result = obj.getJSONObject("result");
//        System.out.println("post_get_group_name_list_mask2:->" + result);
        // post_get_group_name_list_mask2:->{"gmasklist":[],"gnamelist":[{"flag":16778241,"name":"ITBirds","gid":947787014,"code":954375135}],"gmarklist":[]}
        
        JSONArray gnamelist = result.getJSONArray("gnamelist");
        for(int i = 0; i < gnamelist.size(); i++){
        	JSONObject groupObj = (JSONObject) gnamelist.get(i);
        	String name = groupObj.getString("name");
        	long flag = groupObj.getLong("flag");
        	long gid = groupObj.getLong("gid");
        	long code = groupObj.getLong("code");
        	System.out.println("post_get_group_name_list_mask2--> qun:" + i + " : name:->" + name + " , flag:->" + flag + " , gid:->" + gid + " , code:->" + code);
        	this.get_group_info_ext2(code);
        }
		
		
	}

	// 获取个人信息
	void get_self_info2(){
		String url = "http://s.web2.qq.com/api/get_self_info2?t=" + Tools.getCurrentTimeMillis();
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", referer);
		
		String context = "";
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 JSONObject obj = JSONObject.fromObject(context);
//       System.out.println(obj);
     
       int retcode = obj.getInt("retcode");
       if(retcode != 0) {
       	System.err.println("get_self_info2 retcode:->" + retcode);
       	return;
       }
       
       JSONObject result = obj.getJSONObject("result");
       System.out.println("get_self_info2:->" + result);
       
       // {"retcode":0,
       //"result":{
       		//"birthday":{"month":3,"year":1993,"day":2},
       		//"face":564,"phone":"","occupation":"","allow":1,"college":"学校","uin":3207662459,"blood":0,"constel":2,
       		//"lnick":"哈哈，我是个性签名！",
       		//"vfwebqq":"5255189690a7e4bd9c4678f20a59b8a1183d7b5435a65d843dd039bf5ed095499a54e9c60aecdd3a",
       		//"homepage":"","vip_info":0,"city":"朝阳","country":"中国","personal":"介绍","shengxiao":10,"nick":"测试QQ","email":"3207662459@qq.com",
       		//"province":"北京","account":3207662459,"gender":"male","mobile":""}}
       
       this.self_info = result;
       
       
       
       
	}
	
	// 获取好友的在线状态
	void get_online_buddies2(){
		String url = "http://d.web2.qq.com/channel/get_online_buddies2?vfwebqq="+this.vfwebqq+"&clientid="+this.clientid+"&psessionid="+this.psessionid+"&t=" + Tools.getCurrentTimeMillis();
		String referer = "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2";
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", referer);
		
		String context = "";
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			JSONObject obj = JSONObject.fromObject(context);
//	       System.out.println(obj);
	     
	       int retcode = obj.getInt("retcode");
	       if(retcode != 0) {
	       	System.err.println("get_online_buddies2 retcode:->" + retcode);
	       	return;
	       }
	       
	       JSONArray result = obj.getJSONArray("result");
	       System.out.println("get_online_buddies2:->" + result);
			
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		 
		
	}
	
	// unknown
	void post_get_recent_list2(){
		String url = "http://d.web2.qq.com/channel/get_recent_list2";
		String referer = "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2";

		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Referer", referer);
		
	
		JSONObject obj_r = new JSONObject();
		obj_r.put("vfwebqq", this.vfwebqq);
		obj_r.put("clientid", this.clientid);
		obj_r.put("psessionid", this.psessionid);
		
		// 创建参数队列    
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("r", obj_r.toString()));  
       
        
        String context = "";
        try {
        	UrlEncodedFormEntity uefEntity;  
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);  
			CloseableHttpResponse response = this.httpclient.execute(httpPost);
			
	       
	        HttpEntity entity = response.getEntity();  
	        context = EntityUtils.toString(entity);
	        response.close();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        JSONObject obj = JSONObject.fromObject(context);
//        System.out.println(obj);
      
        int retcode = obj.getInt("retcode");
        if(retcode != 0) {
        	System.err.println("post_get_recent_list2 retcode:->" + retcode);
        	return;
        }
        
        JSONArray result = obj.getJSONArray("result");
        System.out.println("post_get_recent_list2:->" + result);
		
	}
	
	// 在此处请求服务器，如果有消息，会在此处返回，
	void post_poll2(){
		System.out.println("post_poll2...");
		String url = "http://d.web2.qq.com/channel/poll2";
		String referer = "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2";
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Referer", referer);
		
	
		JSONObject obj_r = new JSONObject();
		obj_r.put("ptwebqq", this.ptwebqq);
		obj_r.put("clientid", this.clientid);
		obj_r.put("psessionid", this.psessionid);
		obj_r.put("key", "");
		
		// 创建参数队列    
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("r", obj_r.toString()));  
       
        
        String context = "";
        try {
        	UrlEncodedFormEntity uefEntity;  
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);  
			CloseableHttpResponse response = this.httpclient.execute(httpPost);
			
	       
	        HttpEntity entity = response.getEntity();  
	        context = EntityUtils.toString(entity);
	        response.close();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
//        System.out.println("post_poll2->Context:->" + context);
        try {
        	JSONObject obj = JSONObject.fromObject(context);
        	
//          System.out.println(obj);
            
            int retcode = obj.getInt("retcode");
            System.out.println("post_poll2:->retcode->" + retcode);
            if(retcode == 0){
            	 JSONArray result = obj.getJSONArray("result");
//            	 System.out.println("post_poll2:->" + result);
            	 
            	 this.do_poll2_result(result);

            }else if(retcode == 102){
            	String errmsg = obj.getString("errmsg");
            	System.out.println("post_poll2:->errmsg->" + errmsg);
            }else if(retcode == 103){
            	String errmsg = obj.getString("errmsg");
            	System.out.println("post_poll2:->errmsg->" + errmsg);
            }else if(retcode == 116){
            	
            }else{
            	
            }
            
		} catch (JSONException e) {
			// TODO: handle exception
			System.err.println("json Exception");
			e.printStackTrace();
			
		}
        

        
        
		
	}
	
	// ping hot
	void pingd(){
		String url = "http://pinghot.qq.com/pingd?dm=w.qq.com.hot&url=/&hottag=smartqq.im.polltimeout&hotx=9999&hoty=9999&rand=" + Tools.getRand(5);
		String referer = "http://w.qq.com/";
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", referer);
		
		
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
		    response1.close();
		    System.out.println("pingd...");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	void do_poll2_result(JSONArray result){
		
		for(int i = 0; i < result.size(); i++){
			JSONObject obj = result.getJSONObject(i);
			
			String poll_type = obj.getString("poll_type");
			JSONObject value = obj.getJSONObject("value");
			
			System.out.println("poll_type:->" + poll_type);
			
			if(poll_type.equalsIgnoreCase("message")){
				//{"poll_type":"message",
				//"value":{
					//"msg_id":32039,"from_uin":1476733029,"to_uin":3207662459,"msg_id2":636522,"msg_type":9,"reply_ip":178849361,"time":1436921804,
					//"content":[
						//["font",{"size":10,"color":"000000","style":[0,0,0],"name":"\u5B8B\u4F53"}],
						//"\u55E8 "
						//]
				//}}
				
				 long from_uin =  value.getLong("from_uin");
				 long to_uin = value.getLong("to_uin");
				 int msg_type = value.getInt("msg_type");
				 
				 JSONArray content = value.getJSONArray("content");
				 
				 String message = content.getString(1);
				 
				 String sendMsg = "message:->" + "from_uin:->" + from_uin + " , say:->" + message;
				 System.out.println(sendMsg);
				 
				 this.post_send_buddy_msg2(from_uin, message);
				 
				 
				 
				
			}else if(poll_type.equalsIgnoreCase("group_message")){
				
				//{"poll_type":"group_message",
				//"value":{
				//"msg_id":32051,"from_uin":947787014,"to_uin":3207662459,"msg_id2":143192,"msg_type":43,"reply_ip":176886360,
				//"group_code":954375135,"send_uin":1476733029,"seq":13248,"time":1436922723,"info_seq":111520689,
				//"content":[["font",{"size":10,"color":"000000","style":[0,0,0],"name":"\u5B8B\u4F53"}],"\u55E8 "]}}]}
				
				 long from_uin =  value.getLong("from_uin");
				 long to_uin =  value.getLong("to_uin");
				 int msg_type = value.getInt("msg_type");
				 long group_code = value.getLong("group_code");
				 long send_uin =  value.getLong("send_uin");
				 
				 JSONArray content = value.getJSONArray("content");
				 String message = content.getString(1);
				 
				 String sendMsg = "group_message:->" + "group_code:->" + group_code + " , from_uin:->" + from_uin + " , send_uin:->" + send_uin + " , say:->" + message;
				 System.out.println(sendMsg);
				 
				this.post_send_qun_msg2(from_uin, message);
				
				
			}else if(poll_type.equalsIgnoreCase("sess_message")){
//				{"retcode":0,"result":[{"poll_type":"sess_message",
//				"value":{
					//"msg_id":62166,"from_uin":414725931,"to_uin":3207662459,"msg_id2":919345,"msg_type":140,"reply_ip":178851470,"time":1436954955,
					//"id":2758433695,"ruin":33042624,"service_type":0,"flags":{"text":1,"pic":1,"file":1,"audio":1,"video":1},
					//"content":[["font",{"size":10,"color":"000000","style":[0,0,0],"name":"\u5B8B\u4F53"}],"2222 "]}}]}
				
				long from_uin =  value.getLong("from_uin");
				 long to_uin =  value.getLong("to_uin");
				 long id =  value.getLong("id");
				 JSONArray content = value.getJSONArray("content");
				 String message = content.getString(1);
				 
				 this.post_send_buddy_msg2(from_uin, message);

				
			}else if(poll_type.equalsIgnoreCase("buddies_status_change")){
				// {"retcode":0,"result":[{"poll_type":"buddies_status_change",
				//"value":{"uin":1476733029,"status":"offline","client_type":1}}]}
				
				//{"retcode":0,"result":[{"poll_type":"buddies_status_change",
				//"value":{"uin":1476733029,"status":"online","client_type":21}}]}
				System.out.println("buddies_status_change->" + obj);

			}else{
				System.out.println("unknown poll type : ->" + obj);
				

			}
			
			
			
		}// for
		
	}
	
	
	// 给好友发送消息
	void post_send_buddy_msg2(long to, String msg){
		String url = "http://d.web2.qq.com/channel/send_buddy_msg2";
		String referer = "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2";
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Referer", referer);
		
		// r:{"to":1476733029,"content":"[\"aaa\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]","face":564,"clientid":53999199,"msg_id":4710001,"psessionid":"8368046764001d636f6e6e7365727665725f77656271714031302e3133392e372e31363400000e5f00001847036e04007b0b31bf6d0000000a4063445538456c6b6b336d00000028489d3aa9513f4692ee55cb7b6fb73ef92c96e88d0e839d907979afbd1d11c689ca803ebbf1456630"}
//		JSONArray font_style = new JSONArray();
//		font_style.add(0);
//		font_style.add(0);
//		font_style.add(0);
//		JSONObject fontObj = new JSONObject();
//		fontObj.put("name", "宋体");
//		fontObj.put("size", 10);
//		fontObj.put("style", font_style);
//		fontObj.put("color", "000000");
//		JSONArray fontArr = new JSONArray();
//		fontArr.add("font");
//		fontArr.add(fontObj);
//		JSONArray content = new JSONArray();
//		content.add(msg);
//		content.add(fontArr);
		
		String content = "\"[\""+msg+"\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";
		
		
	
		int face = this.self_info.getInt("face");
		
		JSONObject obj_r = new JSONObject();
		obj_r.put("to", to);
//		obj_r.put("content", content.toString());
		obj_r.put("content", content);
		obj_r.put("face", face);
		obj_r.put("clientid", this.clientid);
		obj_r.put("msg_id", this.msg_id);
		obj_r.put("psessionid", this.psessionid);
		
//		System.out.println("post data:->" + obj_r);
		
//		System.out.println("post data to string:->" + obj_r.toString());
		
		
		// 创建参数队列    
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("r", obj_r.toString()));  
       
        System.out.println("params->" + params);
        
        String context = "";
        try {
        	UrlEncodedFormEntity uefEntity;  
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			
			System.out.println("uefEntity->" + uefEntity);
			
			httpPost.setEntity(uefEntity);  
			CloseableHttpResponse response = this.httpclient.execute(httpPost);
			
	       
	        HttpEntity entity = response.getEntity();  
	        context = EntityUtils.toString(entity);
	        response.close();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        JSONObject obj = JSONObject.fromObject(context);
        
        System.out.println("post_send_buddy_msg2:->" + obj);
        // {"retcode":0,"result":"ok"}
        //{"retcode":108,"errmsg":""}
      
        int retcode = obj.getInt("retcode");
        
        if(retcode == 0){
        	this.msg_id++;
        }else{
        	
        }
		
		
	}
	
	// 给群发送消息
	void post_send_qun_msg2(long group_uin, String msg){
		String url = "http://d.web2.qq.com/channel/send_qun_msg2";
		String referer = "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2";
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Referer", referer);
		
		// r:{"group_uin":947787014,"content":"[\"aaa\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]","face":564,"clientid":53999199,"msg_id":4710002,"psessionid":"8368046764001d636f6e6e7365727665725f77656271714031302e3133392e372e31363400000e5f00001847036e04007b0b31bf6d0000000a4063445538456c6b6b336d00000028489d3aa9513f4692ee55cb7b6fb73ef92c96e88d0e839d907979afbd1d11c689ca803ebbf1456630"}
		
//		JSONArray font_style = new JSONArray();
//		font_style.add(0);
//		font_style.add(0);
//		font_style.add(0);
//		JSONObject fontObj = new JSONObject();
//		fontObj.put("name", "宋体");
//		fontObj.put("size", 10);
//		fontObj.put("style", font_style);
//		fontObj.put("color", "000000");
//		JSONArray fontArr = new JSONArray();
//		fontArr.add("font");
//		fontArr.add(fontObj);
//		JSONArray content = new JSONArray();
//		content.add(msg);
//		content.add(fontArr);
		
		String content = "\"[\""+msg+"\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";
		
		int face = this.self_info.getInt("face");
		
		JSONObject obj_r = new JSONObject();
		obj_r.put("group_uin", group_uin);
		obj_r.put("content", content);
		obj_r.put("face", face);
		obj_r.put("clientid", this.clientid);
		obj_r.put("msg_id", this.msg_id);
		obj_r.put("psessionid", this.psessionid);
		
//		System.out.println("post data:->" + obj_r);
		
//		System.out.println("post data to string:->" + obj_r.toString());
		
		// 创建参数队列    
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("r", obj_r.toString()));  
       
        
        String context = "";
        try {
        	UrlEncodedFormEntity uefEntity;  
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);  
			CloseableHttpResponse response = this.httpclient.execute(httpPost);
			
	       
	        HttpEntity entity = response.getEntity();  
	        context = EntityUtils.toString(entity);
	        response.close();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        JSONObject obj = JSONObject.fromObject(context);
        System.out.println("post_send_qun_msg2:->" + obj);
        // {"retcode":0,"result":"ok"}
      
        int retcode = obj.getInt("retcode");
        
        if(retcode == 0){
        	this.msg_id++;
        }else{}
	}
	

	// 获取好友的个性签名
	void get_single_long_nick2(long tuin){
		String url = "http://s.web2.qq.com/api/get_single_long_nick2?tuin="+tuin+"&vfwebqq="+this.vfwebqq+"&t=" + Tools.getCurrentTimeMillis();
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", referer);
		
		String context = "";
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 JSONObject obj = JSONObject.fromObject(context);
//       System.out.println(obj);
     
       int retcode = obj.getInt("retcode");
       if(retcode != 0) {
       	System.err.println("get_single_long_nick2 retcode:->" + retcode);
       	return;
       }
       // {"retcode":0,"result":[{"uin":1476733029,"lnick":"http://blog.csdn.net/mingzznet"}]}
       JSONArray result = obj.getJSONArray("result");
       System.out.println("get_single_long_nick2:->" + result);
		
	}

	// 获取好友 群好友的 qq号
	void get_friend_uin2(long tuin){
		String url = "http://s.web2.qq.com/api/get_friend_uin2?tuin="+tuin+"&type=1&vfwebqq="+this.vfwebqq+"&t=" + Tools.getCurrentTimeMillis();
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", referer);
		
		String context = "";
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 JSONObject obj = JSONObject.fromObject(context);
//       System.out.println(obj);
     
       int retcode = obj.getInt("retcode");
       if(retcode != 0) {
       	System.err.println("get_friend_uin2 retcode:->" + retcode);
       	return;
       }
       // {"retcode":0,"result":{"uiuin":"","account":305603665,"uin":1476733029}}
       JSONObject result = obj.getJSONObject("result");
//       System.out.println("get_friend_uin2:->" + result);
       long account = result.getLong("account");
       System.out.println("get_friend_uin2--> QQ:->" + account);
		
	}
	
	// 获取好友、群好友 个人信息
	void get_friend_info2(long tuin){
		String url = "http://s.web2.qq.com/api/get_friend_info2?tuin="+tuin+"&vfwebqq="+this.vfwebqq+"&clientid="+this.clientid+"&psessionid="+this.psessionid+"&t=" + Tools.getCurrentTimeMillis();
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", referer);
		
		String context = "";
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 JSONObject obj = JSONObject.fromObject(context);
//       System.out.println(obj);
     
       int retcode = obj.getInt("retcode");
       if(retcode != 0) {
       	System.err.println("get_friend_info2 retcode:->" + retcode);
       	return;
       }
       // {"retcode":0,"result":{"face":0,"birthday":{"month":0,"year":0,"day":0},"occupation":"","phone":"","allow":0,"college":"","uin":1476733029,"constel":0,"blood":0,"homepage":"","stat":20,"vip_info":0,"country":"","city":"","personal":"","nick":"明子Jim","shengxiao":0,"email":"","province":"","gender":"unknown","mobile":"-"}}
       JSONObject result = obj.getJSONObject("result");
       System.out.println("get_friend_info2:->" + result);
		
	}

	// 获取群资料
	void get_group_info_ext2(long gcode){
		String url = "http://s.web2.qq.com/api/get_group_info_ext2?gcode="+gcode+"&vfwebqq="+this.vfwebqq+"&t=" + Tools.getCurrentTimeMillis();
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", referer);
		
		String context = "";
		try {
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
//			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
		    
		    context = EntityUtils.toString(entity1);
		    response1.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 JSONObject obj = JSONObject.fromObject(context);
//       System.out.println(obj);
     
       int retcode = obj.getInt("retcode");
       if(retcode != 0) {
       	System.err.println("get_group_info_ext2 retcode:->" + retcode);
       	return;
       }
       // {"retcode":0,
       //"result":{
       		//"stats":[{"client_type":41,"uin":3207662459,"stat":10}],
       		//"minfo":[
       			//{"nick":"明子Jim","province":"","gender":"unknown","uin":1476733029,"country":"","city":""},
       			//{"nick":"测试QQ","province":"北京","gender":"male","uin":3207662459,"country":"中国","city":"朝阳"}
       		//],
       		//"ginfo":{
       			//"face":0,"memo":"","class":10048,"fingermemo":"","code":954375135,"createtime":1423416889,
       			//"flag":16778241,"level":0,"name":"ITBirds","gid":947787014,"owner":1476733029,
       			//"members":[
       				//{"muin":1476733029,"mflag":0},
       				//{"muin":3207662459,"mflag":0}
       				//],
       			//"option":1},
       		//"cards":[{"muin":1476733029,"card":"改个群名片"}],
       		//"vipinfo":[{"vip_level":0,"u":1476733029,"is_vip":0},{"vip_level":0,"u":3207662459,"is_vip":0}]}}
       JSONObject result = obj.getJSONObject("result");
       System.out.println("get_group_info_ext2:->" + result);
       
       JSONArray stats = result.getJSONArray("stats");
       JSONArray minfo = result.getJSONArray("minfo");
       JSONObject ginfo = result.getJSONObject("ginfo");
       JSONArray cards = result.getJSONArray("cards");
       JSONArray vipinfo = result.getJSONArray("vipinfo");
       
       System.out.println("group members:");
       for(int i = 0; i < minfo.size(); i++){
    	   JSONObject minfoObj = (JSONObject) minfo.get(i); 
    	   String nick = minfoObj.getString("nick");
    	   String province = minfoObj.getString("province");
    	   String gender = minfoObj.getString("gender");
    	   long uin = minfoObj.getLong("uin");
    	   String country = minfoObj.getString("country");
    	   String city = minfoObj.getString("city");
    	   System.out.println("nick:->" + nick + " , province:->" + province + " , gender:->" + gender + " , uin:->" + uin + " , country:->" + country + " , city:->" + city);
    	   
    	   this.get_friend_uin2(uin);
    	   this.get_single_long_nick2(uin);
    	   this.get_friend_info2(uin);
    	  
    	   
       }
       
       
	}
	


}
