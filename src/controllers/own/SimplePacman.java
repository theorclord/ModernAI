package controllers.own;


import behaviortree.*;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * The Class NearestPillPacMan.
 */
public class SimplePacman extends Controller<MOVE>
{	
	
	/* (non-Javadoc)
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	public MOVE getMove(Game game,long timeDue)
	{	
		BehaviorTree tree = new BehaviorTree();
		tree.data.put("game", game);
		tree.data.put("timeDue", timeDue);
		Sequence main = new Sequence();
		tree.SetRootNode(main);
		Inverter inv = new Inverter();
		tree.AddNode(main, inv);
		Sequence seqGhost = new Sequence();
		tree.AddNode(inv, seqGhost);
		//add ghost nearby add run from ghost
		tree.AddNode(main, new MoveToNearestPill(tree.data));
		tree.Run();
		return (MOVE) tree.data.get("result");
	}
}