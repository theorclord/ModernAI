package stateMachines;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MoveToNearestPowerPill extends State {

	private StateMachine mach;
	public MoveToNearestPowerPill(StateMachine mach){
		this.mach = mach;
	}
	@Override
	public MOVE run(Game game, long timeDue) {
		System.out.println("Move to Power Pill");
		int currentNodeIndex=game.getPacmanCurrentNodeIndex();
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getClosestNodeIndexFromNodeIndex(currentNodeIndex,game.getActivePowerPillsIndices(),DM.PATH),DM.PATH);
	}

	@Override
	public State changeState(Game game, long timeDue) {
		int pacManPos = game.getPacmanCurrentNodeIndex();
		for(GHOST ghost : GHOST.values()){
			int distTime = game.getGhostEdibleTime(ghost)-game.getShortestPathDistance(pacManPos, game.getGhostCurrentNodeIndex(ghost));
			if(distTime > mach.DistToEdable){
				mach.dataStruc.put("ghost", ghost);
				System.out.println("(MoveToNearestPowerPill) Try eat ghost");
				return (State) mach.dataStruc.get("eatGhost");
			}
		}
		int tempMin = -1;
		for(GHOST ghost : GHOST.values()){
			if(game.getShortestPathDistance(pacManPos,game.getGhostCurrentNodeIndex(ghost)) < tempMin || tempMin == -1){
				tempMin = game.getShortestPathDistance(pacManPos,game.getGhostCurrentNodeIndex(ghost));
			}
		}
		if(tempMin > mach.DistFromNonEdable)
		{
			System.out.println("(MoveToNearestPowerPill) try nearest pill");
			return (State) mach.dataStruc.get("moveNearestPill");
		}
		return null;
	}

}
