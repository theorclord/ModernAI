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
		int distFromNonEidable = 10;
		int distToPowerPill = 20;
		int distToEdable = 10;
		mach = new StateMachine( distFromNonEidable, distToPowerPill, distToEdable);
		
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