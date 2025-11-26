package edu.uclm.alarcos.qmutator.model.operators;

public interface IInitializationError extends IOperator {
	default String getFamily() {
		return "Initialization errors";
	}
}
