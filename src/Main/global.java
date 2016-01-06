/**######################################################################################################################
 * DroFFS (Droid Files Fuzzing System) for Android
 * Global Configuration/Setup File to be used by DroFFS. The variables in "SETUP CONFIGURATION" needs to be specified
 * prior to running this software. 
 * @author Dr Ilung Pranata of The University of Newcastle, Australia
 * (c) 2016
 * 
 * License: MIT License
 * ######################################################################################################################
 */

package Main;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class global {
	
	/**
	 * DroFFS SETUP CONFIGURATION - PLEASE SETUP THE ANDROID WORKING DIRECTORY, FILE FORMAT FOR FUZZING,
	 * NO OF TEST CASES IN A BATCH AND LOG ENTRIES EXCLUSIONS FOR ALOC
	 */
	//Specify the accessible working directory of your Android Device/Emulator (with read & write permissions)
	//working directory will be used to store test batches during fuzzing process
	public static String workingDir = "/storage/sdcard"; // for Android 5.1.1 emulator
	//public static String workingDir = "/storage/extSdCard"; //for Samsung Galaxy Note 4 with external SDCard
	
	//Specify the format/type of file used for fuzzing process
	//see fileFormat.java for full list of file formats supported by DroFFS
	public static String fuzzFileFormat = fileFormat.pdf;
	
	//Specify the number of test cases for each test batch 
	public static int noOfTestCasesInBatch = 50;
	
	//Specify the list of exclusions for log entries - to be used in ALoC for log summary
	public static String[] exclusions = {"E/SQLiteLog", "E/copresGcore"};
		
	
	//###############################################################################################################//
	//########################################## GLOBAL METHODS #####################################################//
	
	/**
	 * Executes ADB command on Android AVD/Device and return its messages or responses.
	 * It waits for the process to finish executing. If the input/output is large than
	 * the process cache, this method will not return and hangs.
	 * 
	 * @param command ADB command to run on Android AVD/Device
	 * @return responses from Android AVD/Device
	 */
	@SuppressWarnings("finally")
	public static String execADBCommand(String command){
		String response = "";
		try{
			String line = "";
			Runtime runtime = Runtime.getRuntime();
			Process pr = runtime.exec(command);
	        
	        pr.waitFor(); //wait for process to finish
	
	        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	       
	        while ((line = buf.readLine()) != null) {
	        	response += line+";";
	        }
	        
	    }catch(Exception ex){
	    	System.out.println(ex.toString());
	    }finally{
	    	return response;
	    }
	
	}
	
	/**
	 * This method is a modified version from execADBCommand() as such this method
	 * implements StreamGobbler class to flush process cache. 
	 * @param command
	 * @return
	 */
	@SuppressWarnings("finally")
	public static String execLongADBCommand(String command){
		String response = "";
		try{
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(command);
			
			// all input messages
            StreamGobbler inputGobbler = new 
                StreamGobbler(proc.getErrorStream(), "INPUT");            
            
            // any output message?
            StreamGobbler outputGobbler = new 
                StreamGobbler(proc.getInputStream(), "OUTPUT");

            // kick them off to separate threads
            inputGobbler.start();
            outputGobbler.start();
                                    
            // show final message
            int exitVal = proc.waitFor();
            response = "Process is finished";        
	        
	    }catch(Exception ex){
	    	response = "Error."; 
	    	System.out.println(ex.toString());
	    }finally{
	    	return response;
	    }
	
	}
}
