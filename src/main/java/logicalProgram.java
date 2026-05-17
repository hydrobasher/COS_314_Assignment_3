import java.util.Random;

public class LogicalProgram {
    public Node root;

    public LogicalProgram() {
        this.root = null;
    }

    private Node copyHelper(Node toCopy) {
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

    public LogicalProgram copy(){
        LogicalProgram copy = new LogicalProgram();
        copy.root = copyHelper(this.root);
        return copy;
    }

    private Node growHelper(int depth) {
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

            LogicalProgram left = new LogicalProgram();
            left.full(depth - 1);
            this.root.YES = left.root;
            LogicalProgram right = new LogicalProgram();
            right.full(depth - 1);
            this.root.NO = right.root;
        }
    }

    public boolean resolve(breastData data) {
        Node ptr = this.root;

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
            boolean correct = resolve(b) == (b.recurrence == 1);

            if (correct)
                count++;
        }
        return (float) count / data.data.size();
    }

    private Node getNodeHelper(Node node, int[] target) {
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

    public int numberOfNonLeafNodes(Node node) {
        if (node == null || node.isLeaf) return 0;

        return 1 + numberOfNonLeafNodes(node.YES) + numberOfNonLeafNodes(node.NO);
    }

    public int numberOfNodes(Node node) {
        if (node == null) return 0;

        return 1 + numberOfNodes(node.YES) + numberOfNodes(node.NO);
    }

    // returns a random non leaf node
    // The actual random node will be 50% chance left, 50% chance right
    public logicalNode getRandomNode() {
        int numNodes = numberOfNonLeafNodes(this.root);
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

    private int getDepthHelper(Node node) {
        if (node == null) return 0;

        if (node.isLeaf) return 1;

        int leftDepth = getDepthHelper(node.YES);
        int rightDepth = getDepthHelper(node.NO);

        return 1 + Math.max(leftDepth, rightDepth);
    }

    public int getDepth() {
        return getDepthHelper(this.root);
    }

    private Node getNodeV2Helper(Node node, int[] target) {
        if (node == null)
            return null;

        if (target[0] == 0) return node;
        target[0]--;

        Node left = getNodeV2Helper(node.YES, target);

        if (left != null) 
            return left;

        return getNodeV2Helper(node.NO, target);
    }

    private Node getRandomNodeV2() {
        int numNodes = numberOfNodes(this.root);
        int[] target = {(int) (Math.random() * numNodes)};
    
        Node temp = getNodeV2Helper(root, target);

        return (logicalNode) temp;
    }

    public void mutate(Random random) {
        Node node = getRandomNodeV2();

        if (node == null) {
            System.out.println("CHECK MUTATE");
            this.print();
        }

        if (node.isLeaf) {
            ((logicalLeaf) node).recurrence = random.nextBoolean();
        } else {
            logicalNode n = (logicalNode) node;
            n.attribute = random.nextInt(9) + 1;
            n.value = random.nextInt(n.randomHelper(n.attribute));
        }
    }
}