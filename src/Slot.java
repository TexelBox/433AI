


// NOTE: must have H:MM or HH:MM, 
// 0:00 == 00:00
// from 0:00 to 23:59 will be accecpted as a valid time, then later checked inside this class to be a valid slot
// NOTE: 10 :00 not accepted, 10: 00 not accepted, 10 : 00 not accepted, 10:00 only accepted

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Slot {

    private static Set<String> _validCourseSlots = new HashSet<String>(); // will be initialized by first instance to contains hashKeys for every valid lecture slot as defined by assignment desc.
    private static Set<String> _validLabSlots = new HashSet<String>(); // will be initialized by first instance to contains hashKeys for every valid lab slot as defined by assignment desc.    

    public enum Day {MO, TU, FR};

    public int _hashIndex; 

    public Day _day; 

    public String _startHourStr;
    public String _startMinuteStr; 

    public int _startHourNum;
    public int _startMinuteNum;

    public int _coursemax = 0; // ~~~~~~~~~~~~~~~~~~~~~~~~~~~THESE 4 get set externally~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public int _coursemin = 0;
    public int _labmax = 0;
    public int _labmin = 0;

    public boolean _isEveningSlot;

    public boolean _isCourseSlot; // if false then its a labslot ~~~~~~~~~~~~~~~~set externally

    public String _outputID; // e.g. MO, 10:00

    public String _courseSlotHashKey;
    public String _labSlotHashKey;



    private int _startTimeInMinutes; // use this for slot overlap setting
    private int _endTimeInMinutes;



    public Set<Integer> _overlaps = new HashSet<Integer>(); // the set of slot indices that this slot overlaps (not including itself)

    public int _twinSlotIndex = -1; // index of the slot that starts at the same time as this slot if there is one... (will stay -1 if this slot has no twin)
    public boolean _hasTwin = false;

    // algorithm stuff...
    //public List<Integer> _courseIndices = new ArrayList<Integer>(); // current indices of courses (lecs+labs) assigned to this slot
    //public int _lectureCount = 0; // number of lectures in _courseIndices 
    //public int _labCount = 0; // number of labs/tuts in _courseIndices

    



    public Slot(int hashIndex, Day day, String startHourStr, String startMinuteStr) {
        initValidSlots(); // the first instance will init the static hashsets
        _hashIndex = hashIndex;
        _day = day;
        _startHourStr = startHourStr;
        _startMinuteStr = startMinuteStr;
        setTimeNums();
        setEveningFlag();
        setOutputID();
        setHashKeys();
    }

/*
    // copy constructor...
    public Slot(Slot slot) {
        this._hashIndex = slot._hashIndex;
        this._day = slot._day; // pass ref by val (doesn't matter, never changes)
        this._startHourStr = slot._startHourStr; // pass ref by val (doesn't matter, never changes)
        this._startMinuteStr = slot._startMinuteStr; // pass ref by val (doesn't matter, never changes)
        this._startHourNum = slot._startHourNum;
        this._startMinuteNum = slot._startMinuteNum;
        this._coursemax = slot._coursemax;
        this._coursemin = slot._coursemin;
        this._labmax = slot._labmax;
        this._labmin = slot._labmin;
        this._isEveningSlot = slot._isEveningSlot;
        this._isCourseSlot = slot._isCourseSlot;
        this._outputID = slot._outputID; // pass ref by val (doesn't matter, never changes)
        this._courseSlotHashKey = slot._courseSlotHashKey; // pass ref by val (doesn't matter, never changes)
        this._labSlotHashKey = slot._labSlotHashKey; // pass ref by val (doesn't matter, never changes)
        this._startTimeInMinutes = slot._startTimeInMinutes;
        this._endTimeInMinutes = slot._endTimeInMinutes;
        this._overlaps = slot._overlaps; // pass ref by val (doesn't matter, never changes)
    


        // stuff below is intented to get overwritten after call to this constructor.
        List<Integer> template = slot._courseIndices; // get reference to it
        List<Integer> clone = new ArrayList<Integer>(template.size());
        for (int index : template) {
            clone.add(index);
        } 
        this._courseIndices = clone; // pass deep copy of it

        this._lectureCount = slot._lectureCount;
        this._labCount = slot._labCount;
    }
*/


    private void initValidSlots() { // inits the 2 static hashsets of valid slot hashKeys, only need to do the work for the first time (first instance)
        if (_validCourseSlots.isEmpty()) { // init valid slots for LEC
            // MONDAY:
            _validCourseSlots.add("MO:8:00:LEC");
            _validCourseSlots.add("MO:9:00:LEC");
            _validCourseSlots.add("MO:10:00:LEC");
            _validCourseSlots.add("MO:11:00:LEC");
            _validCourseSlots.add("MO:12:00:LEC");
            _validCourseSlots.add("MO:13:00:LEC");
            _validCourseSlots.add("MO:14:00:LEC");
            _validCourseSlots.add("MO:15:00:LEC");
            _validCourseSlots.add("MO:16:00:LEC");
            _validCourseSlots.add("MO:17:00:LEC");
            _validCourseSlots.add("MO:18:00:LEC");
            _validCourseSlots.add("MO:19:00:LEC");
            _validCourseSlots.add("MO:20:00:LEC");
            // TUESDAY:
            _validCourseSlots.add("TU:8:00:LEC");
            _validCourseSlots.add("TU:9:30:LEC");
            _validCourseSlots.add("TU:11:00:LEC");
            _validCourseSlots.add("TU:12:30:LEC");
            _validCourseSlots.add("TU:14:00:LEC");
            _validCourseSlots.add("TU:15:30:LEC");
            _validCourseSlots.add("TU:17:00:LEC");
            _validCourseSlots.add("TU:18:30:LEC");
        }

        if (_validLabSlots.isEmpty()) { // init valid slots for LAB/TUT
            // MONDAY:
            _validLabSlots.add("MO:8:00:LAB");
            _validLabSlots.add("MO:9:00:LAB");
            _validLabSlots.add("MO:10:00:LAB");
            _validLabSlots.add("MO:11:00:LAB");
            _validLabSlots.add("MO:12:00:LAB");
            _validLabSlots.add("MO:13:00:LAB");
            _validLabSlots.add("MO:14:00:LAB");
            _validLabSlots.add("MO:15:00:LAB");
            _validLabSlots.add("MO:16:00:LAB");
            _validLabSlots.add("MO:17:00:LAB");
            _validLabSlots.add("MO:18:00:LAB");
            _validLabSlots.add("MO:19:00:LAB");
            _validLabSlots.add("MO:20:00:LAB");
            // TUESDAY:
            _validLabSlots.add("TU:8:00:LAB");
            _validLabSlots.add("TU:9:00:LAB");
            _validLabSlots.add("TU:10:00:LAB");
            _validLabSlots.add("TU:11:00:LAB");
            _validLabSlots.add("TU:12:00:LAB");
            _validLabSlots.add("TU:13:00:LAB");
            _validLabSlots.add("TU:14:00:LAB");
            _validLabSlots.add("TU:15:00:LAB");
            _validLabSlots.add("TU:16:00:LAB");
            _validLabSlots.add("TU:17:00:LAB");
            _validLabSlots.add("TU:18:00:LAB");
            _validLabSlots.add("TU:19:00:LAB");
            _validLabSlots.add("TU:20:00:LAB");
            // FRIDAY:
            _validLabSlots.add("FR:8:00:LAB");
            _validLabSlots.add("FR:10:00:LAB");
            _validLabSlots.add("FR:12:00:LAB");
            _validLabSlots.add("FR:14:00:LAB");
            _validLabSlots.add("FR:16:00:LAB");
            _validLabSlots.add("FR:18:00:LAB");
        }

    }



    // this assumes that the 2 strings are numeric
    private void setTimeNums() {
        _startHourNum = Integer.parseInt(_startHourStr);
        _startMinuteNum = Integer.parseInt(_startMinuteStr);            
    }

    private void setEveningFlag() {
        _isEveningSlot = _startHourNum >= 18;
    }

    private void setOutputID() {
        _outputID = _day.name() + ", " + _startHourStr + ":" + _startMinuteStr;
    }

    private void setHashKeys() {
        //_hashKey = _day.name() + ":" + Integer.toString(_startHourNum) + ":" + _startMinuteStr; // e.g. MO, 00:00 becomes MO:0:00, synonymously, MO, 0:00 becomes MO:0:00 (same hash)
        _courseSlotHashKey = _day.name() + ":" + Integer.toString(_startHourNum) + ":" + _startMinuteStr + ":LEC";
        _labSlotHashKey = _day.name() + ":" + Integer.toString(_startHourNum) + ":" + _startMinuteStr + ":LAB";
    }

    
    // check that all this data is in fact a valid slot per assignment desc.
    // i think all that needs to be checked is that this hashkey is inside the proper hashset or both?
    // ONLY call this after construction (which will be enforced by default) and enforce that ALL of the external init happends before calling this fucntion
    public boolean verifySlotValidity() {

        if (_isCourseSlot && !_validCourseSlots.contains(_courseSlotHashKey)) { // if this courseslot does not correspond to a valid day and start time...
            return false;
        }
        if (!_isCourseSlot && !_validLabSlots.contains(_labSlotHashKey)) { // if this labslot does not correspond to a valid day and start time...
            return false;
        }



        /*
        if (_isCourseSlot && !_validCourseSlots.contains(_hashKey)) { // if this courseslot does not correspond to a valid day and start time...
            return false;
        }
        if (_isLabSlot && !_validLabSlots.contains(_hashKey)) { // if this labslot does not correspond to a valid day and start time...
            return false;
        }
        */
        
        // thus 2 checks are done if its is both slot types

        // get here if VALID in whatever case...

        return true;        
    }




    // NOTE: will call this after everything is init (at least after _isCourseSlot is set externally)
    public void setTimeIntervalNums() {
        int deltaMinutes; // interval length from start time to end time
        if (_isCourseSlot) { // is for lecture lengths
            if (_day == Day.MO) { // monday lecture has length 1 hour 
                deltaMinutes = 60;
            }
            else { // tuesday lecture has length 1.5 hours
                deltaMinutes = 90;
            }
        } 
        else { // is for lab lengths
            if (_day == Day.MO || _day == Day.TU) { // both monday and tuesday labs are 1 hour
                deltaMinutes = 60;
            }
            else { // friday labs are 2 hours
                deltaMinutes = 120;
            }
        }

        // now convert starttime hour + minute to just minutes then add 

        _startTimeInMinutes = 60*_startHourNum + _startMinuteNum;
        _endTimeInMinutes = _startTimeInMinutes + deltaMinutes;
    }


    public static boolean checkForTwin(Slot slot1, Slot slot2) {

        if (slot1._startTimeInMinutes == slot2._startTimeInMinutes && slot1._endTimeInMinutes == slot2._endTimeInMinutes) { // if the 2 slots start at the same time...
            if (slot1._day == slot2._day) { // if they are on the same day (and time overlaps), theres an overlap
                return true;
            }
        }
        
        return false;

    }


    // this static function will be used by the post-process() of the parser ONLY to check if 2 slots overlap
    // then the parser will update the overlap sets for each slot symmetrically if they do overlap
    // return True if overlap
    public static boolean checkForOverlap(Slot slot1, Slot slot2) {

        // check for time non-overlap....
        if (slot1._startTimeInMinutes >= slot2._endTimeInMinutes) {
            return false;
        }
        if (slot1._endTimeInMinutes <= slot2._startTimeInMinutes) {
            return false;
        }

        // get here and theres an overlap for time, but what about the day?

        // check the day
        // cases of overlaps...
        // 1. MO LEC w/ MO LEC (self-overlap)
        // 2. MO LAB w/ MO LAB (self-overlap) ~~~~~~combine these 4 into 1
        // 3. MO LEC w/ MO LAB 
        // 4. MO LAB w/ MO LEC

        // 5. MO LEC w/ FR LAB

        // 6. TU LEC w/ TU LEC (self-overlap) ~~~~~~~~~combine these 4 into 1
        // 7. TU LAB w/ TU LAB (self-overlap)
        // 8. TU LEC w/ TU LAB 
        // 9. TU LAB w/ TU LEC

        // 10. FR LAB w/ FR LAB (self-overlap)

        if (slot1._day == slot2._day) { // if they are on the same day (and time overlaps), theres an overlap
            return true;
        }
        else if (slot1._isCourseSlot && slot1._day == Day.MO && slot2._day == Day.FR) {
            return true;
        }
        else if (slot2._isCourseSlot && slot2._day == Day.MO && slot1._day == Day.FR) {
            return true;
        }
        else {
            return false; // days that never overlaps
        }

    }





    // algorithm will call this after this slot has been assigned to the next course in problem vector (updating _courseIndices, lecCount, and labCOunt)
    // return True if no hard constraints are violated
    // this is the slot that has just been changed
    // Param node - is the focused on node at the moment
    public boolean checkHardConstraints(Node node) {
        // get the course indices currently assigned to this slot
        // using these indices we then check over the data structures in Input class

        if (node._lectureCounts[_hashIndex] > _coursemax) {
            return false; // VIOLATION
        }

        if (node._labCounts[_hashIndex] > _labmax) {
            return false; // VIOLATION
        }

        // get the list of course indices currently assigned to each overlapping slot...
        // NOTE: there won't be any duplicates since a course can't be assigned to multiple slots at once, thus we can use a list

        // compare the changedIndex (which is the new index added to _courseIndices) with the other indices in _courseIndices and also with the overlapped indices

        int changedIndex = node._changedIndex; // WE ONLY HAVE TO CHECK THIS CHANGE (added 1 course to this slot by the expansion)

        // check inside this slot...
        for (Course sameSlotCourse : node._coursesAssignedToSlots.get(_hashIndex)) { 
        	int sameSlotCourseIndex = sameSlotCourse._hashIndex;
            if (sameSlotCourseIndex != changedIndex) { // have to ignore comparison with itself
                if (Input.getInstance()._notCompatibles[changedIndex][sameSlotCourseIndex]) { // check not-compat
                    return false; // VIOLATION
                }

                if (Input.getInstance()._courseList.get(changedIndex)._is500Course && sameSlotCourse._is500Course) { // 2 500-lvl LECS overlapping
                    return false; // VIOLATION
                }
            }
        }

        // now check inside overlapping slots...

        for (int overlapSlotIndex : _overlaps) { // for each overlapped slot..
        	if (!node._coursesAssignedToSlots.get(overlapSlotIndex).isEmpty()) { // if this overlap slot was assigned to at least 1 course in current problem...
        		Slot overlapSlot = Input.getInstance()._slotList.get(overlapSlotIndex);
        		for (Course overlapSlotCourse : node._coursesAssignedToSlots.get(overlapSlotIndex)) { // for each overlapping course in this overlap slot...
        			int overlapSlotCourseIndex = overlapSlotCourse._hashIndex;
                    if (Input.getInstance()._notCompatibles[changedIndex][overlapSlotCourseIndex]) { // check not-compat
                        return false; // VIOLATION
                    }
    
                    if (Input.getInstance()._courseList.get(changedIndex)._is500Course && overlapSlotCourse._is500Course) { // 2 500-lvl LECS overlapping
                        return false; // VIOLATION
                    }
                }
        	}
        	
        }

        // NOTE: no need to check partassign constraint since it will be true by default if the preprocessing worked correctly (already did the check for setting the root)

        // UNWANTED CHECK - only have to check within this slot (no overlaps)
        // unwanted(a,s) means that we can't assign class a to the slot s (defined by its day and start time and slot type)

        if (Input.getInstance()._unwanteds[changedIndex][_hashIndex]) { // if our new course can't be assigned to this slot...
            return false; // VIOLATION
        }


        // EVENING CHECK
        // an evening course (lecture) must be assigned to an evening slot
        if (Input.getInstance()._courseList.get(changedIndex)._isEveningCourse && !_isEveningSlot) { // if our newcourse is an evening course and this slot is not an evening slot...
            return false; // VIOLATION
        }

        return true;
    }


/*
    private double getEvalMinfilled(Node node) {

        double evalMinfilled;

        if (_isCourseSlot) { // if this is a course slot... (only need to consider coursemax/min)
            // calculate the max number of courses that could be assigned to this slot in the future...
            int maxPossibleLecCount = _lectureCount + node._remainingCoursesCount; // assume we could add all remaining unassigned courses into this slot
            // now calculate the difference between this max and our coursemin
            int diff = _coursemin - maxPossibleLecCount;
            if (diff > 0) { // for every course under the min, we add pen_coursemin
                evalMinfilled = diff * Algorithm.pen_coursemin;
            }
            else {
                evalMinfilled = 0;
            }
        }
        else { // if this is a lab slot... (only need to consider labmax/min)
            // calculate the max number of labs that could be assigned to this slot in the future...
            int maxPossibleLabCount = _labCount + node._remainingLabsCount; // assume we could add all remaining unassigned labs into this slot
            // now calculate the difference between this max and our labmin
            int diff = _labmin - maxPossibleLabCount;
            if (diff > 0) { // for every lab under the min, we add pen_labsmin
                evalMinfilled = diff * Algorithm.pen_labsmin;
            }
            else {
                evalMinfilled = 0;
            }
        }

        return evalMinfilled;
    }


    private int getEvalPref(Node node) {

        int evalPref = 0;

        for (int i = 0; i < Input.getInstance()._courseList.size(); i++) { // loop through all courses
            // i is the next courseIndex
            // only add to evalPref if the course is 1) ASSIGNED and 2) ASSIGNED TO ANOTHER SLOT
            int nextPref = Input.getInstance()._preferences[i][_hashIndex]; // lookup value stored in table

            // check if we should add it...

            if (_courseIndices.contains(i)) { // if this course is assigned to this slot, then we don't want to add this penalty
                continue;
            }

            // get here if this course isn't assigned to this slot

            // so 2 possibilities:
            // 1. course isnt assigned to any slot yet (so we can ignore penalty for now, since its possible it could be added in future)

            if (node._problem[i] == null) { // unassigned course
                continue;
            }

            // get here and we know that this course is assigned to a slot that isn't this one, so we now add the penalty...
            evalPref += nextPref;
        }

        return evalPref;
    }

    // penalty if 2 paired courses/labs aren't assigned to same day and start time and end time
    private double getEvalPair(Node node) { 

        double evalPair = 0;

        // loop over _courseIndices,
        // we have an index, which we can look up its row in _pairs
        // if the entry is TRUE, then we check if that paired index is assigned yet...
        // if it is ASSIGNED to a DIFFERENT slot, then we add pen_notpaired, and then we can can flag this index as checked (only flag the left index as checked), that way when it becomes the right index in another slot's function, we ignore it

        for (int nextCourseIndex : _courseIndices) { // for each class stored in this slot...
            for (int otherIndex = 0; otherIndex < Input.getInstance()._courseList.size(); otherIndex++) {

                if (node._courseIndicesAlreadyCheckedForByPair.contains(otherIndex)) { // if this index was already check as a previous nextCourseIndex (either in this slot or other slots)...
                    continue; // skip it (prevent symmetry causing 2*penalty)
                }

                if (Input.getInstance()._pairs[nextCourseIndex][otherIndex]) { // if we expect these 2 to be paired up...

                    if (_courseIndices.contains(otherIndex)) { // if both courses are assigned to this slot (paired up), then we don't want to add this penalty
                        continue;
                    }

                    if (_hasTwin) {
                        Slot twinSlot;
                        if (node._assignedSlots.containsKey(_twinSlotIndex)) { // if in our map..
                            twinSlot = node._assignedSlots.get(_twinSlotIndex); // get from map if there is one
                        }
                        else { // if not in map (not assigned to any course yet)
                            // get the template from _slotList
                            twinSlot = Input.getInstance()._slotList.get(_twinSlotIndex);
                        }

                        if (twinSlot._courseIndices.contains(otherIndex)) {
                            continue;
                        }
                    }
                    

                    // get here if othercourse isn't assigned to this slot (or its twin slot if it has one)
                    // 2 cases, either not assigned at all (don't add penalty yet) or assigned to a different slot (now we can add penalty)

                    if (node._problem[otherIndex] == null) { // unassigned course
                        continue;
                    }

                    // get here and we know that this other course is assigned to a slot that isn't this one, so we now add the penalty...
                    evalPair += Algorithm.pen_notpaired;
                }
            }

            node._courseIndicesAlreadyCheckedForByPair.add(nextCourseIndex); // flag that we have already checked all the pairs containing this course (don't check again and add penalty again!)
        }

        return evalPair;
    }

    private double getEvalSecdiff(Node node) {

        double evalSecdiff = 0;

        // here we apply a "soft" not-compatible for lecs of same class 
        // we need to check in this slot and in overlapslot, how many pairs of sections do we have...
        // multiply this number by pen_secdiff to get return value

        // check every pair in this slot directly
        // no need to check if not in hashset because they won't be since they're in this slot only
        for (int i = 0; i < _courseIndices.size() - 1; i++) {
            int sameSlotCourseIndexI = _courseIndices.get(i);
            for (int j = i+1; j < _courseIndices.size(); j++) {
                int sameSlotCourseIndexJ = _courseIndices.get(j);
                // this only applies to LECs of same class (thus both isLecture and sharedhashkeys are equal)
                Course sameSlotCourseI = Input.getInstance()._courseList.get(sameSlotCourseIndexI);
                Course sameSlotCourseJ = Input.getInstance()._courseList.get(sameSlotCourseIndexJ);
                if (sameSlotCourseI._isLecture && sameSlotCourseJ._isLecture) {
                    if (sameSlotCourseI._sharedHashKey.equals(sameSlotCourseJ._sharedHashKey)) {
                        evalSecdiff += Algorithm.pen_section;
                    }
                }
            }
        }

        // now check each sameslotindex w/ each index in overlap slots
        // then afterwards make sure to add every index in _courseIndices into the hashset to prevent duplicate pairs from counting in future slots who reference this

        for (int sameSlotCourseIndex : _courseIndices) {
            for (int overlapSlotIndex : _overlaps) {
                if (node._assignedSlots.containsKey(overlapSlotIndex)) { // if this slot has been assigned to at least 1 course (it won't be in this map if it has 0)
                    Slot overlapSlot = node._assignedSlots.get(overlapSlotIndex);
                    for (int overlapSlotCourseIndex : overlapSlot._courseIndices) {
                        // now we got our 2 course indices: sameSlotCourseIndex and overlapSlotCourseIndex
                        // check that we haven't already looked at this pair before (with overlapSlotCourseIndex as the leftIndex)
                        if (node._courseIndicesAlreadyCheckedForBySecdiff.contains(overlapSlotCourseIndex)) {
                            continue;
                        }
                        // get here if this is first time checking this pair

                        // this only applies to LECs of same class (thus both isLecture and sharedhashkeys are equal)
                        Course sameSlotCourse = Input.getInstance()._courseList.get(sameSlotCourseIndex);
                        Course overlapSlotCourse = Input.getInstance()._courseList.get(overlapSlotCourseIndex);
                        if (sameSlotCourse._isLecture && overlapSlotCourse._isLecture) {
                            if (sameSlotCourse._sharedHashKey.equals(overlapSlotCourse._sharedHashKey)) {
                                evalSecdiff += Algorithm.pen_section;
                            }
                        }
                    }
                }
            }

            // at end, we flag course as checked for future by putting in in hashset
            node._courseIndicesAlreadyCheckedForBySecdiff.add(sameSlotCourseIndex);
        }

        return evalSecdiff;
    }

    public double getEval(Node node) {
        double evalMinfilled = getEvalMinfilled(node);
        int evalPref = getEvalPref(node);
        double evalPair = getEvalPair(node);
        double evalSecdiff = getEvalSecdiff(node); 

        double eval = evalMinfilled * Algorithm.w_minfilled + evalPref * Algorithm.w_pref + evalPair * Algorithm.w_pair + evalSecdiff * Algorithm.w_secdiff;

        return eval;
    } 
*/

}