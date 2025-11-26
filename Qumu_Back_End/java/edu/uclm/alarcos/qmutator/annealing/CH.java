package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.g.GVariable;
import edu.uclm.alarcos.qmutator.annealing.mut.AnnMutant;
import edu.uclm.alarcos.qmutator.annealing.so.CHCalculator;
import edu.uclm.alarcos.qmutator.model.Manager;

public class CH {
	private X x;
	private HashMap<String, InitializedVariable> variables;
	private ArrayList<ResultExpr> expressions;
	private List<XValue> currenCombination;
	
	public CH() {
		this.x = new X();
		this.expressions = new ArrayList<>();
		this.variables = new HashMap<>();
	}
	
	public CH(JSONObject jso) throws Exception {
		this();
		JSONObject jsoX = jso.optJSONObject("x");
		if (jsoX==null)
			throw new Exception("No se ha encontrado la variable x");
		
		String name = jsoX.optString("name");
		if (name.length()==0)
			name = "x";
		x.setName(name);
		JSONArray jsaCardinals = jsoX.getJSONArray("cardinals");
		x.setCardinals(jsaCardinals);
		JSONArray jsaxCombinations = jsoX.getJSONArray("values");
		x.setValues(jsaxCombinations);
		
		JSONArray jsaVariables = jso.getJSONArray("variables");
		for (int i=0; i<jsaVariables.length(); i++) {
			JSONObject jsoVariable = jsaVariables.getJSONObject(i);
			name = jsoVariable.getString("name");

			InitializedVariable variable = new InitializedVariable(name);
			
			jsaCardinals = jsoVariable.getJSONArray("cardinals");
			ArrayList<Integer> cardinals = new ArrayList<>();
			for (int j=0; j<jsaCardinals.length(); j++)
				cardinals.add(jsaCardinals.getInt(j));
			variable.setCardinals(cardinals);
			
			this.variables.put(name, variable);
			JSONArray jsaValues = jsoVariable.getJSONArray("values");
			ArrayList<Double> values = new ArrayList<>();
			for (int j=0; j<jsaValues.length(); j++)
				values.add(jsaValues.getDouble(j));

			variable.setValues(values);
		}
		JSONArray jsaExpressions = jso.getJSONArray("expressions");
		for (int i=0; i<jsaExpressions.length(); i++) {
			JSONObject jsoExpr = jsaExpressions.getJSONObject(i);
			ResultExpr expr = ResultExpr.build(this, jsoExpr);
			this.expressions.add(expr);
		}
		prepare();
	}

	public void prepare() {
		this.x.prepare();
	}
	
	public void setX(String name, Integer... xCardinals) {
		this.x.setName(name);
		this.x.setCardinals(xCardinals);
	}
	
	public void setX(String name, ArrayList<Integer> xCardinals) {
		this.x.setName(name);
		this.x.setCardinals(xCardinals);
	}
	
	public int getXCardinal() {
		return this.x.getCardinal();
	}
	
	public int getXCardinal(int index) {
		return this.x.getCardinal(index);
	}
	
	public CH add(ResultExpr... exprs) {
		for (ResultExpr expr : exprs) 
			this.expressions.add(expr);
		return this;
	}
		
	public CH add(List<ResultExpr> exprs) {
		for (ResultExpr expr : exprs) {
			expr.function = this;
			this.expressions.add(expr);
		}
		return this;
	}
	
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject().put("type", this.getClass().getSimpleName());
		jso.put("x", this.x.toJSON());
		
		JSONArray jsaVariables = new JSONArray();
		
		Iterator<String> keys = this.variables.keySet().iterator();
		String key;
		InitializedVariable iv;
		while (keys.hasNext()) {
			key = keys.next();
			iv = this.findVariable(key);
			jsaVariables.put(iv.toJSON());
		}
		jso.put("variables", jsaVariables);
		
