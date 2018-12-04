
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class Node {

    public Slot[] _problem; // only differs from its parent by one slot (leftmost null was changed in current plan), except in the case of the partassign expansions
    public boolean _sol; // false = ?, true = 'yes'

    // ~~~~ensure that the depth always equals the number of courses that have been assigned (non-$) in this problem.
    public int _depth; // depth 0 = root

    public double _eval; // eval of _problem (assignment)

    public int _changedIndex; // which index in parent's _problem was assigned a slot to become this _problem

    public int _remainingCoursesCount; // number of courses that haven't been assigned a slot yet
    public int _remainingLabsCount; // number of labs that haven't been assigned a slot yet
    
    public List<ArrayList<Course>> _coursesAssignedToSlots; // kinda like the reverse of _problem (here we keep track of the courses assigned to a slot rather than vice versa)

    public int[] _lectureCounts; // element i will be the lecture count of slot i
    
    public int[] _labCounts;
    
    
    

    //public Map<Integer,Slot> _assignedSlots = new HashMap<Integer,Slot>(); // stores the deep-copied (updated/assigned) slots by their index

    //public Map<Integer,Slot> _assignedSlotsCopies = new HashMap<Integer,Slot>(); // stores the copies of each non-null slot in _problem
    

    // stores the course/lab indices that were already considered as the left element of a pair (for pair eval)
    //public Set<Integer> _courseIndicesAlreadyCheckedForByPair = new HashSet<Integer>(); // init empty, don't need to pass on to children

    // stores the course/lab indices that were already considered as the left element of a pair (for secdiff eval)
    //public Set<Integer> _courseIndicesAlreadyCheckedForBySecdiff = new HashSet<Integer>(); // init empty, don't need to pass on to children

    //public boolean _isFull = true; // assume its full until we prove it has a null

    
    


    // ONLY PASS IN -1 TO CHANGEINDEX WHEN WORKING WITH EMPTY NODE
    public Node(Slot[] problem, boolean sol, int depth, int changedIndex, int remainingCoursesCount, int remainingLabsCount, List<ArrayList<Course>> coursesAssignedToSlots, int[] lectureCounts, int[] labCounts) {
        _problem = problem;
        _sol = sol;
        _depth = depth;
        _changedIndex = changedIndex;
        _remainingCoursesCount = remainingCoursesCount;
        _remainingLabsCount = remainingLabsCount;
        _coursesAssignedToSlots = coursesAssignedToSlots;
    }



    // return True if full (depth (#of non-$ / # of assigned courses) = problem vector length)
    public boolean isFull() {
        return _depth == _problem.length; // no null in problem
    }


    /*
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
	*/

    
    
    public void setEval() {
    	double evalMinfilled = getEvalMinfilled();
        int evalPref = getEvalPref();
        double evalPair = getEvalPair();
        double evalSecdiff = getEvalSecdiff(); 

        _eval = evalMinfilled * Algorithm.w_minfilled + evalPref * Algorithm.w_pref + evalPair * Algorithm.w_pair + evalSecdiff * Algorithm.w_secdiff;
    	
    }
    
    private double getEvalMinfilled() {
    	
    	double evalMinfilled = 0;
    	
    	for (int i = 0; i < _coursesAssignedToSlots.size(); i++) { // loop over every slot...
    		Slot slot = Input.getInstance()._slotList.get(i);
    		if (slot._isCourseSlot) { // if this is a course slot... (only need to consider coursemax/min)
    			// calculate the max number of courses that could be assigned to this slot in the future...
                int maxPossibleLecCount = _lectureCounts[i] + _remainingCoursesCount; // assume we could add all remaining unassigned courses into this slot
                // now calculate the difference between this max and our coursemin
                int diff = slot._coursemin - maxPossibleLecCount;
                if (diff > 0) { // for every course under the min, we add pen_coursemin
                    evalMinfilled = diff * Algorithm.pen_coursemin;
                }
                else {
                    evalMinfilled = 0;
                }
    		}
    		else { // if this is a lab slot... (only need to consider labmax/min)
                // calculate the max number of labs that could be assigned to this slot in the future...
                int maxPossibleLabCount = _labCounts[i] + _remainingLabsCount; // assume we could add all remaining unassigned labs into this slot
                // now calculate the difference between this max and our labmin
                int diff = slot._labmin - maxPossibleLabCount;
                if (diff > 0) { // for every lab under the min, we add pen_labsmin
                    evalMinfilled = diff * Algorithm.pen_labsmin;
                }
                else {
                    evalMinfilled = 0;
                }
            }
    	}

        return evalMinfilled;
    	
    }
    
    private int getEvalPref() {
    	
    	int evalPref = 0;

    	for (int i = 0; i < _coursesAssignedToSlots.size(); i++) { // loop over every slot...
    		
    		for (int j = 0; j < _problem.length; j++) { // loop through every course...
    			
    			// can now get pref[j][i] of course j with slot i
    			// only add to evalPref if the course is 1) ASSIGNED and 2) ASSIGNED TO ANOTHER SLOT
    			
    			Slot assignedSlot = _problem[j]; // get the slot assigned to this course currently...
    			
    			if (assignedSlot == null) { // if this course hasnt been assigned yet...
    				continue; // skip it
    			}
    			
    			// get here and you know course is assigned to a slot, but is it this one?
    			
    			if (assignedSlot._hashIndex == i) { // if this course is assigned to this slot...
    				continue; // skip it
    			}
    			
    			// get here if course is assigned to a different slot
    			// now we can add the pref value (note: if no line was in file, the pref value is 0, thus we can still add)
    			
    			evalPref += Input.getInstance()._preferences[j][i];
    			
    		}
    		
    		
    	}

        return evalPref;
    	
    }
    
    // penalty if 2 paired courses/labs aren't assigned to same day and start time and end time (same slot or twin slot)
    private double getEvalPair() {
    	
    	/*
    	double evalPair = 0;

        // loop over _courseIndices,
        // we have an index, which we can look up its row in _pairs
        // if the entry is TRUE, then we check if that paired index is assigned yet...
        // if it is ASSIGNED to a DIFFERENT slot, then we add pen_notpaired, and then we can can flag this index as checked (only flag the left index as checked), that way when it becomes the right index in another slot's function, we ignore it

        for (int nextCourseIndex : _courseIndices) { // for each class stored in this slot...
            for (int otherIndex = 0; otherIndex < Input.getInstance()._courseList.size(); otherIndex++) {

                if (node._courseIndicesAlreadyCheckedForByPair.contains(otherIndex)) { // if this index was already check as a previous nextCourseIndex (either in this slot or other slots)...
                    continue; // skip it (prevent symmetry causing 2*penalty)
                }

                if (Input.getInstance()._pairs[nextCourseIndex][otherIndex]) { // if we expect these 2 to be paired up...

                    if (_courseIndices.contains(otherIndex)) { // if both courses are assigned to this slot (paired up), then we don't want to add this penalty
                        continue;
                    }

                    if (_hasTwin) {
                        Slot twinSlot;
                        if (node._assignedSlots.containsKey(_twinSlotIndex)) { // if in our map..
                            twinSlot = node._assignedSlots.get(_twinSlotIndex); // get from map if there is one
                        }
                        else { // if not in map (not assigned to any course yet)
                            // get the template from _slotList
                            twinSlot = Input.getInstance()._slotList.get(_twinSlotIndex);
                        }

                        if (twinSlot._courseIndices.contains(otherIndex)) {
                            continue;
                        }
                    }
                    

                    // get here if othercourse isn't assigned to this slot (or its twin slot if it has one)
                    // 2 cases, either not assigned at all (don't add penalty yet) or assigned to a different slot (now we can add penalty)

                    if (node._problem[otherIndex] == null) { // unassigned course
                        continue;
                    }

                    // get here and we know that this other course is assigned to a slot that isn't this one, so we now add the penalty...
                    evalPair += Algorithm.pen_notpaired;
                }
            }

            node._courseIndicesAlreadyCheckedForByPair.add(nextCourseIndex); // flag that we have already checked all the pairs containing this course (don't check again and add penalty again!)
        }

        return evalPair;
        */
    	
    }
    
    private double getEvalSecdiff() {
    	
    }
    
    
    
    
    
    
    
    // Return : True - if all hard constraints were satisfied...
    public boolean checkHardConstraints() {
    	
    	if (_changedIndex == -1) { // safety case for empty node
    		return true;
    	}
    	
    	// only need to check over the slot assigned to the course at changedIndex (only change from parent)
    	
    	Slot changedSlot = _problem[_changedIndex]; 
        return changedSlot.checkHardConstraints(this);
    	
    }
    
    
    


    // closing node means were done with it
    // symbolic (not really used anywhere)
    public void close() {
        _sol = true; // sol = 'yes'
    }

   
    public void expand(boolean doPartAssign, int partAssignChangedIndex) {

        // based on doPartAssign figure out which index in _problem we are changing

        int newChangedIndex = -1;

        Slot forcedSlot = Input.getInstance()._partialAssignments[partAssignChangedIndex];
        List<Slot> forcedSlotSingletonList = new ArrayList<Slot>();
        forcedSlotSingletonList.add(forcedSlot);
        
        if (doPartAssign) {
            newChangedIndex = partAssignChangedIndex;
        }
        else {
        	// find leftmost null in _problem
        	for (int i = 0; i < _problem.length; i++) {
        		if (_problem[i] == null) {
        			newChangedIndex = i;
        			break;
        		}
        	}
        	
        }
        
        // ~~~~~~~~~~NOTE: newChangedIndex should always be ovewrwriten, if it isn't it will be -1 causing an arrayoutofboundsexception below to indicate a bug is somewhere


        // 2. figure out how many children we have...
        // get if index is for a course or a lab then permute over proper list
        
        Course courseToAssign = Input.getInstance()._courseList.get(newChangedIndex);


        // NOTE: parser ensures that partassign can have only matching types)...

        List<Slot> permutationList;

        if (doPartAssign) {
            permutationList = forcedSlotSingletonList;
        }
        else {
            if (courseToAssign._isLecture) {
            	permutationList = Input.getInstance()._courseSlotList; 
            }
            else {
            	permutationList = Input.getInstance()._labSlotList; 
            }
        }

        for (Slot slot : permutationList) {
            // create a single child each iteration

            // deep copy our _problem array (dont need to deep copy Slots though since they will never change)
        	
        	Slot[] childProblem = new Slot[_problem.length]; // problems will be same size
        	
        	// now copy over the slot references...
        	
        	for (int i = 0; i < _problem.length; i++) { // copy all slot refs from parent prob to child prob
        		childProblem[i] = _problem[i]; 
        	}
        	
        	// now overwrite our null at newChangedIndex with slot reference
        	
        	childProblem[newChangedIndex] = slot;
        	
        	// now childProblem is COMPLETE
        	
        	// now change the 3 fields that i moved from slot to here...
        	
        	List<ArrayList<Course>> childCoursesAssignedToSlots = new ArrayList<ArrayList<Course>>(_coursesAssignedToSlots.size()); // empty
        	
        	// now copy over a copy of inner lists....
        	
        	for (ArrayList<Course> innerList : _coursesAssignedToSlots) {
        		ArrayList<Course> childInnerList = new ArrayList<Course>(); // empty
        		for (Course c : innerList) {
        			childInnerList.add(c);
        		}
        		childCoursesAssignedToSlots.add(childInnerList);
        	}
        	
        	// now add the newChangedCourse/Lab into the proper slot list
        	
        	childCoursesAssignedToSlots.get(slot._hashIndex).add(courseToAssign);
        	
        	// now childCoursesAssignedToSlots is COMPLETE
        	
        	
        	int[] childLectureCounts = new int[_lectureCounts.length];
        	
        	for (int i = 0; i < _lectureCounts.length; i++) {
        		childLectureCounts[i] = _lectureCounts[i];
        	}
        	
        	
        	int[] childLabCounts = new int[_labCounts.length];
        	
        	for (int i = 0; i < _labCounts.length; i++) {
        		childLabCounts[i] = _labCounts[i];
        	}
        	
        	if (courseToAssign._isLecture) {
        		// now increment lecture count for this courseSlot (since it is being assigned to a new lecture)
            	childLectureCounts[slot._hashIndex] = childLectureCounts[slot._hashIndex] + 1;
        	}
        	else { // if lab
        		// now increment lab count for this labSlot (since it is being assigned to a new lab)
            	childLabCounts[slot._hashIndex] = childLabCounts[slot._hashIndex] + 1;
        	}
        	
        	// create child node...
        	Node nextChild;
        	if (courseToAssign._isLecture) {
        		nextChild = new Node(childProblem, false, _depth+1, newChangedIndex, _remainingCoursesCount-1, _remainingLabsCount, childCoursesAssignedToSlots, childLectureCounts, childLabCounts);
        	}
        	else {
        		nextChild = new Node(childProblem, false, _depth+1, newChangedIndex, _remainingCoursesCount, _remainingLabsCount-1, childCoursesAssignedToSlots, childLectureCounts, childLabCounts);
        	}
        	
            AndTree._leaves.push(nextChild);
            
        }
    }    

}


 
 
 
 