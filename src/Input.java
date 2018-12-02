


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
    X what if Example-name is not specified? (look at 449 case)
    ~ should we have a general error message if theres a typo in file?
    X error for if a keyword missing? (look at 449)
    X is LEC01, LEC0 1,  possible? - no this will be a parse error
    X do we need a blank line before next keyword like 449? - i'm saying no, it doesnt matter
    - what do we do if we are given CPSC 433 LEC 01 is not compatible with CPSC 433 LEC 01 (I would say throw an error since you would never get a valid answer)
    - if one line is CPSC 433 LAB 01 and another line is CPSC 433 TUT 01, do we treat them as the same thing?
    - EVENING CLASSES: is it only LEC 09 or is it >= 9?, is CPSC 433 LEC 09 TUT 01 evening? - i dont think so since its a tut not a 'course section' = lecture

    ANSWERS:
    - name can't be a blank line or a keyword
    X he expects the parsing to go character by character L->R (NL understanding) - not doing it this way since we dont have specific parse error msgs
    X this assignment seems to be less focused on the parser, and more on the algorithm (there isnt special error messages), can just have 1 general error msg

*/

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;


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

    // fields...

    public String _name;

    // have these 
    public Map<String, Integer> _mapSlotToIndex = new HashMap<>(); // init size 0, get index j for a slot
    public Map<String, Integer> _mapCourseToIndex = new HashMap<>(); // init size 0, get index i for a course

    public List<Slot> _slotList = new ArrayList<>(); // add a slot TO END everytime input file specifies a new slot that wasnt mentioned before, NOTE: AND-Tree class will use this to expand leaves
    public List<Course> _courseList = new ArrayList<>(); // add a course TO END everytime input file specifies a new course that wasnt mentioned before

    // this array is symmetric across diagonal
    // later on init the size to be square at SxS where S = size of _mapCourseToIndex (number of keys = number of courses to schedule)
    public boolean[][] _notCompatibles; // default init is false, set _notCompatibles[i][j] = true and _notCompatibles[j][i] = true, if we get line COURSE1, COURSE2 (in not-compatible table) and index(Course1) = i and index(course2) = j

    // later on init the size to be [S][N] where S = size of _mapCourseToIndex (number of courses) and N = size of _mapSlotToIndex (number of available slots)
    public boolean[][] _unwanteds; // [i][j] = true if course i can't be assigned to slot j (rows are courses, cols are slots)

    // WATCH OUT! (the line is read L->R with Slot -> course which makes you want to put i for slot and j for course, but to keep consisten with other data structures, we use i for course and j for slot)
    // IT MUST BE A NATURAL NUMBER
    // set size to be SxN like _unwanteds
    public int[][] _preferences; // [i][j] = x if preference value of course i with slot j is x, 0 by default

    // this array is symmetric across diagonal
    // init size as SxS (S = number of courses)
    public boolean[][] _pairs; // [i][j] = true if course i is paired with course j, false by default, maybe init diagonal to be true (reflexive property of relation), nah not gonna do this since it would cause unnecesaey checks that would always be true

    // init to size S (S = number of courses)
    // represent $ as NULL
    public Slot[] _partialAssignments; // [i] = sl, means that course i must assign to slot sl, NULL means no assignment ($)



    // e.g. CPSC 433 LEC 01, CPSC 433 LEC 02 both have sharedHashKey "CPSC:433" and thus .get("CPSC:433") will return a list of size 2. 2 indices i,j each for these 2 course objects
    private Map<String, ArrayList<Integer>> _mapClassIDToListOfLecs = new HashMap<>(); 

    // e.g. CPSC 433 TUT 01, CPSC 433 LEC 01 TUT 02 both have sharedHashKey "CPSC:433" and thus .get("CPSC:433") will return a list of size 2. 2 indices i,j each for these 2 course objects
    private Map<String, ArrayList<Integer>> _mapClassIDToListOfLabsTuts = new HashMap<>();

    private List<Pair<Integer,Integer>> _builtInNotCompats = new ArrayList<Pair<Integer,Integer>>(); // post process will use this to update not-compatible array for lecs and labs of same sharedHashKey

    // IDEAS:

    /*
    IDEA 1:

    A HashMap<String, Integer> with (e.g. key = "CPSC 433 LEC 01") and value = i (where this was the ith (from 0) class found in input file reading top to bottom)

    */

    // we are given a new course/lab identifier on a line, we have that unique string (after some magic like trimming), convert it 

    // Have an ArrayList<ArrayList<Lab>> which is has the outer list ordered from 0 to m-1, and an inner list element ordered from 0 to k-1 - thus list[i] is the list of labs/tuts connected to course c_i. If k=0 then have {} empty list

    // INSIDE ALGORITHM:
    // have a vector of size S (S = number of courses)
    // ith element corresponds to current slot assignment to course with index i

    // NOTE: ~~~~~~~~for algorithm, keep track of best assignment as a copy of the arraylsit of slots and have the EVAL stored.

    // 10 keynames to find...
    private boolean _nameKeyFound, _courseSlotsKeyFound, _labSlotsKeyFound, _coursesKeyFound, _labsKeyFound, _notCompatibleKeyFound, _unwantedKeyFound, _preferencesKeyFound, _pairKeyFound, _partialAssignmentsKeyFound;

    private boolean _courseSlotDefined, _labSlotDefined, _courseDefined, _labDefined; // all init are false

    // methods...

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
        // make sure we don't have a missing keyword...
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

        if (!_courseSlotDefined && !_labSlotDefined) { // if 0 course slots and 0 lab slots were defined...
            System.out.println("Error: No slots were defined.");
            return false;
        }


        if (!setCoursesData(coursesTable)) {
            System.out.println("Error: Invalid courses data");
            return false;
        }

        if (_courseDefined && !_courseSlotDefined) { // if >= 1 courses were defined but 0 course slots were defined...
            System.out.println("Error: Courses were defined, but no course slots were defined.");
            return false;
        }


        if (!setLabsData(labsTable)) {
            System.out.println("Error: Invalid labs data");
            return false;
        }

        if (_labDefined && !_labSlotDefined) { // if >= 1 labs were defined but 0 lab slots were defined...
            System.out.println("Error: Labs were defined, but no lab slots were defined.");
            return false;
        }

        if (!_courseDefined && !_labDefined) { // if 0 courses and 0 labs were defined...
            System.out.println("Error: No courses/labs were defined.");
            return false;
        }

        
        // IF EVERTHING IS GOOD...

        // init sizes of the arrays...

        int n = _slotList.size(); // N = number of possible slots
        int s = _courseList.size(); // S = number of courses (Lectures + labs/tuts) to schedule

        _notCompatibles = new boolean[s][s]; // symmetric
        _unwanteds = new boolean[s][n];
        _preferences = new int[s][n];
        _pairs = new boolean[s][s]; // symmetric
        _partialAssignments = new Slot[s];

        // MAYBE do any other initializtion?

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

        // all explicit data set correctly...

        if (!postProcessData()) {
            System.out.println("NO VALID SOLUTION");
            return false;
        }

        // get here if no errors occurred...

        return true; // default return
    }	
    


    // ~~~~~~~~~~~~~~~~NOTE: remove the error msgs from these functions or only have them for debug mode?



    // return True if no error occurred...
    private boolean setNameData(List<String> table) {

        if (table.size() == 0) {
            System.out.println("ERROR: missing name"); 
            return false;
        }
        else if (table.size() > 1) {
            System.out.println("ERROR: invalid extra line under Name:");
            return false;
        }
        else { // if 1 line
            // check that this line is not a keyword, if so then return false
            theName = table.get(0);
            switch(theName) {
                case "Name:":
                case "Course slots:":
                case "Lab slots:":
                case "Courses:":
                case "Labs:":
                case "Not compatible:":
                case "Unwanted:":
                case "Preferences:":
                case "Pair:":
                case "Partial assignments:":
                    System.out.println("ERROR: name cannot be a keyword, " + theName);
                    return false;
                default:
                    _name = theName;
            }
        }
        return true;
    }



    // return True if no error occurred...
    private boolean setCourseSlotsData(List<String> table) {

        if (table.size() == 0) {
            return true; // emptiness errors are checked externally
        }

        for (int i = 0; i < table.size(); i++) { // loop line by line...
            String line = table.get(i); // it is TRIMMED AND NON-BLANK string
            String[] segments = line.split(","); // split line by commas
            
            if (segments.length != 4) { // make sure line has 4 parts
                return false;
            }

            String dayString = segments[0].trim();
            String timeString = segments[1].trim();
            String coursemaxString = segments[2].trim();
            String courseminString = segments[3].trim();

            Slot newSlot = getNewSlot(dayString, timeString, true, true);
            if (newSlot == null) {
                return false;
            }
    
            // now set the max/min...
            // RULES: each must be a non-negative int (e.g. even 0000001 will be accepted as 1)

            int coursemax;
            int coursemin;

            try {
                coursemax = Integer.parseInt(coursemaxString);
                coursemin = Integer.parseInt(courseminString);
            }
            catch (NumberFormatException e) {
                return false;
            }

            if (coursemax < 0 || coursemin < 0 || (coursemax < coursemin)) {
                return false;
            }

            newSlot._coursemax = coursemax;
            newSlot._coursemin = coursemin;

            String hashKey = newSlot._hashKey;
            int hashIndex = newSlot._hashIndex;

            // NOTE: I don't think I need to check slot validity again?

            // NOTE: due to the order of the input file, the only way this slot was already found, is that it was under course slots header (_courseSlot = true, so no need to check this flag)
            // RIGHT NOW duplicates are treated as an error, since they could list differing max/min
            if (_mapSlotToIndex.containsKey(hashKey)) { 
                return false;
            }
             
            // get here if this is the first time this slot has been found in file...
             
            _mapSlotToIndex.put(hashKey, hashIndex); // record that we have found this slot in file (just in case it shows up again)
            _slotList.add(newSlot); // add this slot to end of list
            _courseSlotDefined = true; // flag that at least 1 course slot was found

        }

        return true;
    }


    
    // pretty close to setCourseSlotsData()
    // return True if no error occurred...
    private boolean setLabSlotsData(List<String> table) {

        if (table.size() == 0) {
            return true; // emptiness errors are checked externally
        }

        for (int i = 0; i < table.size(); i++) { // loop line by line...
            String line = table.get(i); // it is TRIMMED AND NON-BLANK string
            String[] segments = line.split(","); // split line by commas
            
            if (segments.length != 4) { // make sure line has 4 parts
                return false;
            }

            String dayString = segments[0].trim();
            String timeString = segments[1].trim();
            String labmaxString = segments[2].trim();
            String labminString = segments[3].trim();

            Slot newSlot = getNewSlot(dayString, timeString, false, true);
            if (newSlot == null) {
                return false;
            }
    
            // now set the max/min...
            // RULES: each must be a non-negative int (e.g. even 0000001 will be accepted as 1)

            int labmax;
            int labmin;

            try {
                labmax = Integer.parseInt(labmaxString);
                labmin = Integer.parseInt(labminString);
            }
            catch (NumberFormatException e) {
                return false;
            }

            if (labmax < 0 || labmin < 0 || (labmax < labmin)) { 
                return false;
            }

            String hashKey = newSlot._hashKey;

            // NOTE: 2 ways of hashkey already existing...
            // 1. only found before under CourseSlots (good, just overwrite lab fields)
            // 2. found before under LabSlots (BAD, - duplicate definition)
            // RIGHT NOW duplicates are treated as an error, since they could list differing max/min
            if (_mapSlotToIndex.containsKey(hashKey)) {
                int exHashIndex = _mapCourseToIndex.get(hashKey); // get the existing hash index
                if (_slotList.get(exHashIndex)._isLabSlot) { // if we have a duplicate lab slot definition...
                    return false;
                }
                else { // we already only found this slot under CourseSlots...
                    // just need to update the lab fields...
                    _slotList.get(exHashIndex)._labmax = labmax;
                    _slotList.get(exHashIndex)._labmin = labmin;
                    _slotList.get(exHashIndex)._isLabSlot = true;
                }
            }
             
            // get here if this is the first time this slot has been found in file...

            int hashIndex = newSlot._hashIndex;

            newSlot._labmax = labmax;
            newSlot._labmin = labmin;
             
            _mapSlotToIndex.put(hashKey, hashIndex); // record that we have found this slot in file (just in case it shows up again)
            _slotList.add(newSlot); // add this slot to end of list
            _labSlotDefined = true; // flag that at least 1 lab slot was found

        }

        return true;
    }



    // return True if no error occurred...
    private boolean setCoursesData(List<String> table) {

        if (table.size() == 0) {
            return true; // emptiness errors are checked externally
        }

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

            // NOTE: currently lecture duplicates are simply ignored
            // i'm gonna change it so that it prints out a warning for duplicate lectures, but it will ignore the duplicate and continue

            String hashKey = newCourse._hashKey;
            int hashIndex = newCourse._hashIndex;

            if (!_mapCourseToIndex.containsKey(hashKey)) { // if this is the first time finding this lecture...
                _mapCourseToIndex.put(hashKey, hashIndex); // record that we have found this lecture in file (just in case it shows up again)
                _courseList.add(newCourse); // add this lecture to end of list
                _courseDefined = true; // flag that at least 1 course was found

                String sharedHashKey = newCourse._sharedHashKey; // e.g. "CPSC:433"
                if (!_mapClassIDToListOfLecs.containsKey(sharedHashKey)) { // e.g. is this is the first time finding a lec for CPSC 433...
                    ArrayList<Integer> newList = new ArrayList<>(); // creating the list for the first time
                    newList.add(hashIndex); // keep track of the index of this lecture section for CPSC 433
                    _mapClassIDToListOfLecs.put(sharedHashKey, newList);
                }
                else { // if the arraylist was already created by a previous LEC section of e.g. CPSC433, then we just add to the list
                    _mapClassIDToListOfLecs.get(sharedHashKey).add(hashIndex); // add this section to list
                }
            }
            else { // if this is a duplicate lecture definition...
                System.out.println("WARNING: duplicate course listed, " + newCourse._outputID); // print warning and continue
            }

        }

        return true;
    }


    
    // UPDATES: now if we get CPSC 433 TUT 01, it returns an error if file didnt define any LEC for CPSC 433
    // if no error occurs, then we mark this lab not-compat with every lec section defined.
    // now if we get CPSC 433 LEC 01 TUT 02, it returns an error if file didnt define CPSC 433 LEC 01
    // if it was defined, then we mark this lab and its corresponding lec section as not-compat.
    // NOTE: these not-compats will be formally set in the 2D array in postProcess()

    // UPDATE 2:
    // now stuff like CPSC 433 LEC 01 TUT 01 and CPSC 433 LAB 01 both being defined (same tut/lab section number) will return an error.

    // return True if no error occurred...
    private boolean setLabsData(List<String> table) {

        if (table.size() == 0) {
            return true; // emptiness errors are checked externally
        }

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

            // I am changing it so that a duplicate like: CPSC 433 LAB 01 and CPSC 433 LAB 01 will print a warning but will get ignored
            // and a duplicate of CPSC 433 LAB 01 and CPSC 433 TUT 01 will return an error 
            // NOTE: CPSC 433 TUT 01 == CPSC 433 LAB 01 (synonymous)

            String hashKey = newCourse._hashKey;
            int hashIndex = newCourse._hashIndex;

            if (!_mapCourseToIndex.containsKey(hashKey)) { // if this is the first time finding this lab...

                String sharedHashKey = newCourse._sharedHashKey; // e.g. "CPSC:433"
                if (!_mapClassIDToListOfLabsTuts.containsKey(sharedHashKey)) { // e.g. is this is the first time finding a lab/tut for CPSC 433...
                    // no tuturial section number clashes to check for here
                    ArrayList<Integer> newList = new ArrayList<>(); // creating the list for the first time
                    newList.add(hashIndex); // keep track of the index of this lab/tut section for CPSC 433
                    _mapClassIDToListOfLabsTuts.put(sharedHashKey, newList);
                }
                else { // if the arraylist was already created by a previous LAB/TUT section of e.g. CPSC433, then we just add to the list
                    // before adding to list we must check that this newCourse's tutorial/lab section number is different from all the other tuts/labs of same class (sharedHashkey)
                    ArrayList<Integer> sharedLabsList = _mapClassIDToListOfLabsTuts.get(sharedHashKey); // get the list of tuts/labs sharing this class
                    for(int j = 0; j < sharedLabsList.size(); j++) { // loop through all shared labs
                        int otherLabIndex = sharedLabsList.get(j); // get index of next lab to check
                        Course otherLab = _courseList[otherLabIndex]; // retrieve reference to the instance

                        // now compare the tutorial section strings
                        String newCourseLabSection = newCourse.SecondaryType == Course.SecondaryType.NONE ? newCourse._primarySection : newCourse._secondarySection;
                        String otherLabLabSection = otherLab.SecondaryType == Course.SecondaryType.NONE ? otherLab._primarySection : otherLab._secondarySection;

                        if (newCourseLabSection.equals(otherLabLabSection)) {
                            System.out.println("ERROR: 2 different labs/tutorials of the same class cannot have the same lab number, " + otherLab._outputID + " & " + newCourse._outputID);
                            return false;
                        }

                    } 

                    // otherwise, we get here and no shared labs/tuts conflicted with this new labs tutorial section number

                    _mapClassIDToListOfLabsTuts.get(sharedHashKey).add(hashIndex); // add this section to list
                }

                // now we have a lab, but we need to check for LECs of same sharedHashKey
                // case 1) if we have CPSC 433 TUT 01, we need at least 1 LEC of CPSC 433 in file
                // could then also set not-compatibility here
                // case 2) if we have CPSC 433 LEC 02 TUT 01, we need to have CPSC 433 LEC 02 in file
                // could also set not-compat here

                // NOTE: can change it so we have if (4seg) then check if null, if not set NC with whole list, else if (6seg) check if specific lec is there and set NC 

                ArrayList<Integer> sharedLecsList = _mapClassIDToListOfLecs.get(sharedHashKey);

                if (newCourse._secondaryType == Course.SecondaryType.NONE) { // if 4 segments...
                    if (sharedLecsList == null) { // if no LECS were defined with this shared key...
                        System.out.println("ERROR: lab/tutorial defined without any lecture section for their shared class identifier, " + newCourse._outputID);
                        return false;
                    }
                    else { // so we have found a list which we know was initialized to size 1 when it was first put in map. And it could have grown with the addition of more LECS
                        // set this newCourse not-compatible with every LEC in this list
                        for(int j = 0; j < sharedLecsList.size(); j++) {
                            int nextCourseIndex = sharedLecsList.get(j); // get index of next LEC in list
                            _builtInNotCompats.add(new Pair<Integer,Integer>(hashIndex, nextCourseIndex));
                            _builtInNotCompats.add(new Pair<Integer,Integer>(nextCourseIndex, hashIndex)); // for NC symmetry
                            // then in postprocess, we can iterate through this list and set true in boolean area for each pair.
                        }
                    }
                }
                else { // if 6 segments...
                    // now if this newCourse (lab) is of the form e.g. CPSC 433 LEC 02 TUT 01 (6 segments), we must find CPSC 433 LEC 02
                    if (sharedLecsList == null) { // if no LECS were defined with this shared key...
                        System.out.println("ERROR: lab/tutorial defined without its corresponding lecture section, " + newCourse._outputID);
                        return false;
                    }
                    else {
                        // search through list to try to find corresponding lec and the push not-compatible pairs
                        boolean candidateFound = false;
                        for(int j = 0; j < sharedLecsList.size(); j++) {
                            int nextCourseIndex = sharedLecsList.get(j); // get index of next LEC in list
                            Course candidate = _courseList[nextCourseIndex]; // get the instance

                            if (candidate._primarySection.equals(newCourse._primarySection)) { // e.g. if 02 == 02
                                // we found the corresponding lecture section...
                                candidateFound = true; // update flag to prevent error
                                _builtInNotCompats.add(new Pair<Integer,Integer>(hashIndex, nextCourseIndex));
                                _builtInNotCompats.add(new Pair<Integer,Integer>(nextCourseIndex, hashIndex)); // for NC symmetry
                                // then in postprocess, we can iterate through this list and set true in boolean area for each pair.
                            }

                        }

                        if (!candidateFound) {
                            System.out.println("ERROR: lab/tutorial defined without its corresponding lecture section, " + newCourse._outputID);
                            return false;
                        }
                    }

                }

                // get here with this lab/tut not causing any problems

                _mapCourseToIndex.put(hashKey, hashIndex); // record that we have found this lab in file (just in case it shows up again)
                _courseList.add(newCourse); // add this lab to end of list
                _labDefined = true; // flag that at least 1 lab was found

            }
            else { // if this is a duplicate lab/lab or tut/tut or lab/tut or tut/lab definition...
                int exHashIndex = _mapCourseToIndex.get(hashKey); // get the existing index
                Course oldLab = _courseList[exHashIndex]; // retrieve reference to the lab we already found sharing this hashkey

                // NOTE: the only way oldLab and newCourse would have matching hashkeys is if they have the same number of segments
                if (newCourse._secondaryType == Course.SecondaryType.NONE) { // 4 segments
                    if (newCourse._primaryType == oldLab._primaryType) { // case 1: LAB/LAB or TUT/TUT (warning only)
                        System.out.println("WARNING: duplicate lab/tutorial listed, " + newCourse._outputID); // print warning and continue
                    }
                    else { // case 2: LAB/TUT or TUT/LAB (error)
                        System.out.println("ERROR: lab/tutorial defined with both LAB and TUT, " + oldLab._outputID + " & " newCourse._outputID); // print error and return
                        return false;
                    }
                }
                else { // 6 segments
                    if (newCourse._secondaryType == oldLab._secondaryType) { // case 1: LAB/LAB or TUT/TUT (warning only)
                        System.out.println("WARNING: duplicate lab/tutorial listed, " + newCourse._outputID); // print warning and continue
                    }
                    else { // case 2: LAB/TUT or TUT/LAB (error)
                        System.out.println("ERROR: lab/tutorial defined with both LAB and TUT, " + oldLab._outputID + " & " newCourse._outputID); // print error and return
                        return false;
                    }
                }

            }

        }

        return true;
    }



    // return True if no error occurred...
    private boolean setNotCompatibleData(List<String> table) {

        if (table.size() == 0) { // no error for 0 not comaptibles
            return true; 
        }

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

            int leftHashIndex = _mapCourseToIndex.get(leftHashKey);
            int rightHashIndex = _mapCourseToIndex.get(rightHashKey);

            _notCompatibles[leftHashIndex][rightHashIndex] = true; // flag both cells (symmetrically across diagonal)
            _notCompatibles[rightHashIndex][leftHashIndex] = true;            

        }

        return true;
    }



    // return True if no error occurred...
    private boolean setUnwantedData(List<String> table) {

        if (table.size() == 0) { // no error for 0 unwanteds
            return true; 
        }

        for (int i = 0; i < table.size(); i++) { // loop line by line
            String line = table.get(i); // is a non-blank trimmed line
            String[] segments = line.split(","); // 1. split line by commas
            if (segments.length != 3) {
                return false;
            }

            String seg0 = segments[0].trim();
            String seg1 = segments[1].trim();
            String seg2 = segments[2].trim();

            Course courseInfo = getNewCourse(seg0);

            if (courseInfo == null) { // if this string couldn't be parsed...
                return false;
            }

            // get here if course fits format

            Slot slotInfoLEC = getNewSlot(seg1, seg2, true, false); // no verification!
            Slot slotInfoLAB = getNewSlot(seg1, seg2, false, false); // no verification!

            if (slotInfoLEC == null && slotInfoLAB == null) { // if these 2 strings couldn't be parsed (violated format)
                return false;
            }

            // get here if slot fits either courseslot format or labslot format or both

            // now that we have checked for typos, we can check if the course is defined in our file

            String courseHashKey = courseInfo._hashKey;

            if (!_mapCourseToIndex.containsKey(courseHashKey)) { // if line has a valid course, but wasn't defined in our file, we can skip it
                continue; // skip this line
            }
            // get here if it is defined so we can get its hashIndex
            int courseHashIndex = _mapCourseToIndex.get(courseHashKey);

            // Now we can use the hashmap instead to check validity (since only valid and defined slots got hashed) 

            // use one of the infos that isn't null (since we know theres at least 1, if both are !null, then we can use either since we just need the hashkey which is the same)
            String slotHashKey;
            if (slotInfoLEC != null) {
                slotHashKey = slotInfoLEC._hashKey;
            }
            else { // slotInfoLAB != null
                slotHashKey = slotInfoLAB._hashKey;
            }

            if (!_mapSlotToIndex.containsKey(slotHashKey)) { // slot fits format, but it isn't VALID or DEFINED
                continue; // skip this line
            }
            // get here if it is valid and defined, so we can get its hashIndex
            int slotHashIndex = _mapSlotToIndex.get(slotHashKey);

            // flag this course and this slot as unwanted

            _unwanteds[courseHashIndex][slotHashIndex] = true;

        }    

        return true;
    }



    // return True if no error occurred...
    private boolean setPreferencesData(List<String> table) {

        if (table.size() == 0) { // no error for 0 not preferences
            return true; 
        }

        for (int i = 0; i < table.size(); i++) { // loop line by line
            String line = table.get(i); // is a non-blank trimmed line
            String[] segments = line.split(","); // 1. split line by commas
            if (segments.length != 4) {
                return false;
            }

            String seg0 = segments[0].trim(); // day
            String seg1 = segments[1].trim(); // time
            String seg2 = segments[2].trim(); // course identifier
            String seg3 = segments[3].trim(); // preference value

            Slot slotInfoLEC = getNewSlot(seg0, seg1, true, false); // no verification!
            Slot slotInfoLAB = getNewSlot(seg0, seg1, false, false); // no verification!

            if (slotInfoLEC == null && slotInfoLAB == null) { // if these 2 strings couldn't be parsed (violated format)
                return false;
            }
            // get here if slot fits either courseslot format or labslot format or both

            Course courseInfo = getNewCourse(seg2);

            if (courseInfo == null) { // if this string couldn't be parsed...
                return false;
            }
            // get here if course fits format

            int prefValue;
            try {
                prefValue = Integer.parseInt(seg3);
            }
            catch (NumberFormatException e) {
                return false;
            }

            if (prefValue < 0) {
                return false;
            }

            // get here if prefValue fits format (RULE: any string that can be parsed as a natural number)
            
            // now that we have checked for typos, we can check VALIDITY/DEFINITION of slot then course

            // Now we can use the hashmap instead to check validity (since only valid and defined slots got hashed) 

            // use one of the infos that isn't null (since we know theres at least 1, if both are !null, then we can use either since we just need the hashkey which is the same)

            String slotHashKey;
            if (slotInfoLEC != null) {
                slotHashKey = slotInfoLEC._hashKey;
            }
            else { // slotInfoLAB != null
                slotHashKey = slotInfoLAB._hashKey;
            }

            if (!_mapSlotToIndex.containsKey(slotHashKey)) { // slot fits format, but it isn't VALID or DEFINED
                continue; // skip this line
            }
            // get here if it is valid and defined, so we can get its hashIndex
            int slotHashIndex = _mapSlotToIndex.get(slotHashKey);


            // now check for course definition
            String courseHashKey = courseInfo._hashKey;

            if (!_mapCourseToIndex.containsKey(courseHashKey)) { // if line has a valid course, but wasn't defined in our file, we can skip it
                continue; // skip this line
            }
            // get here if it is defined so we can get its hashIndex
            int courseHashIndex = _mapCourseToIndex.get(courseHashKey);


            // ~~~~~~~~~~~~~~~~~~~~~~~NOTE: currently if we get a duplicate line, we will just treat it as an overwrite.

            // update pref value...
            _preferences[courseHashIndex][slotHashIndex] = prefValue;

        }    

        return true;        
    }



    // return True if no error occurred...
    private boolean setPairData(List<String> table) {

        if (table.size() == 0) { // no error for 0 pairs
            return true; 
        }
        
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

            int leftHashIndex = _mapCourseToIndex.get(leftHashKey);
            int rightHashIndex = _mapCourseToIndex.get(rightHashKey);

            _pairs[leftHashIndex][rightHashIndex] = true; // flag both cells (symmetrically across diagonal)
            _pairs[rightHashIndex][leftHashIndex] = true;            

        }

        return true;
    }



    // QUESTION: ~~~~~~~~~~~~~~~~~~~CAN 813/913 be found normally in the file???? - i'm assuming not

    // return True if no error occurred...
    private boolean setPartialAssignmentsData(List<String> table) {

        if (table.size() == 0) { // no error for 0 partassigns
            return true; 
        }

        for (int i = 0; i < table.size(); i++) { // loop line by line
            String line = table.get(i); // is a non-blank trimmed line
            String[] segments = line.split(","); // 1. split line by commas
            if (segments.length != 3) {
                return false;
            }

            String seg0 = segments[0].trim(); // course ID
            String seg1 = segments[1].trim(); // day
            String seg2 = segments[2].trim(); // time

            Course courseInfo = getNewCourse(seg0);

            if (courseInfo == null) { // if this string couldn't be parsed...
                return false;
            }

            // get here if course fits format

            Slot slotInfoLEC = getNewSlot(seg1, seg2, true, false); // no verification!
            Slot slotInfoLAB = getNewSlot(seg1, seg2, false, false); // no verification!

            if (slotInfoLEC == null && slotInfoLAB == null) { // if these 2 strings couldn't be parsed (violated format)
                return false;
            }

            // get here if slot fits either courseslot format or labslot format or both

            // now that we have checked for typos, we can check if the course is defined in our file

            String courseHashKey = courseInfo._hashKey;

            if (!_mapCourseToIndex.containsKey(courseHashKey)) { // if line has a valid course, but wasn't defined in our file, we have a fatal error
                return false;
            }
            // get here if it is defined so we can get its hashIndex
            int courseHashIndex = _mapCourseToIndex.get(courseHashKey);

            // Now we can use the hashmap instead to check validity (since only valid and defined slots got hashed) 

            // use one of the infos that isn't null (since we know theres at least 1, if both are !null, then we can use either since we just need the hashkey which is the same)
            String slotHashKey;
            if (slotInfoLEC != null) {
                slotHashKey = slotInfoLEC._hashKey;
            }
            else { // slotInfoLAB != null
                slotHashKey = slotInfoLAB._hashKey;
            }

            if (!_mapSlotToIndex.containsKey(slotHashKey)) { // slot fits format, but it isn't VALID or DEFINED
                return false;
            }
            // get here if it is valid and defined, so we can get its hashIndex
            int slotHashIndex = _mapSlotToIndex.get(slotHashKey);

            // now we have a defined course and a defined slot

            // make sure that there is no mismatch of course with pure labslot and lab with pure courseslot...

            Course theCourse = _courseList.get(courseHashIndex);
            Slot theSlot = _slotList.get(slotHashIndex);

            if ((theCourse._isLecture && !theSlot._isCourseSlot) || (!theCourse._isLecture && !theSlot._isLabSlot)) { // if we are assigning a course to a pure lab slot OR a lab to a pure course slot
                System.out.println("ERROR: partial assignment mismatch of course to slot type (course assigned to lab slot or lab assigned to course slot.)");
                return false;
            }

            // now must check if this course has already been partassigned in a previous line (error if we have a duplicate)

            if (_partialAssignments[courseHashIndex] != null) { // if this is a duplicate or a different assignment...
                System.out.println("ERROR: partial assignment of a course has multiple definitions.");
                return false;
            }

            // get here if everything is OK...

            // can now assign this slot to course index of list

            _partialAssignments[courseHashIndex] = theSlot;

        }    

        return true;

    }








    // use this method after all the setData methods are called to do any other post processing to the data structures (like checking for contradictions that result in NO VALID SOLUTION)
    // return FALSE if we can identify that we have NO VALID SOLUTION right now
    private boolean postProcessData() {

        // ~~~~~~~~~~~~NOTE: maybe just change this so we overwrite coursemax only
        // I think it should stay as overwriting both, because the hard constraint is that we require EXCATLY 0 (min=max=0) courses in this slot
        //5. if _mapSlotToIndex contains hashkey for TU 11:00, then overwrite coursemax=coursemin=0
        // make sure to test this is the proper hashkey...
        String tu11HashKey = "TU:11:00"
        if (_mapSlotToIndex.containsKey(tu11HashKey)) {
            int tu11HashIndex = _mapSlotToIndex.get(tu11HashKey);
            Slot tu11Slot = _slotList.get(tu11HashIndex); // retrieve reference to the slot
            tu11Slot._coursemax = 0;
            tu11Slot._coursemin = 0;
        }

        //3. make sure courses and labs of same class are properly init as not-compatible according to specs (ex. have the sharedHashMap and use the sharedHaskkey to set this list right here)
        for (Pair<Integer,Integer> pair : _builtInNotCompats) {
            int indexL = pair.getKey();
            int indexR = pair.getValue();
            _notCompatibles[indexL][indexR] = true;
        }



        // could split the slotlist into 3 lists: COURSESLOTS, LABSLOTS, BOTHSLOTS
        // then the algortihm would be more efficient since if the $ was for a courseslot, then we would only expand a leaf into courseslot permutations.
        // but that is an optimization thing that is just saving the steps of checking hard constrait max=0
        // this would cut the tree in half though...
        // or just re parse into 2 lists and then the end time can be easily set and then i can have a function here that sets the overlaps


        /*
        STUFF TO DO HERE...

        1.  - OR i could just use a 2D boolean NxN array _slotOverlaps which would be symmetric
            -create private field in this class: ArrayList<Slot>[] _slotOverlaps; which should be initialized to size N (number of slots), [i] corresponds to the list of overlapping slots with slot i
            -inside Slot.java create a function to check if 'this' slot overlaps with another slot passed as a param. (factor in stuff like type, day, time)
            -iterate through _slotList and check for overlaps with all entry to the right of it (shifted nested for loops) and update this 2D array.
            -NOTE: make sure that a slot doesn't overlap with itself (technically it does, but keep it at default FALSE in order to avoid major errors/redundancy)  
            - OR a more efficient way would be to generate the list of slot overlaps fo every slot here, thus you dont linear search everytime.


        * slot class should have 2 lists
        * list1 is list of slot indices that would overlap this slot if this slot is considered as lecture slot
        * list2 is list of slot indices that would overlap this slot if this slot is considered as lab slot
        * what to do about slot overlaps
        * only 2 forms of overlaps:
        * LEC on TU and LAB on TU e.g. 
        * TU 8-9:30
        * LEC on MO (which implies on FR as well for 1hr) and LAB on FR


        2.  along the diagonal of _notcompatibles, check for a true value, if there is one return false (means the input file had a line saying a course is not-compat with itself) - maybe change to parse error?, this should be a parse error that I handle above
        
        

        4. for all the partassigns, make sure that that course and slot are also not 'not-compatible', if so, return false





        n. last thing to do should be to check for 313/413 and add 813/913 if needed and also init all the incompatibilities and special rules
        - we can store them as CPSC 813/913 LAB 01 and if slot TU 18:00 was defined, then set a partassign for this (also make sure its labmax is >= 1 otherwise output no solution), if not defined, can create this slot with labmin = labmax = 1/2
        - recursively set their not-compatibles
        - make sure we don't fall into an infinite loop! can mark every course checked?
        */


        return true;
    }






















