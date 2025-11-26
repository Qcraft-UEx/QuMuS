package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class Gate1 extends Gate {
	
	public Gate1() {
		super();
		this.name = "1";
		this.quirkId = 1;
	}
	
	public Gate1(QColumn column, int qubit) {
		this.column = column;
		this.qubit = qubit;
	}

	@Override
	public Object getName() {
		return 1;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gate concreteCopy() {
		return new Gate1();
	}

	@Override
	public String getQiskitCode() {
		return "circuit.initialize(" + this.qubit + ", 1)\n";
	}
	
	@Override
	public Object getQuirkId() {
		return 1;
	}

}
