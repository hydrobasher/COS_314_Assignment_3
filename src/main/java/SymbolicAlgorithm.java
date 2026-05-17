import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

public class SymbolicAlgorithm {
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
    SymbolicNode[] population;

    public static void main(String[]args){
        boolean trainingDemo = false;
        if (args.length > 0 && args[0].equals("training")) {
            trainingDemo = true;
        }

        dataset ds = new dataset("Breast_train.csv");

        Random bigR = new Random(314);
        int runs = (trainingDemo) ? 2 : 10;
        Solution[] bestIndividuals = new Solution[runs];
        long[] runTimes = new long[runs];
        run(bigR, ds, bestIndividuals, runs, runTimes, trainingDemo);
        ds = new dataset("Breast_test.csv");

        double[] testScore = new double[runs];
        int[][] fMeasureTracker = new int[runs][];

        for (int i = 0; i < runs; i++) {
            fMeasureTracker[i] = new int[4];//true positive(11), true negative(00), false positive(10), false negative(01)
            for (int j = 0; j < 4; j++) {
                fMeasureTracker[i][j]=0;
            }

            int correct = 0;

            for (breastData dataPoint : ds.data) {
                double output = bestIndividuals[i].root.resolve(dataPoint);
                int predicted = (output > 0) ? 1 : 0; // threshold = 0
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
        System.out.println("Average Run Time: "+avgRunTime+" ms");
    }

    private static void run(Random bigR, dataset ds, Solution[] bestIndividuals, int runs, long[] runTimes, boolean trainingDemo) {
        for (int k = 0; k < runs; k++) {
            long startTime = System.currentTimeMillis();
            SymbolicAlgorithm sm = new SymbolicAlgorithm(3, 0.95, 0.9, 2, bigR.nextLong());
            sm.generateInitialPopulation();
            PriorityQueue<Solution> fitnessScores = sm.evaluateFitness(ds.data);
            ArrayList<Solution> sortedPopulation = new ArrayList<>();

            for (int i = 0; i < sm.maxGenerations; i++) {
                if (trainingDemo && k != 0) {
                    System.out.println("Best Individual: " + fitnessScores.peek().root.toString()+ "\nFitness Score: " + (1.0-fitnessScores.peek().fitness) + "\n");
                }
                bestIndividuals[k]=new Solution(fitnessScores.peek().root.cloneSubTree(), fitnessScores.peek().fitness);
                SymbolicNode theBest = fitnessScores.peek().root.cloneSubTree();

                sortedPopulation.clear();
                selectionTransforms(sortedPopulation, fitnessScores);

                ArrayList<SymbolicNode> offspring = sm.crossover(sortedPopulation);
                sm.mutatePopulation(offspring);

                sm.population[0] = theBest;
                for (int j = 0; j < offspring.size() && j + 1 < sm.populationSize; j++) {
                    sm.population[j + 1] = offspring.get(j);
                }

                fitnessScores = sm.evaluateFitness(ds.data);
            }
            runTimes[k] = System.currentTimeMillis()-startTime;
        }
    }

    private void mutatePopulation(ArrayList<SymbolicNode> offspring) {
        for (SymbolicNode symbolicNode : offspring) {
            if (random.nextDouble() < mutationRate) {
                int mutationPoint = random.nextInt(symbolicNode.size());
                symbolicNode.get(new int[]{mutationPoint}).mutate(random);
            }
        }
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
        final SymbolicNode root;
        double fitness;

        Solution(SymbolicNode root, double fitness) {
            this.root = root;
            this.fitness = fitness;
        }

        @Override
        public int compareTo(Solution o) {
            return Double.compare(this.fitness, o.fitness);
        }
    }

    SymbolicAlgorithm(int initialTreeDepth, double crossoverRate, double mutationRate, int mutationOffspringDepth, long seed) {
        this.initialTreeDepth = initialTreeDepth;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.mutationOffspringDepth = mutationOffspringDepth;
        this.random = new Random(seed);
    }

    void generateInitialPopulation() {
        int[] depths = new int[initialTreeDepth - 1];
        for (int i = 2; i <= initialTreeDepth; i++) {
            depths[i - 2] = i;
        }
        int length = depths.length;
        int perLevel = populationSize / length;
        int remainder = populationSize % length;

        population = new SymbolicNode[populationSize];
        int tail = 0;
        for (int _ : depths) {
            int fullCount = perLevel / 2;
            int growCount = perLevel - fullCount;

            for (int i = 0; i < fullCount; i++) {
                population[tail] = generateFullTree(0);
                tail++;
            }

            for (int i = 0; i < growCount; i++) {
                population[tail] = generateGrowTree(0);
                tail++;
            }
        }
        for (int i = 0; i < remainder; i++) {
            population[i] = generateGrowTree(0);
        }
    }

    SymbolicNode generateFullTree(int currentDepth) {
        if (currentDepth == maxOffspringDepth) {
            double chooser = random.nextDouble();
            boolean[] value = new boolean[4];
            populateEncodedTerminals(chooser, value);
            return new SymbolicNode(new boolean[]{false, false, false}, value, true, null, null);
        }
        double chooser = random.nextDouble();
        boolean[] function = new boolean[3];
        boolean isUnary = populateEncodedFunctions(chooser, function);
        SymbolicNode left, right = null;

        if (isUnary) {
            left = generateFullTree(currentDepth + 1);
        } else {
            left = generateFullTree(currentDepth + 1);
            right = generateFullTree(currentDepth + 1);
        }
        return new SymbolicNode(function, null, false, left, right);
    }

    SymbolicNode generateGrowTree(int currentDepth){
        if(currentDepth==maxOffspringDepth){
            double chooser = random.nextDouble();
            boolean[] value = new boolean[4];
            populateEncodedTerminals(chooser, value);
            return new SymbolicNode(new boolean[]{false, false, false}, value, true, null, null);
        }
        //first 9 - terminals
        //rest - functions
        double sixteenth = 1.0/16.0;
        double chooser = random.nextDouble();

        if (chooser < sixteenth*9) {// terminals
            chooser*=19.0/9.0;//expand to use with the 9 terminals
            boolean[] value = new boolean[4];
            populateEncodedTerminals(chooser, value);
            return new SymbolicNode(null, value, true, null, null);
        }
        else {//functions
            chooser=(chooser-sixteenth*9)*19/9;//expand to use with the 7 functions
            boolean[] function = new boolean[3];
            boolean isUnary = populateEncodedFunctions(chooser, function);
            if (isUnary) return new SymbolicNode(function, null, false, generateGrowTree(currentDepth+1), null);
            else return new SymbolicNode(function, null, false, generateGrowTree(currentDepth+1), generateGrowTree(currentDepth+1));
        }
    }

    static void populateEncodedTerminals(double chooser, boolean[] value) {
        double ninth=1.0/9.0;
        if (chooser < ninth) {// 0000
            value[0] = false;
            value[1] = false;
            value[2] = false;
            value[3] = false;
        } else if (chooser < ninth * 2) {// 0001
            value[0] = false;
            value[1] = false;
            value[2] = false;
            value[3] = true;
        } else if (chooser < ninth * 3) {// 0010
            value[0] = false;
            value[1] = false;
            value[2] = true;
            value[3] = false;
        } else if (chooser < ninth * 4) {// 0011
            value[0] = false;
            value[1] = false;
            value[2] = true;
            value[3] = true;
        } else if (chooser < ninth * 5) {// 0100
            value[0] = false;
            value[1] = true;
            value[2] = false;
            value[3] = false;
        } else if (chooser < ninth * 6) {// 0101
            value[0] = false;
            value[1] = true;
            value[2] = false;
            value[3] = true;
        } else if (chooser < ninth * 7) {// 0110
            value[0] = false;
            value[1] = true;
            value[2] = true;
            value[3] = false;
        } else if (chooser < ninth * 8) {// 0111
            value[0] = false;
            value[1] = true;
            value[2] = true;
            value[3] = true;
        } else {// 1000
            value[0] = true;
            value[1] = false;
            value[2] = false;
            value[3] = false;
        }
    }

    static boolean populateEncodedFunctions(double chooser, boolean[] function){
        double seventh = 1.0 / 7.0;
        if (chooser < seventh) {
            function[0] = false;
            function[1] = false;
            function[2] = false;
            return false;
        } else if (chooser < seventh * 2) {
            function[0] = false;
            function[1] = false;
            function[2] = true;
            return false;
        } else if (chooser < seventh * 3) {
            function[0] = false;
            function[1] = true;
            function[2] = false;
            return true;
        } else if (chooser < seventh * 4) {
            function[0] = false;
            function[1] = true;
            function[2] = true;
            return false;
        } else if (chooser < seventh * 5) {
            function[0] = true;
            function[1] = false;
            function[2] = false;
            return false;
        } else if (chooser < seventh * 6) {
            function[0] = true;
            function[1] = false;
            function[2] = true;
            return true;
        } else {
            function[0] = true;
            function[1] = true;
            function[2] = false;
            return true;
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
                double output = population[i].resolve(dataPoint);
                int predicted = (output > 0) ? 1 : 0; // threshold = 0
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

    public ArrayList<SymbolicNode> crossover(ArrayList<Solution> selection){
        ArrayList<SymbolicNode> offspring = new ArrayList<>();

        while (offspring.size()<populationSize-1) {
            SymbolicNode parent1 = selectOne(selection);
            SymbolicNode parent2 = selectOne(selection);

            if (random.nextDouble() < crossoverRate && parent1.size() > 1 && parent2.size() > 1) {
                int parent1CrossPoint = 1 + random.nextInt(parent1.size() - 1);
                int parent2CrossPoint = 1 + random.nextInt(parent2.size() - 1);

                SymbolicNode subtree1 = parent1.get(new int[]{parent1CrossPoint});
                SymbolicNode subtree2 = parent2.get(new int[]{parent2CrossPoint});

                if(subtree1==null||subtree2==null) throw new IllegalStateException("Subtree's selected for crossover cannot be null");

                SymbolicNode child1 = parent1.cloneSubTree();
                SymbolicNode child2 = parent2.cloneSubTree();

                child1.set(new int[]{parent1CrossPoint}, subtree2.cloneSubTree());
                child2.set(new int[]{parent2CrossPoint}, subtree1.cloneSubTree());

                offspring.add((child1.getDepth()>maxOffspringDepth)?parent1:child1);
                offspring.add((child2.getDepth()>maxOffspringDepth)?parent2:child2);
            }
            else {
                offspring.add(parent1.cloneSubTree());
                offspring.add(parent2.cloneSubTree());
            }
        }
        return offspring;
    }

    private SymbolicNode selectOne(ArrayList<Solution> selection) {
        double chooser = random.nextDouble();
        for (int i = 0; i < selection.size(); i++) {
            if (chooser <= selection.get(i).fitness) {
                return selection.get(i).root;
            }
        }
        return selection.getLast().root;
    }
}