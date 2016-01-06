/**######################################################################################################################
 * DroFFS (Droid Files Fuzzing System) for Android
 * The Automatic Log Checker (ALoC) provides an automatic summary of log entries resulted from fuzzing process
 * @author Dr Ilung Pranata of The University of Newcastle, Australia
 * (c) 2016
 * 
 * License: MIT License
 * ######################################################################################################################
 */


package Main;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public class logChecker {

	public static void startALoC(){
		try{
			//get the current directory of this program
			String currentJavaDir =  System.getProperty("user.dir");
			
			//get a list of Application Under Test (AUTs) from AppsUnderTest directory
			File[] listOfLogs =  new File(currentJavaDir + "/logs").listFiles();
			Charset charset = Charset.forName("ISO-8859-1");
			
			if(listOfLogs.length > 0){
				for(File log : listOfLogs){
					//read all lines/entries from the log
					Path wiki_path = Paths.get(log.getAbsolutePath());
					List<String> lines = Files.readAllLines(wiki_path, charset);
					
					//remove exclusions from log entries
					lines = removeExclusion(lines);
					
					//find unique and fatal errors
					List<String> uniqueErrors = getUniqueErrors(lines);
					String strUniqueErrors = "";
					String strFatalErrors = "";
					int totalFatalErrors = 0;;
					for(String error : uniqueErrors){
						strUniqueErrors += error+", ";
						
						//get Fatal errors
						if(error.indexOf("F/")!= -1){
							strFatalErrors +=error+", ";
							totalFatalErrors++;
						}
					}
					
					//Print all unique and fatal errors
					System.out.println("##### Log Name : "+log.getName());
					System.out.println("Total Unique Errors : "+uniqueErrors.size());
					System.out.println("Types of Unique Errors: "+ strUniqueErrors);
					System.out.println("Total FATAL Errors : "+totalFatalErrors);
					System.out.println("Types of FATAL Errors: "+ strFatalErrors);
				}
				
			}
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
	}
	
	/**
	 * Remove exclusions from log entries
	 * @param lines
	 * @return log entries free from exclusions
	 */
	private static List<String> removeExclusion(List<String> lines){
		// use iterator to remove exclusions in the log entries
		Iterator<String> i = lines.iterator();
		try{
			while(i.hasNext()){
				String line = i.next();
				for(String pattern : global.exclusions){
					if(line.indexOf(pattern) != -1){
						i.remove();
					}
				}
			}
			
			return lines;
		}catch(Exception ex){
			System.out.println(ex.toString());
			return null;
		}
	}
	
	/**
	 * Find unique errors from a 'clean' log file.
	 * @param lines
	 * @return unique errors
	 */
	private static List<String> getUniqueErrors(List<String> lines){
		List<String> uniqueErrors = new ArrayList<String>();
		for(String line : lines){
			//find the open parentheses in the line. Get the error type from characters before this parentheses
			//An example of log format used in Android 5.x logcat is 'E/memtrack(273)'
			int index = line.indexOf("(");
			if(index != -1){
				String error = line.substring(0,index);
				if(!uniqueErrors.contains(error))
					uniqueErrors.add(error);
			}
		}
		
		return uniqueErrors;
		
	}
}
