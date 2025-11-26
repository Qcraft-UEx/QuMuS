package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateH extends Gate implements IControlable {
	
	public GateH() {
		super();
		this.name = "H";
		this.quirkId = this.name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateH();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.h(qreg[" + this.qubit + "])\n";
	}
}
