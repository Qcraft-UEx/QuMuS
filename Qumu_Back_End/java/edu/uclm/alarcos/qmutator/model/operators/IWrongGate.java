package edu.uclm.alarcos.qmutator.model.operators;

public interface IWrongGate extends IOperator {
	default String getFamily() {
		return "Swap gate";
	}
}
