package behaviortree;

import behaviortree.BehaviorTree.Status;

public class Sequence extends Composite {
	private int currentNode = 0;

	@Override
	public Status run() {
		System.out.println(currentNode);
		Status stat = super.nodes.get(currentNode).run();
		if (stat == Status.Failed) {
			return Status.Failed;
		} else if (stat == Status.Running) {
			return Status.Running;
		} else {
			System.out.println("inc");
			currentNode++;
			if (super.nodes.size() == currentNode) {
				System.out.println("hit");
				currentNode = 0;
				return Status.Succes;
			}
			return Status.Running;
		}
	}

}
