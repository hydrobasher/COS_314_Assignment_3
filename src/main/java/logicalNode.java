// This class represents a node in the decision tree used in the genetic programming algorithm.
// Each node is represents : next = (type < value) ? YES : NO

public class logicalNode {
    // 0 = recurrence
    // 1 = age
    // 2 = menopause
    // 3 = tumor_size
    // 4 = inv_nodes
    // 5 = node_caps
    // 6 = deg_malig
    // 7 = breast
    // 8 = breast_quad
    // 9 = irradiat
    public int attribute;

    public int value;

    public logicalNode YES;
    public logicalNode NO;

    public boolean recurrence;

    public logicalNode(int attribute, int value) {
        this.attribute = attribute;
        this.value = value;
        this.YES = null;
        this.NO = null;

        this.recurrence = false;
    }

    private int randomHelper(int attrib) {
        switch (attrib) {
            case 0:
                return 2;
            case 1:
                return 6;
            case 2:
                return 3;
            case 3:
                return 11;
            case 4:
                return 4;
            case 5:
                return 4;
            case 6:
                return 4;
            case 7:
                return 2;
            case 8:
                return 5;
            case 9:
                return 2;
            default:
                return 0;
        }
    }

    public logicalNode() {
        this.attribute = (int) (Math.random() * 9) + 1;
        this.value = (int) (Math.random() * randomHelper(attribute));
        this.YES = null;
        this.NO = null;

        this.recurrence = (Math.random() < 0.5);
    }
}
