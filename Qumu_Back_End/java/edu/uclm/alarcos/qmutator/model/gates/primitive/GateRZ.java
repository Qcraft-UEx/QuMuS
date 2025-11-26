package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;
import edu.uclm.alarcos.qmutator.model.gates.IHasRotationAngle;

public class GateRZ extends GateR implements IControlable, IHasRotationAngle {
	
	public GateRZ() {
		super();
		this.name = "RZ";
		this.quirkId = "Z^½";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		GateRZ gate = new GateRZ();
		gate.angle = this.angle;
		return gate;
	}

	@Override
	public String getQiskitCode() {
		return "circuit.rz(" + this.angle + ", qreg[" + this.qubit + "])\n";
	}
	
}
