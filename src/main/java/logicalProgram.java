// This class represents an individual in the decision tree genetic programming algorithm.

public class logicalProgram {
    public logicalNode root;

    public logicalProgram() {
        this.root = null;
    }

    public void grow(int depth) {
        this.root = new logicalNode();

        // 10% chance to create a leaf node
        if (depth <= 0 || Math.random() < 0.1) {
            this.root.recurrence = Math.random() < 0.5;
            return;
        }
    
        logicalProgram left = new logicalProgram();
        left.grow(depth - 1);
        this.root.YES = left.root;
        logicalProgram right = new logicalProgram();
        right.grow(depth - 1);
        this.root.NO = right.root;
    }

    public void full(int depth) {
        this.root = new logicalNode();

        if (depth <= 0) {
            this.root.recurrence = Math.random() < 0.5;
            return;
        }
    
        logicalProgram left = new logicalProgram();
        left.full(depth - 1);
        this.root.YES = left.root;
        logicalProgram right = new logicalProgram();
        right.full(depth - 1);
        this.root.NO = right.root;
    }

    public boolean evaluate(breastData data) {
        logicalNode ptr = this.root;
        boolean recurrence = false;

        while (ptr != null) {
            recurrence = ptr.recurrence;
            int value = data.getAttribute(ptr.attribute);

            if (value < ptr.value)
                ptr = ptr.YES;
            else
                ptr = ptr.NO;
        }

        return recurrence;
    }

    public float fitness(dataset data) {
        int count = 0;

        for (int i = 0; i < data.data.size(); i++) {
            breastData b = data.data.get(i);
            boolean correct = evaluate(b) == (b.recurrence == 1);

            if (correct)
                count++;
        }
        return (float) count / data.data.size();
    }

    private void printHelper(logicalNode node, int depth) {
        if (node == null) return;

        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }

        if (node.YES == null && node.NO == null) {
            System.out.println("Leaf: recurrence = " + node.recurrence);
        } else {
            System.out.println("Node: attribute " + node.attribute + " < " + node.value);
            printHelper(node.YES, depth + 1);
            printHelper(node.NO, depth + 1);
        }
    }

    public void print() {
        printHelper(this.root, 0);
    }
}
