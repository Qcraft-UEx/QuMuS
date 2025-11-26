package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Lambda;
import edu.uclm.alarcos.qmutator.annealing.Product;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;
import edu.uclm.alarcos.qmutator.annealing.Square;
import edu.uclm.alarcos.qmutator.annealing.Substraction;
import edu.uclm.alarcos.qmutator.annealing.Sum;

public class GSetSummation extends GSummation {

	private String indexName;
	private GSet set;
	
	public GSetSummation(GRule rule) {
		super(rule);
	}

	public GSetSummation(GRule rule, JSONObject jso, boolean withDetails) {
		this(rule);		
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
	public void getResultExpr(CH f, ArrayList<GForAll> forAlls) {
		if (!forAlls.isEmpty()) {
			ArrayList<GExpr> newBody = this.applyForAlls(forAlls);
			this.body = newBody;
		}

		List<Integer> values = this.set.getValues();
		
		for (int i=0; i<this.body.size(); i++) {
			GExpr expr = this.body.get(i);
			Sum sum = new Sum(f);
			ResultExpr resultExpr = sum;
			for (int j=0; j<values.size(); j++) {
				ResultExpr term = expr.getResultExpr(f, this.indexName, values.get(j));
				sum.add(term);
			}
			if (this.rule.getRight()!=null) {
				ResultExpr right = this.rule.getRight().getResultExpr(f, indexName, null);
				Substraction substraction = new Substraction(f);
				substraction.setLeft(sum);
				substraction.setRight(right);
				resultExpr = new Square(f, substraction);
			}
			if (this.rule.getLambda()!=null)
				resultExpr = new Product(f, new Lambda(f, this.rule.getLambda()), resultExpr);
			f.add(resultExpr);
		}
	}

	@Override
	public String toString() {
		String r = "sum(\n\t" + 
			this.indexName + " in " + this.set.toString() + "\n\t";
		return r + this.body.toString() + "\n}";
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("indexName", this.indexName);
		jso.put("set", this.set.toJSON());
		
		JSONArray jsaExprs = new JSONArray();
		for (int i=0; i<this.body.size(); i++)
			jsaExprs.put(this.body.get(i).toJSON());
		jso.put("body", jsaExprs);
		
		return jso;
	}


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
	
	public void setSet(String indexName, GSet set) {
		this.indexName = indexName;
		this.set = set;
	}
	
	public GSet getSet() {
		return set;
	}

	public String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<munderover>");
		sb.append("<mo data-mjx-texclass=\"OP\">&#x2211;</mo>");
		sb.append(this.set.toML());
		sb.append("</munderover>");
		for (int i=0; i<body.size()-1; i++) 
			sb.append(body.get(i).toML());
		sb.append(body.get(body.size()-1).toML());
		return sb.toString();
	}
}
