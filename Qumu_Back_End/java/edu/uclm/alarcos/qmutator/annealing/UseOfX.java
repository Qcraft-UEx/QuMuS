package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class UseOfX extends ResultExpr {

	private List<ResultExpr> indexes;
	
	public UseOfX(CH f) {
		super(f);
		this.indexes = new ArrayList<>();
	}

	public UseOfX(CH f, String variableAndIndexes) {
		this(f);
		String[] tokens = variableAndIndexes.split("_");
		for (int i=1; i<tokens.length; i++) {
			DoubleValue index = new DoubleValue(f).setValue(Integer.parseInt(tokens[i]));
			index.setParent(this);
			this.indexes.add(index);
		}
	}
	
	public UseOfX(CH f, JSONObject jso) {
		this(f);
		JSONArray jsaIndexes = jso.getJSONArray("indexes");
		for (int i=0; i<jsaIndexes.length(); i++) {
			ResultExpr index = ResultExpr.build(f, jsaIndexes.getJSONObject(i));
			index.setParent(this);
			this.indexes.add(index);
		}
	}
	
	public void addIndex(ResultExpr index) {
		this.indexes.add(index);
	}

	public void setIndexes(Integer[] indexes) {
		for (Integer index : indexes)
			this.indexes.add(new DoubleValue(this.function, index));
	}

	@Override
	protected void completeJSON(JSONObject jso) {
		jso.put("variable", this.function.getX().getName());
		JSONArray jsaIndexes = new JSONArray();
		for (ResultExpr resultExpr : this.indexes)
			jsaIndexes.put(resultExpr.toJSON());
		jso.put("indexes", jsaIndexes);
	}

	public String getName() {
		return this.function.getX().getName();
	}

	@Override
	public double getValue() {
		Integer value = this.function.getXValue(indexes).getValue();
		return value;
	}
	
	@Override
	public double getValue(List<XValue> comb, int... prods) {
		double pos = 0;
		int size = this.indexes.size();
		for (int i=0; i<size; i++)
			pos = pos + this.indexes.get(i).getValue() * prods[i];
		pos = pos + this.indexes.get(size-1).getValue();
		return comb.get((int) pos).getValue();
	}

	@Override
	public String toString() {
		String r = "x";
		for (int i=0; i<this.indexes.size(); i++)
			r = r + "_" + (int) this.indexes.get(i).getValue();
		return r;
	}

	@Override
	public List<ResultExpr> getChildren() {
		return null;
	}

	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<msub>");
		sb.append("<mi>x</mi>");
		if (indexes.size()>0) {
			sb.append("<mrow>");
			for (int i=0; i<indexes.size()-1; i++)
				sb.append("<mn>" + (int) this.indexes.get(i).getValue() + "</mn><mo>,</mo>");
			sb.append("<mn>" + (int) this.indexes.get(indexes.size()-1).getValue() + "</mn>");
			sb.append("</mrow>");
		}
		sb.append("</msub>");
		return sb.toString();
	}
}
