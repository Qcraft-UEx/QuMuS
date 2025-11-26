package edu.uclm.alarcos.qmutator.model.gates.primitive;

public class GateNoOperation extends OneQubitGate {
	public GateNoOperation() {
		super();
		this.name = "…";
		this.quirkId = this.name;
	}

	@Override
	public String getQiskitCode() {
		return "#Gate removed (no operation)\n";
	}
	
}
