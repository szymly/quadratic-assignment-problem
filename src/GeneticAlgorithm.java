import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Szymon on 26.02.2018.
 */
public class GeneticAlgorithm {

    public static final int ALGORITHM_ITERATIONS = 1;
    public static final String FILE_PATH = "resources/had12.dat";

    public static final int TOURNAMENT = 1;
    public static final int ROULETTE = 2;

    public static final int POPULATION_SIZE = 100;

    public static final int ACCEPTABLE_COST = 1500;
    public static final int GENERATIONS = 100;

    public static final double CROSSOVER_PROB = 0.7;
    public static final double MUTATION_PROB = 0.01;
    public static final int TOUR = 5;

    public static final boolean USE_RANDOM_GENES = true;
    public static final boolean USE_EMPTY_GENES = false;

    public static final int RANDOM_GENE_BOUND = 12;



    private Random random;
    private int[][] distanceMatrix;
    private int[][] flowMatrix;
    private MatrixFileReader matrixFileReader;
    private int geneSize;
    private int generationsCounter;



    public GeneticAlgorithm(){
        random = new Random();
        matrixFileReader = new MatrixFileReader();
        distanceMatrix = new int[0][0];
        flowMatrix = new int[0][0];
        generationsCounter = 1;
    }

    private void init(){
        matrixFileReader.readMatrixFromFile(FILE_PATH);
        distanceMatrix = matrixFileReader.getDistanceMatrix();
        flowMatrix = matrixFileReader.getFlowMatrix();
        geneSize = matrixFileReader.getMatrixSize();
    }



    public static void main(String[] args){

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.init();
        int[][] distMatrix = geneticAlgorithm.getDistanceMatrix();
        int[][] flowMatrix = geneticAlgorithm.getFlowMatrix();

        for(int i=0; i<ALGORITHM_ITERATIONS; i++) {
            System.out.println("Initializing population...");
            Population inputPopulation = geneticAlgorithm.initializePopulation(USE_RANDOM_GENES);
            System.out.println("Population initialized.");

            Population outputPopulation = geneticAlgorithm.evolve(inputPopulation);

            int result = outputPopulation.getFittest(distMatrix, flowMatrix).calcCost(distMatrix, flowMatrix);

            System.out.println("Result: " + result + "\n");
        }
    }


    public Population initializePopulation(boolean useRandomGenes){
        Population outputPopulation;

        outputPopulation = new Population(POPULATION_SIZE);

        for(int i=0; i<POPULATION_SIZE; i++){

            //System.out.println("Initializing individual no " + i);

            Individual individual = new Individual(geneSize);

            //System.out.println("Individual created. Initializing with genes.");

            individual.initialize(useRandomGenes);

            //System.out.println("Gene created. Adding individual no " + i + " to population.");

            outputPopulation.addIndividual(individual, i);
        }

        return outputPopulation;
    }



    public Population evolve(Population inputPopulation) {

        System.out.println("Evolving... Generation: " + (generationsCounter));


        Population outputPopulation;
        outputPopulation = initializePopulation(USE_EMPTY_GENES);
        Individual fittest = inputPopulation.getFittest(distanceMatrix, flowMatrix);
        outputPopulation.addIndividual(fittest, 0);

        System.out.println("Fittest to be transferred to new population: "
                + fittest.calcCost(distanceMatrix,flowMatrix));


        for (int i = 1; i < POPULATION_SIZE; i++) {

            //selection SELECT WHOLE POPULATION
            Individual newIndividual = selection(inputPopulation, TOURNAMENT);

            //crossover
            if (random.nextDouble() % 1 < CROSSOVER_PROB) {
                Individual parent1 = newIndividual;
                Individual parent2 = inputPopulation.getIndividual(random.nextInt(POPULATION_SIZE));
                newIndividual = crossover(parent1, parent2);
            }

            //repair
            //newIndividual = repair(newIndividual);


            //mutation
            newIndividual = mutate(newIndividual);

            outputPopulation.addIndividual(newIndividual, i);
        }

            while (generationsCounter < GENERATIONS) // && fittest.calcCost(distanceMatrix,flowMatrix) > ACCEPTABLE_COST)
            {
                generationsCounter++;
                evolve(outputPopulation);
            }

            return outputPopulation;

    }


    public Individual mutate(Individual inputIndividual){
        Individual individual = inputIndividual;

        for(int i=0; i<geneSize; i++) {
            if (random.nextDouble() % 1 < MUTATION_PROB) {
                int firstIndex = random.nextInt(geneSize);
                int secondIndex = random.nextInt(geneSize);

                int tempGene = individual.getGene(firstIndex);

                individual.setGene(firstIndex, individual.getGene(secondIndex));
                individual.setGene(secondIndex, tempGene);
            }
        }
        return individual;
    }


    public Individual crossover(Individual i1, Individual i2){
        Individual individual;

        int pivot = random.nextInt(geneSize);

        individual = new Individual(geneSize);


        for(int i=0; i<pivot; i++)
        {
            individual.setGene(i, i1.getGene(i));
        }

        for(int i=pivot; i<geneSize; i++)
        {
            int gene = i2.getGene(i);

            if(!individual.getGenes().contains(gene))
            {
                individual.setGene(i, i2.getGene(i));
            }
            else
            {
                int randomGene = random.nextInt(RANDOM_GENE_BOUND);

                while(individual.getGenes().contains(randomGene)){
                    randomGene = random.nextInt(RANDOM_GENE_BOUND);
                }

                individual.setGene(i, randomGene);

            }
        }

        return individual;
    }


    public Individual selection(Population population, int type){
        Individual individual = new Individual(geneSize);

        switch(type){
            case 1:
                Population tournament = new Population(TOUR);
                for(int i=0; i<TOUR; i++){
                    Individual drawn = population.getIndividual(random.nextInt(POPULATION_SIZE));
                    tournament.addIndividual(drawn, i);
                }
                individual = tournament.getFittest(distanceMatrix,flowMatrix);
                break;

            case 2:
                //roulette
                break;
        }


        return individual;
    }

    public Individual repair(Individual individual){

        ArrayList<Integer> checkedGenes = new ArrayList<>();

        Individual outputIndividual = new Individual(geneSize);

        for(int i=0; i<individual.getGenes().size(); i++){
            int newGene = individual.getGene(i);
            if(checkedGenes.contains(individual.getGene(i)))
            {
                newGene = random.nextInt(RANDOM_GENE_BOUND);

                while(checkedGenes.contains(newGene)){
                    newGene = random.nextInt(RANDOM_GENE_BOUND);
                    //System.out.println("Szukam!");
                }
            }
            outputIndividual.getGenes().add(i, newGene);
            checkedGenes.add(outputIndividual.getGenes().get(i));
        }
        return outputIndividual;
    }

    public int[][] getDistanceMatrix(){
        return this.distanceMatrix;
    }

    public int[][] getFlowMatrix(){
        return this.flowMatrix;
    }




}
