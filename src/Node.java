
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class Node {

    public Slot[] _problem; // only differs from its parent by one slot (leftmost null was changed in current plan)
    public boolean _sol; // false = ?, true = 'yes'

    //public Node _parent; // may not be necessary (no backtracking)
    //public List<Node> _children = new ArrayList<Node>(); // may not be necessary (too much memory), could put in AndTree~~~~~~~~~~~~~

    // ~~~~ensure that the depth always equals the number of courses that have been assigned in this problem.
    public int _depth; // depth 0 = root

    public double _eval; // eval of _problem (assignment), _eval = parent._eval + deltaEval

    public int _changedIndex; // which index in parent's _problem was assigned a slot to become this _problem


    public int _remainingCoursesCount;
    public int _remainingLabsCount;



    public Map<Integer,Slot> _assignedSlots = new HashMap<Integer,Slot>();

    public Set<Integer> _courseIndicesAlreadyCheckedForByPair = new HashSet<Integer>(); // init empty, don't need to pass on to children

    public Set<Integer> _courseIndicesAlreadyCheckedForBySecdiff = new HashSet<Integer>(); // init empty, don't need to pass on to children



    public Node(Slot[] problem, boolean sol, int depth, double eval, int changedIndex, int remainingCoursesCount, int remainingLabsCount) {
        _problem = problem;
        _sol = sol;
        _depth = depth;
        //_eval = eval;
        _changedIndex = changedIndex;
        _remainingCoursesCount = remainingCoursesCount;
        _remainingLabsCount = remainingLabsCount;
        setAssignedSlots();
    }


    private void setAssignedSlots() {
        for (Slot slot : _problem) {
            if (slot != null) {
                int key = slot._hashIndex;
                if (!_assignedSlots.containsKey(key)) { // if we didn't already put this slot in map... (prevent redundant putting)
                    _assignedSlots.put(key, slot);
                }
            }
        }
    }


    // ~~~~~~~~~~~~for now in the unoptimized form, i don't use the parent's eval and then add a deltaEval, I simply recalculate everything
    // ~~~~~~~~~~~~I need to add a safety check, if one of the weights/pens is negative then we don't call setEval unless the problem is full (makes the search take longer but at least we don't lose valid/optimal solutions)
    // trying to get something to work 
    public void setEval() {
        // calculate Eval(_problem)
        double constructedEval = 0;
        // iterate over _slotList and if the slot is in _assignedSlots then use the instance found in there. Otherwise, use the template found in _slotList
        for (int index = 0; index < Input.getInstance._slotList.size(); index++) {
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






    public void expandNode() {
        // 7. If you choose to expand the leaf, choose the leftmost NULL/$ in the problem vector of our chosen leaf; and change the dollarsign to each possible slot and get n new leaves. 
        // Once we expand the chosen leaf and get the children leaves, first we add the children to the list of leaves, and we go back to f_leaf and loop.


        // 1. find leftmost null in _problem
        // 2. foreach possible slot, create a new child node (child problem = parent problem (deep copy) but with leftmost null replaced by this possible slot), 
        // sol = ?, set parent to this, set this.children to this new child node), depth = this.depth + 1
        // make sure to remove this node from AndTree's leaves list and add all new children to leaves list.
    }

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

    // ~~~~~~~~~NOTE: when a node is closed, we could set it to null in order to free up memory (done with it), make sure to remove it from the leaves list







}