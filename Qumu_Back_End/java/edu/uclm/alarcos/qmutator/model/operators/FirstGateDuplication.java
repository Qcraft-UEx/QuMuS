package edu.uclm.alarcos.qmutator.model.operators;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class FirstGateDuplication extends GateDuplication implements IInitializationError {
	
	@Override
	public void checkMutablePositions(Circuit circuit) {
		QColumn column0 = circuit.getColumns().get(0);
		Gate gate;
		for (int i=0; i<column0.getGates().size(); i++) {
			gate = column0.getGates().get(i);
			if (super.isMutable(gate))
				circuit.addMutablePosition(0, gate.getQubit());
		}
	}
	
	@Override
	public String getDescription() {
		return "Duplicates gates in the first column";
	}
}
