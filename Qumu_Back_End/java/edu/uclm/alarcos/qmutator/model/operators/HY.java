package edu.uclm.alarcos.qmutator.model.operators;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateY;

public class HY extends SwapGatesOperator {
	
	@Override
	protected String mutableGate() {
		return "H";
	}
	
	@Override
	protected Gate newGate() {
		return new GateY();
	}
}
