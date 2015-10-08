package behaviortree;

import java.util.ArrayList;

public abstract class Composite extends Node {
	public ArrayList<Node> nodes;
	
	public Composite(){
		nodes = new ArrayList<Node>();
	}
}
