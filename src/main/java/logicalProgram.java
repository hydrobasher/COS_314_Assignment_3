// This class represents an individual in the decision tree genetic programming algorithm.

public class logicalProgram {
    public logicalNode root;

    public logicalProgram() {
        this.root = null;
    }

    public boolean evaluate(boob data) {
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
}
