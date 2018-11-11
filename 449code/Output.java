import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Output {

    static String outputFile;
    static BufferedWriter writer;
    static boolean DEBUG = false;

    public static void setOutputFile(String outputFile) {
        Output.outputFile = outputFile;

        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            System.out.println("Error in instantiating BufferedWriter obj");
        }
    }

    public static void msg(String msg) {

        if (DEBUG) {
            System.out.print(outputFile + " - ");
            System.out.println(msg);
        }

        try {
            writer.write(msg);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    public static void display (int status) {

        if (status == -1) {
        	msg("No valid solution possible!");
        }
    }

    public static void display (ArrayList<Task> arr, int minPenalty) {

    	String output = "Solution ";
        for (int i = 0; i < arr.size(); ++i) {
        	output += arr.get(i);
        	output += " ";
        }
        
        output = output.substring(0, output.length()-1);
        output += "; Quality: ";
        output += Integer.toString(minPenalty);
        
        msg(output);
    }
    
//    public static void main(String[] args) {
//
//        System.out.println(args.length);
//
//        Output.setOutputFile("jjm");
//
//    	display(-1);
//
//    	ArrayList<Task> arr = new ArrayList<Task>();
//    	arr.add(Task.getTask(0));
//    	arr.add(Task.getTask(1));
//    	arr.add(Task.getTask(2));
//    	arr.add(Task.getTask(3));
//    	arr.add(Task.getTask(4));
//    	arr.add(Task.getTask(5));
//    	arr.add(Task.getTask(6));
//    	arr.add(Task.getTask(7));
//
//        // display(arr, 10);
//    }
}
