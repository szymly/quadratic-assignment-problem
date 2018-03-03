import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Szymon on 26.02.2018.
 */
public class Individual {

    private int genesNumber;
    private ArrayList<Integer> genes;
    private int cost;

    public Individual(int genesNumber){
        this.genesNumber = genesNumber;
        genes = new ArrayList(genesNumber);
    }


    public int getGene(int geneIndex){
        return genes.get(geneIndex);
    }

    public void setGene(int geneIndex, int gene){
        if(genes.size()<=geneIndex){
            genes.add(geneIndex, gene);
        }
        else {
            genes.set(geneIndex, gene);
        }
    }

    public ArrayList<Integer> getGenes(){
        return this.genes;
    }

    public int calcCost(int[][] distMatrix, int[][] flowMatrix){
        int cost = 0;

        for(int i=0; i<genesNumber; i++){
            for(int j=0; j<genesNumber; j++){
                cost += (distMatrix[i][j]*flowMatrix[genes.get(i)][genes.get(j)]);
            }
        }

        //System.out.println("Cost: " + cost);
        this.cost = cost;
        return cost;
    }

    public void initialize(boolean setRandomGenes){
        Random random = new Random();

        for(int j=0; j<genesNumber; j++){
            int gene = 0;

            if(setRandomGenes) {
                gene = random.nextInt(genesNumber);

                while (genes.contains(gene)) {
                    gene = random.nextInt(genesNumber);
                }
            }

            genes.add(gene);
        }

    }
    public int getGenesNumber(){
        return this.genesNumber;
    }

}
