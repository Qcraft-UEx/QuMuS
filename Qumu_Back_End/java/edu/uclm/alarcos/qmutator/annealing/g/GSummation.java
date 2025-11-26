package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;

public abstract class GSummation {

	protected GRule rule;
	protected ArrayList<GExpr> body;
	
	public GSummation(GRule rule) {
		this.rule = rule;
		this.body = new ArrayList<>();
	}

	public GIndexedValue newIndexedValue(String variableName, String... indexNames) {
		GVariable variable = this.rule.findVariable(variableName);
		if (variable==null) {
			variable = new GVariable(variableName);
			variable.setDimensions(indexNames.length);
			this.rule.addVariable(variable);
		}
		GIndexedValue iv = new GIndexedValue(variable, indexNames);
		return iv;
	}

	@JsonIgnore
	public abstract void getResultExpr(CH f, ArrayList<GForAll> forAlls);

	final protected ArrayList<GExpr> applyForAlls(ArrayList<GForAll> forAlls) {
		ArrayList<GExpr> forAllExprs = new ArrayList<>();
		for (int i=0; i<body.size(); i++)
			forAllExprs.add(body.get(i).copy());
		
		GForAll forAll = forAlls.get(0);
		forAllExprs = this.applyForAll0(forAll, forAllExprs);
		
		for (int i=1; i<forAlls.size(); i++) {
			forAll = forAlls.get(i);
			forAllExprs = this.applyForAllN(forAll, forAllExprs);
		}
		
		return forAllExprs;
	}
	
	private ArrayList<GExpr> applyForAllN(GForAll forAll, ArrayList<GExpr> forAllExprs) {
		ArrayList<GExpr> result = new ArrayList<>();
		String variableName = forAll.getVariable();
		for (int i=0; i<forAllExprs.size(); i++) {
			GExpr expr = forAllExprs.get(i);
			List<Integer> values = forAll.getValues(); 
			for (int j=0; j<values.size(); j++) {
				GExpr newExpr = expr.copy().replace(variableName, values.get(j));
				result.add(newExpr);
			}
		}
		return result;
	}
	
	private ArrayList<GExpr> applyForAll0(GForAll forAll, ArrayList<GExpr> forAllExprs) {
		ArrayList<GExpr> result = new ArrayList<>();
		String variableName = forAll.getVariable();
		
		List<Integer> values = forAll.getValues(); 
		for (int i=0; i<values.size(); i++) {
			for (int j=0; j<forAllExprs.size(); j++) {
				GExpr expr = forAllExprs.get(j);
				GExpr newExpr = expr.copy().replace(variableName, values.get(i));
				result.add(newExpr);
			}
		}
		
		return result;
	}

	@Override
	public abstract String toString();

	public abstract JSONObject toJSON();

	public ArrayList<GExpr> getBody() {
		return body;
	}

	public void setBody(GExpr body) {
		this.body.add(body);
	}

	public void add(GIndex index) {
		this.rule.add(index);
	}

	public GUseOfX newUseOfX(String... indexNames) {
		GUseOfX uox = new GUseOfX(indexNames);
		return uox;
	}

	public GIndex findIndex(String indexName) {
		return this.rule.findIndex(indexName);
	}

	public GVariable findVariable(String variableName) {
		return this.rule.findVariable(variableName);
	}
	
	@JsonIgnore
	public GRule getRule() {
		return rule;
	}

	public abstract String toML();
}
