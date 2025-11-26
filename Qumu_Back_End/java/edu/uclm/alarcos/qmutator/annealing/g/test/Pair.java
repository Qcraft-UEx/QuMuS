package edu.uclm.alarcos.qmutator.annealing.g.test;

public class Pair {
	private PairsTable pairsTable;
	private int indexValueA;
	private int indexValueB;
	private int visits;
	
	public Pair(PairsTable pairsTable, int indexA, int indexB) {
		this.pairsTable = pairsTable;
		this.indexValueA = indexA;
		this.indexValueB = indexB;
		this.visits = 0;
	}

	public String toString() {
		return "(" + pairsTable.getParameterA().getValue(indexValueA) + ", " + pairsTable.getParameterB().getValue(indexValueB) + ") -> " + this.visits;
	}

	public int getVisits() {
		return this.visits;
	}

	public String getParameterAName() {
		return this.pairsTable.getParameterA().getName();
	}

	public String getParameterBName() {
		return this.pairsTable.getParameterB().getName();
	}

	public void visit() {
		this.visits=visits+1;
	}
	
	public int getIndexValueA() {
		return indexValueA;
	}
	
	public int getIndexValueB() {
		return indexValueB;
	}
	
	public Double getValueA() {
		return pairsTable.getParameterA().getValue(indexValueA);
	}
	
	public Double getValueB() {
		return pairsTable.getParameterB().getValue(indexValueB);
	}
}
