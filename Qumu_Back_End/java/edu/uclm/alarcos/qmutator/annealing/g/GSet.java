package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public class GSet extends GExpr {
	private GH h;
	private String name;
	private List<Integer> values;
	
	public GSet(GH h) {
		this.h = h;
	}
	
	public GSet(GH h, String setName) {
		this(h);
		this.name = setName;
		this.values = new ArrayList<>();
	}
	
	public GSet(JSONObject jso, GH h) {
		this(h);
		this.name = jso.getString("name");
		JSONArray jsaValues = jso.getJSONArray("values");
		for (int i=0; i<jsaValues.length(); i++)
			this.values.add(jsaValues.getInt(i));
	}
	
	public void setValues(List<Integer> values) {
		this.values = values;
	}
	
	public void setValues(Integer... values) {
		for (int i=0; i<values.length; i++)
			this.values.add(values[i]);
	}
	
	public void addValue(Integer value) {
		this.values.add(value);
	}
	
	public String getName() {
		return name;
	}
	
	public List<Integer> getValues() {
		return values;
	}

	@Override
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Double getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GExpr copy() {
		GSet result = new GSet(h, this.name);
		for (Integer value : this.values)
			result.addValue(value);
		return result;
	}

	@Override
	protected GExpr replace(String variable, int value) {
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.name + "={");
		if (!this.values.isEmpty()) {
			for (int i=0; i<this.values.size()-1; i++)
				sb.append(values.get(i)+ ", ");
			sb.append(values.get(this.values.size()-1));
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("name", this.name);
		return jso;
	}

	@Override
	protected String toML() {
		// TODO Auto-generated method stub
		return null;
	}
}
