package clbw;

import java.util.ArrayList;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * Pac-Man implementation of clbw.BehaviourTree
 * 
 * @author Claus Wind
 * Initial structure (for simple states) of BehaviourTree inspired from pacman.controllers.example.StarterPacMan.java
 */
public class PacmanBT extends BehaviourTree {
	Game pacmanGame;
	int pacmanPos = 0;
	MOVE nextMove = MOVE.UP;
	
	public PacmanBT() {
		Leaf DetectGhosts = new DetectGhosts("DetectGhosts", 25);
		Leaf DetectCloseGhosts = new DetectGhosts("DetectGhosts", 10);
		Leaf FindGhosts = new DetectGhosts("FindGhosts", 50);
		Leaf RunAway = new RunAway("RunAway");

		//Leaf DetectDangerousGhosts = new DetectDangerousGhosts("DetectDangerousGhosts", 10);
		Leaf ChaseGhosts = new ChaseGhosts("ChaseGhosts");
		Leaf AtePowerPill = new AtePowerPill("AtePowerPill");

		Leaf EatPills = new EatPills("EatPills");
		Leaf FindPowerPills = new FindPowerPills("FindPowerPills", 100);
		Leaf GetPowerPill = new GetPowerPill("GetPowerPill");
		
		Decorator GhostsNearby = new Inverter(DetectGhosts);
		Decorator KeepRunning = new RepeatUntilFail(RunAway);

		Composite EatPowerPill = new Sequence("EatPowerPill", new Node[] {FindPowerPills, GetPowerPill});
		Composite PillOrPowerPill = new Selector("GhostsAreNear", new Node[] {EatPowerPill, EatPills});
		Composite GhostsImminent = new Sequence("GhostsAreNear", new Node[] {DetectCloseGhosts, KeepRunning});
		
		//Composite RunFromDanger = new Sequence("RunFromDanger", new Node[] {DetectDangerousGhosts, RunAway});
		//Composite SenseDanger = new Selector("SenseDanger", new Node[] {RunFromDanger, FindGhosts});
		Composite EatGhosts = new Sequence("EatGhosts", new Node[] {FindGhosts, ChaseGhosts});
		Composite GhostsOrPills = new Selector("GhostsOrPills", new Node[] {EatGhosts, EatPills});
		
		Composite ConsumableGhosts = new Sequence("ConsumableGhosts", new Node[] {AtePowerPill, GhostsOrPills});
		Composite DefaultMode = new Sequence("ToEatOrNotToEat", new Node[] {GhostsNearby, EatPills});
		Composite GhostsAreNear = new Selector("GhostsAreNear", new Node[] {PillOrPowerPill, GhostsImminent});
		
		Composite EatOrRun = new Selector("EatOrRun", new Node[] {ConsumableGhosts, DefaultMode, GhostsAreNear});
		
		root = EatOrRun;
	}
	
	private void update(Game game) {
		pacmanGame = game;
		this.pacmanPos = game.getPacmanCurrentNodeIndex();
	}
	public MOVE getMove() {
		return nextMove;
	}
	
	private void setMove(MOVE move) {
		nextMove = move;
	}

	public MOVE nextMove(Game game) {
		update(game);
		root.Run();
		return getMove();
	}

	//Leaf Nodes
	private class DetectGhosts extends Leaf {
		int distance;
		public DetectGhosts(String name, int minDistance) {
			super(name);
			distance = minDistance;
		}
		public STATE Run() {
			for(GHOST ghost : GHOST.values())
				if(pacmanGame.getGhostLairTime(ghost) == 0) 
					if(pacmanGame.getShortestPathDistance(pacmanPos, pacmanGame.getGhostCurrentNodeIndex(ghost)) < distance)
						return STATE.SUCCESS;
			return STATE.FAILURE;
		}
	}
	private class DetectDangerousGhosts extends Leaf { //TODO: Make fix and re-implement
		int distance;
		public DetectDangerousGhosts(String name, int minDistance) {
			super(name);
			distance = minDistance;
		}
		public STATE Run() {
			for(GHOST ghost : GHOST.values())
				if(pacmanGame.getGhostLairTime(ghost) != 0 && pacmanGame.getGhostEdibleTime(ghost) == 0) 
					if(pacmanGame.getShortestPathDistance(pacmanPos, pacmanGame.getGhostCurrentNodeIndex(ghost)) < distance)
						return STATE.SUCCESS;
			return STATE.FAILURE;
		}
	}
	private class ChaseGhosts extends Leaf {
		GHOST nearestGhost = null;
		int ghostDist = Integer.MAX_VALUE;
		public ChaseGhosts(String name) {
			super(name);
		}
		
