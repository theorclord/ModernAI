package controllers.own;


import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import stateMachines.*;

public class PacmanStateMachine extends Controller<MOVE>
{	
	private StateMachine mach;
	public PacmanStateMachine(){
		//parameters for EA
		int distFromNonEdable = 42;
		int distToPowerPill = 50;
		int distToEdable = 42;
		mach = new StateMachine( distFromNonEdable, distToPowerPill, distToEdable);
		
		mach.SetCurrentState((State) mach.dataStruc.get("moveNearestPill"));
	}
	public PacmanStateMachine(int[] params){
		//parameters for EA
		int distFromNonEdable = params[0];
		int distToPowerPill = params[1];
		int distToEdable = params[2];	
		mach = new StateMachine( distFromNonEdable, distToPowerPill, distToEdable);
		
		mach.SetCurrentState((State) mach.dataStruc.get("moveNearestPill"));
	}
	/* (non-Javadoc)
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	public MOVE getMove(Game game,long timeDue)
	{	
		return mach.run(game, timeDue);
	}
}