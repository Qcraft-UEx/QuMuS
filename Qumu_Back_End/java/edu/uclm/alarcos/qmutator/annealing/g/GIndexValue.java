package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public abstract class GIndexValue extends GExpr {
	
	protected abstract GIndexValue copy();

	protected abstract String getName();
	
	@Override
	public abstract String toString();

	protected abstract ResultExpr instantiate(CH f, Integer index);

	protected abstract JSONObject toJSON();

	protected abstract String toML();

}
