package MCTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MCTS {
	private StarterGhosts ghost = new StarterGhosts();
	private Random ran = new Random();
	private Node root;
	private final float ExplorationVal = 10;
	public MCTS(){
		
	}

	public MOVE getMove(Game game, long timeDue){
		long currentTime = System.currentTimeMillis();
		if(game.wasPacManEaten()){
			return MOVE.NEUTRAL;
		}
		root = new Node(game.copy());
		MOVE result = MOVE.NEUTRAL;
		while(currentTime < timeDue){
			Node currentNode = TreePolicy(root);
			double reward = defaultPolicy(currentNode);
			BackProp(currentNode, reward);
			currentTime = System.currentTimeMillis();
		}
		Node tempNode = BestChild(root,ExplorationVal);
		if(tempNode != null){
			return tempNode.mov;
		}
		return result;
	}
	
	private void BackProp(Node currentNode, double reward) {
		while(currentNode != null){
			currentNode.visit++;
			currentNode.reward += reward;
			currentNode = currentNode.parent;
		}
	}

	private double defaultPolicy(Node currentNode) {
		Game tempState = currentNode.state.copy();
		while(!isTerminal(tempState)){
			MOVE[] moves = tempState.getPossibleMoves(tempState.getPacmanCurrentNodeIndex());
			int select = ran.nextInt(moves.length);
			tempState.advanceGame(moves[select], ghost.getMove());
		}
		return getReward(tempState);
	}

	private double getReward(Game tempState) {
		//prioritize the final highest score
		return tempState.getScore();
	}

	private Node TreePolicy(Node parent){
		Node currentNode = parent;
		while(!isTerminal(currentNode.state)){
			if(currentNode.possibleMoves.size()>0){
				return Expand(currentNode);
			} else {
				currentNode = BestChild(currentNode,ExplorationVal);
			}
		}
		return currentNode;
	}
	
	private Node Expand(Node currentNode) {
		int selected = ran.nextInt(currentNode.possibleMoves.size());
		MOVE mov = currentNode.possibleMoves.get(selected);
		currentNode.possibleMoves.remove(selected);
		Game copy = currentNode.state.copy();
		Node child = new Node(copy);
		child.state.advanceGame(mov, ghost.getMove());
		child.mov = mov;
		child.parent = currentNode;
		currentNode.children.add(child);
		return child;
	}
	
	private Node BestChild(Node currentNode, float exVal) {
		Node bestChild = null;
		double bestScore = Double.MIN_VALUE;
		for(Node child : currentNode.children){
			double score = child.reward/child.visit + exVal * 
			Math.sqrt((2*Math.log(currentNode.visit))/child.visit);
			if(score > bestScore){
				bestScore = score;
				bestChild = child;
			}
		}
		return bestChild;
	}

	

	private boolean isTerminal(Game state){
		return state.wasPacManEaten();
	}
	
	private class Node{
		public Game state;
		public int visit;
		public double reward;
		public Node parent;
		public ArrayList<MOVE> possibleMoves;
		public ArrayList<Node> children;
		public MOVE mov;
		
		public Node(Game state){
			this.state = state;
			children = new ArrayList<Node>();
			possibleMoves = new ArrayList<MOVE>();
			possibleMoves.addAll(Arrays.asList(this.state.getPossibleMoves(state.getPacmanCurrentNodeIndex())));
			
		}
	}

}
