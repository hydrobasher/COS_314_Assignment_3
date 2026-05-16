import java.util.ArrayList;

public class decisionTree {
    dataset data;

    logicalProgram bestProgram;
    float bestFitness;

    private ArrayList<logicalProgram> population;

    public decisionTree(String filePath) {
        this.data = new dataset(filePath);
    }

    private void initialisePopulation() {
        population = new ArrayList<logicalProgram>();

        int startDepth = 3;
        int maxDepth = 5;

        for (int i = startDepth; i <= maxDepth; i++) {
            int numIndividuals = 200 / (maxDepth - startDepth + 1) / 2;

            for (int j = 0; j < numIndividuals; j++) {
                logicalProgram individual = new logicalProgram();
                individual.grow(i);
                population.add(individual);
            }

            for (int j = 0; j < numIndividuals; j++) {
                logicalProgram individual = new logicalProgram();
                individual.full(i);
                population.add(individual);
            }
        }
    }

    private void findBest(){
        for (logicalProgram individual : population) {
            float fitness = individual.fitness(data);

            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestProgram = individual;
            }
        }
    }

    public void run() {
        System.out.println("Running decision tree");
        initialisePopulation();
        System.out.println("Initial population size: " + population.size());

        bestFitness = 0;
        findBest();
        System.out.println("Best fitness: " + bestFitness);

        for (int generation = 1; generation <= 100 && bestFitness < 0.99; generation++) {
            // System.out.println("Generation " + generation);
        }

        // for (int i = 0; i < population.size(); i++) {
        //     logicalProgram individual = population.get(i);
        //     float fitness = individual.fitness(data);
        //     System.out.println("Individual " + i + " fitness: " + fitness);
        // }

        System.out.println("Best fitness: " + bestFitness);
        
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