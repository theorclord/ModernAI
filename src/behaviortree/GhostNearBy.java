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
			MIN_DISTANCE = 20;
		}
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Status run() {
		Game game = (Game) super.data.get("game");
		game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		game.getGhostCurrentNodeIndex(GHOST.PINKY);
		game.getGhostCurrentNodeIndex(GHOST.INKY);
		game.getGhostCurrentNodeIndex(GHOST.SUE);
		game.getPacmanCurrentNodeIndex();
		for(GHOST ghost : GHOST.values()){
			//if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
			if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
			{
				super.SetVariable("ghost", ghost);
				return Status.Succes;
			}
			//return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
		}
		return Status.Failed;
	}

}
