package MCTS;

import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;

public class MCTS {
	
	public MCTS(){
		
	}
	
	private void buildTree(Game game){
		MOVE[] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		
		Maze nodes = game.getCurrentMaze();
		nodes.toString();
	}
	

}
