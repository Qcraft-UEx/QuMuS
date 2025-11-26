package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateSdg extends Gate implements IControlable {
	
	public GateSdg() {
		super();
		this.name = "Z^-½";
		this.quirkId = "Z^-½";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateSdg();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.sdg(qreg[" + this.qubit + "])\n";
	}
}
