package edu.uclm.alarcos.qmutator.model.operators;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.ControlGate;
import edu.uclm.alarcos.qmutator.model.gates.Gate;
import edu.uclm.alarcos.qmutator.model.gates.primitive.GateNoOperation;

public class OneQubitGateRemoval extends Operator implements IMissingGate {
	
	@Override
	public void checkMutablePositions(Circuit circuit) {
		Gate gate;
		for (int i=0; i<circuit.getColumns().size(); i++) {
			QColumn column = circuit.getColumns().get(i);
			if (column.getControlGates().size()==1) {
				for (int j=0; j<column.getQubits(); j++) {
					gate = column.getGates().get(j);
					if (gate instanceof ControlGate)
						circuit.addMutablePosition(i, gate.getQubit());
				}
			}
		}
	}
	
	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		int columnIndex, rowIndex;
		QColumn column;
		
		Iterator<Integer> columnIndexes = circuit.getMutablePositions().keySet().iterator();
		while (columnIndexes.hasNext()) {
			columnIndex = columnIndexes.next();
			ArrayList<Integer> rowIndexes = circuit.getMutablePositions().get(columnIndex);
			for (int i=0; i<rowIndexes.size(); i++) {
				rowIndex = rowIndexes.get(i);
				Mutant mutant = prepareMutant(circuit);
				Circuit mutantCircuit = mutant.getQuantumCircuit();
				column = mutantCircuit.getColumn(columnIndex);
				boolean columnIsRemovable = this.removeGate(column, rowIndex);
				if (columnIsRemovable)
					mutantCircuit.removeColumn(columnIndex);
				this.saveMutant(contador, mutant, callableGateIndex, columnIndex, rowIndex);
			}
		}
	}
	
	private boolean removeGate(QColumn column, int rowIndex) {
		GateNoOperation gno = new GateNoOperation();
		gno.setColumn(column);
		gno.setQubit(rowIndex);
		column.getGates().set(rowIndex, gno);
		return column.recalculateControlGates(null);
	}
	
	@Override
	public String getDescription() {
		return "Removes a column with exactly 1 control gate";
	}
}
