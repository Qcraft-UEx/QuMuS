package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateU extends Gate implements IControlable {
	
	private double theta = Math.PI / 2;
	private double phi = Math.PI / 2;
	private double lambda = Math.PI / 2;
	
	public GateU() {
		super();
		this.name = "U";
		this.quirkId = this.name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		GateU gate = new GateU();
		gate.theta = this.theta;
		gate.phi = this.phi;
		gate.lambda = this.lambda;
		return gate;
	}

	@Override
	public String getQiskitCode() {
		return "circuit.u(" + this.theta + ", " + this.phi + ", " + this.lambda + ", qreg[" + this.qubit + "])\n";
	}
}
