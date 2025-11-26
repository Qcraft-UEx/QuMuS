package edu.uclm.alarcos.qmutator.model.gates.complex;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class CallToUserDefinedGate extends Gate implements IControlable {

	private UserDefinedGate calledGate;
	
	public CallToUserDefinedGate() {
		super();
	}

	public CallToUserDefinedGate(UserDefinedGate calledGate) {
		this();
		this.calledGate = calledGate;
	}

	public UserDefinedGate getCalledGate() {
		return calledGate;
	}
	
	@Override
	public String toString() {
		return calledGate.toString();
	}

	@Override
	public Gate concreteCopy() {
		return new CallToUserDefinedGate(calledGate);
	}

	@Override
	public String getQiskitCode() {
		return calledGate.getQiskitCode();
	}
}
