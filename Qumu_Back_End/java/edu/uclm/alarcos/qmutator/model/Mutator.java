package edu.uclm.alarcos.qmutator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uclm.alarcos.qmutator.dao.MutantRepository;
import edu.uclm.alarcos.qmutator.model.gates.CallableGate;
import edu.uclm.alarcos.qmutator.model.operators.Operator;

public class Mutator {
	
	private Circuit originalCircuit;
	private List<Operator> selectedOperators = new ArrayList<>();
	
	private MutantRepository mutantRepo;
	
	public Mutator(Circuit quantumCircuit, List<String> operatorNames, MutantRepository mutantRepo) throws Exception {
		this.originalCircuit = quantumCircuit;
		
		for (String operatorName : operatorNames) 
			selectedOperators.add(Manager.get().findOperator(operatorName));
		
		this.mutantRepo = mutantRepo;
	}
	
	public void mutate() throws Exception {
		Manager.get().getMutantRepo().deleteByOriginalCircuitId(this.originalCircuit.getId());
		
		int[] contador = { 1 };
		
		this.mutate(this.originalCircuit, contador);
	}
	
	private void mutate(Circuit circuit, int[] contador) {
		for (Operator operator : this.selectedOperators) {
			operator.setMutantRepo(this.mutantRepo);
			circuit.clearMutablePositions();
			operator.checkMutablePositionsAndSubcircuits(circuit);
			Map<Integer, ArrayList<Integer>> mutablePositions = circuit.getMutablePositions();
			if (!mutablePositions.isEmpty()) {
				try {
					operator.apply(contador, circuit, -1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			List<CallableGate> callableGates = circuit.getCallableGates();
			CallableGate gate;
			for (int index=0; index<callableGates.size(); index++) {
				gate = callableGates.get(index);
				if (!gate.getCircuit().getMutablePositions().isEmpty()) {
					try {
						Circuit gateCircuit = gate.getCircuit();
						operator.apply(contador, gateCircuit, index);						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
