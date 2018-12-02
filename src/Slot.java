
// ~~~~~~then have the ending time be based on the day? (is it also based on the type of course assigned to it?)

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

    //public int _startHour;
    //public int _startMinute;

    public int _coursemax = 0; // ~~~~~~~~~~~~~~~~~~~~~~~~~~~THESE 4 get set externally~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public int _coursemin = 0;
    public int _labmax = 0;
    public int _labmin = 0;

    public boolean _isEveningSlot;

    public boolean _isCourseSlot; // if false then its a labslot ~~~~~~~~~~~~~~~~set externally

    //public boolean _isCourseSlot = false; // set true if this slot was found under CourseSlot header, ~~~~~~~~~~~~~~~~~~~these 2 are SET EXTERNALLY~~~~~~~~~~~~~~~~~~~~~~
    //public boolean _isLabSlot = false; // set true if this slot was found under LabSlot header 

    public String _outputID; // e.g. MO, 10:00

    public String _courseSlotHashKey;
    public String _labSlotHashKey;


    //public String _hashKey; // for lookup of its index in hashmap, init this when we construct this object and push its key/value pair into hashmap



    private int _startTimeInMinutes; // use this for slot overlap setting
    private int _endTimeInMinutes;

    //public list of overlapping slots (need to set symmetrically)

    // algorithm stuff...
    public List<Integer> _courseIndices = new ArrayList<Integer>(); // current indices of courses assigned to this slot
    public int _lectureCount = 0; // number of lectures in _courseIndices 
    public int _labCount = 0; // number of labs/tuts in _courseIndices

    



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







    // have function to test if this is a valid slot which the constructor can call (or when we change data?)

    // have function to test if 2 slots are overlapping (their interval (depends on day) overlaps), could be static so it can be called from the algorithm easier, or have it check for an overlap with this slot

   

    









    // return True if no hard constraints are violated
    // this is the slot that has just been changed
    public boolean checkHardConstraints() {
        // get the course indices currently assigned to this slot
        // using these indices we then check over the data structures in Input class

        // UPDATE ALL THIS STUFF FOR OVERLAPS~~~~~~~~~~~~~~~~~~~~~~~~~~~

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

        // ~~~~~~~~~~~~~~~~ STILL need to have no course (is it only lecs?) that can't be assign to slot TU 11:00 - 12:30, this should be handled automatically by coursemax

        // ~~~~~~~~~~~~~~~~~ ALSO NEED to implement checking over slots that have their interval intersect with this slot we are testing (maybe Slot has a list of overlapping slots?)

        // NOTE: for 813 and 913 (if present), make sure they get initialized as Evening classes (actually this doesnt matter since technically they arent evening classes and the conditional doesnt apply)and are partassigned to TU 18:00 and Input preprocessed it so that they are not-compatible recursively with 313 or 413 and their not-compats,

        return true;
    }

}