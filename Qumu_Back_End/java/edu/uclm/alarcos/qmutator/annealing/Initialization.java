package edu.uclm.alarcos.qmutator.annealing;

import org.json.JSONObject;

public class Initialization extends Expr {

	private Variable left;
	private Expr right;
	
	public Initialization setLeft(Variable left) {
		this.left = left;
		return this;
	}

	public Initialization setLeft(String variableName) {
		this.left = new Variable(variableName);
		return this;
	}
	
	public Initialization setRight(Expr right) {
		this.right = right;
		return this;
	}

	public Initialization setRight(int intValue) {
		this.right = new DoubleValue(null, intValue);
		return this;
	}

	@Override
	protected void completeJSON(JSONObject jso) {
		jso.put("left", this.left.toJSON());
		jso.put("right", this.right.toJSON());
	}


}
