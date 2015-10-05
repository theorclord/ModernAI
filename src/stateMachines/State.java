package stateMachines;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public abstract class State {
	public abstract MOVE run(Game game, long timeDue);
	
}
