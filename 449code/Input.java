/*
Input file: Aaron, Bryan
Goal: 
-To parse a correct input file.
-Put corresponding parsed input into variables which would be parsed into 
algorithm class
Ver:
	Update 1: 
		-Changed set name+fpa methods from booleans to void (work the error cases in the methods)
		-Name variable assigned after parsing: 
			-Test message added (remove in final product)
			-Exceptions still needs to be handled
		-Forced Partial assignment array assigned after parsing
			-Added a counter so that it'll give you error msg when more than 8 lines
			-Test message added (remove in final product)
			-Exceptions still needs to be handled
		-Forbidden Machine: 
			-Now a 2d array
	Update 2:
		-Two implementations of Forbidden Machine. Would like to discuss which is better on monday
			-SetFM1: implemented as an Array of list.
			-SetFM2: Implemented as a boolean 2d array.
			-Test messages added to both (remove in final product)
			-Exceptions still needs to be handled
	Update 3: 
		-Machine penalties assigned after parsing:
			-Test message added (remove in final product)
			-Exceptions still needs to be handled
	Update 4:
		-Too near task/penalties assigned after parsing:
			-Test messages added for both (remove in final product)
			-Exceptions still needs to be handled
	Update 5:
		- Added _Aaron methods (placeholder name)
	Update 6:
		-Fixed Name method: whitespace
		-Fixed Forbidden Machine: Assignment to array was being funky
		-Problems to note: 
			-Do we handle duplicates (for all methods)
			-To Aaron: You need to explain TNP flags to me!
				-TNP: Initialize to constant = -2
				-While we're at at it, make constant for -1
			-To Aaron: You need to explain what "Finished" does to me!
			-Sometimes prints out two error message (should only print out one, since it terminates program right after error message)
				-Use System.exit(0)?
	Things that needs to be done:
		-Exception handling
		-Seeing if our code lines up (algorithm.java and input.java)
	Update 7:
		- Added checkForTuple() method which is called in FPA, FM, TNT, and TNP methods which makes sure line is in proper tuple format ... 
		- ... before splitting the string. 
		- Added Exception handling for Integer.parseInt() inside FPA and FM methods 
	Update 8:
		- renamed ...Aaron methods
		- added new flag to catch error when a duplicate key name is found
		- added new flag to catch when any specific error has occurred to prevent more than 1 error message being displayed 
		- *** temporarily disabled most counter variables since I think the design should be that any lines between headers belong to the ... 
		- ... above header (thus, if more lines are specified than there should be, the method will detect that anyway and throw an error). 
	Update 9:
		-Corrected error message for setTNP.
			-Was: invalid machine/task
			-Now: invalid task
		-Changes sysout messages to Output.msg()
	Update 10:
		- changed the way duplicates work for FM, TNT and how penalties update in TNP
		- removed tooNearPenaltiesFlags[] since it is no longer important for checking duplicates
		- changed some error messages
		- removed duplicateKeyFound flag since it was redundant
		- implemented the checking of previous line when a key is found to make sure that there was at least a gap of blank space before it
	Update 11:
		- Changed setMP so that it prints out correct error message: 
			-after 8 lines w/o error, seeing a white space: it expects header so it should be "Error while Parsing"
			-At least that's how proffesor explained it to me
	Update 12:
		-Did the same thing to setMP to setFPA. after 8 lines seeing a whitespace, will output "Error while Parsing"
	Update 13:
		-Change check for keys so that it doesn't have to wait till the end of file to check if missing any headers
	Update 14: 
		-Accept EXACTLY headers specified on assignment description
*/
import java.io.*;

public class Input {
	
	private final int[] MACHINES = {1, 2, 3, 4, 5, 6, 7, 8}; 
	private final String[] TASKS = {"A", "B", "C", "D", "E", "F", "G", "H"}; 

	public String name;

