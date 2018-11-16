



public class CourseSlot extends Slot {

    public int _coursemax; 
    public int _coursemin;

    


    public CourseSlot(Day day, int startHour, int startMinute, int coursemax, int coursemin) {
        super(day, startHour, startMinute);
        _coursemax = coursemax;
        _coursemin = coursemin;
    }

}