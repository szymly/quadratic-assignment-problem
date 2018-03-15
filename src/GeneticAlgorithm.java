import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Szymon on 26.02.2018.
 */
public class GeneticAlgorithm {

    public static final int ALGORITHM_ITERATIONS = 10;
    public static final String FILE_PATH = "resources/had14.dat";

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



    private Random random;
    private int[][] distanceMatrix;
    private int[][] flowMatrix;
    private MatrixFileReader matrixFileReader;
    private int chromosomeSize;
    private int generationsCounter;
    private LinkedList<String> csvRows;


    private Individual fittest;



    public GeneticAlgorithm(){
        random = new Random();
        matrixFileReader = new MatrixFileReader();
        distanceMatrix = new int[0][0];
        flowMatrix = new int[0][0];
        generationsCounter = 1;
        fittest = new Individual(chromosomeSize);
        fittest.initialize(USE_RANDOM_GENES);
        csvRows = new LinkedList<>();
    }

    private void init(){
        matrixFileReader.readMatrixFromFile(FILE_PATH);
        distanceMatrix = matrixFileReader.getDistanceMatrix();
        flowMatrix = matrixFileReader.getFlowMatrix();
        chromosomeSize = matrixFileReader.getMatrixSize();
    }



    public static void main(String[] args){

        long fileMark = System.currentTimeMillis();

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.init();
        int[][] distMatrix = geneticAlgorithm.getDistanceMatrix();
        int[][] flowMatrix = geneticAlgorithm.getFlowMatrix();

        for(int i=0; i<ALGORITHM_ITERATIONS; i++) {

            //geneticAlgorithm.clearRows();
            geneticAlgorithm.clearCounter();

            //System.out.println("Initializing population...");
            Population inputPopulation = geneticAlgorithm.initializePopulation(USE_RANDOM_GENES);
            //System.out.println("Population initialized.");

            geneticAlgorithm.evolve(inputPopulation);





            RandomSelectionAlgorithm randomSelectionAlgorithm = new RandomSelectionAlgorithm(geneticAlgorithm.getChromosomeSize(), distMatrix, flowMatrix);
            Individual randomIndividual = randomSelectionAlgorithm.randomSelect(inputPopulation, 100);
            System.out.println("Random individual: " + randomIndividual.calcCost(distMatrix,flowMatrix));

            GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm(distMatrix,flowMatrix, geneticAlgorithm.getChromosomeSize());
            Individual greedyIndividual = greedyAlgorithm.getFittest();
            System.out.println("Greedy individual: " + greedyIndividual.calcCost(distMatrix,flowMatrix) + "\n");
        }

        new CSVMaker().saveCSV("outputs/GA-QAP "+ geneticAlgorithm.getChromosomeSize() +"."+fileMark+".csv",
                geneticAlgorithm.getRows());
    }


    public Population initializePopulation(boolean useRandomGenes){
        Population outputPopulation;

        outputPopulation = new Population(POPULATION_SIZE);

        for(int i=0; i<POPULATION_SIZE; i++){

            //System.out.println("Initializing individual no " + i);

            Individual individual = new Individual(chromosomeSize);

            //System.out.println("Individual created. Initializing with genes.");

            individual.initialize(useRandomGenes);

            //System.out.println("Gene created. Adding individual no " + i + " to population.");

            outputPopulation.addIndividual(individual, i);
        }

        return outputPopulation;
    }



