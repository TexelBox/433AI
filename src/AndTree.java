
/*

THIS WILL WORK! i hope...

init root by applying the same algorithm 1 by one (that way i can use same functions)

init node as <NULL, ...., NULL> // all nulls, push into leaves (now we have 1 leaf)

for (lenggth of non nulls in partassignments) {
	take leaf and change the problem index corresponding to next partassign to create a new child node,
	we pop off the old leaf and we push this new leaf (now we have 1 leaf)	
}

after all this we have 1 leaf which we can call the root, but it will have a depth of #of nonnulls in partassign


*/

// NOTE: the way we keep track of courses that are already assigned is by looking at the problem vector to see if its a null or not...

import java.util.ArrayList;
import java.util.List;

public class AndTree {

    // fields...

    public Node _root; // starting point
    public List<Node> _leaves = new ArrayList<Node>(); // ~~~~~~~~~~~~~maybe use a stack instead since we are checking based on least number of NULLS which when we expand, the new leaves get put on top of stack and have less NULL than everything else

    // thus we will have NO VALID SOLUTION once we reach the goal condition (stop) and _bestAssign is NULL, or now use _foundValidAssign == false
    public Slot[] _bestAssign; // remember our best _currentAssign that we had (if we had one that was valid and complete)
    public double _bestEval = Double.POSITIVE_INFINITY; 

    public boolean _foundValidAssign = false; // set this true once we find first valid assign

    // methods...

    public AndTree() {
        // ~~~~init root before adding!!!, ether here or at declaration
        // do all the partassign checks here or maybe have the root be all NULL then we run another function to initialize the tree by expanding a chain 1 partassign at a time and checking hard cobstraints
        // NOTE: eval doesn't need to be calculated until we find our first best solution (optimization), have a flag in here to specify when to start
        initRoot();
        _leaves.add(_root);
    }

    // here initialize the root node as a node w/ problem = <NULL, NULL, ... partassign, .., NULL> and sol = ? (false)
    // will basically be running the algorithm without fLeaf and fTrans here, since we want to hard code control of what to use next.
    private void initRoot() {

        // we want our root problem to look like Input.getInstance()._partialAssignments
        // so we need to take that array right there and deep copy it to get our problem
        // we then call variant function of checkHardconstarints to check over every cell in thus problem
        // if no hard cons violated, we then call variant function of getEval which works over entire problem
        // we set eval to this, sol to false, changedIndex to -1 (means we dont have to check this node at all)
        // then set root to this new node and then begin search
        // ~~~~~~~~~~~~ACTUALLY it would be beneficial to set the depth = number of non-nulls in array, so that I can subtract TotalNumberOfCourses/Labs - labcount/leccount and if this difference is < coursemin/coursemax - leccount/labcount then I can add this part of the Eval and flip the flag (to say dont add this again)



        //Slot[] problem = new Slot[Input.getInstance()._courseList.size()]; // all NULLs
        //boolean sol = false; // start with sol = ?
        //int depth = 0;
        //double eval = 0;
        //int changedIndex = -1 // no change
        //_root = new Node(blankProb, sol, depth, eval, changedIndex);

        
    }

    // 3. f_leaf needs to be implemented inside the AndTree class (choose the leaf with the least dollar signs; or if there are ties, choose the leftmost leaf).
    public Node fLeaf() {
        // NOTE: least $ (NULL) would be the leaf with largest depth
        // NOTE: could just tiebreak this with a random leaf (since leftmost is kinda arbitrary in code, you would need to sort the leaves list everytime you expand which is inneficient
        return null;
    }

    // 4. f_trans needs to be implemented inside the AndTree class (either close the leaf - this is changing sol to 'yes'; OR expand it). f_trans uses Constr and Eval to decide which option to go with.
    // Param: chosenLeaf - is the node returned by fLeaf()
    public void fTrans(Node chosenLeaf) {
        // 5. after f_trans, we remove the leaf (whether it's closed or not) from the list of leaves.

        // 6. If you choose to close the leaf, you need to FIRST check if it's getting closed because it violates Constr or the Eval is more than the bestEval. 
        // THEN If we are closing the leaf because the problem is solved (as in we found all the slot assignments; there are no NULLs), then we update the bestEval and bestAssign accordingly. 
        // Note that we can only check Eval after a valid solution is found (if bestAssign != NULL).
    }

}