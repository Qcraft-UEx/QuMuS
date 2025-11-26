package edu.uclm.alarcos.qmutator.model.operators;

import java.util.Iterator;
import java.util.List;

import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.ControlGate;

public class ForceEntanglement extends Operator {

	@Override
	public void checkMutablePositions(Circuit circuit) {
		List<QColumn> columns = circuit.getColumns();
		for (int i=0; i<columns.size()-1; i++) {
			QColumn leftColumn = columns.get(i);
			int posControl  = this.indexOf(leftColumn, ControlGate.class);
			if (posControl==-1)
				continue;
			int posX = this.indexOf(leftColumn, "X");
			if (posX==-1)
				continue;
			QColumn rightColumn = columns.get(i+1);
			int posH = this.indexOf(rightColumn, "H");
			if (posH==-1 || posH!=posControl)
				continue;
			
			circuit.addMutablePosition(i);
		}
	}
	
	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		Iterator<Integer> keys = circuit.getMutablePositions().keySet().iterator();
		while (keys.hasNext()) {
			Integer columnIndex = keys.next();
			
			Mutant mutant = prepareMutant(circuit);
			Circuit mutantCircuit = mutant.getQuantumCircuit();
			mutantCircuit.swapColumns(columnIndex, columnIndex+1);
			this.saveMutant(contador, mutant, callableGateIndex, columnIndex, -1);
		}
	}
	
	@Override
	public String getDescription() {
		return "Detects the opposite structure to an entanglement and forces it";
	}
	
	@Override
	public String getFamily() {
		return "Entanglement";
	}
}
