package clbw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * 
 * @author Mats & Claus
 *
 */
public class PacManMCTS2 {
	private static final float C = (float) (1 / Math.sqrt(2)); // Higher values lead to more exploration
	private final int NUMBER_OF_LOOPS = 100;
	private long timeDue;
	private StarterGhosts ghosts = new StarterGhosts();

	Node rootNode = null;
	Node currentNode = null;
	Game updatedState = null;

	public PacManMCTS2(long timeDue) {
		this.timeDue = timeDue;
	}

	public MOVE search(Game initialState) {
		rootNode = new Node(null, null, initialState);

		int a = 0;
		while (a < NUMBER_OF_LOOPS) {
			currentNode = rootNode;
			currentNode = TreePolicy(rootNode);
			float value = DefaultPolicy(currentNode.game);
			BackPropagate(currentNode, value);
			a++;
		}
		System.out.println(rootNode.toString());
		System.exit(0);
		return BestChild(rootNode, C).getMove();
	}

	private Node TreePolicy(Node root) {
		// System.out.println("Tree Policy");
		while (!TerminalState(root.game)) {
			if (!FullyExpanded(root)) {
				return Expand(root);
			} else {
				root = BestChild(root, C);
			}
		}
		return root;
	}

	private Node BestChild(Node root, float c2) {
		Random r = new Random();
		double epsilon = 1e-6;
		double maximum = -99999;
		Node bestChild = null;
		for (Node child : root.Children) {
			double score = ((child.reward / (child.visits))
					+ (c2 * Math.sqrt((2 * Math.log(child.parent.visits))) / (child.visits)) + r
					.nextDouble() * epsilon);
			if (score > maximum) {
				maximum = score;
				bestChild = child;
			}
		}
		return bestChild;
	}

	private Node Expand(Node root) {
		Random r = new Random();
		int index = r.nextInt(root.untriedMoves.size());
		System.out.println("Root is currently : " + root.untriedMoves.size());
		Game copy = root.game.copy();
		MOVE move = root.untriedMoves.get(index);
		root.untriedMoves.remove(index);
		copy.advanceGame(move, null);
		Node child = new Node(move, root, copy);
		root.Children.add(child);
		System.out.println("Move: " + move);
		// System.out.println("current position: "
		// + root.game.getPacmanCurrentNodeIndex() + " nearest pill: "
		// + getNearestPill(root.game) + ", wannabe position: "
		// + child.game.getPacmanCurrentNodeIndex() + " nearest pill: "
		// + getNearestPill(child.game));
		// System.exit(0);
		return child;
	}

	private int getNearestPill(Game game) {
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();

		// get all active pills
		int[] activePills = game.getActivePillsIndices();

		// get all active power pills
		int[] activePowerPills = game.getActivePowerPillsIndices();

		// create a target array that includes all ACTIVE pills and power pills
		int[] targetNodeIndices = new int[activePills.length
				+ activePowerPills.length];

		for (int i = 0; i < activePills.length; i++)
			targetNodeIndices[i] = activePills[i];

		for (int i = 0; i < activePowerPills.length; i++)
			targetNodeIndices[activePills.length + i] = activePowerPills[i];
		return game.getClosestNodeIndexFromNodeIndex(currentNodeIndex,
				targetNodeIndices, DM.PATH);
	}

	private boolean FullyExpanded(Node root) {
		return root.untriedMoves.size() == 0;
	}

	private boolean TerminalState(Game game) {
		return (game.getNumberOfActivePills() == 0);
	}

	private float DefaultPolicy(Game game) {
		Random r = new Random();
		Game currentGame = null;
		int movecount = 0;
		// System.out.println("Default Policy");
		while (!TerminalState(game) && movecount < NUMBER_OF_LOOPS) {
			// get action from s, where s = f(s,a)
			MOVE[] possibleMoves = game.getPossibleMoves(game
					.getPacmanCurrentNodeIndex());
			int index = r.nextInt(possibleMoves.length);
			MOVE move = possibleMoves[index];
			currentGame = game.copy();
			movecount++;
			currentGame.advanceGame(move, null);
		}
		return GetReward(game, currentGame, movecount);
	}

	private float GetReward(Game initialGame, Game currentGame, int moves) {
		float score = 0;
		// if (currentGame.wasPacManEaten()) {
		// score -= 500;
		// }
		// score -= moves * 200;
		score += ((currentGame.getNumberOfPills() - currentGame
				.getNumberOfActivePills()) * 12);
		// score += (initialGame.getShortestPathDistance(
		// initialGame.getPacmanCurrentNodeIndex(),
		// currentGame.getPacmanCurrentNodeIndex()) * 50);
		// System.out.println("Reward: " + score);
		return score;
	}

	private void BackPropagate(Node bestChild, float value) {
		// System.out.println("Backpropagating");
		int i = 0;
		int z = bestChild.depth;
		while (bestChild != null) {
			bestChild.visits += 1;
			System.out.println("Visits: " + bestChild.visits);
			System.out.println("Reward before:" + bestChild.reward);
			bestChild.reward += value;
			System.out.println("Reward after: " + bestChild.reward);
			i++;

			bestChild = bestChild.parent; // move up in the hierarchy
		}
		System.out.println("Depth: " + i + " and note depth is: " + z);
	}

	private class Node {
		List<Node> Children = new ArrayList<Node>();
		Node parent;
		float reward;
		int visits;
		List<MOVE> untriedMoves = new ArrayList<MOVE>();
		Game game;
		MOVE move;
		int depth = 0;

		public Node(MOVE move, Node parent, Game game) {
			this.parent = parent;
			this.move = move;
			this.game = game;
			untriedMoves.addAll(Arrays.asList(game.getPossibleMoves(game
					.getPacmanCurrentNodeIndex())));
			if (parent != null)
				depth = parent.depth + 1;
		}

		public MOVE getMove() {
			return move;
		}

		@Override
		public String toString() {
			int noOfChildren = 0;
			String out = "";
			if (parent != null) {
				noOfChildren += Integer.parseInt(parent.toString());
			} else {
				noOfChildren += 1;
			}
			return "" + noOfChildren;
		}
	}
}
