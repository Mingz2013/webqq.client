package com.mingz;

public class Log {
	Log(String fileName){
		
	}
	
	
	public static void log(Object... args) {  
		String str = "";
        for (Object arg : args) {  
            str += arg;
        }
        
        System.out.println(str);
    }  
	
	
	
}
