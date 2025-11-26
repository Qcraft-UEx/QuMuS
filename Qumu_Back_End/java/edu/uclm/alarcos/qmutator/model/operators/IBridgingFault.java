package edu.uclm.alarcos.qmutator.model.operators;

public interface IBridgingFault extends IOperator {
	default String getFamily() {
		return "Bridging faults";
	}
}
