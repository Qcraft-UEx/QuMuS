package edu.uclm.alarcos.qmutator.model.gates.primitive;

import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public class GateSwap extends OneQubitGate implements IControlable {
	public GateSwap() {
		super();
		this.name="Swap";
		this.quirkId = this.name;
	}
	
	@Override
	public String getQiskitCode() {
		StringBuilder sb = new StringBuilder("circuit.swap(");
		
		int rows = column.getGates().size();
		Gate gate;
		for (int i=0; i<rows; i++) { 
			gate = column.getGates().get(i);
			if (gate instanceof GateSwap)
				sb = sb.append("qreg[" + i + "], ");
		}
		
		String r = sb.toString();
		r = r.substring(0,  r.length()-2) + ")\n";
		return r;
	}
}
