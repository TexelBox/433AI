


public class LabSlot extends Slot {

    public int _labmax;
    public int _labmin;



    public LabSlot(Day day, int startHour, int startMinute, int labmax, int labmin) {
        super(day, startHour, startMinute);
        _labmax = labmax;
        _labmin = labmin;
    }

}