package edu.uclm.alarcos.qmutator.model.operators;

import java.util.Iterator;
import java.util.List;

import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.ControlGate;

public abstract class EntanglementOperator extends Operator {
	
	@Override
	public void checkMutablePositions(Circuit circuit) {
		List<QColumn> columns = circuit.getColumns();
		for (int i=0; i<columns.size()-1; i++) {
			QColumn leftColumn = columns.get(i);
			int posH = this.indexOf(leftColumn, "H");
			if (posH == -1)
				continue;
			QColumn rightColumn = columns.get(i+1);
			int posControl = this.indexOf(rightColumn, ControlGate.class);
			if (posControl==-1 || posControl!=posH)
				continue;
			int posX = this.indexOf(rightColumn, "X");
			if (posX==-1)
				continue;
			
			circuit.addMutablePosition(i, posControl, posX);
		}
	}

	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		Iterator<Integer> columns = circuit.getMutablePositions().keySet().iterator();
		while (columns.hasNext()) {
			Integer columnIndex = columns.next();
			
			Mutant mutant = prepareMutant(circuit);
			Circuit mutantCircuit = mutant.getQuantumCircuit();
			mutantCircuit.swapColumns(columnIndex, columnIndex+1);
			this.saveMutant(contador, mutant, callableGateIndex, columnIndex, -1);
		}
	}

	@Override
	public String getFamily() {
		return "Entanglement";
	}
}
