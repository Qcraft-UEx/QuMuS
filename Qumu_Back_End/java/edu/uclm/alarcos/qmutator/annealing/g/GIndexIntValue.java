package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.DoubleValue;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public class GIndexIntValue extends GIndexValue {

	private int indexValue;

	public GIndexIntValue(int indexValue) {
		this.indexValue = indexValue;
	}

	@Override
	protected GIndexValue copy() {
		return new GIndexIntValue(this.indexValue);
	}

	@Override
	protected String getName() {
		return "" + this.indexValue;
	}

	@Override
	public String toString() {
		return "" + this.indexValue;
	}
	
	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("indexValue", this.indexValue);
		return jso;
	}

	@Override
	protected ResultExpr instantiate(CH f, Integer index) {
		return new DoubleValue(f, this.indexValue);
	}
	
	@JsonIgnore
	public int getIndexValue() {
		return indexValue;
	}

	@Override
	protected String toML() {
		return "<mn>" + this.indexValue + "</mn>";
	}

	@Override
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		return new DoubleValue(f, this.indexValue);
	}

	@Override
	protected Double getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GExpr replace(String variable, int value) {
		return this;
	}
}
