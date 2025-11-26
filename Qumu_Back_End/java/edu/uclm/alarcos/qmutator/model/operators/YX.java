package edu.uclm.alarcos.qmutator.model.operators;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateX;

public class YX extends SwapGatesOperator {

	@Override
	protected String mutableGate() {
		return "Y";
	}
	
	@Override
	protected Gate newGate() {
		return new GateX();
	}
}
