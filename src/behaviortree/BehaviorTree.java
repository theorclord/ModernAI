package behaviortree;

import java.util.HashMap;

public class BehaviorTree {
	public enum Status {
		Failed, Succes, Running
	}

	private Node root;
	public HashMap<String, Object> data;

	public BehaviorTree(){
		data = new HashMap<String,Object>();
	}
	public boolean AddNode(Node parent, Node child) {
		if (parent == null) {
			root = child;
		}
		if (parent instanceof Leaf) {
			return false;
		}
		if (parent instanceof Decorator) {
			((Decorator) parent).child = child;
			return true;
		}
		if (parent instanceof Composite) {
			((Composite) parent).nodes.add(child);
		}

		return false;
	}

	public void SetRootNode(Node root) {
		this.root = root;
	}

	public void Run() {
		root.run();
		// loop through nodes, execute leafs
	}

}
