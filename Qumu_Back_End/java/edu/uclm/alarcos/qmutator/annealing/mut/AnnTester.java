package edu.uclm.alarcos.qmutator.annealing.mut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.annealing.ProblemSolution;
import edu.uclm.alarcos.qmutator.model.Manager;

public class AnnTester {

	private Problem problem;
	private Combination combination;
	private ProblemSolution problemSolution;
	
	public AnnTester() {
	}

	public AnnTester(Problem problem, Combination combination) {
		this.problem = problem;
		this.combination = combination;
		this.problemSolution = Manager.get().getProblemSolutionRepo().findByProblemId(problem.getId()).get(0);
	}
	
	public List<Combination> executeMutant(String problemId, String mutantId) throws Exception {
		AnnMutant mutant = Manager.get().getAnnMutantRepo().findById(mutantId).get();
		CH cf = new CH(new JSONObject(mutant.getConcreteFunction()));
		return cf.calculateAll(problemId, mutantId, true, true);
	}
	
	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	public Map<String, Double[]> test() throws Exception {
		double mutationScore = 0.0;
		List<AnnMutant> mutants = Manager.get().getAnnMutantRepo().findByProblemId(problem.getId());
		
		double killedMutants = 0.0;
		double totalMutants = mutants.size();
		
		Map<String, Double[]> results = new HashMap<>();
		for (AnnMutant mutant : mutants) {
			Double[] mutantResult = results.get(mutant.getOperator());
			if (mutantResult==null) {
				mutantResult = new Double[] { 0.0, 0.0, 0.0 };
				results.put(mutant.getOperator(), mutantResult);
			}
			mutantResult[0]++;

			double energy = mutant.evaluate(this.combination);
			if (energy!=problemSolution.getEnergy()) {
				killedMutants = killedMutants + 1;
				mutantResult[1]++;
				mutantResult[2]=mutantResult[1]/mutantResult[0];
				mutant.setKilled(true);
				mutant.setEnergy(energy);
				Manager.get().getAnnMutantRepo().save(mutant);
			}
		}
		mutationScore = killedMutants / totalMutants;
		results.put("General", new Double[] { totalMutants, killedMutants, mutationScore });
		return results;
	}

}
