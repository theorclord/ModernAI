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
	private BehaviorTree tree;
	public SimplePacman(){
		tree = new BehaviorTree();
		
		Sequence main = new Sequence();
		tree.SetRootNode(main);
		Inverter inv = new Inverter();
		tree.AddNode(main, inv);
		Sequence seqGhost = new Sequence();
		tree.AddNode(inv, seqGhost);
		tree.AddNode(seqGhost, new GhostNearBy(tree.data));
		tree.AddNode(seqGhost, new RunFromGhost(tree.data));
		tree.AddNode(main, new MoveToNearestPill(tree.data));
	}
	/* (non-Javadoc)
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	public MOVE getMove(Game game,long timeDue)
	{	
		System.out.println("Move called");
		tree.data.put("game", game);
		tree.data.put("timeDue", timeDue);
		tree.Run();
		return (MOVE) tree.data.get("result");
	}
}