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
        SymbolicAlgorithm sm = new SymbolicAlgorithm(5, 0.5, 0.5, 3, 314);
        sm.generateInitialPopulation();

        dataset ds = new dataset("src/main/java/Breast_train.csv");

        PriorityQueue<Solution> fitnessScores = sm.evaluateFitness(ds.data)
        for (int i = 0; i < sm.maxGenerations; i++) {
            //select fitter
            //sex
            //calc new fitness
        }
        System.out.println(sm.population[0]);
    }

    static class Solution implements Comparable<Solution> {
        final SymbolicNode root;  // simplified: use your own tree structure
        final double fitness;

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
        int depths[] = new int[initialTreeDepth - 1];
        for (int i = 2; i <= initialTreeDepth; i++) {
            depths[i - 2] = i;
        }
        int length = depths.length;
        int perLevel = populationSize / length;
        int remainder = populationSize % length;

        population = new SymbolicNode[populationSize];
        int tail = 0;
        for (int d : depths) {
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
        SymbolicNode left = null, right = null;

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
        double seventeenth = 1/16;
        double chooser = random.nextDouble();

        if (chooser < seventeenth*9) {// terminals
            chooser*=19/9;//expand to use with the 9 terminals
            boolean[] value = new boolean[4];
            populateEncodedTerminals(chooser, value);
            return new SymbolicNode(null, value, true, null, null);
        }
        else {//functions
            chooser=(chooser-seventeenth*9)*19/9;//expand to use with the 7 functions
            boolean[] function = new boolean[3];
            boolean isUnary = populateEncodedFunctions(chooser, function);
            if (isUnary) return new SymbolicNode(function, null, false, generateGrowTree(currentDepth+1), null);
            else return new SymbolicNode(function, null, false, generateGrowTree(currentDepth+1), generateGrowTree(currentDepth+1));
        }
    }

    private void populateEncodedTerminals(double chooser, boolean[] value) {
        double ninth=1/9;
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

    private boolean populateEncodedFunctions(double chooser, boolean[] function){
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
    calculates the mean square error of each solution for each entry of sample data
    returns an array of the results.
     */
    public PriorityQueue evaluateFitness(ArrayList<boob> data){
        PriorityQueue<Solution> minHeap= new PriorityQueue<Solution>();
        for (int i = 0; i < populationSize; i++) {
            double meanSquareError = 0.0;
            for(boob dataPoint:data){
                double temp = dataPoint.recurrence-population[i].resolve(dataPoint);
                temp*=temp;
                meanSquareError+=temp;
            }
            meanSquareError/=data.size();
            minHeap.add(new Solution(population[i], meanSquareError));
        }
        return minHeap;
    }

    /*
    FUNCTION crossover_population(selected[], pc, max_depth)
    // selected[] is an array of individuals (size N, usually even)
    // pc = crossover probability per pair
    // max_depth = maximum allowed tree depth
    offspring = new array of size N

    FOR i = 0 TO N-1 STEP 2:
        parent1 = selected[i]
        parent2 = selected[i+1]

        // Apply crossover with probability pc
        IF random() <= pc THEN
            // Choose random crossover points in each parent
            node1 = random_node(parent1)   // any node (internal or leaf)
            node2 = random_node(parent2)

            // Swap subtrees
            subtree1 = copy_subtree(node1)
            subtree2 = copy_subtree(node2)

            child1 = replace_subtree(parent1, node1, subtree2)
            child2 = replace_subtree(parent2, node2, subtree1)

            // Enforce depth limit (if exceeded, revert to parents)
            IF depth(child1) <= max_depth AND depth(child2) <= max_depth THEN
                offspring[i] = child1
                offspring[i+1] = child2
            ELSE
                offspring[i] = copy(parent1)
                offspring[i+1] = copy(parent2)
            END IF
        ELSE
            // No crossover: copy parents unchanged
            offspring[i] = copy(parent1)
            offspring[i+1] = copy(parent2)
        END IF
    END FOR

    RETURN offspring
END FUNCTION
     */
    public ArrayList<SymbolicNode> crossover(ArrayList<SymbolicNode> selection){
        ArrayList<SymbolicNode> offspring = new ArrayList<>();

        for (int i = 0; i < selection.size(); i+=2) {
            SymbolicNode parent1 = selection.get(i);
            SymbolicNode parent2 = selection.get(i+1);

            if(random.nextDouble()<=crossoverRate){
                int parent1CrossPoint = random.nextInt(parent1.size()-1);
                int parent2CrossPoint = random.nextInt(parent2.size()-1);

                SymbolicNode subtree1 = parent1.get(parent1CrossPoint);
                SymbolicNode subtree2 = parent2.get(parent2CrossPoint);

                if(subtree1==null||subtree2==null) throw new IllegalStateException("Subtree's selected for crossover cannot be null");

                SymbolicNode child1 = parent1.cloneSubTree();
                child1.set(parent1CrossPoint, subtree2);
                SymbolicNode child2 = parent2.cloneSubTree();
                child2.set(parent2CrossPoint, subtree1);

                offspring.add((child1.getDepth()>maxOffspringDepth)?parent1:child1);
                offspring.add((child2.getDepth()>maxOffspringDepth)?parent2:child2);
            }
            else {
                offspring.add(parent1);
                offspring.add(parent2);
            }
        }
        return offspring;
    }
}
