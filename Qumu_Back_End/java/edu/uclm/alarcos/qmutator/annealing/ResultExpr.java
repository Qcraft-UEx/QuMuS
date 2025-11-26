package edu.uclm.alarcos.qmutator.annealing;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public abstract class ResultExpr extends Expr {
	
	protected ResultExpr parent;
	protected CH function;
	
	public ResultExpr(CH f) {
		super();
		this.function = f;
	}
	
	public void setFunction(CH function) {
		this.function = function;
	}

	public abstract double getValue();
	
	public ResultExpr setLambda(double lambda) {
		return new Product(this.function, new Lambda(this.function, lambda), this);
	}
	
	@SuppressWarnings("unchecked")
	public static ResultExpr build(CH f, JSONObject jso) {
		String type = jso.getString("type");
		try {
			type = "edu.uclm.alarcos.qmutator.annealing." + type;
			Class<ResultExpr> clazz = (Class<ResultExpr>) Class.forName(type);
			Constructor<ResultExpr> constructor = clazz.getDeclaredConstructor(CH.class, JSONObject.class);
			return constructor.newInstance(f, jso);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Lambda getLambda() {
		return null;
	}
	
	public void setParent(ResultExpr parent) {
		this.parent = parent;
	}
	
	public ResultExpr getParent() {
		return parent;
	}
	
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		completeJSON(jso);
		return jso;
	}

	public abstract List<ResultExpr> getChildren();
	
	protected abstract void completeJSON(JSONObject jso);

	protected abstract String toML();

	public abstract double getValue(List<XValue> comb, int... prods);

}
