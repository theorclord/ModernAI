package stateMachines;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * State responsible for eating ghost
 * @author Mikkel Stolborg
 *
 */
public class EatGhost extends State {

	private StateMachine mach;
	public EatGhost(StateMachine mach){
		this.mach = mach;
	}
	/**
	 * Moves toward the saved ghost
	 */
	public MOVE run(Game game, long timeDue) {
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), 
				game.getGhostCurrentNodeIndex((GHOST)mach.dataStruc.get("ghost")), DM.PATH);
	}

	/**
	 * Changes the state.
	 * Checks if the current ghost is closest and edible.
	 * Changes state to eat nearest pill if no ghost matches requirement
	 * @return returns change state. Returns null if it remains in this state
	 */
	public State changeState(Game game, long timeDue) {
		//Check to eat ghost
		int tempGhostDist = -1;
		GHOST tempGhost = null;
		if(game.isGhostEdible((GHOST)mach.dataStruc.get("ghost"))){
			tempGhostDist = game.getGhostCurrentNodeIndex((GHOST)mach.dataStruc.get("ghost"));
			tempGhost = (GHOST)mach.dataStruc.get("ghost");
		}
		//Check if another ghost is closer
		for(GHOST ghost : GHOST.values()){
			if(!game.isGhostEdible(ghost)){
				continue;
			}
			int tempPathLength = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost)); 
			if(tempPathLength < tempGhostDist){
				tempGhostDist = tempPathLength;
				tempGhost = ghost;
			}
		}
		if(tempGhostDist < mach.DistToEdable && tempGhost != null){
			mach.dataStruc.put("ghost", tempGhost);
			return null;
		}
		// Eat nearest pill
		//System.out.println("(EatGhost) Try eat pill");
		return (State) mach.dataStruc.get("moveNearestPill");
	}

}
