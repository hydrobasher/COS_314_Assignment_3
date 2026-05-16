public class boob {
    // These are all public final. 
    // public for easy access
    // final so they don't get accidently modified

    public final int recurrence;
    public final int age;
    public final int menopause;
    public final int tumor_size;
    public final int inv_nodes;
    public final int node_caps;
    public final int deg_malig;
    public final int breast;
    public final int breast_quad;
    public final int irradiat;

    public boob(int recurrence, int age, int menopause, int tumor_size, int inv_nodes, int node_caps, int deg_malig, int breast, int breast_quad, int irradiat) {
        this.recurrence = recurrence;
        this.age = age;
        this.menopause = menopause;
        this.tumor_size = tumor_size;
        this.inv_nodes = inv_nodes;
        this.node_caps = node_caps;
        this.deg_malig = deg_malig;
        this.breast = breast;
        this.breast_quad = breast_quad;
        this.irradiat = irradiat;
    }

    public boob(String line) {
        String[] parts = line.split(",");
        this.recurrence = Integer.parseInt(parts[0]);
        this.age = Integer.parseInt(parts[1]);
        this.menopause = Integer.parseInt(parts[2]);
        this.tumor_size = Integer.parseInt(parts[3]);
        this.inv_nodes = Integer.parseInt(parts[4]);
        this.node_caps = Integer.parseInt(parts[5]);
        this.deg_malig = Integer.parseInt(parts[6]);
        this.breast = Integer.parseInt(parts[7]);
        this.breast_quad = Integer.parseInt(parts[8]);
        this.irradiat = Integer.parseInt(parts[9]);
    }
}
