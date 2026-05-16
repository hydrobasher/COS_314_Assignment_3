// Test dataset parser

public class test {
    public static void main(String[] args) {
        dataset data = new dataset("src/main/java/Breast_train.csv");
        System.out.println("Loaded " + data.data.size() + " records.");

        for (boob b : data.data) {
            System.out.println("Recurrence: " + b.recurrence + ", Age: " + b.age + ", Menopause: " + b.menopause + ", Tumor Size: " + b.tumor_size + ", Inv Nodes: " + b.inv_nodes + ", Node Caps: " + b.node_caps);
        }
    }
}
