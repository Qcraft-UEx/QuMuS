package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateRX extends GateR implements IControlable {
	
	public GateRX() {
		super();
		this.name = "RX";
		this.quirkId = "X^½";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		GateRX gate = new GateRX();
		gate.angle = this.angle;
		return gate;
	}

	@Override
	public String getQiskitCode() {
		return "circuit.rx(" + this.angle + ", qreg[" + this.qubit + "])\n";
	}

}