///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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



    // Param: line (any string)
    // Return: NULL (if error occurred)
    // Return: Course instance created from this line
    private Course getNewCourse(String line) {

        if (line == null) {
            return null;
        }

        String lineTrimmed = line.trim();

        if (lineTrimmed.isEmpty()) {
            return null;
        }

        // get here if we have a non-blank trimmed string

        String[] segments = lineTrimmed.split("\\s+"); // split line by whitespace

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



    // Param: dayString (any string)
    // Param: timeString (any string)
    // Param: isCourseSlot (if false, then we have a labslot)
    // Param: toVerify (if false, only check for matching format, if true also check that this slot is one of the valid university slots)
    // Return: NULL (if error occurred)
    // Return: Slot instance created from this line
    private Slot getNewSlot(String dayString, String timeString, boolean isCourseSlot, boolean toVerify) {

        if (dayString == null || timeString == null) {
            return null;
        }

        String dayStringTrimmed = dayString.trim();
        String timeStringTrimmed = timeString.trim();

        if (dayStringTrimmed.isEmpty() || timeStringTrimmed.isEmpty()) {
            return null;
        }

        // 1. DAY
        // RULES: must be exactly "MO", "TU" for CourseSlot (additonally "FR" for LabSlot)

        Slot.Day day;
        if (isCourseSlot) {
            switch(dayStringTrimmed) {
                case "MO":
                    day = Slot.Day.MO;
                    break;
                case "TU":
                    day = Slot.Day.TU;
                    break;
                default:
                    return null;
            }
        }
        else {
            switch(dayStringTrimmed) {
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
                    return null;
            }
        }



        // 2. START TIME
        // RULES: form H:MM or HH:MM, where 0:00 == 00:00, and between 00:00 and 23:59
        // Also, 10 : 00, 10: 00, 10 :00 not allowed. only 10:00 is allowed

        String startHourStr;
        String startMinuteStr;
        String[] timeSegments = timeStringTrimmed.split(":");

        if (timeSegments.length != 2) {
            return null;
        }

        String hourStr = timeSegments[0];
        String minuteStr = timeSegments[1];

        if ((hourStr.length() != 1 && hourStr.length() != 2) || minuteStr.length() != 2) { // if we don't have H:MM or HH:MM
            return null;
        }

        // now check that H or HH is numeric string and between 0 and 23
        // also that MM is numeric string and between 0 and 59

        if (!isNumeric(hourStr) || !isNumeric(minuteStr)) { // make sure numeric
            return null;
        }

        int hourNum = Integer.parseInt(hourStr);
        int minuteNum = Integer.parseInt(minuteStr);

        // now make sure in range 00:00 to 23:59
        if (hourNum < 0 || hourNum > 23 || minuteNum < 0 || minuteNum > 59) {
            return null;    
        }

        // get here if times are valid times (but still need to check slot validity later)
        startHourStr = hourStr;
        startMinuteStr = minuteStr;


        
        int hashIndex = _mapSlotToIndex.size(); // thus the new index is the size of map. ex. it contains 3 slots already (0,1,2), now we want to put this 4th slot at index 3
        Slot newSlot = new Slot(hashIndex, day, startHourStr, startMinuteStr);
        if (isCourseSlot) {
            newSlot._isCourseSlot = true;
        }
        else {
            newSlot._isLabSlot = true;
        }

        // NOTE: verification must only occur after _isCourseSlot or _isLabSlot are set, so don't change this order

        if (toVerify) {
            if (!newSlot.verifySlotValidity()) {
                return null;
            }

            // otherwise, get here with a partial slot that is valid
        }
        
        return newSlot;         
    }

}



// ~~~~~~~~~~~~~~~~~~~~~~~~NOTE: print out specific error messages in order to debug in the future





