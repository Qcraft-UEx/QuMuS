package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;
import edu.uclm.alarcos.qmutator.annealing.UseOfX;

public class GUseOfX extends GExpr {

	private ArrayList<GIndexValue> indexes;
	
	public GUseOfX() {
		this.indexes = new ArrayList<>();
	}
	
	public GUseOfX(String... indexNames) {
		this();
		for (String indexName : indexNames)
			this.indexes.add(new GIndexStringValue(indexName));
	}

	public GUseOfX(JSONObject jso, GH h) {
		this();
		
		JSONArray jsaIndexes = jso.optJSONArray("indexes");
		
		if (jsaIndexes==null) {
			String indexNames = jso.optString("indexes");
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
	@JsonIgnore
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		UseOfX uox = new UseOfX(f);
		for (int i=0; i<this.indexes.size(); i++) {
			GIndexValue gIndex = this.indexes.get(i);
			ResultExpr concreteIndex = gIndex.instantiate(f, index);
			uox.addIndex(concreteIndex);
		}
		return uox;
	}

	@Override
	@JsonIgnore
	protected Double getValue() {
		return null;
	}

	@Override
	protected GExpr copy() {
		GUseOfX result = new GUseOfX();
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
		String r = "x";
		for (GIndexValue indexValue : this.indexes)
			r+= "_" + indexValue.toString();
		return r;
	}
	
	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		JSONArray jsa = new JSONArray();
		for (GIndexValue indexValue : this.indexes)
			jsa.put(indexValue.toJSON());
		jso.put("indexes", jsa);
		return jso;
	}

	public void addIndex(GIndexValue index) {
		this.indexes.add(index);
	}
	
	public ArrayList<GIndexValue> getIndexes() {
		return indexes;
	}
	
	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<msub>");
		sb.append("<mi>x</mi>");
		if (indexes.size()>0) {
			sb.append("<mrow>");
			for (int i=0; i<indexes.size(); i++)
				sb.append(this.indexes.get(i).toML());
			sb.append("</mrow>");
		}
		sb.append("</msub>");
		return sb.toString();
	}
}
