package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateY extends Gate implements IControlable {
	
	public GateY() {
		super();
		this.name = "Y";
		this.quirkId = this.name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateY();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.y(qreg[" + this.qubit + "])\n";
	}
}
