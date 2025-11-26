package edu.uclm.alarcos.qmutator.model.operators;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.ControlGate;
import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateSwap;

public abstract class GateDuplication extends Operator implements IInitializationError {
	
	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		Integer columnIndex;
		int rowIndex;
		Gate newGate;
		ArrayList<Integer> rowIndexes;
		QColumn originalColumn, mutatedColumn;
		
		Iterator<Integer> columns = circuit.getMutablePositions().keySet().iterator();
		boolean swapMutated;
		while (columns.hasNext()) {
			columnIndex = columns.next();
			rowIndexes = circuit.getMutablePositions().get(columnIndex);
			swapMutated = false;
			for (int j=0; j<rowIndexes.size(); j++) {
				rowIndex = rowIndexes.get(j);
				Mutant mutant = prepareMutant(circuit);
				
				Circuit mutantCircuit = mutant.getQuantumCircuit();
				originalColumn = mutantCircuit.getColumns().get(columnIndex);
				mutatedColumn = new QColumn(originalColumn);
				
				Gate oldGate = originalColumn.getGates().get(rowIndex); 
				
				if (oldGate.getClass()==GateSwap.class && swapMutated)
					continue;
				
				newGate = oldGate.copy(mutatedColumn);
				
				mutantCircuit.addColumn(columnIndex, mutatedColumn);
				mutantCircuit.setGate(columnIndex+1, newGate);
				
				if (oldGate.getClass()==GateSwap.class) {
					for (int k=0; k<circuit.getQubits(); k++) {
						Gate otherSwap = originalColumn.getGates().get(k);
						if (otherSwap.getClass()==GateSwap.class && j!=k) {
							Gate newSwap = otherSwap.copy(mutatedColumn);
							mutantCircuit.setGate(columnIndex+1, newSwap);
							swapMutated = true;
						}
					}
					
				}
				
				this.saveMutant(contador, mutant, callableGateIndex, columnIndex, rowIndex);
			}
		}
	}

	protected boolean isMutable(Gate gate) {
		return (!(gate instanceof ControlGate) && !gate.isNumeric());
	}
}
