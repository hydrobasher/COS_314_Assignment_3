// This class represents an individual in the decision tree genetic programming algorithm.

public class logicalProgramOld {
    public NodeOld root;

    public logicalProgramOld() {
        this.root = null;
    }

    private NodeOld copyHelper(NodeOld toCopy) {
        if (toCopy == null) return null;

        if (toCopy.isLeaf) {
            logicalLeaf leaf = new logicalLeaf();
            leaf.recurrence = ((logicalLeaf) toCopy).recurrence;
            return leaf;
        } 

        logicalNode node = new logicalNode(((logicalNode) toCopy).attribute, ((logicalNode) toCopy).value);
        node.YES = copyHelper(toCopy.YES);
        node.NO = copyHelper(toCopy.NO);
        return node;
    }

    public logicalProgramOld copy(){
        logicalProgramOld copy = new logicalProgramOld();
        copy.root = copyHelper(this.root);
        return copy;
    }

    private NodeOld growHelper(int depth) {
        if (depth <= 0 || Math.random() < 0.1) {
            logicalLeaf leaf = new logicalLeaf();
            return leaf;
        } else {
            logicalNode node = new logicalNode();
            node.YES = growHelper(depth - 1);
            node.NO = growHelper(depth - 1);
            return node;
        }
    }

    public void grow(int depth) {
        this.root = new logicalNode();

        this.root.YES = growHelper(depth - 1);
        this.root.NO = growHelper(depth - 1);
    }

    public void full(int depth) {
        if (depth <= 0) {
            this.root = new logicalLeaf();
            return;
        } else {
            this.root = new logicalNode();

            logicalProgramOld left = new logicalProgramOld();
            left.full(depth - 1);
            this.root.YES = left.root;
            logicalProgramOld right = new logicalProgramOld();
            right.full(depth - 1);
            this.root.NO = right.root;
        }
    }

    public boolean evaluate(breastData data) {
        NodeOld ptr = this.root;

        while (!ptr.isLeaf) {
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

    private NodeOld getNodeHelper(NodeOld node, int[] target) {
        if (node == null) {
            System.out.println("This should never run");
            return null;
        }

        if (node.isLeaf) return null;

        if (target[0] == 0) return node;
        target[0]--;

        NodeOld left = getNodeHelper(node.YES, target);

        if (left != null) 
            return left;

        return getNodeHelper(node.NO, target);
    }

    public int numberOfNonLeafNodes(NodeOld node) {
        if (node == null || node.isLeaf) return 0;

        return 1 + numberOfNonLeafNodes(node.YES) + numberOfNonLeafNodes(node.NO);
    }

    // returns a random non leaf node
    // The actual random node will be 50% chance left, 50% chance right
    public logicalNode getRandomNode() {
        int numNodes = numberOfNonLeafNodes(this.root);
        int[] target = {(int) (Math.random() * numNodes)};
        int shi = target[0];
    
        NodeOld temp = getNodeHelper(root, target);

        if (temp == null || temp.isLeaf) {
            System.out.println("CHECK GET RANDOM NODE");
            System.out.println(numNodes);
            System.out.println(shi);
            this.print();
            return null;
        }

        return (logicalNode) temp;
    }

    public void printHelper(NodeOld node, int depth) {
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
