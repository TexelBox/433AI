
import java.util.ArrayList;
import java.util.List;

public class AndTree {

    // fields...

    public Input _input;

    public Node _root; // starting point
    public List<Node> _leaves = new ArrayList<Node>();

    public Slot[] _bestAssign; // remember our best _currentAssign that we had (if we had one that was valid and complete)
    public double _bestEval = Double.POSITIVE_INFINITY; // ~~~~~~~~~~~make sure that if we can check when these 2 are not overwritten (meaning NO VALID ASSIGNMENT)

    // methods...

    public AndTree(Input input) {
        _input = input;
        // ~~~~init root before adding!!!, ether here or at declaration
        _leaves.add(_root);
        // also init size of arrays here based on number of courses/slots found in input
    }


}