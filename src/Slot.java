
// ~~~~~~then have the ending time be based on the day? (is it also based on the type of course assigned to it?)

import java.util.ArrayList;
import java.util.List;

public class Slot {
    
    public enum Day {MO, TU, FR};

    public String _hashKey; // for lookup of its index in hashmap, init this when we construct this object and push its key/value pair into hashmap
    public int _hashIndex; 

    public Day _day; 

    public int _startHour;
    public int _startMinute;

    public int _coursemax = 0; 
    public int _coursemin = 0;
    public int _labmax = 0;
    public int _labmin = 0;

    public boolean _isEveningSlot;

    public List<Integer> _courseIndices = new ArrayList<Integer>(); // current indices of courses assigned to this slot

    public int _lectureCount = 0; // number of lectures in _courseIndices 
    public int _labCount = 0; // number of labs/tuts in _courseIndices

    public boolean _isCourseSlot = false; // set true if this slot was found under CourseSlot header
    public boolean _isLabSlot = false; // set true if this slot was found under LabSlot header 

    // have function to test if this is a valid slot which the constructor can call (or when we change data?)

    // have function to test if 2 slots are overlapping (their interval (depends on day) overlaps), could be static so it can be called from the algorithm easier, or have it check for an overlap with this slot

    public Slot(String hashKey, int hashIndex, Day day, int startHour, int startMinute) {
        _hashKey = hashKey;
        _hashIndex = hashIndex;
        _day = day;
        _startHour = startHour;
        _startMinute = startMinute;
        setEveningFlag();
    }

    private void setEveningFlag() {
        _isEveningSlot = _startHour >= 18;
    }



    // after construction, check that this slot, (which has the proper format for the 4 fields), is one of the VALID slots per the assignment description.
    public boolean checkSlotValidity() { // ~~~~~~~~~~~~~~~~~~~~~~~~~~~TODO~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        return true;
    }









    // return True if no hard constraints are violated
    // this is the slot that has just been changed
    public boolean checkHardConstraints() {
        // get the course indices currently assigned to this slot
        // using these indices we then check over the data structures in Input class

        if (_lectureCount > _coursemax) {
            return false; // VIOLATION
        }

        if (_labCount > _labmax) {
            return false; // VIOLATION
        }

        // check each pair of indices...
        for (int i = 0; i < _courseIndices.size() - 1; i++) {
            for (int j = i + 1; j < _courseIndices.size(); j++) {
                if (Input.getInstance()._notCompatibles[i][j]) {
                    return false; // VIOLATION
                }

                if (Input.getInstance()._courseList.get(i)._is500Course && Input.getInstance()._courseList.get(j)._is500Course) { // 2 500-lvl LECS assigned to same slot
                    return false; // VIOLATION
                }

            }    

        }

        // no need to check partassign constraint since it will be true by default if the preprocessing worked correctly (already did the check)

        // check each index...
        for (int i = 0; i < _courseIndices.size(); i++) {
            if (Input.getInstance()._unwanteds[i][_hashIndex]) {
                return false; // VIOLATION
            }

            if (Input.getInstance()._courseList.get(i)._isEveningCourse && !_isEveningSlot) { // an evening course not assigned to an evening slot
                return false; // VIOLATION
            }

        }

        // ~~~~~~~~~~~~~~~~ STILL need to have no course (is it only lecs?) that can't be assign to slot TU 11:00 - 12:30

        // ~~~~~~~~~~~~~~~~~ ALSO NEED to implement checking over slots that have their interval intersect with this slot we are testing (maybe Slot has a list of overlapping slots?)

        // NOTE: for 813 and 913 (if present), make sure they get initialized as Evening classes (actually this doesnt matter since technically they arent evening classes and the conditional doesnt apply)and are partassigned to TU 18:00 and Input preprocessed it so that they are not-compatible recursively with 313 or 413 and their not-compats,

        return true;
    }



    



}