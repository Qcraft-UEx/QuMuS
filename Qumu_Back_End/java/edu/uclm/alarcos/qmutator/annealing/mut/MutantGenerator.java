package edu.uclm.alarcos.qmutator.annealing.mut;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.reflections.Reflections;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Problem;
import edu.uclm.alarcos.qmutator.model.Manager;

public class MutantGenerator {

	private static List<AnnOperator> operators;

	public List<String> generateMutants(CH f, String problemId, JSONArray jsaOperators) {
		Problem problem = Manager.get().getProblemRepo().findById(problemId).get();
		Manager.get().getAnnMutantRepo().deleteByProblemId(problemId);
		List<String> ids = new ArrayList<>();
		for (int i=0; i<jsaOperators.length(); i++) {
			try {
				AnnOperator operator = (AnnOperator) Class.forName("edu.uclm.alarcos.qmutator.annealing.mut." + jsaOperators.getString(i)).newInstance();
				ids.addAll(operator.generateMutants(f, problem));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ids;
	}

	public static List<AnnOperator> loadOperators() {
		Reflections reflections = new Reflections("edu.uclm.alarcos.qmutator.annealing.mut");
		Iterator<Class<? extends AnnOperator>> operatorClasses = reflections.getSubTypesOf(AnnOperator.class).iterator();

		operators = new ArrayList<>();
		while (operatorClasses.hasNext()) {
			try {
				Class<? extends AnnOperator> operatorClazz = operatorClasses.next();
				if (!Modifier.isAbstract(operatorClazz.getModifiers()) && !operatorClazz.isInterface() && 
						AnnOperator.class.isAssignableFrom(operatorClazz)) {
					AnnOperator operator = operatorClazz.newInstance();

					operators.add(operator);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return operators;
	}
}
