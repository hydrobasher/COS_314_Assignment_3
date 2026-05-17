// This class represents an individual in the decision tree genetic programming algorithm.

public class logicalProgram {
    public Node root;

    // Crucially, this is the number of non leaf nodes.
    public int numNodes;

    public logicalProgram() {
        this.root = null;
        this.numNodes = 0;
    }

    public void grow(int depth) {
        // 10% chance to create a leaf node
        if (depth <= 0 || Math.random() < 0.1) {
            this.root = new logicalLeaf();
            return;
        } else {
            this.root = new logicalNode();
            this.numNodes++;
            
            logicalProgram left = new logicalProgram();
            left.grow(depth - 1);
            this.root.YES = left.root;
            logicalProgram right = new logicalProgram();
            right.grow(depth - 1);
            this.root.NO = right.root;

            this.numNodes += left.numNodes + right.numNodes;
        }
    }

    public void full(int depth) {
        if (depth <= 0) {
            this.root = new logicalLeaf();
            return;
        } else {
            this.root = new logicalNode();
            this.numNodes++;

            logicalProgram left = new logicalProgram();
            left.full(depth - 1);
            this.root.YES = left.root;
            logicalProgram right = new logicalProgram();
            right.full(depth - 1);
            this.root.NO = right.root;

            this.numNodes += left.numNodes + right.numNodes;
        }
    }

    public boolean evaluate(breastData data) {
        Node ptr = this.root;

        while (ptr != null && !ptr.isLeaf) {
            int value = data.getAttribute(((logicalNode) ptr).attribute);

            if (value < ((logicalNode) ptr).value)
                ptr = ptr.YES;
            else
                ptr = ptr.NO;
        }

        if (ptr.isLeaf) {
            return ((logicalLeaf) ptr).recurrence;
        }

        this.print();
        System.out.println("Whoopsie");

        return false;
    }

    public float fitness(dataset data) {
        int count = 0;

        for (int i = 0; i < 2 && i < data.data.size(); i++) {
            breastData b = data.data.get(i);
            boolean correct = evaluate(b) == (b.recurrence == 1);

            if (correct)
                count++;
        }
        return (float) count / data.data.size();
    }

    private Node getNodeHelper(Node node, int[] target) {
        // target[0]--;

        if (node == null) {
            System.out.println("This should never run");
            return null;
        }

        if (node.isLeaf) return null;

        if (target[0] == 0) return node;
        target[0]--;

        Node left = getNodeHelper(node.YES, target);

        if (left != null) 
            return left;

        return getNodeHelper(node.NO, target);
    }

    // returns a random non leaf node
    // The actual random node will be 50% chance left, 50% chance right
    public logicalNode getRandomNode() {
        int[] target = {(int) (Math.random() * numNodes)};
    
        Node temp = getNodeHelper(root, target);
        return (logicalNode) temp;
    }

    public void printHelper(Node node, int depth) {
        if (node == null) return;

        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }

        if (node.isLeaf) {
            System.out.println("Leaf: recurrence = " + ((logicalLeaf) node).recurrence);
        } else {
            System.out.println("Node: attribute " + ((logicalNode) node).attribute + " < " + ((logicalNode) node).value);
            printHelper(node.YES, depth + 1);
            printHelper(node.NO, depth + 1);
        }
    }

    public void print() {
        printHelper(this.root, 0);
    }
}
