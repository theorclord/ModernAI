package stateMachines;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MoveToNearestPill extends State {

	private StateMachine mach;
	public MoveToNearestPill(StateMachine mach) {
		this.mach = mach;
	}

	@Override
	public MOVE run(Game game, long timeDue) {
		int currentNodeIndex=game.getPacmanCurrentNodeIndex();
		
		//get all active pills
		int[] activePills=game.getActivePillsIndices();
		
		//get all active power pills
		int[] activePowerPills=game.getActivePowerPillsIndices();
		
		//create a target array that includes all ACTIVE pills and power pills
		int[] targetNodeIndices=new int[activePills.length+activePowerPills.length];
		
		for(int i=0;i<activePills.length;i++)
			targetNodeIndices[i]=activePills[i];
		
		for(int i=0;i<activePowerPills.length;i++)
			targetNodeIndices[activePills.length+i]=activePowerPills[i];
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getClosestNodeIndexFromNodeIndex(currentNodeIndex,targetNodeIndices,DM.PATH),DM.PATH);
	}
	
	public State changeState(Game game, long timeDue){
		int pacmanPos = game.getPacmanCurrentNodeIndex();
		//Check for non edable ghost
		for(GHOST ghost : GHOST.values()){
			if(game.getGhostLairTime(ghost) > 0 || game.isGhostEdible(ghost)){
				continue;
			}
			if(game.getShortestPathDistance(pacmanPos,game.getGhostCurrentNodeIndex(ghost))<mach.DistFromNonEdable)
			{
				//System.out.println("(MoveToNearestPill) Try run from Ghost");
				mach.dataStruc.put("ghost", ghost);
				return (State)mach.dataStruc.get("runFromGhost");
			}
		}
		//Check for edable ghost
		for(GHOST ghost : GHOST.values()){
			if(game.isGhostEdible(ghost) && 
					game.getShortestPathDistance(pacmanPos, game.getGhostCurrentNodeIndex(ghost)) < mach.DistToEdable){
				//System.out.println("(MoveToNearestPill) Try eat ghost");
				mach.dataStruc.put("ghost", ghost);
				return (State) mach.dataStruc.get("eatGhost");
			}
		}
		return null;
	}

}
