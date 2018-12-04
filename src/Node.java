
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class Node {

    public Slot[] _problem; // only differs from its parent by one slot (leftmost null was changed in current plan)
    public boolean _sol; // false = ?, true = 'yes'

    // ~~~~ensure that the depth always equals the number of courses that have been assigned (non-$) in this problem.
    public int _depth; // depth 0 = root

    public double _eval; // eval of _problem (assignment)

    public int _changedIndex; // which index in parent's _problem was assigned a slot to become this _problem

    public int _remainingCoursesCount; // number of courses that haven't been assigned a slot yet
    public int _remainingLabsCount; // number of labs that haven't been assigned a slot yet

    public Map<Integer,Slot> _assignedSlots = new HashMap<Integer,Slot>(); // stores the deep-copied (updated/assigned) slots by their index

    public Map<Integer,Slot> _assignedSlotsCopies = new HashMap<Integer,Slot>(); // stores the copies of each non-null slot in _problem
    

    // stores the course/lab indices that were already considered as the left element of a pair (for pair eval)
    public Set<Integer> _courseIndicesAlreadyCheckedForByPair = new HashSet<Integer>(); // init empty, don't need to pass on to children

    // stores the course/lab indices that were already considered as the left element of a pair (for secdiff eval)
    public Set<Integer> _courseIndicesAlreadyCheckedForBySecdiff = new HashSet<Integer>(); // init empty, don't need to pass on to children

    public boolean _isFull = true; // assume its full until we prove it has a null


    //private List<Node> _children = new ArrayList<Node>(); // init empty


    // ONLY PASS IN -1 TO CHANGEINDEX WHEN WORKING WITH EMPTY NODE
    public Node(Slot[] problem, boolean sol, int depth, int changedIndex, int remainingCoursesCount, int remainingLabsCount) {
        _problem = problem;
        _sol = sol;
        _depth = depth;
        _changedIndex = changedIndex;
        _remainingCoursesCount = remainingCoursesCount;
        _remainingLabsCount = remainingLabsCount;
        setAssignedSlots();
    }


    // place the deep copied slot instances in here (those that have been assigned and that differ from _slotList instances by the 3 fields)
    private void setAssignedSlots() {
        for (Slot slot : _problem) {
            if (slot != null) {
                int key = slot._hashIndex;
                if (!_assignedSlots.containsKey(key)) { // if we didn't already put this slot in map... (prevent redundant putting)
                    _assignedSlots.put(key, slot);
                }
            }
            else { // if we found a null
                _isFull = false;
            }
        }
    }


    /* use this in the future?
    // return True if full (depth (#of non-$ / # of assigned courses) = problem vector length)
    public boolean isFull() {
        return _depth == _problem.length;
    }
    */


    // ~~~~~~~~~~~~for now in the unoptimized form, i don't use the parent's eval and then add a deltaEval, I simply recalculate everything
    // ~~~~~~~~~~~~I need to add a safety check, if one of the weights/pens is negative then we don't call setEval unless the problem is full (makes the search take longer but at least we don't lose valid/optimal solutions)
    public void setEval() {
        // calculate Eval(_problem)
        double constructedEval = 0; // build up from 0
        // iterate over _slotList and if the slot is in _assignedSlots then use the instance found in there. Otherwise, use the template found in _slotList
        for (int index = 0; index < Input.getInstance()._slotList.size(); index++) {
            Slot nextSlot;
            if (_assignedSlots.containsKey(index)) { // use assigned slot
                nextSlot = _assignedSlots.get(index);
            }
            else { // use empty slot (template)
                nextSlot = Input.getInstance()._slotList.get(index);
            }

            // now that we have our slot, get its eval and add to tally
            constructedEval += nextSlot.getEval(this); // pass in this node
        }

        _eval = constructedEval;
    }


    // Return : True - if all hard constraints were satisfied...
    public boolean checkHardConstraints() {

        // only need to check over the slot assigned to the course at changedIndex (only change from parent)

        Slot changedSlot = _problem[_changedIndex]; 
        return changedSlot.checkHardConstraints(this);
    }


    // closing node means were done with it
    public void close() {
        _sol = true; // sol = 'yes'
    }

    public void expand(boolean doPartAssign, int partAssignChangedIndex) {

        // based on doPartAssign figure out which index in _problem we are changing

        int newChangedIndex = 0;

        Slot forcedSlot = Input.getInstance()._partialAssignments[partAssignChangedIndex];
        List<Slot> forcedSlotSingletonList = new ArrayList<Slot>();
        forcedSlotSingletonList.add(forcedSlot);
        
        if (doPartAssign) {
            newChangedIndex = partAssignChangedIndex;
        }
        else {
            // 1. find leftmost null, which would be right of our last leftmost null (changedIndex)
            for (int i = _changedIndex+1; i < _problem.length; i++) {
                if (_problem[i] == null) {
                    newChangedIndex = i;
                    break;
                }
            }
        }


        // 2. figure out how many children we have...
        // get if index is for a course or a lab then permute over proper list

        Course courseToAssign = Input.getInstance()._courseList.get(newChangedIndex);
        if (courseToAssign._isLecture) {

            // NOTE: parser ensures that this slot is a course slot...

            List<Slot> permutationList;

            if (doPartAssign) {
                permutationList = forcedSlotSingletonList;
            }
            else {
                // use courseslotlist
                permutationList = Input.getInstance()._courseSlotList; 
            }



            for (Slot courseSlot : permutationList) {
                // create a single child

                // deep copy our _problem

                Slot[] newProblem = new Slot[_problem.length]; // problems will be same size

                // now just deep copy non-null elements into same slot

                for (int i = 0; i < _problem.length; i++) {
                    Slot aSlot = _problem[i];
                    if (aSlot != null) { 

                        // overwrite null in newproblem with a deep copy of aSlot

                        if (!_assignedSlotsCopies.containsKey(aSlot._hashIndex)) { // if map doesnt already contain this copy
                            // deep copy from _assignSlots map (which happens to be same as reference in _problem)
                            // use aSlot
                            Slot newSlot = new Slot(aSlot); // deep copy aSlot
                            newProblem[i] = newSlot;
                            _assignedSlotsCopies.put(newSlot._hashIndex, newSlot);
                        }
                        else { // a copy sharing this slot index was already made and stored inside _assignedSlotCopies map
                            // retrieve copy from copy map...
                            Slot newSlot = _assignedSlotsCopies.get(aSlot._hashIndex);
                            newProblem[i] = newSlot;
                        }
                        
                    }
                }

                // get here with a deep copy problem of parent

                // now overwrite the newChangedIndex (leftmost null) entry with a different permutation

                int courseSlotIndex = courseSlot._hashIndex;

                Slot overwriteSlot;
                if (_assignedSlotsCopies.containsKey(courseSlotIndex)) { // if we want to overwrite with a slot that already exists as a copy in our copy map...
                    overwriteSlot = _assignedSlotsCopies.get(courseSlotIndex);
                    overwriteSlot._courseIndices.add(newChangedIndex); // assign course into slot's course list
                    overwriteSlot._lectureCount++; // increment lecture count
                }
                else { // map doesnt contain copy...
                    Slot slotToCopy = courseSlot;
                    overwriteSlot = new Slot(slotToCopy);
                    overwriteSlot._courseIndices.add(newChangedIndex); // assign course into slot's course list
                    overwriteSlot._lectureCount++; // increment lecture count
                }

                newProblem[newChangedIndex] = overwriteSlot; 

                // now we have the final problem to put in child node...


                // create child node

                Node nextChild = new Node(newProblem, false, _depth+1, newChangedIndex, _remainingCoursesCount-1, _remainingLabsCount);

                AndTree._leaves.push(nextChild);
                
            }
        }
        else { // is lab...

            // NOTE: parser ensures that this slot is a lab slot...

            List<Slot> permutationList;

            if (doPartAssign) {
                permutationList = forcedSlotSingletonList;
            }
            else {
                // use labslotlist                
                permutationList = Input.getInstance()._labSlotList; 
            }


            for (Slot labSlot : permutationList) {
                
                // create a single child

                // deep copy our _problem

                Slot[] newProblem = new Slot[_problem.length]; // problems will be same size

                // now just deep copy non-null elements into same slot

                for (int i = 0; i < _problem.length; i++) {
                    Slot aSlot = _problem[i];
                    if (aSlot != null) { 

                        // overwrite null in newproblem with a deep copy of aSlot

                        if (!_assignedSlotsCopies.containsKey(aSlot._hashIndex)) { // if map doesnt already contain this copy
                            // deep copy from _assignSlots map (which happens to be same as reference in _problem)
                            // use aSlot
                            Slot newSlot = new Slot(aSlot); // deep copy aSlot
                            newProblem[i] = newSlot;
                            _assignedSlotsCopies.put(newSlot._hashIndex, newSlot);
                        }
                        else { // a copy sharing this slot index was already made and stored inside _assignedSlotCopies map
                            // retrieve copy from copy map...
                            Slot newSlot = _assignedSlotsCopies.get(aSlot._hashIndex);
                            newProblem[i] = newSlot;
                        }
                        
                    }
                }

                // get here with a deep copy problem of parent

                // now overwrite the newChangedIndex (leftmost null) entry with a different permutation

                int labSlotIndex = labSlot._hashIndex;

                Slot overwriteSlot;
                if (_assignedSlotsCopies.containsKey(labSlotIndex)) { // if we want to overwrite with a slot that already exists as a copy in our copy map...
                    overwriteSlot = _assignedSlotsCopies.get(labSlotIndex);
                    overwriteSlot._courseIndices.add(newChangedIndex); // assign lab into slot's course list
                    overwriteSlot._labCount++; // increment lab count
                }
                else { // map doesnt contain copy...
                    Slot slotToCopy = labSlot;
                    overwriteSlot = new Slot(slotToCopy);
                    overwriteSlot._courseIndices.add(newChangedIndex); // assign lab into slot's course list
                    overwriteSlot._labCount++; // increment lab count
                }

                newProblem[newChangedIndex] = overwriteSlot; 

                // now we have the final problem to put in child node...


                // create child node

                Node nextChild = new Node(newProblem, false, _depth+1, newChangedIndex, _remainingCoursesCount, _remainingLabsCount-1);


                AndTree._leaves.push(nextChild);

                
            }

        }
        
    }

    /*
    public void pushChildren() {
        for (Node child : _children) {
            AndTree._leaves.push(child);
        }
    }
    */


}





