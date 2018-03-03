/**
 * Created by Szymon on 26.02.2018.
 */
public class Population {

    private Individual[] individuals;

    public Population(int populationSize){
        individuals = new Individual[populationSize];
    }

    public Individual getIndividual(int index){
        return individuals[index];
    }

    public void addIndividual(Individual individual, int index){
        individuals[index] = individual;
    }

    public Individual getFittest(int[][] distMatrix, int[][] flowMatrix){
        Individual fittest = individuals[0];
        for(int i=1; i<individuals.length; i++){

            if(fittest.calcCost(distMatrix, flowMatrix) > individuals[i].calcCost(distMatrix, flowMatrix))
                fittest = individuals[i];

        }
        //System.out.println("Fittest: " + fittest.calcCost(distMatrix,flowMatrix));
        return fittest;
    }

    public Individual getWeakest(int[][] distMatrix, int[][] flowMatrix){
        Individual weakest = individuals[0];
        for(int i=1; i<individuals.length; i++){

            if(weakest.calcCost(distMatrix, flowMatrix) > individuals[i].calcCost(distMatrix, flowMatrix))
                weakest = individuals[i];

        }
        return weakest;
    }

    public double getAvg(int[][] distMatrix, int[][] flowMatrix){
        int sum = 0;
        for(int i=0; i<individuals.length; i++){
            sum += individuals[i].calcCost(distMatrix,flowMatrix);
        }
        return sum / individuals.length;
    }




}
