

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


    // return TRUE if output file exists...
    public boolean setOutputFilePath(String outputFilePath) {
        _outputFilePath = outputFilePath;

        try {
            writer = new BufferedWriter(new FileWriter(_outputFilePath));
        } catch (IOException e) {
            System.out.println("ERROR: The specified output file was not found/created.");
            return false;
        }

        return true;

    }


    // methods...

    

    public void outputValidSolution() { // write alphabetical listing + eval...

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
        

        try {
            writer.close();
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to close output file");
        }



    }

    public void outputNoValidSolution() { // write NO VALID SOLUTION to file
        
        try {
            writer.write("NO VALID SOLUTION\n");  
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to write to output file");
        }

        try {
            writer.close();
        }
        catch (IOException e) {
            System.out.println("ERROR: failed to close output file");
        }


    }


    
}