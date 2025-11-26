package edu.uclm.alarcos.qmutator.annealing.mut;

import java.util.ArrayList;
import java.util.List;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.DoubleValue;
import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;
import edu.uclm.alarcos.qmutator.model.Manager;

public class PlusToMinus extends AnnOperator {

	@Override
	protected List<String> generateMutants(CH f, Problem problem) {
		ArrayList<ResultExpr> expressions = f.getExpressions();
		ResultExpr expr;
		List<String> ids = new ArrayList<>();
		for (int i=0; i<expressions.size(); i++) {
			expr = expressions.get(i);
			
			List<ResultExpr> children = expr.getChildren();
			if (children==null)
				continue;
			
			for (ResultExpr child : children)
				this.generateMutants(f, ids, child, problem);
		}
		return ids;
	}

	private void generateMutants(CH f, List<String> ids, ResultExpr expr, Problem problem) {
		if (expr instanceof DoubleValue) {
			DoubleValue value = (DoubleValue) expr;
			if (value.getValue()==0)
				return;

			AnnMutant mutant = new AnnMutant();
			value.setValue(-value.getValue());
			mutant.setConcreteFunction(f);
			mutant.setProblem(problem);
			mutant.setDescription("Expression: " + expr.toString());			
			mutant.setOperator(this.getClass().getSimpleName());
			Manager.get().getAnnMutantRepo().save(mutant);
			
			value.setValue(-value.getValue());
			ids.add(mutant.getId());
			return;
		}
		List<ResultExpr> children = expr.getChildren();
		if (children==null)
			return;
		
		for (ResultExpr child : children)
			this.generateMutants(f, ids, child, problem);
	}

}
