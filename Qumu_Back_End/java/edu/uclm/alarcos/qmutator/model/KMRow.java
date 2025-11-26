package edu.uclm.alarcos.qmutator.model;

public class KMRow {

	private int index;
	private boolean killed;
	private boolean injured;
	private String operator;

	public void setMutant(Mutant mutant) {
		this.index = mutant.getMutantIndex();
		this.killed = mutant.isKilled();
		this.injured = mutant.isInjured();
		this.operator = mutant.getOperatorName();
	}
	
	public int getIndex() {
		return index;
	}

	public String getMutant() {
		return "m" + index;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public boolean isKilled() {
		return killed;
	}
	
	public boolean isInjured() {
		return injured;
	}
}
