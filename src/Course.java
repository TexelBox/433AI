


// A 'Course' can be either a LECTURE or a LAB/TUTORIAL
public class Course {

    public enum CourseType {LEC, LAB, TUT, LECLAB, LECTUT};

    // fields

    public String _hashKey; // for lookup of its index in hashmap, init this when we construct this object and push its key/value pair into hashmap
    public int _hashIndex;

    public String _department; // e.g. CPSC

    public int _number; // e.g. 433

    public CourseType _type; // e.g. LEC X TUT Y

    public int _primarySection; // e.g. X = 1 (for LEC 01)

    public int _secondarySection; // e.g. Y = 5 (for TUT 05), only overwrite this is _type is LECLAB or LECTUT

    public boolean _isEveningCourse;

    public boolean _is500Course;



    //public String _identifier; // what we print out at the end (e.g. "CPSC 203 LEC 01") ~~~~~~~~could expand this instead into the 4 (or 6) parts

    //public Slot _currentSlot; // the slot thats currently assigned to this course, maybe split this class into Lecture and Tutorial and then have CourseSlot and LabSlot respectively  

    //public boolean _isLecture; // is the course a lecture? or a tut/lab?

    // could store list of not-compatible partners, but we would have to use twice the memory and we would be doing twice the work, so I think my 2D array + HashMap Idea is better, if we are given a not-compatible line that contains a non-existent course/lab (hash get returns null), then ignore it (or throw error?)

    // similar idea for Unwanted

    // also have a 2d array for preferences : 2 hash index lookups + O(1) array access to get the int/float stored there (preference value)

    // same with pair

    //public Slot _partialAssignment = null; // if !null then _currentSlot MUST match this all the time (except for when currentSlot is null and then we initialize it to this)

    // methods

    public Course(String hashKey, int hashIndex, String department, int number, CourseType type, int primarySection, int secondarySection) {
        _hashKey = hashKey;
        _hashIndex = hashIndex;
        _department = department;
        _number = number;
        _type = type;
        _primarySection = primarySection;
        _secondarySection = secondarySection;
        SetEveningFlag();
        Set500Flag();
    }

    public Course(String hashKey, int hashIndex, String department, int number, CourseType type, int primarySection) {
        this(hashKey, hashIndex, department, number, type, primarySection, -1);
    }


    private void SetEveningFlag() {
        //_isEveningCourse = _type == CourseType.LEC && _primarySection == 9; // or is it >= 9? ~~~~~~~~~~~~~~~~~~~~, or does it mean starting with 9.. like LEC 91? I think it means the latter when looking at his instances, so imma change it, would 09 not be evening then? - i dont think so
    }

    private void Set500Flag() {
        _is500Course = _type == CourseType.LEC && (_number >= 500 && _number <= 599);
    }

}