package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class GateI extends Gate {
	
	public GateI() {
		super();
		this.name = "I";
		this.quirkId = "\u2026";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateI();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.id(qreg[" + this.qubit + "])\n";
	}
}
