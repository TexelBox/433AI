import java.util.ArrayList;

public class Algorithm {

	private boolean DEBUG = false;
	private boolean DEBUG_PARAM = false;
	private int numTasks = 8;

	public int[][] machine_penalty = new int[8][8];
	
	// forced_partial_asignment[i] = j: task j must be assigned to
	// machine i. default: -1
	public Task[] forced_partial_assignment = new Task[8];
	
	// forbidden_machine[i][] = j: assigning task j to machine i is invalid
	public int[][] forbidden_machine = new int[8][8];
	
	// if too_near_tasks[i][j] = -1: task i and task j are too-near
	// tasks; 0: no penalty; otherwise: too-near penalty;
	public int[][] too_near_tasks = new int[8][8];

	private boolean forced_partial_assignment_flg = false;
	private boolean forbidden_machine_flg = false;
	private boolean too_near_tasks_flg = false;

	public ArrayList<Task> best; // store the best solution
	public int minPenalty = Integer.MAX_VALUE; // minimum total penalty
	
	public boolean found = false; // default: false, if something found: true

	// Constructor
	public Algorithm(int[][] machine_penalty, Task[] forced_partial_assignment,
					 int[][] forbidden_machine, int[][] too_near_tasks) {

		System.arraycopy(forced_partial_assignment, 0, this.forced_partial_assignment, 0,
				forced_partial_assignment.length);

		for (int w = 0; w < this.forbidden_machine.length; ++w) {
			System.arraycopy(forbidden_machine[w], 0, this.forbidden_machine[w], 0, forbidden_machine[w].length);
		}

		for (int w = 0; w < this.machine_penalty.length; ++w) {
			System.arraycopy(machine_penalty[w], 0, this.machine_penalty[w], 0, machine_penalty[w].length);
		}

		for (int w = 0; w < this.too_near_tasks.length; ++w) {
			System.arraycopy(too_near_tasks[w], 0, this.too_near_tasks[w], 0, too_near_tasks[w].length);
		}

		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<Task> remaining_tasks = new ArrayList<Task>();
		ArrayList<Task> arr = new ArrayList<Task>();

		// Initialize branches
		for (int i = 0; i < numTasks; ++i) {

			remaining_tasks.clear();
			tasks.clear();

			for (int k = 0; k < numTasks; ++k) {
				tasks.add(Task.getTask(k));
			}

			// If task = A, then remaining_tasks = [B,C,D, ..., H]
			// If task = B, then remaining_tasks = [A,C,D, ..., H]
			Task task = tasks.remove(i);
			for (int j = 0; j < tasks.size(); ++j) {
				remaining_tasks.add(tasks.get(j));
			}

			exploreBranch(arr, remaining_tasks, 1, task);
		}
		
		// Output final result
		if (found) {
			Output.display(best, minPenalty);
		} else {
			Output.display(-1);
		}
	}

