package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

public class GFrom {

	private GIndexedSummation sum;
	private GIndex index;
	
	public GFrom(GIndexedSummation sum) {
		this.sum = sum;;
	}

	public GFrom(GIndexedSummation sum, JSONObject jso, GH h) {
		this(sum);
		JSONObject jsoIndex = jso.getJSONObject("index");
		String indexName = jsoIndex.getString("index");
		
		JSONObject jsoStart = jsoIndex.getJSONObject("start");
		GExpr value = GExpr.build(jsoStart, h);
		
		this.index = newIndex(indexName, value);
	}

	public GIndex newIndex(String indexName, GExpr value) {
		GIndex index = this.sum.findIndex(indexName);
		if (index!=null)
			return index;
		this.index = new GIndex(indexName, value);
		this.sum.add(this.index);
		return this.index;
	}
	
	public GIndex newIndex(String indexName, int value) {
		GIndex index = this.sum.findIndex(indexName);
		if (index!=null)
			return index;
		index = new GIndex(indexName, value);
		this.index = index;
		this.sum.add(this.index);
		return this.index;
	}

	public GIndex getIndex() {
		return this.index;
	}

	public Integer getValue() {
		GIndex gIndex = this.sum.findIndex(index.getName());
		return gIndex.getValue(0);
	}

	@Override
	public String toString() {
		return this.index.toString();
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", "GFrom");
		jso.put("index", this.index.toJSON());
		return jso;
	}

	public void setValue(GExpr value) {
		this.index.setStart(value);
	}

	public String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.index.toML());
		return sb.toString();
	}
	
}
