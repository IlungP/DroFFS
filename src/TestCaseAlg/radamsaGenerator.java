package TestCaseAlg;

import java.io.File;
import Main.global;

public class radamsaGenerator implements ITestCasesGenerator {
	
	//number of mutated test cases per source file
	private int noOfMutatedTestCases;
	
	
	public radamsaGenerator(int noOfMutatedTestCasesPerSourceFile)
	{
		System.out.println("Creating test cases from source files...");
		noOfMutatedTestCases  = noOfMutatedTestCasesPerSourceFile;
	}
	
	@Override
	public String getGeneratorName() {
		// TODO Auto-generated method stub
		return "radamsa";
	}

	@Override
	public boolean generateTestCases() {
		// TODO Auto-generated method stub
		try{
			//get the current directory of this java program
			String currentJavaDir =  System.getProperty("user.dir");
			
			File folder = new File(currentJavaDir + sourceFilesPath);
			File[] listOfSourceFiles = folder.listFiles();
			
			if(listOfSourceFiles.length > 0){
				for(File sourceFile : listOfSourceFiles)
				{
					//call radamsa python script to create test cases
					//radamsa/radamsa -o fuzzingFiles/sample_student_essays-%n.pdf -n 30 SourceFiles/sample_student_essays.pdf
					global.execADBCommand("src/TestCaseAlg/radamsaAlg/radamsa -o TestCases/" + sourceFile.getName() + "-%n." + 
							sourceFile.getPath() + "-n" + noOfMutatedTestCases + sourceFilesPath);
				}
				return true;
			}else{
				
				return false;
			}
		}catch(Exception ex){
			System.out.println("Error: TestCaseAlg/generateTestCases() - " + ex.toString());
			return false;
		}
	}

	@Override
	public void filesMutationAlg() {
		// TODO Auto-generated method stub

	}

}
