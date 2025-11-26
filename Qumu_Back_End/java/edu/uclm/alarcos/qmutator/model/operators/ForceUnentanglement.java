package edu.uclm.alarcos.qmutator.model.operators;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.Mutant;

public class ForceUnentanglement extends EntanglementOperator {
	
	@Override
	public void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception {
		Iterator<Integer> keys = circuit.getMutablePositions().keySet().iterator();
		while (keys.hasNext()) {
			Integer columnIndex = keys.next();
			ArrayList<Integer> rowIndexes = circuit.getMutablePositions().get(columnIndex);
			int posH = rowIndexes.get(0);
			
			Mutant mutant = prepareMutant(circuit);
			Circuit mutantCircuit = mutant.getQuantumCircuit();
			mutantCircuit.removeGate(columnIndex, posH);
			
			this.saveMutant(contador, mutant, callableGateIndex, columnIndex, posH);
		}
	}

	@Override
	public String getDescription() {
		return "Removes the H gate in an entanglement";
	}
	
	@Override
	public String getFamily() {
		return "Entanglement";
	}
}
