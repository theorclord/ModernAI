package behaviortree;

import behaviortree.BehaviorTree.Status;

public class Inverter extends Decorator {

	@Override
	public Status run() {
		Status stat = child.run();
		if(stat == Status.Failed){
			return Status.Succes;
		} else if(stat == Status.Succes){
			return Status.Failed;
		} else {
			return Status.Running;
		}
	}

}
