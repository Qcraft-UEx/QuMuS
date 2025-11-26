package edu.uclm.alarcos.qmutator.annealing.mut;

import java.util.ArrayList;
import java.util.List;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Lambda;
import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;
import edu.uclm.alarcos.qmutator.model.Manager;

public class LambdaBetween10 extends AnnOperator {

	@Override
	protected List<String> generateMutants(CH f, Problem problem) {
		ArrayList<ResultExpr> expressions = f.getExpressions();
		ResultExpr expr;
		List<String> ids = new ArrayList<>();
		for (int i=1; i<expressions.size(); i++) {
			expr = expressions.get(i);
			
			Lambda lambda = expr.getLambda();
			if (lambda==null)
				continue;
			
			double lambdaValue = lambda.getValue();
			lambda.setValue(lambdaValue/10);
			AnnMutant mutant = new AnnMutant();
			
			mutant.setConcreteFunction(f);
			mutant.setProblem(problem);
			mutant.setOperator(this.getClass().getSimpleName());
			mutant.setDescription("Expr. " + i + ": lambda= " + (lambda.getValue()/10)  + " instead of " + lambdaValue + ", expression: " + expr.toString());
			Manager.get().getAnnMutantRepo().save(mutant);
			expr.setLambda(lambdaValue);
			ids.add(mutant.getId());
		}
		return ids;
	}

}