	// forcedPartialAssignment[i] = j: task j must be assigned to machine i
	// Default: null
	public Task[] forcedPartialAssignment = new Task[8];

	public int[][] forbiddenMachine = new int[8][8]; 
	
	//if tooNearTasks[i][j] = -1: task i and task j are too-near
	// if tooNearTasks[i][j] = 0: no penalty
	// otherwise: too-near penalty (actual value)
	public int[][] tooNearTasksAndPenalties = new int[8][8];
	
	public int[][] machinePenalties = new int[8][8];
	
	public boolean nameFound, fpaFound, fmFound, tntFound, mpFound, tnpFound; // 6 keys to find
	
	private boolean specialErrorOccurred = false; 
	
	private String previousLine = ""; // used to check if line before a header line was empty (which is required)
	private String currentLine = ""; 
	
	public Input() {
		for (int i = 0; i<8; i++) {
			for (int j = 0; j<8; j++) {
				forbiddenMachine[i][j] = -1; 
			}
		}
	}
	
	public boolean parseFile(String filename) throws Exception{
		FileReader fr = null;
		BufferedReader br = null; 
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr); 
			String line;
			
			while((line = br.readLine()) != null) {
				line = line.trim();
				previousLine = currentLine;
				currentLine = line; 
				if (line.isEmpty()) { // if line is blank... 
					continue;
				}
				boolean keyFound = checkForKey(line, br, true); 
				if (!keyFound) { // found a value instead for the first (non-empty) line in file
					Output.msg("Error while parsing input file"); // means that a header is missing at top
					specialErrorOccurred = true; 
				}
				if (specialErrorOccurred)
					break; 
			 
			}
		}catch (FileNotFoundException e) {
			System.out.println("Error: The specified input file was not found.");
			specialErrorOccurred = true;
		}catch (IOException e) {
			System.out.println("Error: Failed to read line properly.");
			specialErrorOccurred = true;
		}finally {
			if (fr != null) {
				fr.close();
			}	
		}
		
		if (specialErrorOccurred) // already took care of a specific error message
			return false; 
		
	/*	
		if (!nameFound || !fpaFound || !fmFound || !tntFound || !mpFound || !tnpFound) { // if a key header wasn't found... 
			Output.msg("Error while parsing input file"); // missing a key header
			return false;
		}
		return true; // default return (parsing was successful if no errors occurred)
	*/	
	
		// parsing was successful if no errors occurred
		if (nameFound && fpaFound && fmFound && tntFound && mpFound && tnpFound) {
			return true;
		} else {
	
			// if a key header wasn't found...
			Output.msg("Error while parsing input file"); // missing a key header
			return false;
		}
	}	
	
	// helpers will return if they were successful or not
	// eventually these boolean returns will dictate if execution continues or not (and what error message to print, if any)
	
	// ****** NEED to quit when an error occurs
	/**
	 * @param line (check if current line is a key or not)
	 * @param br
	 * @param finished (is previous method successfully finished executing?)
	 * @return keyFound (if line was a key)
	 */
	private boolean checkForKey(String line, BufferedReader br, boolean finished) {
		boolean keyFound = true; // assume a key will be found, if not then return false
		String testLine = line; 
		
		switch (testLine) {
		case "Name:":
			if (!nameFound) {
				nameFound = true;
				if (!previousLine.isEmpty()) {
					Output.msg("Error while parsing input file"); // line proceeding header was not empty
					specialErrorOccurred = true;
				}
			}
			else {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true; 
			}
			if (finished && !specialErrorOccurred) {
				try {
					setName(br);
				} catch (Exception e) {
					// quit
				}
			}	
			break;
		case "forced partial assignment:":
			if (!nameFound) {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}
			
			if (!fpaFound) {
				fpaFound = true;
				if (!previousLine.isEmpty()) {
					Output.msg("Error while parsing input file"); // line proceeding header was not empty
					specialErrorOccurred = true;
				}
			}	
			else {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}	
			if (finished && !specialErrorOccurred) {	
				try {
					setFPA(br);
				} catch (Exception e) {
					// quit
				}
			}	
			break;
		case "forbidden machine:":
			if (!fpaFound) {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}
			if (!fmFound) {
				fmFound = true;
				if (!previousLine.isEmpty()) {
					Output.msg("Error while parsing input file"); // line proceeding header was not empty
					specialErrorOccurred = true;
				}
			}	
			else {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}	
			if (finished && !specialErrorOccurred) {
				try {
					setFM(br);
				} catch (Exception e) {
					// quit
				}
			}	
			break;
		case "too-near tasks:":
			if (!fmFound) {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}
			if (!tntFound) {
				tntFound = true;
				if (!previousLine.isEmpty()) {
					Output.msg("Error while parsing input file"); // line proceeding header was not empty
					specialErrorOccurred = true;
				}
			}	
			else {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}
			if (finished && !specialErrorOccurred) {
				try {
					setTNT(br);
				} catch (Exception e) {
					// quit
				}
			}	
			break;
		case "machine penalties:":
			if (!tntFound) {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}
			if (!mpFound) {
				mpFound = true;
				if (!previousLine.isEmpty()) {
					Output.msg("Error while parsing input file"); // line proceeding header was not empty
					specialErrorOccurred = true;
				}
			}	
			else {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}	
			if (finished && !specialErrorOccurred) {
				try {
					setMP(br);
				} catch (Exception e) {
					// quit
				}
			}	
			break; 
		case "too-near penalities":
			if (!mpFound) {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}
			if (!tnpFound) {
				tnpFound = true;
				if (!previousLine.isEmpty()) {
					Output.msg("Error while parsing input file"); // line proceeding header was not empty
					specialErrorOccurred = true;
				}
			}	
			else {
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
			}	
			if (finished && !specialErrorOccurred) {
				try {
					setTNP(br);
				} catch (Exception e) {
					// quit
				}
			}	
			break;
		default:
			keyFound = false;
		}
		return keyFound; 
	}

	// *** Work In Progress
	// NOTE: name can be ANY character string as per assignment description (even a key name or blank line ?) - do we need to trim?
	private void setName(BufferedReader br) throws Exception {
		String line;
		boolean finished = false; // not finished until a name has been found (character string that isn't a header) 
		boolean keyFound = false;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			previousLine = currentLine;
			currentLine = line;
			if (line.isEmpty()) {
				continue;
			}
				
			//Checks if you reach a header
			keyFound = checkForKey(line, br, finished);
			if (keyFound) { // found a header before finding a name!
				if (!specialErrorOccurred) {
					Output.msg("Error while parsing input file");
					specialErrorOccurred = true;
				}
				return;
			}
			else { // found a string that isn't a header!
				name = line;
				finished = true; 
				//System.out.println("Test Message: Name is " + name);
				return;
			}
		}
	}
	// ***
		
	// *** How do we need to handle duplicate tuples?
	// *** right now it treats them as a new tuple and throws an error
	private void setFPA(BufferedReader br) throws Exception {
		String line;
		boolean finished = true; 
		boolean keyFound = false;
		boolean lookForHeader = false;
		boolean moreThanEight = false;
		int counter = 0;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			previousLine = currentLine;
			currentLine = line;
			if (line.isEmpty()) {
				if (moreThanEight)
					lookForHeader = true;
				continue;
			}
			
			//Checks if you reach a header
			keyFound = checkForKey(line, br, finished);
			if (keyFound)
				return; // *** No Error since values can be blank
			else {
				if (lookForHeader) {
					Output.msg("Error while parsing input file");
					specialErrorOccurred = true;
					return; 
				}
			}
			
			if (!checkForTuple(line, 2)) { // make sure the line is in proper 2-tuple format
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
				return; 
			}	
			String[] segments = line.split("[(,)]"); // results in {" ",*mach*,*task*} ideally
			
			//Checks if machine is a valid machine
			int machine;
			try {
				machine = Integer.parseInt(segments[1].trim()); // *** need to handle exceptions
			}
			catch (NumberFormatException e) {
				Output.msg("invalid machine/task"); // machine is not an integer
				specialErrorOccurred = true;
				return; // quit
			}
			if (!verifyMachineNumber(machine)) {
				Output.msg("invalid machine/task"); // machine is an integer but not in [1,8]
				specialErrorOccurred = true;
				return; // quit
			}
			
			//Checks if task is a valid symbol
			String taskSymbol = segments[2].trim(); 
			if (!verifyTaskSymbol(taskSymbol)) {
				Output.msg("invalid machine/task"); // task is not in [A,H]
				specialErrorOccurred = true;
				return; // quit
			}
			//Checks for overlap
			Task task = Task.valueOf(taskSymbol);
			for (Task aTask : forcedPartialAssignment) {
				if (aTask != null) {
					if (task.id == aTask.id) { 
						Output.msg("partial assignment error"); // 2 machines share same task 
						specialErrorOccurred = true;
						return; // quit
					}
				}
			}
			if (forcedPartialAssignment[machine-1] == null) {
				forcedPartialAssignment[machine-1] = task; // store task in array if slot is empty (meaning machine hasn't been assigned a task yet)
			}
			else {
				Output.msg("partial assignment error"); // 2 tasks share same machine
				specialErrorOccurred = true;
				return; // quit
			}
			counter++;
			if (counter >= 8) {
				moreThanEight = true;
			}
		}		
	}
	
	private void setFM(BufferedReader br) throws Exception {
		String line;
		boolean finished = true; 
		boolean keyFound = false;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			previousLine = currentLine;
			currentLine = line;
			if (line.isEmpty()) {
				continue;
			}
			
			//Checks for header
			keyFound = checkForKey(line, br, finished);
			if (keyFound)
				return; // *** No Error since values can be blank
			
			if (!checkForTuple(line, 2)) { // make sure the line is in proper 2-tuple format
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
				return; 
			}
			String[] segments = line.split("[(,)]"); // results in {" ",*mach*,*task*} ideally
			
			//Checks if its a valid machine
			int machine;
			try {
				machine = Integer.parseInt(segments[1].trim()); // *** need to handle exceptions
			}
			catch (NumberFormatException e) {
				Output.msg("invalid machine/task"); // machine is not an integer
				specialErrorOccurred = true;
				return; // quit
			}
			if (!verifyMachineNumber(machine)) {
				Output.msg("invalid machine/task"); // machine is an integer but not in [1,8]
				specialErrorOccurred = true;
				return; // quit
			}
			
			//Checks if its a valid task
			String taskSymbol = segments[2].trim(); 
			if (!verifyTaskSymbol(taskSymbol)) {
				Output.msg("invalid machine/task"); // task is not in [A,H]
				specialErrorOccurred = true;
				return; // quit
			}
			
			//Set value in the array (duplicates will just be treated as usual since no error will occur)
			int taskID = Task.valueOf(taskSymbol).id;
			forbiddenMachine[machine-1][taskID] = taskID;
			
		}
	}
	
	private void setTNT(BufferedReader br) throws Exception {
		String line;
		boolean finished = true; 
		boolean keyFound = false;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			previousLine = currentLine;
			currentLine = line;
			if (line.isEmpty()) {
				continue;
			}
			
			//Checks for header
			keyFound = checkForKey(line, br, finished);
			if (keyFound)
				return; // *** No Error since values can be blank
			
			if (!checkForTuple(line, 2)) { // make sure the line is in proper 2-tuple format
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
				return; // quit
			}
			String[] segments = line.split("[(,)]"); // results in {" ",*task i*,*task j*} ideally
			String taskSymbol_i = segments[1].trim(); // *** need to handle exceptions
			String taskSymbol_j = segments[2].trim();
			
			//Check if valid task
			if ((!verifyTaskSymbol(taskSymbol_i)) || (!verifyTaskSymbol(taskSymbol_j))) {
				Output.msg("invalid machine/task"); // task is not in [A,H]
				specialErrorOccurred = true;
				return; // terminate
			}
			
			//Set array
			int taskID_i = Task.valueOf(taskSymbol_i).id;
			int taskID_j = Task.valueOf(taskSymbol_j).id;
			tooNearTasksAndPenalties[taskID_i][taskID_j] = -1; // hard constraint will override any soft penalty
			
		}
	}
	
	private void setMP(BufferedReader br) throws Exception {
		String line;
		int counter = 0; // how many non-empty lines we've processed so far
		boolean finished = false; // not finished until we have 8 lines
		boolean keyFound = false; 
		boolean lookForHeader = false;	//More than 8 lines and after a whitespace...you are in look for header mode
		while ((line = br.readLine()) != null) {
			line = line.trim();
			previousLine = currentLine;
			currentLine = line;
			if (line.isEmpty()) {
				//If white space after 8 lines...
				if (finished)	
					lookForHeader = true;
				continue;
			}
			
			//Check for header
			keyFound = checkForKey(line, br, finished);
			if (keyFound)
				if (finished)
					return; // *** No Error since we had 8 lines before moving on
				else {	
					if (!specialErrorOccurred) { // only print message if another error hasn't occurred yet (prevent 2 error messages)
						Output.msg("machine penalty error"); // less than 8 lines
						specialErrorOccurred = true;
					}	
					return; // quit
				}
			else if (finished) {
				//Expecting a header after 8 lines + white space
				if (lookForHeader) {
					Output.msg("Error while parsing input file"); 
					specialErrorOccurred = true;
					return; // quit
				}
				//after 8 lines + no whitespace
				else {
					Output.msg("machine penalty error"); // more than 8 lines *** change this to check if line is in 8-int line format before usng this error msg
					specialErrorOccurred = true;
					return; // quit
				}
				
			}	
			
			//Checks if line is valid
			String[] segments = line.split("\\s+"); // allow any number of spaces between values on line
			if (segments.length != 8) {
				Output.msg("machine penalty error"); // less than or more than 8 values on a line
				specialErrorOccurred = true;
				return; // quit
			}
			
			//Check if penalty is valid
			for (int i=0; i<segments.length; i++) {
				int penalty; 
				try {
					penalty = Integer.parseInt(segments[i]); // make sure penalty is an integer
				}	
				catch (NumberFormatException e) { 
					Output.msg("invalid penalty"); // penalty is not an integer
					specialErrorOccurred = true;
					return; // terminate
				}
				
				if (penalty < 0) { // make sure penalty is non-negative
					Output.msg("invalid penalty"); // penalty is not non-negative
					specialErrorOccurred = true;
					return; // terminate
				}
				
				machinePenalties[counter][i] = penalty;
			}	
			counter++; // increment counter after setting all 8 penalties for the machine on this line
			
			if (counter == 8) // we have processed all necessary information once we have read 8 lines
				finished = true; 
		}	
		// Test Case: if MP: is last header in file and exactly 8 lines arent specified, then we need this error to be included (might only happen when counter <8)
		if (counter != 8) {
			Output.msg("machine penalty error"); 
			specialErrorOccurred = true;
			return; // quit
		}
	}
	
	// NOTE: changed implementation to now treat penalties as a variable that will update to the latest value (e.g. (A,B,10) becomes (A,B,15))
	private void setTNP(BufferedReader br) throws Exception {
		String line;
		boolean finished = true; 
		boolean keyFound = false;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			previousLine = currentLine;
			currentLine = line;
			if (line.isEmpty()) {
				continue;
			}
			
			//Check for headers
			keyFound = checkForKey(line, br, finished);
			if (keyFound)
				return; // *** No Error since values can be blank
			
			if (!checkForTuple(line, 3)) { // make sure the line is in proper 3-tuple format
				Output.msg("Error while parsing input file");
				specialErrorOccurred = true;
				return; 
			}
			//Check if its a valid task
			String[] segments = line.split("[(,)]"); // results in {" ",*task i*,*task j*,*penalty*} ideally
			String taskSymbol_i = segments[1].trim(); // *** need to handle exceptions
			String taskSymbol_j = segments[2].trim();
			if ((!verifyTaskSymbol(taskSymbol_i)) || (!verifyTaskSymbol(taskSymbol_j))) {
				Output.msg("invalid task"); // task not in [A,H]
				specialErrorOccurred = true;
				return; // terminate
			}
			
			//Checks if penalty is valid
			int penalty;
			try {
				penalty = Integer.parseInt(segments[3].trim()); // make sure penalty is an integer
			}
			catch (NumberFormatException e) {
				Output.msg("invalid penalty"); // penalty is not an integer
				specialErrorOccurred = true;
				return; // terminate
			}
			
			if (penalty < 0) { // make sure penalty is non-negative
				Output.msg("invalid penalty"); // penalty is not non-negative
				specialErrorOccurred = true;
				return; // terminate
			}
			
			//Setting up the array
			int taskID_i = Task.valueOf(taskSymbol_i).id;
			int taskID_j = Task.valueOf(taskSymbol_j).id;
			if (tooNearTasksAndPenalties[taskID_i][taskID_j] != -1) { // make sure tasks aren't already hard constrained
				tooNearTasksAndPenalties[taskID_i][taskID_j] = penalty; // overwrite any previous penalty if there was one
			}
		}
	}
	
	private boolean verifyMachineNumber(int machine) {
		boolean validMachine = false;
		for (int mach : MACHINES) {
			if (machine == mach) {
				validMachine = true;
				break; 
			}
		}
		return validMachine; 
	}
	
	private boolean verifyTaskSymbol(String task) {
		boolean validTask = false;
		for (String symbol : TASKS) {
			if (task.equals(symbol)) {
				validTask = true;
				break;
			}
		}
		return validTask; 
	}
	
	/**
	 * @param str (check this trimmed string if it is a tuple)
	 * @param size (size (n) for an n-tuple where size >= 1)
	 * @return (if string is an n-tuple)
	 */
	private boolean checkForTuple(String str, int size) {
		
		char[] symbols = str.toCharArray(); // break str into its characters
		int minLength = 2*size + 1;  // minimum number of characters needed for an n-tuple
		int commaCount = 0; // number of commas found in str so far
		int commasNeeded = size - 1; // number of commas that should appear in an n-tuple
		if (symbols.length < minLength) { // check that str has enough characters
			return false;
		}
		if (symbols[0] != '(' || symbols[symbols.length-1] != ')') { // check that first character is '(' and last is ')'
			return false;
		}
		if (symbols[1] == ',' || symbols[symbols.length-2] == ',') { // make sure "(," or ",)" doesn't appear (empty value)
			return false;
		}
		for (int i = 1; i < symbols.length - 1; i++) { // loop through all characters between ()
			if (symbols[i] == '(' || symbols[i] == ')') { // make sure that any extra brackets don't appear
				return false;
			}
			if (symbols[i] == ',') { // if a comma is found...
				if (symbols[i-1] == ',') { // make sure previous character wasn't a comma to avoid ",," appearing (empty value)
					return false; // two commas together means an empty value , e.g. ( ,, )  
				}
				commaCount++; // increment number of commas found so far
				if (commaCount > commasNeeded) { // stop checking characters if too many commas have been found
					return false; 
				}
			}
		}
		
		return (commaCount == commasNeeded); // make sure that the proper number of commas was found
	}	
		
}
