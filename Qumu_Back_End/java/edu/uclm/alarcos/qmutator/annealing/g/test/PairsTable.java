package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;
import java.util.List;

public class PairsTable {
	private int indexParameterA;
	private int indexParameterB;
	private List<TestParameter> parameters;
	private List<Pair> pairs;

	public PairsTable(int indexParameterA, int indexParameterB, List<TestParameter> parameters) {
		this.indexParameterA = indexParameterA;
		this.indexParameterB = indexParameterB;
		this.pairs=new ArrayList<>();
		this.parameters=parameters;
		TestParameter parameterA = this.parameters.get(indexParameterA);
		TestParameter parameterB = this.parameters.get(indexParameterB);
		for (int i=0; i<parameterA.size(); i++) {
			for (int j=0; j<parameterB.size(); j++) {
				Pair pair=new Pair(this, i, j);
				this.pairs.add(pair);
			}
		}
	}

	public List<Pair> getPairs() {
		return pairs;
	}

	public Pair getPairWithWeight(int weight) {
		for (Pair pair : this.pairs) {
			if (pair.getVisits()==weight)
				return pair;
		}
		return null;
	}

	public int weightOfThePairs(TestCombination combination) {
		/*for (int i=0; i<pairs.size(); i++) {
			Pair pair=pairs.get(i);
			if (pair.getParameterAName().equals(combination.getValue(this.indexA)) &&
					pair.getParameterBName().equals(combination.getValue(this.indexB)))
				return pair.getWeight();
		}*/
		return 0;
	}

	public int getNumberOfPairsVisited(TestCombination combination) {
		/*for (int i=0; i<pairs.size(); i++) {
			Pair auxi=pairs.get(i);
			if (auxi.getParameterAName().equals(combination.getValue(this.indexA)) &&
					auxi.getParameterBName().equals(combination.getValue(this.indexB)))
				return 1;
		}*/
		return 0;	
	}
	

	public Pair getPairVisited(TestCombination combination) {
		/*for (int i=0; i<pairs.size(); i++) {
			Pair auxi=pairs.get(i);
			if (auxi.getParameterAName().equals(combination.getValue(this.indexA)) &&
					auxi.getParameterBName().equals(combination.getValue(this.indexB)))
				return auxi;
		}*/
		return null;	
	}

	public void visit(int indexValueA, int indexValueB) {
		for (int i=0; i<this.pairs.size(); i++) {
			Pair pair=this.pairs.get(i);
			if (pair.getIndexValueA()==indexValueA && pair.getIndexValueB()==indexValueB) {
				pair.visit();
				return;
			}
		}
	}

	public void removePair(String a, String b) {
		for (Pair pair : this.pairs) {
			if (pair.getParameterAName().equals(a) && pair.getParameterBName().equals(b)) {
				this.pairs.remove(pair);
				return;
			}
		}
	}
	
	public Pair getPair(int indexA, int indexB) {
		for (Pair pair : this.pairs)
			if (pair.getIndexValueA()==indexA && pair.getIndexValueB()==indexB)
				return pair;
		return null;
	}

	public Pair getPair(String valueA, String valueB) {
		for (Pair pair : this.pairs)
			if (pair.getValueA().equals(valueA) && pair.getValueB().equals(valueB))
				return pair;
		return null;
	}
	
	public TestParameter getParameterA() {
		return this.parameters.get(this.indexParameterA);
	}
	
	public TestParameter getParameterB() {
		return this.parameters.get(this.indexParameterB);
	}
	
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("(" + this.indexParameterA + ", " + this.indexParameterB + "): " + this.getParameterA().getName() + " x " + this.getParameterB().getName() + "\n"); 
		for (Pair pair : pairs)
			sb.append("\t" + pair.toString() + "\n");
		return sb.toString();
	}

	public int getIndexParameterA() {
		return this.indexParameterA;
	}
	
	public int getIndexParameterB() {
		return indexParameterB;
	}
}
