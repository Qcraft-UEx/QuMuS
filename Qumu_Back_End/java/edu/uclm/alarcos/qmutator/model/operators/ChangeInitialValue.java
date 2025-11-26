package edu.uclm.alarcos.qmutator.model.operators;

import edu.uclm.alarcos.qmutator.model.InitColumn;
import edu.uclm.alarcos.qmutator.model.InitialValue;
import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.Circuit;

public class ChangeInitialValue extends Operator implements IInitializationError {
	
	@Override
	public void checkMutablePositions(Circuit circuit) {
		int rows = circuit.getQubits();
		for (int i=0; i<rows; i++)
			circuit.addMutablePosition(-1, i);
	}
	
	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		InitColumn initColumn = circuit.getInitColumn();
		int rows = circuit.getQubits();
		
		if (initColumn==null || initColumn.size()==0) {
			for (int i=0; i<rows; i++) {
				initColumn = new InitColumn();
				for (int j=0; j<i; j++)
					initColumn.add(InitialValue.ZERO);
				initColumn.add(InitialValue.ONE);
				
				Mutant mutant = prepareMutant(circuit);
				mutant.getQuantumCircuit().setInitColumn(initColumn);
				this.saveMutant(contador, mutant, callableGateIndex, -1, i);			
			}
		} else {
			for (int i=0; i<rows; i++) {
				Mutant mutant = prepareMutant(circuit);
				initColumn = mutant.getQuantumCircuit().getInitColumn();
				InitialValue oldValue = initColumn.getValue(i);
				initColumn.setValue(i, not(oldValue));
				this.saveMutant(contador, mutant, callableGateIndex, -1, i);
			}
		}
	}
	
	private InitialValue not(InitialValue value) {
		if (value==InitialValue.ZERO)
			return InitialValue.ONE;
		return InitialValue.ZERO;
	}

	@Override
	public String getDescription() {
		return "Modifies the initial values of the qubits";
	}
}
