/**######################################################################################################################
 * DroFFS (Droid Files Fuzzing System) for Android
 * Performs logging for each fuzzing cycle.
 * @author Dr Ilung Pranata of The University of Newcastle, Australia
 * (c) 2016
 * 
 * License: MIT License
 * ######################################################################################################################
 */

package FuzzEngine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Main.global;

import java.io.*;


public class logging {

	private static String command = "";
	private static String response = "";
	
	public static void logError(File testcase, String AUTName, int batchNo){
		try{
			//log all Error (E/) & Fatal (F/) messages from logcat
			command = "adb logcat *:E";
			response = execADBForLogging(command);
			
			//Save a log file in the 'logs' folder
			String logName = "logs/log_" + AUTName + ".txt";
			File f = new File (logName);
			PrintWriter writer = null;
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			
			//if current log exists, append the log
			if(f.exists() && !f.isDirectory()){
				writer = new PrintWriter(new FileOutputStream(new File(logName), true));		
			}else{
				//create a new log
				writer = new PrintWriter(logName);
			}
			
			writer.println("#################################################################################");
			writer.println(">>> " + dateFormat.format(date) + "; Test_case: " + testcase.getName() + "; File_size: " + testcase.length() +
					"; AUT: "+ AUTName + "; Batch_no: " + batchNo);
			writer.println(response);
			writer.close();
			
			checkFatalError(testcase.getName(), date, response);
			
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
	}
	
	/**
	 * Checks for any Fatal ("F/") error in the log file. If found, get the tombstone log from Android. 
	 * 
	 * @param date
	 * @param response
	 */
	private static void checkFatalError(String caseName, Date date, String response)
	{
		try{
			if(response.indexOf("F/") != -1){
				DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd'T'HHmm");
				
				//if there is Fatal error, pull the tombstone
				command = "adb pull /data/tombstones/tombstone_00 ./tombstones";
				response = global.execLongADBCommand(command);
				
				File file = new File("./tombstones/tombstone_00");
				File newFile = new File("./tombstones/tombstone_"+ caseName + "_"+ dateFormat.format(date));
				
				if(file.exists()){
					file.renameTo(newFile);
				}
			}
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
	}
	
	/**
	 * Executes ADB command on Android AVD/Device and return its messages or responses
	 * 
	 * @param command ADB command to run on Android AVD/Device
	 * @return responses from Android AVD/Device
	 */
	@SuppressWarnings("finally")
	private static String execADBForLogging(String command){
		String response = "";
		try{
			String line = "";
			Runtime runtime = Runtime.getRuntime();
			Process pr = runtime.exec(command);
	        
			Thread.sleep(3000);
	        pr.destroy();
	
	        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	       
	        while ((line = buf.readLine()) != null) {
	        	if(!line.isEmpty()){
	        		response += line+"\n";
	        	}
	        }
	        
	    }catch(Exception ex){
	    	System.out.println(ex.toString());
	    }finally{
	    	return response;
	    }
	}
	
}
