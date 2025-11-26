package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateRY extends GateR implements IControlable {
	
	public GateRY() {
		super();
		this.name = "RY";
		this.quirkId = "Y^½";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		GateRY gate = new GateRY();
		gate.angle = this.angle;
		return gate;
	}

	@Override
	public String getQiskitCode() {
		return "circuit.ry(" + this.angle + ", qreg[" + this.qubit + "])\n";
	}
}
