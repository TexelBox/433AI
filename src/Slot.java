
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

    // have function to test if this is a valid slot which the constructor can call (or when we change data?)

    // have function to test if 2 slots are overlapping (their interval (depends on day) overlaps), could be static so it can be called from the algorithm easier, or have it check for an overlap with this slot

    public Slot(String hashKey, int hashIndex, Day day, int startHour, int startMinute) {
        _hashKey = hashKey;
        _hashIndex = hashIndex;
        _day = day;
        _startHour = startHour;
        _startMinute = startMinute;
        SetEveningFlag();
    }

    private void SetEveningFlag() {
        _isEveningSlot = _startHour >= 18;
    }

}