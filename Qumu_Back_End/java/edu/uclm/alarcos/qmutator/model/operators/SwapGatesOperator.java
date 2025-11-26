package edu.uclm.alarcos.qmutator.model.operators;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.GateBuilder;
import edu.uclm.alarcos.qmutator.model.gates.IControlable;

public abstract class SwapGatesOperator extends Operator implements IWrongGate {
	
	@Override
	public void checkMutablePositions(Circuit circuit) {
		QColumn column;
		Gate gate;
		for (int i=0; i<circuit.getColumns().size(); i++) {
			column = circuit.getColumns().get(i);
			for (int j=0; j<column.getGates().size(); j++) {
				gate = column.getGates().get(j);
				if (!column.getControlGates().isEmpty())
					break;
				if (gate.getName().equals(mutableGate()))
					circuit.addMutablePosition(i, j);
			}
		}
	}
	
	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		Iterator<Integer> keys = circuit.getMutablePositions().keySet().iterator();
		QColumn column;
		
		while (keys.hasNext()) {
			Integer columnIndex = keys.next();
			column = circuit.getColumn(columnIndex);
			if (!column.getControlGates().isEmpty())
				continue;
			ArrayList<Integer> rowIndexes = circuit.getMutablePositions().get(columnIndex);
			int rowIndex;
			for (int i=0; i<rowIndexes.size(); i++) {
				rowIndex = rowIndexes.get(i);
				Gate existingGate = column.getGates().get(rowIndex);
				Gate newGate = GateBuilder.build(column, rowIndex, this.newGate().getName().toString());
				
				if (!existingGate.getControlGates().isEmpty() && !(newGate instanceof IControlable))
					continue;
				
				Mutant mutant = prepareMutant(circuit);
				Circuit mutantCircuit = mutant.getQuantumCircuit();
				mutantCircuit.setOutputQubits(circuit.getOutputQubits());
				mutantCircuit.setGate(columnIndex, newGate);
				
				this.saveMutant(contador, mutant, callableGateIndex, columnIndex, rowIndex);
			}
		}
	}

	@Override
	public String getDescription() {
		return "Swaps a gate by another one";
	}

	protected abstract String mutableGate();
	
	protected abstract Gate newGate();
}
