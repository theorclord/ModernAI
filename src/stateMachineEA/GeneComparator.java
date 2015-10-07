package stateMachineEA;

import java.util.Comparator;
/**
 * Comparator for comparing genes
 * @author Mikkel Stolborg
 *
 */
public class GeneComparator implements Comparator<Gene> {

	@Override
	public int compare(Gene arg0, Gene arg1) {
		if( arg0.mFitness <arg1.mFitness){
			return -1;
		} else if(arg0.mFitness > arg1.mFitness){
			return 1;
		} else {
		return 0;
		}
	}

}
