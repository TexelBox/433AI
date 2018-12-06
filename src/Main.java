


public class Main {

    public static boolean _DEBUG = false; // NOTE: I call this debug, but its really for the large files if you want to have a max runtime..

    public static void main(String[] args) {

        if (args.length == 10 || args.length == 12) { 

            // checking here that the files exist... (at least the inputfile, since the outputfile would be created if not existing)

            String inputFilePath = args[0];
            String outputFilePath = args[1];
            
            boolean doesInputFileExist = Input.getInstance().setInputFilePath(inputFilePath);
            
            if (!doesInputFileExist) {
            	return;
            }

            boolean doesOutputFileExist = Output.getInstance().initOutputWriter(outputFilePath);

            if (!doesOutputFileExist) {
                return;
            }

            try {
                Algorithm.pen_coursemin = Double.parseDouble(args[2]);
                Algorithm.pen_labsmin = Double.parseDouble(args[3]);
                Algorithm.pen_notpaired = Double.parseDouble(args[4]);
                Algorithm.pen_section = Double.parseDouble(args[5]);
                Algorithm.w_minfilled = Double.parseDouble(args[6]);
                Algorithm.w_pref = Double.parseDouble(args[7]);
                Algorithm.w_pair = Double.parseDouble(args[8]);
                Algorithm.w_secdiff = Double.parseDouble(args[9]);
            }
            catch (NumberFormatException e) {
                System.out.println("ERROR: one of the command-line penalties/weights is non-numeric. Please ensure to only specify strings that can be interpreted as a real number.");
                return;
            }

            Algorithm.setNegativeWeightOrPenalty();


            if (args.length == 12) {
                if (!args[10].equals("TIMEOUT")) {
                    System.out.println("Usage: java -jar CPSC433AI.jar <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff>");
                    System.out.println("or");
                    System.out.println("Timeout Usage: java -jar CPSC433AI.jar <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff> TIMEOUT <max runtime in milliseconds>");
                    System.out.println("TIMEOUT parameter has a typo");
                    return;
                }
                int runtime;
                try {
                    runtime = Integer.parseInt(args[11]);
                }
                catch (NumberFormatException e) {
                    System.out.println("Usage: java -jar CPSC433AI.jar <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff>");
                    System.out.println("or");
                    System.out.println("Timeout Usage: java -jar CPSC433AI.jar <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff> TIMEOUT <max runtime in milliseconds>");
                    System.out.println("please only specify a positive integer value (in ms) for the maximum runtime");
                    return;
                }
                if (runtime <= 0) { // negative or 0 runtime would be undefined/pointless
                    System.out.println("Usage: java -jar CPSC433AI.jar <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff>");
                    System.out.println("or");
                    System.out.println("Timeout Usage: java -jar CPSC433AI.jar <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff> TIMEOUT <max runtime in milliseconds>");
                    System.out.println("please only specify a positive integer value (in ms) for the maximum runtime");
                    return;
                }
                Algorithm._runtime = runtime;

                boolean doesLogFileExist = Output.getInstance().initLogWriter();
                if (!doesLogFileExist) {
                    return;
                }

                // if everything went good, we are in DEBUG/TIMEOUT mode...

                _DEBUG = true;

            }



            boolean parseErrorOccurred = !Input.getInstance().parseFile(); 

            if (!parseErrorOccurred) {
                // use the data structures in input to run the algorithm on.
                Algorithm algo = new Algorithm();
                boolean isValidSol = algo.processTree(); 
                if (isValidSol) {
                    // print bestEval and bestAssign to output file
                    Output.getInstance().outputValidSolution();
                }
                else { // didn't find a valid solution..
                	if (_DEBUG) {
                		// if we timed out without finding one...
                        if (System.currentTimeMillis() >= Algorithm._endtime) { // if we timed out with no solution found yet...
                            Output.getInstance().outputNoSolutionFoundYet();
                        }
                        else {
                            // print NO VALID SOLUTION to output file
                            Output.getInstance().outputNoValidSolution();
                        }
                	}
                	else {
                		Output.getInstance().outputNoValidSolution();
                	}
                    
                }

            }

            //Output.getInstance().closeOutputFile();
            if (_DEBUG) {
                Output.getInstance().closeLogFile();
            }
            
        }
        else {
            System.out.println("Usage: java -jar CPSC433AI.jar <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff>");
            System.out.println("or");
            System.out.println("Timeout Usage: java -jar CPSC433AI.jar <input file> <output file> <pen_coursemin> <pen_labsmin> <pen_notpaired> <pen_section> <w_minfilled> <w_pref> <w_pair> <w_secdiff> TIMEOUT <max runtime in milliseconds>");
        }
    }
}