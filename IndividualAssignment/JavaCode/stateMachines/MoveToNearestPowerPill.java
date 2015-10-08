package stateMachines;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
/**
 * State responsible for moving to nearest power pill
 * @author Mikkel Stolborg
 *
 */
public class MoveToNearestPowerPill extends State {

	private StateMachine mach;
	public MoveToNearestPowerPill(StateMachine mach){
		this.mach = mach;
	}
	/**
	 * Moves towards nearest power pill
	 */
	@Override
	public MOVE run(Game game, long timeDue) {
		int currentNodeIndex=game.getPacmanCurrentNodeIndex();
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getClosestNodeIndexFromNodeIndex(currentNodeIndex,game.getActivePowerPillsIndices(),DM.PATH),DM.PATH);
	}

	/**
	 * Changes the state.
	 * Checks if there is edible ghost nearby, if true try to eat.
	 * Checks if there is remaining power pills, if true stay in state
	 * Checks if there is non edible ghost nearby, if true try to run.
	 * Else return to eat pills
	 * @return returns change state. Returns null if it remains in this state
	 */
	@Override
	public State changeState(Game game, long timeDue) {
		int pacmanPos = game.getPacmanCurrentNodeIndex();
		//Check to eat ghost
		for(GHOST ghost : GHOST.values()){
			if(!game.isGhostEdible(ghost) || game.getGhostLairTime(ghost)>0){
				continue;
			}
			int dist = game.getShortestPathDistance(pacmanPos, game.getGhostCurrentNodeIndex(ghost));
			if(dist < mach.DistToEdible){
				mach.dataStruc.put("ghost", ghost);
				//System.out.println("(MoveToNearestPowerPill) Try eat ghost");
				return (State) mach.dataStruc.get("eatGhost");
			}
		}
		//Check for remaining Power Pills
		if(game.getNumberOfActivePowerPills()>0){
			return null;
		}
		//Check for run from ghost
		for(GHOST ghost : GHOST.values()){
			if(game.getGhostLairTime(ghost) > 0 || game.isGhostEdible(ghost)){
				continue;
			}
			if(game.getShortestPathDistance(pacmanPos,game.getGhostCurrentNodeIndex(ghost))<mach.DistFromNonEdible)
			{
				//System.out.println("(MoveToNearestPowerPill) Try run from Ghost");
				mach.dataStruc.put("ghost", ghost);
				return (State)mach.dataStruc.get("runFromGhost");
			}
		}
		//System.out.println("(MoveToNearestPowerPill) Try nearest pill");
		return (State)mach.dataStruc.get("moveNearestPill");
	}

}