    public Population evolve(Population inputPopulation) {

        //System.out.println("Evolving... Generation: " + (generationsCounter));

        fittest = inputPopulation.getFittest(distanceMatrix, flowMatrix);

        Population outputPopulation;

        //selection
        outputPopulation = selection(inputPopulation, ROULETTE);


        outputPopulation.addIndividual(fittest, 0);

        csvRows.add(generationsCounter+";"+fittest.calcCost(distanceMatrix,flowMatrix)+";"
        +inputPopulation.getWeakest(distanceMatrix,flowMatrix).calcCost(distanceMatrix,flowMatrix)
                +";"+inputPopulation.getAvg(distanceMatrix,flowMatrix));

        if(generationsCounter == 100) {
            System.out.println("Genetic individual: "
                    + fittest.calcCost(distanceMatrix, flowMatrix));
        }

        for (int i = 1; i < POPULATION_SIZE; i++) {

            Individual newIndividual = outputPopulation.getIndividual(i);

            //crossover
            if (random.nextDouble() % 1 < CROSSOVER_PROB) {
                Individual parent1 = newIndividual;
                Individual parent2 = inputPopulation.getIndividual(random.nextInt(POPULATION_SIZE));
                newIndividual = crossover(parent1, parent2);
            }

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

        for(int i = 0; i< chromosomeSize; i++) {
            if (random.nextDouble() % 1 < MUTATION_PROB) {
                int firstIndex = i;
                int secondIndex = random.nextInt(chromosomeSize);

                int tempGene = individual.getGene(firstIndex);

                individual.setGene(firstIndex, individual.getGene(secondIndex));
                individual.setGene(secondIndex, tempGene);
            }
        }
        return individual;
    }


    public Individual crossover(Individual i1, Individual i2){
        Individual individual;

        int pivot = random.nextInt(chromosomeSize);

        individual = new Individual(chromosomeSize);


        for(int i=0; i<pivot; i++)
        {
            individual.setGene(i, i1.getGene(i));
        }

        for(int i = pivot; i< chromosomeSize; i++)
        {
            int gene = i2.getGene(i);

            if(!individual.getGenes().contains(gene))
            {
                individual.setGene(i, i2.getGene(i));
            }
            else
            {
                individual.setGene(i, -1);
            }
        }

        for(int i=0; i<chromosomeSize; i++)
        {
            if(individual.getGene(i)==-1){
                int randomGene = random.nextInt(chromosomeSize);

                while(individual.getGenes().contains(randomGene)){
                    randomGene = random.nextInt(chromosomeSize);
                }

                individual.setGene(i, randomGene);

            }
        }
        return individual;
    }


    public Population selection(Population population, int type){

        Population newPopulation = new Population(POPULATION_SIZE);

        switch(type){
            case 1:
                for(int i=1; i<POPULATION_SIZE; i++) {
                    Individual individual;
                    Population tournament = new Population(TOUR);
                    for (int j = 0; j < TOUR; j++) {
                        Individual drawn = population.getIndividual(random.nextInt(POPULATION_SIZE));
                        tournament.addIndividual(drawn, j);
                    }
                    individual = tournament.getFittest(distanceMatrix, flowMatrix);
                    newPopulation.addIndividual(individual, i);
                }
                break;

            case 2:
                int totalCost = population.calcTotalCost(distanceMatrix,flowMatrix);
                int worstCost = population.getWeakest(distanceMatrix,flowMatrix).getCost();
                Random random = new Random();

                for(int i=0; i<POPULATION_SIZE; i++) {
                    Individual individual = population.getIndividual(i);
                    int cost = individual.calcCost(distanceMatrix,flowMatrix);
                    population.getIndividual(i).setRouletteProb((worstCost-cost+1)/(totalCost+1));
                }

                for(int i=0; i<POPULATION_SIZE; i++){
                    double r = random.nextDouble() % 1;

                    int j = random.nextInt(POPULATION_SIZE);
                    while(r < population.getIndividual(j).getRouletteProb()){
                        j = random.nextInt(POPULATION_SIZE);
                    }
                    newPopulation.addIndividual(population.getIndividual(j), i);
                }
                break;
        }


        return newPopulation;
    }


    public int[][] getDistanceMatrix(){
        return this.distanceMatrix;
    }

    public int[][] getFlowMatrix(){
        return this.flowMatrix;
    }


    public Individual getFittest() {
        return fittest;
    }

    public LinkedList<String> getRows(){
        return csvRows;
    }

    public void setRows(LinkedList<String> newRows){
        this.csvRows = newRows;
    }

    public void clearRows(){
        this.csvRows = new LinkedList<>();
    }

    public int getChromosomeSize(){
        return chromosomeSize;
    }

    public void clearCounter(){
        this.generationsCounter = 1;
    }

}
