package stateMachines;

import java.util.ArrayList;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class StateMachine {

	public int MinDist;
	public int DistFromNonEidable;
	public int DistToPowerPill;
	private State currentState;
	public StateMachine(int MinDist, int DistFromNonEidable, int DistToPowerPill) {
		this.MinDist = MinDist;
		this.DistFromNonEidable = DistFromNonEidable;
		this.DistToPowerPill = DistToPowerPill;
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
