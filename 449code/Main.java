public class Main {

    public static void main(String[] args) {

        if (args.length == 2) {

            String inputFile = args[0];
            String outputFile = args[1];

            Output.setOutputFile(outputFile);

            Input input = new Input();
            
            boolean errorOccurred = false;

            try {
                errorOccurred = !input.parseFile(inputFile);
            } catch (Exception e) {
                System.out.println("Error parseFile");
                errorOccurred = true;
            }

            if (!errorOccurred) {
                new Algorithm(input.machinePenalties, input.forcedPartialAssignment,
                        input.forbiddenMachine, input.tooNearTasksAndPenalties);
            }
        }
    }
}
