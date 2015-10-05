package controllers.own;


import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import stateMachines.*;

public class PacmanStateMachine extends Controller<MOVE>
{	
	private StateMachine mach;
	public PacmanStateMachine(){
		//parameter 1
		int minDist = 10;
		int distFromNonEidable = 8;
		int distToPowerPill = 7;
		mach = new StateMachine(minDist, distFromNonEidable, distToPowerPill);
		
		mach.SetCurrentState(new MoveToNearestPill(mach));
	}
	/* (non-Javadoc)
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	public MOVE getMove(Game game,long timeDue)
	{	
		return mach.run(game, timeDue);
	}
}