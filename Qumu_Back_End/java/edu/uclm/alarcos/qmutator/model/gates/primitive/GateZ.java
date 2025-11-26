package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateZ extends Gate implements IControlable {
	
	public GateZ() {
		super();
		this.name = "Z";
		this.quirkId = this.name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateZ();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.z(qreg[" + this.qubit + "])\n";
	}
}
