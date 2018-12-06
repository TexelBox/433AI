


// NOTE: the way we keep track of courses that are already assigned is by looking at the problem vector to see if its a null or not...

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AndTree {

    // fields...

    //public Node _root; // starting point
    public static Stack<Node> _leaves = new Stack<Node>();

    /*
    leaves will be pushed on top of the stack (and will be the leaves with the largest depth/ least amount of $/null)
    thus, f_leaf can just select the top of the stack to pop off...
    */

    // thus we will have NO VALID SOLUTION once we reach the goal condition (stop) and _bestAssign is NULL, or now use _foundValidAssign == false
    public Slot[] _bestAssign; // remember our best _currentAssign that we had (if we had one that was valid and complete)
    public double _bestEval = Double.POSITIVE_INFINITY; 

    public boolean _foundValidAssign = false; // set this true once we find first valid assign, use this 

    // methods...

    public AndTree() {
        initRoot(); // create first node with empty problem on stack
    }


    private void initRoot() {
        Slot[] blankProblem = new Slot[Input.getInstance()._courseList.size()]; // init all nulls

        int remainingCoursesCount = 0; // build up from 0
        int remainingLabsCount = 0;

        List<Course> theCourseList = Input.getInstance()._courseList;
        for (Course course : theCourseList) {
            if (course._isLecture) {
                remainingCoursesCount++;
            }
            else {
                remainingLabsCount++;
            }
        }
        
        
        List<ArrayList<Course>> coursesAssignedToSlots = new ArrayList<ArrayList<Course>>(Input.getInstance()._slotList.size()); // empty list (no inner lists yet)
        
        for (Slot sl : Input.getInstance()._slotList) { // for every defined slot...
        	ArrayList<Course> innerEmptyList = new ArrayList<Course>(); // empty
        	coursesAssignedToSlots.add(innerEmptyList);
        }
        
        
        int[] lectureCounts = new int[Input.getInstance()._slotList.size()]; // all 0 by default
        
        int[] labCounts = new int[Input.getInstance()._slotList.size()]; // all 0 by default
        

        Node root = new Node(blankProblem, false, 0, -1, remainingCoursesCount, remainingLabsCount, coursesAssignedToSlots, lectureCounts, labCounts);

        _leaves.push(root);
    }



    // 3. f_leaf needs to be implemented inside the AndTree class (choose the leaf with the least dollar signs; or if there are ties, choose the leftmost leaf).
    public Node fLeaf() {
        // NOTE: least $ (NULL) would be the leaf with largest depth
        // NOTE: could just tiebreak this with a random leaf (since leftmost is kinda arbitrary in code, you would need to sort the leaves list everytime you expand which is inneficient
        return _leaves.pop();
    }

    // 5. f_leaf pops the leaf that we check next from the list of leaves.

    // 4. f_trans needs to be implemented inside the AndTree class (either close the leaf - this is changing sol to 'yes'; OR expand it). f_trans uses Constr and Eval to decide which option to go with.
    // Param: chosenLeaf - is the node returned by fLeaf()
    public void fTrans(Node chosenLeaf, boolean doPartAssign, int partAssignChangedIndex) {

        if (chosenLeaf._changedIndex == -1) { // if coming from the empty start node...
            chosenLeaf.expand(doPartAssign, partAssignChangedIndex);
            return;           
        }

        // otherwise for all other nodes...

        boolean allHardConsSatisfied = chosenLeaf.checkHardConstraints();

        // CHECK VALIDITY...
        if (!allHardConsSatisfied) {
            // close node unfavorably (violation) - don't need to compare to best
            chosenLeaf.close();
            return;
        }

        // get here and no HCs are violated
        // CHECK OPTIMALITY...

        // 6. If you choose to close the leaf, you need to FIRST check if it's getting closed because it violates Constr or the Eval is more than the bestEval. 
        // THEN If we are closing the leaf because the problem is solved (as in we found all the slot assignments; there are no NULLs), then we update the bestEval and bestAssign accordingly. 
        // Note that we can only check Eval after a valid solution is found (if bestAssign != NULL).

        
        // only want to get eval when _foundValidAssign = true || chosenLeaf._isFull
        
        
        if (Algorithm._isNegWeightOrPenalty) {
        	double theEval = 0;
            if (chosenLeaf.isFull()) {
            	chosenLeaf.setEval();
                theEval = chosenLeaf._eval;
                if (_foundValidAssign) {
                	if (theEval < _bestEval) {
            			// update best then close node
                        updateBest(chosenLeaf._problem, theEval);
            		}
            		chosenLeaf.close(); // close cause its full
                }
                else {
                	// set best for the first time here and close (nothing to compare to, so we are the best)
                    updateBest(chosenLeaf._problem, theEval);
                    chosenLeaf.close();
                }
            }
            else {
            	// expand node - nothing to compare to (so we dont need to eval - which we didnt)
                chosenLeaf.expand(doPartAssign, partAssignChangedIndex); // it still has potential
            }
            return;
        }
        
        
        // get here if all weights/pens are non-negative...
        
        double theEval = 0;
        if (_foundValidAssign || chosenLeaf.isFull()) {
        	chosenLeaf.setEval();
            theEval = chosenLeaf._eval;
        }
        
        if (_foundValidAssign) {
        	if (chosenLeaf.isFull()) {
        		if (theEval < _bestEval) {
        			// update best then close node
                    updateBest(chosenLeaf._problem, theEval);
        		}
        		chosenLeaf.close(); // close cause its full
        	}
        	else {
        		if (theEval < _bestEval) {
                    // expand node
                    chosenLeaf.expand(doPartAssign, partAssignChangedIndex); // it has potential still
                }
                else {
                    // close node without updating best
                    chosenLeaf.close(); // future eval will still be worse            
                }
        	}
        }
        else { // havent found a valid assign yet...
        	if (chosenLeaf.isFull()) {
                // set best for the first time here and close (nothing to compare to, so we are the best)
                updateBest(chosenLeaf._problem, theEval);
                chosenLeaf.close();
        	}
        	else {
        		// expand node - nothing to compare to (so we dont need to eval - which we didnt)
                chosenLeaf.expand(doPartAssign, partAssignChangedIndex); // it still has potential
        	}
        }

    }
    
    
    
    public void updateBest(Slot[] newBestAssign, double newBestEval) {
        _bestAssign = newBestAssign;
        _bestEval = newBestEval;
        _foundValidAssign = true;
        Output.getInstance().outputValidSolution(); // overwrite the valid solution in file... (in case user terminates program after their patience has run out)
        if (Main._DEBUG) {
            
            long timePassed = System.currentTimeMillis() - Algorithm._starttime;
            System.out.println("BETTER SOLUTION FOUND, time passed = " + Long.toString(timePassed));

            Output.getInstance().logValidSolution();
            
            //Output.getInstance().printValidSolution();
        }
    }



}


//preprocesses the Input's _partasignments (which we know have either 0 or 1 forced slot assignements to a course/lab)
// we also know that they don't overlap an unwanted, so we dont need to check again, even though node will, but whatever
//public boolean preProcess() {
    // apply my heuristic below...
//}


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




