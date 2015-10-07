package stateMachineEA;

import java.util.Collections;
import java.util.Random;        // for generating random numbers

import controllers.own.PacmanStateMachine;
import pacman.Executor;
import pacman.controllers.examples.StarterGhosts;

import java.util.ArrayList;     // arrayLists are more versatile than arrays


/**
 * Genetic Algorithm class.
 * The goal of this GA is to maximize the value of the pacman score.
 */

public class GeneticAlgorithm {
    // --- constants
    static int CHROMOSOME_SIZE=3;
    static int POPULATION_SIZE=40;
    static int EVALUTION_TRIALS = 10;
    static int GENERATION_SIZE = 100;

    // --- variables:

    /**
     * The population contains an ArrayList of genes (the choice of arrayList over
     * a simple array is due to extra functionalities of the arrayList, such as sorting)
     */
    ArrayList<Gene> mPopulation;

    // --- functions:

    /**
     * Creates the starting population of Gene classes, whose chromosome contents are random
     * @param size: The size of the population is passed as an argument from the main class
     */
    public GeneticAlgorithm(int size){
        // initialize the arraylist and each gene's initial weights HERE
        mPopulation = new ArrayList<Gene>();
        for(int i = 0; i < size; i++){
            Gene entry = new Gene();
            entry.randomizeChromosome();
            mPopulation.add(entry);
        }
    }
    /**
     * All the members of the population are evaluated based on their performace in the game
     */
    public void evaluateGeneration(){
        for(int i = 0; i < mPopulation.size(); i++){
        	Executor exe = new Executor();
        	// calculates the fitness passed from a number of trials
        	mPopulation.get(i).mFitness = exe.runExperiment(new PacmanStateMachine(mPopulation.get(i).mChromosome), 
        			new StarterGhosts(), EVALUTION_TRIALS);
        }
    }
    /**
     * With each gene's fitness as a guide, chooses which genes should mate and produce offspring.
     * The offspring are added to the population, replacing the previous generation's Genes either
     * partially or completely. The population size, however, should always remain the same.
     * If you want to use mutation, this function is where any mutation chances are rolled and mutation takes place.
     */
    public void produceNextGeneration(){
        // use one of the offspring techniques suggested in class (also applying any mutations) HERE
    	Collections.sort(mPopulation,Collections.reverseOrder(new GeneComparator()));
    	while(mPopulation.size()>POPULATION_SIZE/2){
    		mPopulation.remove(mPopulation.size()-1);
    	}
    	int i =0;
    	while(mPopulation.size()<POPULATION_SIZE){
    		Gene[] offspring = mPopulation.get(i).reproduce(mPopulation.get(i+1));
    		for(int j = 0; j<offspring.length; j++){
    			mPopulation.add(offspring[j]);
    		}
    		i++;
    	}
    	for(int k = POPULATION_SIZE/2; k<POPULATION_SIZE; k++){
    		mPopulation.get(k).mutate();
    	}
    }

    // accessors
    /**
     * @return the size of the population
     */
    public int size(){ return mPopulation.size(); }
    /**
     * Returns the Gene at position <b>index</b> of the mPopulation arrayList
     * @param index: the position in the population of the Gene we want to retrieve
     * @return the Gene at position <b>index</b> of the mPopulation arrayList
     */
    public Gene getGene(int index){ return mPopulation.get(index); }

    // Genetic Algorithm main method for generating AI's
    public static void main( String[] args ){
        // Initializing the population. A population contains few AI's
    	// The small size is based on the few variables included and
    	// the time it takes to run the GA
        GeneticAlgorithm population = new GeneticAlgorithm(POPULATION_SIZE);
        int generationCount = 0;
        // The algorithm runs until the population has undergone at least several mutations
        while(generationCount < GENERATION_SIZE){
            // --- evaluate current generation:
            population.evaluateGeneration();
            // --- print results here:
            // we choose to print the average fitness,
            // as well as the maximum and minimum fitness
            // as part of our progress monitoring
            float avgFitness=0.f;
            float minFitness=Float.POSITIVE_INFINITY;
            float maxFitness=Float.NEGATIVE_INFINITY;
            String bestIndividual="";
            String worstIndividual="";
            for(int i = 0; i < population.size(); i++){
                float currFitness = population.getGene(i).getFitness();
                avgFitness += currFitness;
                if(currFitness < minFitness){
                    minFitness = currFitness;
                    worstIndividual = population.getGene(i).getPhenotype();
                }
                if(currFitness > maxFitness){
                    maxFitness = currFitness;
                    bestIndividual = population.getGene(i).getPhenotype();
                }
            }
            if(population.size()>0){ avgFitness = avgFitness/population.size(); }
            String output = "Generation: " + generationCount;
            output += "\t AvgFitness: " + avgFitness;
            output += "\t MinFitness: " + minFitness + " (" + worstIndividual +")";
            output += "\t MaxFitness: " + maxFitness + " (" + bestIndividual +")";
            System.out.println(output);
            // produce next generation:
            population.produceNextGeneration();
            generationCount++;
        }
    }
};

