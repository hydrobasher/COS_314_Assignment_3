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

    public float value;

    public logicalNode YES;
    public logicalNode NO;

    public boolean recurrence;

    public logicalNode(int attribute, float value) {
        this.attribute = attribute;
        this.value = value;
        this.YES = null;
        this.NO = null;

        this.recurrence = false;
    }
}
