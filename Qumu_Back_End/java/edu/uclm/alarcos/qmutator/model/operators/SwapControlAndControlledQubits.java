package edu.uclm.alarcos.qmutator.model.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.AControlGate;
import edu.uclm.alarcos.qmutator.model.gates.Gate;

public class SwapControlAndControlledQubits extends Operator {
	
	@Override
	public void checkMutablePositions(Circuit circuit) {		
		QColumn column;
		AControlGate controlGate;
		
		List<QColumn> columns = circuit.getColumns();
		for (int i=0; i<columns.size(); i++) {
			column = columns.get(i);
			if (column.getControlGates().isEmpty())
				continue;
			List<Integer> controlGateIndexes = new ArrayList<>();
			for (int j=0; j<column.getControlGates().size(); j++) {
				controlGate = column.getControlGates().get(j);
				controlGateIndexes.add(controlGate.getQubit());
			}
			controlGateIndexes.add(column.getControlledGate().getQubit());
			circuit.addMutablePosition(i, controlGateIndexes);
		}
	}

	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		Integer columnIndex;
		QColumn column;
		ArrayList<Integer> rowIndexes;
		int controlledQubitGate;
		
		Iterator<Integer> columns = circuit.getMutablePositions().keySet().iterator();
		while (columns.hasNext()) {
			columnIndex = columns.next();
			rowIndexes = circuit.getMutablePositions().get(columnIndex);
			controlledQubitGate = rowIndexes.get(rowIndexes.size()-1);

			for (int i=0; i<rowIndexes.size()-1; i++) {
				int controlQubit = rowIndexes.get(i);
				Mutant mutant = prepareMutant(circuit);
				
				Circuit mutantCircuit = mutant.getQuantumCircuit();
				column = mutantCircuit.getColumn(columnIndex);
				this.swap(column, controlQubit, controlledQubitGate);
				this.saveMutant(contador, mutant, callableGateIndex, columnIndex, i);
			}
		}
	}
	
	private void swap(QColumn column, int controlQubit, int controlledQubitGate) {
		AControlGate controlGate = (AControlGate) column.getGates().get(controlQubit);
		Gate controlledGate = column.getGates().get(controlledQubitGate);
		
		column.getGates().set(controlQubit, controlledGate);
		column.getGates().set(controlledQubitGate, controlGate);
		controlGate.setQubit(controlledQubitGate);
		controlledGate.setQubit(controlQubit);
	}

	//@Override
	public void performChange(Mutant mutant, int columnIndex, int controlIndex, int otherIndex) {
		Circuit circuit = mutant.getQuantumCircuit();
		circuit.swapGates(columnIndex, controlIndex, otherIndex);
	}
	
	@Override
	public String getDescription() {
		return "In a control gate, swaps the positions of the control and one of the controlled gates";
	}

	@Override
	public String getFamily() {
		return "Bridging faults";
	}
}
