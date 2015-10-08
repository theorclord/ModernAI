package controllers.own;


import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import stateMachines.*;

/**
 * Pacman state machine. The initial values in the empty constructor are found using a GA
 * @author Mikkel Stolborg
 *
 */
public class PacmanStateMachine extends Controller<MOVE>
{	
	private StateMachine mach;
	public PacmanStateMachine(){
		//parameters from GA
		int distFromNonEdible = 43;
		int distToPowerPill = 43;
		int distToEdible = 42;
		mach = new StateMachine( distFromNonEdible, distToPowerPill, distToEdible);
		
		// set initial state of the machine to get pills
		mach.SetCurrentState((State) mach.dataStruc.get("moveNearestPill"));
	}
	/**
	 * Constructor for AI learning.
	 * @param params, the list of the parameters for changes in the state machine
	 */
	public PacmanStateMachine(int[] params){
		//parameters from GA
		int distFromNonEdible = params[0];
		int distToPowerPill = params[1];
		int distToEdible = params[2];	
		mach = new StateMachine( distFromNonEdible, distToPowerPill, distToEdible);
		
		// set initial state of the machine to get pills
		mach.SetCurrentState((State) mach.dataStruc.get("moveNearestPill"));
	}

	/**
	 * Function for providing the next move to the game executor
	 * @param game, the game. timeDue, time till next step
	 */
	public MOVE getMove(Game game,long timeDue)
	{	
		return mach.run(game, timeDue);
	}
}