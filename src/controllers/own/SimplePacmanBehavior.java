package controllers.own;


import behaviortree.*;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * The Class NearestPillPacMan.
 */
public class SimplePacmanBehavior extends Controller<MOVE>
{	
	private BehaviorTree tree;
	public SimplePacmanBehavior(){
		tree = new BehaviorTree();
		
		Sequence main = new Sequence();
		tree.SetRootNode(main);
		Inverter inv1 = new Inverter();
		tree.AddNode(main, inv1);
		Sequence seqGhost = new Sequence();
		tree.AddNode(inv1, seqGhost);
		//tree.AddNode(seqGhost, new NearestGhost(tree.data));
		//tree.AddNode(seqGhost, new GhostNear(tree.data));
		tree.AddNode(seqGhost, new GhostNearBy(tree.data));
		Inverter inv2 = new Inverter();
		tree.AddNode(inv2, new IsGhostInHome(tree.data));
		tree.AddNode(seqGhost, inv2);
		Inverter inv3 = new Inverter();
		tree.AddNode(inv3, new IsGhostEdible(tree.data));
		tree.AddNode(seqGhost, inv3);
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