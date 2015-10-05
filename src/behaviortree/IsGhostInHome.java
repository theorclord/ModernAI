package behaviortree;

import java.util.HashMap;

import behaviortree.BehaviorTree.Status;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class IsGhostInHome extends Leaf {

	public IsGhostInHome(HashMap<String, Object> dataContext) {
		super(dataContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Status run() {
		// requires a ghost to be found
		Game game = (Game) super.data.get("game");
		int time = game.getGhostLairTime((GHOST) data.get("ghost"));
		if(time > 0){
			System.out.println("Ghost in lair");
			return Status.Success;
		}
		else
		{
			System.out.println("Ghost not in lair");
			return Status.Failed;
		}
	}

}
