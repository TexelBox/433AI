


// A 'Slot' is either a 'CourseSlot' or a 'LabSlot'
public class Slot {
    
    public enum Day {MO, TU, FR};

    public String _hashKey; // for lookup of its index in hashmap, init this when we construct this object and push its key/value pair into hashmap

    public Day _day; 

    public int _startHour;
    public int _startMinute;

    // could also have an endtime or a duration


    // have function to test if this is a valid slot which the constructor can call

    // have function to test if 2 slots are overlapping (1. same type (ex. COURSE/COURSE or LAB/LAB) 2. their interval overlaps), could be static so it can be called from the algorithm easier, or have it check for an overlap with this slot

    public Slot(Day day, int startHour, int startMinute) {
        _day = day;
        _startHour = startHour;
        _startMinute = startMinute;
    }

}