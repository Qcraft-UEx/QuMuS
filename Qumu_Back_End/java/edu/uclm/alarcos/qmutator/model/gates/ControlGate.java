package edu.uclm.alarcos.qmutator.model.gates;

import org.json.JSONArray;

public class ControlGate extends AControlGate {
	
	public ControlGate() {
		super("•");
	}

	public String getQiskitCode() {
		/*if (this.controlledGate instanceof RotationOneQubitGate) {
			RotationOneQubitGate roqb = (RotationOneQubitGate) this.controlledGate;
			String r = "circuit.cu(";
			UserDefinedGate udg = this.column.getContainerCircuit().findCustomizableGate(roqb.getName());
			r = r + udg.getQiskitCode();
			r = r + ", qreg[" + this.qubit + "], qreg[" + this.controlledQubit + "])\n";
			return r;
		} else if (this.controlledGate instanceof SimpleOneQubitGate) {
			return "circuit.cx(qreg[" + this.qubit + "], qreg[" +  this.controlledQubit + "])\n";
		} else {
			return "# ERROR con la controlledGate";
		}*/
		return "";
	}

	public JSONArray toJSON() {
		JSONArray jsa = new JSONArray();
		
		/*int min = this.qubit<this.controlledQubit ? this.qubit : this.controlledQubit;
		
		for (int i=0; i<min; i++)
			jsa.put(1);

		if (min==this.qubit) {
			jsa.put(this.name);
			for (int i=min+1; i<this.controlledQubit; i++)
				jsa.put(1);
			jsa.put(this.getControlledGate().getQuirkId());
		} else {
			jsa.put(this.getControlledGate().getQuirkId());
			for (int i=min+1; i<this.qubit; i++)
				jsa.put(1);
			jsa.put(this.name);
		}*/
		
		return jsa;
	}
}
