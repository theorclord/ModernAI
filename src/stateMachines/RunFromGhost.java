package stateMachines;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * State responsible for running from ghost
 * @author Mikkel Stolborg
 *
 */
public class RunFromGhost extends State {

	private StateMachine mach;
	public RunFromGhost(StateMachine mach) {
		this.mach = mach;
	}

	/**
	 * runs from current ghost
	 */
	@Override
	public MOVE run(Game game, long timeDue) {
		return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex((GHOST) mach.dataStruc.get("ghost")),DM.PATH);
	}
	/**
	 * Changes the state.
	 * Checks if there is active power pills nearby, if true go eat.
	 * Checks if there still is a ghost close, if true stay in state
	 * Defaults to eat pill state
	 * @return returns change state. Returns null if it remains in this state
	 */
	public State changeState(Game game, long timeDue){
		int pacmanPos = game.getPacmanCurrentNodeIndex();
		//Check for power pill
		int closestPowerPill = 0;
		int closestDistPowerPill = -1;
		if(game.getNumberOfActivePowerPills() >0){
			for( int i : game.getActivePowerPillsIndices()){
				int temp = game.getShortestPathDistance(pacmanPos, i);
				if( closestDistPowerPill > temp || closestDistPowerPill == -1){
					closestPowerPill = i;
					closestDistPowerPill = temp;
				}
			}
		}
		if(game.getNumberOfActivePowerPills()>0 && game.getShortestPathDistance(pacmanPos, closestPowerPill) < mach.DistToPowerPill){
			//System.out.println("(RunFromGhost)Try power pill");
			return (State)mach.dataStruc.get("moveNearestPowerPill");
		}
		//Check for ghost distance
		
		for(GHOST ghost : GHOST.values()){
			if(game.getGhostLairTime(ghost) > 0 || game.isGhostEdible(ghost)){
				continue;
			}
			if(game.getShortestPathDistance(pacmanPos,game.getGhostCurrentNodeIndex(ghost)) < mach.DistFromNonEdable){
				mach.dataStruc.put("ghost", ghost);
				return null;
			}
		}		
		//System.out.println("(RunFromGhost) Try nearest pill");
		return (State)mach.dataStruc.get("moveNearestPill");
	}

}
