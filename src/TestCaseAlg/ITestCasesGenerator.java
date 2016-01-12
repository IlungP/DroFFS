/**######################################################################################################################
 * DroFFS (Droid Files Fuzzing System) for Android
 * Interface class for fuzzer mutation algorithms. Implement your files mutation algorithms to generate test cases
 * from source files.
 * @author Dr Ilung Pranata of The University of Newcastle, Australia
 * (c) 2016
 * 
 * License: MIT License
 * ######################################################################################################################
 */

package TestCaseAlg;

public interface ITestCasesGenerator {

	//path to source files
	String sourceFilesPath = "/SourceFiles";
	

	/**
	 * interface method to return generator name
	 */
	public String getGeneratorName();
	
	
	/**
	 * interface method to generate test cases
	 */
	public boolean generateTestCases();
	
	
	/**
	 * interface method to implement files mutation algorithm
	 */
	public void filesMutationAlg();
	
}