		JSONArray jsaExpressions = new JSONArray();
		for (ResultExpr expr : this.expressions)
			jsaExpressions.put(expr.toJSON());
		jso.put("expressions", jsaExpressions);
		return jso;
	}

	public void setVariableValues(String variableName, Double... values) {
		//InitializedVariable variable = new InitializedVariable(variableName, values);
		//this.variables.put(variableName, variable);
	}
	
	public X getX() {
		return x;
	}
	
	public InitializedVariable findVariable(String variableName) {
		return this.variables.get(variableName);
	}
	
	public List<Combination> calculateParallel(String problemId, String mutantId, boolean testing, boolean keepBestSolution) {
		CHCalculator calculator = new CHCalculator(this);
		return calculator.calculate();
	}
	
	public List<Combination> calculateAll(String problemId, String mutantId, boolean testing, boolean keepBestSolution) {
		List<Combination> best50 = new ArrayList<>();
		List<Combination> results = new ArrayList<>();
		int n = this.getNumberOfCombinations();
		double lowestEnergy = Double.MAX_VALUE;
		Combination bestSolution = null;
		
		for (int i=0; i<n; i++) {
			Combination combination = this.calculate(i);
			if (!keepBestSolution)
				results.add(combination);
			else {
				if (combination.getValue()<lowestEnergy) {
					lowestEnergy = combination.getValue();
					bestSolution = combination;
				}
			}
		}
		if (!keepBestSolution) {
			results.sort(new Comparator<Combination>() {
				@Override
				public int compare(Combination o1, Combination o2) {
					return (int) (o1.getValue()-o2.getValue());
				}
			});
		
			n = results.size()<50 ? results.size() : 50;
			for (int i=0; i<n; i++)
				best50.add(results.get(i));
		} else {
			best50.add(bestSolution);
		}
		
		if (testing) {
			Combination best = results.get(0);
			double value = best.getValue();
			int i=0;
			while ((best=results.get(i++)).getValue()==value) {
				
			}
			for (int j=results.size()-1; j>=i; j--)
				results.remove(j);
		}
		
		if (problemId!=null) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					saveSolutions(problemId, mutantId, best50);
				}
			};
			new Thread(r).start();
		}
		return best50;
	}

	private void saveSolutions(String problemId, String mutantId, List<Combination> results) {
		Problem problem = Manager.get().getProblemRepo().findById(problemId).get();
		if (mutantId==null) {
			Manager.get().getProblemSolutionRepo().removeByProjectId(problemId);
			Combination best = results.get(0);
			double value = best.getValue();
			int i=0;
			while (((best=results.get(i++)).getValue()==value) && i<results.size()) {
				ProblemSolution solution = new ProblemSolution();
				solution.setProblem(problem);
				solution.setEnergy(value);
				solution.setCombination(best.toJSON().toString());
				Manager.get().getProblemSolutionRepo().save(solution);
			}
		} else {
			AnnMutant mutant = Manager.get().getAnnMutantRepo().findById(mutantId).get();
			Manager.get().getProblemSolutionRepo().setToNullMutantSolutionsByProjectId(mutantId);
			Combination best = results.get(0);
			double value = best.getValue();
			int i=0;
			while (((best=results.get(i++)).getValue()==value) && i<results.size()) {
				ProblemSolution solution = new ProblemSolution();
				solution.setProblem(problem);
				solution.setAnnMutant(mutant);
				solution.setEnergy(value);
				solution.setCombination(best.toJSON().toString());
				Manager.get().getProblemSolutionRepo().save(solution);
			}
		}
	}

	public Combination calculate(int index) {
		List<XValue> comb = this.getCombination(index);
		this.currenCombination = comb;
		ResultExpr expr;
		double r = 0;
		for (int i=0; i<this.expressions.size(); i++) {
			expr = this.expressions.get(i);
			r = r + expr.getValue();
		}
		Combination result = new Combination();
		result.setData(comb);
		result.setValue(r);
		return result;
	}
	
	public double calculate(Combination combination) {
		this.currenCombination = combination.getData();
		ResultExpr expr;
		double r = 0;
		for (int i=0; i<this.expressions.size(); i++) {
			expr = this.expressions.get(i);
			r = r + expr.getValue();
		}
		return r;
	}
	
	public List<XValue> getCurrenCombination() {
		return currenCombination;
	}
	
	public void setZero(int... indexes) {
		for (int i=0; i<indexes.length; i++)
			this.x.setValue(0, indexes[i]);
	}
	
	public void setOne(int... indexes) {
		for (int i=0; i<indexes.length; i++)
			this.x.setValue(1, indexes[i]);
	}
	
	public int getNumberOfCombinations() {
		return this.x.getNumberOfCombinations();
	}

	public List<XValue> getCombination(int index) {
		return this.x.getCombination(index);
	}
	
	public void addInitializedVariable(InitializedVariable initializedVariable) {
		if (this.variables.containsKey(initializedVariable.getName()))
			return;
		this.variables.put(initializedVariable.getName(), initializedVariable);
	}

	public InitializedVariable getInitializedVariable(String variableName) {
		return this.variables.get(variableName);
	}

	public List<ResultExprDetails> getDetails(JSONObject jso) {
		Combination combination = new Combination(jso.getString("html"));
		ResultExpr expr;
		ResultExprDetails red;
		List<ResultExprDetails> result = new ArrayList<>();
		this.currenCombination = combination.getData();
		for (int i=0; i<expressions.size(); i++) {
			expr = expressions.get(i);
			red = new ResultExprDetails();
			red.setResultExpr(expr);
			red.setValue(expr.getValue());
			result.add(red);
		}
		return result;
	}
	
	public ArrayList<ResultExpr> getExpressions() {
		return expressions;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Problem name\n\n" + this.expressions.get(0).toString() + "\n\n");
		for (int i=1; i<this.expressions.size(); i++)
			sb.append(this.expressions.get(i).toString() + "\n");
		return sb.toString();
	}

	public void instantiateVariables(HashMap<String, GVariable> variables) {
		Iterator<GVariable> iVariables = variables.values().iterator();
		while (iVariables.hasNext()) {
			GVariable variable = iVariables.next();
			InitializedVariable v = new InitializedVariable(variable.getName());
			v.setValues(variable.getValues());
			this.variables.put(v.getName(), v);
		}
	}

	public void setXValue(Integer value, int... coords) {
		this.x.setValue(value, coords);
	}

	public XValue getXValue(List<ResultExpr> indexes) {
		int[] indexValues = new int[indexes.size()];
		for (int i=0; i<indexes.size(); i++)
			indexValues[i] = (int) indexes.get(i).getValue();
		int pos = this.x.getPos(indexValues);
		return this.currenCombination.get(pos);
	}

	public String toML() {
		StringBuilder sb = new StringBuilder();
		ResultExpr expr;
		int n = this.expressions.size();
		for (int i=0; i<n; i++) {
			expr = this.expressions.get(i);
			sb.append("<math xmlns='http://www.w3.org/1998/Math/MathML' display='block'>");
			sb.append(expr.toML());
			if (i<n-1) {
				sb.append("<mo>+</mo>");
			}
			sb.append("</math>");
		}
		return sb.toString();
	}
}
