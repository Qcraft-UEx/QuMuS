package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class IndexedVariableValue extends ResultExpr {
	private Variable variable;
	private List<ResultExpr> indexes;
	
	public IndexedVariableValue(CH f) {
		super(f);
		this.indexes = new ArrayList<>();
	}
	
	public IndexedVariableValue(CH f, JSONObject jso) {
		this(f);
		this.variable = new Variable(jso.getJSONObject("variable").getString("name"));
		JSONArray jsaIndexes = jso.getJSONArray("indexes");
		for (int i=0; i<jsaIndexes.length(); i++) {
			ResultExpr index = ResultExpr.build(f, jsaIndexes.getJSONObject(i));
			index.setParent(this);
			this.indexes.add(index);
		}
	}

	public IndexedVariableValue(CH f, String variableName, Integer... indexes) {
		this(f);
		this.variable = new Variable(variableName);
		for (int i=0; i<indexes.length; i++) {
			DoubleValue index = new DoubleValue(f, indexes[i]);
			index.setParent(this);
			this.indexes.add(index);
		}
	}

	public IndexedVariableValue(CH f, String variableAndIndexes) {
		this(f);
		String[] tokens = variableAndIndexes.split("_");
		String variableName = tokens[0];
		this.setVariable(variableName);
		for (int i=1; i<tokens.length; i++) {
			DoubleValue index = new DoubleValue(f).setValue(Integer.parseInt(tokens[i]));
			index.setParent(this);
			this.indexes.add(index);
		}
	}

	public IndexedVariableValue setVariable(String variableName) {
		this.variable = new Variable(variableName);
		return this;
	}
	
	public IndexedVariableValue setIndexes(ResultExpr... indexes) {
		for (ResultExpr index : indexes) {
			index.setParent(this);
			this.indexes.add(index);
		}
		return this;
	}
	
	public void addIndex(ResultExpr index) {
		this.indexes.add(index);
	}

	@Override
	protected void completeJSON(JSONObject jso) {
		jso.put("variable", variable.toJSON());
		JSONArray jsaIndexes = new JSONArray();
		for (ResultExpr resultExpr : this.indexes)
			jsaIndexes.put(resultExpr.toJSON());
		jso.put("indexes", jsaIndexes);
	}

	@Override
	public double getValue() {
		InitializedVariable variable = this.function.findVariable(this.variable.getName());
		return variable.getValue(this.indexes);
	}

	@Override
	public double getValue(List<XValue> comb, int... prods) {
		return this.getValue();
	}
	
	@Override
	public String toString() {
		/*String r = this.variable.toString();
		for (int i=0; i<this.indexes.size(); i++)
			r = r + "_" + (int) this.indexes.get(i).getValue();
		return r;*/
		return "" + this.getValue();
	}

	@Override
	public List<ResultExpr> getChildren() {
		return this.indexes;
	}

	@Override
	protected String toML() {
		return "<mn>" + this.getValue() + "</mn>";
	}
}
