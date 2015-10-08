package behaviortree;

import java.util.HashMap;

import behaviortree.BehaviorTree.Status;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class RunFromGhost extends Leaf {

	public RunFromGhost(HashMap<String, Object> dataContext) {
		super(dataContext);
	}

	@Override
	public Status run() {
		Game game = (Game)super.data.get("game");
		//System.out.println("Moving From Ghost");
		super.SetVariable("result", game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex((GHOST) super.data.get("ghost")), DM.PATH));
		return Status.Success;
	}

}
