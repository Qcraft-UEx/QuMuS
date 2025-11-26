package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.IndexedVariableValue;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public class GIndexedValue extends GExpr {

	private GVariable variable;
	private ArrayList<GIndexValue> indexes;
	
	public GIndexedValue() {
		this.indexes = new ArrayList<>();
	}

	public GIndexedValue(String giv) {
		this();
		String[] tokens = giv.split("_");
		this.variable = new GVariable(tokens[0]);
		for (int i=1; i<tokens.length; i++) {
			GIndexStringValue index = new GIndexStringValue(tokens[i]);
			this.indexes.add(index);
		}
	}
	
	public GIndexedValue(GVariable variable, String... indexNames) {
		this();
		this.variable = variable;
		for (int i=0; i<indexNames.length; i++)
			this.indexes.add(new GIndexStringValue(indexNames[i]));
	}

	public GIndexedValue(JSONObject jso, GH h) {
		this();
		
		String variableName;
		if (jso.optJSONObject("variable")==null)
			variableName = jso.getString("variable");
		else
			variableName = jso.getJSONObject("variable").getString("name");
		this.variable = h.findVariable(variableName);
		if (this.variable==null) 
			this.variable = h.putVariable(variableName);
			
		JSONArray jsaIndexes = jso.optJSONArray("indexes");
		if (jsaIndexes==null) {
			String indexNames = jso.getString("indexes");
			String[] indexes = indexNames.split(",");
			for (int i=0; i<indexes.length; i++) {
				String sIndex = indexes[i].trim();
				try {
					int index = Integer.parseInt(sIndex);
					this.indexes.add(new GIndexIntValue(index));
				} catch (Exception e) {
					this.indexes.add(new GIndexStringValue(sIndex));
				}
			}
		} else {
			for (int i=0; i<jsaIndexes.length(); i++) {
				JSONObject jsoIndex = jsaIndexes.getJSONObject(i);
				if (jsoIndex.getString("type").equals(GIndexStringValue.class.getSimpleName())) {
					this.indexes.add(new GIndexStringValue(jsoIndex.getString("indexName")));
				} else {
					this.indexes.add(new GIndexIntValue(jsoIndex.getInt("value")));
				}
			}
		}
	}

	@Override
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		IndexedVariableValue ivv = new IndexedVariableValue(f);
		ivv.setVariable(this.variable.getName());

		for (int i=0; i<this.indexes.size(); i++) {
			GIndexValue gIndex = this.indexes.get(i);
			ivv.addIndex(gIndex.instantiate(f, index));
		}
		return ivv;
	}

	@Override
	protected Double getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GExpr copy() {
		GIndexedValue result = new GIndexedValue();
		result.variable = this.variable;
		for (GIndexValue indexValue : this.indexes)
			result.indexes.add(indexValue.copy());
		return result;
	}

	@Override
	protected GExpr replace(String variable, int value) {
		for (int i=0; i<this.indexes.size(); i++) {
			GIndexValue index = this.indexes.get(i);
			if (variable.equals(index.getName())) {
				this.indexes.set(i, new GIndexIntValue(value));
				break;
			}
		}
		return this;
	}

	@Override
	public String toString() {
		String r = this.variable.getName();
		for (GIndexValue indexValue : this.indexes)
			r+= "_" + indexValue.toString();
		return r;
	}
	
	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("variable", this.variable.toJSON());
		JSONArray jsa = new JSONArray();
		for (GIndexValue indexValue : this.indexes)
			jsa.put(indexValue.toJSON());
		jso.put("indexes", jsa);
		return jso;
	}

	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<msub>");
		sb.append(this.variable.toML());
		if (indexes.size()>0) {
			sb.append("<mrow>");
			for (int i=0; i<indexes.size(); i++)
				sb.append(this.indexes.get(i).toML());
			sb.append("</mrow>");
		}
		sb.append("</msub>");
		return sb.toString();
	}

	public void addIndex(GIndexValue index) {
		this.indexes.add(index);
	}

	public void setIndexValues(double[] values) {
		for (double value : values)
			this.indexes.add(new GIndexIntValue((int) value));
	}
	
	public void setVariable(GVariable variable) {
		this.variable = variable;
	}
	
	public ArrayList<GIndexValue> getIndexes() {
		return indexes;
	}
}
