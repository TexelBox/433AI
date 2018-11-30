# 433AI
To-Do List:
- Refine the parser.
- Eval function needs to be implemented inside the Slot class.
- f_leaf needs to be implemented inside the AndTree class (choose the leaf with the least dollar signs; or if there are ties, choose the leftmost leaf).
- f_trans needs to be implemented inside the AndTree class (either close the leaf - this is changing sol to 'yes'; OR expand it). f_trans uses Constr and Eval to decide which option to go with.
- after f_trans, we remove the leaf (whether it's closed or not) from the list of leaves.
- If you choose to close the leaf, you need to FIRST check if it's getting closed because it violates Constr or the Eval is more than the bestEval. THEN If we are closing the leaf because the problem is solved (as in we found all the slot assignments; there are no NULLs), then we update the bestEval and bestAssign accordingly. Note that we can only check Eval after a valid solution is found (if bestAssign != NULL).
- If you choose to expand the leaf, choose the leftmost NULL/$ in the problem vector of our chosen leaf; and change the dollarsign to each possible slot and get n new leaves. Once we expand the chosen leaf and get the children leaves, first we add the children to the list of leaves, and we go back to f_leaf and loop.
- Make test files and run them.
- ASK QUESTIONS TO JORG!!!

Distribution of Work:
Aaron:
- Finish the parser
- 

Esther:
- f_leaf
- Eval
- f_trans

Cynthia:


Herman:
