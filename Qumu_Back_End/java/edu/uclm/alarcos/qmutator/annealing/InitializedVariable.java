package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.g.ArbitraryArray;

public class InitializedVariable extends Expr {

	private String name;
	private ArbitraryArray<Double> values;
	
	public InitializedVariable(String variableName) {
		this.name = variableName;
		this.values = new ArbitraryArray<Double>();
	}
	
	@Override
	protected void completeJSON(JSONObject jso) {
		jso.put("name", name);
		JSONArray jsa = this.values.toJSON();
		jso.put("values", jsa);
		jso.put("cardinals", new JSONArray(this.values.getDimensions()));
	}

	public void setValues(ArbitraryArray<Double> values) {
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public int cardinal() {
		return (int) values.getCardinal();
	}

	public double getValue(List<ResultExpr> exprs) {
		int[] pos = new int[exprs.size()];
		for (int i=0; i<exprs.size(); i++) {
			ResultExpr expr = exprs.get(i);
			pos[i] = (int) expr.getValue();
		}
		return this.values.get(pos);
	}

	public void setValues(ArrayList<Double> values) {
		this.values.setValues(values);
	}

	public void setCardinals(ArrayList<Integer> cardinals) {
		this.values.setDimensions(cardinals);
	}
	
	public void setCardinals(int... cardinals) {
		this.values.setDimensions(cardinals);
	}
	
	@Override
	public String toString() {
		return this.name + "= { " + this.values.toString() + "}";
	}
}
