package stateMachines;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class EatGhost extends State {

	private StateMachine mach;
	public EatGhost(StateMachine mach){
		this.mach = mach;
	}
	@Override
	public MOVE run(Game game, long timeDue) {
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), 
				game.getGhostCurrentNodeIndex((GHOST)mach.dataStruc.get("ghost")), DM.PATH);
	}

	@Override
	public State changeState(Game game, long timeDue) {
		int tempGhostDist = -1;
		GHOST tempGhost = null;
		if(game.isGhostEdible((GHOST)mach.dataStruc.get("ghost"))){
			tempGhostDist = game.getGhostCurrentNodeIndex((GHOST)mach.dataStruc.get("ghost"));
			tempGhost = (GHOST)mach.dataStruc.get("ghost");
		}
		
		for(GHOST ghost : GHOST.values()){
			if(!game.isGhostEdible(ghost)){
				continue;
			}
			int tempPathLength = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost)); 
			if(tempPathLength < tempGhostDist || tempGhostDist == -1){
				tempGhostDist = tempPathLength;
				tempGhost = ghost;
			}
		}
		if(tempGhostDist != -1 && tempGhostDist < mach.DistToEdable){
			mach.dataStruc.put("ghost", tempGhost);
			return null;
		} else {
			System.out.println("(EatGhost) Try eat pill");
			return (State) mach.dataStruc.get("moveNearestPill");
		}
	}

}
