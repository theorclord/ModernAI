package stateMachines;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Abstract state class.
 * @author Mikkel Stolborg
 *
 */
public abstract class State {
	public abstract MOVE run(Game game, long timeDue);
	public abstract State changeState(Game game, long timeDue);
}
