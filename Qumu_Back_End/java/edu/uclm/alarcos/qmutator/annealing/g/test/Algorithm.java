/**
 * This class is the root of the algorithm's hierarchy. An Algorithm represents a combination strategy.
 * The algorithm contains a vector of @see Set elements, a list of @see PairsTable and a collection of integers, which represents the selected positions.
 * When the algorithm is executed from a web application, a JspWriter out object can be assigned to the algorithm. If it exists, each algorithm
 * will show the different steps of its execution.
 *  @author Macario Polo Usaola
 */

package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Algorithm {
	protected List<TestParameter> parameters;
	protected Map<String, PairsTable> pairsTables;

	public Algorithm() {
		this.parameters=new ArrayList<>();
	}
	
	public void setParameters(List<TestParameter> parameters) {
		this.parameters=parameters;
	}
	
	protected TestCombination getSizedCombination(int size) {
		return new TestCombination(size);
	}
	
	public abstract List<TestCombination> buildCombinations();

	public abstract String getName();

	public List<TestParameter> getParameters() {
		return parameters;
	}

	protected int getPairsVisited(int parameterIndex, int valueIndex) {
		int result=0;
		Collection<PairsTable> pptt = this.pairsTables.values();
		
		for (PairsTable pairsTable : pptt) {
			if (pairsTable.getIndexParameterA()==parameterIndex || pairsTable.getIndexParameterB()==parameterIndex) {
				List<Pair> pairs = pairsTable.getPairs();
				for (Pair pair : pairs) {
					if (pair.getVisits()==0) {
						if ((pairsTable.getIndexParameterA()==parameterIndex && pair.getIndexValueA()==valueIndex) || (pairsTable.getIndexParameterB()==parameterIndex && pair.getIndexValueB()==valueIndex))
							result++;
					}
				}
			}
		}
		return result;
	}

	protected PairsTable findPairsTable(int indexParameterA, int indexParameterB) {
		return this.pairsTables.get(indexParameterA + "." + indexParameterB);
	}

	protected Map<String, PairsTable> buildPairTables() {
		Map<String, PairsTable> result=new HashMap<>();
		PairsTable pairsTable;
		for (int i=0; i<this.parameters.size(); i++) {
			for (int j=i+1; j<this.parameters.size(); j++) {
				pairsTable=new PairsTable(i, j, this.parameters);
				result.put(i + "." + j, pairsTable);
			}
		}
		return result;
	}
	
	public abstract String getCredits();
	
	public abstract boolean requiresRegister();

	public static Algorithm getAlgorithm(String algorithmName) {
		if (algorithmName.equals("All combinations"))
			return new AllCombinationsAlgorithm();
		if (algorithmName.equals("Each choice"))
			return new EachChoiceAlgorithm();
		if (algorithmName.equals("AETG"))
			return new AETGAlgorithm();
		return new RandomAlgorithm();
	}
}
