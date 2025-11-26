package edu.uclm.alarcos.qmutator.dwave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.DoubleValue;
import edu.uclm.alarcos.qmutator.annealing.Lambda;
import edu.uclm.alarcos.qmutator.annealing.Product;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;
import edu.uclm.alarcos.qmutator.annealing.Square;
import edu.uclm.alarcos.qmutator.annealing.Sum;
import edu.uclm.alarcos.qmutator.model.Manager;

public class DWaver {

	public static String getCode(CH f) throws IOException {
		String result = Manager.get().readFileAsString("dwaveTemplate.txt");
		
		ArrayList<ResultExpr> exprs = f.getExpressions();
		ArrayList<String> rules = new ArrayList<>();
		rules.add(getRule(exprs.get(0), 0));
		
		for (int i=1; i<exprs.size(); i++)
			rules.add(getRule(exprs.get(i), i));
		
		result = result.replace("#RULES#", rules.toString());
		return result;
	}

	private static String getRule(ResultExpr expr, int index) {
		StringBuilder sb = new StringBuilder("\n");
		if (index==0)
			sb = new StringBuilder("lambda_Rule" + index + " = 1\n");
		else
			sb = new StringBuilder("lambda_Rule" + index + " = " + expr.getLambda().getValue() + "\n");

		if (expr instanceof Sum) {
			Sum sum = (Sum) expr;
			List<ResultExpr> children = sum.getChildren();
			for (int i=0; i<children.size(); i++) {
				Product product = (Product) children.get(i);
				sb.append("Q[(" + i + ", " + i + ")] += lambda_Rule" + index + "*(" + product.getChildren().get(0).getValue() + ")\n");
			}
		} else if (expr instanceof Product) {
			Product product = (Product) expr;
			Square square = (Square) product.getChildren().get(0);
			Sum rootSquareSum = (Sum) square.getChildren().get(0);
			List<ResultExpr> products = rootSquareSum.getChildren();
			for (int i=0; i<products.size()-1; i++) {
				ResultExpr child1 = products.get(i);
				Product product1 = (Product) child1.getChildren().get(0);
				double value1 = product1.getChildren().get(0).getValue();
				for (int j=0; j<products.size(); j++) {
					ResultExpr child2 = products.get(j);
					Product product2 = (Product) child2.getChildren().get(0);
					double value2 = product2.getChildren().get(0).getValue();
					sb.append("Q[(" + i + ", " + j + ")] += lambdaRule" + index + "*2(" + value1 + "*" + value2 + ")");
				}
			}
			System.out.println(product);
		}
		return sb.toString();
	}

}