		public STATE Run() {
			nearestGhost = null;
			ghostDist = Integer.MAX_VALUE;
			for(GHOST ghost : GHOST.values())
				if(pacmanGame.getGhostEdibleTime(ghost) > 0) {
					int dist = pacmanGame.getShortestPathDistance(pacmanPos, pacmanGame.getGhostCurrentNodeIndex(ghost));
					if(dist < ghostDist) {
						ghostDist = dist;
						nearestGhost = ghost;
					}
				}
			if(nearestGhost != null) {
				setMove(pacmanGame.getNextMoveTowardsTarget(pacmanPos,
						pacmanGame.getGhostCurrentNodeIndex(nearestGhost),DM.PATH));
				return STATE.SUCCESS;
			}
			return STATE.FAILURE;
		}
	}
	private class EatPills extends Leaf {
		public EatPills(String name) {
			super(name);
		}
		
		public STATE Run() { //TODO: Get most rather than just nearest
			int[] pills=pacmanGame.getPillIndices();
			int[] powerPills=pacmanGame.getPowerPillIndices();		
			
			ArrayList<Integer> targets=new ArrayList<Integer>();
			
			for(int i=0;i<pills.length;i++)					//check which pills are available			
				if(pacmanGame.isPillStillAvailable(i))
					targets.add(pills[i]);
			
			for(int i=0;i<powerPills.length;i++)			//check with power pills are available
				if(pacmanGame.isPowerPillStillAvailable(i))
					targets.add(powerPills[i]);				
			
			int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array
			
			for(int i=0;i<targetsArray.length;i++)
				targetsArray[i]=targets.get(i);
			
			//return the next direction once the closest target has been identified
			setMove(pacmanGame.getNextMoveTowardsTarget(pacmanPos, 
					pacmanGame.getClosestNodeIndexFromNodeIndex(pacmanPos, targetsArray, DM.PATH), DM.PATH));
			
			return STATE.SUCCESS;
		}
	}
	private class RunAway extends Leaf {
		GHOST nearestGhost = null;
		int ghostDist = Integer.MAX_VALUE;
		public RunAway(String name) {
			super(name);
		}
		
		public STATE Run() { 
			nearestGhost = null;
			ghostDist = Integer.MAX_VALUE;
			for(GHOST ghost : GHOST.values()) 
				if(pacmanGame.getGhostEdibleTime(ghost)==0 && pacmanGame.getGhostLairTime(ghost)==0) {
					int dist = pacmanGame.getShortestPathDistance(pacmanPos, pacmanGame.getGhostCurrentNodeIndex(ghost));
					if(dist < ghostDist) {
						nearestGhost = ghost;
						ghostDist = dist;
					}
				}
			
			setMove(pacmanGame.getNextMoveAwayFromTarget(pacmanPos, 
					pacmanGame.getGhostCurrentNodeIndex(nearestGhost), DM.PATH));
			if(nearestGhost != null)
				return STATE.FAILURE;
			return STATE.SUCCESS;
		}
	}
	private class FindPowerPills extends Leaf {
		int distance;
		public FindPowerPills(String name, int dist) {
			super(name);
			distance = dist;
		}
		
		public STATE Run() {
			int[] activePowerPills = pacmanGame.getActivePowerPillsIndices();
			if(pacmanGame.getClosestNodeIndexFromNodeIndex(pacmanPos, activePowerPills, DM.PATH) < distance && !(activePowerPills.length == 0))
				return STATE.SUCCESS;
			return STATE.FAILURE;
		}
	}
	private class GetPowerPill extends Leaf {
		public GetPowerPill(String name) {
			super(name);
		}
		
		public STATE Run() {
			int[] activePowerPills = pacmanGame.getActivePowerPillsIndices();
			int closest = pacmanGame.getClosestNodeIndexFromNodeIndex(pacmanPos, activePowerPills, DM.PATH);
			setMove(pacmanGame.getNextMoveTowardsTarget(pacmanPos, closest, DM.PATH));
			return STATE.SUCCESS;
		}
	}
	private class AtePowerPill extends Leaf {
		public AtePowerPill(String name) {
			super(name);
		}
		public STATE Run() {
			for(GHOST ghost : GHOST.values())
				if(pacmanGame.getGhostEdibleTime(ghost) > 0) 
					return STATE.SUCCESS; 
			return STATE.FAILURE;
		}
	}
}
