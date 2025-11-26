package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class Gate0 extends Gate {
	
	public Gate0() {
		super();
		this.name = "0";
		this.quirkId = "0";
	}
	
	@Override
	public Object getName() {
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new Gate0();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.reset(qreg[" + this.qubit + "])\n";
	}
	
	@Override
	public Object getQuirkId() {
		return 0;
	}
}
