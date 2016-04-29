package com.mingz;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println("hello world");
		
		try {
			Main.testWebQQ();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void testWebQQ() throws IOException{
		WebQQClient webQQClient = new WebQQClient("3207662459", "xiaojing123qwe");
//		webQQClient.getTestUrl("http://www.baidu.com");
		boolean need_img = webQQClient.get_check();
		
		if(need_img){
			
			webQQClient.get_image();
			
			boolean isLoop = false;
			String code = "";
			do{
				System.out.println("验证码图片保存路径：" + " ./getimage.jpeg");
				System.out.println("请输入验证码：");
				
				code = "";
				int i = 4;
				while(i--!=0){
					code += (char)System.in.read();
				}
				
				System.out.println("输入的验证码：" + code);
				System.out.println("是否重新输入？（Y/N）:");
				char isAgain = (char)System.in.read();
				
				if(isAgain == 'Y' || isAgain == 'y'){
					isLoop = true;
				}
			
			}while(isLoop);
			
			webQQClient.setVerifycode(code);
			
		}
		
		webQQClient.get_login();
		webQQClient.get_check_sig();
//		webQQClient.get_getvfwebqq();
		webQQClient.post_login2();
		webQQClient.post_get_user_friends2();
		webQQClient.post_get_group_name_list_mask2();
		webQQClient.get_self_info2();
		webQQClient.get_online_buddies2();
		webQQClient.post_get_recent_list2();
		
		(new Timer()).schedule(new TimerTask(){
					
					public void run(){
						webQQClient.pingd();
					}
					
				}, 1 * 1000, 5 * 1000);//五秒  
		
		
		  
		(new Timer()).schedule(new TimerTask(){
			
			public void run(){
				webQQClient.post_poll2();
			}
			
		}, 1 * 1000, 5 * 1000);//五秒  
		
		
		
//		(new Timer()).schedule(new TimerTask() {	
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//			}
//		}, 1 * 1000, 5 * 1000);
		
		
		
		
		
	}
	
	
	

}
