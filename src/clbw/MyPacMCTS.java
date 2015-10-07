package clbw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
 * Monte Carlo Tree Search algorithm (implementing UCT method) for PacMan controller
 * 
 * @author Claus Wind
 * Inspiration of implementation taken from the UCT.java provided in the MCTS labs
 * Reward values based off rewards table from http://www.ai.rug.nl/~mwiering/GROUP/ARTICLES/MS_PACMAN_RL.pdf
 */
public class MyPacMCTS {
	private Game game;
	private long timeInit;
	private long timer;
	
	private Random random = new Random();
	//rootNode is the starting point of the present state
	Node rootNode;
	Node currentNode;
	//Exploration coefficient
	private float C = (float) (1.0/Math.sqrt(2));
	
	//Ghost controller for simulation of ghost behaviour
	private pacman.controllers.examples.StarterGhosts ghostMan = new pacman.controllers.examples.StarterGhosts();
	
	public MyPacMCTS(Game game) {
		this.game = game;
		rootNode = new Node(null, null, game);
		timeInit = System.currentTimeMillis();
    	timer = 0;
	}
	
	/**
	 * Method for running the UCT algorithm
	 * @return Move from best child of current state
	 * @throws Exception This happen when Mrs Pac-Man dies and the 
	 */
	public MOVE runUCT(Game game) throws Exception {
		this.game = game;
		timeInit = System.currentTimeMillis();
    	timer = 0;
		if(game.wasPacManEaten()) {
			rootNode = new Node(null, null, game);
			return MOVE.NEUTRAL;
		}
		while(timer < 40) {
			Game temp = this.game.copy();
        	TreePolicy(temp);
        	float reward = DefaultPolicy(temp);
        	Backpropagate(reward);
        	//System.out.println(reward);
        	timer = System.currentTimeMillis() - timeInit;
			//System.out.println("Current best move at " + timer + ": " + BestChild(rootNode, C).move);
		}
        currentNode = rootNode;
    	//System.out.println(BestChild(rootNode, C).reward);
		//System.out.println("Best move is: " + BestChild(rootNode, C).move);
    	MOVE nextMove = rootNode.move;
		return nextMove;
	}
	
	/**
	 * Expand the nonterminal nodes with one available child. 
	 * Chose a node to expand with BestChild(C) method
	 */
	private void TreePolicy(Game game) {
		currentNode = rootNode;
		while(!TerminalState(game)) {
			if(!FullyExpanded()) {
				currentNode.Expand(game);
			} else {
				BestChild(C);
			}
		}
	}
	
	/**
	 * Simulation of the game. Choose random actions up until the game is over (goal reached or dead)
	 * @return reward -> see GetReward
	 */
	private float DefaultPolicy(Game game) {
		int movesCounter = 0;
		int reverseCounter = 0;
		while(!TerminalState(game)){
			//Get random move from available moves in untriedMoves
			int randomIndex = random.nextInt(currentNode.untriedMoves.size());
			MOVE move = currentNode.untriedMoves.get(randomIndex);
			MOVE lastMove = game.getPacmanLastMoveMade();
			if(	(move.equals(MOVE.LEFT) && lastMove.equals(MOVE.RIGHT)) ||
				(move.equals(MOVE.RIGHT) && lastMove.equals(MOVE.LEFT)) ||
				(move.equals(MOVE.UP) && lastMove.equals(MOVE.DOWN)) 	||
				(move.equals(MOVE.DOWN) && lastMove.equals(MOVE.UP))	) 
				reverseCounter++;
			movesCounter++;
			
			//Make a copy of the game and simulate a future state
			game.advanceGame(move, ghostMan.getMove(game, timer));
		}
		return GetReward(game, movesCounter, reverseCounter);
	}

	/**
	 * Calculate reward for a given game (score values based off rewards table from http://www.ai.rug.nl/~mwiering/GROUP/ARTICLES/MS_PACMAN_RL.pdf)
	 * @param st Game state for which the reward is to be calculated
	 * @param moves Number of moves taken in given game
	 * @param reverse Number of times Mrs Pac-Man reversed on her path
	 * @return Calculated reward from game
	 */
	private float GetReward(Game st, int moves, int reverse) {
		int score = 0;
		if(st.getNumberOfActivePills() == 0)
			score += 50;
		if(st.wasPacManEaten())
			score -= 350;
		score += (st.getNumGhostsEaten() * 20);
		score += ((st.getNumberOfPills() - st.getNumberOfActivePills()) * 12);
		score += ((st.getNumberOfPowerPills() - st.getNumberOfActivePowerPills()) * 3);
		score -= (moves * 5);
		score -= (reverse * 6);
		//score += game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), st.getPacmanCurrentNodeIndex());
		//if(st.getScore() > game.getScore()) 
		//	score += (st.getScore() - game.getScore());
		return score;
	}
	
