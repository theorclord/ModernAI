package stateMachines;
import java.util.HashMap;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * State machine which runs the AI.
 * @author Mikkel Stolborg
 *
 */
public class StateMachine {

	// public variables used in the states
	public int DistFromNonEdible;
	public int DistToPowerPill;
	public int DistToEdible;
	// Data structure for sharing data
	public HashMap<String,Object> dataStruc;
	
	private State currentState;
	/**
	 * 
	 * @param DistFromNonEdable, distance from non edible ghost
	 * @param DistToPowerPill, distance to power pill
	 * @param DistToEdable, distance to edible ghost
	 */
	public StateMachine( int DistFromNonEdible, int DistToPowerPill,
			int DistToEdible) {
		dataStruc = new HashMap<String,Object>();
		this.DistFromNonEdible = DistFromNonEdible;
		this.DistToPowerPill = DistToPowerPill;
		this.DistToEdible = DistToEdible;
		//puts the states in the data structure so only one of each exits
		dataStruc.put("moveNearestPill", new MoveToNearestPill(this));
		dataStruc.put("moveNearestPowerPill", new MoveToNearestPowerPill(this));
		dataStruc.put("runFromGhost", new RunFromGhost(this));
		dataStruc.put("eatGhost", new EatGhost(this));
	}
	/**
	 * Function called from controller
	 * @param game, pacman game
	 * @param timeDue, till response
	 * @return MOVE, direction for the pacman
	 */
	public MOVE run(Game game, long timeDue){
		// checks if state changes
		State tempState = currentState.changeState(game, timeDue);
		if(tempState != null){
			//checks if new state is optimal
			currentState = tempState;
			run(game, timeDue);
		}
		return currentState.run(game, timeDue);
	}
	
	/**
	 * Used to set the state of the machine.
	 * @param state, new state
	 */
	public void SetCurrentState(State state){
		currentState = state;
	}

}
