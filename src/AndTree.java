


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



    //public List<Node> _leaves = new ArrayList<Node>(); // ~~~~~~~~~~~~~maybe use a stack instead since we are checking based on least number of NULLS which when we expand, the new leaves get put on top of stack and have less NULL than everything else

    // thus we will have NO VALID SOLUTION once we reach the goal condition (stop) and _bestAssign is NULL, or now use _foundValidAssign == false
    public Slot[] _bestAssign; // remember our best _currentAssign that we had (if we had one that was valid and complete)
    public double _bestEval = Double.POSITIVE_INFINITY; 

    public boolean _foundValidAssign = false; // set this true once we find first valid assign, use this 

    // methods...

    public AndTree() {
        // ~~~~init root before adding!!!, ether here or at declaration
        // do all the partassign checks here or maybe have the root be all NULL then we run another function to initialize the tree by expanding a chain 1 partassign at a time and checking hard cobstraints
        // NOTE: eval doesn't need to be calculated until we find our first best solution (optimization), have a flag in here to specify when to start
        //initRoot(); // this will run through partassign and generate 

        initRoot();
    }


    private void initRoot() {
        Slot[] blankProblem = new Slot[Input.getInstance()._courseList.size()]; // init all nulls

        int remainingCoursesCount = 0; // start at 0
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

        Node root = new Node(blankProblem, false, 0, -1, remainingCoursesCount, remainingLabsCount);

        _leaves.push(root);
    }





    // preprocesses the Input's _partasignments (which we know have either 0 or 1 forced slot assignements to a course/lab)
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









    /*
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
    */


    







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

        if (chosenLeaf._changedIndex == -1) {
            chosenLeaf.expand(doPartAssign, partAssignChangedIndex);           
        }


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

        if (_foundValidAssign && chosenLeaf._isFull) {
            chosenLeaf.setEval();
            double theEval = chosenLeaf._eval;

            if (theEval < _bestEval) {
                // update best then close node
                updateBest(chosenLeaf._problem, theEval);
                chosenLeaf.close();
            }
            else {
                // close node without updating best
                chosenLeaf.close();
            }
        }
        else if (_foundValidAssign) { // only found valid (not full)
            chosenLeaf.setEval();
            double theEval = chosenLeaf._eval;

            if (theEval < _bestEval) {
                // expand node
                chosenLeaf.expand(doPartAssign, partAssignChangedIndex);
            }
            else {
                // close node without updating best
                chosenLeaf.close();            
            }
        }
        else if (chosenLeaf._isFull) { // only full (havent found valid yet)
            chosenLeaf.setEval();
            double theEval = chosenLeaf._eval;

            // set best for the first time here and close
            updateBest(chosenLeaf._problem, theEval);
            chosenLeaf.close();
        }
        else { // if we haven't found a valid and not full
            // expand node - nothing to comapre to
            chosenLeaf.expand(doPartAssign, partAssignChangedIndex);
        }

    }


    public void updateBest(Slot[] newBestAssign, double newBestEval) {
        _bestAssign = newBestAssign;
        _bestEval = newBestEval;
        _foundValidAssign = true;
    }



}