	/**
	 * Recursively finds all possible task assignments
	 * 
	 * 
	 * @param arr
	 *            arr[0] represents machine 1, while value at arr[0] represents the
	 *            task assigned to machine 1
	 * @param tasks
	 *            an array of remaining/possible tasks
	 * @param index
	 *            the location of where the next must be assigned or the machine
	 *            number, which must be filled (get a task) next
	 * @param task
	 *            the task that will be assigned to machine represented by index
	 */
	public void exploreBranch(ArrayList<Task> arr, ArrayList<Task> tasks, int index, Task task) {

		if (DEBUG_PARAM) {

			System.out.printf("arr: size = %d, ", arr.size());
			for (int i = 0; i < arr.size(); ++i) {
				System.out.print(arr.get(i) + " ");
			}
			System.out.println();

			System.out.printf("index: %d\n", index);
			System.out.println("task:" + task);

			System.out.printf("tasks: size = %d, ", tasks.size());
			for (int i = 0; i < tasks.size(); ++i) {
				System.out.print(tasks.get(i) + " ");
			}
			System.out.println("\n");
		}

		// Must create a new array copy, because arrays are passed by reference
		ArrayList<Task> new_arr = new ArrayList<Task>(arr);

		// Add task to array
		new_arr.add(task);

		// Base Case
		// If array is full, then return
		if (new_arr.size() >= numTasks) {
			
			if (DEBUG) {
				for (Task aNew_arr : new_arr) {
					System.out.print(aNew_arr + " ");
				}
				System.out.println();
			}

			// If-else block to update `best` and `minPenalty`
			if (checkForcedPartialAssignment (new_arr)) {
				if (checkForbiddenMachine (new_arr)) {
					int currentPenalty = 0;

					currentPenalty += machinePenaltiesCalculator(new_arr);
					
					int tooNearPenalty = tooNearPenaltiesCalculator(new_arr);
					
					// if a too-near task was found in the sequence...
					if (tooNearPenalty == -1) {
						return;
					}
					
					currentPenalty += tooNearPenalty; 

					if (currentPenalty < minPenalty) {

						minPenalty = currentPenalty;
						best = new ArrayList<>(new_arr);
						found = true;
					}
				}
			}

			return;
		}

		// Loop represents a single level of the tree
		int counter = 0;
		for (int i = index; i < numTasks; ++i) {

			// Get the next task
			Task new_task = tasks.get(counter);

			// Create an array of remaining tasks
			ArrayList<Task> remaining_tasks = new ArrayList<Task>(tasks);
			remaining_tasks.remove(counter++);

			exploreBranch(new_arr, remaining_tasks, index + 1, new_task);
		}
	}

	// ------------------------Soft constraints------------------------//
	/**
	 * Calculate the machine penalties of the assignment.
	 *
	 *
	 * @param arr
	 *            arr[0] represents machine 1, while value at arr[0] represents the
	 *            task assigned to machine 1
	 * @return machine penalty
	 * @note forbidden machine and too-near machine are not taken into consideration
	 */
	public int machinePenaltiesCalculator(ArrayList<Task> arr) {
		int machinePenalty = 0;
		for (int i = 0; i < numTasks; i++) {
			machinePenalty += this.machine_penalty[i][arr.get(i).id];
		}
		return machinePenalty;
	}

	/**
	 * Calculate the too-near penalties of the assignment.
	 *
	 *
	 * @param arr
	 *            arr[0] represents machine 1, while value at arr[0] represents the
	 *            task assigned to machine 1
	 * @return -1 if too-near-tasks is invalid otherwise, return tooNearPenalty
	 * @note
	 *
	 */
	public int tooNearPenaltiesCalculator(ArrayList<Task> arr) {
		int tmp = 0;
		int tooNearPenalty = 0;
		for (int i = 0; i < 7; i++) {
			tmp = too_near_tasks[arr.get(i).id][arr.get(i+1).id];
			if (tmp == -1) {
				return -1;
			} else {
				tooNearPenalty += tmp;
			}
		}
		tmp = too_near_tasks[arr.get(7).id][arr.get(0).id];
		if (tmp == -1) {
			return -1;
		} else {
			tooNearPenalty += tmp;
		}

		return tooNearPenalty;
	}

	// ------------------------Hard constraints------------------------//
	/**
	 * Check if the forced partial assignment have been satisfied
	 *
	 * @param arr
	 *            arr[0] represents machine 1, while value at arr[0] represents the
	 *            task assigned to machine 1
	 * @return false: if the forced partial assignment has not been satisfied true:
	 *         if the forced partial assignment has been satisfied
	 * @note
	 *
	 */
	public boolean checkForcedPartialAssignment(ArrayList<Task> arr) {
		for (int i = 0; i < 8; i++) {
			if (this.forced_partial_assignment[i] != arr.get(i) && this.forced_partial_assignment[i] != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the forbidden machine has been satisfied
	 *
	 * @param arr
	 *            arr[0] represents machine 1, while value at arr[0] represents the
	 *            task assigned to machine 1
	 * @return false: if the forbidden machine has not been satisfied true: if the
	 *         forbidden machine has been satisfied
	 * @note
	 *
	 */
	public boolean checkForbiddenMachine(ArrayList<Task> arr) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < this.forbidden_machine[i].length; j++) {
				if (this.forbidden_machine[i][j] == arr.get(i).id) {
					return false;
				}
			}
		}
		return true;
	}
}
