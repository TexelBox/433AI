
import java.util.ArrayList;
import java.util.List;

public class Node {

    public Slot[] _problem; 
    //public List<Slot> _problem = new ArrayList<Node>(); // only differs from its parent by one slot (leftmost null was changed)
    public boolean _sol; // false = ?, true = 'yes'

    public Node _parent; // may not be necessary (no backtracking)
    public List<Node> _children = new ArrayList<Node>(); // may not be necessary (too much memory), could put in AndTree~~~~~~~~~~~~~

    public int _depth; // depth 0 = root

    public double _eval; // eval of _problem (assignment)

    public void expandNode() {
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