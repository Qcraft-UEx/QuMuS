package edu.uclm.alarcos.qmutator.model.operators;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.model.Circuit;

public class WrongEntanglementInitialization extends EntanglementOperator {
	
	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		Iterator<Integer> keys = circuit.getMutablePositions().keySet().iterator();
		
		while (keys.hasNext()) {
			Integer columnIndex = keys.next();
			ArrayList<Integer> rowIndexes = circuit.getMutablePositions().get(columnIndex);
			int posControl = rowIndexes.get(0);
			int posX = rowIndexes.get(1);
			
			Mutant mutant = prepareMutant(circuit);
			Circuit mutantCircuit = mutant.getQuantumCircuit();
			mutantCircuit.moveGate(columnIndex, posControl, posX);
			this.saveMutant(contador, mutant, callableGateIndex, columnIndex, posControl);
		}
	}

	@Override
	public String getDescription() {
		return "Moves the H gate to the row of the X gate";
	}
	
	@Override
	public String getFamily() {
		return "Entanglement";
	}
}
