import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

public class LogicalAlgorithm {
    final int populationSize = 200;//N
    //Initial tree generation ramped half-and-half
    int initialTreeDepth;//D
    int maxOffspringDepth = 10;
    //Selection method = fitness proportionate
    //Tournament size dd
    //Function set dd
    double crossoverRate;
    double mutationRate;
    //mutation type=point
    int mutationOffspringDepth;
    //Fitness function accuracy
    final int maxGenerations = 100;
    Random random;
    LogicalProgram[] population;

    public static void main(String[]args){
        dataset ds = new dataset("src/main/java/Breast_train.csv");

        Random bigR = new Random(314);
        int runs=30;
        Solution[] bestIndividuals = new Solution[runs];
        long[] runTimes = new long[runs];
        run(bigR, ds, bestIndividuals, runs, runTimes);
        ds = new dataset("src/main/java/Breast_test.csv");

        int[][] fMeasureTracker = new int[runs][];

        double testScore[] = new double[runs];
        for (int i = 0; i < runs; i++) {
            fMeasureTracker[i] = new int[4];    //true positive(11), true negative(00), false positive(10), false negative(01)
            for (int j = 0; j < 4; j++) {
                fMeasureTracker[i][j]=0;
            }

            int correct = 0;

            for (breastData dataPoint : ds.data) {
                boolean output = bestIndividuals[i].root.resolve(dataPoint);
                int predicted = output ? 1 : 0; // threshold = 0
                if (predicted == dataPoint.recurrence) {
                    correct++;
                }

                if(predicted==0 && dataPoint.recurrence==0){
                    fMeasureTracker[i][0]++;
                }
                else if(predicted==1 && dataPoint.recurrence==1){
                    fMeasureTracker[i][1]++;
                }
                else if(predicted==1 && dataPoint.recurrence==0){
                    fMeasureTracker[i][2]++;
                }
                else{
                    fMeasureTracker[i][3]++;
                }
            }

            double accuracy = (double) correct / ds.data.size();
            testScore[i]=accuracy;

            System.out.println("Run "+(i+1)+": "+(1.0-bestIndividuals[i].fitness)+ "\tTest Score: "+accuracy+"\t Runtime: "+ runTimes[i]+ " ms");
            System.out.println("\tTrue Positives: "+fMeasureTracker[i][0]+"\tTrue Negatives: "+fMeasureTracker[i][1]+"\tFalse Positives: "+fMeasureTracker[i][2]+"\tFalse Negatives: "+fMeasureTracker[i][3]);
        }
        double avgTrainingScore = 0.0;
        double avgTestScore = 0.0;
        long avgRunTime = 0;
        for (int i = 0; i < runs; i++) {
            avgTrainingScore+=1.0-bestIndividuals[i].fitness;
            avgTestScore+=testScore[i];
            avgRunTime+=runTimes[i];
        }
        avgTrainingScore/=runs;
        avgTestScore/=runs;
        avgRunTime/=runs;

        System.out.println("\nAverage Training Score: "+avgTrainingScore);
        System.out.println("Average Test Score: "+avgTestScore);
        System.out.println("Average Run Time: "+avgRunTime);
    }

    private static void run(Random bigR, dataset ds, Solution[] bestIndividuals, int runs, long[] runTimes) {
        for (int k = 0; k < runs; k++) {
            long startTime = System.currentTimeMillis();
            LogicalAlgorithm sm = new LogicalAlgorithm(3, 0.95, 0.9, 2, bigR.nextLong());
            sm.generateInitialPopulation();
            PriorityQueue<Solution> fitnessScores = sm.evaluateFitness(ds.data);
            ArrayList<Solution> sortedPopulation = new ArrayList<>();

            for (int i = 0; i < sm.maxGenerations; i++) {
                //System.out.println("Best Individual: " + fitnessScores.peek().root.toString()+ "\nFitness Score: " + (1.0-fitnessScores.peek().fitness));
                bestIndividuals[k]=new Solution(fitnessScores.peek().root.copy(), fitnessScores.peek().fitness);
                LogicalProgram theBest = fitnessScores.peek().root.copy();

                sortedPopulation.clear();
                selectionTransforms(sortedPopulation, fitnessScores);

                ArrayList<LogicalProgram> offspring = sm.crossover(sortedPopulation);
                offspring = sm.mutatePopulation(offspring);

                sm.population[0] = theBest;
                for (int j = 0; j < offspring.size() && j + 1 < sm.populationSize; j++) {
                    sm.population[j + 1] = offspring.get(j);
                }

                fitnessScores = sm.evaluateFitness(ds.data);
            }
            runTimes[k] = System.currentTimeMillis()-startTime;
        }
    }

    private ArrayList<LogicalProgram> mutatePopulation(ArrayList<LogicalProgram> offspring) {
        for (int i = 0; i < offspring.size(); i++) {
            if(random.nextDouble()<mutationRate){
                offspring.get(i).mutate(random);
            }
        }
        return offspring;
    }

