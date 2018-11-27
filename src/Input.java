


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
    - what do we do if we are given CPSC 433 LEC 01 is not compatible with CPSC 433 LEC 01 (I would say throw an error since you would never get a valid answer)
    - if one line is CPSC 433 LAB 01 and another line is CPSC 433 TUT 01, do we treat them as the same thing?
    - EVENING CLASSES: is it only LEC 09 or is it >= 9?, is CPSC 433 LEC 09 TUT 01 evening? - i dont think so since its a tut not a 'course section' = lecture

    ANSWERS:
    - name can't be a blank line or a keyword
    - he expects the parsing to go character by character L->R (NL understanding)
    - this assignment seems to be less focused on the parser, and more on the algorithm (there isnt special error messages), can just have 1 general error msg

*/

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;



public class Input {

    // singleton design pattern:

    private static Input instance = null;

    private Input() {
        // init...
    }

    public static Input getInstance() {
        if (instance == null) {
            instance = new Input();
        }
        return instance;
    }

    //



    // fields...

    public String _name;

    // have these 
    public Map<String, Integer> _mapSlotToIndex = new HashMap<>(); // init size 0, get index j for a slot
    public Map<String, Integer> _mapCourseToIndex = new HashMap<>(); // init size 0, get index i for a course

    public List<Slot> _slotList = new ArrayList<>(); // add a slot TO END everytime input file specifies a new slot that wasnt mentioned before, NOTE: AND-Tree class will use this to expand leaves
    public List<Course> _courseList = new ArrayList<>(); // add a course TO END everytime input file specifies a new course that wasnt mentioned before

    // this array is symmetric across diagonal
    // later on init the size to be square at NxN where N = size of _mapCourseToIndex (number of keys = number of courses to schedule)
    public boolean[][] _notCompatibles; // default init is false, set _notCompatibles[i][j] = true and _notCompatibles[j][i] = true, if we get line COURSE1, COURSE2 (in not-compatible table) and index(Course1) = i and index(course2) = j

    // later on init the size to be [M][N] where M = size of _mapCourseToIndex (number of courses) and N = size of _mapSlotToIndex (number of available slots)
    public boolean[][] _unwanteds; // [i][j] = true if course i can't be assigned to slot j (rows are courses, cols are slots)

    // WATCH OUT! (the line is read L->R with Slot -> course which makes you want to put i for slot and j for course, but to keep consisten with other data structures, we use i for course and j for slot)
    // not sure if this should be int or double
    // set size to be MxN like _unwanteds
    public double[][] _preferences; // [i][j] = x if preference value of course i with slot j is x, 0.0 by default

    // init size as NxN (N = number of courses)
    public boolean[][] _pairs; // [i][j] = true if course i is paired with course j, false by default, maybe init diagonal to be true (reflexive property of relation)

    // NOTE: could put partial assignment here in a 2D array, but that seems overkill since each course can only have 1 partial assignment, so we could store this as a field of a Course instead. print no possible solution if a course is assigned 2 partial assignments

    // figure out best data structures to store parsed data in here...

    // IDEAS:

    /*
    IDEA 1:

    A HashMap<String, Integer> with (e.g. key = "CPSC 433 LEC 01") and value = i (where this was the ith (from 0) class found in input file reading top to bottom)

    */

    // we are given a new course/lab identifier on a line, we have that unique string (after some magic like trimming), convert it 

    // Have an 

    // represent $ as NULL

    // Have an ArrayList<Course>, which is ordered from 0 to m-1 - contains all the courses we need to schedule (provided input)
    // Have an ArrayList<ArrayList<Lab>> which is has the outer list ordered from 0 to m-1, and an inner list element ordered from 0 to k-1 - thus list[i] is the list of labs/tuts connected to course c_i. If k=0 then have {} empty list
    // Have an ArrayList<Slot> which is ordered from 0 to n-1
    // Have an ArrayList<Slot> which contains all possible slots (thus this is the superset of the previous line)


    // INSIDE ALGORITHM:
    // have a vector of size p (number of input slots)
    // ith element corresponds to current slot assignment to 

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

    // NOTE: ~~~~~~~~for algorithm, keep track of best assignment as a copy of the arraylsit of slots and have the EVAL stored.

    // 10 keynames to find...
    private boolean _nameKeyFound, _courseSlotsKeyFound, _labSlotsKeyFound, _coursesKeyFound, _labsKeyFound, _notCompatibleKeyFound, _unwantedKeyFound, _preferencesKeyFound, _pairKeyFound, _partialAssignmentsKeyFound;

