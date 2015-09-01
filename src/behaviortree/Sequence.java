package behaviortree;

import behaviortree.BehaviorTree.Status;

public class Sequence extends Composite {
	private int currentNode = 0;

	@Override
	public Status run() {
		Status stat = super.nodes.get(currentNode).run();
		if (stat == Status.Failed) {
			return Status.Failed;
		} else if (stat == Status.Running) {
			return Status.Running;
		} else {
			currentNode++;
			if (super.nodes.size() == currentNode) {
				return Status.Succes;
			}
			return Status.Running;
		}
	}

}
