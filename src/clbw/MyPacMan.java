package clbw;

import java.util.ArrayList;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MyPacMan extends Controller<MOVE> {
	private static final int MIN_DISTANCE = 20; // if a ghost is this close, run
												// away

	PacmanBT BT;
	PacManMCTS2 mcts;
	boolean setup = true;

	// TIP: change pacman.Executor line 65 for another controller (PacMan or
	// Ghosts)
	@Override
	public MOVE getMove(Game game, long timeDue) {
		/*
		 * A few essential method calls : Maze m = game.getCurrentMaze(); //Gets
		 * the current level represented as a Maze object. Node[] graph =
		 * m.graph; //Gets an array of all the nodes in the current maze. int
		 * pacmanPos = game.getPacmanCurrentNodeIndex(); //Gets the node index
		 * (for use in the graph array above) for PacMan’s current position.
		 * int[] activePills=game.getActivePillsIndices(); //Gets the node
		 * indices for all the normal pills left in the current maze. int[]
		 * activePowerPills=game.getActivePowerPillsIndices(); //Gets the node
		 * indices for all power pills left in the current maze.
		 * game.getNextMoveTowardsTarget(int fromNodeIndex, int toNodeIndex,DM
		 * distanceMeasure) //Returns the direction from the fromNodeIndex to
		 * get to the toNodeIndex fastest using the specified distance measure.
		 * getNextMoveAwayFromTarget(int fromNodeIndex, int toNodeIndex,DM
		 * distanceMeasure) //Returns the direction from the fromNodeIndex to
		 * get away from the toNodeIndex fastest using the specified distance
		 * measure. int getGhostCurrentNodeIndex(GHOST ghostType) //Gets the
		 * node index for the current position for the specified ghost.
		 */

		return monteCarloTreeSearch(game, timeDue);
		// return behaviourTree(game, timeDue);
		// return tutorialCode(game, timeDue);
	}

	/**
	 * BehaviorTree Controller
	 */
	private MOVE behaviourTree(Game game, long timeDue) {
		if (setup) {
			BT = new PacmanBT();
			setup = false;
		}

		return BT.nextMove(game);
	}

	/**
	 * MonteCarloTreeSearch Controller
	 */
	private MOVE monteCarloTreeSearch(Game game, long timeDue) {
		// if (setup) {
		mcts = new PacManMCTS2(timeDue);
		// setup = false;
		// }
		try {
			return mcts.search(game);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MOVE.NEUTRAL;
	}

	/**
	 * Pac-Man controller as part of the starter package Feel free to modify it
	 * or to start from scratch, using the classes supplied with the original
	 * software.
	 * 
	 * This controller utilises 3 tactics, in order of importance: 1. Get away
	 * from any non-edible ghost that is in close proximity 2. Go after the
	 * nearest edible ghost 3. Go to the nearest pill/power pill
	 */
	private MOVE tutorialCode(Game game, long timeDue) {

		int current = game.getPacmanCurrentNodeIndex();

		// Strategy 1: if any non-edible ghost is too close (less than
		// MIN_DISTANCE), run away
		for (GHOST ghost : GHOST.values())
			if (game.getGhostEdibleTime(ghost) == 0
					&& game.getGhostLairTime(ghost) == 0)
				if (game.getShortestPathDistance(current,
						game.getGhostCurrentNodeIndex(ghost)) < MIN_DISTANCE)
					return game.getNextMoveAwayFromTarget(
							game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost), DM.PATH);

		// Strategy 2: find the nearest edible ghost and go after them
		int minDistance = Integer.MAX_VALUE;
		GHOST minGhost = null;

		for (GHOST ghost : GHOST.values())
			if (game.getGhostEdibleTime(ghost) > 0) {
				int distance = game.getShortestPathDistance(current,
						game.getGhostCurrentNodeIndex(ghost));

				if (distance < minDistance) {
					minDistance = distance;
					minGhost = ghost;
				}
			}

		if (minGhost != null) // we found an edible ghost
			return game.getNextMoveTowardsTarget(
					game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(minGhost), DM.PATH);

		// Strategy 3: go after the pills and power pills
		int[] pills = game.getPillIndices();
		int[] powerPills = game.getPowerPillIndices();

		ArrayList<Integer> targets = new ArrayList<Integer>();

		for (int i = 0; i < pills.length; i++)
			// check which pills are available
			if (game.isPillStillAvailable(i))
				targets.add(pills[i]);

		for (int i = 0; i < powerPills.length; i++)
			// check with power pills are available
			if (game.isPowerPillStillAvailable(i))
				targets.add(powerPills[i]);

		int[] targetsArray = new int[targets.size()]; // convert from ArrayList
														// to array

		for (int i = 0; i < targetsArray.length; i++)
			targetsArray[i] = targets.get(i);

		// return the next direction once the closest target has been identified
		return game.getNextMoveTowardsTarget(current, game
				.getClosestNodeIndexFromNodeIndex(current, targetsArray,
						DM.PATH), DM.PATH);
	}
}
