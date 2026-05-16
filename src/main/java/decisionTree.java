public class decisionTree {
    dataset data;

    public decisionTree(String filePath) {
        this.data = new dataset(filePath);
    }

    public void run() {
        System.out.println("Running decision tree");
        
        // 1: Initialise population with random individuals (programs)
        // 2: Define fitness function to evaluate individuals
        // 3: while termination condition not met do
        // 4: Evaluate fitness of each individual
        // 5: Select individuals for reproduction based on fitness
        // 6: Apply genetic operators (crossover and mutation) to create offspring
        // 7: Replace the old population with new offspring
        // 8: end while
        // 9: Return the best solution found =0
    }

    public static void main(String[] args) {
        decisionTree GP = new decisionTree("src/main/java/Breast_train.csv");
        GP.run();
    }
}