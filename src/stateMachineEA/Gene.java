package stateMachineEA;

import java.util.Random;

public class Gene {
    // --- variables:

	/**
	 * The random generator for the initial values.
	 */
	private Random ran;
    /**
     * The fitness is the current score the AI achieved in the last simulation.
     * The score is calculated from an average over a number of games.
     */
    protected float mFitness;
    
    /**
     * The chromosome contains the values the ai uses for the switching between the states
     */
    protected int mChromosome[];

    // --- functions:
    /**
     * Sets up the gene for the computation. The initial fitness before the run is 0.
     * The size of the gene is set in the algorithm.
     */
    public Gene() {
        // allocating memory for the chromosome array
        mChromosome = new int[GeneticAlgorithm.CHROMOSOME_SIZE];
        // initializing fitness
        mFitness = 0.f;
        ran = new Random();
    }

    /**
     * Randomizes the numbers on the mChromosome array to values between 1 and 100
     */
    public void randomizeChromosome(){
    	for(int i = 0; i< mChromosome.length; i++){
    		mChromosome[i] = ran.nextInt(50)+1;
    	}
    }

    /**
     * Creates a number of offspring by combining (using crossover) the current
     * Gene's chromosome with another Gene's chromosome.
     * Usually two parents will produce an equal amount of offpsring, although
     * in other reproduction strategies the number of offspring produced depends
     * on the fitness of the parents.
     * @param other: the other parent we want to create offpsring from
     * @return Array of Gene offspring (default length of array is 2).
     * These offspring will need to be added to the next generation.
     */
    public Gene[] reproduce(Gene other){
        Gene[] result = new Gene[1];
        for(int j = 0; j<result.length; j++){
        	Gene temp = new Gene();
			for (int i = 0; i < GeneticAlgorithm.CHROMOSOME_SIZE; i++) {
				int select = ran.nextInt(2);
				if(select ==0){
					temp.mChromosome[i] = this.mChromosome[i];
				} else{
					temp.mChromosome[i] = other.mChromosome[i];
				}
			}
			result[j] = temp;
        }
        return result;
    }

    /**
     * Mutates the gene randomly with either +1 or -1
     */
    public void mutate(){
    	for(int i =0; i<mChromosome.length; i++){
    		int temp = ran.nextInt(3)-1;
    		mChromosome[i]+=temp;
    		if(mChromosome[i] <1){
    			mChromosome[i] = 1;
    		} else if(mChromosome[i] >50){
    			mChromosome[i] = 50;
    		}
    	}
    }
    /**
     * Sets the fitness, after it is evaluated in the GeneticAlgorithm class.
     * @param value: the fitness value to be set
     */
    public void setFitness(int value) { mFitness = value; }
    /**
     * @return the gene's fitness value
     */
    public float getFitness() { return mFitness; }
    /**
     * Returns the element at position <b>index</b> of the mChromosome array
     * @param index: the position on the array of the element we want to access
     * @return the value of the element we want to access (0 or 1)
     */
    public int getChromosomeElement(int index){ return mChromosome[index]; }

    /**
     * Sets a <b>value</b> to the element at position <b>index</b> of the mChromosome array
     * @param index: the position on the array of the element we want to access
     * @param value: the value we want to set at position <b>index</b> of the mChromosome array (0 or 1)
     */
    public void setChromosomeElement(int index, int value){ mChromosome[index]=value; }
    /**
     * Returns the size of the chromosome (as provided in the Gene constructor)
     * @return the size of the mChromosome array
     */
    public int getChromosomeSize() { return mChromosome.length; }
    /**
     * Corresponds the chromosome encoding to the phenotype, which is a representation
     * that can be read, tested and evaluated by the main program.
     * @return a String with a length equal to the chromosome size, composed of A's
     * at the positions where the chromosome is 1 and a's at the posiitons
     * where the chromosme is 0
     */
    public String getPhenotype() {
        // create an empty string
        String result="[";
        for(int i = 0; i < mChromosome.length; i++){
        	result += "" + mChromosome[i] + ",";
        }
        result += "]";
        return result;
    }
    
    
}
