package edu.uclm.alarcos.qmutator.model.operators;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.AControlGate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.Gate1;

public class MultiQubitGateRemoval extends Operator implements IMissingGate {
	
	@Override
	public void checkMutablePositions(Circuit circuit) {
		QColumn column;
		AControlGate controlGate;
		for (int i=0; i<circuit.getColumns().size(); i++) {
			column = circuit.getColumns().get(i);
			if (column.getControlGates().size()>1) {
				for (int j=0; j<column.getControlGates().size(); j++) {
					controlGate = column.getControlGates().get(j);
					circuit.addMutablePosition(i, controlGate.getQubit());
				}
			}
		}
	}
	
	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		int columnIndex, rowIndex;
		QColumn column;
		ArrayList<Integer> rowIndexes;
		
		Iterator<Integer> columnIndexes = circuit.getMutablePositions().keySet().iterator();
		while (columnIndexes.hasNext()) {
			columnIndex = columnIndexes.next();
			rowIndexes = circuit.getMutablePositions().get(columnIndex);

			for (int i=0; i<rowIndexes.size(); i++) {
				rowIndex = circuit.getMutablePositions().get(columnIndex).get(i);
				Mutant mutant = prepareMutant(circuit);
				Circuit mutantCircuit = mutant.getQuantumCircuit();
				column = mutantCircuit.getColumn(columnIndex);
				this.removeGate(column, rowIndex);
				this.saveMutant(contador, mutant, callableGateIndex, columnIndex, rowIndex);
			}
		}
	}
	
	private void removeGate(QColumn column, int rowIndex) {
		AControlGate controlGate = (AControlGate) column.getGates().get(rowIndex);
		Gate1 gate1 = new Gate1();
		gate1.setColumn(column);
		gate1.setQubit(rowIndex);
		column.getGates().set(rowIndex, gate1);
		column.recalculateControlGates(controlGate);
	}
	
	@Override
	public String getDescription() {
		return "Removes a control in a multicontrol gate";
	}
}
