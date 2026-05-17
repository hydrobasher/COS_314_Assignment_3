import java.util.ArrayList;

public class decisionTree {
    final static int startDepth = 3;
    final static int maxDepth = 5;
    final static int populationSize = 30;
    final static int tournamentSize = 20;

    dataset data;

    logicalProgram bestProgram;
    float bestFitness;

    private ArrayList<logicalProgram> population;

    public decisionTree(String filePath) {
        this.data = new dataset(filePath);
    }

    private void initialisePopulation() {
        population = new ArrayList<logicalProgram>();

        for (int i = startDepth; i <= maxDepth; i++) {
            int numIndividuals = populationSize / (maxDepth - startDepth + 1) / 2;

            for (int j = 0; j < numIndividuals; j++) {
                logicalProgram individual = new logicalProgram();
                individual.full(i);
                population.add(individual);
            }

            for (int j = 0; j < numIndividuals; j++) {
                logicalProgram individual = new logicalProgram();
                individual.grow(i);
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

    public logicalProgram select(int t){
        int[] tournament = new int[t];
        for (int i = 0; i < t; i++) {
            tournament[i] = (int) (Math.random() * population.size());
        }

        logicalProgram tourneyBestProgram = population.get(tournament[0]);
        float tourneyBestFitness = tourneyBestProgram.fitness(data);
        for (int i = 1; i < t; i++) {
            logicalProgram individual = population.get(tournament[i]);
            float fitness = individual.fitness(data);

            if (fitness > tourneyBestFitness) {
                tourneyBestFitness = fitness;
                tourneyBestProgram = individual;
            }
        }
        return tourneyBestProgram;
    }

    public ArrayList<logicalProgram> crossover(ArrayList<logicalProgram> toBreed) {
        ArrayList<logicalProgram> newPopulation = new ArrayList<logicalProgram>();

        for (int i = 0; i < toBreed.size() - 1; i += 2) {
            logicalProgram p1 = toBreed.get(i);
            logicalProgram p2 = toBreed.get(i + 1);

            logicalNode parent1 = p1.getRandomNode();
            logicalNode parent2 = p2.getRandomNode();

            boolean crossoverSide1 = (Math.random() < 0.5);
            boolean crossoverSide2 = (Math.random() < 0.5);

            try {
            
                Node crossover1 = crossoverSide1? parent1.YES : parent1.NO;
                Node crossover2 = crossoverSide2? parent2.YES : parent2.NO;

                if (crossover1 == null || crossover2 == null) {
                    System.out.println("This should never run");
                    continue;
                } 
            } catch (Exception e) {
                System.out.println(e);

                

                continue;
            }

            // if (crossoverSide1) parent1.YES = crossover2;
            // else parent1.NO = crossover2;

            // if (crossoverSide2) parent2.YES = crossover1;
            // else parent2.NO = crossover1;

            // newPopulation.add(p1);
            // newPopulation.add(p2);
        }
    
        return newPopulation;
    }

    public void run() {
        System.out.println("Running decision tree");
        initialisePopulation();
        System.out.println("Initial population size: " + population.size());

        bestFitness = 0;
        findBest();
        System.out.println("Best fitness: " + bestFitness);

        for (int generation = 1; generation <= 2 && bestFitness < 0.99; generation++) {
            System.out.println("Generation " + generation);

            ArrayList<logicalProgram> toBreed = new ArrayList<logicalProgram>();
            for (int i = 0; i < population.size(); i++) {
                logicalProgram selected = select(tournamentSize);

                toBreed.add(selected);
            }

            ArrayList<logicalProgram> newPopulation = crossover(toBreed);

            population = newPopulation;
            findBest();
            System.out.println("Best fitness: " + bestFitness);
        }

        // for (int i = 0; i < population.size(); i++) {
        //     logicalProgram individual = population.get(i);
        //     float fitness = individual.fitness(data);
        //     System.out.println("Individual " + i + " fitness: " + fitness);
        // }

        System.out.println("Final Best fitness: " + bestFitness);
        
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