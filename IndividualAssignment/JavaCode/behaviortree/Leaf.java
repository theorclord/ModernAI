package behaviortree;

import java.util.HashMap;

public abstract class Leaf extends Node {
	public HashMap<String,Object> data;
	
	public Leaf(HashMap<String,Object> dataContext){
		data = dataContext;
	}
	
	public void SetVariable(String varName, Object var){
		data.put(varName, var);
	}
}
