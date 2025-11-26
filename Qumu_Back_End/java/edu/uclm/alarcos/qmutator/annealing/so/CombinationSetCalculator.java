package edu.uclm.alarcos.qmutator.annealing.so;

import java.util.List;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;
import edu.uclm.alarcos.qmutator.annealing.XValue;

public class CombinationSetCalculator implements Runnable {

	private CH ch;
	private List<Combination> result;
	private int[] prods;
	private int start;
	private int end;

	public CombinationSetCalculator(List<Combination> result, CH ch, int start, int end) {
		this.ch = ch;
		this.result = result;
		this.start = start;
		this.end = end;
		this.prods = ch.getX().getProds();
	}

	@Override
	public void run() {
		ResultExpr expr;
		List<XValue> comb;
		for (int i=start; i<end; i++) {
			comb = this.ch.getCombination(i);
			double r = 0;
			for (int j=0; j<this.ch.getExpressions().size(); j++) {
				expr = this.ch.getExpressions().get(j);
				r = r + expr.getValue(comb, prods);
			}
			Combination result = new Combination();
			result.setData(comb);
			result.setValue(r);
			this.result.add(result);
		}
	}

	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
}
