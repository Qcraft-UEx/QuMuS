package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.DoubleValue;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public class GIndexStringValue extends GIndexValue {

	private String indexName;

	public GIndexStringValue(String indexName) {
		this.indexName = indexName;
	}

	@Override
	protected GIndexValue copy() {
		return new GIndexStringValue(indexName);
	}

	@Override
	public String getName() {
		return this.indexName;
	}

	@Override
	public String toString() {
		return this.indexName;
	}
	
	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("indexName", this.indexName);
		return jso;
	}

	@Override
	protected ResultExpr instantiate(CH f, Integer index) {
		return new DoubleValue(f, index);
	}

	@Override
	protected String toML() {
		return "<mi>" + this.indexName + "</mi>";
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
	protected GExpr replace(String variable, int value) {
		return this;
	}
}
