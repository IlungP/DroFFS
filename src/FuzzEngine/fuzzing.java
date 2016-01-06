/**######################################################################################################################
 * DroFFS (Droid Files Fuzzing System) for Android
 * Performs files fuzzing including test batches creation & transfer, AUT installations, and the actual fuzzing process
 * @author Dr Ilung Pranata of The University of Newcastle, Australia
 * (c) 2016
 * 
 * License: MIT License
 * ######################################################################################################################
 */

package FuzzEngine;

import Main.global;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class fuzzing {
	private static String command = "";
	private static String response = "";
	private static File[] listOfTestCases;
	private static File[] listOfAUT;
	private static String currentJavaDir;
	private static int noOfBatches = 1;
	
	public static void startFuzzing(String type){
		try{
			//get the current directory of this java program
			currentJavaDir =  System.getProperty("user.dir");
			
			//create test batches from all test cases in temp folder
			createTestBatches();
			
			//get a list of Application Under Test (AUTs) from AppsUnderTest directory
			listOfAUT =  new File(currentJavaDir + "/AppsUnderTest").listFiles();
			
			if(listOfAUT.length > 0){
				//for each AUT, install AUT on an Android emulator/device and run fuzzing test
				for(File aut : listOfAUT){
					// installing an AUT
					System.out.println("Installing AUT: " + aut.getCanonicalPath());
					command = "adb install " + aut.getCanonicalPath();
					response = global.execLongADBCommand(command);
					System.out.println(response);
					
					//get package name
					command = "aapt dump badging " + aut.getCanonicalPath();
					response = global.execLongADBCommand(command);
					int startIndex = response.indexOf("package: name='") + 15;
					String packageName = response.substring(startIndex, response.indexOf("' versionCode"));
					// TO BE FIXED --- String packageName = "com.xodo.pdf.reader";
					
					//run fuzzing
					runFuzzing(type, packageName);
					
					//stop running AUT in Android device or emulator
					command = "adb shell pm clear " + packageName;
					response = global.execADBCommand(command);
					
					// uninstalling the AUT
					System.out.println("Uninstalling AUT and starting next AUT Fuzzing");
					command = "adb uninstall " + packageName;
					response = global.execADBCommand(command);
				}
			}else{
				//run fuzzing
				runFuzzing(type, "");
			}
			
			System.out.println("######### Fuzzing process is finished.");
			
		}catch(Exception ex){
			System.out.println("fuzzing/startFuzzing(): "+ex.toString());
		}
	}
	
	/**
	 * Creates test batches that contains a specific number of test cases as defined 
	 * in noOfTestCasesInBatch configuration. It takes all test cases in temp folder.
	 */
	public static void createTestBatches()
	{
		try{
			System.out.println("Automating test batches creation ...");
			File folder = new File(currentJavaDir + "/temp");
			listOfTestCases = folder.listFiles();
			
			if(listOfTestCases != null && listOfTestCases.length > 0){
				//find out the number of test batches need to be created
				noOfBatches = listOfTestCases.length / global.noOfTestCasesInBatch;
				int modulus = listOfTestCases.length % global.noOfTestCasesInBatch;
				
				if(modulus > 0)
					noOfBatches++;
				
				//create batches based on the previously computed number of test batches
				boolean success;
				int counter = 0;
				CopyOption[] options = new CopyOption[]{ //options for copying files
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
				};
				
				for(int i = 0; i< noOfBatches; i++)
				{
					//create directory to store test cases in a batch
					success = (new File(currentJavaDir+"/TestCases/TestBatch_"+i)).mkdirs();
					if(success){
						int loopCounter = 0; // this loop counter makes sure that the total test cases in a batch does not exceed
						while(counter < listOfTestCases.length && loopCounter < global.noOfTestCasesInBatch){
							Path source = Paths.get(listOfTestCases[counter].getAbsolutePath());
							Path target = Paths.get(currentJavaDir+"/TestCases/TestBatch_"+i+"/"+listOfTestCases[counter].getName());
							Files.copy(source, target, options);
							counter++;
							loopCounter++;
						}// end inner for
					}// end if
				}
			}
			System.out.println("Test batches creation complete with "+ noOfBatches + " batches created.");
		}catch(Exception ex){
			System.out.println("Error (fuzzing/createTestBatches): "+ex.toString());
		}
	}
	
	/**
	 * Perform fuzzing test in Android device/emulator with all created test batches 
	 * 
	 * @param type file-type of test cases used in fuzzing (i.e. application/pdf, jpeg, gif, etc.)
	 * @param packageName Name of AUT used in fuzzing
	 */
	private static void runFuzzing(String type, String packageName){
		try{
			for(int i = 0;i < noOfBatches;i++){
				//transferring current test batch to Android device/emulator
				boolean isContinue = transferTestBatch(i);
				
				if(isContinue){
					//fuzz an AUT with all test cases transferred earlier
					for(File testcase : listOfTestCases){
						System.out.println("Fuzzing testcase: " + testcase.getName());
			            //clear Android's device or emulator log to fuzz with the test case
			            command = "adb logcat -c";
			            response = global.execADBCommand(command);
			            
			            //clear Android's tombstone log
			            command = "adb shell rm /data/tombstones/*";
			            response = global.execADBCommand(command);
			            
						//issue intent command to open this test case with an AUT
						command = "adb shell am start -a android.intent.action.VIEW -d file://"
								+ global.workingDir + "/AppsFuzzer/TestCases/" + testcase.getName()
								+ " -t " + type;
						response = global.execADBCommand(command);
						
						//sleep this program for 25 seconds to give time for AUT to open the file
						Thread.sleep(25000); //25000
						
						if(!packageName.isEmpty()){
							//stop the running AUT in Android device/emulator
							command = "adb shell pm clear " + packageName;
							response = global.execADBCommand(command);
							//log errors for this test
							logging.logError(testcase, packageName, i);
						}else{
							//log errors for this test
							logging.logError(testcase, "default_app", i);
						}
					}// end inner for
					
					//clear any previous test cases stored in Android device/emulator
					command = "adb shell rm -r "+ global.workingDir + "/AppsFuzzer/TestCases";
					response = global.execADBCommand(command);
				}//end if
				
				//sleep this program for 90 seconds to give time for AUT to open the file
				Thread.sleep(2000); //220000
				
			}// end outer for
		}catch(Exception ex){
			System.out.println("Error (fuzzing/runFuzzing): "+ex.toString());
		}
	}
	
	/**
	 * First check if there are test cases in ../TestCases directory
	 * Transfer all test cases to an Android emulator or device
	 * 
	 * @param batchNo the batch number for current test
	 */
	private static boolean transferTestBatch(int batchNo){
		try{
			//check for the availability of test cases and store their file names
			String testBatchLoc = currentJavaDir + "/TestCases/TestBatch_" + batchNo;
			File folder = new File(testBatchLoc);
			listOfTestCases = folder.listFiles();
			
			if(listOfTestCases.length > 0){
				System.out.println("Transferring test cases ... \nThis process may take some time depending on the size of files. "+
						"Please wait for the transfer to finish.");
				
				//push all files in the TestCases directory to the Android device or emulator
				command = "adb push " + testBatchLoc + " " + global.workingDir + "/AppsFuzzer/TestCases";
				
				//get the response back from this process and write them
				response = global.execLongADBCommand(command);
				System.out.println(response);
				
				return true;
			}else{
				System.out.println("No test cases found. Please create some test cases.");
				return false;
			}
			
		}catch(Exception ex){
			System.out.println("Error (fuzzing/transferTestBatch): " + ex.toString());
			return false;
		}
	}
}
