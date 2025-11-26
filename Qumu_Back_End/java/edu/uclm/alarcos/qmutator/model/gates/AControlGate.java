package edu.uclm.alarcos.qmutator.model.gates;

import edu.uclm.alarcos.qmutator.model.gates.primitive.OneQubitGate;

public abstract class AControlGate extends OneQubitGate {
	
	protected Gate controlledGate;

	public AControlGate(String name) {
		super(name);
	}

	public void setControlledGate(Gate controlledGate) {
		this.controlledGate = controlledGate;
	}
	
	public Gate getControlledGate() {
		return controlledGate;
	}
}
