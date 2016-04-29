package com.mingz;

import java.util.Random;

public class Tools {
	
	// 获取n位随机数
	static long getRand(int n){
		String result = "";
		Random random = new Random();
		for(int i = 0; i < n; i++){
//		    System.out.println(random.nextInt());
		    result += random.nextInt(9);
		}
		return Long.parseLong(result);
		
	}
	
	// 获取两数之间随机数[min, max]
	static int getRand(int min, int max){
		return (int) Math.round(Math.random() * (max - min) + min);
	}
	
	
	// 作用：从前往后查找left，从后往前查找right，返回两文本中间字串
	static String getMidOfTwoText(String context, String left, String right){
	  
	    return context.substring(
	                context.indexOf(left) + left.length(),
	                context.lastIndexOf(right)
	                );

	}
	
	static String getCurrentTimeMillis(){
		return Long.toString(System.currentTimeMillis());
	}
	
	
	
	
}
