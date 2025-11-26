package edu.uclm.alarcos.qmutator.model.gates;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.model.QColumn;

public abstract class Gate {
	protected String name;
	protected Object quirkId;
	protected int qubit;

	protected QColumn column;
	private List<AControlGate> controlGates = new ArrayList<>();
	
	@Override
	public abstract String toString();
	
	public Gate copy(QColumn column) {
		Gate gate = this.concreteCopy();
		gate.setName(this.name);
		gate.setQubit(this.qubit);
		gate.setQuirkId(this.quirkId);
		gate.setColumn(column);
		return gate;
	}

	public abstract Gate concreteCopy();
	
	
	public abstract String getQiskitCode();
	
	public void setColumn(QColumn column) {
		this.column = column;
	}
	
	@JsonIgnore
	public QColumn getColumn() {
		return column;
	}
	
	public int getQubit() {
		return qubit;
	}
	
	public void setQubit(int qubit) {
		this.qubit = qubit;
	}
	
	public Object getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Object getQuirkId() {
		if (quirkId==null)
			return null;
		return quirkId.toString();
	}
	
	public void setQuirkId(Object quirkId) {
		this.quirkId = quirkId;
	}

	public boolean isNumeric() {
		return this.getName().equals(0) || this.getName().equals(1) || 
				this.getName().equals("i") || this.getName().equals("-i") || 
				this.getName().equals("+") || this.getName().equals("-");
	}
	
	public boolean isR() {
		return false;
	}

	public void addControlGate(AControlGate controlGate) {
		this.controlGates.add(controlGate);
	}
	
	public List<AControlGate> getControlGates() {
		return controlGates;
	}
}
