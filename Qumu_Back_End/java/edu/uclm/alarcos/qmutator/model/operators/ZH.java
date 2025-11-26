package edu.uclm.alarcos.qmutator.model.operators;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateH;

public class ZH extends SwapGatesOperator {

	@Override
	protected String mutableGate() {
		return "Z";
	}
	
	@Override
	protected Gate newGate() {
		return new GateH();
	}
}
