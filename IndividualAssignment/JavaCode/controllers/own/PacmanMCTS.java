package controllers.own;

import MCTS.*;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PacmanMCTS extends Controller<MOVE> {

	private MCTS monte;
	public PacmanMCTS(){
		monte = new MCTS();
	}
	@Override
	public MOVE getMove(Game game, long timeDue) {
		return monte.getMove(game, timeDue);
	}

}
