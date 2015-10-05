package behaviortree;

import behaviortree.BehaviorTree.Status;

public class Inverter extends Decorator {

	@Override
	public Status run() {
		Status stat = child.run();
		if(stat == Status.Failed){
			return Status.Success;
		} else if(stat == Status.Success){
			return Status.Failed;
		} else {
			return Status.Running;
		}
	}

}
