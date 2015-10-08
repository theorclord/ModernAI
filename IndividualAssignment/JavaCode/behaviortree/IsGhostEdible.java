package behaviortree;

import java.util.HashMap;

import behaviortree.BehaviorTree.Status;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class IsGhostEdible extends Leaf {

	public IsGhostEdible(HashMap<String, Object> dataContext) {
		super(dataContext);
	}

	@Override
	public Status run() {
		// requires a ghost to be found
		Game game = (Game) super.data.get("game");
		int time = game.getGhostEdibleTime((GHOST) data.get("ghost"));
		if(time > 0){
			//System.out.println("Ghost edible");
			return Status.Success;
		}
		else
		{
			//System.out.println("Ghost not edible");
			return Status.Failed;
		}
	}

}
