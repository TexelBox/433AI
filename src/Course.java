


// A 'Course' can be either a LECTURE or a LAB/TUTORIAL (treated synonymously)
public class Course {

    //public enum CourseType {LEC, LAB, TUT, LECLAB, LECTUT};

    public enum PrimaryType {LEC, LAB, TUT};
    public enum SecondaryType {NONE, LAB, TUT};

    // fields...

    public int _hashIndex;

    public String _department; // e.g. CPSC

    //public int _number; // e.g. 433

    public String _number; // e.g. 433

    //public CourseType _type; // e.g. LEC X TUT Y

    public PrimaryType _primaryType;

    //public int _primarySection; // e.g. X = 1 (for LEC 01)

    public String _primarySection; // e.g. 01

    //public int _secondarySection; // e.g. Y = 5 (for TUT 05), only overwrite this is _type is LECLAB or LECTUT

    public SecondaryType _secondaryType;

    public String _secondarySection; // e.g. 01


    // EXTRA DATA
    public boolean _isLecture;

    public boolean _isEveningCourse;

    public boolean _is500Course;

    public String _outputID; // e.g. CPSC 433 LEC 01 TUT 01, what gets printed out at end

    public String _hashKey; // for lookup of its index in hashmap, init this when we construct this object and push its key/value pair into hashmap


    //public String _sharedHashKey; // e.g. CPSC 433 LEC 01 and CPSC 433 LEC 02 both have shared hashkey "CPSC433" ~~~~~~~~~~~~ use later



    // methods

    public Course(int hashIndex, String department, String number, PrimaryType primaryType, String primarySection, SecondaryType secondaryType, String secondarySection) {
        _hashIndex = hashIndex;
        _department = department;
        _number = number;
        _primaryType = primaryType;
        _primarySection = primarySection;
        _secondaryType = secondaryType;
        _secondarySection = secondarySection;
        setLectureFlag(); // IN THIS ORDER
        setEveningFlag();
        set500Flag();
        setOutputID();
        setHashKey();
    }

    public Course(int hashIndex, String department, String number, PrimaryType primaryType, String primarySection) {
        this(hashIndex, department, number, primaryType, primarySection, SecondaryType.NONE, null);
    }

    private void setLectureFlag() {
        _isLecture = _primaryType == PrimaryType.LEC && _secondaryType == SecondaryType.NONE;
    }

    // ~~~~~~~~~~~~~~~NOTE: i think labs can also be flagged as evening?
    private void setEveningFlag() {
        _isEveningCourse = _isLecture && _primarySection.charAt(0) == '9';
    }

    private void set500Flag() {
        _is500Course = _isLecture && _number.length() == 3 && _number.charAt(0) == '5';
    }

    private void setOutputID() {
        if (_secondaryType == SecondaryType.NONE) { // 4 segments
            _outputID = _department + " " + _number + " " + _primaryType.name() + " " + _primarySection;
        }
        else { // 6 segments
            _outputID = _department + " " + _number + " " + _primaryType.name() + " " + _primarySection + " " + _secondaryType.name() + " " + _secondarySection;
        }
    }

    private void setHashKey() {
        if (_secondaryType == SecondaryType.NONE) { // 4 segments
            String primaryTypeID;
            if (_primaryType == PrimaryType.LEC) {
                primaryTypeID = "LEC";
            }
            else { // if LAB or TUT (synonyms)
                primaryTypeID = "LAB/TUT";
            }
            _hashKey = _department + ":" + _number + ":" + primaryTypeID + ":" + _primarySection;
        }
        else { // 6 segments
            String primaryTypeID;
            if (_primaryType == PrimaryType.LEC) {
                primaryTypeID = "LEC";
            }
            else { // if LAB or TUT (synonyms) ~~~~~~~~~~~~~~~~~~~shoulnt this not be here and instead be an error? or it never happens due to outside checking
                primaryTypeID = "LAB/TUT";
            }
            String secondaryTypeID = "LAB/TUT";
            _hashKey = _department + ":" + _number + ":" + primaryTypeID + ":" + _primarySection + ":" + secondaryTypeID + ":" + _secondarySection;
        }
    }

}