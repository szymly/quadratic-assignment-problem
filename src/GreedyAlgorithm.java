import java.util.Random;

/**
 * Created by Szymon on 11.03.2018.
 */
public class GreedyAlgorithm {

    private int[][] distMatrix;
    private int[][] flowMatrix;
    private int chromosomeSize;
    private Random random;

    public GreedyAlgorithm(int[][] distMatrix, int[][] flowMatrix, int chromosomeSize){
        this.distMatrix = distMatrix;
        this.flowMatrix = flowMatrix;
        this.chromosomeSize = chromosomeSize;
        random = new Random();
    }

    public Individual getFittest(){
        Individual individual = new Individual(chromosomeSize);
        individual.initialize(GeneticAlgorithm.USE_EMPTY_GENES);

        for(int i=0; i<chromosomeSize-1; i++){
            int gene = i+1;
            for(int j=0; j<chromosomeSize; j++){
                if((distMatrix[i][j]*flowMatrix[i][gene]>distMatrix[i][j]*flowMatrix[i][j]))
                {
                    if(!individual.getGenes().contains(j))
                    {
                        gene = j;
                    }
                }
            }
            individual.setGene(i, gene);
        }

        int lastgene = random.nextInt(chromosomeSize);

        while(individual.getGenes().contains(lastgene)){
            lastgene = random.nextInt(chromosomeSize);
        }

        individual.setGene(chromosomeSize-1, lastgene);

        return individual;
    }
}
