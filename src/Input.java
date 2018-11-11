


// read from text file
// keywords + tables below with comma seperated fields

// KEYWORDS:
/*
    "Name:"
    "Course slots:"
    "Lab slots:"
    "Courses:"
    "Labs:"
    "Not compatible:"
    "Unwanted:"
    "Preferences:"
    "Pair:"
    "Partial assignments:"

*/

// ADDITIONAL INFO:
/*

    course slots: "MO", "TU"
    lab slots: "MO", "TU", "FR"

    if a possible slot isn't in input file, set coursemax/coursemin and labmax/labmin to 0 (so we can't assign anything to these slots)

    schedule only the courses in the table below "Courses:" keyword (except for 813 (313) and 913 (413), both scheduled into LAB SLOTS)

    schedule only the labs in the table below "Labs:" keyword

    build in incompatibility for course sections and labs of same course. incompatiblity is symmetric.

    not-compatible section has no particular order...

    unwanted section has no particular order...

    preferences section has no particular order...


    if a preference lists an invalid time slot, IGNORE IT (but can also print a warning message)

    if we have a preference that is also unwanted, then unwanted overrides it (no error)

    pair section has no perticular order...

    partial assignments has no particular order...

    IF A PARTIAL ASSIGNMENT IS NOT VALID (i.e. COURSE + SLOT THATS NOT A COURSE SLOT OR LAB + SLOTS THATS NOT A LABSLOT, EXCEPTION: 813/913), TERMINATE WITH AN ERROR MESSAGE


    KEYWORDS OCCUR IN THE SAME ORDER IN EVERY FILE
    TRIM ALL THE EXTRA WHITESPACE
    GUI? IT SAYS WE CAN START THE SYSTEM WITH COMMAND-LINE PARAMS (LIKE INPUT FILE, OUTPUT FILE, SOFT CONSTRAINT/EVAL DATA)

    DEFINE EVAL USING 4 SUBFUNCTIONS: Eval_minfilled, Eval_pref, Eval_pair, Eval_secdiff (each has a value for each of 4 soft-constraints)
    Eval(assign) = Evalminfilled(assign) * wminfilled + Evalpref(assign) * wpref + Evalpair * wpair + Evalsecdiff(assign) * wsecdiff
    4 weights to rate importance of each type of soft-constraint
    - some examples will have these values as 0

    OUTPUT:

    form specified on page
    order by courses alphabetically: number > lec number > lab number > tut number


    PLAN:
    - slots have a start time (end time can be calculated based on ?)



    POSSIBLE ERRORS:
    1. missing keyword


    QUESTIONS:
    - what if Example-name is not specified? (look at 449 case)
    - should we have a general error message if theres a typo in file?
    - error for if a keyword missing? (look at 449)
    - is LEC01, LEC0 1,  possible?
    - do we need a blank line before next keyword like 449?

*/

import java.io.*;

public class Input {

    // fields...

    // figure out best data structures to store parsed data in here...

/*
    "Name:"
    "Course slots:"
    "Lab slots:"
    "Courses:"
    "Labs:"
    "Not compatible:"
    "Unwanted:"
    "Preferences:"
    "Pair:"
    "Partial assignments:"
*/

    // 10 keynames to find...
    private boolean nameKeyFound, courseSlotsKeyFound, labSlotsKeyFound, coursesKeyFound, labsKeyFound, notCompatibleKeyFound, unwantedKeyFound, preferencesKeyFound, pairKeyFound, partialAssignmentsKeyFound;


    // methods...

    public Input() {
        // init...
    }



    public boolean parseFile(String filename) throws Exception {

		FileReader fr = null;
		BufferedReader br = null; 
        try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr); 
			String line; // go through line by line...
			
			while((line = br.readLine()) != null) {
				line = line.trim(); // remove leading and trailing whitespace...
				//previousLine = currentLine;
				//currentLine = line; 
				if (line.isEmpty()) { // if trimmed line is blank... 
					continue;
				}
				//boolean keyFound = checkForKey(line, br, true); 
				//if (!keyFound) { // found a value instead for the first (non-empty) line in file
				//	Output.msg("Error while parsing input file"); // means that a header is missing at top
				//	specialErrorOccurred = true; 
				//}
				//if (specialErrorOccurred)
				//	break; 
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Input.java | The specified input file was not found.");
            //specialErrorOccurred = true;
        } catch (IOException e) {
            System.out.println("Error: Input.java | Failed to read line properly.");
            //specialErrorOccurred = true;
        } finally {
            if (fr != null) {
                fr.close(); // also closes br for us
            }	
        }

        //if (specialErrorOccurred) // already took care of a specific error message
        //	return false; 
        

        // parsing was successful if no errors occurred
		if (nameKeyFound && courseSlotsKeyFound && labSlotsKeyFound && coursesKeyFound && labsKeyFound && notCompatibleKeyFound && unwantedKeyFound && preferencesKeyFound && pairKeyFound && partialAssignmentsKeyFound) {
			return true; // if all keywords were found
        } 
        else {
			// if any keyword wasn't found...
			//Output.msg("Error while parsing input file"); // missing a key header
			return false;
        }
        
	}	


}