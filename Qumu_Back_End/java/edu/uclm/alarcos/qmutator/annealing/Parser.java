package edu.uclm.alarcos.qmutator.annealing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.model.Manager;

public class Parser {
	private String minFunction;
	private JSONArray constraints;
	
	public static void main(String[] args) throws Exception {
		String file = "/Users/macariopolousaola/Downloads/sudoku4x4.txt";
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String name = br.readLine();
			br.readLine();
			
			String min = br.readLine();
			br.readLine();
			
			String line;
			Problem problem = new Problem();
			problem.setName(name);
			problem.setMinFunction(min);
			while ((line = br.readLine()).trim().length() > 0) {
				ProblemConstraint pc = new ProblemConstraint(new JSONObject().put("constraintText", line).put("lambda", 1));
				pc.setProblem(problem);
				problem.add(pc);
			}
			Manager.get().getProblemRepo().save(problem);
		    System.out.println("Hecho");
		}
	}
	
	public Parser(String minFunction, JSONArray constraints) {
		this.minFunction = minFunction;
		this.constraints = constraints;
	}

	public CH parse() throws Exception {
		CH f = new CH();
		HashMap<String, Integer> cardinalidades = new HashMap<>();
		if (minFunction.length()>0)
			parseMinFunction(f, cardinalidades);
		parseConstraints(f);
		f.prepare();
		return f;
	}

	private void parseConstraints(CH f) throws JSONException, Exception {
		for (int i=0; i<constraints.length(); i++) {
			parseConstraint(f, constraints.getJSONObject(i));
		}
	}

	private void parseConstraint(CH f, JSONObject constraint) throws Exception {
		String constraintText = constraint.getString("constraintText");
		int posEqual = constraintText.indexOf('=');
		if (posEqual==-1)
			throw new Exception("The constraint " + constraintText + " should contain an =");
		String sLeft = constraintText.substring(0, posEqual).trim();
		String sRight = constraintText.substring(posEqual+1).trim();
		
		String[] sumandos = sLeft.split("[+]");
		String sumando;
		if (sumandos.length==1) {
			sumando = sumandos[0];
			if (numBarras(sumando)==1)
				setValueToVariable(f, sRight, sumando);
			else {
				Product left = createProduct(f, sumando);
				ResultExpr right = buildExpression(f, sRight);
				Sum sum = new Sum(f, left, right);
				Square square = new Square(f);
				square.setExpr(sum);
				Lambda lambda = new Lambda(f, constraint.getDouble("lambda"));
				Product product = new Product(f, square, lambda);
				f.add(product);
			}
		} else {
			ResultExpr left = buildExpression(f, sumandos);
			ResultExpr right = new DoubleValue(f, -Double.parseDouble(sRight));
			Sum sum = new Sum(f, left, right);
			Square square = new Square(f);
			square.setExpr(sum);
			Lambda lambda = new Lambda(f, constraint.getDouble("lambda"));
			Product product = new Product(f, square, lambda);
			f.add(product);
		}
	}
	
	private Product createProduct(CH f, String sumando) throws Exception {
		String[] lr = sumando.split("[*]");
		String sLeft = lr[0];
		String sRight = lr[1];
		ResultExpr left = buildExpression(f, sLeft);
		ResultExpr right = buildExpression(f, sRight);
		Product product = new Product(f, left, right);
		return product;
	}

	private int numBarras(String sumando) {
		int r = 0;
		for (int i=0; i<sumando.length(); i++)
			if (sumando.charAt(i)=='_')
				r++;
		return r;
	}

	private ResultExpr buildExpression(CH f, String[] sumandos) throws Exception {
		String sumando;
		List<ResultExpr> sums = new ArrayList<>();
		for (int i=0; i<sumandos.length; i++) {
			sumando = sumandos[i];
			String[] terms = sumando.split("[*]");
			if (terms.length==1) {
				ResultExpr left = buildExpression(f, terms[0]);
				sums.add(left);
			} else {
				String sLeft = terms[0];
				String sRight = terms[1];
				ResultExpr left = buildExpression(f, sLeft);
				ResultExpr right = buildExpression(f, sRight);
				sums.add(new Product(f, left, right));
			}
		}
		return new Sum(f, sums);
	}

	private ResultExpr buildExpression(CH f, String term) throws Exception {
		ResultExpr expr=null;
		int posBarra = term.indexOf('_');
		if (posBarra!=-1) {
			String variableName = term.substring(0, posBarra);
			String sIndex = term.substring(posBarra+1);
			int index = Integer.parseInt(sIndex);
			if (variableName.equalsIgnoreCase("x")) {
				expr = new UseOfX(f, term);
			} else {
				expr = new IndexedVariableValue(f, variableName, index);
			}
		} else {
			try {
				double value = Double.parseDouble(term);
				expr = new DoubleValue(f, value);
			} catch (Exception e) {
				throw new Exception("The term " + term + " in the min function should be a numeric value");
			}
		}
		return expr;
	}
	

	private void setValueToVariable(CH f, String sRight, String sumando) throws Exception {
		int posBarra = sumando.indexOf('_');
		String variableName = sumando.substring(0, posBarra);
		String sIndex = sumando.substring(posBarra+1);
		int index = Integer.parseInt(sIndex);
		if (variableName.equals("x")) {
			f.getX().setValue(Integer.parseInt(sRight), index);
		} else {
			InitializedVariable variable = f.getInitializedVariable(variableName);
			if (variable==null)
				throw new Exception(variableName + " variable not found");
			//variable.setValue(index, Double.parseDouble(sRight));
		}
	}

	private void parseMinFunction(CH f, HashMap<String, Integer> cardinalidades) throws Exception {
		String[] sumandos = minFunction.split("[+]");
		if (sumandos.length<=1)
			throw new Exception("The min function must contain addends (sumandos)");
		
		Sum minFunction = new Sum(f);
		for (int i=0; i<sumandos.length; i++)
			minFunction.add(createMinFunction(f, sumandos[i], cardinalidades));
		minFunction.setFunction(f);
		f.add(minFunction);
		
		Set<String> variables = cardinalidades.keySet();
		for (String variable : variables) {
			Integer cardinalidad = cardinalidades.get(variable);
			if (variable.equals("x"))
				f.setX("x", cardinalidad+1);
			else {
				InitializedVariable iv = f.findVariable(variable);
				if (iv==null) {
					iv = new InitializedVariable(variable);
					iv.setCardinals(cardinalidad);
					f.addInitializedVariable(iv);
				} else 
					iv.setCardinals(cardinalidad+1);
			}
		}
	}

	private ResultExpr createMinFunction(CH f, String sumando, HashMap<String, Integer> cardinalidades) throws Exception {
		String[] terms = sumando.split("[*]");
		if (terms.length==1) 
			return buildExpressionAndVariable(f, cardinalidades, terms[0]);
		ResultExpr sum = buildExpressionAndVariable(f, cardinalidades, terms);
		return sum;
	}

	private ResultExpr buildExpressionAndVariable(CH f, HashMap<String, Integer> cardinalidades, String[] terms) throws Exception {
		Product product = new Product(f);
		ResultExpr expr;
		for (int i=0; i<terms.length; i++) {
			expr = buildExpressionAndVariable(f, cardinalidades, terms[i]);
			product.add(expr);
		}
		return product;
	}
	
	private ResultExpr buildExpressionAndVariable(CH f, HashMap<String, Integer> cardinalidades, String term) throws Exception {
		ResultExpr expr=null;
		int posBarra = term.indexOf('_');
		if (posBarra!=-1) {
			String variableName = term.substring(0, posBarra);
			String sIndex = term.substring(posBarra+1);
			int index = Integer.parseInt(sIndex);
			Integer cardinalidad = cardinalidades.get(variableName);
			if (cardinalidad==null) {
				cardinalidad = new Integer(0);
				cardinalidades.put(variableName, cardinalidad);
			}
			cardinalidad = index>cardinalidad ? index : cardinalidad;
			cardinalidades.put(variableName, cardinalidad);
			if (variableName.equalsIgnoreCase("x")) {
				expr = new UseOfX(f, term);
			} else {
				expr = new IndexedVariableValue(f, term);
			}
		} else {
			try {
				double value = Double.parseDouble(term);
				expr = new DoubleValue(f, value);
			} catch (Exception e) {
				throw new Exception("The term " + term + " in the min function should be a numeric value");
			}
		}
		return expr;
	}

}
