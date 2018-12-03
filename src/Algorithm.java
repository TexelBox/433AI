


public class Algorithm {

    // fields...

    AndTree _mainTree;

    // methods...

    public Algorithm() {
        _mainTree = new AndTree();
        // call the trees init function
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