	//private boolean specialErrorOccurred = false; 


    // methods...


    //public Input() {
      //  // init...
    //}


    // return T or F (was parsing error-free?)
    public boolean parseFile(String filename) throws Exception {

        List<String> allNonBlankTrimmedLines = new ArrayList<String>();

		FileReader fr = null;
		BufferedReader br = null; 
        try {
			fr = new FileReader(filename);
            br = new BufferedReader(fr); 
            
            // 1. read entire file into an arraylist that stores each NON-BLANK TRIMMED line as an element

			String line; // read 1 line at a time into arraylist
			
            while ((line = br.readLine()) != null) { // read until hitting EOF...
                
                line = line.trim(); // remove leading and trailing whitespace...
                
                if (line.isEmpty()) { // if trimmed line is blank... 
					continue; // don't add to list
				}

                allNonBlankTrimmedLines.add(line); // append to end of list
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: Input.java | The specified input file was not found.");
        } catch (IOException e) {
            System.out.println("Error: Input.java | Failed to read line properly.");
        } finally {
            if (fr != null) {
                fr.close(); // also closes br for us
            }	
        }



        int nameKeyIndex, courseSlotsKeyIndex, labSlotsKeyIndex, coursesKeyIndex, labsKeyIndex, notCompatibleKeyIndex, unwantedKeyIndex, preferencesKeyIndex, pairKeyIndex, partialAssignmentsKeyIndex;
        nameKeyIndex = courseSlotsKeyIndex = labSlotsKeyIndex = coursesKeyIndex = labsKeyIndex = notCompatibleKeyIndex = unwantedKeyIndex = preferencesKeyIndex = pairKeyIndex = partialAssignmentsKeyIndex = -1; // init to an invalid index

        // 2. check to make sure all keywords are in arraylist EXACTLY ONCE EACH (and if so, then we know they are in the correct order, by default)

        for (int i = 0; i < allNonBlankTrimmedLines.size(); i++) {

            String str = allNonBlankTrimmedLines.get(i); 

            switch(str) {
                case "Name:":
                    if (!_nameKeyFound) { // if this is the first time finding this key...
                        _nameKeyFound = true; // flag that we found it
                        nameKeyIndex = i;
                    } 
                    else { // if this key was already found, we have a duplicate and thats an error...
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Course slots:":
                    if (!_courseSlotsKeyFound) {
                        _courseSlotsKeyFound = true;
                        courseSlotsKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Lab slots:":
                    if (!_labSlotsKeyFound) {
                        _labSlotsKeyFound = true;
                        labSlotsKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Courses:":
                    if (!_coursesKeyFound) {
                        _coursesKeyFound = true;
                        coursesKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Labs:":
                    if (!_labsKeyFound) {
                        _labsKeyFound = true;
                        labsKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Not compatible:":
                    if (!_notCompatibleKeyFound) {
                        _notCompatibleKeyFound = true;
                        notCompatibleKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Unwanted:":
                    if (!_unwantedKeyFound) {
                        _unwantedKeyFound = true;
                        unwantedKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Preferences:":
                    if (!_preferencesKeyFound) {
                        _preferencesKeyFound = true;
                        preferencesKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Pair:":
                    if (!_pairKeyFound) {
                        _pairKeyFound = true;
                        pairKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
                case "Partial assignments:":
                    if (!_partialAssignmentsKeyFound) {
                        _partialAssignmentsKeyFound = true;
                        partialAssignmentsKeyIndex = i;
                    }
                    else {
                        System.out.println("Error: Duplicate keyword found in input file"); // maybe change this to a general error msg later? or write this in output file?
                        return false; // stop parsing
                    }
                    break;
            }

        }

        // now we know we dont have a duplicate keyword.
        // make sure we don't have a missing keywrord...
        if (!_nameKeyFound || !_courseSlotsKeyFound || !_labSlotsKeyFound || !_coursesKeyFound || !_labsKeyFound || !_notCompatibleKeyFound || !_unwantedKeyFound || !_preferencesKeyFound || !_pairKeyFound || !_partialAssignmentsKeyFound) {
            System.out.println("Error: Input file is missing a keyword.");
            return false; // stop parsing
        }

        // get here if all keywords were found EXACTLY ONCE
        // by the assignment definition, we KNOW that the input file has the keywords in the proper order



        // 3. split arraylist into 10 sublists (tables) for each keyword area


        // first make sure the input file didn't have any garbage non-blank lines before "Name:"

        if (nameKeyIndex != 0) {
            System.out.println("Error: Unrecognized symbols at top of input file."); // maybe change this to a general error msg later? or write this in output file?
            return false;
        }


        // each table will include the non-blank trimmed lines between its parent keyword (exclusive) and next keyword (exclusive)

        List<String> nameTable = allNonBlankTrimmedLines.subList(nameKeyIndex + 1, courseSlotsKeyIndex);
        List<String> courseSlotsTable = allNonBlankTrimmedLines.subList(courseSlotsKeyIndex + 1, labSlotsKeyIndex);
        List<String> labSlotsTable = allNonBlankTrimmedLines.subList(labSlotsKeyIndex + 1, coursesKeyIndex);
        List<String> coursesTable = allNonBlankTrimmedLines.subList(coursesKeyIndex + 1, labsKeyIndex);
        List<String> labsTable = allNonBlankTrimmedLines.subList(labsKeyIndex + 1, notCompatibleKeyIndex);
        List<String> notCompatibleTable = allNonBlankTrimmedLines.subList(notCompatibleKeyIndex + 1, unwantedKeyIndex);
        List<String> unwantedTable = allNonBlankTrimmedLines.subList(unwantedKeyIndex + 1, preferencesKeyIndex);
        List<String> preferencesTable = allNonBlankTrimmedLines.subList(preferencesKeyIndex + 1, pairKeyIndex);
        List<String> pairTable = allNonBlankTrimmedLines.subList(pairKeyIndex + 1, partialAssignmentsKeyIndex);
        List<String> partialAssignmentsTable = allNonBlankTrimmedLines.subList(partialAssignmentsKeyIndex + 1, allNonBlankTrimmedLines.size()); // make sure to test when this keyword is last line in file. (it should be fine)


        // 4. parse each of the 10 subarrays in order, storing the data into data structure fields of this class.

        // NOTE: must define data structures before doing this

        if (!setNameData(nameTable)) {
            System.out.println("Error: Invalid name data");
            return false;
        }

        if (!setCourseSlotsData(courseSlotsTable)) {
            System.out.println("Error: Invalid course slots data");
            return false;
        }

        if (!setLabSlotsData(labSlotsTable)) {
            System.out.println("Error: Invalid lab slots data");
            return false;
        }

        // ~~~~~~~~~~~~PUT CHECK HERE that we have at least 1 slot (otherwise output an error - no frames to put in)

        if (!setCoursesData(coursesTable)) {
            System.out.println("Error: Invalid courses data");
            return false;
        }

        if (!setLabsData(labsTable)) {
            System.out.println("Error: Invalid labs data");
            return false;
        }

        // ~~~~~~~~~~~~~~~~PUT CHECK HERE that we have at least 1 course (otherwise output an error - nothing to schedule)

        // IF EVERTHING IS GOOD...

        // init sizes of the arrays...

        int n = _slotList.size(); // N = number of possible slots
        int s = _courseList.size(); // S = number of courses (Lectures + labs/tuts) to schedule

        _notCompatibles = new boolean[s][s];




        // TEMP- for visual aid...
        //public boolean[][] _notCompatibles; 

        //public boolean[][] _unwanteds; 
    

        //public double[][] _preferences; 
    
        //public boolean[][] _pairs;







        if (!setNotCompatibleData(notCompatibleTable)) {
            System.out.println("Error: Invalid not compatible data");
            return false;
        }

        if (!setUnwantedData(unwantedTable)) {
            System.out.println("Error: Invalid unwanted data");
            return false;
        }

        if (!setPreferencesData(preferencesTable)) {
            System.out.println("Error: Invalid preferences data");
            return false;
        }

        if (!setPairData(pairTable)) {
            System.out.println("Error: Invalid pair data");
            return false;
        }

        if (!setPartialAssignmentsData(partialAssignmentsTable)) {
            System.out.println("Error: Invalid partial assignments data");
            return false;
        }

        // get here if no errors occurred...

        return true; // default return
    }	
    

    // ~~~~~~~~~~~~~~~~NOTE: remove the error msgs from these functions?


    // return True if no error occurred...
    private boolean setNameData(List<String> table) {

        if (table.size() == 0) {
            System.out.println("Error: missing name"); 
            return false;
        }
        else if (table.size() > 1) {
            System.out.println("Error: invalid extra line under Name:");
            return false;
        }
        else { // if 1 line
            _name = table.get(0);
        }
        return true;
    }



    // return True if no error occurred...
    private boolean setCourseSlotsData(List<String> table) {

        // NOTE: LATER ON WE NEED TO CHECK THAT NUMBER OF SLOTS (courseslots + labslots) > 0 and NUMBER OF COURSES (lectures + labs) > 0, can just have boolean flags to set true if we enter the for loops (or later check if list is size 0) ~~~~~~~~~~~~~~~~~~ 

        for (int i = 0; i < table.size(); i++) { // loop line by line
            String[] segments = table.get(i).split(","); // 1. split line by commas
            // 2. make sure we have expected number of segments
            if (segments.length != 4) {
                System.out.println("Error: invalid course slot");
                return false;
            }
            // get here if we have proper number of segments...
            // 3. see if each segment is valid input and if so keep track of it, make sure to trim each segment of leading and trailing whitespace and allow case-insensitivity?

            // NOTE: here we are just checking if the segments are in an understandable form, then later on we will check if our created Slot is a valid slot
            // ex. here we just check if a time is in valid HH:MM format ranging from 00:00 to 23:59, NOTE: im writing this to allow 03:00, 3:00, but NOT 3:0, thus HH:MM = H:MM

            // first segment = Day
            Slot.Day day;
            String seg0 = segments[0].trim().toUpperCase();
            switch (seg0) {
                case "MO":
                    day = Slot.Day.MO;
                    break;
                case "TU":
                    day = Slot.Day.TU;
                    break;
                default:
                    System.out.println("Error: invalid course slot"); // just using a general msg here
                    return false;
            }
            // get here if day was initialized properly

            // second segment = Start time
            int startHour;
            int startMinute;
            String seg1 = segments[1].trim(); // no need for uppercase since not working with letters here
            String[] subsegs_of_seg1 = seg1.split(":");
            if (subsegs_of_seg1.length != 2) {
                System.out.println("Error: invalid course slot");
                return false;
            } 

            // ~~~~~~~~~~ what is Integer.parseInt("1 1"); i would think an exception, but ill try it out when testing, also parseInt of a float?
            // make sure parseInt works for 00 and 0X, i'm pretty sure this works, note: in slot we will store the time as 0,1,2,...,10,11 (not 00, 01, but we can format this for output printing later)

            // must have HH:MM or H:MM
            if ((subsegs_of_seg1[0].length() != 1 && subsegs_of_seg1[0].length() != 2) || subsegs_of_seg1[1].length() != 2) {
                System.out.println("Error: invalid course slot");
                return false;
            }

            try {
                startHour = Integer.parseInt(subsegs_of_seg1[0]);
                startMinute = Integer.parseInt(subsegs_of_seg1[1]);
            }
            catch (NumberFormatException e) {
                System.out.println("Error: invalid course slot");
                return false;
            }

            // now make sure in range 00:00 to 23:59
            if (startHour < 0 || startHour > 23 || startMinute < 0 || startMinute > 59) {
                System.out.println("Error: invalid course slot");
                return false;
            }
            // get here if times are valid times (but still need to check slot validity later)

            // third segment = coursemax, 4th segment = coursemin
            int coursemax;
            int coursemin;
            String seg2 = segments[2].trim(); // no need for uppercase
            String seg3 = segments[3].trim(); // no need for uppercase

            try {
                coursemax = Integer.parseInt(seg2);
                coursemin = Integer.parseInt(seg3);
            }
            catch (NumberFormatException e) {
                System.out.println("Error: invalid course slot");
                return false;
            }

            if (coursemax < 0 || coursemin < 0 || (coursemax < coursemin)) { // ~~~~maybe if coursemax < coursemin, we output no valid solution instead?
                System.out.println("Error: invalid course slot");
                return false;
            }
            
            // get here if all 4 fields are set properly



            String hashKey = day.name() + Integer.toString(startHour) + Integer.toString(startMinute); // ~~~~~~~~test that this is consistent
            int hashIndex = _mapSlotToIndex.size(); // thus the new index is the size of map. ex. it contains 3 slots already (0,1,2), now we want to put this 4th slot at index 3
            Slot newSlot = new Slot(hashKey, hashIndex, day, startHour, startMinute);
            newSlot._coursemax = coursemax;
            newSlot._coursemin = coursemin;
            if (!newSlot.checkSlotValidity()) {
                System.out.println("Error: invalid course slot");
                return false;
            }
            
            if (_mapSlotToIndex.containsKey(hashKey)) { // due to the order of the input file, the only way this slot was already found, is that it was under course slots header (_courseSlot = true, so no need to check)
                System.out.println("Error: duplicate course slot"); // ~~~~~~~~~~~~~~~~~~~~~~~~~Figure out what to do with duplicates~~~~~~~~~~~~~~~~~~~~~~~~~~
                return false;
            }

            // get here if this is the first time this slot has been found in file
            newSlot._isCourseSlot = true; // flag that we found this slot under Course slots header
            _mapSlotToIndex.put(hashKey, hashIndex); // record that we have found this slot in file (just in case it shows up again)
            _slotList.add(newSlot); // add this slot to end of list
            
        }

        return true;
    }



    // pretty close to setCourseSlotsData()
    // return True if no error occurred...
    private boolean setLabSlotsData(List<String> table) {

        for (int i = 0; i < table.size(); i++) { // loop line by line
            String[] segments = table.get(i).split(","); // 1. split line by commas
            // 2. make sure we have expected number of segments
            if (segments.length != 4) {
                System.out.println("Error: invalid lab slot");
                return false;
            }
            // get here if we have proper number of segments...
            // 3. see if each segment is valid input and if so keep track of it, make sure to trim each segment of leading and trailing whitespace and allow case-insensitivity?

            // NOTE: here we are just checking if the segments are in an understandable form, then later on we will check if our created Slot is a valid slot
            // ex. here we just check if a time is in valid HH:MM format ranging from 00:00 to 23:59, NOTE: im writing this to allow 03:00, 3:00, but NOT 3:0, thus HH:MM = H:MM

            // first segment = Day
            Slot.Day day;
            String seg0 = segments[0].trim().toUpperCase();
            switch (seg0) {
                case "MO":
                    day = Slot.Day.MO;
                    break;
                case "TU":
                    day = Slot.Day.TU;
                    break;
                case "FR":
                    day = Slot.Day.FR;
                    break;
                default:
                    System.out.println("Error: invalid lab slot"); // just using a general msg here
                    return false;
            }
            // get here if day was initialized properly

            // second segment = Start time
            int startHour;
            int startMinute;
            String seg1 = segments[1].trim(); // no need for uppercase since not working with letters here
            String[] subsegs_of_seg1 = seg1.split(":");
            if (subsegs_of_seg1.length != 2) {
                System.out.println("Error: invalid lab slot");
                return false;
            } 

            // ~~~~~~~~~~ what is Integer.parseInt("1 1"); i would think an exception, but ill try it out when testing, also parseInt of a float?
            // make sure parseInt works for 00 and 0X, i'm pretty sure this works, note: in slot we will store the time as 0,1,2,...,10,11 (not 00, 01, but we can format this for output printing later)

            // must have HH:MM or H:MM
            if ((subsegs_of_seg1[0].length() != 1 && subsegs_of_seg1[0].length() != 2) || subsegs_of_seg1[1].length() != 2) {
                System.out.println("Error: invalid lab slot");
                return false;
            }

            try {
                startHour = Integer.parseInt(subsegs_of_seg1[0]);
                startMinute = Integer.parseInt(subsegs_of_seg1[1]);
            }
            catch (NumberFormatException e) {
                System.out.println("Error: invalid lab slot");
                return false;
            }

            // now make sure in range 00:00 to 23:59
            if (startHour < 0 || startHour > 23 || startMinute < 0 || startMinute > 59) {
                System.out.println("Error: invalid lab slot");
                return false;
            }
            // get here if times are valid times (but still need to check slot validity later)

            // third segment = labmax, 4th segment = labmin
            int labmax;
            int labmin;
            String seg2 = segments[2].trim(); // no need for uppercase
            String seg3 = segments[3].trim(); // no need for uppercase

            try {
                labmax = Integer.parseInt(seg2);
                labmin = Integer.parseInt(seg3);
            }
            catch (NumberFormatException e) {
                System.out.println("Error: invalid lab slot");
                return false;
            }

            if (labmax < 0 || labmin < 0 || (labmax < labmin)) { // ~~~~maybe if labmax < labmin, we output no valid solution instead?
                System.out.println("Error: invalid lab slot");
                return false;
            }
            
            // get here if all 4 fields are set properly



            String hashKey = day.name() + Integer.toString(startHour) + Integer.toString(startMinute); // ~~~~~~~~test that this is consistent
            int hashIndex = _mapSlotToIndex.size(); // thus the new index is the size of map. ex. it contains 3 slots already (0,1,2), now we want to put this 4th slot at index 3
            Slot newSlot = new Slot(hashKey, hashIndex, day, startHour, startMinute);
            newSlot._labmax = labmax;
            newSlot._labmin = labmin;
            if (!newSlot.checkSlotValidity()) {
                System.out.println("Error: invalid lab slot");
                return false;
            }
            
            if (_mapSlotToIndex.containsKey(hashKey)) { // if this slot was already found in file...

                int theIndex = _mapSlotToIndex.get(hashKey);

                if (_slotList.get(theIndex)._isLabSlot) { // if we found this slot already under lab slot header... (bad duplicate) 
                    System.out.println("Error: duplicate lab slot"); // ~~~~~~~~~~~~~~~~~~~~~~~~~Figure out what to do with duplicates~~~~~~~~~~~~~~~~~~~~~~~~~~
                    return false;
                }
                else { // we found this slot before (but it was under course slot header only)
                    // need to just overwrite these 3 fields (everything else is same)
                    _slotList.get(theIndex)._labmax = labmax;
                    _slotList.get(theIndex)._labmin = labmin;
                    _slotList.get(theIndex)._isLabSlot = true;
                }

            }
            else { // if this is the first time finding this slot in file (didn't find it before under course slots header)
                newSlot._isLabSlot = true; // flag that we found this slot under Lab slots header
                _mapSlotToIndex.put(hashKey, hashIndex); // record that we have found this slot in file (just in case it shows up again)
                _slotList.add(newSlot); // add this slot to end of list
            }
            
        }

        return true;
    }





// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~





    // return True if no error occurred...
    private boolean setCoursesData(List<String> table) {

        for (int i = 0; i < table.size(); i++) { // loop line by line...
            String line = table.get(i); // it is TRIMMED AND NON-BLANK string
            Course newCourse = getNewCourse(line);
            if (newCourse == null) {
                return false;
            }

            // get here if a valid course was constructed from this line...
            // now check that it is in fact a lecture...

            if (!newCourse._isLecture) {
                return false;
            }

            // get here if we have a valid lecture

            // ~~~~~~~~~~~~~~~~NOTE: currently duplicates are simply ignored

            String hashKey = newCourse._hashKey;
            int hashIndex = newCourse._hashIndex;

            if (!_mapCourseToIndex.containsKey(hashKey)) { // if this is the first time finding this lecture...
                _mapCourseToIndex.put(hashKey, hashIndex); // record that we have found this lecture in file (just in case it shows up again)
                _courseList.add(newCourse); // add this lecture to end of list
            }

        }

        return true;

    }



    // ~~~~~~~~~~~~~~~~~~~~~~~~~NOTE: Do we need to check for the lecture section part actually existing???? (for now, i'm not doing this)
    // ~~~~~~~~~~~~~~~~~~~~~~~~~what if we get CPSC 433 LEC 01 TUT 01 and CPSC 433 TUT 01 
    // ~~~~~~~~~~~~~~~~~~~~~~~~~TO ADD: not-compatible of this lab with same course Lec's and other labs, can use new hashmap + sharedKeys

    // return True if no error occurred...
    private boolean setLabsData(List<String> table) {

        for (int i = 0; i < table.size(); i++) { // loop line by line...
            String line = table.get(i); // it is TRIMMED AND NON-BLANK string
            Course newCourse = getNewCourse(line);
            if (newCourse == null) {
                return false;
            }

            // get here if a valid course was constructed from this line...
            // now check that it is in fact a lab...

            if (newCourse._isLecture) {
                return false;
            }

            // get here if we have a valid lab

            // ~~~~~~~~~~~~~~~~NOTE: currently duplicates are simply ignored
            // ~~~~~~~~~~~~~~~~NOTE: CPSC 433 TUT 01 == CPSC 433 LAB 01 (currently synonymous)

            String hashKey = newCourse._hashKey;
            int hashIndex = newCourse._hashIndex;

            if (!_mapCourseToIndex.containsKey(hashKey)) { // if this is the first time finding this lab...
                _mapCourseToIndex.put(hashKey, hashIndex); // record that we have found this lab in file (just in case it shows up again)
                _courseList.add(newCourse); // add this lab to end of list
            }

        }

        return true;

    }








    // TODO...


    // NOTE: ",CPSC 433 LEC 01" will break this currently since we pass in an empty left string, so update newCourse to return NULL on empty or null input. Also, might as well trim it to be safe

    // return True if no error occurred...
    private boolean setNotCompatibleData(List<String> table) {

        for (int i = 0; i < table.size(); i++) { // loop line by line
            String line = table.get(i); // is a non-blank trimmed line
            String[] segments = line.split(","); // 1. split line by commas
            if (segments.length != 2) {
                return false;
            }

            String seg0 = segments[0].trim();
            String seg1 = segments[1].trim();

            Course leftCourse = getNewCourse(seg0);
            Course rightCourse = getNewCourse(seg1);
            if (leftCourse == null || rightCourse == null) { // if either segment wasn't parsed into a course...
                return false;
            }

            // NOTE: don't use the new hashIndices, they will be wrong
            // now we can extract the hashKeys and lookup if they were actually defined before...
            // RULES: right now, if we find a non-defined course, just ignore this line ~~~~~~~~~~~~~~maybe change to print an error in future?
            // ALSO, if a duplicate line is given, no special treatment is needed since it will overwrite TRUE with TRUE (no change)

            String leftHashKey = leftCourse._hashKey;
            String rightHashKey = rightCourse._hashKey;

            if (!_mapCourseToIndex.containsKey(leftHashKey) || !_mapCourseToIndex.containsKey(rightHashKey)) { // if one of these 2 courses is undefined...
                continue; // ignore this line
            }

            // get here if both courses are defined...
            // now update array (symmetric)...

            int leftHashIndex = _mapCourseToIndex.get(leftHashIndex);
            int rightHashIndex = _mapCourseToIndex.get(rightHashIndex);

            _notCompatibles[leftHashIndex][rightHashIndex] = true; // flag both cells (symmetrically across diagonal)
            _notCompatibles[rightHashIndex][leftHashIndex] = true;            

        }

        return true;

    }















    // return True if no error occurred...
    private boolean setUnwantedData(List<String> table) {

        return true;
    }

    // return True if no error occurred...
    private boolean setPreferencesData(List<String> table) {

        return true;
    }

    // return True if no error occurred...
    private boolean setPairData(List<String> table) {

        return true;
    }

    // return True if no error occurred...
    private boolean setPartialAssignmentsData(List<String> table) {

        return true;
    }










    // helper methods...

    // returns T if all characters are letters (a-z,A-Z)
    // test this so it doesn't consider the terminating character (like \0) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private boolean isAlphabetic(String str) {

        if (str == null) {
            return false;
        }

        char[] chars = str.toCharArray();
        int count = 0; // number of letters found so far
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(chars[i])) {
                count++;
            }
            else {
                return false;
            }
        }

        if (count > 0) {
            return true;
        }
        else { // special case for empty string passed in
            return false;
        }
    }



    // returns T if all characters are digits (0-9)
    // test this so it doesn't consider the terminating character (like \0) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private boolean isNumeric(String str) {

        if (str == null) {
            return false;
        }

        char[] chars = str.toCharArray();
        int count = 0; // number of digits found so far
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(chars[i])) {
                count++;
            }
            else {
                return false;
            }
        }

        if (count > 0) {
            return true;
        }
        else { // special case for empty string passed in
            return false;
        }
    }





/*


    // Param: line (non-blank and trimmed string)
    // Return: NULL (if error occurred)
    // Return: Course instance created from this line
    private Course getNewCourse(String line) {

        String[] segments = line.split("\\s+"); // split line by whitespace

        if (segments.length == 4 || segments.length == 6) {

            // no need to trim a segment since the split by whitespace did that automatically
            // check if department identifier is alphabetic and if so make it uppercase and move on

            String department;
            String seg0 = segments[0];
            if (!isAlphabetic(seg0)) {
                return null;
            }    
            department = seg0.toUpperCase();

            // next check the number

            int number;
            String seg1 = segments[1];
            try {
                number = Integer.parseInt(seg1);
            }
            catch (NumberFormatException e) {
                return null;
            }

            if (number < 0) {
                return null;
            }

            // next check for LEC, LAB, TUT, LECLAB, or LECTUT (case-insensitive)

            Course.CourseType type;
            String seg2 = segments[2];
            if (segments.length == 4) {
                String typeStr1 = seg2.toUpperCase();
                switch(typeStr1) {
                    case "LEC":
                        type = Course.CourseType.LEC;
                        break;
                    case "LAB":
                        type = Course.CourseType.LAB;
                        break;
                    case "TUT":
                        type = Course.CourseType.TUT;
                        break;
                    default:
                        return null;
                }
            }
            else if (segments.length == 6) {
                String typeStr1 = seg2.toUpperCase();
                String seg4 = segments[4];
                String typeStr2 = seg4.toUpperCase();
                if (typeStr1.equals("LEC")) {
                    switch(typeStr2) {
                        case "LAB":
                            type = Course.CourseType.LECLAB;
                            break;
                        case "TUT":
                            type = Course.CourseType.LECTUT;
                            break;
                        default:
                            return null;
                    }
                }
                else {
                    return null;
                }
            }
            










            


            // next check for section

            int primarySection;
            String seg3 = segments[3];
            try {
                primarySection = Integer.parseInt(seg3);
            }
            catch (NumberFormatException e) {
                System.out.println("Error: invalid course");
                return false;
            }

            if (primarySection < 0) {
                System.out.println("Error: invalid course");
                return false;
            }

            // now we know we have a valid course (since we dont have to check that this course actually exists at the university)

            String hashKey = department + Integer.toString(number) + type.name() + Integer.toString(primarySection);
            int hashIndex = _mapCourseToIndex.size();
            Course newCourse = new Course(hashKey, hashIndex, department, number, type, primarySection);

            // if this course line was already found (duplicate, but no error (just dont add this course again))
            if (!_mapCourseToIndex.containsKey(hashKey)) { // thus, if this is the first time finding this course...
                _mapCourseToIndex.put(hashKey, hashIndex); // record that we have found this course in file (just in case it shows up again)
                _courseList.add(newCourse); // add this course to end of list
            }






















        }
        else {
            return null;
        }
    
    }

*/














    // Param: line (non-blank and trimmed string)
    // Return: NULL (if error occurred)
    // Return: Course instance created from this line
    private Course getNewCourse(String line) {

        String[] segments = line.split("\\s+"); // split line by whitespace

        if (segments.length == 4 || segments.length == 6) {

            // no need to trim a segment since the split by whitespace did that automatically
            // 1. DEPARTMENT 
            // RULES: any alphabetical string will be accepted AS IS - case-sensitive

            String department;
            String seg0 = segments[0];
            if (!isAlphabetic(seg0)) { // VIOLATION: non-alphabetic
                return null;
            }
            department = seg0;



            // 2. NUMBER
            // RULES: any numerical string will be accepted AS IS

            String number;
            String seg1 = segments[1];
            if (!isNumeric(seg1)) { // VIOLATION: non-numeric
                return null;
            }
            number = seg1;



            // 3. PRIMARYTYPE
            // RULES: either exactly "LEC", "LAB", "TUT" 

            Course.PrimaryType primaryType;
            String seg2 = segments[2];
            switch(seg2) {
                case "LEC":
                    primaryType = Course.PrimaryType.LEC;
                    break;
                case "LAB":
                    primaryType = Course.PrimaryType.LAB;
                    break;
                case "TUT":
                    primaryType = Course.PrimaryType.TUT;
                    break;
                default:
                    return null;
            }



            // 4. PRIMARYSECTION
            // RULES: any numerical string will be accepted AS IS

            String primarySection;
            String seg3 = segments[3];
            if (!isNumeric(seg3)) { // VIOLATION: non-numeric
                return null;
            }
            primarySection = seg3;



            if (segments.length == 4) { // 4 segments (DONE)
                // now we have a valid course...
                int hashIndex = _mapCourseToIndex.size();
                Course newCourse = new Course(hashIndex, department, number, primaryType, primarySection);
                return newCourse;
            }
            else { // 6 segments
                // 5. SECONDARYTYPE
                // RULES: either exactly "LAB" or "TUT", but primarytype must be "LEC"

                if (primaryType != Course.PrimaryType.LEC) {
                    return null;
                }

                Course.SecondaryType secondaryType;
                String seg4 = segments[4];
                switch(seg4) {
                    case "LAB":
                        secondaryType = Course.SecondaryType.LAB;
                        break;
                    case "TUT":
                        secondaryType = Course.SecondaryType.TUT;
                        break;
                    default:
                        return null;
                }



                // 6. SECONDARYSECTION
                // RULES: any numerical string will be accepted AS IS

                String secondarySection;
                String seg5 = segments[5];
                if (!isNumeric(seg5)) { // VIOLATION: non-numeric
                    return null;
                }
                secondarySection = seg5;



                // now we have a valid course...
                int hashIndex = _mapCourseToIndex.size();
                Course newCourse = new Course(hashIndex, department, number, primaryType, primarySection, secondaryType, secondarySection);
                return newCourse;
            }
            
        }
        else { // VIOLATION: segment count
            return null;
        }

    }

}





// NOTE: if a line specifies constraints on a course that supposedly was written on a line before, but wasn't, then just ignore the line