package edu.uclm.alarcos.qmutator.model.operators;

public interface IMissingGate extends IOperator {
	default String getFamily() {
		return "Missing gate";
	}
}
