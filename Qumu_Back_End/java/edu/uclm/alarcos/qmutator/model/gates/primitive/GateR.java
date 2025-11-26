package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IHasRotationAngle;

public abstract class GateR extends Gate implements IHasRotationAngle {
	
	protected double angle = Math.PI/2;
	
	public GateR() {
		super();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getAngle() {
		return angle;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	@Override
	public final boolean isR() {
		return true;
	}
}
