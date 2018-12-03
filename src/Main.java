


public class Main {

    public static void main(String[] args) {

        if (args.length == 10) { // change this later when more command-line args need to be specified (like soft constraints)

            // ~~~~~~~add checking here that the files exist... (at least the inputfile, since the outputfile would be created if notn existing)

            String inputFile = args[0];
            String outputFile = args[1];

            try {
                Algorithm.pen_coursemin = Double.parseDouble(args[2]);
                Algorthm.pen_labsmin = Double.parseDouble(args[3]);
                Algorithm.pen_notpaired = Double.parseDouble(args[4]);
                Algortihm.pen_section = Double.parseDouble(args[5]);
                Algorithm.w_minfilled = Double.parseDouble(args[6]);
                Algorithm.w_pref = Double.parseDouble(args[7]);
                Algorithm.w_pair = Double.parseDouble(args[8]);
                Algorithm.w_secdiff = Double.parseDouble(args[9]);
            }
            catch (NumberFormatException e) {
                System.out.println("ERROR: one of the command-line penalties/weights is non-numeric. Please ensure to only specify strings that can be interpreted as a real number");
                return;
            }
            

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
        else {
            System.out.println("Usage: java Main <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff>");
        }
    }
}