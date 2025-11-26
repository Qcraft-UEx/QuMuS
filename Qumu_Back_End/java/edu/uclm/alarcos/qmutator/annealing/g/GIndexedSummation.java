package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;

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

public class GIndexedSummation extends GSummation {

	private GFrom from;
	private GExpr to;
	
	public GIndexedSummation(GRule rule) {
		super(rule);
	}

	public GIndexedSummation(GRule rule, JSONObject jso, boolean withDetails, GH h) {
		this(rule);
		
		JSONObject jsoFrom = jso.getJSONObject("from");
		this.from = newFrom(jsoFrom, withDetails, h);
		
		JSONObject jsoTo = jso.getJSONObject("to");
		this.to = GExpr.build(jsoTo, rule.getH()); 
		
		JSONObject jsoBody = jso.optJSONObject("body");
		if (jsoBody!=null)
			this.body.add(new GProduct(jsoBody, rule.getH()));
		else {
			JSONArray jsaBody = jso.getJSONArray("body");
			for (int i=0; i<jsaBody.length(); i++)
				this.body.add(GExpr.build(jsaBody.getJSONObject(i), h));
		}
	}
	
	public GIndexedSummation(GH h, GFrom from, String to) {
		super(null);
		this.from = from;
		this.to = new GParameter(h, to, null);
	}

	private GFrom newFrom(JSONObject jso, boolean withDetails, GH h) {
		GFrom from = new GFrom(this);
		GExpr value;
		JSONObject jsoValue;
		String indexName;
		JSONObject jsoIndex = jso.optJSONObject("index");
		if (jsoIndex==null) {
			indexName = jso.getString("index");
			jsoValue = jso.getJSONObject("value");
			value = GExpr.build(jsoValue, rule.getH());
		} else {
			indexName = jsoIndex.getString("name");
			value = GExpr.build(jsoIndex.getJSONObject("start"), h);
		}
		from.newIndex(indexName, value);
		this.from = from;
		return this.from;
	}

	public GFrom newFrom(String indexName, int value) {
		GFrom from = new GFrom(this);
		from.newIndex(indexName, value);
		this.from = from;
		return this.from;
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
		ArrayList<GExpr> newBody = new ArrayList<>();
		if (!forAlls.isEmpty()) 
			newBody = this.applyForAlls(forAlls);
		else
			newBody = this.body;

		Integer start = this.from.getValue();
		Double end = this.to.getValue();
		String indexName = this.from.getIndex().getName();
		
		for (int i=0; i<newBody.size(); i++) {
			GExpr expr = newBody.get(i);
			Sum sum = new Sum(f);
			ResultExpr resultExpr = sum;
			for (int j=start; j<end; j++) {
				ResultExpr term = expr.getResultExpr(f, indexName, j);
				sum.add(term);
			}
			if (this.rule.getRight()!=null) {
				ResultExpr right = this.rule.getRight().getResultExpr(f, indexName, start);
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
			this.from.toString() + "\n\t";
		if (this.to!=null)
			r = r + this.to.toString() + "\n\t";
		return r + this.body.toString() + "\n}";
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("from", this.from.toJSON());
		jso.put("to", this.to.toJSON());
		
		JSONArray jsaExprs = new JSONArray();
		for (int i=0; i<this.body.size(); i++)
			jsaExprs.put(this.body.get(i).toJSON());
		jso.put("body", jsaExprs);
		
		return jso;
	}

	public GFrom getFrom() {
		return from;
	}

	public void setFrom(GFrom from) {
		this.from = from;
	}

	public GExpr getTo() {
		return to;
	}

	public void setTo(GExpr to) {
		this.to = to;
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

	public String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<munderover>");
		sb.append("<mo data-mjx-texclass=\"OP\">&#x2211;</mo>");
		sb.append(this.from.toML());
		sb.append(this.to.toML());
		sb.append("</munderover>");
		//for (int i=0; i<body.size()-1; i++) 
		//	sb.append(body.get(i).toML());
		//sb.append(body.get(body.size()-1).toML());
		return sb.toString();
	}
}
