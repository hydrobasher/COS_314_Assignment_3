import java.util.ArrayList;

public class decisionTreeOld {
    final static int startDepth = 3;
    final static int maxDepth = 5;
    final static int populationSize = 20;
    final static int tournamentSize = 20;
    final static int maxGenerations = 2;

    boolean debug = false;

    dataset data;

    logicalProgramOld bestProgram;
    float bestFitness;

    private ArrayList<logicalProgramOld> population;

    public decisionTreeOld(String filePath) {
        this.data = new dataset(filePath);
    }

    private void initialisePopulation() {
        population = new ArrayList<logicalProgramOld>();

        for (int i = startDepth; i <= maxDepth; i++) {
            int numIndividuals = populationSize / (maxDepth - startDepth + 1) / 2;

            for (int j = 0; j < numIndividuals; j++) {
                logicalProgramOld individual = new logicalProgramOld();
                individual.full(i);
                population.add(individual);
                if (individual.root.isLeaf) {
                    System.out.println("CHECK INITIALISE POPULATION FULL");
                    System.out.println(i);
                }
            }

            for (int j = 0; j < numIndividuals; j++) {
                logicalProgramOld individual = new logicalProgramOld();
                individual.grow(i);
                population.add(individual);
                if (individual.root.isLeaf) {
                    System.out.println("CHECK INITIALISE POPULATION GROW");
                    System.out.println(i);
                }
            }
        }
    }

    private void findBest(){
        for (logicalProgramOld individual : population) {
            float fitness = individual.fitness(data);

            if (fitness > bestFitness) {
                if (debug) {
                    System.out.println("New best fitness: " + fitness);
                    System.out.println("Best program:");
                    individual.print();
                }
                
                bestFitness = fitness;
                bestProgram = individual;
            }
        }
    }

    public logicalProgramOld select(int t){
        int[] tournament = new int[t];
        for (int i = 0; i < t; i++) {
            tournament[i] = (int) (Math.random() * population.size());
        }

        logicalProgramOld tourneyBestProgram = population.get(tournament[0]);
        float tourneyBestFitness = tourneyBestProgram.fitness(data);
        for (int i = 1; i < t; i++) {
            logicalProgramOld individual = population.get(tournament[i]);
            float fitness = individual.fitness(data);

            if (fitness > tourneyBestFitness) {
                tourneyBestFitness = fitness;
                tourneyBestProgram = individual;
            }
        }
        return tourneyBestProgram;
    }

    public ArrayList<logicalProgramOld> crossover(ArrayList<logicalProgramOld> toBreed) {
        ArrayList<logicalProgramOld> newPopulation = new ArrayList<logicalProgramOld>();

        for (int i = 0; i < toBreed.size() - 1; i += 2) {
            logicalProgramOld p1 = toBreed.get(i).copy();
            logicalProgramOld p2 = toBreed.get(i + 1).copy();

            logicalNode parent1 = p1.getRandomNode();
            logicalNode parent2 = p2.getRandomNode();

            boolean crossoverSide1 = (Math.random() < 0.5);
            boolean crossoverSide2 = (Math.random() < 0.5);

            Node crossover1 = crossoverSide1 ? parent1.YES : parent1.NO;
            Node crossover2 = crossoverSide2 ? parent2.YES : parent2.NO;

            if (crossoverSide1) parent1.YES = crossover2;
            else parent1.NO = crossover2;

            if (crossoverSide2) parent2.YES = crossover1;
            else parent2.NO = crossover1;

            newPopulation.add(p1);
            newPopulation.add(p2);
        }
    
        return newPopulation;
    }

    public void run() {
        System.out.println("Running decision tree");
        initialisePopulation();
        System.out.println("Initial population size: " + population.size());
        findBest();
        debug = true;

        for (int generation = 0; generation < maxGenerations; generation++) {
            findBest();
            System.out.println("Generation " + generation + " best fitness: " + bestFitness);

            ArrayList<logicalProgramOld> toBreed = new ArrayList<logicalProgramOld>();
            for (int i = 0; i < populationSize; i++) {
                toBreed.add(select(tournamentSize));
            }

            population = crossover(toBreed);
            System.out.println(population.get(0).fitness(data));
            population.get(0).print();
        }

        // for (int i = 0; i < population.size(); i++) {
        //     logicalProgram individual = population.get(i);
        //     float fitness = individual.fitness(data);
        //     System.out.println("Individual " + i + " fitness: " + fitness);
        // }

        // System.out.println("Final Best fitness: " + bestFitness);
        
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
        decisionTreeOld GP = new decisionTreeOld("src/main/java/Breast_train.csv");
        GP.run();
    }
}