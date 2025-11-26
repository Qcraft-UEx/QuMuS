package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateS extends Gate implements IControlable {
	
	public GateS() {
		super();
		this.name = "S";
		this.quirkId = "Z^½";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateS();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.s(qreg[" + this.qubit + "])\n";
	}
}
