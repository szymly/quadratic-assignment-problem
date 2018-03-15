import java.util.Random;

/**
 * Created by Szymon on 11.03.2018.
 */
public class RandomSelectionAlgorithm {

    private Random random;
    private int chromosomeSize;

    private int[][] distMatrix;
    private int[][] flowMatrix;

    public RandomSelectionAlgorithm(int chromosomeSize, int[][] distMatrix, int[][] flowMatrix){
        random = new Random();
        this.chromosomeSize = chromosomeSize;
        this.distMatrix = distMatrix;
        this.flowMatrix = flowMatrix;
    }

    public Individual randomSelect(Population inputPopulation, int iterations) {
        Individual individual = new Individual(chromosomeSize);
        individual.initialize(GeneticAlgorithm.USE_RANDOM_GENES);

        for (int i = 1; i < iterations; i++) {
            int index = random.nextInt(GeneticAlgorithm.POPULATION_SIZE);

            Individual newIndividual = inputPopulation.getIndividual(index);

            if(individual.calcCost(distMatrix,flowMatrix)>newIndividual.calcCost(distMatrix,flowMatrix)){
                individual = newIndividual;
            }
        }
        return individual;
    }
}
