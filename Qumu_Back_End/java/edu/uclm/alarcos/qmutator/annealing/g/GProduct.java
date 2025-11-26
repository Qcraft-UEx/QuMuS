package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Product;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public class GProduct extends GExpr {

	private ArrayList<GExpr> exprs;
	
	public GProduct() {
		this.exprs = new ArrayList<>();
	}

	public GProduct(String expr) throws Exception {
		this();
		int posPor = expr.indexOf('*');
		if (posPor==-1)
			posPor = expr.indexOf('·');
		if (posPor==-1)
			throw new Exception("Wrong expression: " + expr + " * or · expected");
		String a = expr.substring(0, posPor).trim();
		if (a.length()==0)
			throw new Exception("Wrong expression: " + expr + " Variable expected before sign");
		
		try {
			int value = Integer.parseInt(a); 
			this.exprs.add(new GIntValue(value));
		} catch (Exception e) {
			GIndexedValue giv = new GIndexedValue(a);
			this.exprs.add(giv);
		}
		
		if (expr.charAt(posPor+1)!='x' && expr.charAt(posPor+1)!='X')
			throw new Exception("Wrong expression: " + expr + " Expected x after sign");
		
		String b = expr.substring(posPor+3).trim();
		String[] tokens = b.split("_");
		GUseOfX guox = new GUseOfX(tokens);
		this.exprs.add(guox);
	}

	public GProduct(JSONObject jso, GH h) {
		this();
		JSONArray jsaExprs = jso.getJSONArray("exprs");
		for (int i=0; i<jsaExprs.length(); i++) {
			JSONObject jsoExpr = jsaExprs.getJSONObject(i);
			GExpr expr = GExpr.build(jsoExpr, h);
			this.exprs.add(expr);
		}
	}
	
	public GProduct(JSONArray jsaExprs, GH h) {
		this();
		for (int i=0; i<jsaExprs.length(); i++) {
			JSONObject jsoExpr = jsaExprs.getJSONObject(i);
			GExpr expr = GExpr.build(jsoExpr, h);
			this.exprs.add(expr);
		}
	}

	@Override
	@JsonIgnore
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		Product p = new Product(f);
		for (int i=0; i<this.exprs.size(); i++) {
			GExpr expr = this.exprs.get(i);
			p.add(expr.getResultExpr(f, indexName, index));
		}
		return p;
	}

	@Override
	@JsonIgnore
	protected Double getValue() {
		double r = 1;
		for (GExpr expr : this.exprs) {
			r = r * expr.getValue();
			if (r==0)
				return 0.0;
		}
		return r;
	}

	public GProduct copy() {
		GProduct result = new GProduct();
		for (GExpr expr : this.exprs)
			result.exprs.add(expr.copy());
		return result;
	}

	public GExpr replace(String variable, int value) {
		for (GExpr expr : this.exprs)
			expr.replace(variable, value);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<this.exprs.size()-1; i++)
			sb.append(exprs.get(i).toString() + " * ");
		sb.append(exprs.get(this.exprs.size()-1).toString());
		return sb.toString();
	}
	
	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		JSONArray jsa = new JSONArray();
		for (int i=0; i<this.exprs.size(); i++)
			jsa.put(this.exprs.get(i).toJSON());
		jso.put("exprs", jsa);
		return jso;
	}

	public void add(GExpr... exprs) {
		for (GExpr expr : exprs)
			this.exprs.add(expr);
	}
	
	public ArrayList<GExpr> getExprs() {
		return exprs;
	}

	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<mfenced separators='' open='(' close = ')'>");
		for (int i=0; i<exprs.size()-1; i++) { 
			sb.append(exprs.get(i).toML());
			sb.append("<mo>·</mo>");
		}
		sb.append(exprs.get(exprs.size()-1).toML());
		sb.append("</mfenced>");
		return sb.toString();
	}
}
