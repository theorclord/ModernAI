package clbw;

/**
 * BehaviourTree structure ready for implementation
 * 
 * @author Claus Wind
 * Implementation inspired from http://www.gamasutra.com/blogs/ChrisSimpson/20140717/221339/Behavior_trees_for_AI_How_they_work.php
 */
public class BehaviourTree {
	Node root;
	public enum STATE {
		SUCCESS, FAILURE, RUNNING, NOT_ACTIVE
	}
	/*
	 * Composite (one or more children)
	 * 	Sequence (do all)
	 * 	Selector (do till succeed)
	 * 	Random of both
	 * Decorator (processing single child)
	 * 	Inverter (success->fail, fail->success)
	 * 	Succeeder (always success, regardless)
	 * 	Repeater (keep repeating)
	 * 	Repeat Until Fail (will continue to reprocess their child, until the child returns a failure, 
	 *  	at which point the repeater will return success to its parent)
	 * Leaf (actions)
	 */
	
	abstract class Node {
		STATE state = STATE.NOT_ACTIVE;
		String name = "";
		Node parent = null;
		
		public void Name(String Name) {
			name = Name;
		}
		
		public abstract STATE Run();
		
		public boolean Equals(Node node) {
			boolean equals = true;
			if(!name.equals(node.name))
				equals = false;
			if(!state.equals(node.state))
				equals = false;
			if(!parent.equals(node.parent))
				equals = false;
			return equals;
		}
		
		public String toString() {
			return name;
		}
	}
	
	abstract class Composite extends Node {
		Node[] children;
		
		public Composite(String name, Node[] children) {
			Name(name);
			CreateChildren(children);
		}
		
		public Node[] Add(Node child) {
			Node[] newList = new Node[children.length + 1];
			child.parent = this;
			
			for(int i = 0; i < children.length; i++) {
				newList[i] = children[i];
			} newList[children.length] = child;
			
			this.children = newList;
			return newList;
		}
		protected void CreateChildren(Node[] children) {
			Node[] newList = new Node[children.length];
			
			for(int i = 0; i < children.length; i++) {
				children[i].parent = this;
				newList[i] = children[i];
			} 
			
			this.children = newList;
		}
		
		public Node[] Remove(Node child) {
			Node[] newList = new Node[children.length - 1];
			int j = 0;
			
			for(int i = 0; i < children.length; i++) {
				if(!children[i].Equals(child)) 
					newList[j++] = children[i];
			}
			child.parent = null;

			this.children = newList;
			return newList;
		}

		public boolean Equals(Node node) {
			boolean equals = super.Equals(node);
			if(node instanceof Composite) {
				for(int i = 0; i < children.length; i++) {
					if(!this.children[i].Equals(((Composite) node).children[i])) {
						equals = false;
						break;
					}
				}
				return equals;
			}
			return false;
		}
	}
	
	public class Sequence extends Composite { // Operates like an AND-statement 
		public Sequence(String name, Node[] children) {
			super(name, children);
		}
		public Sequence(Node[] children) {
			super("Sequence", children);
		}
		
		public STATE Run() { 
			STATE curr = STATE.NOT_ACTIVE;

			state = STATE.RUNNING;
			for(Node n : children) { 
				curr = n.Run();
				if(curr == STATE.FAILURE) {
					state = STATE.FAILURE;
					return STATE.FAILURE;
				}
			}
			state = STATE.SUCCESS;
			return STATE.SUCCESS;
		}
	}
	
	public class Selector extends Composite { // Operates like an OR-statement 
		public Selector(String name, Node[] children) {
			super(name, children);
		}
		public Selector(Node[] children) {
			super("Selector", children);
		}
		
		public STATE Run() { 
			STATE curr = STATE.NOT_ACTIVE;

			state = STATE.RUNNING;
			for(Node n : children) {
				curr = n.Run();
				
				if(curr == STATE.SUCCESS){
					state = STATE.SUCCESS;
					return STATE.SUCCESS;
				}
			}
			state = STATE.FAILURE;
			return STATE.FAILURE;
		}
	}
	
	abstract class Decorator extends Node {
		Node child;
		
		public Decorator(String name, Node child) {
			Name(name);
			child.parent = this;
			this.child = child;
		}
		
		public boolean Equals(Node node) {
			boolean equals = super.Equals(node);
			if(node instanceof Decorator) {
				if(!this.child.Equals(((Decorator) node).child))
					equals = false;
				return equals;
			}
			return false;
		}
	}
	
	public class Inverter extends Decorator { // success->fail, fail->success 
		public Inverter(String name, Node child) {
			super(name, child);
		}
		public Inverter(Node child) {
			super("Inverter", child);
		}
		
		public STATE Run() {
			STATE curr = child.Run();
			if(curr.equals(STATE.FAILURE)) {
				state = STATE.SUCCESS;
				return STATE.SUCCESS;
			}
			else if(curr.equals(STATE.SUCCESS)) {
				state = STATE.FAILURE;
				return STATE.FAILURE;
			}
			state = STATE.RUNNING;
			return STATE.RUNNING; // Something went wrong with the implementation the child
		}
	}
	
	public class Succeeder extends Decorator { // always success, regardless 
		public Succeeder(String name, Node child) {
			super(name, child);
		}
		public Succeeder(Node child) {
			super("Succeeder", child);
		}
		
		public STATE Run() {
			child.Run();
			state = STATE.SUCCESS;
			return STATE.SUCCESS;
		}
	}
	
	public class Repeater extends Decorator { // keep repeating 
		int repeat;
		public Repeater(String name, Node child, int count) {
			super(name, child);
			repeat = count;
		}
		public Repeater(Node child, int count) {
			super("Repeater", child);
			repeat = count;
		}
		
		public STATE Run() {
			for(repeat--; repeat > 0;)
				child.Run();
			state = STATE.SUCCESS;
			return STATE.SUCCESS;
		}
	}
	
	public class RepeatUntilFail extends Decorator { // repeat until fail, then succeed 
		public RepeatUntilFail(String name, Node child) {
			super(name, child);
		}
		public RepeatUntilFail(Node child) {
			super("RepeatUntilFail", child);
		}
		
		public STATE Run() {
			STATE curr = STATE.NOT_ACTIVE;
			while(!(curr == STATE.FAILURE)) {
				curr = child.Run();
				state = STATE.RUNNING;
			}
			state = STATE.SUCCESS;
			return STATE.SUCCESS;
		}
	}
	
	public abstract class Leaf extends Node {
		public Leaf(String name) {
			Name(name);
		}
	}
}
