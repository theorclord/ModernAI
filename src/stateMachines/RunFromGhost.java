package stateMachines;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunFromGhost extends State {

	private StateMachine mach;
	public RunFromGhost(StateMachine mach) {
		this.mach = mach;
	}

	@Override
	public MOVE run(Game game, long timeDue) {
		System.out.println("Run from ghost");
		return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex((GHOST) mach.dataStruc.get("ghost")),DM.PATH);
	}
	
	public State changeState(Game game, long timeDue){
		int pacManPos = game.getPacmanCurrentNodeIndex();
		int closestPowerPill = 0;
		int closestDistPowerPill = -1;
		if(game.getNumberOfActivePowerPills() >0){
			for( int i : game.getActivePowerPillsIndices()){
				int temp = game.getShortestPathDistance(pacManPos, i);
				if( closestDistPowerPill > temp || closestDistPowerPill == -1){
					closestPowerPill = i;
					closestDistPowerPill = temp;
				}
			}
		}
		if(game.getShortestPathDistance(pacManPos, closestPowerPill) < mach.DistToPowerPill){
			System.out.println("(RunFromGhost)Try power pill");
			return (State)mach.dataStruc.get("moveNearestPowerPill");
		}
		int tempMin = -1;
		for(GHOST ghost : GHOST.values()){
			if(game.getGhostLairTime(ghost) > 0 || game.getGhostEdibleTime(ghost)>0){
				continue;
			}
			if(game.getShortestPathDistance(pacManPos,game.getGhostCurrentNodeIndex(ghost)) < tempMin || tempMin == -1){
				tempMin = game.getShortestPathDistance(pacManPos,game.getGhostCurrentNodeIndex(ghost));
			}
		}
		if(tempMin > mach.DistFromNonEdable || tempMin == -1)
		{
			System.out.println("(RunFromGhost) Try nearest pill");
			return (State)mach.dataStruc.get("moveNearestPill");
		}
		return null;
	}

}
