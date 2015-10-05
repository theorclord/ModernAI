package stateMachines;
import java.util.HashMap;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class StateMachine {

	public int MinDist;
	public int DistFromNonEdable;
	public int DistToPowerPill;
	private State currentState;
	public HashMap<String,Object> dataStruc;
	public int DistToEdable;
	public StateMachine( int DistFromNonEdable, int DistToPowerPill,
			int DistToEdable) {
		dataStruc = new HashMap<String,Object>();
		this.DistFromNonEdable = DistFromNonEdable;
		this.DistToPowerPill = DistToPowerPill;
		this.DistToEdable = DistToEdable;
		dataStruc.put("moveNearestPill", new MoveToNearestPill(this));
		dataStruc.put("moveNearestPowerPill", new MoveToNearestPowerPill(this));
		dataStruc.put("runFromGhost", new RunFromGhost(this));
		dataStruc.put("eatGhost", new EatGhost(this));
	}
	
	public MOVE run(Game game, long timeDue){
		State tempState = currentState.changeState(game, timeDue);
		if(tempState != null){
			currentState = tempState;
			run(game, timeDue);
		}
		return currentState.run(game, timeDue);
	}
	
	public void SetCurrentState(State state){
		currentState = state;
	}

}
