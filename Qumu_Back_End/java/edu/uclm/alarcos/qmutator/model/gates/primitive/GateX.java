package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateX extends Gate implements IControlable {
	
	public GateX() {
		super();
		this.name = "X";
		this.quirkId = this.name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateX();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.x(qreg[" + this.qubit + "])\n";
	}
}
