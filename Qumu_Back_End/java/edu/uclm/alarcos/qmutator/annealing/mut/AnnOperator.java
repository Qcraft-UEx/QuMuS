package edu.uclm.alarcos.qmutator.annealing.mut;

import java.util.List;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Problem;

public abstract class AnnOperator {

	protected abstract List<String> generateMutants(CH f, Problem problem);

	public String getName() {
		return this.getClass().getSimpleName();
	}
}
