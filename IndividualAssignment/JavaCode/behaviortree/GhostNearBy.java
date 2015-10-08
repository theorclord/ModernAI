package behaviortree;

import java.util.HashMap;

import behaviortree.BehaviorTree.Status;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class GhostNearBy extends Leaf {

	private int MIN_DISTANCE;
	public GhostNearBy(HashMap<String, Object> dataContext) {
		super(dataContext);
		if(super.data.get("ghostDist") != null){
			MIN_DISTANCE = (int) super.data.get("ghostDist");
		}
		else 
		{
			MIN_DISTANCE = 10;
		}
	}

	@Override
	public Status run() {
		Game game = (Game) super.data.get("game");
		for(GHOST ghost : GHOST.values()){
			if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
			{
				super.SetVariable("ghost", ghost);
				//System.out.println("Run From Ghost");
				return Status.Success;
			}
		}
		return Status.Failed;
	}

}