/*
    public void expandNode() {
        // 7. If you choose to expand the leaf, choose the leftmost NULL/$ in the problem vector of our chosen leaf; and change the dollarsign to each possible slot and get n new leaves. 
        // Once we expand the chosen leaf and get the children leaves, first we add the children to the list of leaves, and we go back to f_leaf and loop.


        // 1. find leftmost null in _problem
        // 2. foreach possible slot, create a new child node (child problem = parent problem (deep copy) but with leftmost null replaced by this possible slot), 
        // sol = ?, set parent to this, set this.children to this new child node), depth = this.depth + 1
        // make sure to remove this node from AndTree's leaves list and add all new children to leaves list.

        // make sure to INIT ALL NECESSARY NODE FIELDS AND UPDATE THE 3 FIELDS IN CHANGEDSLOT
        // FOR DEEP COPY PERMUTATIONS, IF SLOT IS IN _ASSIGNEDSLOTS THEN WE DEEP COPY THAT ONE AND ADD TO ITS LIST AND INCREMENT PROPER INDEX
        // IF NOT IN LIST WE DEEPCOPY _SLOTLIST AS USUAL
    }
    */

    /*
    MOVE THIS INTO FTRANS
    // evaluate the problem (partial assignment) in this node before deciding whether to...
    // 1. close it unfavorably (set sol to 'yes') - do this if hard constraints are violated or if this.eval >= bestEval (found so far)
    // 2. expand it - do this if hard cons are good, this.eval < bestEVal and there it still a leftmost null in _problem
    // 3. close it favorably (set sol to 'yes' and update bestEval = this.EVal and bestAssign = _problem) - do this when no more nulls left and hardcons are good and this.eval < bestEVal
    public boolean evaluateProblem() {
        // NOTE: only need it to evaluate the different element of _problem from parent's _problem (1 slot has changed),
        // so when the parent expands, it should pass in the index of the arrayList that was changed.
        // now that we have this index, we evaluate this slot by...
        // a. checkHardConstraints(slot) // returns true or false
        // b. evaluateSoftConstraints(slot) // returns the eval of the slot???
        
        // if a. is false || b. >= bestEval:
        // then case 1.

        // else if there is still a leftmost NULL in _problem:
        // then case 2.

        // else:
        // then case 3.

        return true;
    }
    */

    // ~~~~~~~~~NOTE: when a node is closed, we could set it to null in order to free up memory (done with it), make sure to remove it from the leaves list