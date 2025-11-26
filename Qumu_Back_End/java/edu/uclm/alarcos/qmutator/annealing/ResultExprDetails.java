package edu.uclm.alarcos.qmutator.annealing;

public class ResultExprDetails {

	private ResultExpr expr;
	private double value;

	public void setResultExpr(ResultExpr expr) {
		this.expr = expr;
	}

	public void setValue(double value) {
		this.value = value;
	}

	
	
	public double getValue() {
		return value;
	}
}
