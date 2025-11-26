package edu.uclm.alarcos.qmutator.model.operators;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class FurtherGateDuplication extends GateDuplication implements IInitializationError {

	@Override
	public void checkMutablePositions(Circuit circuit) {
		QColumn column;
		Gate gate;
		for (int i=1; i<circuit.getColumns().size(); i++) {
			column = circuit.getColumns().get(i);
			for (int j=0; j<column.getGates().size(); j++) {
				gate = column.getGates().get(j);
				if (super.isMutable(gate))
					circuit.addMutablePosition(i, j);
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "Duplicates a one qubit gate in the second and following columns";
	}
}
