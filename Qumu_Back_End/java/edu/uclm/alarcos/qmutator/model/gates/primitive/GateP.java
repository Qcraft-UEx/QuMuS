package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;
import edu.uclm.alarcos.qmutator.model.gates.IHasRotationAngle;

public class GateP extends Gate implements IHasRotationAngle, IControlable {
	
	private double angle = Math.PI/2;
	
	public GateP() {
		super();
		this.name = "P";
		this.quirkId = "X^½";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		GateP gate = new GateP();
		gate.angle = this.angle;
		return gate;
	}

	@Override
	public String getQiskitCode() {
		return "circuit.p(" + this.angle + ", qreg[" + this.qubit + "])\n";
	}
	
	@Override
	public double getAngle() {
		return this.angle;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
}
