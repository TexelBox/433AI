# 433AI

*To-Do List:*
1. Refine the parser.
2. Eval function needs to be implemented inside the Slot class. (For the course min, lab min soft constraints, there are two options: consider it at the end when you have a full solution, or consider it little bit earlier based on lecture count + number of courses that have not been assigned yet < course min).
3. f_leaf needs to be implemented inside the AndTree class (choose the leaf with the least dollar signs; or if there are ties, choose the leftmost leaf).
4. f_trans needs to be implemented inside the AndTree class (either close the leaf - this is changing sol to 'yes'; OR expand it). f_trans uses Constr and Eval to decide which option to go with.
5. after f_trans, we remove the leaf (whether it's closed or not) from the list of leaves.
6. If you choose to close the leaf, you need to FIRST check if it's getting closed because it violates Constr or the Eval is more than the bestEval. THEN If we are closing the leaf because the problem is solved (as in we found all the slot assignments; there are no NULLs), then we update the bestEval and bestAssign accordingly. Note that we can only check Eval after a valid solution is found (if bestAssign != NULL).
7. If you choose to expand the leaf, choose the leftmost NULL/$ in the problem vector of our chosen leaf; and change the dollarsign to each possible slot and get n new leaves. Once we expand the chosen leaf and get the children leaves, first we add the children to the list of leaves, and we go back to f_leaf and loop.
8. Output should look like specified (look at examples provided by Jorg).
9. Make test files and run them.
10. ASK QUESTIONS TO JORG!!!

*Distribution of Work:*
*!!! FINISH YOUR PARTS BEFORE TUESDAY AT THE LATEST !!! *
Aaron:
- #1, #2, #10

Esther:
- #3, #4, #5, #6

Cynthia:
- #7, #8

Herman:
- #9

*Next Meeting:*
- Tuesday, December 4, 2018 @ 3:30 pm
