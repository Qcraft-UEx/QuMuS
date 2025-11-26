package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class GateT extends Gate {
	
	public GateT() {
		super();
		this.name = "Z^¼";
		this.quirkId = "Z^¼";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateT();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.t(qreg[" + this.qubit + "])\n";
	}

}
