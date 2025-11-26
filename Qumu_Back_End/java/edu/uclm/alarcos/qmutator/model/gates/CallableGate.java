package edu.uclm.alarcos.qmutator.model.gates;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.gates.complex.UserDefinedGate;

public class CallableGate extends UserDefinedGate {
	
	protected Circuit circuit;
	
	public CallableGate() {
		super();
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject jso = super.toJSON();
		jso.put("circuit", this.circuit.toJSON());
		return jso;
	}
	
	@Override
	public String toString() {
		return this.toJSON().toString();
	}

	@Override
	public Gate concreteCopy() {
		CallableGate result = new CallableGate();
		result.setCircuit(this.circuit);
		return result;
	}

	@Override
	public String getQiskitCode() {
		return "#" + this.getClass().getSimpleName() + "\n";
	}
	
	@JsonIgnore
	public Circuit getCircuit() {
		return circuit;
	}
	
	public void setCircuit(Circuit circuit) {
		this.circuit = circuit;
	}
	
	@Override
	public String getDeclaration() {
		return "#Declaration of " + this.getClass().getSimpleName() + "\n";
	}
}