    private static void selectionTransforms(ArrayList<Solution> sortedPopulation,
                                            PriorityQueue<Solution> fitnessScores) {
        ArrayList<Solution> temp = new ArrayList<>();
        while (!fitnessScores.isEmpty()) {
            temp.add(fitnessScores.poll());
        }
        Collections.reverse(temp);
        sortedPopulation.addAll(temp);

        double total = 0.0;
        for (Solution s : sortedPopulation) {
            double inv = 1.0 / (1.0 + s.fitness);
            s.fitness = inv;
            total += inv;
        }

        double cumulative = 0.0;
        for (Solution s : sortedPopulation) {
            double normalized = s.fitness / total;
            s.fitness = cumulative + normalized;
            cumulative += normalized;
        }
    }
    static class Solution implements Comparable<Solution> {
        final LogicalProgram root;
        double fitness;

        Solution(LogicalProgram root, double fitness) {
            this.root = root;
            this.fitness = fitness;
        }

        @Override
        public int compareTo(Solution o) {
            return Double.compare(this.fitness, o.fitness);
        }
    }

    LogicalAlgorithm(int initialTreeDepth, double crossoverRate, double mutationRate, int mutationOffspringDepth, long seed) {
        this.initialTreeDepth = initialTreeDepth;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.mutationOffspringDepth = mutationOffspringDepth;
        this.random = new Random(seed);
    }

    void generateInitialPopulation() {
        int depths[] = new int[initialTreeDepth - 1];
        for (int i = 2; i <= initialTreeDepth; i++) {
            depths[i - 2] = i;
        }
        int length = depths.length;
        int perLevel = populationSize / length;
        int remainder = populationSize % length;

        population = new LogicalProgram[populationSize];
        int tail = 0;
        for (int d : depths) {
            int fullCount = perLevel / 2;
            int growCount = perLevel - fullCount;

            for (int i = 0; i < fullCount; i++) {
                LogicalProgram program = new LogicalProgram();
                program.full(d);
                population[tail] = program;
                tail++;
            }

            for (int i = 0; i < growCount; i++) {
                LogicalProgram program = new LogicalProgram();
                program.grow(d);
                population[tail] = program;
                tail++;
            }
        }
        for (int i = 0; i < remainder; i++) {
            LogicalProgram program = new LogicalProgram();
            program.grow(0);
            population[i] = program;
        }
    }

    /*
    calculates the accuracy of each solution for each entry of sample data
    returns an array of the results.
     */
    public PriorityQueue<Solution> evaluateFitness(ArrayList<breastData> data) {
        PriorityQueue<Solution> minHeap = new PriorityQueue<>();

        for (int i = 0; i < populationSize; i++) {
            int correct = 0;

            for (breastData dataPoint : data) {
                boolean output = population[i].resolve(dataPoint);
                int predicted = (output) ? 1 : 0; // threshold = 0
                if (predicted == dataPoint.recurrence) {
                    correct++;
                }
            }

            double accuracy = (double) correct / data.size();
            double errorRate = 1.0 - accuracy;

            minHeap.add(new Solution(population[i], errorRate));
        }

        return minHeap;
    }

    public ArrayList<LogicalProgram> crossover(ArrayList<Solution> selection){
        ArrayList<LogicalProgram> offspring = new ArrayList<>();

        while (offspring.size()<populationSize-1) {
            LogicalProgram parent1 = selectOne(selection);
            LogicalProgram parent2 = selectOne(selection);

            if(random.nextDouble()<crossoverRate){
                LogicalProgram child1 = parent1.copy();
                LogicalProgram child2 = parent2.copy();

                logicalNode parentNode1 = child1.getRandomNode();
                logicalNode parentNode2 = child2.getRandomNode();

                boolean crossoverSide1 = (Math.random() < 0.5);
                boolean crossoverSide2 = (Math.random() < 0.5);

                Node crossover1 = crossoverSide1 ? parentNode1.YES : parentNode1.NO;
                Node crossover2 = crossoverSide2 ? parentNode2.YES : parentNode2.NO;

                if (crossoverSide1) parentNode1.YES = crossover2;
                else parentNode1.NO = crossover2;

                if (crossoverSide2) parentNode2.YES = crossover1;
                else parentNode2.NO = crossover1;

                offspring.add((child1.getDepth()>maxOffspringDepth)?parent1:child1);
                offspring.add((child2.getDepth()>maxOffspringDepth)?parent2:child2);
            }
            else {
                offspring.add(parent1.copy());
                offspring.add(parent2.copy());
            }
        }
        return offspring;
    }

    private LogicalProgram selectOne(ArrayList<Solution> selection) {
        double chooser = random.nextDouble();
        for (int i = 0; i < selection.size(); i++) {
            if (chooser <= selection.get(i).fitness) {
                return selection.get(i).root;
            }
        }
        return selection.getLast().root;
    }
}