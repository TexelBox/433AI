// ~~~~~~~~~ Maybe move half of this class to AndTree.java and the constraint checking to Slot.java 



// ~~~~~~~~~~~IGNORE THIS CLASS~~~~~~~~~~~~~~~~~~



public class Algorithm {

    // fields...

    public Input _input;

    // init this size after figuring out how many courses were specified in input file
    public Slot[] _currentAssign; // size is N where (N = numbers of courses), index i corresponds to course with hashindex i, init this based on partassign values
    public double _currentEval;

    // IDEA: maybe have the above 2 fields be part of the recursive algorithm so that their value is stored (and remembered) locally
    // but then we would have to deepcopy the array many times...

    public Slot[] _bestAssign; // remember our best _currentAssign that we had (if we had one that was valid and complete)
    public double _bestEval;

    // methods...

    // PLAN:
    /*
        build the AND-TREE recursively
        0. preprocess by starting with a blank Slot array, foreach partialassign (found in input file and ONLY 1 per course), put 1 in at a time and check hardconstraints and calculate Eval
        1. next we look at the LEFTMOST NULL entry in array, place next entry of PossibleSlots into Slot array, check hardconstraints and calculate Eval. If hardconstraints is broken OR _currentEval > _bestEval then return (closes the branch)
        2. if Slot array becomes full and hardconstraint is satisfied and currentEval < bestEval then update bestAssign as a copy of this array and update bestEval to currentEval
        3. return;
        4. goal condition (stopping condition is when all branches are closed - meaning there is no morepossible slots to use)

        NOTE: since each recursive level only makes 1 CHANGE (to leftmost null slot)

    */

    // ~~~~~~~~~ every time we add a PossibleSlot into _currentAssign at index i, we should call a function in Slot to add index i to its list of assigned course indices (also increment appropriate count field). When we return (recursive backtrack) we need to remember to remove this index from the Slots indices list

    // return True if no hard constraint are violated
    // pass in slot reference that has just been changed (added into currentAssign)
    // NOTE: here we assume that for an entry in _currentAssign, we only ever assign CourseSlot to an index corresponding to a Lecture and LabSlot only to an index for a Lab
    public boolean checkHardConstraints(Slot slot) {
        // get the course indices currently assigned to this slot
        // using these indices we then check over the data structures in Input class

        if (slot._lectureCount > slot._coursemax) {
            return false; // VIOLATION
        }

        if (slot._labCount > slot._labmax) {
            return false; // VIOLATION
        }

        // check each pair of indices...
        for (int i = 0; i < slot._courseIndices.size() - 1; i++) {
            for (int j = i + 1; j < slot._courseIndices.size(); j++) {
                if (_input._notCompatibles[i][j]) {
                    return false; // VIOLATION
                }

                if (_input._courseList.get(i)._is500Course && _input._courseList.get(j)._is500Course) { // 2 500-lvl LECS assigned to same slot
                    return false; // VIOLATION
                }

            }    

        }

        // no need to check partassign constraint since it will be true by default if the preprocessing worked correctly (already did the check)

        // check each index...
        for (int i = 0; i < slot._courseIndices.size(); i++) {
            if (_input._unwanteds[i][slot._hashIndex]) {
                return false; // VIOLATION
            }

            if (_input._courseList.get(i)._isEveningCourse && !slot._isEveningSlot) { // an evening course not assigned to an evening slot
                return false; // VIOLATION
            }

        }

        // ~~~~~~~~~~~~~~~~ STILL need to have no course (is it only lecs?) that can't be assign to slot TU 11:00 - 12:30

        // ~~~~~~~~~~~~~~~~~ ALSO NEED to implement checking over slots that have their interval intersect with this slot we are testing (maybe Slot has a list of overlapping slots?)

        // NOTE: for 813 and 913 (if present), make sure they get initialized as Evening classes (actually this doesnt matter since technically they arent evening classes and the conditional doesnt apply)and are partassigned to TU 18:00 and Input preprocessed it so that they are not-compatible recursively with 313 or 413 and their not-compats,

        return true;
    }



    public Algorithm(Input input) {
        _input = input;
        // also init size of currentAssign and bestAssign here to be N
    }







}