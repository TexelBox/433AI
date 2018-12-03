

// NOTE: for getting eval of current problme, we need to use paren'ts eval + deltaEval that would occur due to this new change + indirect changes like reducing remaining pool
// whenever we need to reference to slots that have at least 1 class in them, we use the hashmap
// whenever we need reference to slots that have 0 classes in them, we use the _courselotslist and _labslotslist
// what we can do is everytime we eval we have to check over every single slot in both of these lists, get their hasindex, if they are in map use the map version, otherwise stick to the default template version in the list
// we can look for deltaeval here, where 
// i can write 2 functions, getTotalEval(Slot) nad getDeltaEval(Slot)



public class Algorithm {

    // fields...

    // main.java will set the 4 penalties and 4 weights here
    public static double pen_coursemin; // only add these 2 min penalties when remaining course/lab count would result in this being below (then every level this increases because the available pool gets smaller)
    public static double pen_labsmin;
    public static double pen_notpaired;
    public static double pen_section;

    public static double w_minfilled;
    public static double w_pref;
    public static double w_pair;
    public static double w_secdiff;

    public static boolean _isNegWeightOrPenalty; // true if at least one is negative

    AndTree _mainTree;

    // methods...

    public Algorithm() {
        _mainTree = new AndTree();
        // call the trees init function
    }

    // must be called after statics are externally set...
    public static void setNegativeWeightOrPenalty() {
        _isNegWeightOrPenalty = (pen_coursemin < 0 || pen_labsmin < 0 || pen_notpaired < 0 || pen_section < 0 || w_minfilled < 0 || w_pref < 0 || w_pair < 0 || w_secdiff < 0);
    }




    public void processTree() { // work in here

    }

}









// OLD PLAN:
    /*
        build the AND-TREE recursively
        0. preprocess by starting with a blank Slot array, foreach partialassign (found in input file and ONLY 1 per course), put 1 in at a time and check hardconstraints
        1. next we look at the LEFTMOST NULL entry in array, place next entry of PossibleSlots into Slot array, check hardconstraints and calculate Eval. If hardconstraints is broken OR _currentEval > _bestEval then return (closes the branch)
        2. if Slot array becomes full and hardconstraint is satisfied and currentEval < bestEval then update bestAssign as a copy of this array and update bestEval to currentEval
        3. return;
        4. goal condition (stopping condition is when all branches are closed - meaning there is no morepossible slots to use)

        NOTE: since each recursive level only makes 1 CHANGE (to leftmost null slot)

    */

    // ~~~~~~~~~ every time we add a PossibleSlot into _currentAssign at index i, we should call a function in Slot to add index i to its list of assigned course indices (also increment appropriate count field). When we return (recursive backtrack) we need to remember to remove this index from the Slots indices list
