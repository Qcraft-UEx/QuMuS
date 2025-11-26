package edu.uclm.alarcos.qmutator.model.gates.complex;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.gates.Gate;

public abstract class UserDefinedGate extends Gate {
	protected Circuit containerCircuit;

	public UserDefinedGate() {
		super();
	}
	
	@JsonIgnore
	public Circuit getContainerCircuit() {
		return containerCircuit;
	}
	
	public void setContainerCircuit(Circuit containerCircuit) {
		this.containerCircuit = containerCircuit;
	}
	
	public JSONObject toJSON() {
		return new JSONObject().put("id", this.quirkId).put("name", this.name);
	}

	public abstract String getDeclaration();

}
