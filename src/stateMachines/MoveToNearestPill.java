package stateMachines;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * State responsible for moving to nearest pill
 * @author Mikkel Stolborg
 *
 */
public class MoveToNearestPill extends State {

	private StateMachine mach;
	public MoveToNearestPill(StateMachine mach) {
		this.mach = mach;
	}

	/**
	 * Moves toward nearest pill
	 */
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
		return game.getNextMoveTowardsTarget(currentNodeIndex,game.getClosestNodeIndexFromNodeIndex(currentNodeIndex,targetNodeIndices,DM.PATH),DM.PATH);
	}
	
	/**
	 * Changes the state.
	 * Checks if there is non edible ghost nearby, if true try to run
	 * Checks if there is edible ghost nearby, if true try to eat.
	 * @return returns change state. Returns null if it remains in this state
	 */
	public State changeState(Game game, long timeDue){
		int pacmanPos = game.getPacmanCurrentNodeIndex();
		//Check for non edible ghost
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
		//Check for edible ghost
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
