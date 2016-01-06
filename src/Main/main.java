/**######################################################################################################################
 * DroFFS (Droid Files Fuzzing System) for Android
 * the main class for starting Android Apps Fuzzer. This class prepares the environment for fuzzing Android Apps.
 * It checks if an Android device or emulator is connected and create the necessary working directory.
 * @author Dr Ilung Pranata of The University of Newcastle, Australia
 * (c) 2016
 * 
 * License: MIT License
 * ######################################################################################################################
 */


package Main;

import java.util.Timer;
import FuzzEngine.fuzzing;

public class main {

	private static String command = "";
	private static String response = "";

	public static void main(String[] args) {
		//preparing environment for fuzzing process
		prepareEnvironment();
		
		//start fuzzing process
		fuzzing.startFuzzing(global.fuzzFileFormat);
		
		//call Automatic Log Checker (ALoC) to provide summary of errors found
		logChecker.startALoC();
	}
	
	
	/**
	 * Prepares an Android emulator/device environment for fuzzing process.
	 * Various checks such as checking for the availability of an Android emulator/device, 
	 * checking for the existence of working directory, etc. are performed.
	 */
	private static void prepareEnvironment(){
		boolean checkDevice = checkAndroidDevice(); //check if an Android AVD/device is connected
		if(checkDevice){
			createWorkingDir(); //check and create a working directory
		}
	}
	
	/**
	 * Starts ADB server and checks whether an Android emulator or device is connected. 
	 * It also checks if multiple devices are connected.
	 * 
	 * @return true/false true if only one device or emulator is connected
	 */
	private static boolean checkAndroidDevice(){
		try{
			//first start the ADB server
			System.out.println("Starting ADB daemon ...");
			command = "adb start-server";
			response = global.execADBCommand(command);
			
			
			//checks if an Android Device or AVD is connected
			System.out.println("Checking connected devices ...");
			command = "adb devices";
			response = global.execADBCommand(command);
			
			//get the number of devices connected to the system
			String[] devices = response.split(";");
			
			if(devices.length < 2){ //the first connected device is always located in second line of response
				System.out.println("No Android emulator or device is connected.");
				return false;
			}else if(devices.length > 2){
				System.out.println("More than one Android emulator and/or device are connected. This application can only support one connected instance. " + response);
				return false;
			}else{
				return true;
			}
			
		}catch(Exception ex){
			return false;
		}
	}
	
	/**
	 * Checks for existing working directory (i.e. AppsFuzzer)in connected Android emulator or device. 
	 * If a working directory is not found, create one in the device.
	 */
	private static void createWorkingDir(){
		try{
			System.out.println("Creating a working directory ...");
			
			//set command for checking whether an existing working directory is found
			command = "adb shell ls " + global.workingDir + " | grep -c \"AppsFuzzer\"";
			
			//check if working directory exists
			response = global.execADBCommand(command);
			//get the first line of the response
			int intResponse = Integer.parseInt(response.substring(0, response.indexOf(";")));
			
			if(intResponse == 0){
				//create a working directory if it does not exist
				command = "adb shell mkdir "+ global.workingDir + "/AppsFuzzer";
				response = global.execADBCommand(command);
				System.out.println("A working directory (i.e. AppsFuzzer) is successfully created in "+global.workingDir);
			}else{
				System.out.println("A working directory (i.e. AppsFuzzer) already exists in "+global.workingDir);
			}
			
		}catch (Exception ex){
			System.out.println(ex.toString());
		}
	}
	
}
