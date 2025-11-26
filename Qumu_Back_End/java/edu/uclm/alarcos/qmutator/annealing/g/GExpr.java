package edu.uclm.alarcos.qmutator.annealing.g;

import java.lang.reflect.Constructor;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public abstract class GExpr {

	@SuppressWarnings("unchecked")
	public static GExpr build(JSONObject jso, GH h) {
		String type = jso.getString("type");
		if (type.equals(GParameter.class.getSimpleName())) {
			GParameter parameter = h.findParameter(jso.getString("parameter"));
			if (parameter!=null)
				return parameter;
		}
		
		type = "edu.uclm.alarcos.qmutator.annealing.g." + type;
		try {
			Class<GExpr> clazz = (Class<GExpr>) Class.forName(type);
			Constructor<GExpr> constructor = clazz.getDeclaredConstructor(JSONObject.class, GH.class);
			return constructor.newInstance(jso, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@JsonIgnore
	public abstract ResultExpr getResultExpr(CH f, String indexName, Integer index);

	@JsonIgnore
	protected abstract Double getValue();

	protected abstract GExpr copy();

	protected abstract GExpr replace(String variable, int value);

	@Override
	public abstract String toString();

	protected abstract JSONObject toJSON();

	protected abstract String toML();
}
