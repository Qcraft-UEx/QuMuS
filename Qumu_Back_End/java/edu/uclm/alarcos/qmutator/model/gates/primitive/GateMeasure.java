package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class GateMeasure extends Gate {
	
	public GateMeasure() {
		super();
		this.name = "Measure";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new GateMeasure();
	}

	@Override
	public String getQiskitCode() {
		return "#Measure\n";
	}

}
