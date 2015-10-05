package behaviortree;

import java.util.HashMap;

import behaviortree.BehaviorTree.Status;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class NearestGhost extends Leaf {
	public NearestGhost(HashMap<String, Object> dataContext) {
		super(dataContext);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Status run() {
		Game game = (Game) super.data.get("game");
		int tempMin = -1;
		for(GHOST ghost : GHOST.values()){
			if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost)) < tempMin || tempMin == -1){
				tempMin = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost));
				super.SetVariable("ghost", ghost);
			}
			//return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
		}
		System.out.println("NearestGhost");
		return Status.Success;
	}

}
