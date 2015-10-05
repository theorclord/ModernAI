package behaviortree;

import java.util.HashMap;

import behaviortree.BehaviorTree.Status;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class GhostNear extends Leaf {

	private int MIN_DISTANCE;
	public GhostNear(HashMap<String, Object> dataContext) {
		super(dataContext);
		if(super.data.get("ghostDist") != null){
			MIN_DISTANCE = (int) super.data.get("ghostDist");
		}
		else 
		{
			MIN_DISTANCE = 10;
		}
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Status run() {
		Game game = (Game) super.data.get("game");
		GHOST ghost = (GHOST) super.data.get("ghost");
		if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
		{
			System.out.println("Ghost near");
			return Status.Success;
		}
		return Status.Failed;
	}

}