	/**
	 * Assign the received reward to every parent of the parent up to the rootNode
	 * Increase the visited count of every node included in backpropagation
	 * @param reward
	 */
	private void Backpropagate(float reward) {
		while(currentNode != null) {
			currentNode.updateVisit();
			currentNode.updateReward(reward);
			currentNode = currentNode.parent;
		}
	}
	
	/**
	 * Check if currentNode is fully expanded
	 * @return Whether or not its list of UntriedMoves is exhausted
	 */
	private boolean FullyExpanded() {
		System.out.println("FullyExpanded says: " + currentNode.children.size());
		return currentNode.getUntriedMoves().size() == 0;
	}
	
	/**
	 * Choose the best child according to the UCT value
	 * Assign it as a currentNode
	 * @param c Exploration coefficient
	 * @return 
	 */
	private void BestChild(float c) {
		Node nt = currentNode;
		Node bestChild = null;
		double tempScore = -1;
		
		for(Node child : nt.children) {
			double score = ((child.reward / child.timesvisited) + 
					(c * Math.sqrt((2 * Math.log(nt.timesvisited))) / 
							(child.timesvisited)));
			System.out.println("Score is: " + score);
			if(score >= tempScore) {
				bestChild = child;
				tempScore = score;
			}
		}
		System.out.println("BestChild says: " + currentNode.children.size());
		System.out.println("BestChild of current says: " + bestChild.parent.children.size());
		currentNode = bestChild;
	}
	
	/**
	 * Calculate UCT value for the best child choosing
	 * @param n child node of currentNode
	 * @param c Exploration coefficient
	 * @return
	 */
	private double UCTvalue(Node n, float c) {
		double score = ((n.reward / n.timesvisited) + (c * Math.sqrt((2 * Math.log(n.parent.timesvisited))) / (n.timesvisited)));
		System.out.println("UCTValue: " + score);
		return score;
	}

	/**
	 * Check if the state is the end of the game
	 * @param state
	 * @return
	 */
	private boolean TerminalState(Game game) {
		return (game.getNumberOfActivePills() == 0) || game.wasPacManEaten();
	}
	
	/**
	 * 
	 * @author Claus Wind
	 * 
	 */
	private class Node {
		private Game game;
		public List<Node> children;
		public Node parent = null;
		public float reward = 0;
		public int timesvisited = 0;
		public MOVE move;
		private List<MOVE> untriedMoves;
		
		Node(Node parent, MOVE move, Game game){
			this.move = move;
			this.parent = parent;
			this.game = game.copy();
			children = new ArrayList<Node>();
			untriedMoves = new ArrayList<MOVE>();
			untriedMoves.addAll(Arrays.asList(game.getPossibleMoves(game.getPacmanCurrentNodeIndex())));
		} 
		
		/**
		 * Takes a random move from untriedMoves, simulates a future state 
		 * of the game and adds the child to children.
		 */
		public Node Expand(Game game) {
			//Get random move from available moves in untriedMoves
			int randomIndex = random.nextInt(this.untriedMoves.size());
			MOVE move = this.untriedMoves.get(randomIndex);
			//Remove the used move from untriedMoves
			this.untriedMoves.remove(randomIndex);
			
			//Make a copy of the game and simulate a future state
			Game copy = game.copy();
			copy.advanceGame(move, ghostMan.getMove(copy, timer));
			
			//Create and add the new child to list of children
			Node child = new Node(this, move, copy);
			children.add(child);
			System.out.println("Expand says: " + child.children.size());
			return child;
		}
		/**
		 * @param reward to be added to Node
		 */
		public void updateReward(float reward) { 
			this.reward += 1; 
		} 
		/**
		 * Add a visit to the Node
		 */
		public void updateVisit() { 
			this.timesvisited += 1; 
		} 
		/**
		 * Get list of untried moves
		 * @return untriedMoves from this Node
		 */
		public List<MOVE> getUntriedMoves() { 
			return untriedMoves; 
		}
	}
}
