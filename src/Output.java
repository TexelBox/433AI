

import java.io.*;

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


    // return TRUE if file exists...
    public boolean setOutputFilePath(String outputFilePath) {
        _outputFilePath = outputFilePath;

        try {
            writer = new BufferedWriter(new FileWriter(_outputFilePath));
        } catch (IOException e) {
            System.out.println("ERROR: output file doesn't exist");
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

            try {
                writer.write(outLine + "\n");
            }
            catch (IOException e) {
                System.out.println("ERROR: failed to write to output file");
            }
        }





    }

    public void outputNoValidSolution() { // write NO VALID SOLUTION to file
        
    }



    
}