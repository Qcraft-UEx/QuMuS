package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.DoubleValue;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public class GParameter extends GExpr {

	private GH h;
	private String parameter;
	private Double value;
	
	public GParameter(GH h) {
		this.h = h;
	}

	public GParameter(JSONObject jso, GH h) {
		this(h);
		
		this.parameter = jso.getString("parameter");
		if (jso.has("value"))
			this.value = jso.getDouble("value");
		if (this.h.findParameter(this.parameter)==null)
			this.h.addParameter(this);
	}
	
	public GParameter(GH h, String parameter, Double value) {
		this(h);
		this.parameter = parameter;
		this.value = value;
		h.addParameter(this);
	}
	
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getParameter() {
		return parameter;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	@JsonIgnore
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		DoubleValue db = new DoubleValue(f);
		db.setValue(this.value);
		return db;
	}

	@Override
	@JsonIgnore
	protected Double getValue() {
		if (this.value==null)
			this.value = this.h.findParameter(this.parameter).value;
		return this.value;
	}

	@Override
	protected GExpr copy() {
		return new GParameter(h, parameter, value);
	}

	@Override
	protected GExpr replace(String variable, int value) {
		return this;
	}

	@Override
	public String toString() {
		return this.parameter + "=" + (value==null ? this.parameter : "" + this.value);
	}
	
	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("parameter", this.parameter);
		return jso;
	}
	
	@Override
	protected String toML() {
		return "<mi>" + this.parameter + "</mi>";
	}
}
