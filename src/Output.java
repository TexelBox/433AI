

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Output {

    // fields...

    // singleton design pattern:

    private static Output instance = null;

    private Output() {
        // init...
    }

    public static Output getInstance() {
        if (instance == null) {
            instance = new Output();
        }
        return instance;
    }

    public AndTree _tree;

    public String _outputFilePath;

    private BufferedWriter writer;

    private BufferedWriter logwriter;


    // methods...


    public boolean initOutputWriter(String outputFilePath) {
        _outputFilePath = outputFilePath;
        boolean isOpen = openOutputFile();
        if (!isOpen) {
            return false;
        }
        boolean isClosed = closeOutputFile();
        return isClosed;
    }



    public boolean initLogWriter() {

        try {
            logwriter = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            System.out.println("ERROR: log.txt could not be created.");
            return false;
        }

        return true;
    }



    public boolean openOutputFile() {
        try {
            writer = new BufferedWriter(new FileWriter(_outputFilePath));
        } catch (IOException e) {
            System.out.println("ERROR: The specified output file was not found/created.");
            return false;
        }
        return true;
    }





    public boolean closeOutputFile() {
        try {
            writer.close();
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to close output file");
            return false;
        }
        return true;
    }

    public boolean closeLogFile() {
        try {
            logwriter.close();
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to close log file");
            return false;
        }
        return true;
    }



    // DEBUG (or for huge files)....
    public void logValidSolution() {
        try {
            logwriter.write("===============================================\n");
            logwriter.write("passed time: " + Long.toString(System.currentTimeMillis() - Algorithm._starttime) + "\n");
            logwriter.write("Eval-value: " + Double.toString(_tree._bestEval) + "\n");  
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to write to output file");
        }

        Slot[] bestAssign = _tree._bestAssign;
        
        List<String> outList = new ArrayList<String>();

        for (int i = 0; i < bestAssign.length; i++) {
            Course nextCourse = Input.getInstance()._courseList.get(i);
            String nextCourseOutputID = nextCourse._outputID;
            String nextSlotOutputID = bestAssign[i]._outputID;

            String spacing;
            if (nextCourse._secondaryType == Course.SecondaryType.NONE) {
                spacing = "             : ";
            }
            else {
                spacing = "      : ";
            }

            String outLine = nextCourseOutputID + spacing + nextSlotOutputID;
            
            outList.add(outLine);

        }
        
        Collections.sort(outList);
        
        
        try {
        	for (String str : outList) {
        		logwriter.write(str + "\n");
            }
            logwriter.flush(); // write to file NOW
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to write to output file");
        }
    }
    
    
    
    // DEBUG....
    public void printValidSolution() {
    	System.out.println("===============================================");
    	System.out.println("passed time: " + Long.toString(System.currentTimeMillis() - Algorithm._starttime));
    	System.out.println("Eval-value: " + Double.toString(_tree._bestEval));
    	
    	Slot[] bestAssign = _tree._bestAssign;
        
        List<String> outList = new ArrayList<String>();

        for (int i = 0; i < bestAssign.length; i++) {
            Course nextCourse = Input.getInstance()._courseList.get(i);
            String nextCourseOutputID = nextCourse._outputID;
            String nextSlotOutputID = bestAssign[i]._outputID;

            String spacing;
            if (nextCourse._secondaryType == Course.SecondaryType.NONE) {
                spacing = "             : ";
            }
            else {
                spacing = "      : ";
            }

            String outLine = nextCourseOutputID + spacing + nextSlotOutputID;
            
            outList.add(outLine);

        }
        
        Collections.sort(outList);
        
        
       
    	for (String str : outList) {
    		System.out.println(str);
    	}
        
        
    }



    // overwrite file contents
    public void outputValidSolution() { // write alphabetical listing + eval...

        boolean isOpen = openOutputFile();
        if (!isOpen) {
            return;
        }

        try {
            writer.write("Eval-value: " + Double.toString(_tree._bestEval) + "\n");  
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to write to output file");
        }

        Slot[] bestAssign = _tree._bestAssign;
        
        List<String> outList = new ArrayList<String>();

        for (int i = 0; i < bestAssign.length; i++) {
            Course nextCourse = Input.getInstance()._courseList.get(i);
            String nextCourseOutputID = nextCourse._outputID;
            String nextSlotOutputID = bestAssign[i]._outputID;

            String spacing;
            if (nextCourse._secondaryType == Course.SecondaryType.NONE) {
                spacing = "             : ";
            }
            else {
                spacing = "      : ";
            }

            String outLine = nextCourseOutputID + spacing + nextSlotOutputID;
            
            outList.add(outLine);

        }
        
        Collections.sort(outList);
        
        try {
        	for (String str : outList) {
        		writer.write(str + "\n");
        	}
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to write to output file");
        }

        boolean isClosed = closeOutputFile();
        if (!isClosed) {
            return;
        }

    }

    public void outputNoValidSolution() { // write NO VALID SOLUTION to file

        boolean isOpen = openOutputFile();
        if (!isOpen) {
            return;
        }
        
        try {
            writer.write("NO VALID SOLUTION\n");  
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to write to output file");
        }

        boolean isClosed = closeOutputFile();
        if (!isClosed) {
            return;
        }

    }


    public void outputNoSolutionFoundYet() { // if we happen to timeout without finding a valid sol...

        boolean isOpen = openOutputFile();
        if (!isOpen) {
            return;
        }
        
        try {
            writer.write("NO VALID SOLUTION FOUND WITHIN SPECIFIED RUNTIME\n");  
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to write to output file");
        }

        boolean isClosed = closeOutputFile();
        if (!isClosed) {
            return;
        }

    }




    
}