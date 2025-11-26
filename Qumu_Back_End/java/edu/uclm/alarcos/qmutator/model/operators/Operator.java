package edu.uclm.alarcos.qmutator.model.operators;

import java.util.List;

import edu.uclm.alarcos.qmutator.model.Mutant;
import edu.uclm.alarcos.qmutator.dao.MutantRepository;
import edu.uclm.alarcos.qmutator.model.Circuit;
import edu.uclm.alarcos.qmutator.model.QColumn;
import edu.uclm.alarcos.qmutator.model.gates.CallableGate;
import edu.uclm.alarcos.qmutator.model.gates.Gate;

public abstract class Operator implements IOperator {
	
	private MutantRepository mutantRepo;

	protected Mutant prepareMutant(Circuit circuit) throws Exception {
		Mutant mutant = new Mutant();
		mutant.setOperatorName(this.getPrintedName());
		mutant.setQuantumCircuit(circuit);
		return mutant;
	}
	
	public String getName() {
		return this.getClass().getSimpleName();		
	}

	public String getPrintedName() {
		String name = this.getClass().getSimpleName();
		String r = "";
		for (int i=0; i<name.length(); i++) {
			if (i>0 && Character.isUpperCase(name.charAt(i)))
				r = r + " ";
			r = r + name.charAt(i);
		}
		return r;
	}
	
	protected int indexOf(QColumn column, String gateName) {
		Gate gate;
		for (int i=0; i<column.getGates().size();i++) {
			gate = column.getGates().get(i);
			if (gate.getName().equals(gateName))
					return i;
		}
		return -1;
	}
	
	public int indexOf(QColumn column, Class<? extends Gate> clazz) {
		Gate gate;
		for (int i=0; i<column.getGates().size();i++) {
			gate = column.getGates().get(i);
			if  (gate.getClass().isAssignableFrom(clazz))
				return i;
		}
		return -1;
	}
	
	public abstract String getDescription();
	
	public void checkMutablePositionsAndSubcircuits(Circuit circuit) {
		this.checkMutablePositions(circuit);
		
		List<CallableGate> callableGates = circuit.getCallableGates();
		CallableGate callableGate;
		Circuit subcircuit;
		for (int i=0; i<callableGates.size(); i++) {
			callableGate = callableGates.get(i);
			subcircuit = callableGate.getCircuit();
			subcircuit.clearMutablePositions();
			this.checkMutablePositions(subcircuit);

			if (!subcircuit.getMutablePositions().isEmpty()) {
				subcircuit.setContainerCircuit(circuit);
			}
		}		
	}

	protected abstract void checkMutablePositions(Circuit circuit);

	public abstract void apply(int[] contador, Circuit circuit, int callableGateIndex) throws Exception;

	public void checkMutablePositions(Circuit subcircuit, Circuit circuit) {
		this.checkMutablePositions(subcircuit);
	}
		
	protected void saveMutant(int[] contador, Mutant mutant, int callableGateIndex, int colIndex, int rowIndex) throws Exception {
		mutant.setMutantIndex(contador[0]);
		mutant.setColIndex(colIndex);
		mutant.setRowIndex(rowIndex);
		
		Circuit mutatedQuantumCircuit = mutant.getQuantumCircuit();
		String qiskitCode = mutatedQuantumCircuit.prepareQiskitCode();
		mutant.setQiskitCode(qiskitCode);
		
		String mutatedStringCircuit = mutatedQuantumCircuit.toString();
		mutant.setCircuit(mutatedStringCircuit);
		if (callableGateIndex==-1) {
			try {
				this.mutantRepo.save(mutant);
			}
			catch (Exception e) {
				System.err.println(e);
			}
		} else {
			Circuit parent = mutant.getQuantumCircuit().getContainerCircuit();
			CallableGate oldGate = parent.getCallableGates().get(callableGateIndex);
			CallableGate newGate = (CallableGate) oldGate.copy(null);
			parent.replaceGate(rowIndex, newGate);
			mutant.setCircuit(parent.getCircuit());
			mutant.setQuantumCircuit(parent);
			this.mutantRepo.save(mutant);
			parent.replaceGate(rowIndex, oldGate);
		}
		contador[0]++;
	}

	public void setMutantRepo(MutantRepository mutantRepo) {
		this.mutantRepo = mutantRepo;
	}
}
