



// A 'Course' can be either a LECTURE OR A LAB/TUTORIAL
public class Course {

    // fields

    public String _hashKey; // for lookup of its index in hashmap, init this when we construct this object and push its key/value pair into hashmap

    public String _identifier; // what we print out at the end (e.g. "CPSC 203 LEC 01") ~~~~~~~~could expand this instead into the 4 (or 6) parts

    public Slot _currentSlot; // the slot thats currently assigned to this course, maybe split this class into Lecture and Tutorial and then have CourseSlot and LabSlot respectively  

    public boolean _isLecture; // is the course a lecture? or a tut/lab?

    // could store list of not-compatible partners, but we would have to use twice the memory and we would be doing twice the work, so I think my 2D array + HashMap Idea is better, if we are given a not-compatible line that contains a non-existent course/lab (hash get returns null), then ignore it (or throw error?)

    // similar idea for Unwanted

    // also have a 2d array for preferences : 2 hash index lookups + O(1) array access to get the int/float stored there (preference value)

    // same with pair

    public Slot _partialAssignment = null; // if !null then _currentSlot MUST match this all the time (except for when currentSlot is null and then we initialize it to this)

    // methods

}