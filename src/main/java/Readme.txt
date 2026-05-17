This source code contains both the symbolic and logical approach to genetic programming. 
The aim is to predict breast cancer recurrence based on a variety of data from the Breast Cancer Wisconsin dataset. 

Attached are two compile jar files, which can be run with the command:
"java -jar SymbolicAlgorithm.jar" or "java -jar LogicalAlgorithm.jar".

Which runs the respective algorithm 30 times and outputs statistical data on the runs.

If the actual trees for each generation are desired, the commands can be modified to be:
"java -jar SymbolicAlgorithm.jar training" or "java -jar LogicalAlgorithm.jar training".

Which will run the respective algorithm once but print the best individual and fitness score for each generation.