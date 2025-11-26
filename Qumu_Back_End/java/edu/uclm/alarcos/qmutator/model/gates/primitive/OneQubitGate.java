package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;

public abstract class OneQubitGate extends Gate {
	
	protected OneQubitGate() {
		super();
	}
	
	protected OneQubitGate(String name) {
		this();
		this.name = name;
		this.quirkId = this.name;
	}
	
	@Override
	public String toString() {
		return "" + this.name;
	}

	@Override
	public Gate concreteCopy() {
		try {
			return this.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
