


public class Main {

    public static void main(String[] args) {

        if (args.length == 2) { // change this later when more command-line args need to be specified (like soft constraints)

            String inputFile = args[0];
            String outputFile = args[1];


            
            boolean errorOccurred = false;

            try {
                errorOccurred = !Input.getInstance().parseFile(inputFile); 
            } catch (Exception e) {
                System.out.println("Error: Main.java | parsing file threw exception");
                errorOccurred = true;
            }

            if (!errorOccurred) {
                // use the data structures in input to run the AND-TREE class on. 
            }
        }
    }
}