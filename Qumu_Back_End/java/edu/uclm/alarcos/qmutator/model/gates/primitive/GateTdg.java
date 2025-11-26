package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateTdg extends GateR implements IControlable {
	
	public GateTdg() {
		super();
		this.name = "Z^-¼";
		this.angle = - this.angle;
		this.quirkId = "Z^-¼";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateTdg();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.rz(" + this.angle + ", qreg[" + this.qubit + "])\n";
	}
}
